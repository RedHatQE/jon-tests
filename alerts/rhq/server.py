import sys,os,re
import requests,json
import logging
import time
from types import *
from testutil import *
import dampenings as damps
from functools import wraps
def _now():
    return int(time.time() * 1000)

def _validRes(func):
    """decorator to check whether valid resource definition was passed as first argument"""
    @wraps(func)
    def wrapperR(*args,**kwargs):
        if kwargs.has_key('resource'):
            res = kwargs['resource']
        elif len(args) > 1:
            res = args[1]
        assert res is not None, 'resource parameter was not passed'
        assert type(res) is DictType, 'resource parameter must be dict'
        assert res.has_key('resourceId'), 'resource parameter must have "resourceId" key'
        return func(*args,**kwargs)
    return wrapperR

def _validConditions(func):
    """decorator to check whether valid alert condition was passed as 2nd argument"""
    @wraps(func)
    def wrapperC(*args,**kwargs):
        # we accept either array of conditions or 1 condition dict
        if kwargs.has_key('conditions'):
            conditions = kwargs['conditions']
        else:
            conditions = args[1]
        if type(conditions) is DictType:
                conditions = [conditions]
        # TODO validate bodies

        return func(*args,**kwargs)
    return wrapperC


class RHQServer(object):
    '''RQHServer provides methods for sending requests to RHQ server'''

    def __init__(self,host='localhost',port=7080,user='rhqadmin',password='rhqadmin'):
        self.log = logging.getLogger(self.__class__.__name__)
        self.log.setLevel('DEBUG')
        self.server_url = 'http://%s:%d/' % (host,port)
        self.endpoint = '%srest/' % self.server_url
        self.auth = (user,password)
        self.headers = {'accept':'application/json','content-type': 'application/json'}
        self.platform = self.findPlatform(name=host)
        if self.platform is None:
            self.log.warning('Unable to lookup platform resource by name %s, fix your setup and expect some tests to fail!' % host)

    def __enter__(self):
        return self

    def __exit__(self,type,value,traceback):
        pass

    def sleep(self,seconds=5):
        """Sleeps for some time
        
            :param seconds: seconds to sleep
        """
        self.log.info('Sleeping %ds' % seconds)
        time.sleep(seconds)

    def waitForAlertDef(self,seconds=30):
        """Sleeps 30s to wait for alert definition(s) to became 'active'"""
        self.sleep(seconds)
    
    def waitForAlert(self,seconds=10):
        """Sleeps 10s to wait for alert to get fired"""
        self.sleep(seconds)

    def newPlatform(self,name=str(_now()),avail=None):
        """Creates new syntetic platform resource
            
            :param name: name of platform (default is current timestamp)
            :param avail: availability (UP,DOWN,UNKNOWN) default is UNKNOWN
            :returns: new resource body
        """
        self.log.info('Creating systetic platform with name=%s' % name)
        r = self.post('/resource/platforms',{'resourceName':name, 'typeName':'Linux'})
        p = r.json()        
        if r.status_code > 201:
            print r.status_code
            raise Exception(r.text)
        if avail:
            self.sendAvail(p,avail)
        return p

    @_validRes
    def deleteResource(self,resource,physical=False):
        '''deleteResource(self,resource,physical=False)
        Deletes/uninventories given `resource` from inventory

        All resources can be uninventoried, but only some can be deleted

            :param resource: resource body to get deleted
            :param physical: True if you want delete, False (default) when you just uninventory
        '''
        self.log.info('Deleting resource id=%d' % resource['resourceId'])
        r = self.delete('resource/%s?physical=%s' % (resource['resourceId'],physical))
        if r.status_code != 204:
            raise Exception(r.text)

    @_validRes
    def sendAvail(self,resource,avail='UP',keep=3600):
        '''sendAvail(self,resource,avail='UP',keep=3600)
        Sends availability or availabilities for given resource
        
        :param resource: resource body
        :param avail: String or list of strigs (UP,DOWN,UNKNOWN) representing availability states
        :param keep: time in seconds for how long to send status (when sending multiple availabilities using array, this is effective only for last value)
        '''
        self.log.info('Sending availability=%s for resource %d' % (str(avail),resource['resourceId']))
        if type(avail) is StringType:
            avail = [avail]
        for av in avail:
            r = self.put('/resource/%d/availability' % resource['resourceId'],
                {'since':_now(),'type':av,'resourceId':resource['resourceId'],'until':_now()+(keep * 1000)})
            if r.status_code != 204:
                raise Exception(r.text)

    def sendAvailUp(self,resource):
        '''Sends availability=UP to given resource

        :param resource: resource body
        '''
        return self.sendAvail(resource,avail='UP')
    
    def sendAvailDown(self,resource):
        '''Sends availability=UP to given resource

        :param resource: resource body
        '''
        return self.sendAvail(resource,avail='DOWN')

    @_validRes
    def sendMetricData(self,resource,schedule,value):
        '''sendMetricData(self,resouce,schedule,value)
        Sends metric data for given `resource` and `schedule`. 

        :param resource: resource body
        :param schedule: schedule body
        :param value: value (either string or number type), depends on type of `schedule` (TRAIT,MEASUREMENT)
        '''
        self.log.info('Sending data for schedule name=%s value=%s' % (schedule['displayName'],str(value)))
        if schedule['type'] == 'TRAIT':
            r = self.put('/metric/data/%d/trait/%d' % (schedule['scheduleId'],_now()),{'value':value})
        elif schedule['type'] == 'MEASUREMENT':
            r = self.put('/metric/data/%d/raw/%d' % (schedule['scheduleId'],_now()),{'value':value})
        if r.status_code > 201:
            raise Exception('Status: %d error : %s' % (r.status_code, r.text))

    @_validRes
    @_validConditions
    def defineAlert(self,resource,conditions=[],notifications=[],name=str(_now()),recovers={'id':0},dampening=damps.none(),enabled=True,mode='ANY',prio='MEDIUM'):
        '''defineAlert(self,resource,conditions=[],notifications=[],name=time.now(),recovers={},dampening=dampenings.none(),enabled=True,mode='ANY,prio='MEDIUM')
        Creates new alert definition

        :param resource: resource body 
        :param conditions: one condition or array of conditions (see :mod:`rhq.conditions` module)
        :param notifications: one notification or array of notifications (see :mod:`rhq.notifications` module)
        :param name: name of alert definition
        :param recovers: alert definition body in case this alert definition recovers other alert def
        :param dampening: dampening (see :mod:`rhq.dampenings` module)
        :param enabled: (True|False) enable/disable new alert definition
        :param mode: conditon mode (ANY|ALL)
        :param prio: alert priority (LOW|MEDIUM|HIGH)

        :returns: new alert definition body
        '''
        if type(conditions) is DictType:
            conditions = [conditions]
        if type(notifications) is DictType:
            notifications = [notifications]
        for n in notifications:
            if n['senderName'] == 'System Users':
                ids = []
                for u in n['config']['subjectId']:
                    self.log.info('Lookup system user ID for %s' % u)
                    r = self.get('/user/%s' % u)
                    if r.status_code != 200:
                        raise Exception('Unable to lookup details for user %s, server responded %d' % (u,r.status_code))
                    ids.append(str(r.json()['id']))
                n['config']['subjectId'] = '|%s|' % '|'.join(ids)
        self.log.info('Creating alert definition name=%s' % name)
        body =  {'name':name,
                'conditions':conditions,
                'notifications':notifications,
                'enabled':enabled,
                'conditionMode':mode,
                'priority':prio,
                'recoveryId':recovers['id']
        }
        body = dict(body.items() + dampening.items())
        r = self.post('/alert/definitions?resourceId=%d' % resource['resourceId'],body)
        if r.status_code != 201:
            raise Exception(r.text)
        return r.json()

    def checkAlertDef(self,definition,*args,**kwargs):
        '''Checks alert definition body
        You can pass named parameter (for example enabled=True) which will result 
        to check if a key `enabled` within alert body has ``True`` value
        

        :param definition: alert definition body
        :param \*\* kwargs: keyword args for alert definition body assertions

        Note that this does work only for simple values (not arrays or hashes)
        
        >>> s = RHQServer()
        >>> p = s.newPlatform()
        >>> alert = s.defineAlert(p,name='abc')
        >>> s.checkAlertDef(alert,enabled=True,name=u'abc')
        True
        >>> s.checkAlertDef(alert,enabled=True,name='abcd')
        AssertionError: Field "name" does not equal abcd

        '''
        r = self.get('/alert/definition/%d' % definition['id'])
        if r.status_code != 200:
            raise Exception(r.text)
        alert = r.json()
        return assertDict(alert,*args,**kwargs)
    
    def undefineAlert(self,alert):
        '''Undefines (deletes) alert definition(s)

        :param alert: alert definition body or array of alert definitions
        '''
        if type(alert) is DictType:
            alert = [alert]
        for a in alert:
            self.log.info('Deleting alert definition id=%d' % a['id'])
            r = self.delete('/alert/definition/%d' % a['id'])
            if r.status_code != 204:
                raise Exception('Failed to undefine alert %s \nreason: %s' % (str(a),r.text))

    @_validRes
    def alertCount(self,resource):        
        '''alertCount(self,resource)
        Gets alert count for given `resource`

        :param resource: resource body
        :returns: number of alerts fired on given `resource`
        '''
        r = self.get('/resource/%d/alerts' % resource['resourceId'])
        if r.status_code != 200:
            raise Exception(r.text)
        self.log.info('Got %d alerts for resource id=%d' % (len(r.json()),resource['resourceId']))
        return len(r.json())
    
    @_validRes
    def createEventSource(self,resource,name,location):
        '''createEventSource(self,resource)
        Creates new event source for given `resource`

        :param resource: resource body
        :param name: event source definition name
        :param location: location of event source (path to log file)
        :returns: event source body
        '''
        defNames = map(lambda d: d['name'], self.get('/event/%d/definitions' % resource['resourceId']).json())
        if not name in defNames:
            raise Exception('Event source\'s name does not match event source definition, allwed values are %s' %
                    str(defNames))
        body = {'name':name,'location':location,'resourceId':resource['resourceId']}
        r = self.post('/event/%d/sources' % resource['resourceId'],body)
        if r.status_code != 200:
            raise Exception(str(r.status_code)+' : ' + r.text)
        return r.json()
    
    def pushEvent(self,eventSource,time=_now(),severity='ERROR',detail=''):
        '''Pushes new event to given `eventSource`

        :param eventSource: eventSource body
        :param time: timestamp of event
        :param severity: severity of event (allowed is ERROR|INFO|WARN|DEBUG|FATAL)
        :param detail: message string
        '''
        body = [{'detail':detail,'severity':severity,'timestamp':time}]
        r = self.post('/event/source/%d/events' % eventSource['id'],body)
        if r.status_code != 204:
            raise Exception(str(r.status_code)+' : ' + r.text)

    @_validRes
    def getSchedule(self,resource,name=''):
        '''getSchedule(self,resource,name='')
        Gets metric schedule for given `resource`

        :param resource: resource body
        :param name: name of schedule you look for (either name or displayName can be used)
        :returns: metric schedule body
        '''
        
        self.log.info('Looking up schedule name=%s for resource id=%d' % (name,resource['resourceId']))
        r = self.get('resource/%d/schedules' % resource['resourceId'])
        if r.status_code != 200:
            raise Exception(r.text)
        for sch in r.json():
            if sch['scheduleName'] == name or sch['displayName'] == name:
                return sch
    @_validRes
    def runOperation(self,resource,name,params={}):
        '''runOperation(self,resource,name,params={})
        Runs operation on given `resource` and returns it's result
        
        :param resource: resource body
        :param name: name of operation to run
        :param params: dict - input configuration for operation
        '''
        self.log.info('Running Operation name=%s for resource id=%d' %(name,resource['resourceId']))
        r = self.get('operation/definitions?resourceId=%d' % resource['resourceId'])
        if r.status_code != 200:
            raise Exception(r.text)
        for opDef in r.json():
            if opDef['name'] == name:
                r = self.post('operation/definition/%d?resourceId=%d' % (opDef['id'],resource['resourceId']),{})
                op = r.json()
                op['readyToSubmit'] = True
                op['params'] = params
                r = self.put('operation/%d' % op['id'],op)
                if r.status_code != 200:
                    raise Exception(r.text)
                history_url = [x for x in r.json()['links'] if x.has_key('history')][0]['history']['href']
                r = self.get(history_url)
                data = r.json()
                while data['status'] == 'In Progress':
                    time.sleep(0.3)
                    data = self.get(history_url).json()
                self.log.info('Operation finished with status=%s' % data['status'])
                return data
        raise Exception('Operation with name [%s] not found for resource id=%d valid names are %s' %
            (name,resource['resourceId'],map(lambda x: x['name'],r.json())))

    def url(self,resource):
        '''Returns an absolute URL to server for given resource (URI)
        
        :param resource: absolute or relative REST resource URI
        '''
        if resource.find('http') == 0:
            return resource
        return self.endpoint+resource.lstrip('./')

    def get(self,resource,accepts='application/json'):
        '''send GET request to RHQ Server
        
        :param resource: absolute or relative REST resource URI
        :param accepts: accepts+content-type header value
        :returns: response
        '''
        url = self.url(resource)
        self.log.debug('GET %s' % url)
        headers = {'accept':accepts,'content-type': accepts}
        self.log.debug('Request HEADERS:%s' % str(headers))
        resp = requests.get(url, auth=self.auth, headers = headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def post(self,resource,data):
        '''send POST request to RHQ Server
        
        :param resource: absolute or relative REST resource URI
        :param data: dict (will be converted to JSON)
        :returns: response
        '''
        json_data = json.dumps(data)
        url = self.url(resource)
        self.log.debug('POST %s' % url)
        self.log.debug('DATA %s' % json_data)
        resp = requests.post(url, json_data, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp
    
    def put(self,resource,data):
        '''send PUT request to RHQ Server
        
        :param resource: absolute or relative REST resource URI
        :param data: dict (will be converted to JSON)
        :returns: response
        '''
        json_data = json.dumps(data)
        url = self.url(resource)
        self.log.debug('PUT %s' % url)
        self.log.debug('DATA %s' % json_data)
        resp = requests.put(url, json_data, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def delete(self,resource):
        '''send DELETE request to RHQ Server
        
        :param resource: absolute or relative REST resource URI
        :returns: response
        '''
        url = self.url(resource)
        self.log.debug('DELETE %s' % url)
        resp = requests.delete(url, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp 

    def importResources(self):
        '''Imports all resources from discovery queue
 
        :returns: number of resources that were imported
        '''
        self.log.info('Importing discovery queue')
        imported = self._importResources({'category':'platform'})
        imported += self._importResources({'category':'server'})
        self.log.info('Import done')
        return imported

    def _importResources(self,query):
        '''Imports resources from discovery queue'''
        query['status'] = 'NEW'
        r = requests.get(self.endpoint+'resource', params=query, auth=self.auth, headers = self.headers)
        data = r.json()
        if len(data) == 0:
            return 0
        self.log.info('Found %d new resources, importing...' % len(data))
        for res in sorted(data,key=lambda r: r['resourceId']):
            res['status'] = 'committed'
            r = self.put('/resource/%d' % res['resourceId'],res)
            if r.status_code != 200:
                raise Exception(r.text)
        return len(data)

    def findRHQAgent(self):
        '''Finds RHQ Agent resource within inventory, prefers agent on current server, returns first found instance'''
        return self._find_resource({'q':'RHQ Agent','category':'SERVER'})
    def findRHQServer(self):
        '''Finds RHQ Server resource'''
        return self._find_resource({'q':'RHQ Server','category':'SERVER'})

    def findEAP6Server(self):
        '''Finds EAP6 Standalone Server in inventory, returns first found instance'''
        return self._find_resource({'q':'EAP (0.0.0.0:9990)','category':'SERVER'})

    def findPlatform(self,name=None):
        '''Finds a Platform in inventory, returns first found instance       
        
        :param name: name of platform to search for, if None platform name of this server is used
        :returns: platform resource
        '''
        if name is None:
            name = self.platform['resourceName']
        return self._find_resource({'category':'PLATFORM','q':name})

    def findPlatforms(self):
        '''Returns all platforms'''
        return self._find_resources({'category':'PLATFORM'})

    def findResourcesByPath(self,*args):
        '''Finds resources on given path (starting within root of inventory)

        >>> s = RHQServer()
        >>> s.findResourcesByPath('platformName','RHQ Agent') # returns agent on `platformName` platform
        >>> s.findResourcesByPath('^foo|bar$','RHQ Agent') # returns agents on platforms starting with `foo` or ending with `bar`
        >>> s.findResourcesByPath('.*','RHQ Agent') # returns all agents on all platforms
        >>> s.findResourcesByPath('.*','RHQ Server','platform-mbean') # returns platform-mbean resrouces on all RHQ Servers
 
        :param \*args: arguments to define a path in inventory, you can use '.*' to match all resources on given path level or regular expression to match resource name
        :return: array of resource bodies
        '''
        def get_children(parents,query):
            children = []
            regex = re.compile('.*')
            try:
                regex = re.compile(query)
            except:
                self.log.warn('\"%s\" is not valid regex, using .* to match all' % query)
            for p in parents:
                r = self.get('/resource/%d/children' % p['resourceId'])
                children += filter(lambda r: regex.search(r['resourceName']), r.json())
            return children
        if len(args) == 0:
            raise Exception('At least 1 arg is expected')
        args = list(args)
        try:
            regex = re.compile(args[0])
        except:
            self.log.warn('\"%s\" is not valid regex, using .* to match all' % query)
            regex = re.compile('.*')
        children = filter(lambda r: regex.search(r['resourceName']), self.findPlatforms())
        del args[0]
        for arg in args:
            children = get_children(children,arg)
        return children


    def _find_resources(self,query):
        '''Finds a resources by given query in inventory'''
        self.log.debug('GET %s' %(self.endpoint+'resource'))
        resp = requests.get(self.endpoint+'resource', params=query, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp.json()

    def _find_resource(self,query):
        '''Finds a resource by given query in inventory'''
        data = self._find_resources(query)
        if len(data) == 0:
            return
        if len(data) == 1:
            return data[0]
        else:
            self.log.info('Retrieved %d resources by query %s, returning first' % (len(data),str(query)))
            return data[0]
    
