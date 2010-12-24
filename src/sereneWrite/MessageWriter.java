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

public class MessageWriter{

	WriteHandler writeHandler;
	
	public MessageWriter(){
		this.writeHandler = new ConsoleHandler();
	}
		
	public void setWriteHandler(WriteHandler writeHandler){
		this.writeHandler = writeHandler;
	}
		
	public void start(String name){
		writeHandler.start(name);
	}
	
	public void init(String name){
		writeHandler.init(name);
	}
	
	public void write(String message){
		writeHandler.out(message);
	}
	
	public void close(){
		writeHandler.close();
	}
}