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

import java.io.File;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;

import serene.validation.schema.simplified.SimplifiedComponent;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AElement;

import sereneWrite.MessageWriter;

class StartMessageHandler implements MessageReporter{
    
    AElement start;
    
    String unknownFoundQName;
    
    String unexpectedFoundQName;
    SimplifiedComponent unexpectedFoundDef;
    
    String ambiguousUnexpectedFoundQName;
    SimplifiedComponent[] ambiguousUnexpectedFoundDef;
    
    String[] ambiguousElementQNameEE;
	String[] ambiguousElementSystemIdEE;
	int[] ambiguousElementLineNumberEE;
	int[] ambiguousElementColumnNumberEE;
	AElement[][] ambiguousElementDefinitionEE;
	int ambiguousElementIndexEE;
	int ambiguousElementSizeEE;
    
    String[] ambiguousElementQNameWW;
	String[] ambiguousElementSystemIdWW;
	int[] ambiguousElementLineNumberWW;
	int[] ambiguousElementColumnNumberWW;
	AElement[][] ambiguousElementDefinitionWW;
	int ambiguousElementIndexWW;
	int ambiguousElementSizeWW;
    
    
    MessageWriter debugWriter;
    public StartMessageHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;        
	}
    
    void clear(){
        clearUnknownElement();
        clearUnexpectedElement();
        clearUnexpectedAmbiguousElement();
        clearAmbiguousElementContentError();
        
        clearAmbiguousElementContentWarning();
    }
    void unknownElement(String foundQName){
        unknownFoundQName = foundQName;
    }
    void clearUnknownElement(){
        unknownFoundQName = null;
    }
    
    void unexpectedElement(String foundQName, SimplifiedComponent foundDefinition){
        unexpectedFoundQName = foundQName;
        unexpectedFoundDef = foundDefinition;
    }
    void clearUnexpectedElement(){
        unexpectedFoundQName = null;
        unexpectedFoundDef = null;
    }
    
    void unexpectedAmbiguousElement(String foundQName, SimplifiedComponent[] foundDefinition){
        ambiguousUnexpectedFoundQName = foundQName;
        ambiguousUnexpectedFoundDef = foundDefinition;
    }
    void clearUnexpectedAmbiguousElement(){
        ambiguousUnexpectedFoundQName = null;
        ambiguousUnexpectedFoundDef = null;
    }

    void ambiguousElementContentError(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
		if(ambiguousElementSizeEE == 0){
			ambiguousElementSizeEE = 1;
			ambiguousElementIndexEE = 0;	
			ambiguousElementQNameEE = new String[ambiguousElementSizeEE];			
			ambiguousElementSystemIdEE = new String[ambiguousElementSizeEE];			
			ambiguousElementLineNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementColumnNumberEE = new int[ambiguousElementSizeEE];
			ambiguousElementDefinitionEE = new AElement[ambiguousElementSizeEE][];
		}else if(++ambiguousElementIndexEE == ambiguousElementSizeEE){			
			String[] increasedQN = new String[++ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementQNameEE, 0, increasedQN, 0, ambiguousElementIndexEE);
			ambiguousElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousElementSizeEE][];
			System.arraycopy(ambiguousElementDefinitionEE, 0, increasedDef, 0, ambiguousElementIndexEE);
			ambiguousElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementSystemIdEE, 0, increasedSI, 0, ambiguousElementIndexEE);
			ambiguousElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementLineNumberEE, 0, increasedLN, 0, ambiguousElementIndexEE);
			ambiguousElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[ambiguousElementSizeEE];
			System.arraycopy(ambiguousElementColumnNumberEE, 0, increasedCN, 0, ambiguousElementIndexEE);
			ambiguousElementColumnNumberEE = increasedCN;
		}
		ambiguousElementQNameEE[ambiguousElementIndexEE] = qName;		
		ambiguousElementSystemIdEE[ambiguousElementIndexEE] = systemId;
		ambiguousElementLineNumberEE[ambiguousElementIndexEE] = lineNumber;
		ambiguousElementColumnNumberEE[ambiguousElementIndexEE] = columnNumber;
		ambiguousElementDefinitionEE[ambiguousElementIndexEE] = possibleDefinitions;
	}
    void clearAmbiguousElementContentError(){
        ambiguousElementSizeEE = 0;
        ambiguousElementIndexEE = -1;	
        ambiguousElementQNameEE = null;			
        ambiguousElementSystemIdEE = null;			
        ambiguousElementLineNumberEE = null;
        ambiguousElementColumnNumberEE = null;
        ambiguousElementDefinitionEE = null;
    }
    
    void ambiguousElementContentWarning(String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){
		if(ambiguousElementSizeWW == 0){
			ambiguousElementSizeWW = 1;
			ambiguousElementIndexWW = 0;	
			ambiguousElementQNameWW = new String[ambiguousElementSizeWW];			
			ambiguousElementSystemIdWW = new String[ambiguousElementSizeWW];			
			ambiguousElementLineNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementColumnNumberWW = new int[ambiguousElementSizeWW];
			ambiguousElementDefinitionWW = new AElement[ambiguousElementSizeWW][];
		}else if(++ambiguousElementIndexWW == ambiguousElementSizeWW){			
			String[] increasedQN = new String[++ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementQNameWW, 0, increasedQN, 0, ambiguousElementIndexWW);
			ambiguousElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousElementSizeWW][];
			System.arraycopy(ambiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousElementIndexWW);
			ambiguousElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementSystemIdWW, 0, increasedSI, 0, ambiguousElementIndexWW);
			ambiguousElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementLineNumberWW, 0, increasedLN, 0, ambiguousElementIndexWW);
			ambiguousElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousElementSizeWW];
			System.arraycopy(ambiguousElementColumnNumberWW, 0, increasedCN, 0, ambiguousElementIndexWW);
			ambiguousElementColumnNumberWW = increasedCN;
		}
		ambiguousElementQNameWW[ambiguousElementIndexWW] = qName;		
		ambiguousElementSystemIdWW[ambiguousElementIndexWW] = systemId;
		ambiguousElementLineNumberWW[ambiguousElementIndexWW] = lineNumber;
		ambiguousElementColumnNumberWW[ambiguousElementIndexWW] = columnNumber;
		ambiguousElementDefinitionWW[ambiguousElementIndexWW] = possibleDefinitions;        
	}
    void clearAmbiguousElementContentWarning(){
        ambiguousElementSizeWW = 0;
        ambiguousElementIndexWW = -1;	
        ambiguousElementQNameWW = null;			
        ambiguousElementSystemIdWW = null;			
        ambiguousElementLineNumberWW = null;
        ambiguousElementColumnNumberWW = null;
        ambiguousElementDefinitionWW = null;
    }
    
    public void setConflictResolutionId(int conflictResolutionId){
        throw new IllegalStateException(); 
    }
	
    public void setContextType(int contextType){
        throw new IllegalStateException();
    }    
    
	public void setContextQName(String qName){
		throw new IllegalStateException();
	}	
	public void setContextLocation(String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();		
	}
	public void setContextDefinition(AElement definition){
		throw new IllegalStateException();
	}
     
    
    public void setParent(MessageReporter parent){
        throw new IllegalStateException();
    }
    
    public void report(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        start = definition;
        
        String errorMessage = getValidationErrorMessage(prefix, restrictToFileName);
        if(errorMessage != null){
            errorMessage = errorMessage.trim();
            if(!errorMessage.equals("")) errorDispatcher.error(new SAXParseException(errorMessage, locator));
        }
        
        String warningMessage = getValidationWarningMessage(prefix, restrictToFileName);
        if(warningMessage != null){
            warningMessage = warningMessage.trim();
            if(!warningMessage.equals("")) errorDispatcher.warning(new SAXParseException(warningMessage, locator));
        }
    }
    
    public void report(boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher, String prefix) throws SAXException{
        throw new IllegalStateException();
    }
    public String getCandidateErrorMessage(String prefix, boolean restrictToFileName){
        throw new IllegalStateException();
    }
    
    public String getErrorMessage(String prefix, boolean restrictToFileName){
        throw new IllegalStateException();
    }
    
    
    String getValidationErrorMessage(String prefix, boolean restrictToFileName){
        String message = "";
        if(unknownFoundQName != null){
            message += "\n"+prefix+"Unknown element."
						+"\n"+prefix+"Expected element corresponding to schema component <"+start.getChild().getQName()+"> at "+start.getChild().getLocation(restrictToFileName)
                        +", found <"+unknownFoundQName+">.";
            return getErrorIntro(prefix, restrictToFileName) + message;
        }
        if(unexpectedFoundQName != null){            
            message += "\n"+prefix+"Unexpected element."
						+"\n"+prefix+"Expected element corresponding to schema component <"+start.getChild().getQName()+"> at "+start.getChild().getLocation(restrictToFileName)
                        +", found <"+unexpectedFoundQName+"> corresponding to schema definition <"+unexpectedFoundDef.getQName()+"> at "+unexpectedFoundDef.getLocation(restrictToFileName)+".";
            return getErrorIntro(prefix, restrictToFileName) + message;
        }
        if(ambiguousUnexpectedFoundQName != null){
            String defs = "";
            for(SimplifiedComponent foundDef : ambiguousUnexpectedFoundDef){
                defs += "\n"+prefix+"<"+foundDef.getQName()+"> at "+foundDef.getLocation(restrictToFileName);
            }
            message += "\n"+prefix+"Unexpected ambiguous element."
						+"\n"+prefix+"Expected element corresponding to schema component <"+start.getChild().getQName()+"> at "+start.getChild().getLocation(restrictToFileName)
                        +", found <"+ambiguousUnexpectedFoundQName+"> corresponding to one of schema definitions:"
                        +defs+".";
            return getErrorIntro(prefix, restrictToFileName) + message;
        }
        if(ambiguousElementQNameEE != null){
			for(int i = 0; i <= ambiguousElementIndexEE; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+ambiguousElementQNameEE[i] + "> at "+getLocation(restrictToFileName, ambiguousElementSystemIdEE[i])+":"+ambiguousElementLineNumberEE[i]+":"+ambiguousElementColumnNumberEE[i]
						+" cannot be resolved by in context validation, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < ambiguousElementDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousElementDefinitionEE[i][j].getQName()+"> at "+ambiguousElementDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
            return getErrorIntro(prefix, restrictToFileName) + message;
		}
        return null;
    }
    
    String getValidationWarningMessage(String prefix, boolean restrictToFileName){
		String message = "";
		// {w1}
		if(ambiguousElementQNameWW != null){
			for(int i = 0; i <= ambiguousElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous content."
						+"\n"+prefix+"Element <"+ambiguousElementQNameWW[i] + "> at "+getLocation(restrictToFileName, ambiguousElementSystemIdWW[i])+":"+ambiguousElementLineNumberWW[i]+":"+ambiguousElementColumnNumberWW[i]
						+" cannot be resolved by in context validation, possible definitions: ";
				for(int j = 0; j < ambiguousElementDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousElementDefinitionWW[i][j].getQName()+"> at "+ambiguousElementDefinitionWW[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
            return getWarningIntro(prefix, restrictToFileName) + message;
		}
		return null;
    }
    
    private String getLocation(boolean restrictToFileName, String systemId){     
        if(systemId == null || !restrictToFileName)return systemId;
        int nameIndex = systemId.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = systemId.lastIndexOf('/')+1;
        return systemId.substring(nameIndex);	
    }
    
    String getErrorIntro(String prefix, boolean restrictToFileName){
        String intro = "\n"+prefix+"Syntax error. Error at the root of the document: ";
        return intro;
    }
    
    String getWarningIntro(String prefix, boolean restrictToFileName){        
        String intro = "\n"+prefix+"Syntax warning. Warning at the root of the document: ";
        return intro;
    }
}
