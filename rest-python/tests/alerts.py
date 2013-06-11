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
        # platform ID to create alert DRIFT definition 
        self.plat_id = int(self.find_resource_platform()['resourceId'])
        # agent ID to create rest of alert definitions
        self.res_id = int(self.find_resource_agent()['resourceId'])
        # schedule ID of some metric on agent
        self.sched_id = self._find_schedule('NumberTotalCommandsSent')
        if not self.sched_id:
            raise Exception('Schedule NumberTotalCommandsSent not found')
        # schedule id of TRAIT metric on agent
        self.trait_id = self._find_schedule('Trait.SigarVersion')
        if not self.trait_id:
            raise Exception('Schedule Trait.SigarVersion  not found')

        # simple alert-def body
        self.body = {'name':'restAlertDef','enabled':True,'dampeningCategory':'NONE','conditionMode':'ALL'}

    def _find_schedule(self,name):
        schedules = self.get('resource/%d/schedules'%self.res_id).json()
        for sch in schedules:
            self.log.info(sch['scheduleName'])
            if name == sch['scheduleName']:
                return int(sch['definitionId'])
    
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
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)


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
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)

    @test
    def create_alertdef_condition_avail_duration(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-availability-duration'
        body['conditions'] = [{'name':'AVAIL_DURATION_DOWN','category':'AVAIL_DURATION','option':'600'},
                {'name':'AVAIL_DURATION_NOT_UP','category':'AVAIL_DURATION','option':'600'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)

    @test
    @blockedBy('967742')
    def create_alertdef_condition_drift(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-drift-detection'
        body['conditions'] = [{'name':'drift.*','category':'DRIFT','option':'.*'}]
        r = self.post('alert/definitions?resourceId=%d'%self.plat_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
        
    @test
    @blockedBy('967744')
    def create_alertdef_condition_event(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-event'
        body['conditions'] = [{'name':'INFO','category':'EVENT','option':'.*'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
        
    @test
    @blockedBy('967832')
    def create_alertdef_condition_threshold(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-measurement-threshold'
        body['conditions'] = [{'category':'THRESHOLD','threshold':12345, 'comparator':'<','measurementDefinition':self.sched_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
        # assertion for BZ 967832
        assert_equal(data['conditions'][0]['measurementDefinition'],self.sched_id)
    
    @test
    def create_alertdef_condition_resource_config(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-resource-config'
        body['conditions'] = [{'category':'RESOURCE_CONFIG'}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
        
    @test
    def create_alertdef_condition_trait(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-trait'
        body['conditions'] = [{'category':'TRAIT','option':'xx.*','measurementDefinition':self.trait_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)

    @test
    def create_alertdef_condition_change(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-change'
        body['conditions'] = [{'category':'CHANGE','measurementDefinition':self.sched_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
        
        
    @test
    def create_alertdef_condition_range(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-range'
        body['conditions'] = [{'category':'RANGE','option':'7','threshold':'0.3','comparator':'<','measurementDefinition':self.sched_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
    
    @test    
    def create_alertdef_condition_baseline(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-baseline'
        body['conditions'] = [{'category':'BASELINE','option':'max','threshold':'0.4','comparator':'<','measurementDefinition':self.sched_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.res_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,201)
    
    @test
    @blockedBy('973102')
    def create_alertdef_condition_incorrect_measurement_def(self):
        body = self.body.copy()
        body['name'] = 'rest-condition-incorrect-mdef'
        body['conditions'] = [{'category':'BASELINE','option':'max','threshold':'0.2','comparator':'<','measurementDefinition':self.sched_id}]
        r = self.post('alert/definitions?resourceId=%d'%self.plat_id,body)
        data = r.json()
        self.log.info(data)
        assert_equal(r.status_code,406)
        
        
@test(groups=['alert'])
class AlertTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])
    
