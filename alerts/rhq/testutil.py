
def assertDict(obj,*args,**kwargs):
    for key,value in kwargs.items():
        if not obj.has_key(key):
            raise Exception('Alert does not have field [%s] to assert' % key)
        assert obj[key] is value, 'Field "%s" does not equal %s' % (key,value)
