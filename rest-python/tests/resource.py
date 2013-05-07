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
        assert_equal(self.iface_id,data['resourceId'],'Returned unexpected resource %s' % data['resourceId'])
        assert_equal(r.status_code,201,'Returned unexpected status %d' % r.status_code)

    @test(depends_on=[create_child])
    def delete_child(self):
        r = self.delete('resource/%s?delete=true' % self.iface_id)
        assert_equal(r.status_code,204,'Returned unexpected status %d' % r.status_code)
        r = self.get('resource/%s' % self.iface_id)            
        assert_equal(r.status_code,404,'Returned unexpected status %d for deleted resource' % r.status_code)

    def create_child_content(self):
        body = {'resourceName':'deploy.war'}


@test
class GetResourceTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_agent()['resourceId'])

    def __check_resource_fields(self,resource,keys=None):
        if not keys:
            keys = ['resourceName','resourceId','typeName','typeId','pluginName','parentId']
        with asserts.Check() as check:
            for key in keys:
                check.true(key in resource)

    @test
    def get_resource(self):
        r = self.get('resource/%d' % self.res_id)
        assert_equal(r.status_code, 200)
        resource = r.json()
        self.__check_resource_fields(resource)

    @test
    def get_non_existing_resource(self):
        r=9999999 # non-existing resource id
        assert_equal(self.get('resource/%d' % r).status_code,404)
        assert_equal(self.get('resource/%d/hierarchy' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/history' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/summary' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/schedules' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/children' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/alerts' % r).status_code,404)

    @test
    def get_resource_hierarchy(self):
        r = self.get('resource/%d/hierarchy' % self.res_id)
        assert_equal(r.status_code, 200)
        result = r.json()
        level = 0
        keys = ['id','name']
        self.__check_resource_fields(result,keys=keys)
        for child in result['children']:
            self.__check_resource_fields(child,keys=keys)
            if level < 1: level += 1
            if child['children']:
                for child2 in child['children']:
                    self.__check_resource_fields(child2,keys=keys)
                    if level < 2: level += 1
        assert_true(level >= 2,'Failed to find 3rd level resource in hierarchy')

    #@test
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

