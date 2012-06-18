/**
 * commonmodule.js implemented using module pattern
 */

/**
 * this common module is instantiated by most of modules as private var
 */
var _common = function() {
	var _println = function(object) {
		if (object instanceof Array) {
			object.forEach(function(x){println(x);});
		}
		else {
			println(object);
		}
	};
	
	return {
		pageListToArray : function(pageList) {
			var resourcesArray = new Array();
		    var i = 0;
		    for(i = 0;i < pageList.size(); i++){
		    	resourcesArray[i] = pageList.get(i);
		    }
		    return resourcesArray;
		},		
		waitFor : function(conditionFunc) {
			var time = 0;
			var timeout = 3;
			while (time<timeout && !conditionFunc()) {
				sleep(10*1000);
				time++;
			}
			return conditionFunc();
		},
		info : function(message) {
			if (typeof verbose == "number" && verbose<=0) {
				_println(message);
			}
		},
		debug : function(message) {
			if (typeof verbose == "number" && verbose>=1) {
				_println(message);
			}
		},
		trace : function(message) {
			if (typeof verbose == "number" && verbose>=2) {
				_println(message);
			}
		}
	};
};

var Inventory = (function () {
	var common = new _common();
	return {
		createCriteria : function(status,resName,parentName,resTypeName, resVersion, resCategory) {
			common.trace("Inventory.createCriteria(status="+status+" resName="+resName+" parentName="+parentName+" resTypeName="+resTypeName+" resVersion="+resVersion+" resCategory="+resCategory+")");
			var criteria = new ResourceCriteria();
		    criteria.addFilterInventoryStatus(status);
		    if (resName!=null)
		    	criteria.addFilterName(resName);
		    if(parentName!=null)
		    	criteria.addFilterParentResourceName(parentName);
		    if(resTypeName != null)
		    	criteria.addFilterResourceTypeName(resTypeName);
		    if(resVersion != null)
		    	criteria.addFilterVersion(resVersion);
		    if(resCategory != null)
		    	criteria.addFilterResourceCategories(resCategory);
		    return criteria;
		},
		
		find : function(resName,parentName,resTypeName, resVersion, resCategory) {    
			common.trace("Inventory.find(resName="+resName+" parentName="+parentName+" resTypeName="+resTypeName+" resVersion="+resVersion+" resCategory="+resCategory+")");
			var criteria = Inventory.createCriteria(InventoryStatus.COMMITTED,resName,parentName,resTypeName,resVersion,resCategory);
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			common.debug("Found resources "+resources);
		    return common.pageListToArray(resources).map(function(x){return new Resource(x);});
		},
		
		platforms : function() {
			common.trace("Inventory.platforms()");
			return Inventory.find(null,null,null,null,ResourceCategory.PLATFORM);
		}
	};
}) ();

Inventory.discoveryQueue = (function () {
	
	var common = new _common();
	
	var _waitForResources = function(criteria) {
		var resources = ResourceManager.findResourcesByCriteria(criteria);
	    time = 0;
	    timeout = 3;
	    while (time<timeout && resources.size() == 0) {
	    	resources = ResourceManager.findResourcesByCriteria(criteria);
	    	sleep(10*1000);
	    	time++;
	    }
	    return resources;
	};
	
	var _importResources = function (resName,parentName,resTypeName, resVersion, resCategory){
		common.trace("discoveryQueue._importResources(resName="+resName+" parentName="+parentName+" resTypeName="+resTypeName+" resVersion="+resVersion+" resCategory="+resCategory+")");
		var criteria = Inventory.createCriteria(InventoryStatus.NEW,resName,parentName,resTypeName,resVersion,resCategory);	    
	    common.info("Waiting until desired resources become NEW");
	    var resources = _waitForResources(criteria);
	    common.debug("Found NEW resources "+resources);	    
	    var resourcesArray = common.pageListToArray(resources);
	    common.debug("Resources about to Import: " + resourcesArray);
	    assertTrue(resources.size()>0, "At least one resrouce was found");
	    DiscoveryBoss.importResources(resourcesArray.map(function(x){return x.id;}));
	    
	    // we need to wait 'till resources are committed
	    criteria = Inventory.createCriteria(InventoryStatus.COMMITTED,resName,parentName,resTypeName,resVersion,resCategory);	   
	    common.info("Waiting until resources become COMMITTED");
	    var committed = _waitForResources(criteria);
	    
	    assertTrue(committed.size() == resources.size(), "Size of COMMITED resources equals size of NEW before import");   
	    return committed;
	};
	
	return {
		list : function () {
			common.trace("discoveryQueue.list()");
			var criteria = Inventory.createCriteria(InventoryStatus.NEW,null,null,null,null,null);
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(resources);
		},
		listPlatforms : function listPlatforms() {
			common.trace("discoveryQueue.listPlatforms()");
			var criteria = Inventory.createCriteria(InventoryStatus.NEW,null,null,null,null,ResourceCategory.PLATFORM);
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(resources);
		},
		importPlatform: function(name) {
			common.trace("discoveryQueue.importPlatform(name="+name+")");
			// first lookup whether platform is already imported
			var resources = Inventory.find(name,null,null,null,ResourceCategory.PLATFORM);
			if (resources.length == 1) {
				return resources[0];
			}
			resources = _importResources(name,null,null,null,ResourceCategory.PLATFORM);
			assertTrue(resources.size() == 1, "Plaform was not imported");
			return new Resource(resources.get(0));
		},
		importResource : function(resource) {
			if (!resource.exists()) {
				DiscoveryBoss.importResources([resource.getId()]);
				waitFor(resource.exists());
			}
		}
	};
}) ();

var Resource = function (param) {
	var common = new _common();
	common.trace("new Resource("+param+")");
	if ("number" == typeof param) {		
		param = ProxyFactory.getResource(param);
	}
	
	var _id = param.id;
	var _res = param;
	
	var find = function() {
		var criteria = new ResourceCriteria();
		criteria.addFilterId(_id);
		criteria.addFilterInventoryStatus(InventoryStatus.COMMITTED);
		var resources = ResourceManager.findResourcesByCriteria(criteria);
		common.info("Resource.find: "+resources);
		return resources;
	};
	var _isAvailable = function() {
		common.trace("Resource("+_id+").isAvaialbe()");
		var found = find();
		if (found.size() != 1) {
			return false;
		}
		return found.get(0).getCurrentAvailability().getAvailabilityType() == AvailabilityType.UP;
	};
	return {
		getId : function() {return _id;},
		toString : function() {return _res.toString();},
		getProxy : function() {
			common.trace("Resource("+_id+").getProxy()");
			return ProxyFactory.getResource(_id);
		},
		remove : function() {},
		exists : function() {
			common.trace("Resource("+_id+").exists()");
			return find().size() == 1;
		},
		isAvailable : _isAvailable,
		waitForAvailable : function() {
			common.trace("Resource("+_id+").waitForAvailable()");
			return common.waitFor(_isAvailable);
		},
		uninventory : function() {
			common.trace("Resource("+_id+").uninventory()");
			ResourceManager.uninventoryResources([_id]);
			common.waitFor(function () {return find().size()==0;});
		}
	};
};


// END of commonmodule.js 

