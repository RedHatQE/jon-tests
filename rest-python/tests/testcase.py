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

    def get(self,resource):
        self.log.debug('GET %s' %(self.endpoint+resource))
        resp = requests.get(self.endpoint+resource, auth=self.auth, headers = {'accept':'application/json','content-type': 'application/json'})
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def post(self,resource,data):
        json_data = json.dumps(data)
        self.log.debug('POST %s' %(self.endpoint+resource))
        self.log.debug('DATA %s' % json_data)
        resp = requests.post(self.endpoint+resource, json_data, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp

    def delete(self,resource):
        self.log.debug('DELETE %s' %(self.endpoint+resource))
        resp = requests.delete(self.endpoint+resource, auth=self.auth, headers = self.headers)
        self.log.debug('Response HEADERS:%s' % str(resp.headers))
        self.log.debug('Response BODY: %s' %(resp.text))
        return resp 

    def find_resource_agent(self):
        return self.__find_resource({'q':'RHQ Agent','category':'SERVER'})

    def find_resource_eap6standalone(self):
        return self.__find_resource({'q':'EAP (0.0.0.0:9990)','category':'SERVER'})

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
    
    def check_fields(self,obj,keys):
        """
        Checks whether given obj contains all given keys
        """
        with proboscis.asserts.Check() as check:
            for key in keys:
                check.true(key in obj,'Key %s was not found in %s' %(key,str(obj)))
