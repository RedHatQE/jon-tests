'''
Alert baseline setting file. This setting file could be used with following JON deployment:
    . 1x JON Server with storage node
    . 1x agent + EAP5
    . 2x agent + EAP6 domain + EAP6 standalone
    . 1x agent + EAP6 standalone
    . 1x agent + EAP6 domain
'''
def mb(mb):
    '''Converts `num` in MB to B (bytes)'''
    return 1024*1024*mb

# setup comma-separated list of emails that get notified
mailTo = 'fbrychta@redhat.com'


# baselines
baselines = [
        {'select':('.*','RHQ Agent','JVM','Threading'),'metric':'Thread Count','max':80},
        {'select':('.*','RHQ Agent','JVM','Memory Subsystem'),'metric':'Heap Usage','max':mb(150)},
        {'select':('.*','RHQ Server','platform-mbean','threading'),'metric':'Thread Count','max':300},
        {'select':('.*','RHQ Server','platform-mbean','memory$'),'metric':'Used heap','max':mb(1000)},
        {'select':('.*',),'metric':'System Load','max':4},
        {'select':('.*','/$'),'metric':'Used Percentage','max':80},
        {'select':('.*','/$'),'metric':'Free Files','min':100000},
        {'select':('.*','RHQ Storage Node','Cassandra Server JVM','Threading'),'metric':'Thread Count','max':200},
        {'select':('.*','RHQ Storage Node','Cassandra Server JVM','Memory Subsystem'),'metric':'Heap Usage','max':mb(400)}
    ]
