import sys,os
import requests,json
import unittest
import proboscis.asserts as asserts
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest
@test
class CreateResourceTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_eap6standalone()['resourceId'])
        self.net_iface_body = {'resourceName':'testnetifaceX',
                'typeName':'Network Interface',
                'pluginName':'JBossAS7',
                'parentId':self.res_id}

    @test
    def create_child(self):
        r = self.post('resource',self.net_iface_body)
        assert_equal(r.status_code,201)
        data = r.json()
        assert_is_not_none(data['resourceId'])
        self.iface_id = data['resourceId']

    @test(depends_on=[create_child])
    def create_child_when_exists(self):
        r = self.post('resource',self.net_iface_body)
        data = r.json()
        assert_is_not_none(data['resourceId'])
        assert_equal(self.iface_id,data['resourceId'],'Server must return an existing resource')
        assert_equal(r.status_code,304,'Server must return NOT MODIFIED response')

    @test(depends_on=[create_child])
    def delete_child(self):
        r = self.delete('resource/%s' % self.iface_id)
        assert_equal(r.status_code,200)
        assert_equal(self.get('resource/%s' % self.iface_id).status_code,404,'Server must return NOT FOUND when resource was just deleted')


@test
class GetResourceTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_agent()['resourceId'])

    @test
    def get_resource(self):
        r = self.get('resource/%d' % self.res_id)
        assert_equal(r.status_code, 200)
        resource = r.json()
        with asserts.Check() as check:
            for key in ['resourceName','resourceId','typeName','typeId','pluginName','parentId']:
                check.true(key in resource)

    @test
    def get_resource_hierarchy(self):
        r = self.get('resource/%d/hierarchy' % self.res_id)
        assert_equal(r.status_code, 200)

    @test
    def test_paging(self):
        self.paging(1,2)
        self.paging(6,2)
        self.paging(15,1)
        self.paging(3,5)

    def paging(self,ps,pages):
        ids = []
        for page in xrange(pages):
            request = 'resource?ps=%d&page=%d' % (ps,page)
            data = self.get(request).json()
            assert_equal(len(data),ps,'Server returned %d items on page, pagesize %d' % (len(data),ps))
            id_list = map(lambda r: r['resourceId'],data)
            ids += id_list
            self.log.info('Request %s returned %s' % (request,str(id_list)))
        assert_equal(len(ids),len(set(ids)),'Server listed same resoruces on different pages')

