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

package serene.validation.handlers.error;

import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import serene.validation.schema.simplified.SElement;

import serene.Reusable;

public interface ContextErrorHandler extends Reusable, ExternalConflictErrorCatcher{
    int NONE = -1;	
    int ROOT = 0;
    int ELEMENT = 1;
    
    boolean isCandidate();
    void setCandidate(boolean isCandidate);
    
	int getId();
	
	void handle(int contextType, String qName, SElement definition, boolean restrictToFileName, Locator locator) throws SAXException;
	void handle(int contextType, String qName, boolean restrictToFileName, Locator locator) throws SAXException;
	void discard();
	void record(int contextType, String qName, boolean restrictToFileName, Locator locator);
		
    int getConflictResolutionId();	
    ConflictMessageReporter getConflictMessageReporter();
} 