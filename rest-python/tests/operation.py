import sys,os,time
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest

@test(groups=['op'])
class OperationTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])
        self.did = self.find_op_def('discovery')
    
    def find_op_def(self,name):
        r = self.get('operation/definitions?resourceId=%d' % self.res_id)
        for od in r.json():
            if name == od['name']:
                return int(od['id'])

    @test
    def create_draft_invalid_def_id(self):
        r = self.post('operation/definition/%d?resourceId=%d' %(99999,self.res_id),{})
        assert_equal(r.status_code,404)
    
    @test
    def create_draft_invalid_res_id(self):
        r = self.post('operation/definition/%d?resourceId=%d' %(self.did,99999),{})
        assert_equal(r.status_code,404)
    
    @test
    def create_draft_invalid_def_both(self):
        r = self.post('operation/definition/%d?resourceId=%d' %(99999,99999),{})
        assert_equal(r.status_code,404)

    @test
    def create_draft(self):
        r = self.post('operation/definition/%d?resourceId=%d' %(self.did,self.res_id),{})
        assert_equal(r.status_code,200)
        self.op_id = r.json()['id']

    @test
    def get_operation_invalid_id(self):
        r = self.get('operation/%d' % 9999)
        assert_equal(r.status_code,404)

    @test(depends_on=[create_draft])
    def get_operation(self):
        r = self.get('operation/%d' % self.op_id)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_equal(data['id'],self.op_id)
        self.log.info(data)

    @test(depends_on=[create_draft])
    def schedule_invalid_params(self):
        r = self.get('operation/%d' % self.op_id)
        data = r.json()
        data['readyToSubmit'] = True
        del data['links']
        data['params'] = {'detailedDiscovery':'foo'}
        r = self.put('operation/%d' %(self.op_id),data)
        assert_equal(r.status_code,406)
    
    @test
    def schedule_invalid_op_id(self):
        r = self.get('operation/%d' % self.op_id)
        data = r.json()
        data['readyToSubmit'] = True
        del data['links']
        data['params'] = {'detailedDiscovery':'foo'}
        r = self.put('operation/%d' % 999999,data)
        assert_equal(r.status_code,404)

    @test(depends_on=[create_draft])
    def schedule(self):
        r = self.get('operation/%d' % self.op_id)
        data = r.json()
        data['readyToSubmit'] = True
        del data['links']
        data['params'] = {'detailedDiscovery':True}
        r = self.put('operation/%d' %(self.op_id),data)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(data['links'][0].has_key('history'),'Operation returned by server was not scheduled')
        self.log.info(data)
        self.op_hist = data['links'][0]['history']['href']

    @test(depends_on=[schedule])
    def get_history(self):
        r = self.get(self.op_hist)
        assert_equal(r.status_code,200)
        self.log.info(r.json())

    @test(depends_on=[schedule])
    def get_history_html(self):
        r = self.get(self.op_hist,accepts='text/html')
        assert_equal(r.status_code,200)
        #self.log.info(r.text)
        r = self.get('operation/history',accepts='text/html')
        assert_equal(r.status_code,200)
        #self.log.info(r.text)
    
    @test(depends_on=[schedule])
    def get_full_history(self):
        r = self.get('operation/history?resourceId=%d' % self.res_id)
        assert_equal(r.status_code,200)
        assert_true(len(r.json()) > 0)

    @test(depends_on=[schedule])
    def get_history_wait_for_result(self):
        result = self.get(self.op_hist).json()
        while result['status'] == 'In Progress':
            result = self.get(self.op_hist).json()
            time.sleep(1)
        assert_is_not_none(result['result'])
        self.log.info(result)

    @test(depends_on=[get_history_wait_for_result])
    def delete_op_history(self):
        self.create_draft()
        self.schedule()
        self.get_history_wait_for_result()
        r = self.delete(self.op_hist)
        assert_equal(r.status_code,204)

    @test(depends_on=[get_history_wait_for_result])
    @blockedBy('962855')
    def delete_op_history_invalid(self):
        r = self.delete(self.op_hist+'foo?validate=true')
        assert_equal(r.status_code,404)

@test(groups=['opdef'])
class OperationDefinitionsTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])

    def _check_opdef(self,opDef):
        def cb_value(key,value):
            if key == 'params' and not type(value) == type([]):
                return 'params value should be array'
            if 'id' == key and not type(value) == type(0):
                return 'id value should be number'
            if 'name' == key and not value:
                return 'name value should not be null'
        self.check_fields(opDef,['id','params','name','links'],cb_value)

    def find_op_def(self,name):
        r = self.get('operation/definitions?resourceId=%d' % self.res_id)
        for od in r.json():
            if name == od['name']:
                return int(od['id'])
    @test
    def get_op_def(self):
        did = self.find_op_def('discovery')
        r = self.get('operation/definition/%d' % did)
        assert_equal(r.status_code,200)
        self._check_opdef(r.json())
        r = self.get('operation/definition/%d?resourceId=%d' % (did, self.res_id))
        assert_equal(r.status_code,200)
        self._check_opdef(r.json())

    @test
    def get_op_def_invalid_id(self):
        r = self.get('operation/definition/99999')
        assert_equal(r.status_code,404)

    @test
    def list_op_defs_invalid_resource(self):
        r = self.get('operation/definitions?resourceId=99999')
        assert_equal(r.status_code,404)
    
    @test
    def list_op_defs_no_params(self):
        r = self.get('operation/definitions')
        assert_equal(r.status_code,406)

    @test
    def list_op_defs(self):
        r = self.get('operation/definitions?resourceId=%d' % self.res_id)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(len(data) == 3,'Server did not return 3 operation definitions for platform resource')
        for od in data:
            self._check_opdef(od)

