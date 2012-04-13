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

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ActiveTypeItem;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AListPattern;
	
import serene.validation.schema.simplified.SimplifiedComponent;

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
		
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
	}
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){       
	}
	
	public void illegalContent(Rule context, int startInputRecordIndex){
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
	}
	
	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
	}
	
	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
	}
	
	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
	}
	
	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
	
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	}    
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
	}
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
	}

    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
    }
    
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
	}
    
    public  void conflict(int conflictResolutionId, MessageReporter commonMessages, int candidatesCount, BitSet disqualified, MessageReporter [] candidateMessages){
    }
	
    public void internalConflict(ConflictMessageReporter conflictMessageReporter){}
	
	
	public void handle(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator)
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