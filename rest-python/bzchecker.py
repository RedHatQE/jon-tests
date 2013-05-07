import requests
import json
import os
from proboscis import SkipTest

BUGZILLA='https://bugzilla.redhat.com/jsonrpc.cgi'
class blockedBy(object):
    def __init__(self,bz):
        self.bz = bz

    def __call__(self,f):
        def wrap(*args):
            if os.getenv('BZCHECK'):
                json_data = json.dumps([{'ids':self.bz}])
                r = requests.get(BUGZILLA+'?method=Bug.get&params=%s' % json_data)
                for bug in r.json()['result']['bugs']:
                    if bug['status'] in ['NEW','MODIFIED','ON_DEV']:
                        raise SkipTest('BZ%s %s - %s' %(bug['id'],bug['status'],bug['summary']))
            f(*args)
        wrap.func_name = f.func_name
        return wrap

