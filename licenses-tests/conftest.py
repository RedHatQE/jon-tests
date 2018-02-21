'''
Created on Jan 18, 2018

@author: fbrychta
'''
import pytest
from pathlib import Path


def pytest_addoption(parser):
    parser.addoption("--licenses_dir", action="store", default="/tmp", help="Full path to licenses directory")

@pytest.fixture(scope="session", autouse=True)
def licenses_dir(request):
    licenses_dir_full = request.config.getoption("--licenses_dir")
    licenses_home = Path(licenses_dir_full)
    if not licenses_home.is_dir():
        raise NotADirectoryError('Provided path for JON licenses home is not a directory: ' + licenses_dir_full)
    return licenses_home