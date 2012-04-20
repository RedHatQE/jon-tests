// displaying output example from http://rhq-project.org/display/JOPR2/Running+the+RHQ+CLI

/**
 * @author fbrychta@redhat.com (Filip Brychta)
 * Apr 20, 2012     
 **/

// Tabular Writer
var criteria = ResourceCriteria();
criteria.addFilterResourceTypeName('VM Memory System');
criteria.addFilterParentResourceName('JVM');
var resources = ResourceManager.findResourcesByCriteria(criteria);

println(resources);
pretty.print(resources.get(0));


// Exporter
var outTxtName = 'output.txt';
exporter.setTarget('raw', outTxtName);
exporter.write(resources);

var outCsvName = 'output.csv';
exporter.setTarget('csv', outCsvName);
exporter.write(resources);

// check that files were created
var javaClases = new JavaImporter(java.io);

with(javaClases){
  var outputDir = java.lang.System.getenv().get("RHQ_CLI_HOME");
  var outTxtFile = new File(outTxtName);
  var outCsvFile = new File(outCsvName);
  if(outputDir != null){
    outTxtFile = new File(outputDir, outTxtName); 
    outCsvFile = new File(outputDir, outCsvName);
    println("RHQ_CLI_HOME: " + outputDir);
  }
  println("File1: " + outTxtFile);
  println("File2: " + outCsvFile);
  assertTrue(outTxtFile.exists(), "File " + outTxtFile + "does not exist!!");
  assertTrue(outCsvFile.exists(), "File " + outCsvFile + "does not exist!!");
  
  // TODO check content
}
