'''This module contains tests for firing alert based on single condition while covering all possible contition types'''

from rhq.conditions import *
from rhq.dampenings import *
from testcase import RHQAlertTest
from nose.plugins.attrib import attr
from bzchecker import blockedBy

@attr('single','numeric','range')
class FireMeasurementValueRangeAlert(RHQAlertTest):
    '''This class tests alerts fired by measurementValueRange condition type (alert is fired when numeric value of
    metric is inside/outsite predefined range of absolute values)
    '''
    @attr('outside-exclusive')
    @blockedBy('1041079')
    def test_rangeOutsideExclusive(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        fired = s.alertCount(p)
        sch = s.getSchedule(p,name='Native.MemoryInfo.free')
        a1 = s.defineAlert(p,metricValueRange(sch,comp='>',low=9,high=11))
        s.waitForAlertDef()
        s.sendMetricData(p,sch,12)
        s.waitForAlert()
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)


@attr('single','numeric','threshold')
class FireMeasurementValueThresholdAlert(RHQAlertTest):
    '''This class tests alerts fired by measurementValueThreshold condition type (alert is fired when numeric value of
    metric is higher/lower/equal to defined value)
    '''
    @blockedBy('1041079')
    def test_threshold(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        fired = s.alertCount(p)
        sch = s.getSchedule(p,name='Native.MemoryInfo.free')
        a1 = s.defineAlert(p,metricValueThreshold(sch,comp='>',value=10))
        s.waitForAlertDef()
        s.sendMetricData(p,sch,11)
        s.waitForAlert()
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)


@attr('single','trait')
class FireTraitAlert(RHQAlertTest):
    '''This class tests alerts fired by TraitValueChange condition type (alert's get rised when given trait value
    changes and matches given pattern'''

    @blockedBy('1035890')
    def test_traitValueChange(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        fired = s.alertCount(p)
        sch = s.getSchedule(p,name='Trait.osname')
        s.sendMetricData(p,sch,'hehe')
        a1 = s.defineAlert(p,[traitValueChange(sch,'test')])
        s.sleep(60)
        s.sendMetricData(p,sch,'test')
        s.sleep(60)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)

@attr('single','control')
class FireControlAlert(RHQAlertTest):
    '''This class tests alerts fired by CONTROL condidion type (when an operation finishes with some result)'''

    @attr('operationSuccess')
    def test_operationStatusSuccess(self):
        s = self.rhqServer()
        agent = s.findRHQAgent()
        # define 2  but only a1 should be fired
        a1 = s.defineAlert(agent,operationExecution('executePromptCommand'))
        a2 = s.defineAlert(agent,operationExecution('executePromptCommand',status='FAILURE'))
        a3 = s.defineAlert(agent,operationExecution('foo'))
        s.sleep(30)
        fired = s.alertCount(agent)
        s.runOperation(agent,name='executePromptCommand',params={'command':'ping'})
        s.sleep(10)
        fired = s.alertCount(agent) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        s.undefineAlert([a1,a2,a3])
    
    @attr('operationFailed')
    def test_operationStatusFailed(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        a1 = s.defineAlert(p,operationExecution('discovery',status='FAILURE'))
        s.sleep(30)
        s.runOperation(p,name='discovery',params={'detailedDiscovery':False})
        s.sleep(10)
        fired = s.alertCount(p)
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        s.deleteResource(p)
    
    @attr('operationFailed')
    def test_operationStatusFailedRealPlatform(self):
        s = self.rhqServer()
        p = s.findPlatform()
        a1 = s.defineAlert(p,operationExecution('cleanYumMetadataCache',status='FAILURE'))
        fired = s.alertCount(p)
        s.sleep(30)
        s.runOperation(p,name='cleanYumMetadataCache')
        s.sleep(10)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        s.undefineAlert(a1)

@attr('single','avail')
class FireAvailabilityAlert(RHQAlertTest):
    '''This class tests alerts fired upon availability-related conditions'''
    @attr('availGoesDown') 
    def test_availGoesDown(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        fired = s.alertCount(p)
        a1 = s.defineAlert(p,availGoesDown())
        s.sleep(30)
        s.sendAvailDown(p)
        s.sleep(60)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)
    
    @attr('availGoesUp') 
    def test_availGoesUp(self):
        s = self.rhqServer()
        p = s.newPlatform()
        fired = s.alertCount(p)
        a1 = s.defineAlert(p,availGoesUp())
        s.sleep(30)
        s.sendAvailUp(p)
        s.sleep(60)
        fired = s.alertCount(p) - fired
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)
    
    @attr('availGoesUnknown') 
    def test_availGoesUnknown(self):
        with self.rhqServer() as s:
            p = s.newPlatform(avail='UP')
            fired = s.alertCount(p)
            a1 = s.defineAlert(p,availGoesUnknown())
            s.sleep(30)
            s.sendAvail(p,avail='UNKNOWN')
            s.sleep(60)
            fired = s.alertCount(p) - fired
            self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
            # if test passed clean up our test resource
            s.deleteResource(p)
    
    @attr('availGoesNotUp')
    def test_availGoesNotUp(self):
        with self.rhqServer() as s:
            p = s.newPlatform(avail='UP')
            fired = s.alertCount(p)
            a1 = s.defineAlert(p,availGoesNotUp())
            s.sleep(30)
            s.sendAvail(p,avail='UNKNOWN')
            s.sleep(60)
            s.sendAvail(p,avail='UP')
            s.sendAvail(p,avail='DOWN')
            s.sleep(60)
            fired = s.alertCount(p) - fired
            self.assertEqual(fired, 2,'Alert count incremeted by 2, but was %d' % fired)
            # if test passed clean up our test resource
            s.deleteResource(p)
   
    @attr('availStaysDown')
    def test_availStaysDown(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        a1 = s.defineAlert(p,availStaysDown(30))
        s.sleep(30)
        s.sendAvail(p,avail='DOWN',keep=20) # send availability DOWN lasting 20s
        s.sleep(20)
        s.sendAvail(p,avail='UP')
        fired = s.alertCount(p)
        self.assertEqual(fired, 0,'Alert count must be 0, but was %d' % fired)
        s.sendAvail(p,avail='DOWN')
        s.sleep(40)
        fired = s.alertCount(p)
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)
    
    @attr('availStaysNotUp')
    def test_availStaysNotUp(self):
        s = self.rhqServer()
        p = s.newPlatform(avail='UP')
        a1 = s.defineAlert(p,availStaysNotUp(30))
        s.sleep(30)
        s.sendAvail(p,avail='DOWN',keep=20) # send availability DOWN lasting 20s
        s.sleep(20)
        s.sendAvail(p,avail='UP')
        fired = s.alertCount(p)
        self.assertEqual(fired, 0,'Alert count must be 0, but was %d' % fired)
        s.sendAvail(p,avail='UNKNOWN')
        s.sleep(40)
        fired = s.alertCount(p)
        self.assertEqual(fired, 1,'Alert count incremeted by 1, but was %d' % fired)
        # if test passed clean up our test resource
        s.deleteResource(p)
