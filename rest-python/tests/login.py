import sys,os,time
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest


@test(groups=['login'])
class LoginTest(RHQRestTest):

    @before_class
    def setUp(self):
        self._orig_auth = self.auth

    @test
    def login_successfull(self):
        self.auth = self._orig_auth
        r = self.get('')
        assert_equal(r.status_code, 200)

    @test
    def login_failed(self):
        self.auth = ('rhqadmin','foo')
        r = self.get('')
        assert_equal(r.status_code, 401)
        
    @test
    def ldap_login_success(self):
        # set credential to known example LDAP user
        self.auth = ('jonqa','jonqa')
        r = self.get('')
        assert_equal(r.status_code, 200)

    
    @test
    def ldap_login_invalid_pass(self):
        # set credential to known example LDAP user with invalid pass
        self.auth = ('jonqa','jonqa2')
        r = self.get('')
        assert_equal(r.status_code, 401)
