/**
 * commonmodule.js implemented using module pattern
 */

// this is nice construct
Array.prototype.each = function(callback){
    for (var i =  0; i < this.length; i++){
        callback(this[i]);
    }
};


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
		/**
		 * @param conditionFunc - predicate
		 * waits until conditionFunc does return any defined value except for false 
		 */
		waitFor : function(conditionFunc) {
			var time = 0;
			var timeout = 3;
			var result = conditionFunc();
			while (time<timeout && !result) {
				sleep(10*1000);
				time++;
				result = conditionFunc();
			}
			return result;
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
		common.debug("Resource.find: "+resources);
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
	var _parent = function() {
		var criteria = Inventory.createCriteria({id:_id});
		criteria.fetchParentResource(true);
		var resources = ResourceManager.findResourcesByCriteria(criteria);
		if (resources.size()==1) {
			return new Resource(resources.get(0).parentResource.id);
		}
	};
	return {
		getId : function() {return _id;},
		toString : function() {return _res.toString();},
		getProxy : function() {
			common.trace("Resource("+_id+").getProxy()");
			return ProxyFactory.getResource(_id);
		},
		parent : function() {
			common.trace("Resource("+_id+").parent()");
			return _parent();
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
			var parent = _parent();
			if (!parent) {
				throw "Resource cannot be deleted without having parent"; 
			}
			var startTime = new Date().getTime();
			var parentId = parent.getId();
			try {
				var history = ResourceFactoryManager.deleteResource(_id);
			}
			catch (exc) {
				common.info("Resource was not deleted :"+exc);
				return false;
			}
			var pageControl = new PageControl(0,1);
			var pred = function() {
				var histories = ResourceFactoryManager.findDeleteChildResourceHistory(parentId,startTime,new Date().getTime(),pageControl);
				var current;
				common.pageListToArray(histories).each(
						function (x) {
							if (x.id==history.id && x.status != DeleteResourceStatus.IN_PROGRESS) {
								current = x;
							}
						}
				);
				return current;
			};
			var result = common.waitFor(pred);
			if (result && result.status == DeleteResourceStatus.SUCCESS) {
				return true;
			}
			return false;
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
		/**
		 * creates a new child resource
		 * @param params
		 * @returns new resource if it was successfully created and discovered, false otherwise
		 */
		createChild : function(params) {
			common.trace("Resource("+_id+").createChild("+common.objToString(params)+")");
			params = params || {};
			if (!params.name && !params.content) {
				throw "Either [name] or [content] parameters must be defined";
			}
			if (!params.type) {
				throw "[type] parameter MUST be specified, how could I guess what type of resource are you creating?";
			}
			// TODO check for existing resource!
			var name=params.name;
			if (!params.name) {
				name = new java.io.File(params.content).getName();
			}
			// bind input params
			var type = params.type;
			var config = params.config;
			var version = params.version || null;
			var content = params.content;
			// these 2 are used for querying resource history
			var startTime = new Date().getTime();
			var pageControl = new PageControl(0,1);
			// we need to obtain resourceTypeId, to get it, we need plugin, where the resource type
			// is defined .. we'll get this plugin from parent (this) resource
			var resType = ResourceTypeManager.getResourceTypeByNameAndPlugin(type, find().get(0).resourceType.plugin); 
			if (!resType) {
				throw "Invalid resource type [type="+type+"]";
			}
			// we need to re-request resource type so it contains configuration definition too
			var criteria = new ResourceTypeCriteria();
			criteria.addFilterId(resType.id);
			criteria.fetchResourceConfigurationDefinition(true);
			criteria.fetchPluginConfigurationDefinition(true); 
			resType = ResourceTypeManager.findResourceTypesByCriteria(criteria).get(0);
			
			common.debug("Creating new ["+type+"] resource called [" + name+"]");
			if (content) {
				// we're creating a resource with backing content
				common.debug("Reading file " + content + " ...");
				var file = new java.io.File(content);
			    var inputStream = new java.io.FileInputStream(file);
			    var fileLength = file.length();
			    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);			    
			    for (numRead=0, offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) {
				    numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 	
			    }
			    var configuration = config || new Configuration();
				history = ResourceFactoryManager.createPackageBackedResource(
					_id, 
					resType.id,
					name, // new resource name
					null, // pluginConfiguration
					name, 
					version, // packageVersion
					null, // architectureId
					configuration, // resourceConfiguration 
					fileBytes, // content
					null // timeout
				);
			}
			else {
				var configuration = new Configuration();
				var template = resType.resourceConfigurationDefinition.defaultTemplate;
				if (template) {
					configuration = template.createConfiguration();
				}
				
				var plugConfiguration = new Configuration();
				var pluginTemplate = resType.pluginConfigurationDefinition.defaultTemplate;
				if (pluginTemplate) {
					plugConfiguration = template.configuration;
				}
				var history = ResourceFactoryManager.createResource(
					_id, 
					resType.id,
					name, // new resource name
					plugConfiguration, // pluginConfiguration
					configuration, // resourceConfiguration
					null  // timeout
				);
			}
			var pred = function() {
				var histories = ResourceFactoryManager.findCreateChildResourceHistory(_id,startTime,new Date().getTime(),pageControl);
				var current;
				common.pageListToArray(histories).each(
						function (x) {
							if (history && x.id==history.id && x.status != CreateResourceStatus.IN_PROGRESS) {
								current = x;
							}
						}
				);
				return current;
			};
			common.debug("Waiting for resrouce creation operation...");
			var result = common.waitFor(pred);
			common.debug("Child resource creation status : " + result.status);
			if (result && result.status == CreateResourceStatus.SUCCESS) {
				common.debug("Waiting for resource to be auto-discovered");
				// we assume there can be exactly one resource of one type having unique name
				var discovered = common.waitFor(function() {return Inventory.find({parentResourceId:_id,resourceTypeId:resType.id,resourceKey:result.newResourceKey}).length==1;});
				if (!discovered) {
					common.info("Resource child was successfully created, but it's autodiscovery timed out!");
					return false;
				}
				return Inventory.find({parentResourceId:_id,resourceTypeId:resType.id,resourceKey:result.newResourceKey})[0];
			}
			common.debug("Resource creation failed, reason : "+result.errorMessage);
			return false;
			
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
				};
			};
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

