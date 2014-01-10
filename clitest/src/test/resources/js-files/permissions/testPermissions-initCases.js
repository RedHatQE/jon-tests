/*
 * initializes  testcases for testPermissions-*.js tests
 * @optional parameter caseName - key of case to be setup
 */

var cases = [
    {
        name: "Tests Create child resource permission",
        key: "createChild_",
        res_groups: [
            {
                name: "X",
                children: [findRHQServer()]
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "CREATE_CHILD_RESOURCES" ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ ]
            }
        ]
    },
    {
        name: "Tests Delete child resource permission (uninventory)",
        key: "deleteChild_",
        res_groups: [
            {
                name: "X",
                children: [findRHQServer()].concat(resources.find({type: "Network Interface"}))
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "DELETE_RESOURCE" ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests Manage Settings",
        key: "manageSettings_",
        roles: [
            {
                name: "YES",
                perms: [ "MANAGE_SETTINGS" ]
            },
            {
                name: "NO",
                perms: [ ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests resource write configuration permission",
        key: "resourceConfig_",
        res_groups: [
            {
                name: "X",
                children: [findRHQServer()]
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "CONFIGURE_WRITE" ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ "CONFIGURE_READ" ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for running operations on resource",
        key: "resourceOperation_",
        res_groups: [
            {
                name: "X",
                children: resources.platforms()
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "CONTROL" ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for running operations on resource",
        key: "resourceGroup_",
        res_groups: [
            {
                name: "X",
                children: resources.platforms()
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ ],
                res_groups: [ ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests manage inventory permission",
        key: "manageInventory_",
        roles: [
            {
                name: "YES",
                perms: [ "MANAGE_INVENTORY" ]
            },
            {
                name: "NO",
                perms: [ ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for managing schedules",
        key: "manageSchedules_",
        res_groups: [
            {
                name: "X",
                children: [findRHQServer()]
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "MANAGE_MEASUREMENTS" ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for managing security which implies all other permissions",
        key: "manageSecurity_",
        roles: [
            {
                name: "YES",
                perms: [ "MANAGE_SECURITY" ]
            },
            {
                name: "NO",
                perms: [ ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for viewing users",
        key: "viewUsers_",
        roles: [
            {
                name: "YES",
                perms: [ "VIEW_USERS" ]
            },
            {
                name: "NO",
                perms: [ ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    },
    {
        name: "Tests permission for editing resources",
        key: "resourcePermission_",
        res_groups: [
            {
                name: "X",
                children: [findRHQServer()]
            }
        ],
        roles: [
            {
                name: "YES",
                perms: [ "MODIFY_RESOURCE" ],
                res_groups: [ "X" ]
            },
            {
                name: "NO",
                perms: [ ],
                res_groups: [ "X" ]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: [ "NO" ]
            }
        ]
    }
    ,
    {
        name: "Tests admin user and user without permissions",
        key: "userRoles_",
        roles: [
            {
                name: "YES",
                perms: permissions.allGlobal
            }
        ],
        users: [
            {
                name: "Admin",
                roles: [ "YES" ]
            },
            {
                name: "Badguy",
                roles: []
            }
        ]
    }
    ,
    {
        name: "Tests permissions for managing drifts",
        key: "manageDrift_",
        res_groups: [
                     {
                         name: "allPlatforms",
                         children: resources.platforms()
                     }
                 ],
        roles: [
            {
                name: "YES",
                perms: ["MANAGE_DRIFT"],
                res_groups: ["allPlatforms"]
            },
            {
                name: "NO",
                perms: [],
                res_groups: ["allPlatforms"]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: ["NO"]
            }
        ]
    },
    {
        name: "Tests permissions for managing repositories",
        key: "manageRepositories_",
        res_groups: [
                     {
                         name: "allPlatforms",
                         children: resources.platforms()
                     }
                 ],
        roles: [
            {
                name: "YES",
                perms: ["MANAGE_REPOSITORIES"],
                res_groups: ["allPlatforms"]
            },
            {
                name: "NO",
                perms: [],
                res_groups: ["allPlatforms"]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: ["NO"]
            }
        ]
    },
    {
        name: "Tests permissions for managing content",
        key: "manageContent_",
        res_groups: [
                     {
                         name: "allPlatforms",
                         children: resources.platforms()
                     }
                 ],
        roles: [
            {
                name: "YES",
                perms: ["MANAGE_CONTENT"],
                res_groups: ["allPlatforms"]
            },
            {
                name: "NO",
                perms: [],
                res_groups: ["allPlatforms"]
            }
        ],
        users: [
            {
                name: "U1",
                roles: [ "YES" ]
            },
            {
                name: "U2",
                roles: ["NO"]
            }
        ]
    }
]

if (typeof caseName == "undefined") {
    cases.forEach(function (tc) {
        tearDownTestCase(tc); // first remove everything that could possibly exist from previous runs
        setupTestCase(tc); // set it up
    })
} else {
    cases.forEach(function (tc) {
        if (tc.key == caseName) {
            tearDownTestCase(tc); // first remove everything that could possibly exist from previous runs
            setupTestCase(tc); // set it up
        }
    })
}

