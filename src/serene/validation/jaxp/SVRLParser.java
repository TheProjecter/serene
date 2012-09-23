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

package serene.validation.jaxp;

import java.util.List;
import java.util.ArrayList;

import javax.xml.XMLConstants;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.util.CharsBuffer;
import serene.util.SpaceCharsHandler;

import serene.Constants;

import serene.schematron.FailedAssertException;
import serene.schematron.SuccessfulReportException;

class SVRLParser implements ContentHandler{
    final static String ACTIVE_PATTERN = "active-pattern";
    final static String FIRED_RULE = "fired-rule";
    final static String FAILED_ASSERT = "failed-assert";
    final static String SUCCESSFUL_REPORT = "successful-report";
    final static String TEXT = "text";
    final static String DIAGNOSTIC_REFERENCE = "diagnostic-reference";
    
    final static String ID = "id";
    final static String NAME = "name";
    final static String CONTEXT = "context";
    final static String LOCATION = "location";
    final static String TEST = "test";
    final static String ROLE = "role";
    final static String FLAG = "flag";
    final static String DIAGNOSTIC = "diagnostic";
    
    
    String activePatternName;
    String activePatternId;
    String activePatternRole;
        
    String firedRuleId;
    String firedRuleContext;
    String firedRuleRole;
    String firedRuleFlag;
        
    String id;
    String location;
    String test;
    String role;
    String flag;
    
    String text;
       
    List<String> diagnostics;
    List<String> diagnosticTexts;
    
    
    boolean failedAssertContext;
    CharsBuffer failedAssertCharsBuffer;
    
    boolean successfulReportContext;
    CharsBuffer successfulReportCharsBuffer;
    
    boolean diagnosticReferenceContext;
    CharsBuffer diagnosticReferenceCharsBuffer;
    
    SpaceCharsHandler spaceCharsHandler;
       
    Locator locator;
    ErrorHandler errorHandler;
    
    boolean acceptLocator;
    
    SVRLParser(){
        
        failedAssertContext = false;
        failedAssertCharsBuffer = new CharsBuffer();
        
        successfulReportContext = false;
        successfulReportCharsBuffer = new CharsBuffer();
    
        diagnosticReferenceContext = false;
        diagnosticReferenceCharsBuffer = new CharsBuffer();
        
        spaceCharsHandler = new SpaceCharsHandler();
        
        diagnostics = new ArrayList<String>();
        diagnosticTexts = new ArrayList<String>();
        
        acceptLocator = true;
    }
    
    void setAcceptLocator(boolean acceptLocator){
        this.acceptLocator = acceptLocator;
    }
    
    public ErrorHandler getErrorHandler(){
		return errorHandler;
	}
	
	public void setErrorHandler(ErrorHandler errorHandler){		
		this.errorHandler = errorHandler;		
	}
	
	public void processingInstruction(String target, String data) throws SAXException{}
	public void skippedEntity(String name) throws SAXException{}
	public void ignorableWhitespace(char[] ch, int start, int len) throws SAXException{}
	public void startDocument()  throws SAXException{}
	public void startPrefixMapping(String prefix, String uri)  throws SAXException{}	
	public void endPrefixMapping(String prefix)  throws SAXException{}	
	public void setDocumentLocator(Locator locator){
	    if(!acceptLocator) return; // workaround for the extra locator introduced by saxon	    
	    this.locator = locator;
	}
	public void characters(char[] chars, int start, int length)throws SAXException{		
        if(diagnosticReferenceContext)diagnosticReferenceCharsBuffer.append(chars, start, length); 
        else if(failedAssertContext)failedAssertCharsBuffer.append(chars, start, length);
        else if(successfulReportContext)successfulReportCharsBuffer.append(chars, start, length);
	}
	public void startElement(String namespaceURI, 
							String localName, 
							String qName, 
							Attributes attributes) throws SAXException{
	    if(namespaceURI.equals(Constants.SVRL_NS_URI)){
	        if(localName.equals(ACTIVE_PATTERN)){
	            activePatternName = attributes.getValue(XMLConstants.NULL_NS_URI, NAME);
	            activePatternId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            activePatternId = attributes.getValue(XMLConstants.NULL_NS_URI, ROLE);
	            // System.out.println("ACTIVE_PATTERN  activePatternName="+activePatternName+"  activePatternId="+activePatternId);
	        }else if(localName.equals(FIRED_RULE)){
	            firedRuleId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            firedRuleContext = attributes.getValue(XMLConstants.NULL_NS_URI, CONTEXT);
	            firedRuleRole = attributes.getValue(XMLConstants.NULL_NS_URI, ROLE);
	            firedRuleFlag = attributes.getValue(XMLConstants.NULL_NS_URI, FLAG);
	            // System.out.println("FIRED_RULE  firedRuleId="+firedRuleId+"  firedRuleContext="+firedRuleContext);
	        }else if(localName.equals(FAILED_ASSERT)){
	            // set message by calling failed assert
	            failedAssertContext = true;
	            id = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            location = attributes.getValue(XMLConstants.NULL_NS_URI, LOCATION);
	            test = attributes.getValue(XMLConstants.NULL_NS_URI, TEST);
	            role = attributes.getValue(XMLConstants.NULL_NS_URI, ROLE);
	            flag = attributes.getValue(XMLConstants.NULL_NS_URI, FLAG);
	            // System.out.println("FAILED_ASSERT  id="+id+"  location="+location+"  test="+test);
	        }else if(localName.equals(SUCCESSFUL_REPORT)){
	            // set message by calling failed assert
	            successfulReportContext = true;
	            id = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            location = attributes.getValue(XMLConstants.NULL_NS_URI, LOCATION);
	            test = attributes.getValue(XMLConstants.NULL_NS_URI, TEST);
	            role = attributes.getValue(XMLConstants.NULL_NS_URI, ROLE);
	            flag = attributes.getValue(XMLConstants.NULL_NS_URI, FLAG);
	        }else if(localName.equals(DIAGNOSTIC_REFERENCE)){
	            diagnosticReferenceContext = true;
	            //setDiagnostics(attributes.getValue(XMLConstants.NULL_NS_URI, DIAGNOSTIC));
	            diagnostics.add(attributes.getValue(XMLConstants.NULL_NS_URI, DIAGNOSTIC));
	        }   
	           
	    }
	}		
		
	/*void setDiagnostics(String diagnostic){
	    if(diagnostics == null){
	        diagnostics = "Diagnostics: "+diagnostic;
	    }else{
	        diagnostics += ("; "+diagnostic);
	    }
	}*/
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
	    if(namespaceURI.equals(Constants.SVRL_NS_URI)){
	        if(localName.equals(DIAGNOSTIC_REFERENCE)){
	            diagnosticTexts.add(new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(diagnosticReferenceCharsBuffer.getCharsArray()))));
	            diagnosticReferenceCharsBuffer.clear();
	            diagnosticReferenceContext = false;
	        }else if(localName.equals(FAILED_ASSERT)){
	            text = new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(failedAssertCharsBuffer.getCharsArray())));
	            failedAssertCharsBuffer.clear();
	            reportAssertError();
	            failedAssertContext = false;
	        }else if(localName.equals(SUCCESSFUL_REPORT)){
	            text = new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(successfulReportCharsBuffer.getCharsArray())));
	            successfulReportCharsBuffer.clear();
	            reportReportError();
	            successfulReportContext = false;
	        }  
	    }
	}
	
	void reportAssertError() throws SAXException{
	    errorHandler.error(new FailedAssertException(activePatternName,
                    activePatternId,
                    activePatternRole,                        
                    firedRuleId,
                    firedRuleContext,
                    firedRuleRole,
                    firedRuleFlag,
                    id,
                    location,
                    test,
                    role,
                    flag,                    
                    text,
                    diagnostics,
                    diagnosticTexts,
                    locator));
	    
	    id = null;
	    location = null;
	    test = null;
	    role = null;
	    flag = null;
	    text = null;
	    diagnostics.clear();
	    diagnosticTexts.clear();
	}
	
	void reportReportError() throws SAXException{
	    errorHandler.error(new SuccessfulReportException(activePatternName,
                    activePatternId,
                    activePatternRole,                        
                    firedRuleId,
                    firedRuleContext,
                    firedRuleRole,
                    firedRuleFlag,
                    id,
                    location,
                    test,
                    role,
                    flag,                    
                    text,
                    diagnostics,
                    diagnosticTexts,
                    locator));
	    
	    id = null;
	    location = null;
	    test = null;
	    role = null;
	    flag = null;
	    text = null;
	    diagnostics.clear();
	    diagnosticTexts.clear();
	}
				
	public void endDocument()  throws SAXException {}
}

