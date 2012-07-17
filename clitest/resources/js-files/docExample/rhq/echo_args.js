// echo_args example from http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI

if (args.length < 2) {
    throw "Not enough arguments!";
}

for (i in args) {
    println('args[' + i +'] = ' + args[i]);
}
println('named args...');
println('x = ' + x);
println('y = ' + y);
