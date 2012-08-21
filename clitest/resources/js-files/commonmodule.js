/**
 * commonmodule.js implemented using module pattern
 */


/**
 * print function that recognizes arrays and prints each item on new line
 */
var p = function(object) {
	if (object instanceof Array) {
		object.forEach(function(x){println(x);});
	}
	else {
		println(object);
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
	var _time = function() {
		var now = new Date();
		var zeros = function(number) {if (number<10) { number = "0"+number;} return number;};
		return zeros(now.getHours())+ ":"+zeros(now.getMinutes())+":"+zeros(now.getSeconds());
	};
	var _debug = function(message) {
		if (typeof verbose == "number" && verbose>=1) {
			_println(_time()+" [DEBUG] "+message);
		}
	};
	var _trace = function(message) {
		if (typeof verbose == "number" && verbose>=2) {
			_println(_time()+" [TRACE] "+message);
		}
	};
	
	var _info = function(message) {
		if (typeof verbose == "number" && verbose>=0) {
			_println(_time()+" [INFO] "+message);
		}
	};
	// taken from CLI samples/utils.js
	/**
	 * A convenience function to convert javascript hashes into RHQ's configuration
	 * objects.
	 * <p>
	 * The conversion of individual keys in the hash follows these rules:
	 * <ol>
	 * <li> if a value of a key is a javascript array, it is interpreted as PropertyList
	 * <li> if a value is a hash, it is interpreted as a PropertyMap
	 * <li> otherwise it is interpreted as a PropertySimple
	 * <li> a null or undefined value is ignored
	 * </ol>
	 * <p>
	 * Note that the conversion isn't perfect, because the hash does not contain enough
	 * information to restore the names of the list members.
	 * <p>
	 * Example: <br/>
	 * <pre><code>
	 * {
	 *   simple : "value",
	 *   list : [ "value1", "value2"],
	 *   listOfMaps : [ { k1 : "value", k2 : "value" }, { k1 : "value2", k2 : "value2" } ]
	 * }
	 * </code></pre>
	 * gets converted to a configuration object:
	 * Configuration:
	 * <ul>
	 * <li> PropertySimple(name = "simple", value = "value")
	 * <li> PropertyList(name = "list")
	 *      <ol>
	 *      <li>PropertySimple(name = "list", value = "value1")
	 *      <li>PropertySimple(name = "list", value = "value2")
	 *      </ol>
	 * <li> PropertyList(name = "listOfMaps")
	 *      <ol>
	 *      <li> PropertyMap(name = "listOfMaps")
	 *           <ul>
	 *           <li>PropertySimple(name = "k1", value = "value")
	 *           <li>PropertySimple(name = "k2", value = "value")
	 *           </ul>
	 *      <li> PropertyMap(name = "listOfMaps")
	 *           <ul>
	 *           <li>PropertySimple(name = "k1", value = "value2")
	 *           <li>PropertySimple(name = "k2", value = "value2")
	 *           </ul>
	 *      </ol>
	 * </ul>
	 * Notice that the members of the list have the same name as the list itself
	 * which generally is not the case.
	 */
	var _asConfiguration = function(hash) {

		config = new Configuration();
		if (!hash) {
			return config;
		}
		for(key in hash) {
			if (!hash.hasOwnProperty(key)) {
				continue;
			}
			value = hash[key];

			(function(parent, key, value) {
				function isArray(obj) {
					return typeof(obj) == 'object' && (obj instanceof Array);
				}

				function isHash(obj) {
					return typeof(obj) == 'object' && !(obj instanceof Array);
				}

				function isPrimitive(obj) {
					return typeof(obj) != 'object' || obj == null || (obj instanceof Boolean  || obj instanceof Number || obj instanceof String);
				}
				//this is an anonymous function, so the only way it can call itself
				//is by getting its reference via argument.callee. Let's just assign
				//a shorter name for it.
				var me = arguments.callee;

				var prop = null;

				if (isPrimitive(value)) {
					if (value==null) {
						prop = new PropertySimple(key, null);
					}
					else if (value instanceof Boolean) {
						prop = new PropertySimple(key, new java.lang.Boolean(value));
					}
					else if (value instanceof Number) {
						prop = new PropertySimple(key, new java.lang.Number(value));
					}
					else {
						prop = new PropertySimple(key, new java.lang.String(value));
					}
				} else if (isArray(value)) {
					prop = new PropertyList(key);
					for(var i = 0; i < value.length; ++i) {
						var v = value[i];
						if (v != null) {
							//me(prop, key, v);
						}
					}
				} else if (isHash(value)) {
					prop = new PropertyMap(key);
					for(var i in value) {
						var v = value[i];
						if (value != null) {
							//me(prop, i, v);
						}
					}
				}
				else {
					println("it is unkonwn");
					println(typeof value);
					println(value);
					return;
				}

				if (parent instanceof PropertyList) {
					parent.add(prop);
				} else {
					parent.put(prop);
				}
			})(config, key, value);
		}

		return config;
	};
	
	// taken from CLI samples/utils.js
	/**
	 * Opposite of <code>asConfiguration</code>. Converts an RHQ's configuration object
	 * into a javascript hash.
	 *
	 * @param configuration
	 * @param configuration definition - optional
	 */
	var _asHash = function(configuration,configDef) {
		ret = {};
		if (!configuration) {
			return ret;
		}
		iterator = configuration.getMap().values().iterator();
		while(iterator.hasNext()) {
			prop = iterator.next();
			var propDef;
			if (configDef) {
				if (configDef instanceof ConfigurationDefinition) {
					propDef = configDef.getPropertyDefinitions().get(prop.name);
				}
				else if (configDef instanceof PropertyDefinitionMap) {
					propDef = configDef.get(prop.name);
				}
			}
			(function(parent, prop) {
				function isArray(obj) {
					return typeof(obj) == 'object' && (obj instanceof Array);
				}

				function isHash(obj) {
					return typeof(obj) == 'object' && !(obj instanceof Array);
				}

				var me = arguments.callee;

				var representation = null;

				if (prop instanceof PropertySimple) {
					if (propDef && propDef instanceof PropertyDefinitionSimple) {
						// TODO implement all propertySimple types .. 
						if (propDef.getType() == PropertySimpleType.BOOLEAN) {
                            if (prop.booleanValue !=null) {
                                if (prop.booleanValue == false) {
                                	representation = false;
                                }
                                else {
                                	representation = true;
                                }
                            }
                        }
						else if (propDef.getType() == PropertySimpleType.DOUBLE
								|| propDef.getType() == PropertySimpleType.INTEGER
								|| propDef.getType() == PropertySimpleType.LONG
								|| propDef.getType() == PropertySimpleType.FLOAT								
								) {
							representation = Number(prop.doubleValue);
						} else {
							representation = String(prop.stringValue);
						}
					}
					else {
						representation = String(prop.stringValue);
					}
				} else if (prop instanceof PropertyList) {
					representation = [];

					for(var i = 0; i < prop.list.size(); ++i) {
						var child = prop.list.get(i);
						me(representation, child);
					}
				} else if (prop instanceof PropertyMap) {
					representation = {};

					var childIterator = prop.getMap().values().iterator();
					while(childIterator.hasNext()) {
						var child = childIterator.next();

						me(representation, child);
					}
				}

				if (isArray(parent)) {
					parent.push(representation);
				} else if (isHash(parent) && !prop.name.startsWith("__")) {
					parent[prop.name] = representation;
				}
			})(ret, prop);
		}
		(function(parent) {

		})(configuration);

		return ret;
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
			if (typeof timeout == "number") {
				var tout = timeout;
			}
			else {
				tout = 20;
			}
			if (typeof delay == "number") {
				var dlay = delay;
			}
			else {
				dlay = 5;
			}
			_trace("common.waitFor(func,delay="+dlay+",timeout="+tout+")");

			var result = conditionFunc();
			while (time<tout && !result) {
				_debug("Waiting "+dlay+"s");
				sleep(dlay * 1000);
				time+=dlay;
				result = conditionFunc();
			}
			if (time>=tout) {
				_debug("Timeout "+tout+"s was reached!!");
			}
			return result;
		},
		info : _info,
		debug : _debug,
		trace : _trace,
		configurationAsHash : _asHash,
		hashAsConfiguration : _asConfiguration,
		/**
		 * generic function to create any type of criteria. This function takes 'criteria' object and fills
		 * it with filter parameters given in 'param'. For param x in params call : criteria.addFilterX() will be done.
		 * There's also a shortcutFunction that can handle non-standart calls, for example
		 * function(key,value) { if (key=="status") {return "addFilterInventoryStatus.InventoryStatus"+value.toUpperCase()+")"}}
		 * so param key 'status' is processed by that returned string and this string is evaluated on the 'criteria' object
		 * 
		 * @param {Criteria} criteria - RHQ Criteria object
		 * @param {Object} params - hash of parameters
		 * @param {function} shortcutFunc - function(key,value), if returns string, it will be evaluated
		 */
		createCriteria : function(criteria,params,shortcutFunc) {
			params = params || {};
			if (!criteria) {
				throw "Criteria object must be defined!";
			}
			for (var k in params) {
			    // use hasOwnProperty to filter out keys from the Object.prototype
			    if (params.hasOwnProperty(k)) {
			    	if (shortcutFunc) {
				    	var shortcutExpr = shortcutFunc(k,params[k]);
				    	if (shortcutExpr) {
				    		// shortcut func returned something so we can eval it and skip normal processing for this property
				    		eval("criteria."+shortcutExpr);
				    		continue;
				    	}
			    	}
			        var key = k[0].toUpperCase()+k.substring(1);        
			        var func = eval("criteria.addFilter"+key);
			        if (typeof func !== "undefined") {
			        	func.call(criteria,params[k]);
			        }
			        else {
			        	var names = "";
			        	criteria.getClass().getMethods().forEach( function (m) {
			        		if (m.getName().startsWith("addFilter")) {
			        		 var name = m.getName().substring(9);
			        		 names+=name.substring(0,1).toLowerCase()+name.substring(1)+", ";
			        		}
			        	});
			        	throw "Parameter ["+k+"] is not valid filter parameter, valid filter parameters are : "+names;
			        }
			    }
			}
			return criteria;
		},
	};
};


// resource groups

/**
 * provides access to ResourceGroups
 */
var groups = (function() {
	var common = new _common();
	
	return {
		/**
		 * creates a org.rhq.domain.criteria.ResourceCriteria object based on given params
		 * @param {Obejct} params - criteria params
		 * @returns org.rhq.domain.criteria.ResourceCriteria
		 */
		createCriteria : function(params) {
			params = params || {};
			common.trace("groups.createCriteria("+common.objToString(params) +")");
			var criteria = common.createCriteria(new ResourceGroupCriteria(),params, function (key,value) {
				if (key=="category") { return "addFilterExplicitResourceCategory(ResourceCategory."+value.toUpperCase();}
			});
			return criteria;
		},
		/**
		 * finds resource groups by given params
		 * @param {Object} params
		 * @returns
		 */
		find : function(params) {
			params = params || {};
			common.trace("groups.find("+common.objToString(params)+")");
			var criteria = groups.createCriteria(params);
			var result = ResourceGroupManager.findGroupsByCriteria(criteria);
			common.debug("Found "+result.size()+" groups ");
		    return common.pageListToArray(result).map(function(x){return new ResourceGroup(x);});
		}
	};
}) ();

var ResourceGroup = function(param) {
	
};

// bundles
/**
 * provides access to Bundle subsystem
 */
var bundles = (function() {
	var common = new _common();
	var _find = function(params) {
		params = params || {};
		common.trace("bundles.find("+common.objToString(params)+")");
		var criteria = bundles.createCriteria(params);
		var result = BundleManager.findBundlesByCriteria(criteria);
		common.debug("Found "+result.size()+" budles ");
	    return common.pageListToArray(result).map(function(x){return new Bundle(x);});
	};
	return {
		createCriteria : function(params) {
			params = params || {};
			common.trace("bundles.createCriteria("+common.objToString(params) +")");
			var criteria = common.createCriteria(new BundleCriteria(),params);
			return criteria;
		},
		find : _find,
		createFromDistFile : function(dist) {
			var file = new java.io.File(dist);
			if (!file.exists()) {
				throw "file parameter does not exist!";
			}
		    var version = BundleManager.createBundleVersionViaFile(file);
		    println(version.bundle.id);
		    return new Bundle(version.bundle);
		},
		createFromRecipe : function(recipe,files) {
			// we're creating a resource with backing content
			common.debug("Reading recipe file " + recipe + " ...");
			var file = new java.io.File(recipe);
			if (!file.exists()) {
				throw "recipe parameter file does not exist!";
			}
		    var inputStream = new java.io.FileInputStream(file);
		    var fileLength = file.length();
		    // TODO read recipe to String properly!
		    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);			    
		    for (numRead=0, offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) {
			    numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 	
		    }
		    println(fileBytes);
		    var recipeStr = new String(fileBytes,0,fileLength);
		    println(recipeStr);
		    var bundleVersion = BundleManager.createBundleVersionViaRecipe(recipeStr);
		}
	};
})();

var Bundle = function(param) {

	// we define Bundle child classes as hidden types
	var Destination = function(param) {
		
	};
	
	var Version = function(param) {
		return {
			remove : function() {
				
			},
			files : function() {
				
			},
		};
	};
	
	var common = new _common();
	common.trace("new Bundle("+param+")");
	if (!param) {
		throw "either number or rhq.domain.Bundle parameter is required";
	}
	var _id = param.id;
	var _bundle = param;
	return {
		toString : function() {return _bundle.toString();},
		destinations : function(params) {
			params = params || {};
			common.trace("Bundle().destinations("+common.objToString(params)+")");
			params["bundleId"] = _id;
			var criteria = common.createCriteria(new BundleDestinationCriteria(),params, function(key,value) {
				if (key=="status"){
					return "addFilterStatus(BundleDeploymentStatus."+value.toUpperCase()+")";
				}
			});
			var result = BundleManager.findBundleDestinationsByCriteria(criteria);
			common.debug("Found "+result.size()+" destinations");
			return common.pageListToArray(result).map(function(x){return new Destination(x);});
		},
		versions : function(params) {
			params = params || {};
			common.trace("Bundle().versions("+common.objToString(params)+")");
			params["bundleId"] = _id;
			var criteria = common.createCriteria(new BundleVersionCriteria(),params);
			var result = BundleManager.findBundleVersionsByCriteria(criteria);
			common.debug("Found "+result.size()+" versions");
			return common.pageListToArray(result).map(function(x){return new Version(x);});
		},
		deploy : function(resourceGroup,params) {
			
		}
	};

};

// resources

var resources = (function () {
	var common = new _common();
	return {
		createCriteria : function(params) {
			params = params || {};
			common.trace("resources.createCriteria("+common.objToString(params) +")");
			var criteria = common.createCriteria(new ResourceCriteria(),params,function(key,value) {
				if (key=="status") {
		    		 return "addFilterInventoryStatus(InventoryStatus."+value.toUpperCase()+")";
		    	}
		    	if (key=="category") {
		    		return "addFilterResourceCategories(ResourceCategory."+value.toUpperCase()+")";
		    	}
		    	if (key=="availability") {
		    		return "addFilterCurrentAvailability(AvailabilityType."+value.toUpperCase()+")";
		    	}
			});
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
			params = params || {};
			common.trace("resources.find("+common.objToString(params)+")");
			params.status="COMMITTED";
			var criteria = resources.createCriteria(params);
			var res = ResourceManager.findResourcesByCriteria(criteria);
			common.debug("Found "+res.size()+" resources ");
		    return common.pageListToArray(res).map(function(x){return new Resource(x);});
		},
		/**
		 * 
		 * @returns array of platforms in inventory
		 */
		platforms : function(params) {
			params = params || {};			
			common.trace("resources.platforms("+common.objToString(params) +"))");
			params['category'] = "PLATFORM";
			return resources.find(params);
		}
	};
}) ();

discoveryQueue = (function () {
	
	var common = new _common();
	
	var _waitForResources = function(criteria) {
		var res = common.waitFor(function() {
			var res = ResourceManager.findResourcesByCriteria(criteria);
			if (res.size()>0) {
				return res;
			}
		});
		if (res==null) {
			return new java.util.ArrayList();
		}
		return res;
	};
	
	var _importResources = function (params){
		params = params || {};
		common.trace("discoveryQueue._importResources("+common.objToString(params)+")");
		params.status="NEW";
		var criteria = resources.createCriteria(params);	    
	    common.info("Waiting until desired resources become NEW");
	    var res = _waitForResources(criteria);
	    common.debug("Found "+res.size()+" NEW resources");	    
	    var resourcesArray = common.pageListToArray(res);
	    assertTrue(res.size()>0, "At least one resrouce was found");
	    DiscoveryBoss.importResources(resourcesArray.map(function(x){return x.id;}));
	    params.status="COMMITTED";
	    criteria = resources.createCriteria(params);	   
	    common.info("Waiting until resources become COMMITTED");
	    var committed = _waitForResources(criteria);	    
	    assertTrue(committed.size() > 0, "COMMITED resources size > 0");
	    // return only imported resources
	    return common.pageListToArray(res).map(function(x){return new Resource(x);});
	};
	
	return {
		list : function (params) {
			params = params || {};
			common.trace("discoveryQueue.list("+common.objToString(params)+")");
			params["status"] = "NEW";
			var criteria = resources.createCriteria(params);
			var res = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(res).map(function(x){return new Resource(x);});
		},
		listPlatforms : function listPlatforms(params) {
			params = params || {};
			common.trace("discoveryQueue.listPlatforms("+common.objToString(params)+")");
			params["status"] = "NEW";
			params["category"] = "PLATFORM";
			var criteria = resources.createCriteria(params);
			var res = ResourceManager.findResourcesByCriteria(criteria);
			return common.pageListToArray(res).map(function(x){return new Resource(x);});
		},
		importPlatform: function(name,children) {
			common.trace("discoveryQueue.importPlatform(name="+name+" children[default=true]="+children+")");
			
			// default is true (when null is passed)
			if(children != false){children = true;}
			
			// first lookup whether platform is already imported
			var reso = resources.find({name:name,category:"PLATFORM"});
			if (reso.length == 1) {
				common.debug("Platform "+name+" is already in inventory, not importing");
				return res[0];
			}
			res = _importResources({name:name,category:"PLATFORM"});
			assertTrue(res.length == 1, "Plaform was not imported");
			if (children) {
				common.debug("Importing platform's children");
				_importResources({parentResourceId:res[0].getId()});
			}
			common.debug("Waiting 15 seconds, 'till inventory syncrhonizes with agent");
			sleep(15*1000);
			return res[0];
		},
		importResource : function(resource,children) {
			common.trace("discoveryQueue.importResource(resource="+resource+" children[default=true]="+children+")");
			// we can accept ID as a parameter too
			if (typeof resource == "number") {
				resource = new Resource(resource);
			}	
			// default is true (when null is passed)
			if(children != false){children = true;}
			
			if (!resource.exists()) {
				DiscoveryBoss.importResources([resource.getId()]);
				common.waitFor(resource.exists);
			}
			if (children) {
				common.debug("Importing resources's children");
				_importResources({parentResourceId:resource.getId()});
			}
			return resource;
		},
		importResources : _importResources,
	};
}) ();

var Resource = function (param) {
	var common = new _common();
	common.trace("new Resource("+param+")");
	if (!param) {
		throw "either number or rhq.domain.Resource parameter is required";
	}
	if ("number" == typeof param) {		
		param = ProxyFactory.getResource(param);
	}
	
	var _id = param.id;
	var _res = param;
	
	var find = function() {
		var criteria = resources.createCriteria({id:_id});
		var res = ResourceManager.findResourcesByCriteria(criteria);
		common.debug("Resource.find: "+resources);
		return res;
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
		var criteria = resources.createCriteria({id:_id});
		criteria.fetchParentResource(true);
		var res = ResourceManager.findResourcesByCriteria(criteria);
		if (res.size()==1 && res.get(0).parentResource) {
			return new Resource(res.get(0).parentResource.id);
		}
	};

	var _waitForOperationResult = function(resourceId, resOpShedule){
		var opHistCriteria = new ResourceOperationHistoryCriteria();
		if(resOpShedule)
			opHistCriteria.addFilterJobId(resOpShedule.getJobId());
		opHistCriteria.addFilterResourceIds(resourceId);
		opHistCriteria.addSortStartTime(PageOrdering.DESC); // put most recent at top of results
		opHistCriteria.setPaging(0, 1); // only return one result, in effect the latest
		opHistCriteria.fetchResults(true);
		var pred = function() {
			var histories = OperationManager.findResourceOperationHistoriesByCriteria(opHistCriteria);
			if (histories.size() > 0) {
				if (histories.get(0).getStatus() != OperationRequestStatus.INPROGRESS) {
					return histories.get(0);
				}
				common.info("Operation in progress..");
			};
		};
		common.debug("Waiting for result..");
		var history = common.waitFor(pred);
		if (!history) {
			// timed out
			var histories = OperationManager.findResourceOperationHistoriesByCriteria(opHistCriteria);
			if (histories.size() > 0) {
				history = histories.get(0);
			}
			else {
				throw "ERROR Cannot get operation history result remote API ERROR?";
			}
		}
		common.debug("Operation finished with status : "+history.status);
		return history;
	};
	var _checkRequiredConfigurationParams = function(configDef,params) {
		if (!configDef) {
			return;
		}
		params = params || {};
		// check whether required params are defined
		var iter = configDef.getPropertyDefinitions().values().iterator();
		while(iter.hasNext()) {
			var propDef = iter.next();
			if (propDef.isRequired() && !params[propDef.name]) {
				throw "Property ["+propDef.name+"] is required";
			}
		}
	};
	
	/**
	 * applies map of values to given configuration
	 * @param original - Configuration instance
	 * @param definition - ConfigurationDefintion
	 * @param values - map of values to be applied to configuration
	 * 
	 * @return original Configuration object with applied values
	 */
	var _applyConfiguration = function(original,definition,values) {
		values = values || {};
		for (var k in values) {
			// we only iterrate over values
			if (values.hasOwnProperty(k)) {		
				// parent - parent configuration
				// definition - parent configuration definition
				// key - config key to be applied
				// value - value to be applied
				(function (parent,definition,key,value) {
					var propDef = null;
					var prop = null;
					// decide which type of property are we working with
					if (definition instanceof PropertyDefinitionMap) {
						//println("DEF is map");
						propDef = definition.get(key);
					} else if (definition instanceof PropertyDefinitionList) {
						//println("DEF is list");
						propDef = definition.getMemberDefinition();
					} else if (definition instanceof ConfigurationDefinition) {
						//println("DEF is config");
						propDef = definition.getPropertyDefinitions().get(key);
					}

					if (propDef==null) {
						common.debug("Unable to get PropertyDefinition for key="+key);
						return;
					}
					// process all 3 possible types
					if (propDef instanceof PropertyDefinitionSimple) {
						prop = new PropertySimple(key, null);
						
						if (value!=null) {
							prop = new PropertySimple(key, new java.lang.String(value));
						}
							//println("it's simple! "+prop);
					} else if (propDef instanceof PropertyDefinitionList) {
						prop = new PropertyList(key);
						//println("it's list! "+prop);
						for(var i = 0; i < value.length; ++i) {
							arguments.callee(prop,propDef,"",value[i]);
						}
					} else if (propDef instanceof PropertyDefinitionMap) {
						prop = new PropertyMap(propDef.name);
						//println("it's map! "+prop);
						for (var i in value) {
							if (value.hasOwnProperty(i)) {
								arguments.callee(prop,propDef,i,value[i]);
							}
						}							
					}
					else {
						common.info("Unkonwn property definition! this is a bug");
						pretty.print(propDef);
						return
					}
					// now we update our Configuration node						
					if (parent instanceof PropertyList) {
						parent.add(prop);
					} else {
						parent.put(prop);
					}
				}) (original,definition,k,values[k]);
			}
		}
		return original;
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
				common.pageListToArray(histories).forEach(
						function (x) {
							if (x.id==history.id) {
								if (x.status != DeleteResourceStatus.IN_PROGRESS) {
									current = x;
									return;
								}
								common.info("Waiting for resource to be removed");
							}
						}
				);
				return current;
			};
			var result = common.waitFor(pred);
			if (result) {
				common.debug("Resource deletion finished with status : "+result.status);
			}
			if (result && result.status == DeleteResourceStatus.SUCCESS) {
				return true;
			}
			if (!result) {
				common.info("Resource deletion still in progress, giving up...");
				return false;
			}
			common.debug("Resource deletion failed, reason : "+result.errorMessage);
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
			return resources.find(params);
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
			var children = resources.find(params);
			if (children.length>0) {
				return children[0];
			}
		},
		/**
		 * updates configuration of this resource. You can either pass whole configuration (retrieved by getConfiguration())
		 * or only params that needs to be changed
		 * @param {Object} params - new configuration parameters, partial configuration is supported
		 * @returns
		 */
		updateConfiguration : function(params) {
			common.trace("Resource("+_id+").updateConfiguration("+common.objToString(params)+")");
			params = params || {};
			common.debug("Retrieving configuration and configuration definition");
			var self = ProxyFactory.getResource(_id);
			var config = ConfigurationManager.getLiveResourceConfiguration(_id,false);
			common.debug("Got configuration : "+config);
			var configDef = ConfigurationManager.getResourceConfigurationDefinitionForResourceType(self.resourceType.id);
			var applied = _applyConfiguration(config,configDef,params);
			common.debug("Will apply this configuration: "+applied);
			
			var update = ConfigurationManager.updateResourceConfiguration(_id,applied);
			if (!update) {
				common.debug("Configuration has not been changed");
				return;
			}
			if (update.status == ConfigurationUpdateStatus.INPROGRESS) {
				var pred = function() {
					var up = ConfigurationManager.getLatestResourceConfigurationUpdate(_id);
					if (up) {
						return up.status != ConfigurationUpdateStatus.INPROGRESS;
					}
				};
				common.debug("Waiting for configuration to be updated...");
				var result = common.waitFor(pred);
				if (!result) {
					throw "Resource configuration update timed out!";
				}
				update = ConfigurationManager.getLatestResourceConfigurationUpdate(_id);
			}					
			common.debug("Configuration update finished with status : "+update.status);
			if (update.status == ConfigurationUpdateStatus.FAILURE) {
				common.info("Resource configuration update failed : "+update.errorMessage);
			}
		},
		/**
		 * retrieves configuration of this resource
		 * @returns
		 */
		getConfiguration : function() {
			common.trace("Resource("+_id+").getConfiguration()");
			var self = ProxyFactory.getResource(_id);
			var configDef = ConfigurationManager.getResourceConfigurationDefinitionForResourceType(self.resourceType.id);
			return common.configurationAsHash(ConfigurationManager.getLiveResourceConfiguration(_id,false),configDef);
		},
		/**
		 * retrieves plugin configuration for this resource
		 * @returns
		 */
		getPluginConfiguration : function() {
			common.trace("Resource("+_id+").getPluginConfiguration()");
			var self = ProxyFactory.getResource(_id);
			var configDef = ConfigurationManager.getPluginConfigurationDefinitionForResourceType(self.resourceType.id);
			return common.configurationAsHash(ConfigurationManager.getPluginConfiguration(_id),configDef);
		},
		/**
		 * creates a new child resource
		 * @param {Object} params hashmap of params
		 * @returns new resource if it was successfully created and discovered, null otherwise
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
			var configuration =  new Configuration();
		    if (config) {
		    	configuration = common.hashAsConfiguration(config);
		    }
		    else {
		    	// we should obtain default/empty configuration
		    	var template = resType.resourceConfigurationDefinition.defaultTemplate;
				if (template) {
					configuration = template.createConfiguration();
				}
		    }
			common.debug("Creating new ["+type+"] resource called [" + name+"]");
			if (content) {
				// we're creating a resource with backing content
				common.debug("Reading file " + content + " ...");
				var file = new java.io.File(content);
				if (!file.exists()) {
					throw "content parameter file does not exist!";
				}
			    var inputStream = new java.io.FileInputStream(file);
			    var fileLength = file.length();
			    var fileBytes = java.lang.reflect.Array.newInstance(java.lang.Byte.TYPE, fileLength);			    
			    for (numRead=0, offset=0; ((numRead >= 0) && (offset < fileBytes.length)); offset += numRead ) {
				    numRead = inputStream.read(fileBytes, offset, fileBytes.length - offset); 	
			    }
			    			    
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
				var plugConfiguration = new Configuration();
				var pluginTemplate = resType.pluginConfigurationDefinition.defaultTemplate;
				if (pluginTemplate) {
					plugConfiguration = pluginTemplate.configuration;
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
				common.pageListToArray(histories).forEach(
						function (x) {
							if (history && x.id==history.id) {
								if (x.status != CreateResourceStatus.IN_PROGRESS) {
									current = x;
									return;
								}
								common.info("Waiting for resource creation..");
							}
						}
				);
				return current;
			};
			common.debug("Waiting for resrouce creation operation...");
			var result = common.waitFor(pred);
			if (result) {
				common.debug("Child resource creation status : " + result.status);
			}
			else {
				common.info("Child resource creation timed out!!");
				return;
			}
			if (result && result.status == CreateResourceStatus.SUCCESS) {
				common.debug("Waiting for resource to be auto-discovered");
				// we assume there can be exactly one resource of one type having unique name
				var discovered = common.waitFor(function() {return resources.find({parentResourceId:_id,resourceTypeId:resType.id,resourceKey:result.newResourceKey}).length==1;});
				if (!discovered) {
					common.info("Resource child was successfully created, but it's autodiscovery timed out!");
					return;
				}
				return resources.find({parentResourceId:_id,resourceTypeId:resType.id,resourceKey:result.newResourceKey})[0];
			}
			common.debug("Resource creation failed, reason : "+result.errorMessage);
			return;
			
		},
		operations : function() {
			
		},
		/**
		 * invokes operation on resource, operation status is polled 'till timeout is reached or operation finishes
		 * @param {String} name of operation
		 * @param {Object} params - hashmap for operation params
		 * @returns
		 */
		invokeOperation : function(name,params) {
			common.trace("Resource("+_id+").invokeOperation(name="+name+",params={"+common.objToString(params)+"})");
			// let's obtain operation definitions, so we can check operation name and required params
			var criteria = new ResourceTypeCriteria();
			criteria.addFilterId(find().get(0).resourceType.id);
			criteria.fetchOperationDefinitions(true);
			var resType = ResourceTypeManager.findResourceTypesByCriteria(criteria).get(0);
			var iter = resType.operationDefinitions.iterator();
			// we put op names here in case invalid name is called
			var ops="";
			while(iter.hasNext()) {
				var op = iter.next();
				ops+=op.name+", ";
				if (name==op.name) {
					var configuration = null;
					if (params || params == {}) {
						configuration = common.hashAsConfiguration(params);
					}
					else if (op.parametersConfigurationDefinition){
						var template = op.parametersConfigurationDefinition.defaultTemplate;
						common.trace("Default template for parameters configuration definition" + template);
						if (template) {
							configuration = template.createConfiguration();
						}
					}
					//if (configuration)
					//	pretty.print(configuration);
					// 	println(common.objToString(common.configurationAsHash(configuration)));
					
					_checkRequiredConfigurationParams(op.parametersConfigurationDefinition,common.configurationAsHash(configuration));
					
					var resOpShedule = OperationManager.scheduleResourceOperation(_id,name,0,0,0,0,configuration,null);
					common.debug("Operation scheduled..");
					return _waitForOperationResult(_id,resOpShedule);
				}
			}
			throw "Operation name ["+name+"] is invalid for this resource, valid operation names are : " + ops;
		},
		/**
		 * Waits until operation is finished or timeout is reached. 
		 * @param resourceId
		 * @param resOpShedule may be null, than the most recent job for given resourceId is picked
		 * @returns operation history
		 */
		waitForOperationResult : _waitForOperationResult,
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
			return common.waitFor(function() { 
				if (!_isAvailable()) {
					common.info("Waiting for resource availability=UP");
				} else { return true; }
			});
		},
		/**
		 * unimports resource
		 * @returns true if resource is dos no longer exist in inventory, false otherwise
		 */
		uninventory : function() {
			common.trace("Resource("+_id+").uninventory()");
			ResourceManager.uninventoryResources([_id]);
			var result = common.waitFor(function () {				
					if (find().size()>0) {
						common.debug("Waiting for resource to be removed from inventory");
						return false;
					}
					else {
						return true;
					}
				}
			);
			common.debug("Waiting 5s for sync..");
			sleep(5*1000);
			return result;
		}
	};
};


var Inventory = resources;
Inventory.discoveryQueue = discoveryQueue;

// default verbosity,timeouts

var verbose = 0; // 0 INFO,1 DEBUG,>=2 TRACE
// poll interval for any waiting
var delay = 5; // poll interval for any waiting in seconds
var timeout = 120; // total timeout of any waiting in seconds
// END of commonmodule.js 

