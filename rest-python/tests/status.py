import sys,os
import requests,json
import unittest
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test
from testcase import RHQRestTest

@test(groups=['status'])
class StatusTest(RHQRestTest):
    @test
    def test_status(self):
        r = self.get('status')
        assert_equal(r.status_code, 200)
