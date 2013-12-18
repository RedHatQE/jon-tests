#!/usr/bin/env python
'''
Utility helping defining alerts on resources based on selector expressions.
Currently supports only alert definitions based on 1 condition of type: measurementValueThreshold

Usecase:
    We're using this script to setup baselines for our testing long-running JON environment and we want to get alerted
    whenever JON Server or agents consumes more resources then is acceptable.
'''

__author__ = 'Libor Zoubek'
__email__ = 'lzoubek@redhat.com'

import os,sys,argparse
sys.path.append(os.path.join(os.path.dirname(__file__),'..'))
from rhq.server import RHQServer
from rhq.conditions import *
from rhq.notifications import emails

import logging.config
logging.config.fileConfig(os.path.join(os.path.dirname(__file__),'..','logging.config'))

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--baseline', metavar='B', help='Alert Baseline setting file')
    parser.add_argument('--host', metavar='H', help='RHQ server hostname (overrides RHQ_HOST environ variable)')
    parser.add_argument('--mailto', metavar='M', help='mail address for recieving alerts (overrides \'mailTo\' from baseline file)')
    args = parser.parse_args()
    if args.baseline:
        if not os.path.exists(args.baseline):
            print 'Alert baseline setting file does not exist'
            sys.exit(1)
        exec 'import %s as baseline' % args.baseline[:-3]
        mailTo = emails(to=baseline.mailTo)
        if args.mailto:
            mailTo = args.mailTo
        host = os.getenv('RHQ_HOST','localhost')
        if args.host:
            host = args.host
        s = RHQServer(host=host)
        if s.importResources() > 0:
            # if something was imported, wait 
            s.sleep(120)
        for base in baseline.baselines:
            print 'Looking up resources by path matching : %s' % ' > '.join(list(base['select']))
            resources = s.findResourcesByPath(*base['select'])
            print 'Found %s' % str(map(lambda r:str(r['resourceId'])+':'+r['resourceName'],resources))
            for res in resources:
                sch = s.getSchedule(res,base['metric'])
                if not sch:
                    print 'Cannot find schedule %s for resource %s' % (base['metric'],res['resourceName'])
                    continue
                name = '%s > %s' % (base['metric'],str(base['max']))
                if base.has_key('name'):
                    name = base['name']
                s.defineAlert(res,name=name,conditions=metricValueThreshold(sch,'>',base['max']),notifications=mailTo)

