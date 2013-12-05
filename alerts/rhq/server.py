import sys,os
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

    def newPlatform(self,name=str(_now()),avail='UNKNOWN'):
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
        '''Sends metric data for given `resource` and `schedule`. 

        :param resource: resource body
        :param schedule: schedule body
        :param value: value (either string or number type), depends on type of `schedule` (TRAIT,MEASUREMENT)
        '''
        self.log.info('Sending data for schedule name=%s value=%s' % (schedule['displayName'],str(value)))
        if schedule['type'] == 'TRAIT':
            r = self.put('/metric/data/%d/trait/%d' % (schedule['scheduleId'],_now()),{'value':value})
        elif schedule['type'] == 'MEASUREMENT':
            r = self.put('/metric/data/%d/raw/%d' % (schedule['scheduleId'],_now()),{'value':value})
        if r.status_code != 200:
            raise Exception(r.text)

    @_validRes
    @_validConditions
    def defineAlert(self,resource,conditions=[],name=str(_now()),recovers={'id':0},dampening=damps.none(),enabled=True):
        '''defineAlert(self,resource,conditions=[],name=time.now(),recovers={},dampening=dampenings.none(),enabled=True)
        Creates new alert definition

        :param resource: resource body 
        :param conditions: one condition or array of conditions (see `conditions` module)
        :param name: name of alert definition
        :param recovers: alert definition body in case this alert definition recovers other alert def
        :param dampening: dampening (see `dampenings` module)
        :param enabled: enable/disable new alert definition

        :returns: new alert definition body
        '''
        if type(conditions) is DictType:
            conditions = [conditions]
        self.log.info('Creating alert difinition name=%s' % name)
        body =  {'name':name,
                'conditions':conditions,
                'enabled':enabled,
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
        to check if a key `enabled` within alert body has `True` value

        Note that this does work only for simple values (not arrays or hashes)

        >>> checkAlertDef(alert,enabled=True,name='abc')

        :param definition: alert definition body
        '''
        r = self.get('/alert/definition/%d' % definition['id'])
        if r.status_code != 200:
            raise Exception(r.text)
        alert = r.json()
        return assertDict(alert,*args,**kwargs)

    def undefineAlert(self,alert):
        '''Undefines (deletes) alert definition

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

    def findRHQAgent(self):
        '''Finds RHQ Agent resource within inventory, prefers agent on current server, returns first found instance'''
        return self.__find_resource({'q':'RHQ Agent','category':'SERVER'})
    def findRHQServer(self):
        '''Finds RHQ Server resource'''
        return self.__find_resource({'q':'RHQ Server','category':'SERVER'})

    def findEAP6Server(self):
        '''Finds EAP6 Standalone Server in inventory, returns first found instance'''
        return self.__find_resource({'q':'EAP (0.0.0.0:9990)','category':'SERVER'})

    def findPlatform(self,name=None):
        '''Finds a Platform in inventory, returns first found instance       
        
        :param name: name of platform to search for, if None platform name of this server is used
        :returns: platform resource
        '''
        if name is None:
            name = self.platform['resourceName']
        return self.__find_resource({'category':'PLATFORM','q':name})

    def __find_resource(self,query):
        '''Finds a resource by given query in inventory'''
        self.log.debug('GET %s' %(self.endpoint+'resource'))
        resp = requests.get(self.endpoint+'resource', params=query, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        data = resp.json()
        if len(data) == 0:
            return
        if len(data) == 1:
            return data[0]
        else:
            self.log.info('Retrieved %d resources by query %s, returning first' % (len(data),str(query)))
            return data[0]
    
