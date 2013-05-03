import sys,os
import requests,json
import unittest

from testcase import RHQRestTest

class StatusTest(RHQRestTest):

    def test_status(self):
        r = self.get('status.json')
        self.assertEquals(r.status_code, 200)
        self.log.info(r.json())
