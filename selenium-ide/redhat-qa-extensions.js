/* A generic method for getting an element in a table based on the text in the
 * adjacent cell.
 */
LocatorBuilders.prototype.getLocatorByAssocText = function(node, xpathPredicate){

    var parent = node.parentNode;
	while (parent && parent.nodeName != "TD"){
		if (parent.nodeName == "TABLE") return null;  //if we hit a table before a td on the way up, we can't use this locator
		parent = parent.parentNode;	
	}
	if (!parent) return null;
	var td = parent;
	this.log.debug("Found td type=" + td.nodeName);
	var tr = td.parentNode;
	this.log.debug("Found tr type=" + tr.nodeName);
 
 		var assocTexts = [];
 		for (i=0; i < tr.childNodes.length; i++){
		var thisNode = tr.childNodes[i];
		if ( thisNode.nodeName == "TD") {
			var textnode = thisNode.textContent;
			
			if (!textnode.match(/^\s*$/)){
				this.log.debug("Found associated text: " + textnode + ", in node " + thisNode.nodeName);
				
				assocTexts.push(textnode);
				
			}
		}
	}
	this.log.debug("Found " + assocTexts.length + " choices");
	var choice = null;
	if (assocTexts.length == 0) return null;
	if (assocTexts.length == 1) choice = 0;
	else {
		//prompt the user
		this.log.debug("Prompting the user");
		choice = this.prompt(assocTexts);
		
	}
	var finalText = assocTexts[choice].replace(/^\s+|\s+$/g, "");
	return "//tr[td[starts-with(normalize-space(.),'" + finalText +  "')]]" + xpathPredicate; 
};

LocatorBuilders.prototype.prompt = function(array){
	var alertText = "Which is the most related to the control you just clicked?\n";
	for (j=0;j< array.length; j++) {
		var thistext = array[j].replace(/^\s+|\s+$/g,"");
		alertText = alertText + (j+1) + ". " + thistext + "\n";
	}
	var choice = prompt(alertText, "Enter number here");
	return parseInt(choice) -1;
};

/* add non-braindead locator for images.  the default one would include the entire src URL including hostname.  This one just
 * uses the URI.
 */
LocatorBuilders.add('xpath:img', function(e) {
 	if (e.nodeName == 'IMG') {
		if (e.alt != '') {
			return "//img[@alt=" + this.attributeValue(e.alt) + "]";
		} else if (e.title != '') {
			return "//img[@title=" + this.attributeValue(e.title) + "]";
		} else if (e.src != '') {
			this.log.debug("Found src: " + e.getAttribute("src"));
			return "//img[contains(@src," + this.attributeValue(e.getAttribute("src")) + ")]";
		}
	}
	return null;
});

LocatorBuilders.add('xpath:submit', function(e) {
 	if (e.nodeName == 'INPUT' && e.type== 'submit') {
		if (e.value != '') {
			return "//input[@value=" + this.attributeValue(e.value) + "]";
		} else if (e.name != '') {
			return "//input[@name=" + this.attributeValue(e.name) + "]";
		} 
	}
	return null;
});

/* Try to get radio button via label next to it
*/
LocatorBuilders.add('xpath:radio', function(e) {
	if (! ( e.nodeName == 'INPUT' && e.type == 'radio'))  return null;
	try {
		var label = e.parentNode.getElementsByTagName("label").item(0).textContent.replace(/^\s+|\s+$/g, "");
		
	}
	catch(err) {
		this.log.debug(err);
		return null;
	}
	
	return "//input[@type='radio' and following-sibling::label[normalize-space(.)='" + label + "']]";
});


LocatorBuilders.add('xpath:checkbox', function(e) {
	if (! ( e.nodeName == 'INPUT' && e.type == 'checkbox'))  return null;
	
	return this.getLocatorByAssocText(e, "//input[@type='checkbox']");
});

/* Gets an image on the page by getting the text
	next to it (note this builder should be lower priority than other img builders)
*/
LocatorBuilders.add('xpath:img:assocText', function(e) {

	if (e.nodeName != 'IMG') return null;

	if (e.alt != '') {
        var predicate = "//[@alt=" + this.attributeValue(e.alt) + "]";
    } else if (e.title != '') {
        var predicate = "//img[@title=" + this.attributeValue(e.title) + "]";
    } else if (e.src != '') {
 	    var predicate = "//img[contains(@src," + this.attributeValue(e.getAttribute("src")) + ")]";
    } else 
        return null;			 
	this.log.debug("Found img predicate: " + predicate);			 
	return this.getLocatorByAssocText(e, predicate);
	
});


/* Locates an object via its class and embedded text (the xpath dot operator)
*/
LocatorBuilders.add('xpath:textContent', function(e) {
	var textnode = e.textContent;
	if (textnode.match(/^\s*$/)) return null;
	
	return "//" + e.nodeName.toLowerCase() + "[.='" + e.textContent + "']";
	
});


//Reset the order in which locators are used.
//LocatorBuilders.order = [ 'xpath:myimg', 'xpath:img' ];
LocatorBuilders.order = ['xpath:submit', 'xpath:checkbox', 'xpath:radio', 'xpath:img', 'xpath:img:assocText', 'link', 'name', 'xpath:link', 'id', 'xpath:attributes', 'xpath:textContent', 'xpath:href', 'dom:name', 'dom:index', 'xpath:position'];

