import sys,os
import requests,json
import unittest

from testcase import RHQRestTest

class ResourceTest(RHQRestTest):

    @classmethod
    def setUpClass(self):
        RHQRestTest.setUpClass()
        self.res_id = 10001

    def test_get_resource(self):
        r = self.get('resource/%d' % self.res_id)
        self.assertEquals(r.status_code, 200)
        resource = r.json()


    def test_get_resource_hierarchy(self):
        r = self.get('resource/%d/hierarchy' % self.res_id)
        self.assertEquals(r.status_code, 200)

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
            self.assertEquals(len(data),ps,'Server returned %d items on page, pagesize %d' % (len(data),ps))
            id_list = map(lambda r: r['resourceId'],data)
            ids += id_list
            self.log.info('Request %s returned %s' % (request,str(id_list)))
        self.assertEquals(len(ids),len(set(ids)),'Server listed same resoruces on different pages')

