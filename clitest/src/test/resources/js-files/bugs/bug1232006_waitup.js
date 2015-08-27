//Check if agent is up
assertTrue(resources.platforms()[0].waitForChild({name:"RHQ Agent"}).waitForAvailable(), "Agent '"
		+ resources.platforms()[0].waitForChild({name:"RHQ Agent"}).name + "' of platform '"+ resources.platforms()[0].name +"' is not UP");
pretty.print("Agent '" + resources.platforms()[0].waitForChild({name:"RHQ Agent"}).name + "' of platform '" + resources.platforms()[0].name + "' is UP");
