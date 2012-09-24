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

import java.util.Arrays;
import java.util.BitSet;

import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Locator;

import serene.validation.schema.simplified.SAttribute;


import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SRule;
import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;

/**
* Does nothing. For Common state shifting. 
*/
public class DefaultErrorHandler extends AbstractContextErrorHandler{
	public DefaultErrorHandler(){
		super();
		id = ContextErrorHandlerManager.DEFAULT;
	}
	
	//public void init(){}
	public void recycle(){		
		pool.recycle(this);

	}
    public boolean isCandidate(){
        return false;
    }
    public void setCandidate(boolean isCandidate){}
    
	public void unknownElement(int inputRecordIndex){
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
	}
	
	public void unknownAttribute(int inputRecordIndex){
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
	}
		
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int[] inputRecordIndex, SPattern[] sourceDefinition, SPattern reper){
	}
	
	public void misplacedContent(SPattern contextDefinition, int startInputRecordIndex, SPattern definition, int inputRecordIndex, SPattern sourceDefinition, SPattern reper){
	}
	
	public void excessiveContent(SRule context, int startInputRecordIndex, SPattern excessiveDefinition, int[] inputRecordIndex){
	}
	
	public void excessiveContent(SRule context, SPattern excessiveDefinition, int inputRecordIndex){
	}
	
	public void missingContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found, int[] inputRecordIndex){       
	}
	
	public void illegalContent(SRule context, int startInputRecordIndex){
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, SElement[] possibleDefinitions){
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, SAttribute[] possibleDefinitions){
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, SElement[] possibleDefinitions){
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, SAttribute[] possibleDefinitions){
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	}    
	public void attributeValueDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	}
	
	public void characterContentValueError(int inputRecordIndex, SValue charsDefinition){
	}
	public void attributeValueValueError(int inputRecordIndex, SValue charsDefinition){
	}
	
	public void characterContentExceptedError(int inputRecordIndex, SData charsDefinition){
	}	
	public void attributeValueExceptedError(int inputRecordIndex, SData charsDefinition){
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, SElement elementDefinition){
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, SAttribute attributeDefinition){
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, SPattern[] possibleDefinitions){
	}
	public void unresolvedAttributeValue(int inputRecordIndex, SPattern[] possibleDefinitions){
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, SPattern charsDefinition, String datatypeErrorMessage){
	}
	public void listTokenValueError(int inputRecordIndex, SValue charsDefinition){
	}
	public void listTokenExceptedError(int inputRecordIndex, SData charsDefinition){
	}

    public void unresolvedListTokenInContextError(int inputRecordIndex, SPattern[] possibleDefinitions){
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, SPattern[] possibleDefinitions){
    }
    
	public void missingCompositorContent(SRule context, int startInputRecordIndex, SPattern definition, int expected, int found){
	}
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
    }
	
    public void internalConflict(ConflictMessageReporter conflictMessageReporter){}
	
	
	public void handle(int contextType, String qName, SElement definition, boolean restrictToFileName, Locator locator)
				throws SAXException{
	}
	
	public void handle(int contextType, String qName, boolean restrictToFileName, Locator locator)
				throws SAXException{
	}
	
	public void discard(){
	
	}
	
    public void record(int contextType, String qName, boolean restrictToFileName, Locator locator){
	    throw new IllegalStateException();
	}
    
	    
	public int getConflictResolutionId(){
        throw new IllegalStateException();
    }
	
    public ConflictMessageReporter getConflictMessageReporter(){
        throw new IllegalStateException();
    } 
    
	public String toString(){
		return "ValidationErrorHandler ";
	}
}