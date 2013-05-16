import sys,os,time
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest

@test(groups=['metric'])
class MetricsTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_platform()['resourceId'])
        schedules = self.get('resource/%d/schedules'%self.res_id).json()
        for sch in schedules:
            if 'Native.MemoryInfo.actualUsed' == sch['scheduleName']:
                self.sid = int(sch['scheduleId'])
                break
        if not self.sid:
            raise Exception('Schedule not found')

    @test
    def set_schedule(self):
        sch = {'enabled':True,'collectionInterval':60000}
        r = self.put('metric/schedule/%d' % self.sid,sch)
        assert_equal(r.status_code,200)
        self.log.info('Waiting 2minutes to get any metric data')
 #       time.sleep(120)

    @test
    def get_schedule_invalid_id(self):
        r = self.get('metric/schedule/%d' % 99999)
        assert_equal(r.status_code,404)

    @test(depends_on=[set_schedule])
    def get_schedule_html(self):
        r = self.get('metric/schedule/%d' % self.sid,accepts='text/html')
        assert_equal(r.status_code,200)

    @test(depends_on=[set_schedule])
    @blockedBy('963691')
    def get_schedule(self):
        r = self.get('metric/schedule/%d' % self.sid)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_equal(data['collectionInterval'],60000)
        assert_equal(data['enabled'],True)
        self.check_fields(data,['scheduleId','scheduleName','displayName','unit','type','definitionId','mtime'])

    @test
    @blockedBy('963667')
    def get_data_without_params(self):
        r = self.get('metric/data')
        assert_equal(r.status_cod,406)

    @test()
    def get_data_for_sid_invalid_id(self):
        r = self.get('metric/data/%d' % 99999)
        assert_equal(r.status_code,404)

    @test(depends_on=[set_schedule])
    def get_data_for_sid(self):
        r = self.get('metric/data/%d' % self.sid)
        assert_equal(r.status_code,200)
    
    @test(depends_on=[set_schedule])
    def get_data_for_sid_html(self):
        r = self.get('metric/data/%d' % self.sid,accepts='text/html')
        assert_equal(r.status_code,200)
         

