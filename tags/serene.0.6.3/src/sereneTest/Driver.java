/*
Copyright 2010 Radu Cernuta 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package sereneTest;

import java.io.File;

import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;

import org.xml.sax.SAXException;

import sereneWrite.WriteErrorHandler;

public class Driver{
	
	/**
	* Naming convention for test directories: all testes are placed under one
	* directory called "testSuite/tests".
	* Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name].
	*/
	public static void main(String args[]){		
	    long startTime = System.currentTimeMillis();
		if(args == null || args.length == 0){
			System.out.println("Usage: java sereneTest.Driver [a] test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
			return;
		}
		
		boolean analyse = false;
		String s = args[0];
		String sourceDirName;
		int optionsStart = 1;
		if(s.equals("-a")){
            if(args.length == 1){
                System.out.println("Usage: java sereneTest.Driver [a] test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
                    +"\n"
                    +"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
                    +"\n"
                    +"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
                    +"\n"
                    +"\nPlease use:"
                    +"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
                    +"\n-d if you want to specify another destination directory"
                    +"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
                    +"\n-x if you want to only test one specific xml file from the test directory.");
                return;
            }
			sourceDirName = args[1];
			analyse = true;
			optionsStart = 2;
		}else{
			sourceDirName = s;
		}
		
		if(sourceDirName == null || sourceDirName.equals(""))return;
		
		// TODO see about index out of bounds
        
		String destinationDirName = "debug";
		String resultsDestinationFileName = "testResults.txt";
		String xmlFileName = null;
		for(int i = optionsStart; i < args.length; i = i+2){
			if(args[i].equals("-d")){
				if(args.length > i){
					destinationDirName = args[i+1];
				}
				else{
					System.out.println("Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
					return;					
				} 
			}else if(args[i].equals("-r")){
				if(args.length > i){					
					resultsDestinationFileName = args[i+1];
				}
				else{
					System.out.println("Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
					return;					
				}
			}else if(args[i].equals("-x")){
				if(args.length > i){
					xmlFileName = args[i+1];
				}
				else{
					System.out.println("Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
					return;					
				}
			}else{
				System.out.println("Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
				return;
			}
		}
				
		File sourceDir = new File(sourceDirName);
		String sourcePath = sourceDir.getAbsolutePath();	
		int sereneIndex = sourcePath.indexOf("testSuite");
		if(sereneIndex < 0){
			System.out.println("Usage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
			return;
		}
		String tail = sourcePath.substring(sereneIndex+9);
				 
		Date date = new Date();                
        Locale locale = new Locale("Romanian", "Romania");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMdd");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("kk-mm-ss");
		String runId = simpleDateFormat.format(date) +"H"
                        + simpleTimeFormat.format(date);
			
		if(destinationDirName.endsWith(File.separator)){
			destinationDirName = destinationDirName + "testSuite_"+runId+tail;
		}else{
			destinationDirName = destinationDirName + File.separator + "testSuite_"+runId+tail;
		}
		
					
		Tester tester = new Tester();			
		
		try{
		    if(xmlFileName != null) tester.test(sourcePath, xmlFileName, destinationDirName, resultsDestinationFileName);
			else tester.test(sourcePath, destinationDirName, resultsDestinationFileName);
		}catch(SAXException e){
		    e.printStackTrace();
		}
		
		System.out.println("Tests: " + tester.getTestCount());
		System.out.println("Elapsed time: " + (System.currentTimeMillis() - startTime));
		
		if(analyse){
			if(!resultsDestinationFileName.equals("testResults.txt")){
				System.out.println("\nRegression analysis not performed, see usage:"
				+"\n"
				+"\n"
				+"\nUsage: java sereneTest.Driver {a} test-directory [-d destination-directory-name] [-r result-file-name] [-x xml-file name]."
				+"\n"
				+"\nBy convention all the test files are placed in the testSuite/tests directory. There is one schema file per leaf directory named schema.rng and zero or more corresponding xml files named document1.xml, document2.xml ... The test-directory argument must specify either the entire testSuite or a subdirectory thereof."
				+"\n"
				+"\nBy default all the files in the specified test-directory are processed recursively and the results of every testing session are written in a directory named debug placed on the same level as sereneTest.jar in a directory with the name testSuite_date-hour-min-sec, preserving the original tree structure, the results of each specific test are placed in text files named testResults.txt in the corresponding subdirectories."
				+"\n"
				+"\nPlease use:"
				+"\n-a if you want the test results to be automatically tested for regression against the corresponding expected results from testSuite/acceptedResults; the results of the test will be written in the results directory in  a text file named regressionAnalysis.txt"
				+"\n-d if you want to specify another destination directory"
				+"\n-r if you want to specify another result file name; when this option is used it is impossible to execute automatical regression analysis"
				+"\n-x if you want to only test one specific xml file from the test directory.");
			return;
			}
			destinationDirName = destinationDirName.substring(0, destinationDirName.indexOf("testSuite")+24);
			File testDir = new File(destinationDirName);
			String testPath = testDir.getAbsolutePath();
						
			String standardDirName = "testSuite/acceptedResults";			
			destinationDirName = destinationDirName;
			resultsDestinationFileName = "regressionAnalysis.txt";
			
			Analyser analyser = new Analyser();
			analyser.analyse(testPath, standardDirName, destinationDirName, resultsDestinationFileName);
		}
				
	}   
	
}