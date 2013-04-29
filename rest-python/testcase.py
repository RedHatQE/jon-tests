import sys,os
import requests
import unittest
import logging

class RHQRestTestCase(unittest.TestCase):

    @classmethod
    def setUpClass(self):
        self.log = logging.getLogger(self.__name__)
        formatter = logging.Formatter('%(asctime)s - %(levelname)s: %(message)s (%(name)s)')
        log_handler = logging.StreamHandler()
        log_handler.setFormatter(formatter)
        self.log.addHandler(log_handler)
        self.log.info('setUpClass')

    def setUp(self):
        self.log.info('setup')

    def test_list2(self):
        self.log.info('wtf')
        self.log.error('error')

    def test_list(self):
        self.log.info('this is OK')
