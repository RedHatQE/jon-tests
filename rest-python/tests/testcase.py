import sys,os
import requests,json
import logging
import proboscis.asserts
from proboscis import before_class
from proboscis import SkipTest

class RHQRestTest(object):
    """
    This is a base class for all tests, provides methods for sending requests
    to RHQ server
    """

    def __init__(self):
        self.log = logging.getLogger(self.__class__.__name__)
        if os.getenv('PS1'):
            # enable console output only if we run in interactive shell
            formatter = logging.Formatter('%(asctime)s - %(levelname)s: %(message)s (%(name)s)')
            log_handler = logging.StreamHandler()
            log_handler.setFormatter(formatter)
            log_handler.setLevel('INFO')
            self.log.addHandler(log_handler)        
        # set parameters to our test class instance
        self.endpoint = 'http://%s:7080/rest/' % os.getenv('RHQ_TARGET','localhost')
        self.auth = (os.getenv('RHQ_USER','rhqadmin'),os.getenv('RHQ_PASSWORD','rhqadmin'))
        self.headers = {'accept':'application/json','content-type': 'application/json'} 

    def url(self,resource):
        if resource.find('http') >= 0:
            return resource
        return self.endpoint+resource.lstrip('./')

    def get(self,resource,accepts='application/json'):
        url = self.url(resource)
        self.log.debug('GET %s' % url)
        resp = requests.get(url, auth=self.auth, headers = {'accept':accepts,'content-type': accepts})
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def post(self,resource,data):
        json_data = json.dumps(data)
        url = self.url(resource)
        self.log.debug('POST %s' % url)
        self.log.debug('DATA %s' % json_data)
        resp = requests.post(url, json_data, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp
    
    def put(self,resource,data):
        json_data = json.dumps(data)
        url = self.url(resource)
        self.log.debug('PUT %s' % url)
        self.log.debug('DATA %s' % json_data)
        resp = requests.put(url, json_data, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def delete(self,resource):
        url = self.url(resource)
        self.log.debug('DELETE %s' % url)
        resp = requests.delete(url, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp 

    def find_resource_agent(self):
        return self.__find_resource({'q':'RHQ Agent','category':'SERVER'})

    def find_resource_eap6standalone(self):
        return self.__find_resource({'q':'EAP (0.0.0.0:9990)','category':'SERVER'})

    def find_resource_platform(self):
        return self.__find_resource({'category':'PLATFORM'})

    def __find_resource(self,query):
        self.log.debug('GET %s' %(self.endpoint+'resource'))
        resp = requests.get(self.endpoint+'resource', params=query, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        data = resp.json()
        if len(data) == 0:
            raise Exception("No resource found by query %s" % str(query))
        if len(data) == 1:
            return data[0]
        else:
            self.log.info('Retrieved %d resources by query %s, returning first' % (len(data),str(query)))
            return data[0]
        return resp.json()
    
    def check_fields(self,obj,keys,value_cb=value_cb):
        """
        Checks whether given obj contains all given keys
        obj (Object) - object to be checked
        keys (Array) - array of string keys 
        value_cb (function(key,value)) - callback function that must return nothing if validation is successfull,
        otherwise a string message
            can be used for additional asserts (when we expect not just key to be present, but some value)
        """
        def _default_value_cb(key,value):
            if key.find('Id') > 0:
                if not type(value) == type(0):
                    return '%s field must be number type' % key
            if key.find('Name') > 0:
                if not value:
                    return '%s field must NOT be null' % key
        with proboscis.asserts.Check() as check:
            for key in keys:
                check.true(key in obj,'Key %s was not found in %s' %(key,str(obj)))
                val = _default_value_cb(key,obj[key])
                check.true(val == None,str(val))
                if value_cb:
                    val = value_cb(key,obj[key])
                    check.true(val == None,str(val))

