import sys,os,time,re
import requests,json
import unittest
import proboscis.asserts as asserts
from bzchecker import *
from proboscis.asserts import *
from proboscis import before_class
from proboscis import test

from testcase import RHQRestTest


@test(groups=['linkcheck'])
class LinkCheckerTest(RHQRestTest):

    @before_class
    def before(self):
        self.headers = {'accept':'text/html','content-type': 'text/html'}
        self.visited = {}

    @test
    @blockedBy(['970592','970593'])
    def visit_all_html(self):
        with asserts.Check() as check:
            r = self.visit_page(check,'','')
        self.log.info('DONE!! Visited %d pages' % len(self.visited))

    def visit_page(self,check,referer,url,follow=True):
        self.visited[url] = True
        r = self.get(url,accepts='text/html')
        self.log.info('Visiting %s'%url)
        check.equal(r.status_code,200,'Server returned %d on GET %s, refering from %s' %(r.status_code,url,referer))
        if follow:
            for m in re.finditer('<a href=\"([^\"]+)',r.text,re.DOTALL | re.IGNORECASE):
                href = m.group(1)
                if href.find('/') == 0:
                    href = self.server_url.rstrip('/')+href
                if href.find(self.server_url) >= 0 and not self.visited.has_key(href):
                    self.visit_page(check,url,href)

