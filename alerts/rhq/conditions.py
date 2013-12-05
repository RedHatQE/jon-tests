
def availGoesDown():
    '''Condition of AVAIL_GOES_DOWN type'''
    return {'name':'AVAIL_GOES_DOWN','category':'AVAILABILITY'}

def availGoesUp():
    '''Condition of AVAIL_GOES_UP type'''
    return {'name':'AVAIL_GOES_UP','category':'AVAILABILITY'}

def availGoesUnknown():
    '''Condition of AVAIL_GOES_UNKNOWN type'''
    return {'name':'AVAIL_GOES_UNKNOWN','category':'AVAILABILITY'}

def availGoesNotUp():
    '''Condition of AVAIL_GOES_NOT_UP type'''
    return {'name':'AVAIL_GOES_NOT_UP','category':'AVAILABILITY'}

def availStaysDown(duration=60):
    '''Condition of type: availability goes down and stays for `duration` seconds
    
    :param duration: time in seconds 
    '''
    return {'name':'AVAIL_DURATION_DOWN','category':'AVAIL_DURATION','option':str(duration)}

def availStaysNotUp(duration=60):
    '''Condition of type: availability goes not UP and stays for `duration` seconds
    
    :param duration: time in seconds 
    '''
    return {'name':'AVAIL_DURATION_NOT_UP','category':'AVAIL_DURATION','option':str(duration)}

def traitValueChange(schedule=None,expr=''):
    '''Condition of type: trait value has changed and matches given `expr`
    
    :param schedule: schedule body 
    :param expr: regular expression to match trait value
    '''
    assert schedule is not None, 'schedule must not be none'     
    return {'category':'TRAIT','option':expr,'measurementDefinition':schedule['definitionId']}

def operationExecution(op,status='SUCCESS'):
    assert op is not None, 'op (operation name) must not be none'     
    return {'name':op,'option':status,'category':'CONTROL'}
