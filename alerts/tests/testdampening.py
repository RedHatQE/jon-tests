'''This module contains tests for firing alert with various dampening scenarios'''

from rhq.conditions import *
from rhq.dampenings import *
from testcase import *
from nose.plugins.attrib import attr
from bzchecker import blockedBy

@attr('dampening','once')
class AlertDampeningOnce(RHQAlertTest):
    '''This class tests alerts dampening=once. Such alert has to get disabled after it was fired once. There can be
    recovery alert, which can re-enable such alert once is fired'''

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

    @attr('ha')
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
        s1.deleteResource(p)

    @attr('event')
    def test_dampeningOnceEvent(self):
        s = self.rhqServer()
        p = s.findPlatform()
        a1 = s.defineAlert(p,event(severity='ERROR'),dampening=once())
        a2 = s.defineAlert(p,event(severity='FATAL'),recovers=a1)
        s.waitForAlertDef()
        es = s.createEventSource(p,'Event Log',location='/dev/null')
        fired = s.alertCount(p)
        for x in range(5):
            s.pushEvent(es,severity='ERROR',detail='error message')
            s.sleep(1)
            s.checkAlertDef(a1,enabled=False)
            s.pushEvent(es,severity='FATAL',detail='fatal message')
            s.sleep(1)
            s.checkAlertDef(a1,enabled=True)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 10, '10 alerts should be fired, but was %d' % fired)
        s.undefineAlert([a1,a2])
 
    @attr('event','syntetic')
    @blockedBy('1058658')
    def test_dampeningOnceEventSynteticPlatform(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        a1 = s.defineAlert(p,event(severity='ERROR'),dampening=once())
        a2 = s.defineAlert(p,event(severity='FATAL'),recovers=a1)
        s.waitForAlertDef()
        es = s.createEventSource(p,'Event Log',location='/dev/null')
        fired = s.alertCount(p)
        for x in range(5):
            s.pushEvent(es,severity='ERROR',detail='error message')
            s.sleep(1)
            s.checkAlertDef(a1,enabled=False)
            s.pushEvent(es,severity='FATAL',detail='fatal message')
            s.sleep(1)
            s.checkAlertDef(a1,enabled=True)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 10, '10 alerts should be fired, but was %d' % fired)
        # if test passed clean up our test resource
        s1.deleteResource(p)


