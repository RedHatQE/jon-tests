'''This module contains tests for firing alert with various dampening scenarios'''

from rhq.conditions import *
from rhq.dampenings import *
from testcase import *
from nose.plugins.attrib import attr
from bzchecker import blockedBy
from unittest import skipIf

@attr('dampening')
class FireTraitAlert(RHQAlertTest):
    '''This class tests alerts fired by TraitValueChange condition type (alert's get rised when given trait value
    changes and matches given pattern'''

    @attr('once')
    def test_dampeningOnce(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        a1 = s.defineAlert(p,availGoesDown(),dampening=once())
        a2 = s.defineAlert(p,availGoesUp(),recovers=a1)
        s.sleep(30)
        s.sendAvailDown(p)
        s.sleep(30)
        s.checkAlertDef(a1,enabled=False)
        s.sendAvailUp(p)
        s.sleep(30)
        fired = s.alertCount(p)
        self.assertEqual(fired, 2,'Alert count incremeted by 2, but was %d' % fired)
        s.checkAlertDef(a1,enabled=True)
        # if test passed clean up our test resource
        s.deleteResource(p)

    @attr('once','ha')
    @skipUnlessHA
    def test_dampeningOnceHA(self):
        s1 = self.rhqServer()
        s2 = self.rhqServer(1)
        p = s1.newPlatform(avail='UP')
        a1 = s1.defineAlert(p,availGoesDown(),dampening=once())
        a2 = s2.defineAlert(p,availGoesUp(),recovers=a1)
        s1.sleep(30)
        s1.sendAvailDown(p)
        s1.sleep(30)
        s1.checkAlertDef(a1,enabled=False)
        s2.sendAvailUp(p)
        s2.sleep(30)
        fired = s2.alertCount(p)
        self.assertEqual(fired,s1.alertCount(p),'Both servers in HA report same alert count')
        self.assertEqual(fired, 2,'Alert count incremeted by 2, but was %d' % fired)
        s1.checkAlertDef(a1,enabled=True)
        # if test passed clean up our test resource
        s.deleteResource(p)

