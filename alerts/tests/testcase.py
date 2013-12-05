import sys,os
import logging
from rhq.server import RHQServer
import unittest

class RHQAlertTest(unittest.TestCase):
    '''This is a base class for all allert tests'''

    @classmethod
    def setUpClass(self):
        self.log = logging.getLogger(self.__class__.__name__)
        self.hosts = os.getenv('RHQ_HOSTS','localhost').split(',')
        self.user = os.getenv('RHQ_USER','rhqadmin')
        self.passwd = os.getenv('RHQ_PASSWORD','rhqadmin')
        # setup logger
        self.log = logging.getLogger(self.__class__.__name__)
        self.log.setLevel('DEBUG')
        # detect RHQ server version
        r = RHQServer(host=self.hosts[0],user=self.user,password=self.passwd).get('status')
        data = r.json()['values']
        os.environ['RHQ_BUILD_VERSION'] = '%s (%s)' % (data['SERVER_VERSION'],data['BuildNumber'])

    def rhqServer(self,index=0):
        '''gets RHQ/JON server object configured via `RHQ_HOSTS` environ variable
        
        `RHQ_HOSTS` can be comma-separated set of hostnames/IPs in case we run HA

        :param index: index of server
        '''
        return RHQServer(host=self.hosts[index],user=self.user,password=self.passwd)
