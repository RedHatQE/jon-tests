'''
Created on Jan 18, 2018

@author: fbrychta
'''

    
def test_required_files_exist(licenses_dir):
    licenses_html_file = licenses_dir.joinpath('licenses.html')
    licenses_xml_file = licenses_dir.joinpath('licenses.xml')
    licenses_css_file = licenses_dir.joinpath('licenses.css')
    
    errors = []
    if not licenses_html_file.is_file():
        errors.append("Required {} is not a file!".format(licenses_html_file.as_posix()))
    if not licenses_xml_file.is_file():
        errors.append("Required {} is not a file!".format(licenses_xml_file.as_posix()))
    if not licenses_css_file.is_file():        
        errors.append("Required {} is not a file!".format(licenses_css_file.as_posix()))
    assert not errors, "Missing required files:\n{}".format("\n".join(errors))