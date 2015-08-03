#!/usr/bin/env python
# Script search from root URL links that points to broken URLs (403,404,... not 200) in range specified by URL Regular Expression
# Please fix any bugs you find, but new functionalities add to separate script with new version and help description.

__author__ = "vprusa"
__date__ = "$Jul 30, 2015 10:29:46 AM$"


import pprint
import urllib2
import re
from urlparse import urlparse
import getopt, sys
import signal 

def signal_handler(signal, frame):
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

url = 'http://documentation-devel.engineering.redhat.com/site/documentation/en-US/Red_Hat_JBoss_Operations_Network/3.3/index.html'
urlRegExp = "documentation-devel\.engineering\.redhat\.com.*Red_Hat_JBoss_Operations_Network/3\.3/"


parsed_uri = urlparse(url)
domain = '{uri.scheme}://{uri.netloc}/'.format(uri=parsed_uri)
pageRegexp = "";
visitedPages = [];
badPages = [];
outfile = None
debug = False
help = False

	
def getPage(url):
    try:
        _p(url)
        response = urllib2.urlopen(url)
        html = response.read()
        code = response.getcode()
        finalURL = response.geturl()
        response.close()
    except urllib2.HTTPError, e:
        html = 'urllib2.HTTPError'
        code = e.getcode()
        return {'status': code, 'response': html, 'finalURL':None};
    except urllib2.URLError, e:
        html = ''
        code = 'urllib2.URLError';
        return {'status': code, 'response': html, 'finalURL':None};
    except:
        html = ''
        code = 'UNKNOWN ERROR';
        return {'status': code, 'response': html, 'finalURL':None};
    return {'status': code, 'response': html, 'finalURL':finalURL};

def getIndicesOf(searchStr, str, caseSensitive):
    startIndex = 0
    searchStrLen = len(searchStr)
    indices = []
    index = 0
    if (caseSensitive == False):
        str = str.lower()
        searchStr = searchStr.lower()

    while True:
        if searchStr not in str[startIndex:]:
            return indices;
        index = str.index(searchStr, startIndex)
        indices.append(index)
        startIndex = index + searchStrLen;
    return indices;

def getPageUrls(page):
    indexes = getIndicesOf("href=", page, True)
    urls = []
    for index in indexes:
        url = page[index + 6: page.index('"', index + 7)]
        urls.append(url)
           
    return urls;

# any check of url before loading links from it
def isUrlValid(url):
    if(re.search(urlRegExp, url) is None):
        return False
    if(domain not in url):
        return False
    return True;

def appendBadPages(append):
    if(outfile is not None):
        with open(outfile, "a") as myfile:
            myfile.write(pprint.pformat(append, indent=1) + '\n')
    badPages.append(append)
    return

def checkUrl(url, validate=True, parentUrl=None):
    if(visited(url)):
        return;

    if(".css" in url[-4:]):
        return;
    #find all urls on page status, response
    res = getPage(url);
    if(parentUrl is not None):
        _p("ParentUrl: " + parentUrl)
    visited(url, True);
    # check if OK
    if (res['status'] != 200):
        _p("BadPage: " + url);
        appendBadPages({'status': res['status'], 'url': url, 'parenUrl':parentUrl})
        return {'status': res['status'], 'url': url, 'type': 'bad'}

    # check if outer source
    if (isUrlValid(url) == False and validate == True):
        _p ("OuterUrl: " + url)
        return {'status': res['status'], 'url': url, 'type':'outer'}
    
    _p("URL OK: " + url);

    # get URLs from page and recursive check
    urls = getPageUrls(res['response']);
    result = [];
      
    for urlNew in urls:
        if(not bool(urlparse(urlNew).netloc)):
            if('#' in urlNew or 'mailto' in urlNew[0:7]):
                continue
            if('/' not in urlNew[0:1]):
                urlNew = url[0:url.rfind("/") + 1] + urlNew
            else:
                urlNew = domain + urlNew
        if (res['finalURL'] is None):
            resApp = checkUrl(urlNew, True, url)
        else:
            resApp = checkUrl(urlNew, True, res['finalURL'])
        result.append(resApp);
        
    return {'url': url, 'result': result}

def visited(page, push=None):
    if (push == None):
        if (page not in visitedPages or visitedPages.index(page) == -1):
            return False;
        return True;
    else:
    # add page to visited
        visitedPages.append(page)

def _p(v):
    if(debug == True):
        pprint.pprint(v)

def help():
    print(" Check Url v1.0")
    print("     HELP:   [--help|-h]")
    print("             Print this (optional & default)")
    print("     URL:    [--url=|-u] http://example.com/l1/language/dsa-a.html")
    print("             Root URL to begin recursive searching")
    print('     REGEXP: [--regexp=|-r] "http*\.example\.com/l1.*/-a.*" ')
    print("             Regular expression of searched pages")
    print("             ! MAKE SURE THE REGEXP IS CORRECT !")
    print("     OUTPUT FILE:")
    print("             [--output=|-o] outfile.txt")
    print("             Output file (optional)")
    print("     DEBUG:")
    print("             [-debug|-d]")
    print("             Printing all to screen (optional)")
    print("")
    print("PARSED ARGS:")
    pprint.pprint(opts)
    sys.exit(1)

try:
    opts, args = getopt.getopt(sys.argv[1:], "hdu:r:o:", ["help", "debug", "url=", "regexp=", "output="])
except getopt.GetoptError as err:
    help()
    sys.exit(2)

if len(opts) == 0:
    o = '-h'
    
for o, a in opts:
    if o in ("-h", "--help"):
        help()
    elif o in ("-d", "--debug"):
        debug = True
    elif o in ("-u", "--url"):
        url = a
        _p("    URL: " + a)
    elif o in ("-r", "--regexp"):
        urlRegExp = a   
        _p("    REGEXP: " + a)
    elif o in ("-o", "--output"):
        outfile = a
    else:
        help()

_p(checkUrl(url, False))