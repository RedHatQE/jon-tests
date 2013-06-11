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
        self.visited = {}
        with asserts.Check() as check:
            r = self.visit_page(check,'','',accepts='text/html',regex='<a href=\"([^\"]+)')
        self.log.info('DONE!! Visited %d pages' % len(self.visited))

    @test
    def visit_all_json(self):
        self.visited = {}
        with asserts.Check() as check:
            r = self.visit_page(check,'','',accepts='application/json',regex='href\"\:\"([^\"]+)')
        self.log.info('DONE!! Visited %d pages' % len(self.visited))

    @test
    def visit_all_xml(self):
        self.visited = {}
        with asserts.Check() as check:
            r = self.visit_page(check,'','',accepts='application/xml',regex='href=\"([^\"]+)')
        self.log.info('DONE!! Visited %d pages' % len(self.visited))
    
    def visit_page(self,check,referer,url,follow=True,accepts='text/html',regex=''):
        self.visited[url] = True
        r = self.get(url,accepts=accepts)
        self.log.info('Visiting %s'%url)
        check.equal(r.status_code,200,'Server returned %d on GET %s, refering from %s' %(r.status_code,url,referer))
        if follow:
            for m in re.finditer(regex,r.text,re.DOTALL | re.IGNORECASE):
                href = m.group(1)
                if href.find('reports') >= 0 and href.find('csv') <= 0:
                    # https://bugzilla.redhat.com/show_bug.cgi?id=972774
                    # reports are "just" in csv
                    continue
                if href.find('/') == 0:
                    href = self.server_url.rstrip('/')+href
                if href.find('http') < 0:
                    href = self.url(href)
                if href.find(self.server_url) >= 0 and not self.visited.has_key(href):
                    self.visit_page(check,url,href,accepts=accepts,regex=regex)

