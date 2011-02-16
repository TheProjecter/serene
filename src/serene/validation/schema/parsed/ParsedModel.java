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


package serene.validation.schema.parsed;

import serene.validation.DTDMapping;

import sereneWrite.MessageWriter;
 
public class ParsedModel{
	Pattern topPattern;
	
    DTDMapping dtdMapping;
    
	MessageWriter debugWriter;
	public ParsedModel(DTDMapping dtdMapping, Pattern topPattern, MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
        this.dtdMapping = dtdMapping;
        this.topPattern = topPattern;
	}
    
    public Pattern getTopPattern(){
        return topPattern;
    }
    
    public DTDMapping getDTDMapping(){
        return dtdMapping;
    }
    
	public void write(){}
}
