import sys,os
import requests,json
import unittest
import logging
import optparse

class RHQRestTest(unittest.TestCase):
    """
    This is a base class for all tests
    """

    @classmethod
    def setUpClass(self):
        """
        this is equivalent to @BeforeClass, here
        we setup logging and read inputs environment variables
        """
        self.log = logging.getLogger(self.__name__)
        formatter = logging.Formatter('%(asctime)s - %(levelname)s: %(message)s (%(name)s)')
        log_handler = logging.StreamHandler()
        log_handler.setFormatter(formatter)
        self.log.addHandler(log_handler)        
        # set parameters to our test class instance
        self.endpoint = 'http://%s:7080/rest/' % os.getenv('RHQ_TARGET','localhost')
        self.auth = (os.getenv('RHQ_USER','rhqadmin'),os.getenv('RHQ_PASSWORD','rhqadmin'))

    def get(self,resource):
        return requests.get(self.endpoint+resource, auth=self.auth)

    def post(self,resource,data):
        json_data = json.dumps(data)
        return requests.post(self.endpoint+resource, json_data, self.auth)
