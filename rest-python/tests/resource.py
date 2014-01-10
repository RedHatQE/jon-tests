import sys,os
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest

import link_header

@test
class CreateResourceTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_eap6standalone()['resourceId'])
        self.net_iface_body = {'resourceName':'testnetiface-rest',
                'typeName':'Network Interface',
                'pluginName':'JBossAS7',
                'parentId':self.res_id}
        self.content_body = {'resourceName':'hello.war',
                'typeName':'Deployment',
                'resourceConfig':{'runtimeName':'hello.war'},
                'pluginName':'JBossAS7',
                'parentId':self.res_id}
        self.deployment = '../sahi/src/test/resources/deploy/original/hello.war'

    @test(groups=['resource'])
    @blockedBy('958922')
    def create_child(self):
        r = self.post('resource',self.net_iface_body)
        assert_equal(r.status_code,201)
        data = r.json()
        assert_is_not_none(data['resourceId'])
        self.iface_id = data['resourceId']

    @test(depends_on=[create_child],groups=['resource'])
    @blockedBy('958922')
    def create_child_when_exists(self):
        r = self.post('resource',self.net_iface_body)
        data = r.json()
        assert_is_not_none(data['resourceId'])
        assert_equal(self.iface_id,data['resourceId'],'Returned unexpected resource %s' % data['resourceId'])
        assert_equal(r.status_code,201,'Returned unexpected status %d' % r.status_code)

    @test(depends_on=[create_child],groups=['resource'])
    @blockedBy('958922')
    def delete_child(self):
        r = self.delete('resource/%s?physical=true' % self.iface_id)
        assert_equal(r.status_code,204,'Returned unexpected status %d' % r.status_code)
        r = self.get('resource/%s' % self.iface_id)
        data = r.json()
        assert_equal(data['status'],'DELETED','Returned unexpected status %s for deleted resource' % data['status'])

    @test(groups=['resource'])
    def create_child_content(self):
        self.log.info('Uploading content')
        req = requests.post(
                self.url('content/fresh'),
                data=open(self.deployment,'rb').read(),
                auth = self.auth,
                headers={'Content-Type':'application/octet-stream','accept':self.headers['accept']}
        )
        # get a handle of uploaded file
        handle = req.json()['value']
        self.log.info('Creating resource')
        r = self.post('resource?handle=%s' % handle, self.content_body)
        assert_equal(r.status_code,200)
        data = r.json()
        assert_is_not_none(data['resourceId'])
        self.content_id = data['resourceId']

    @test(depends_on=[create_child_content],groups='resource')
    @blockedBy('1025388')
    def create_child_content_when_exists(self):
        self.log.info('Uploading content')
        req = requests.post(
                self.url('content/fresh'),
                data=open(self.deployment,'rb').read(),
                auth = self.auth,
                headers={'Content-Type':'application/octet-stream','accept':self.headers['accept']}
        )
        # get a handle of uploaded file
        handle = req.json()['value']
        self.log.info('Creating resource')
        r = self.post('resource?handle=%s' % handle, self.content_body)
        assert_equal(r.status_code,500)
        # just parse response to make sure JSON was returned
        data = r.json()

    @test(depends_on=[create_child_content],groups='resource')
    def delete_child_content(self):
        r = self.delete('resource/%s?physical=true' % self.content_id)
        assert_equal(r.status_code,204,'Returned unexpected status %d' % r.status_code)
        r = self.get('resource/%s' % self.content_id)
        data = r.json()
        assert_equal(data['status'],'DELETED','Returned unexpected status %s for deleted resource' % data['status'])

@test(groups=['getresource'])
class GetResourceTest(RHQRestTest):

    @before_class
    def setUp(self):
        self.res_id = int(self.find_resource_agent()['resourceId'])

    def __check_resource_fields(self,resource,keys=None):
        if not keys:
            keys = ['resourceName','resourceId','typeName','typeId','pluginName','parentId']
        self.check_fields(resource,keys)

    @test
    @blockedBy('962853')
    def get_resource(self):
        r = self.get('resource/%d' % self.res_id)
        assert_equal(r.status_code, 200)
        resource = r.json()
        self.__check_resource_fields(resource)

    @test
    @blockedBy(['960529','962858'])
    def get_non_existing_resource(self):
        r=9999999 # non-existing resource id
        assert_equal(self.get('resource/%d' % r).status_code,404)
        assert_equal(self.get('resource/%d/hierarchy' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/history' % r).status_code,404)
        assert_equal(self.get('resource/%d/availability/summary' % r).status_code,404)
        assert_equal(self.get('resource/%d/children' % r).status_code,404)
        assert_equal(self.get('resource/%d/alerts' % r).status_code,404)
        assert_equal(self.get('resource/%d/schedules' % r).status_code,404)

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

    @test()
    def filter_by_status(self):
        for status in ['all','NEW','COMMITTED','DELETED','UNINVENTORIED','IgnoreD']:
            r = self.get('resource?status=%s' % status)
            assert_equal(r.status_code,200,'Invalid status code (%d) when requested ?status=%s' % (r.status_code,status))
            assert_equal(type(r.json()),type([]),'Server did not return array of resources')
        assert_equal(self.get('resource?status=%s' % 'FOO').status_code ,406)


    @test()
    def filter_by_category(self):
        for cat in ['serViCe','Platform','SERVER']:
            r = self.get('resource?category=%s' % cat)
            assert_equal(r.status_code,200,'Invalid status code (%d) when requested ?category=%s' % (r.status_code,cat))
            assert_equal(type(r.json()),type([]),'Server did not return array of resources')
        assert_equal(self.get('resource?category=%s' % 'FOO').status_code ,406)

    @test(groups=['paging'])
    def paging_visit_using_body_links(self):
        self._visit_page_body_links('resource?ps=13&page=0',13,0)

    def _visit_page_body_links(self,url,ps,page):
        r = self.get(url,accepts='application/vnd.rhq.wrapped+json')
        data = r.json()
        assert_true(data.has_key('links'),'links must be present in response body when sending \"application/vnd.rhq.wrapped+json\" accept header')
        assert_true(len(data['data']) <= ps)
        links = data['links']
        current= None
        last = None
        next = None
        prev = None
        for link in links:
            if link.has_key('current'):
                current = link['current']['href']
            if link.has_key('last'):
                last = link['last']['href']
            if link.has_key('next'):
                next = link['next']['href']
            if link.has_key('prev'):
                prev = link['prev']['href']
        assert_is_not_none(current)
        assert_is_not_none(last)
        if data['currentPage'] > 0:
            assert_is_not_none(prev)
        if not data['lastPage'] == data['currentPage']:
            assert_is_not_none(next)
            assert_true(len(data['data']) == ps)
            self._visit_page_body_links(next,ps,page+1)

    @test(groups=['paging'])
    def paging_visit_using_link_headers(self):
        ids = []
        self._visit_page_link_headers('resource?ps=17&page=0',[],ids)
        assert_equal(len(ids),len(set(ids)),'Server listed same resoruces on different pages')
        
    def _visit_page_link_headers(self,url,visited,ids):
        url = self.url(url) # make URL absolute
        r = self.get(url)
        assert_equal(r.status_code,200)
        data = r.json()
        id_list = map(lambda r: r['resourceId'],data)
        ids += id_list
        self.log.info('Request %s returned %s' % (url,str(id_list)))
        visited.append(url)
        links = link_header.parse_link_value(r.headers['link'])
        self.log.info('Parsed link headers : %s' % str(links))
        for link in links.keys():
            assert_true(links[link].has_key('rel'))
            assert_true(links[link]['rel'] in ['current','next','prev','last'])
            if not link in visited:
                self._visit_page_link_headers(link,visited,ids)

    @test(groups=['paging'])
    @blockedBy('966559')
    def paging(self):
        self._paging(1,2)
        self._paging(6,2)
        self._paging(15,1)
        self._paging(3,6)

    def _paging(self,ps,pages):
        ids = []
        for page in xrange(pages):
            request = 'resource?ps=%d&page=%d' % (ps,page)
            r = self.get(request)
            data = r.json()
            assert_equal(len(data),ps,'Server returned %d items on page, pagesize %d' % (len(data),ps))
            id_list = map(lambda r: r['resourceId'],data)
            ids += id_list
            self.log.info('Request %s returned %s' % (request,str(id_list)))
        assert_equal(len(ids),len(set(ids)),'Server listed same resoruces on different pages')

    @test(groups=['paging'])
    @blockedBy('1029996')
    def paging_defaults(self):
        r = self.get('resource')
        data = r.json()
        assert_equal(len(data),20,'Server should return 20 items but returned %d' % len(data))
    
    @test(groups=['paging'])
    @blockedBy('1029996')
    def paging_default_pagesize(self):
        r = self.get('resource?ps=3')
        data = r.json()
        assert_equal(len(data),3,'Server should return 3 items but returned %d' % len(data))

