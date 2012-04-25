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


package serene.util;

import org.xml.sax.Locator;

public interface InputDescriptor extends Locator{
    
    // Item id's
    int NOT_CLASSIFIED = -1;
	int ROOT = 0;
	int ELEMENT = 1;
	int ATTRIBUTE = 2;
	int CHARACTER_CONTENT = 3;
	int LIST_TOKEN = 4;
	
	
	int getItemId();
	
	String getItemDescription();
    
    String getNamespaceURI();
    
    String getLocalName();
	
    String getAttributeType();
    
    String getStringValue();
    
    char[] getCharArrayValue();
    
	String getSystemId();
	
	String getPublicId();
	
	int getLineNumber();
	
	int getColumnNumber();

	
}
