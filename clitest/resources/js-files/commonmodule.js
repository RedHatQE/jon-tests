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
		objToString : function(obj) {
			var str=""; 
			for (var k in obj) {if (obj.hasOwnProperty(k)) str=str.concat(k+"="+obj[k]+",");}
			return str.substring(0,str.length-1);
		},
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
				_println("[INFO] "+message);
			}
		},
		debug : function(message) {
			if (typeof verbose == "number" && verbose>=1) {
				_println("[DEBUG] "+message);
			}
		},
		trace : function(message) {
			if (typeof verbose == "number" && verbose>=2) {
				_println("[TRACE] "+message);
			}
		}
	};
};


var Inventory = (function () {
	var common = new _common();
	return {
		createCriteria : function(params) {
			common.trace("Inventory.createCriteria("+common.objToString(params) +")");
			var criteria = new ResourceCriteria();	
			for (var k in params) {
			    // use hasOwnProperty to filter out keys from the Object.prototype
			    if (params.hasOwnProperty(k)) {
			    	if (k=="status") {
			    		 eval("criteria.addFilterInventoryStatus(InventoryStatus."+params[k]+")");
			    		 continue;
			    	}
			    	if (k=="category") {
			    		eval("criteria.addFilterResourceCategories(ResourceCategory."+params[k]+")");
			    		 continue;
			    	}
			    	if (k=="availability") {
			    		eval("criteria.addFilterCurrentAvailability(AvailabilityType."+params[k]+")");
			    		continue;
			    	}
			        var key = k[0].toUpperCase()+k.substring(1);        
			        var func = eval("criteria.addFilter"+key);
			        if (typeof func !== "undefined") {
			        	func.call(criteria,params[k]);
			        }
			        else {
			        	throw "Parameter ["+k+"] is not valid filter parameter";
			        }
			    }
			}
			// by default only 200 items are returned, this line discards it .. so we get unlimited list
			criteria.clearPaging();
			return criteria;
		},
		/**
		 * finds resources in inventory
		 * @param params
		 * @returns array of resources
		 */
		find : function(params) {    
			common.trace("Inventory.find("+common.objToString(params)+")");
			params.status="COMMITTED";
			var criteria = Inventory.createCriteria(params);
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			common.debug("Found "+resources.size()+" resources ");
		    return common.pageListToArray(resources).map(function(x){return new Resource(x);});
		},
		/**
		 * 
		 * @returns array of platforms in inventory
		 */
		platforms : function() {
			common.trace("Inventory.platforms()");
			return Inventory.find({category:"PLATFORM"});
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
	
	var _importResources = function (json){
		common.trace("discoveryQueue._importResources("+common.objToString(criteria)+")");
		json.status="NEW";
		var criteria = Inventory.createCriteria(json);	    
	    common.info("Waiting until desired resources become NEW");
	    var resources = _waitForResources(criteria);
	    common.debug("Found "+resources.size()+" NEW resources");	    
	    var resourcesArray = common.pageListToArray(resources);
	    assertTrue(resources.size()>0, "At least one resrouce was found");
	    DiscoveryBoss.importResources(resourcesArray.map(function(x){return x.id;}));
	    json.status="COMMITTED";
	    criteria = Inventory.createCriteria(json);	   
	    common.info("Waiting until resources become COMMITTED");
	    var committed = _waitForResources(criteria);	    
	    assertTrue(committed.size() > 0, "COMMITED resources size > 0");   
	    return common.pageListToArray(committed).map(function(x){return new Resource(x);});
	};
	
	return {
		list : function () {
			common.trace("discoveryQueue.list()");
			var criteria = Inventory.createCriteria({status:"NEW"});
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(resources);
		},
		listPlatforms : function listPlatforms() {
			common.trace("discoveryQueue.listPlatforms()");
			var criteria = Inventory.createCriteria({status:"NEW",category:"PLATFORM"});
			var resources = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(resources);
		},
		importPlatform: function(name,children) {
			common.trace("discoveryQueue.importPlatform(name="+name+" children[default=true]="+children+")");
			children = children || true;
			// first lookup whether platform is already imported
			var resources = Inventory.find({name:name,category:"PLATFORM"});
			if (resources.length == 1) {
				return resources[0];
			}
			resources = _importResources({name:name,category:"PLATFORM"});
			assertTrue(resources.length == 1, "Plaform was not imported");
			if (children) {
				_importResources({parentResourceId:resources[0].getId()});
			}
			return resources[0];
		},
		importResource : function(resource) {
			if (!resource.exists()) {
				DiscoveryBoss.importResources([resource.getId()]);
				waitFor(resource.exists);
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
		var criteria = Inventory.createCriteria({id:_id});
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
	var _exists = function() {
		return find().size() == 1;
	};
	return {
		getId : function() {return _id;},
		toString : function() {return _res.toString();},
		getProxy : function() {
			common.trace("Resource("+_id+").getProxy()");
			return ProxyFactory.getResource(_id);
		},
		/**
		 * removes/deletes this resource from inventory. 
		 * @returns true if resource no longer exists in inventory, false otherwise
		 */
		remove : function() {
			common.trace("Resource("+_id+").remove()");
			if (!_exists()) {
				common.debug("Resource does not exists, nothing to remove");
				return false;
			}
			try {
				ResourceFactoryManager.deleteResource(_id);
			} catch (exc) {
				common.info("Cannot delete resource: "+exc);
				return false;
			}
			return !common.waitFor(_exists);
		},
		/**
		 * 
		 * @param {Object} params
		 * @returns array of child resources
		 */
		children : function(params) {
			common.trace("Resource("+_id+").children("+common.objToString(params)+")");
			params = params || {};
			params.parentResourceId=_id;
			return Inventory.find(params);
		},
		/**
		 * 
		 * @param {Object} params hashmap of filter params
		 * @returns first matching child resource found
		 */
		child : function(params) {
			common.trace("Resource("+_id+").child("+common.objToString(params)+")");
			params = params || {};
			params.parentResourceId=_id;
			var children = Inventory.find(params);
			if (children.length>0) {
				return children[0];
			}
		},
		createChild : function() {
			
		},
		operations : function() {
			
		},
		invokeOperation : function(name,params) {
			common.trace("Resource("+_id+").invokeOperation(name="+name+",params={"+common.objToString(params)+"})");
			var resOpShedule = OperationManager.scheduleResourceOperation(_id,name,0,0,0,0,null,null);
			var opHistCriteria = new ResourceOperationHistoryCriteria();
			opHistCriteria.addFilterJobId(resOpShedule.getJobId());
			opHistCriteria.addFilterResourceIds(_id);
			opHistCriteria.addSortStartTime(PageOrdering.DESC); // put most recent at top of results
			opHistCriteria.setPaging(0, 1); // only return one result, in effect the latest
			opHistCriteria.fetchResults(true);
			var pred = function() {
				var histories = OperationManager.findResourceOperationHistoriesByCriteria(opHistCriteria);
				if (histories.size() > 0 && histories.get(0).getStatus() != OperationRequestStatus.INPROGRESS) {
					return history.get(0);
				}
			}
			var history = common.waitFor(pred);
			return history;
		},
		/**
		 * checks whether resource exists in inventory
		 * @returns bool
		 */
		exists : function() {
			common.trace("Resource("+_id+").exists()");
			return _exists();
		},
		isAvailable : _isAvailable,
		/**
		 * wait's until resource becomes UP or timeout is reached
		 * @returns true if became is available, false otherwise
		 */
		waitForAvailable : function() {
			common.trace("Resource("+_id+").waitForAvailable()");
			return common.waitFor(_isAvailable);
		},
		/**
		 * unimports resource
		 * @returns true if resource is dos no longer exist in inventory, false otherwise
		 */
		uninventory : function() {
			common.trace("Resource("+_id+").uninventory()");
			ResourceManager.uninventoryResources([_id]);
			common.waitFor(function () {return find().size()==0;});
		}
	};
};


// END of commonmodule.js 

