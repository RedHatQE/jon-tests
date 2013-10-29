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
        sch_name = 'Native.MemoryInfo.actualUsed'
        self.res_id = int(self.find_resource_platform()['resourceId'])
        schedules = self.get('resource/%d/schedules'%self.res_id).json()
        self.sid = self._find_schedule(sch_name)
        if not self.sid:
            raise Exception('Schedule %s not found' % sch_name)

    def _find_schedule(self,name):
        schedules = self.get('resource/%d/schedules'%self.res_id).json()
        for sch in schedules:
            if name == sch['scheduleName']:
                return int(sch['scheduleId'])

    @test
    def set_schedule(self):
        sch = {'enabled':True,'collectionInterval':60000}
        r = self.put('metric/schedule/%d' % self.sid,sch)
        assert_equal(r.status_code,200)
        self.log.info('Waiting 2minutes to get any metric data')
        time.sleep(120)

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
    def get_data_invalid_id(self):
        r = self.get('metric/data/%d' % 99999)
        assert_equal(r.status_code,404)

    @test(depends_on=[set_schedule])
    def get_data(self):
        r = self.get('metric/data/%d' % self.sid)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(len(data['dataPoints'])>0)
        self._check_metric_data_fields(data)
    
    @test(depends_on=[set_schedule])
    def get_data_zero_datapoints(self):
        r = self.get('metric/data/%d?dataPoints=0' % (self.sid))
        assert_equal(r.status_code,406)
    
    @test(depends_on=[set_schedule])
    def get_data_html(self):
        r = self.get('metric/data/%d' % self.sid,accepts='text/html')
        assert_equal(r.status_code,200)
    
    @test(depends_on=[set_schedule])
    @blockedBy('963804')
    def get_data_multi(self):
        r = self.get('metric/data/?sid=%d&hideEmpty=false&dataPoints=5' % (self.sid))
        assert_equal(r.status_code,200)
        result = r.json()
        assert_equal(len(result),1)
        for data in result:
            assert_equal(len(data['dataPoints']),5)
            self._check_metric_data_fields(data)
    
    @test(depends_on=[set_schedule])
    @blockedBy('963804')
    def get_data_multi_2shedules(self):
        schedules = self.get('resource/%d/schedules'%self.res_id).json()
        sch_name = 'Native.MemoryInfo.free'
        sid = self._find_schedule(sch_name)
        if sid == None:
            raise Exception('Unable to find %s schedule' % sch_name)
        r = self.get('metric/data/?sid=%d,%d&hideEmpty=true&dataPoints=5' % (self.sid,sid))
        assert_equal(r.status_code,200)
        result = r.json()
        assert_equal(len(result),2)
        for data in result:
            self._check_metric_data_fields(data)

    def _check_metric_data_fields(self,data):
        def value_cb(key,value):
            if key in ['min','max','avg']:
                if value == None:
                    return '%s value must not be None' % key
        self.check_fields(data,['scheduleId','min','max','avg','dataPoints','maxTimeStamp','minTimeStamp'],value_cb)

    @test
    @blockedBy('963667')
    def get_data_multi_without_params(self):
        r = self.get('metric/data')
        assert_equal(r.status_code,406)
    
    @test(depends_on=[set_schedule])
    @blockedBy('963734')
    def get_data_multi_zero_datapoints(self):
        r = self.get('metric/data/?sid=%d&hideEmpty=true&dataPoints=0' % (self.sid))
        assert_equal(r.status_code,406)
    
    @test
    def get_data_for_resource_invalid_id(self):
        r = self.get('metric/data/resource/%d' % 99999)
        assert_equal(r.status_code,404)

    @test
    def get_data_raw_invalid_id(self):
        r = self.get('metric/data/%d/raw' % 99999)
        assert_equal(r.status_code,404)
    
    @test
    def get_data_raw(self):
        r = self.get('metric/data/%d/raw' % self.sid)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(len(data) > 0)
        for item in data:
            self.check_fields(item,['scheduleId','value','timeStamp'])

    @test
    def get_data_for_resource(self):
        r = self.get('metric/data/resource/%d' % self.res_id)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(len(data) > 0)
        for metric in data:
            assert_equal(len(metric['dataPoints']),0)
            self._check_metric_data_fields(metric)

    @test
    def get_data_for_resource_with_datapoints(self):
        r = self.get('metric/data/resource/%d?includeDataPoints=true' % self.res_id)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_true(len(data) > 0)
        for metric in data:
            assert_equal(len(metric['dataPoints']),60,'Schedule %d did not contain 60 (default) datapoints' % metric['scheduleId'])
            self._check_metric_data_fields(metric)

    @test(groups=['putmetric'])
    @blockedBy('1024348')
    def put_data_raw(self):
        t = int(time.time() * 1000) # python represents timestamp with float and different precision
        r = self.put('metric/data/%d/raw/%d' % (self.sid,t),{'value':0.5})
        assert_equal(r.status_code,201)
        r = self.get(r.headers['location'])
        assert_equal(r.status_code,200)
        size = len(r.json())
        assert_equal(size,1,'Should return exactly 1 data we just pushed, but server returned %d' % size)
        assert_equal(r.json()[0]['value'],0.5,'Unexpected returned value')
    
    @test(groups=['putmetric'])
    @blockedBy('964227')
    def put_data_raw_old_time(self):
        t = 12345  # set time to very long past
        r = self.put('metric/data/%d/raw/%d' % (self.sid,t),{'value':0.5})
        assert_equal(r.status_code,406,'Server allowed to PUT data with very old timestamp')
    
    @test(groups=['putmetric'])
    def put_data_raw_invalid_id(self):
        t = int(time.time() * 1000)
        r = self.put('metric/data/%d/raw/%d' % (9999,t),{'value':0.5})
        assert_equal(r.status_code,404)


    @test(groups=['putmetric'])
    def put_data_raw_with_trait_schedule_id(self):
        sid = self._find_schedule('Trait.hostname')
        if sid == None:
            raise Exception('Schedule not found')
        t = int(time.time() * 1000) 
        r = self.put('metric/data/%d/raw/%d' % (sid,t),{'value':0.5})
        assert_equal(r.status_code,406)
