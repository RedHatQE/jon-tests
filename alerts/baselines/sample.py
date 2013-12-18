'''
Alert baseline setting sample file.
'''
def mb(mb):
    '''Converts `num` in MB to B (bytes)'''
    return 1024*1024*mb

# setup comma-separated list of emails that get notified
mailTo = 'your@email'


# baselines is an array of baselines

# each baseline is a dict, having 
# :selector - a tuple of regular expressions to select a subset of resource
# :metric - name or displayName of metric
# :max - max accepted value for metric
# :name - name for alert (optional)
baselines = [
        # alert when any of imported agents raises it's thread count to 80
        {'select':('.*','RHQ Agent','JVM','Threading'),'metric':'Thread Count','max':80},
        # alert when any of RHQ Servers raises it's thread count to 300
        {'select':('.*','RHQ Server','platform-mbean','threading'),'metric':'Thread Count','max':300},
        # alert whay any of RHQ servers raises JVM heap to 1000MB
        {'select':('.*','RHQ Server','platform-mbean','memory$'),'metric':'Used heap','max':mb(1000)},
        # alert when any host has system load higher than 4
        {'select':('.*',),'metric':'System Load','max':4}
    ]
