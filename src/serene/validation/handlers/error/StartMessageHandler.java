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

import serene.util.IntList;

class StartMessageHandler implements MessageReporter{
    
    AElement start;
    
    String unknownFoundQName;
    
    String unexpectedFoundQName;
    SimplifiedComponent unexpectedFoundDef;
    
    String ambiguousUnexpectedFoundQName;
    SimplifiedComponent[] ambiguousUnexpectedFoundDef;
    
    String[] unresolvedAmbiguousElementQNameEE;
	String[] unresolvedAmbiguousElementSystemIdEE;
	int[] unresolvedAmbiguousElementLineNumberEE;
	int[] unresolvedAmbiguousElementColumnNumberEE;
	AElement[][] unresolvedAmbiguousElementDefinitionEE;
	int unresolvedAmbiguousElementIndexEE;
	int unresolvedAmbiguousElementSizeEE;
	
	String[] unresolvedUnresolvedElementQNameEE;
	String[] unresolvedUnresolvedElementSystemIdEE;
	int[] unresolvedUnresolvedElementLineNumberEE;
	int[] unresolvedUnresolvedElementColumnNumberEE;
	AElement[][] unresolvedUnresolvedElementDefinitionEE;
	int unresolvedUnresolvedElementIndexEE;
	int unresolvedUnresolvedElementSizeEE;
    
    String[] ambiguousUnresolvedElementQNameWW;
	String[] ambiguousUnresolvedElementSystemIdWW;
	int[] ambiguousUnresolvedElementLineNumberWW;
	int[] ambiguousUnresolvedElementColumnNumberWW;
	AElement[][] ambiguousUnresolvedElementDefinitionWW;
	int ambiguousUnresolvedElementIndexWW;
	int ambiguousUnresolvedElementSizeWW;
	
	String[] ambiguousAmbiguousElementQNameWW;
	String[] ambiguousAmbiguousElementSystemIdWW;
	int[] ambiguousAmbiguousElementLineNumberWW;
	int[] ambiguousAmbiguousElementColumnNumberWW;
	AElement[][] ambiguousAmbiguousElementDefinitionWW;
	int ambiguousAmbiguousElementIndexWW;
	int ambiguousAmbiguousElementSizeWW;
    
    
    MessageWriter debugWriter;
    public StartMessageHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;        
	}
    
	public void setDiscarded(boolean isDiscarded){
        throw new IllegalStateException();        
    }
    
    public void clear(){
        clearUnknownElement();
        clearUnexpectedElement();
        clearUnexpectedAmbiguousElement();
        clearUnresolvedAmbiguousElementContentError();
        clearUnresolvedUnresolvedElementContentError();
        
        clearAmbiguousUnresolvedElementContentWarning();
        clearAmbiguousAmbiguousElementContentWarning();
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

    void unresolvedAmbiguousElementContentError(
                                    String qName, 
									String systemId, 
									int lineNumber, 
									int columnNumber, 
									AElement[] possibleDefinitions){

		if(unresolvedAmbiguousElementSizeEE == 0){
			unresolvedAmbiguousElementSizeEE = 1;
			unresolvedAmbiguousElementIndexEE = 0;	
			unresolvedAmbiguousElementQNameEE = new String[unresolvedAmbiguousElementSizeEE];			
			unresolvedAmbiguousElementSystemIdEE = new String[unresolvedAmbiguousElementSizeEE];			
			unresolvedAmbiguousElementLineNumberEE = new int[unresolvedAmbiguousElementSizeEE];
			unresolvedAmbiguousElementColumnNumberEE = new int[unresolvedAmbiguousElementSizeEE];
			unresolvedAmbiguousElementDefinitionEE = new AElement[unresolvedAmbiguousElementSizeEE][];
		}else if(++unresolvedAmbiguousElementIndexEE == unresolvedAmbiguousElementSizeEE){			
			String[] increasedQN = new String[++unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementQNameEE, 0, increasedQN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[unresolvedAmbiguousElementSizeEE][];
			System.arraycopy(unresolvedAmbiguousElementDefinitionEE, 0, increasedDef, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementSystemIdEE, 0, increasedSI, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementLineNumberEE, 0, increasedLN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[unresolvedAmbiguousElementSizeEE];
			System.arraycopy(unresolvedAmbiguousElementColumnNumberEE, 0, increasedCN, 0, unresolvedAmbiguousElementIndexEE);
			unresolvedAmbiguousElementColumnNumberEE = increasedCN;
            
		}
		unresolvedAmbiguousElementQNameEE[unresolvedAmbiguousElementIndexEE] = qName;		
		unresolvedAmbiguousElementSystemIdEE[unresolvedAmbiguousElementIndexEE] = systemId;
		unresolvedAmbiguousElementLineNumberEE[unresolvedAmbiguousElementIndexEE] = lineNumber;
		unresolvedAmbiguousElementColumnNumberEE[unresolvedAmbiguousElementIndexEE] = columnNumber;
		unresolvedAmbiguousElementDefinitionEE[unresolvedAmbiguousElementIndexEE] = possibleDefinitions;
		
	}
	void clearUnresolvedAmbiguousElementContentError(){
        unresolvedAmbiguousElementSizeEE = 0;
        unresolvedAmbiguousElementIndexEE = -1;	
        unresolvedAmbiguousElementQNameEE = null;			
        unresolvedAmbiguousElementSystemIdEE = null;			
        unresolvedAmbiguousElementLineNumberEE = null;
        unresolvedAmbiguousElementColumnNumberEE = null;
        unresolvedAmbiguousElementDefinitionEE = null;
    }
    
    
    void unresolvedUnresolvedElementContentError(String qName,
                                    String systemId,
                                    int lineNumber,
                                    int columnNumber, 
									AElement[] possibleDefinitions){
		if(unresolvedUnresolvedElementSizeEE == 0){
			unresolvedUnresolvedElementSizeEE = 1;
			unresolvedUnresolvedElementIndexEE = 0;	
			unresolvedUnresolvedElementQNameEE = new String[unresolvedUnresolvedElementSizeEE];			
			unresolvedUnresolvedElementSystemIdEE = new String[unresolvedUnresolvedElementSizeEE];			
			unresolvedUnresolvedElementLineNumberEE = new int[unresolvedUnresolvedElementSizeEE];
			unresolvedUnresolvedElementColumnNumberEE = new int[unresolvedUnresolvedElementSizeEE];
			unresolvedUnresolvedElementDefinitionEE = new AElement[unresolvedUnresolvedElementSizeEE][];
		}else if(++unresolvedUnresolvedElementIndexEE == unresolvedUnresolvedElementSizeEE){			
			String[] increasedQN = new String[++unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementQNameEE, 0, increasedQN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementQNameEE = increasedQN;
			
			AElement[][] increasedDef = new AElement[unresolvedUnresolvedElementSizeEE][];
			System.arraycopy(unresolvedUnresolvedElementDefinitionEE, 0, increasedDef, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementDefinitionEE = increasedDef;
			
			String[] increasedSI = new String[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementSystemIdEE, 0, increasedSI, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementSystemIdEE = increasedSI;
						
			int[] increasedLN = new int[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementLineNumberEE, 0, increasedLN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementLineNumberEE = increasedLN;
			
			int[] increasedCN = new int[unresolvedUnresolvedElementSizeEE];
			System.arraycopy(unresolvedUnresolvedElementColumnNumberEE, 0, increasedCN, 0, unresolvedUnresolvedElementIndexEE);
			unresolvedUnresolvedElementColumnNumberEE = increasedCN;
		}
		unresolvedUnresolvedElementQNameEE[unresolvedUnresolvedElementIndexEE] = qName;		
		unresolvedUnresolvedElementSystemIdEE[unresolvedUnresolvedElementIndexEE] = systemId;
		unresolvedUnresolvedElementLineNumberEE[unresolvedUnresolvedElementIndexEE] = lineNumber;
		unresolvedUnresolvedElementColumnNumberEE[unresolvedUnresolvedElementIndexEE] = columnNumber;
		unresolvedUnresolvedElementDefinitionEE[unresolvedUnresolvedElementIndexEE] = possibleDefinitions;
		
	}
	void clearUnresolvedUnresolvedElementContentError(){
        unresolvedUnresolvedElementSizeEE = 0;
        unresolvedUnresolvedElementIndexEE = -1;	
        unresolvedUnresolvedElementQNameEE = null;			
        unresolvedUnresolvedElementSystemIdEE = null;			
        unresolvedUnresolvedElementLineNumberEE = null;
        unresolvedUnresolvedElementColumnNumberEE = null;
        unresolvedUnresolvedElementDefinitionEE = null;
        
    }
    
    
    void ambiguousUnresolvedElementContentWarning(String qName,
                                    String systemId,
                                    int lineNumber,
                                    int columnNumber, 
									AElement[] possibleDefinitions){
		if(ambiguousUnresolvedElementSizeWW == 0){
			ambiguousUnresolvedElementSizeWW = 1;
			ambiguousUnresolvedElementIndexWW = 0;	
			ambiguousUnresolvedElementQNameWW = new String[ambiguousUnresolvedElementSizeWW];			
			ambiguousUnresolvedElementSystemIdWW = new String[ambiguousUnresolvedElementSizeWW];			
			ambiguousUnresolvedElementLineNumberWW = new int[ambiguousUnresolvedElementSizeWW];
			ambiguousUnresolvedElementColumnNumberWW = new int[ambiguousUnresolvedElementSizeWW];
			ambiguousUnresolvedElementDefinitionWW = new AElement[ambiguousUnresolvedElementSizeWW][];
		}else if(++ambiguousUnresolvedElementIndexWW == ambiguousUnresolvedElementSizeWW){			
			String[] increasedQN = new String[++ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementQNameWW, 0, increasedQN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousUnresolvedElementSizeWW][];
			System.arraycopy(ambiguousUnresolvedElementDefinitionWW, 0, increasedDef, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementSystemIdWW, 0, increasedSI, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementLineNumberWW, 0, increasedLN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousUnresolvedElementSizeWW];
			System.arraycopy(ambiguousUnresolvedElementColumnNumberWW, 0, increasedCN, 0, ambiguousUnresolvedElementIndexWW);
			ambiguousUnresolvedElementColumnNumberWW = increasedCN;
		}
		ambiguousUnresolvedElementQNameWW[ambiguousUnresolvedElementIndexWW] = qName;		
		ambiguousUnresolvedElementSystemIdWW[ambiguousUnresolvedElementIndexWW] = systemId;
		ambiguousUnresolvedElementLineNumberWW[ambiguousUnresolvedElementIndexWW] = lineNumber;
		ambiguousUnresolvedElementColumnNumberWW[ambiguousUnresolvedElementIndexWW] = columnNumber;
		ambiguousUnresolvedElementDefinitionWW[ambiguousUnresolvedElementIndexWW] = possibleDefinitions;        
	}
    void clearAmbiguousUnresolvedElementContentWarning(){
        ambiguousUnresolvedElementSizeWW = 0;
        ambiguousUnresolvedElementIndexWW = -1;	
        ambiguousUnresolvedElementQNameWW = null;			
        ambiguousUnresolvedElementSystemIdWW = null;			
        ambiguousUnresolvedElementLineNumberWW = null;
        ambiguousUnresolvedElementColumnNumberWW = null;
        ambiguousUnresolvedElementDefinitionWW = null;
    }
    
    
    void ambiguousAmbiguousElementContentWarning(String qName,
                                    String systemId,
                                    int lineNumber,
                                    int columnNumber,
									AElement[] possibleDefinitions){
		if(ambiguousAmbiguousElementSizeWW == 0){
			ambiguousAmbiguousElementSizeWW = 1;
			ambiguousAmbiguousElementIndexWW = 0;	
			ambiguousAmbiguousElementQNameWW = new String[ambiguousAmbiguousElementSizeWW];			
			ambiguousAmbiguousElementSystemIdWW = new String[ambiguousAmbiguousElementSizeWW];			
			ambiguousAmbiguousElementLineNumberWW = new int[ambiguousAmbiguousElementSizeWW];
			ambiguousAmbiguousElementColumnNumberWW = new int[ambiguousAmbiguousElementSizeWW];
			ambiguousAmbiguousElementDefinitionWW = new AElement[ambiguousAmbiguousElementSizeWW][];
		}else if(++ambiguousAmbiguousElementIndexWW == ambiguousAmbiguousElementSizeWW){			
			String[] increasedQN = new String[++ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementQNameWW, 0, increasedQN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementQNameWW = increasedQN;
			
			AElement[][] increasedDef = new AElement[ambiguousAmbiguousElementSizeWW][];
			System.arraycopy(ambiguousAmbiguousElementDefinitionWW, 0, increasedDef, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementDefinitionWW = increasedDef;
			
			String[] increasedSI = new String[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementSystemIdWW, 0, increasedSI, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementSystemIdWW = increasedSI;
						
			int[] increasedLN = new int[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementLineNumberWW, 0, increasedLN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementLineNumberWW = increasedLN;
			
			int[] increasedCN = new int[ambiguousAmbiguousElementSizeWW];
			System.arraycopy(ambiguousAmbiguousElementColumnNumberWW, 0, increasedCN, 0, ambiguousAmbiguousElementIndexWW);
			ambiguousAmbiguousElementColumnNumberWW = increasedCN;
		}
		ambiguousAmbiguousElementQNameWW[ambiguousAmbiguousElementIndexWW] = qName;		
		ambiguousAmbiguousElementSystemIdWW[ambiguousAmbiguousElementIndexWW] = systemId;
		ambiguousAmbiguousElementLineNumberWW[ambiguousAmbiguousElementIndexWW] = lineNumber;
		ambiguousAmbiguousElementColumnNumberWW[ambiguousAmbiguousElementIndexWW] = columnNumber;
		ambiguousAmbiguousElementDefinitionWW[ambiguousAmbiguousElementIndexWW] = possibleDefinitions;        
	}
    void clearAmbiguousAmbiguousElementContentWarning(){
        ambiguousAmbiguousElementSizeWW = 0;
        ambiguousAmbiguousElementIndexWW = -1;	
        ambiguousAmbiguousElementQNameWW = null;			
        ambiguousAmbiguousElementSystemIdWW = null;			
        ambiguousAmbiguousElementLineNumberWW = null;
        ambiguousAmbiguousElementColumnNumberWW = null;
        ambiguousAmbiguousElementDefinitionWW = null;
    }
    
    public boolean containsErrorMessage(){
        if(unknownFoundQName != null
            || unexpectedFoundQName != null
            || ambiguousUnexpectedFoundQName != null
            || unresolvedAmbiguousElementQNameEE != null
            || unresolvedUnresolvedElementQNameEE != null) return true;
        return false;
    }    
    
    public boolean containsOtherErrorMessage(IntList exceptedErrorIds, IntList exceptedErrorCodes){
        throw new IllegalStateException();
    }
    
    public ConflictMessageReporter getConflictMessageReporter(ErrorDispatcher errorDispatcher){
        throw new IllegalStateException();
    } 
    public void setConflictResolutionId(int conflictResolutionId){
        throw new IllegalStateException(); 
    }
	
    public int getConflictResolutionId(){
        throw new IllegalStateException();
    }
    
    public void setReportingContextType(int contextType){
        throw new IllegalStateException();
    }    
    
	public void setReportingContextQName(String qName){
		throw new IllegalStateException();
	}	
	public void setReportingContextLocation(String publicId, String systemId, int lineNumber, int columnNumber){
		throw new IllegalStateException();		
	}
	public void setReportingContextDefinition(AElement definition){
		throw new IllegalStateException();
	}
    public void setRestrictToFileName(boolean restrictToFileName){
        throw new IllegalStateException(); 
    }
    
    public void setParent(MessageReporter parent){
        throw new IllegalStateException();
    }
    
    public void report(int contextType, String qName, AElement definition, boolean restrictToFileName, Locator locator, ErrorDispatcher errorDispatcher) throws SAXException{
        start = definition;
        
        String errorMessage = getValidationErrorMessage("", restrictToFileName);
        if(errorMessage != null){
            errorMessage = errorMessage.trim();
            if(!errorMessage.equals("")) errorDispatcher.error(new SAXParseException(errorMessage, locator));
        }
        
        String warningMessage = getValidationWarningMessage("", restrictToFileName);
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
        //isMessageRetrieved = true;
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
       
		if(unresolvedAmbiguousElementQNameEE != null){
			for(int i = 0; i <= unresolvedAmbiguousElementIndexEE; i++){
				message += "\n"+prefix+"Unresolved element."
						+"\n"+prefix+"Element <"+unresolvedAmbiguousElementQNameEE[i] + "> at "+getLocation(restrictToFileName, unresolvedAmbiguousElementSystemIdEE[i])+":"+unresolvedAmbiguousElementLineNumberEE[i]+":"+unresolvedAmbiguousElementColumnNumberEE[i]
						+", ambiguous after content validation, cannot be resolved by in context validation either, all candidates resulted in errors. Possible definitions: ";
				for(int j = 0; j < unresolvedAmbiguousElementDefinitionEE[i].length; j++){
					message += "\n"+prefix+"<"+unresolvedAmbiguousElementDefinitionEE[i][j].getQName()+"> at "+unresolvedAmbiguousElementDefinitionEE[i][j].getLocation(restrictToFileName);
				}
				message += ".";
			}
			return getErrorIntro(prefix, restrictToFileName) + message;
		}
		// {12 U}
		if(unresolvedUnresolvedElementQNameEE != null){
			for(int i = 0; i <= unresolvedUnresolvedElementIndexEE; i++){
				message += "\n"+prefix+"Unresolved element."
						+"\n"+prefix+"Element <"+unresolvedUnresolvedElementQNameEE[i] + "> at "+getLocation(restrictToFileName, unresolvedUnresolvedElementSystemIdEE[i])+":"+unresolvedUnresolvedElementLineNumberEE[i]+":"+unresolvedUnresolvedElementColumnNumberEE[i]
						+", unresolved by content validation, cannot be resolved by in context validation either, all candidates resulted in errors.";
				
			}
			return getErrorIntro(prefix, restrictToFileName) + message;
		}
        return null;
    }
    
    String getValidationWarningMessage(String prefix, boolean restrictToFileName){
		String message = "";
		// {w1}
		if(ambiguousUnresolvedElementQNameWW != null){
			for(int i = 0; i <= ambiguousUnresolvedElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+ambiguousUnresolvedElementQNameWW[i] + "> at "+getLocation(restrictToFileName, ambiguousUnresolvedElementSystemIdWW[i])+":"+ambiguousUnresolvedElementLineNumberWW[i]+":"+ambiguousUnresolvedElementColumnNumberWW[i]
						+", unresolved by content validation, cannot be resolved by in context validation, all candidates could be correct.";				
			}
            return getWarningIntro(prefix, restrictToFileName) + message;
		}
		
		if(ambiguousAmbiguousElementQNameWW != null){
			for(int i = 0; i <= ambiguousAmbiguousElementIndexWW; i++){
				message += "\n"+prefix+"Ambiguous element."
						+"\n"+prefix+"Element <"+ambiguousAmbiguousElementQNameWW[i] + "> at "+getLocation(restrictToFileName, ambiguousAmbiguousElementSystemIdWW[i])+":"+ambiguousAmbiguousElementLineNumberWW[i]+":"+ambiguousAmbiguousElementColumnNumberWW[i]
						+", ambiguous after content validation, cannot be desambiguated by in context validation, several candidates could be correct. Possible definitions:";
				for(int j = 0; j < ambiguousAmbiguousElementDefinitionWW[i].length; j++){
					message += "\n"+prefix+"<"+ambiguousAmbiguousElementDefinitionWW[i][j].getQName()+"> at "+ambiguousAmbiguousElementDefinitionWW[i][j].getLocation(restrictToFileName);
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
