
def assertDict(obj,*args,**kwargs):
    for key,value in kwargs.items():
        if not obj.has_key(key):
            raise Exception('Dict %s does not have field [%s] to assert' % (str(obj),key))
        assert obj[key] == value, 'Field "%s" does not equal %s' % (key,value)
    return True
