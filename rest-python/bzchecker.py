import requests
import json
import os
from proboscis import SkipTest
import traceback

BUGZILLA='https://bugzilla.redhat.com/'
class blockedBy(object):
    def __init__(self,bz):
        self.bz = bz

    def __call__(self,f):
        def wrap(*args):
            if os.getenv('BZCHECK'):
                json_data = json.dumps([{'ids':self.bz}])
                try:
                    r = requests.get(BUGZILLA+'jsonrpc.cgi?method=Bug.get&params=%s' % json_data)
                except:
                    print 'Could connect to %s not skipping test method' % BUGZILLA
                    traceback.print_exc()
                    f(*args)
                    return
                for bug in r.json()['result']['bugs']:
                    if bug['status'] in ['NEW','MODIFIED','ASSIGNED','ON_DEV']:
                        bzLink = '%sshow_bug.cgi?id=%s' % (BUGZILLA,bug['id'])
                        raise SkipTest('BZ [%s] %s (%s)' % (bug['status'],bug['summary'],bzLink))
            f(*args)
        wrap.func_name = f.func_name
        return wrap

