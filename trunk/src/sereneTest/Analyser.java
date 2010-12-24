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

import java.util.Stack;
import java.util.Arrays;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import java.util.Date;
import java.util.Locale;

import java.text.SimpleDateFormat;

import sereneWrite.MessageWriter;
import sereneWrite.WriteHandler;
import sereneWrite.ConsoleHandler;
import sereneWrite.FileHandler;

class Analyser{
	
	MessageWriter debugWriter;
	
	
	String destinationDirName;
	String resultsDestinationFileName;
	Stack<String> sourceDirNames;

	
	public Analyser(){
		debugWriter = new MessageWriter();			
		debugWriter.setWriteHandler(new FileHandler());
		
		sourceDirNames = new Stack<String>();			
	}
	
	public void analyse(String sourceDirName, String standardDirName, String destinationDirName, String resultsDestinationFileName){
		debugWriter.init(destinationDirName);
		debugWriter.start(resultsDestinationFileName);
		sourceDirNames.clear();
		
		
		Date date = new Date();                
        Locale locale = new Locale("Romanian", "Romania");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMdd");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("kk-mm-ss");
		String runId = simpleDateFormat.format(date) +"H"
                        + simpleTimeFormat.format(date);
		
		debugWriter.write("");						
		debugWriter.write("ANALISYS "+runId);
		System.out.println("ANALISYS "+runId);
		File standardDir = new File(standardDirName);
		if(!standardDir.isDirectory()){
			System.out.println("NO STANDARD DIRECTORY "+standardDirName);
			debugWriter.write("NO STANDARD DIRECTORY "+standardDirName);
			return;
		}
		
		File sourceDir = new File(sourceDirName);		
		if(!sourceDir.isDirectory()){
			System.out.println("NO DIRECTORY "+sourceDirName);
			debugWriter.write("NO DIRECTORY "+sourceDirName);
			return;
		}						
				
		
		debugWriter.write("");
		debugWriter.write("TESTED DIRECTORY "+sourceDir);				
		File[] testContent = sourceDir.listFiles();
		if(testContent.length == 0){
			debugWriter.write("empty directory ");
            System.out.println("TESTED DIRECTORY "+sourceDir);
			System.out.println("empty directory ");
			return;
		}
				
		File[] testFiles = new File[testContent.length];
		File[] testDirs = new File[testContent.length];
		for(int i = 0; i < testContent.length; i++){			
			String fileName = testContent[i].getName();
			if(testContent[i].isDirectory()){
				testDirs[i] = testContent[i]; 	
			}else{
				testFiles[i] = testContent[i];
			}
		}		
		
		File[] standardContent = standardDir.listFiles();
		
		compareDirContent(standardContent, testContent);
		
		for(int i = 0; i < testFiles.length; i++){
			if(testFiles[i] != null){
				debugWriter.write("TESTED DOCUMENT "+ testFiles[i]);
				File standard = getCorrespondingFile(testFiles[i], standardContent);
				if(standard == null){					
					debugWriter.write("No corresponding file in acceptedResults.");
					System.out.println("TESTED DOCUMENT "+ testFiles[i]);
					System.out.println("No corresponding file in acceptedResults.");
				}else{
					//compare files
				}
			}
		}
		
		for(int i = 0; i < testDirs.length; i++){
			if(testDirs[i] != null){
				File standard = getCorrespondingFile(testDirs[i], standardContent);
				if(standard == null){
					debugWriter.write("No corresponding directory in acceptedResults.");
					System.out.println("TESTED DIRECTORY "+sourceDir);
					System.out.println("No corresponding directory in acceptedResults.");
				}else{
					analyse(testDirs[i], standard);
				}				
			}
		}
		debugWriter.close();
	}

	
	private void analyse(File sourceDir, File standardDir){		
		debugWriter.write("");
		debugWriter.write("TESTED DIRECTORY "+sourceDir);		
		File[] testContent = sourceDir.listFiles();
		if(testContent.length == 0){
			debugWriter.write("empty directory ");
            System.out.println("TESTED DIRECTORY "+sourceDir);
			System.out.println("empty directory ");
			return;
		}
		
		sourceDirNames.push(sourceDir.getName());
				
		File[] testFiles = new File[testContent.length];
		File[] testDirs = new File[testContent.length];
		for(int i = 0; i < testContent.length; i++){			
			String fileName = testContent[i].getName();
			if(testContent[i].isDirectory()){
				testDirs[i] = testContent[i]; 	
			}else{
				testFiles[i] = testContent[i];
			}
		}		
		
		File[] standardContent = standardDir.listFiles();
		
		compareDirContent(standardContent, testContent);
		
		for(int i = 0; i < testFiles.length; i++){
			if(testFiles[i] != null){
				debugWriter.write("TESTED DOCUMENT "+ testFiles[i]);
				File standard = getCorrespondingFile(testFiles[i], standardContent);
				if(standard == null){
					debugWriter.write("No corresponding file in acceptedResults.");
					System.out.println("TESTED DOCUMENT "+ testFiles[i]);
					System.out.println("No corresponding file in acceptedResults.");
				}else{
					compare(testFiles[i], standard);
				}
			}
		}
		
		for(int i = 0; i < testDirs.length; i++){
			if(testDirs[i] != null){
				File standard = getCorrespondingFile(testDirs[i], standardContent);
				if(standard == null){
					debugWriter.write("No corresponding directory in acceptedResults.");
					System.out.println("TESTED DIRECTORY "+sourceDir);
					System.out.println("No corresponding directory in acceptedResults.");
				}else{
					analyse(testDirs[i], standard);
				}
				
				
			}
		}
		
		sourceDirNames.pop();
	}
	
	
	private void compareDirContent(File[] standardContent, File[] testContent){
		File[] missing = new File[standardContent.length];
		int k = 0;
		for(int i = 0; i <standardContent.length; i++){
			test:{
			if(standardContent[i] != null){
				for(int j = 0; j < testContent.length; j++){
					if(testContent[j] != null){
						if(standardContent[i].getName().equals(testContent[j].getName())){
							break test;
						}
					}
				}
				missing[k++] = standardContent[i];
			}
			}
		}
		if(k > 0){
			debugWriter.write("Missing content. Files in the acceptedResults without correspondent in the tested directory:");			
			System.out.println("Missing content. Files in the acceptedResults without correspondent in the tested directory:");
			for(int i = 0; i <missing.length; i++){
				if(missing[i] != null){
					System.out.println(missing[i].getAbsolutePath());
					debugWriter.write(missing[i].getAbsolutePath());					
				}else{
					break;					
				}
			}
		}
	}
	
	private File getCorrespondingFile(File testFile, File[] standardFiles){
		String testName = testFile.getName();
		for(int i = 0; i < standardFiles.length; i++){
			if(standardFiles[i].getName().equals(testName))
				return standardFiles[i];
		}
		return null;
	}
	
	private void compare(File testFile, File standardFile){
		try{
			FileReader testFr = new FileReader(testFile);
			BufferedReader testBr = new BufferedReader(testFr);
			
			FileReader standardFr = new FileReader(standardFile);
			BufferedReader standardBr = new BufferedReader(standardFr);
		
			String testLine = null;
			String standardLine = null;
			int lineNumber = 0;
			while(true){	
				lineNumber++;
				boolean noTestLine = false;
				
				
				testLine = testBr.readLine();				
				if(testLine !=null){
					testLine = testLine.trim();
					testLine = clearPathes(testLine);
				}
				else{
					noTestLine = true;
					testLine = "";
				}
				
				standardLine = standardBr.readLine();				
				if(standardLine !=null){
					standardLine = standardLine.trim();
					standardLine = clearPathes(standardLine);
				}
				else if(noTestLine)break;
				else{
					debugWriter.write("Different content starting at line: "+lineNumber+".");
                    System.out.println("FILE "+testFile.getAbsolutePath()+".");
					System.out.println("Different content starting at line: "+lineNumber+".");
					break;
				}
				
				if(!testLine.equals(standardLine)){
					debugWriter.write("Different content starting at line: "+lineNumber+".");
                    System.out.println("FILE "+testFile.getAbsolutePath()+".");
					System.out.println("Different content starting at line: "+lineNumber+".");
					break;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
	String clearPathes(String line){
		int pathIndex = line.indexOf("testSuite")+1;
		int startIndex = -1;
		String interm = "";
		while(pathIndex > 0){			
			interm = line.substring(pathIndex); 
			line = line.substring(0, pathIndex);
			startIndex = line.lastIndexOf(" ");
			if(startIndex < 0) startIndex= 0;
			line = line.substring(0, startIndex);
			line += interm;
			pathIndex = line.indexOf("testSuite")+1;
		}
		return line;
	}
}