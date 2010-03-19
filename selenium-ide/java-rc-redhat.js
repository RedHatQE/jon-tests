
load('java-rc.js');
this.name = "java-rc-redhat";
this.remoteControl = true;
this.playable = true;

//override to not strip off the "AndWait".
//this.oldgetdef = Command.getDefinition;


Command.prototype.getDefinition = function() {
        if (this.command == null) return null;
        var commandName = this.command;  //.replace(/AndWait$/, '');
        var api = Command.loadAPI();
        var r = /^(assert|verify|store|waitFor)(.*)$/.exec(commandName);
        if (r) {
                var suffix = r[2];
                var prefix = "";
                if ((r = /^(.*)NotPresent$/.exec(suffix)) != null) {
                        suffix = r[1] + "Present";
                        prefix = "!";
                } else if ((r = /^Not(.*)$/.exec(suffix)) != null) {
                        suffix = r[1];
                        prefix = "!";
                }
                var booleanAccessor = api[prefix + "is" + suffix];
                if (booleanAccessor) {
                        return booleanAccessor;
                }
                var accessor = api[prefix + "get" + suffix];
                if (accessor) {
                        return accessor;
                }
        }
        return api[commandName];
}

//override to not split commands like "clickAndWait" into 2 separate commands
this.filterForRemoteControl = function(originalCommands) {
		if (this.remoteControl && (!this.name == "java-rc-redhat")) {
		var commands = [];
		for (var i = 0; i < originalCommands.length; i++) {
			var c = originalCommands[i];
			if (c.type == 'command' && c.command.match(/AndWait$/)) {
				var c1 = c.createCopy();
				c1.command = c.command.replace(/AndWait$/, '');
				commands.push(c1);
				commands.push(new Command("waitForPageToLoad", options['global.timeout'] || "30000"));
			} else {
				commands.push(c);
			}
		}
		return commands;
	} else {
		return originalCommands;
	}
};

