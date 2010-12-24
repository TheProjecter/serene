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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;

import serene.datatype.MissingLibraryException;

public class WriteErrorHandler implements ErrorHandler{
    boolean countMissingLibraryExceptions;
    
    boolean hasError = false;
	WriteHandler writeHandler;
	
	public WriteErrorHandler(){
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
	
    public void init(){
        hasError = false;
    }
    
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException{
        if(name == null) throw new NullPointerException();
        if(name.equals("http://example.com/countMissingLibraryExceptions")) countMissingLibraryExceptions = value;
        else throw new SAXNotRecognizedException();
    }
    
    public boolean getFeature(String name) throws SAXNotRecognizedException{
        if(name == null) throw new NullPointerException();
        if(name.equals("http://example.com/countMissingLibraryExceptions")) return countMissingLibraryExceptions;
        else throw new SAXNotRecognizedException();
    }
    
	public void fatalError(SAXParseException exception) throws SAXException{
		writeHandler.out(exception.toString());
	}
	
	public void error(SAXParseException exception) throws SAXException{
        if(countMissingLibraryExceptions){
            hasError = true;
        }else{
            if(!(exception instanceof MissingLibraryException)){
                hasError = true;
            }
        }
		String s = exception.getSystemId()+":"+exception.getLineNumber()+":"+exception.getColumnNumber()+" "+exception.getMessage();
		writeHandler.out("\n"+s);
	}
	
	public void warning(SAXParseException exception) throws SAXException{
		String s = exception.getSystemId()+":"+exception.getLineNumber()+":"+exception.getColumnNumber()+" "+exception.getMessage();
		writeHandler.out("\n"+s);
	}
	
	public void close(){
		writeHandler.close();
	}
	
    public boolean hasError(){
        return hasError;
    }
	public void print(String s){
		writeHandler.out("\n"+s);
	}
}