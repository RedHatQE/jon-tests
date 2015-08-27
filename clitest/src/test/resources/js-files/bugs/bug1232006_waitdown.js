//Check if agent is down
assertTrue(resources.platforms()[0].waitForChild({name:"RHQ Agent"}).waitForNotAvailable(), "Agent '"
		+ resources.platforms()[0].waitForChild({name:"RHQ Agent"}).name + "' of platform '"+ resources.platforms()[0].name +"' is not DOWN");
pretty.print("Agent '" + resources.platforms()[0].waitForChild({name:"RHQ Agent"}).name + "' of platform '" + resources.platforms()[0].name + "' is DOWN");