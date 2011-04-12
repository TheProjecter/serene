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

import java.util.Arrays;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SElement;

import serene.dtdcompatibility.AttributeDefaultValueException;

import sereneWrite.MessageWriter;

public class AttributeDefaultValueErrorHandler implements ErrorCatcher{
    
    String qName;
    String location;
    
    ArrayList<String> messages;
    
    ErrorDispatcher errorDispatcher;
    
    MessageWriter debugWriter;
    
    public AttributeDefaultValueErrorHandler(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.errorDispatcher = errorDispatcher;
        messages = new ArrayList<String>();
    }
    
    
    public void clear(){
        qName = null;
        location = null;
        messages.clear();
    }
    
    public void setAttribute(String qName, String location){
        this.qName = qName;
        this.location = location;
    }
    
    public boolean hasError(){
        return !messages.isEmpty();
    }
    
    public void report() throws SAXException{        
        for(String message : messages){
            errorDispatcher.error(new AttributeDefaultValueException(message, null, null, -1, -1));
        }
    }
    //errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(String qName, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}	
	public void unexpectedElement(String qName, AElement definition, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(String qName, AElement[] definition, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}
	
	
	public void unknownAttribute(String qName, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}	
	public void unexpectedAttribute(String qName, AAttribute definition, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(String qName, AAttribute[] definition, String systemId, int lineNumber, int columnNumber){
        throw new IllegalStateException();
	}
	
		
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String qName, String systemId, int lineNumber, int columnNumber, APattern sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
	
	public void misplacedElement(APattern contextDefinition, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, String[] qName,  String[] systemId, int[] lineNumber, int[] columnNumber, APattern[] sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
		
	
	public void excessiveContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern excessiveDefinition, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Excessive content in the context of <"+context.getQName()+"> at "+context.getLocation()+". ";                 
        messages.add(message);
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, String qName, String systemId, int lineNumber, int columnNumber){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Excessive content in the context of <"+context.getQName()+"> at "+context.getLocation()+". ";
        messages.add(message);
	}
	
	public void missingContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern missingDefinition, int expected, int found, String[] qName, String[] systemId, int[] lineNumber, int[] columnNumber){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Missing content in the context of <"+context.getQName()+"> at "+context.getLocation()+". ";
        messages.add(message);
	}

	public void illegalContent(Rule context, String startQName, String startSystemId, int startLineNumber, int startColumnNumber){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Illegal content in the context of <"+context.getQName()+"> at "+context.getLocation()+". ";
        messages.add(message);
	}
	
	public void ambiguousElementContentError(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
        throw new IllegalStateException();
	}

	public void ambiguousAttributeContentError(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharsContentError(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Ambiguous value, possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation();
        }
        message += ".";
        messages.add(message);
	}

	
	public void ambiguousElementContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousAttributeContentWarning(String qName, String systemId, int lineNumber, int columnNumber, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharsContentWarning(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
        String message = "DTD compatibility warning. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match a single definition in attribute pattern."
                        +" Ambiguous value, possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation();
        }
        message += ".";
        messages.add(message);
	}

	
	
	
	public void undeterminedByContent(String qName, String candidateMessages){
		throw new IllegalStateException();
	}

	public void characterContentDatatypeError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();        
	}
	public void attributeValueDatatypeError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Datatype error in <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation()+". "+datatypeErrorMessage;
        messages.add(message);
	}
	
	public void characterContentValueError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		throw new IllegalStateException();        
	}
	public void attributeValueValueError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Value error in <"+charsDefinition.getQName()+"> at . "+charsDefinition.getLocation();
        messages.add(message);        
	}
	
	public void characterContentExceptedError(String elementQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(String attributeQName, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Value excepted by in <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation()+". ";
        messages.add(message);
	}
	
	public void unexpectedCharacterContent(String charsSystemId, int charsLineNumber, int columnNumber, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(String charsSystemId, int charsLineNumber, int columnNumber, AAttribute attributeDefinition){
        String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Unexpected value.";
        messages.add(message);
	}
	
	public void ambiguousCharacterContent(String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void ambiguousAttributeValue(String attributeQName, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Ambiguous value, possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation();
        }
        message += ".";
        messages.add(message);
	}
	
	public void listTokenDatatypeError(String token, String charsSystemId, int charsLineNumber, int columnNumber, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" List token "+token+" does not match datatype of <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation()+". "+datatypeErrorMessage;
        messages.add(message);
	}
	public void listTokenValueError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AValue charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" List token "+token+" does not match <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation()+". ";
        messages.add(message);
	}
	public void listTokenExceptedError(String token, String charsSystemId, int charsLineNumber, int columnNumber, AData charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" List token "+token+" is excepted by datatype of <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation()+". ";
        messages.add(message);
	}
	public void ambiguousListToken(String token, String systemId, int lineNumber, int columnNumber, CharsActiveTypeItem[] possibleDefinitions){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Ambiguous list token \""+token+"\", possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation();
        }
        message += ".";
        messages.add(message);
	}
	
	public void missingCompositorContent(Rule context, String startSystemId, int startLineNumber, int startColumnNumber, APattern definition, int expected, int found){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+location
                        +" does not match attribute pattern."
                        +" Missing compositor content in <"+context.getQName()+"> at "+context.getLocation()+"."
                        +" Expected "+expected+" occurrences of <"+definition.getQName()+"> at "+definition.getLocation()+", found "+found+".";
        messages.add(message);
	}
	//--------------------------------------------------------------------------
    
    public void contextDependentDatatypeError(SPattern pattern){
		String message = "DTD compatibility error. Attribute definition <"+qName+"> at "+location+" cannot have a default value."
                        +" Context dependent datatype at <"+pattern.getQName()+"> at "+pattern.getLocation()+".";
        messages.add(message);
    }    
}
