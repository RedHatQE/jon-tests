'''
Created on Feb 20, 2018

@author: fbrychta
'''
import requests
import xml.etree.ElementTree as ET


rh_license_list_url = 'https://code.engineering.redhat.com/gerrit/gitweb?p=jboss-licenses.git;a=blob_plain;f=rh_license_list.json;hb=HEAD'

def test_approved_list(licenses_dir):
    errors = []
    
    r = requests.get(rh_license_list_url,verify=False)
    license_list_json = r.json()
    
    
    licenses_xml_file = licenses_dir.joinpath('licenses.xml')
    root = ET.parse(licenses_xml_file).getroot()
    
    licenses = dict()
    # get all unique license names with urls
    for dep in root.find('dependencies'):
        lic_el = dep.find('licenses').find('license') 
        licenses[lic_el.find('name').text] = lic_el.find('url').text
        
    for name,url in licenses.items():
        if name in license_list_json:
            if not license_list_json[name]['approved'] == 'yes':
                errors.append("Following license '{}' is on a licenses list but not approved!".format(name))
            if not license_list_json[name]['url'] == url:
                errors.append("Url '{}' of following license '{}' does not match url from approved list!".format(url,name))
        else:
            errors.append("Following license '{}' is not on a approved licenses list!".format(name)) 
        
        
    assert not errors, "Following errors found:\n{}".format("\n".join(errors))
    
    
def test_license_urls(licenses_dir):
    errors = []
    
    licenses_xml_file = licenses_dir.joinpath('licenses.xml')
    root = ET.parse(licenses_xml_file).getroot()
    
    for dep in root.find('dependencies'):
        lic_el = dep.find('licenses').find('license')
        url = lic_el.find('url').text
        r = requests.get(url)
        r.status_code
        if not r.status_code == 200:
            errors.append("Url '{}' of following license '{}' does not return 200 response code!".format(url,lic_el.find('name').text))
    
    assert not errors, "Following errors found:\n{}".format("\n".join(errors))
    
def test_licenses_exist(licenses_dir):
    errors = []
    
    licenses_xml_file = licenses_dir.joinpath('licenses.xml')
    root = ET.parse(licenses_xml_file).getroot()
    
    for dep in root.find('dependencies'):
        lic_el = dep.find('licenses').find('license')
        file_name = lic_el.find('name').text + '.txt'
        if not licenses_dir.joinpath(file_name).is_file():
            errors.append("License file '{}' does not exist on a file system!".format(file_name))
        
    assert not errors, "Following errors found:\n{}".format("\n".join(errors))