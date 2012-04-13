/*
Copyright 2012 Radu Cernuta 

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


package serene.bind.util;

import java.util.Map;

import sereneWrite.MessageWriter;


public interface DocumentIndexedData{
            
    int NO_RECORD = -2;    
    int UNKNOWN = -3;
    
        
		
	int getItemId(int recordIndex);	
	Map<String, String> getDeclaredXmlns(int recordIndex);	
	String getItemDescription(int recordIndex);    
    String getNamespaceURI(int recordIndex);	
    String getLocalName(int recordIndex);		
	String getAttributeType(int recordIndex);
	char[] getCharArrayValue(int recordIndex);
	String getStringValue(int recordIndex);	
	String getSystemId(int recordIndex);	
	String getPublicId(int recordIndex);	
	int getLineNumber(int recordIndex);	
	int getColumnNumber(int recordIndex);
	/**
	* Returns an imutable DocumentIndexedData object containing a copy of the 
	* records in this object. 
	*/
	DocumentIndexedData getDocumentIndexedData();
}
