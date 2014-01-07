'''This module contains various notification types to be used when creating alert definition
'''
from types import StringType

def emails(to=[]):
    '''notification by sending direct email

    :param to: email address string or list of email addresses
    '''
    if type(to) is StringType:
        to = [to]
    return {'senderName':'Direct Emails','config':{'emailAddress':','.join(to)}}

def users(logins=[]):
    '''notification by sending email to specified users

    :param logins: login name string or list of user login names
    '''
    if type(logins) is StringType:
        logins = [logins]
    return {'senderName':'System Users','config':{'subjectId':logins}}
