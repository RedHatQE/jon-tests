'''This module contains various condition types to be used when creating alert definition'''

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

def metricValueThreshold(schedule=None,comp='>',value=1):
    '''Condition of type metricValueThreshold
    This conditoin is met whenever metric value for given `schedule` compared by `comp` to `value` evaluates to True

    :param schedule: schedule body
    :param comp: comparator String ">" "<" "="
    :param value: value to be compared
    '''
    assert schedule is not None, 'schedule must not be none'
    return {'category':'THRESHOLD','threshold':value,'comparator':comp,'measurementDefinition':schedule['definitionId']}


def metricValueRange(schedule=None,comp='>',low=0,high=1):
    '''Condition of type measurementValueRange
        this condition is met whenever metric value for given `schedule` reports value maching range
        defined by `low` and `high` and `comp` comparator

    :param schedule: schedule body
    :param comp: comparator String ">" : match outside this range, exclude min/max,"<=" : match inside this range, include min/max, additionally ">=","<"
    :param low: range minimum
    :param high: range maximum
    '''
    assert schedule is not None, 'schedule must not be none'
    return {'category':'RANGE','option':str(high),'threshold':str(low),'comparator':comp,'measurementDefinition':schedule['definitionId']}

def operationExecution(op,status='SUCCESS'):
    '''Condition of type : given operation name `op` finishes with status `status`

    :param op: operation name
    :param status: status to match (SUCCESS|FAILURE|INPROGRESS|CANCELED)
    '''
    assert op is not None, 'op (operation name) must not be none' 
    return {'name':op,'option':status,'category':'CONTROL'}
