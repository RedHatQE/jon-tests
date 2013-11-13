/**
 * Initializes bundle permission test cases based on
 * https://docs.jboss.org/author/display/RHQ/Bundle+Permissions#BundlePermissions-SupportedUseCases
 * 
 * @author lzoubek@redhat.com requires permissions/common.js
 */

var case1 = {
    name : "User Deploying His Own Bundle To Specific Resource Group. (using 1 role)",
    key : "case1_",
    res_groups : [ {
        name : "X",
        children : resources.platforms({
            type : "Linux"
        })
    }, {
        name : "Y",
        children : resources.platforms({
            type : "Linux"
        })
    } ],
    bundle_groups : [ {
        name : "A",
        children : []
    },{
        name : "B",
        children : []
    } ],
    roles : [ {
        name : "R",
        perms : [ "CREATE_BUNDLES", "VIEW_BUNDLES_IN_GROUP", "DEPLOY_BUNDLES_TO_GROUP" ],
        res_groups : [ "X" ],
        bundle_groups : [ "A" ]
    } ],
    users : [ {
        name : "U",
        roles : [ "R" ]
    } ],
}

var case1a = {
    name : "User Deploying His Own Bundle To Specific Resource Group. (using 2 roles)",
    key : "case1a_",
    res_groups : [ {
        name : "X",
        children : resources.platforms({
            type : "Linux"
        })
    } ],
    bundle_groups : [ {
        name : "A",
        children : []
    } ],
    roles : [ {
        name : "R1",
        perms : [ "CREATE_BUNDLES" ],
        res_groups : [],
        bundle_groups : [ "A" ]
    }, {
        name : "R2",
        perms : [ "DEPLOY_BUNDLES_TO_GROUP" ],
        res_groups : [ "X" ],
        bundle_groups : []
    } ],
    users : [ {
        name : "U",
        roles : [ "R1", "R2" ]
    } ],
}

var case1b = {
        name : "User Deploying His Own Bundle To Specific Resource Group. (using 2 roles)",
        key : "case1b_",
        res_groups : [ {
            name : "X",
            children : resources.platforms({
                type : "Linux"
            })
        } ],
        bundle_groups : [ ],
        roles : [ {
            name : "R2",
            perms : [ "DEPLOY_BUNDLES" ],
            res_groups : ["X"],
            bundle_groups : []
        }, {
            name : "R3",
            perms : [ "CREATE_BUNDLES","VIEW_BUNDLES" ],
            res_groups : [ "X" ],
            bundle_groups : []
        } ],
        users : [ {
            name : "U",
            roles : [ "R2", "R3" ]
        } ],
    }


var case2 = {
    name : "User Deploying Another User's Bundle To Specific Resource Group",
    key : "case2_",
    res_groups : [ {
        name : "X",
        children : resources.platforms({
            type : "Linux"
        })
    } ],
    bundle_groups : [ {
        name : "A",
        children : []
    } ],
    roles : [ {
        name : "R",
        perms : [ "DEPLOY_BUNDLES_TO_GROUP" ],
        res_groups : [ "X" ],
        bundle_groups : [ "A" ]
    } ],
    users : [ {
        name : "U",
        roles : [ "R" ]
    } ],
}

var case3 = {
    name : "Team Leader Creates Bundles, Team Members Deploy Those Bundles",
    key : "case3_",
    res_groups : [ {
        name : "X",
        children : resources.platforms({
            type : "Linux"
        })
    } ],
    bundle_groups : [ {
        name : "A",
        children : []
    } ],
    roles : [ {
        name : "R1",
        perms : [ "CREATE_BUNDLES" ],
        res_groups : [],
        bundle_groups : [ "A" ]
    }, {
        name : "R2",
        perms : [ "DEPLOY_BUNDLES_TO_GROUP" ],
        res_groups : [ "X" ],
        bundle_groups : [ "A" ]
    } ],
    users : [ {
        name : "TeamLeader",
        roles : [ "R1" ]
    }, {
        name : "Member1",
        roles : [ "R2" ]
    } ],
}

var case4 = {
        name : "Deployment Manager Gives Teams Bundles Which They Can Deploy",
        key : "case4_",
        res_groups : [ {
            name : "X",
            children : resources.platforms({
                type : "Linux"
            })
        } ],
        bundle_groups : [ {
            name : "A",
            children : []
        },{
            name : "B",
            children : []
        }  ],
        roles : [ {
            name : "R1",
            perms : [ "CREATE_BUNDLES" ],
            res_groups : [],
            bundle_groups : ["A"]
        }, {
            name : "R2",
            perms : [ "ASSIGN_BUNDLES_TO_GROUP","UNASSIGN_BUNDLES_FROM_GROUP" ],
            res_groups : [],
            bundle_groups : [ "A","B" ]
        }, {
            name : "R3",
            perms : [ "DEPLOY_BUNDLES_TO_GROUP" ],
            res_groups : [ "X" ],
            bundle_groups : [ "B" ]
        } ],
        users : [ {
            name : "TeamLeader",
            roles : [ "R1" ]
        }, {
            name : "DeployManager",
            roles : [ "R2" ]
        }, {
            name : "TeamMember1",
            roles : [ "R3" ]
        }
        ],
    }

var testCases = [ case1, case1a, case2, case3, case4 ];

println("Removing all existing bundles");
bundles.find().forEach(function(b){
    b.remove();
})

testCases.forEach(function(tc) {
    tearDownTestCase(tc); // first remove everything that could possibly exist from previous runs
    setupTestCase(tc); // set it up
})