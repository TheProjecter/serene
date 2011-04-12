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

package sereneWrite;

import java.io.File;
import java.io.FileWriter;

public class FileHandler extends WriteHandler{
	
	FileWriter fileWriter;	
	String destinationDirectory;
	
	public FileHandler(){}
		
	public void init(String s){
		int index = s.indexOf(File.separator);
		if(index < 0){
			File file= new File(s);
			file.mkdir();
			return;
		}		
		String head  = s.substring(0, index);
		String tail = s.substring(index+1);
		String name;
		File file= new File(head);
		file.mkdir();		
		while(index > 0){
			index = tail.indexOf(File.separator);
			name = tail.substring(0, index+1);
			head = head + File.separator + name;
			tail = tail.substring(index+1);			
			file= new File(head);
			file.mkdir();
		}
		
		if(tail != null && !tail.equals("")){
			head = head + tail;
			file= new File(head);
			file.mkdir();
		}
		
		destinationDirectory = head;
	}
	
	public void start(String s){		
		try{			
			File file= new File(destinationDirectory+File.separator+s);
			fileWriter = new FileWriter(file, true);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
		
	public void out(String message){		
		try{		
			fileWriter.write(("\n"+message), 0, message.length()+1);			
			fileWriter.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void close(){
		try{
			fileWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}