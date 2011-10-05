/*
Copyright 2011 Radu Cernuta 

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

import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.validation.schema.active.components.AElement;

public interface MessageReporter{    
    int UNRESOLVED = 0;
    int RESOLVED = 1;    
    int AMBIGUOUS = 2;
    
    void setContextType(int contextType);
	void setContextQName(String qName);
	void setContextLocation(String publicId, String systemId, int lineNumber, int columnNumber);
	void setContextDefinition(AElement definition);    
	void setRestrictToFileName(boolean restrictToFileName);
    
    void setParent(MessageReporter parent);
    
    void report(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException;
    void report(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException;
    String getCandidateErrorMessage(String prefix, boolean restrictToFileName);
    String getErrorMessage(String prefix, boolean restrictToFileName);
    
    void setConflictResolutionId(int conflictResolutionId);
    int getConflictResolutionId();
    
    ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher);
    
    boolean containsErrorMessage();
}
