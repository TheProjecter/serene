
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

import java.io.File;

import org.xml.sax.SAXException;

import serene.validation.schema.active.Rule;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SNameClass;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SElement;

import serene.dtdcompatibility.AttributeDefaultValueException;

import serene.validation.handlers.content.util.ActiveInputDescriptor;

import sereneWrite.MessageWriter;

public class AttributeDefaultValueErrorHandler implements ErrorCatcher{
    
    String qName;
    String location;
    
    ArrayList<String> errorMessages;
    ArrayList<String> warningMessages;
    
    ErrorDispatcher errorDispatcher;
    
    boolean restrictToFileName;
    
    ActiveInputDescriptor activeInputDescriptor;
    
    MessageWriter debugWriter;
    
    public AttributeDefaultValueErrorHandler(ActiveInputDescriptor activeInputDescriptor,
                                                ErrorDispatcher errorDispatcher, 
                                                MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.errorDispatcher = errorDispatcher;
        this.activeInputDescriptor = activeInputDescriptor;
        
        errorMessages = new ArrayList<String>();
        warningMessages = new ArrayList<String>();
    }
    
       
    public void clear(){
        qName = null;
        location = null;
        errorMessages.clear();
        warningMessages.clear();
    }
    
    public void setAttribute(String qName, String location){
        this.qName = qName;
        this.location = location;
    }
    
    public void setRestrictToFileName(boolean value){
        restrictToFileName = value;
    }
    
    /*public boolean hasError(){
        return !messages.isEmpty();
    }*/
    
    public void report() throws SAXException{        
        for(String message : errorMessages){
            errorDispatcher.error(new AttributeDefaultValueException(message, null, null, -1, -1));
        }
        for(String message : warningMessages){
            errorDispatcher.warning(new AttributeDefaultValueException(message, null, null, -1, -1));
        }
    }
    //errorCatcher
	//--------------------------------------------------------------------------
	public void unknownElement(int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedElement(SimplifiedComponent definition, int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousElement(SimplifiedComponent[] definition, int inputRecordIndex){
        throw new IllegalStateException();
	}
	
	
	public void unknownAttribute(int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedAttribute(SimplifiedComponent definition, int inputRecordIndex){
        throw new IllegalStateException();
	}	
	public void unexpectedAmbiguousAttribute(SimplifiedComponent[] possibleDefinition, int inputRecordIndex){
        throw new IllegalStateException();
	}
	
		
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int inputRecordIndex, APattern sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
	
	public void misplacedContent(APattern contextDefinition, int startInputRecordIndex, APattern definition, int[] inputRecordIndex, APattern[] sourceDefinition, APattern reper){
        throw new IllegalStateException();
	}
		
	
	public void excessiveContent(Rule context, int startInputRecordIndex, APattern excessiveDefinition, int[] inputRecordIndex){		
		/*String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Excessive content in the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+". ";                 
        errorMessages.add(message);*/
        throw new IllegalStateException();
	}
	
	public void excessiveContent(Rule context, APattern excessiveDefinition, int inputRecordIndex){
		/*String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Excessive content in the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);*/
        throw new IllegalStateException();
	}
	
	public void missingContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found, int[] inputRecordIndex){
		/*String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Missing content in the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);*/
        throw new IllegalStateException();
	}

	public void illegalContent(Rule context, int startInputRecordIndex){
		/*String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Illegal content in the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);*/
        throw new IllegalStateException();        
	}
	
	public void unresolvedAmbiguousElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void unresolvedUnresolvedElementContentError(int inputRecordIndex, AElement[] possibleDefinitions){
        throw new IllegalStateException();
	}

	public void unresolvedAttributeContentError(int inputRecordIndex, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	
	public void ambiguousUnresolvedElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}
	
	public void ambiguousAmbiguousElementContentWarning(int inputRecordIndex, AElement[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousAttributeContentWarning(int inputRecordIndex, AAttribute[] possibleDefinitions){
		throw new IllegalStateException();
	}

	public void ambiguousCharacterContentWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        throw new IllegalStateException();
	}
	
	public void ambiguousAttributeValueWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
        String message = "DTD compatibility warning. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match a single definition in attribute pattern."
                        +" Ambiguous value, possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName);
        }
        message += ".";
        errorMessages.add(message);
	}

	
	public void characterContentDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		throw new IllegalStateException();        
	}
	public void attributeValueDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Datatype error in <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation(restrictToFileName)+". "+datatypeErrorMessage;
        errorMessages.add(message);
	}
	
	public void characterContentValueError(int inputRecordIndex, AValue charsDefinition){
		throw new IllegalStateException();        
	}
	public void attributeValueValueError(int inputRecordIndex, AValue charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Value error in <"+charsDefinition.getQName()+"> at . "+charsDefinition.getLocation(restrictToFileName);
        errorMessages.add(message);
	}
	
	public void characterContentExceptedError(int inputRecordIndex, AData charsDefinition){
		throw new IllegalStateException();
	}	
	public void attributeValueExceptedError(int inputRecordIndex, AData charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Value excepted by in <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);
	}
	
	public void unexpectedCharacterContent(int inputRecordIndex, AElement elementDefinition){
		throw new IllegalStateException();
	}	
	public void unexpectedAttributeValue(int inputRecordIndex, AAttribute attributeDefinition){
        String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Unexpected value.";
        errorMessages.add(message);
	}
	
	public void unresolvedCharacterContent(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		throw new IllegalStateException();
	}
	public void unresolvedAttributeValue(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Unresolved value, all candidates resulted in errors, available definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName);
        }
        message += ".";
        errorMessages.add(message);
	}
	
	public void listTokenDatatypeError(int inputRecordIndex, DatatypedActiveTypeItem charsDefinition, String datatypeErrorMessage){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" List token "+activeInputDescriptor.getItemDescription(inputRecordIndex)+" does not match datatype of <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation(restrictToFileName)+". "+datatypeErrorMessage;
        errorMessages.add(message);
	}
    
	public void listTokenValueError(int inputRecordIndex, AValue charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" List token "+activeInputDescriptor.getItemDescription(inputRecordIndex)+" does not match <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);
	}
	public void listTokenExceptedError(int inputRecordIndex, AData charsDefinition){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" List token "+activeInputDescriptor.getItemDescription(inputRecordIndex)+" is excepted by datatype of <"+charsDefinition.getQName()+"> at "+charsDefinition.getLocation(restrictToFileName)+". ";
        errorMessages.add(message);
	}
	
    public void unresolvedListTokenInContextError(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Token \""+activeInputDescriptor.getItemDescription(inputRecordIndex)+"\" could not be resolved resolved to a single definition, all candidates resulted in errors. Available definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName);
        }
        message += ".";
        errorMessages.add(message);
    }    
	public void ambiguousListTokenInContextWarning(int inputRecordIndex, CharsActiveTypeItem[] possibleDefinitions){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Token \""+activeInputDescriptor.getItemDescription(inputRecordIndex)+"\" could not be resolved to a single definition, several candidates could be correct. Possible definitions: ";
        for(CharsActiveTypeItem definition : possibleDefinitions){
            message += "\n<"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName);
        }
        message += ".";
        warningMessages.add(message);
    }
    
    
	public void missingCompositorContent(Rule context, int startInputRecordIndex, APattern definition, int expected, int found){
		String message = "DTD compatibility error. Default value for attribute definition <"+qName+"> at "+getLocation(location)
                        +" does not match attribute pattern."
                        +" Missing compositor content in <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+"."
                        +" Expected "+expected+" occurrences of <"+definition.getQName()+"> at "+definition.getLocation(restrictToFileName)+", found "+found+".";
        errorMessages.add(message);
	}
	
	public void internalConflict(ConflictMessageReporter conflictMessageReporter){
	    throw new IllegalStateException();
    }
	//--------------------------------------------------------------------------
    
    public void contextDependentDatatypeError(SPattern pattern){
		String message = "DTD compatibility error. Attribute definition <"+qName+"> at "+getLocation(location)+" cannot have a default value."
                        +" Context dependent datatype at <"+pattern.getQName()+"> at "+pattern.getLocation(restrictToFileName)+".";
        errorMessages.add(message);
    } 
    
    private String getLocation(String location){     
        if(location == null || !restrictToFileName)return location;
        int nameIndex = location.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = location.lastIndexOf('/')+1;
        return location.substring(nameIndex);	
    }
}
