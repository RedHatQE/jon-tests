
def none():
    '''No dampening'''
    return {'dampeningCategory':'NONE'}

def once():
    ''' ONCE: alert will get fired exactly once and then it gets damped'''
    return {'dampeningCategory':'ONCE'}

def consecutive(occurs=1):
    '''CONSECUTIVE - alert gets damped after it was fired `occurs` times
    
    :param occurs: how many times alert should be fired
    '''
    return {'dampeningCategory':'CONSECUTIVE_COUNT','dampeningCount':occurs}

def timePeriod(occurs=1,period=1,units='MINUTES'):
    '''DURATION_COUNT - alert gets damped after it occurs `occurs` times within past time `period` (in `units`)
    
    :param occurs: how many times alert should be fired
    :param period: time period
    :param units: period unit (MINUTES,HOURS)
    '''
    return {'dampeningCategory':'DURATION_COUNT','dampeningCount':occurs,'dampeningPeriod':period,'dampeningUnit':units}

def lastNEvals(occurs=1,evals=2):
    '''PARTIAL_COUNT - alert gets damped if it was fired at least `occurs` times from past `evals` evaulations
    
    :param occurs: number of alert occurences
    :param evals: number of alert evaulations
    '''
    return {'dampeningCategory':'PARTIAL_COUNT','dampeningCount':occurs,'dampeningPeriod':evals}

