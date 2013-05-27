import sys,os,time
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest


@test(groups=['alertdef'])
class AlertDefinitionTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])
        # simple alert-def body
        self.body = {'name':'restAlertDef','enabled':True,'dampeningCategory':'NONE','conditionMode':'ALL'}

    def _check_opdef(self,opDef):
        def cb_value(key,value):
            if key == 'params' and not type(value) == type([]):
                return 'params value should be array'
            if 'id' == key and not type(value) == type(0):
                return 'id value should be number'
            if 'name' == key and not value:
                return 'name value should not be null'
        self.check_fields(opDef,['id','params','name','links'],cb_value)

    @test
    def create_alertdef_invalid_query_params(self):
        for req in ['alert/definitions',
                'alert/definitions?resourceId=1&groupId=2',
                'alert/definitions?groupId=2&resourceTypeId=1']:
            r = self.post(req,self.body)
            assert_equal(r.status_code,406)

    @test
    @blockedBy('967488')
    def create_alertdef_condition_op_result(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-operationResult'
        body['conditions'] = [{'name':'discovery','option':'SUCCESS','category':'CONTROL'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        assert_equal(r.status_code,201)
        data = r.json()
        self.log.info(data)


    @test
    def create_alertdef_condition_avail_change(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-availability-change'
        body['conditions'] = [{'name':'AVAIL_GOES_DOWN','category':'AVAILABILITY'},
                {'name':'AVAIL_GOES_UP','category':'AVAILABILITY'},
                {'name':'AVAIL_GOES_DISABLED','category':'AVAILABILITY'},
                {'name':'AVAIL_GOES_NOT_UP','category':'AVAILABILITY'},
                {'name':'AVAIL_GOES_UNKNOWN','category':'AVAILABILITY'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        assert_equal(r.status_code,201)
        data = r.json()
        self.log.info(data)

    @test
    def create_alertdef_condition_avail_duration(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-availability-duration'
        body['conditions'] = [{'name':'AVAIL_DURATION_DOWN','category':'AVAIL_DURATION','option':'600'},
                {'name':'AVAIL_DURATION_NOT_UP','category':'AVAIL_DURATION','option':'600'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        assert_equal(r.status_code,201)
        data = r.json()
        self.log.info(data)

    @test
    def create_alertdef_condition_drift(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-drift-detection'
        body['conditions'] = [{'name':'drift.*','category':'DRIFT','option':'.*'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        assert_equal(r.status_code,201)
        data = r.json()
        self.log.info(data)
        
@test(groups=['alert'])
class AlertTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])
    
