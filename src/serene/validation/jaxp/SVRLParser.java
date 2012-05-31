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
    final static String DIAGNOSTIC = "diagnostic";
    
    String activePatternName;
    String activePatternId;
        
    String firedRuleId;
    String firedRuleContext;
    String failedAssertId;
    String failedAssertLocation;
    String failedAssertTest;
    String successfulReportId;
    String successfulReportLocation;
    String successfulReportTest;
    String diagnostics;
    
    boolean failedAssertContext;
    CharsBuffer failedAssertCharsBuffer;
    
    boolean successfulReportContext;
    CharsBuffer successfulReportCharsBuffer;
    
    boolean diagnosticReferenceContext;
    CharsBuffer diagnosticReferenceCharsBuffer;
    
    SpaceCharsHandler spaceCharsHandler;
       
    Locator locator;
    ErrorHandler errorHandler;

    SVRLParser(){
        
        failedAssertContext = false;
        failedAssertCharsBuffer = new CharsBuffer();
        
        successfulReportContext = false;
        successfulReportCharsBuffer = new CharsBuffer();
    
        diagnosticReferenceContext = false;
        diagnosticReferenceCharsBuffer = new CharsBuffer();
        
        spaceCharsHandler = new SpaceCharsHandler();
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
	            if(activePatternName == null)activePatternId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	        }else if(localName.equals(FIRED_RULE)){
	            firedRuleId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            firedRuleContext = attributes.getValue(XMLConstants.NULL_NS_URI, CONTEXT);
	        }else if(localName.equals(FAILED_ASSERT)){
	            // set message by calling failed assert
	            failedAssertContext = true;
	            failedAssertId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            failedAssertLocation = attributes.getValue(XMLConstants.NULL_NS_URI, LOCATION);
	            failedAssertTest = attributes.getValue(XMLConstants.NULL_NS_URI, TEST);
	        }else if(localName.equals(SUCCESSFUL_REPORT)){
	            // set message by calling failed assert
	            successfulReportContext = true;
	            successfulReportId = attributes.getValue(XMLConstants.NULL_NS_URI, ID);
	            successfulReportLocation = attributes.getValue(XMLConstants.NULL_NS_URI, LOCATION);
	            successfulReportTest = attributes.getValue(XMLConstants.NULL_NS_URI, TEST);
	        }else if(localName.equals(DIAGNOSTIC_REFERENCE)){
	            diagnosticReferenceContext = true;
	            setDiagnostics(attributes.getValue(XMLConstants.NULL_NS_URI, DIAGNOSTIC));
	        }   
	           
	    }
	}		
		
	void setDiagnostics(String diagnostic){
	    if(diagnostics == null){
	        diagnostics = "Diagnostics: "+diagnostic;
	    }else{
	        diagnostics += ("; "+diagnostic);
	    }
	}
	
	public void endElement(String namespaceURI, 
							String localName, 
							String qName) throws SAXException{
	    if(namespaceURI.equals(Constants.SVRL_NS_URI)){
	        if(localName.equals(DIAGNOSTIC_REFERENCE)){
	            diagnostics += ": "+new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(diagnosticReferenceCharsBuffer.getCharsArray())));
	            diagnosticReferenceCharsBuffer.clear();
	            diagnosticReferenceContext = false;
	        }else if(localName.equals(FAILED_ASSERT)){
	            reportAssertError();
	            failedAssertContext = false;
	        }else if(localName.equals(SUCCESSFUL_REPORT)){
	            reportReportError();
	            successfulReportContext = false;
	        }  
	    }
	}
	
	void reportAssertError() throws SAXException{
	    String message = null;
	    if(activePatternName != null){
	        message = "Error in pattern \""+activePatternName+"\", "+getRule()+".";
	    }else if(activePatternId != null){
	        message = "Error in pattern \""+activePatternId+"\", "+getRule()+".";
	    }else{
	        message = "Error in unidentified pattern, "+getRule()+".";
	    }
	    
	    if(failedAssertId != null){
	        message += "\nFailed assertion: "+failedAssertId+"=\""+new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(failedAssertCharsBuffer.getCharsArray())))+"\".";
	    }else{
	        message += "\nFailed assertion: \""+new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(failedAssertCharsBuffer.getCharsArray())))+"\".";
	    }
	    failedAssertCharsBuffer.clear();
	    
	    
	    if(failedAssertLocation != null){
	        message += "\nLocation: "+failedAssertLocation+".";
	    }else{
	        message += "\nLocation: not available.";
	    }
	    
	    if(failedAssertTest != null){
	        message += "\nTest: "+failedAssertTest+".";
	    }else{
	        message += "\nTest: not available.";
	    }
	    
	    if(diagnostics == null){
	        errorHandler.error(new SAXParseException(message+"\nNo diagnostics present.", locator));
	    }else{
	        errorHandler.error(new SAXParseException(message+"\n"+diagnostics+".", locator));
	    }
	    failedAssertId = null;
	    failedAssertLocation = null;
	    failedAssertTest = null;
	    diagnostics = null;
	}
	
	void reportReportError() throws SAXException{
	    String message = null;
	    if(activePatternName != null){
	        message = "Error in pattern \""+activePatternName+"\", "+getRule()+".";
	    }else if(activePatternId != null){
	        message = "Error in pattern \""+activePatternId+"\", "+getRule()+".";
	    }else{
	        message = "Error in unidentified pattern, "+getRule()+".";
	    }
	    
	    if(successfulReportId != null){
	        message += "\nSuccessful report: "+successfulReportId+"=\""+new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(successfulReportCharsBuffer.getCharsArray())))+"\".";
	    }else{
	        message += "\nSuccessful report: \""+new String(spaceCharsHandler.collapseSpace(spaceCharsHandler.trimSpace(successfulReportCharsBuffer.getCharsArray())))+"\".";
	    }
	    successfulReportCharsBuffer.clear();
	    
	    
	    if(successfulReportLocation != null){
	        message += "\nLocation: "+successfulReportLocation+".";
	    }else{
	        message += "\nLocation: not available.";
	    }
	    
	    if(successfulReportTest != null){
	        message += "\nTest: "+successfulReportTest+".";
	    }else{
	        message += "\nTest: not available.";
	    }
	    
	    if(diagnostics == null){
	        errorHandler.error(new SAXParseException(message+"\nNo diagnostics present.", locator));
	    }else{
	        errorHandler.error(new SAXParseException(message+"\n"+diagnostics+".", locator));
	    }
	    successfulReportId = null;
	    successfulReportLocation = null;
	    successfulReportTest = null;
	    diagnostics = null;
	}
		
	String getRule(){
	    if(firedRuleId == null) return " rule context \""+firedRuleContext+"\"";
	    return " rule \""+firedRuleId+"\" for context \""+firedRuleContext+"\"";
	}
		
	public void endDocument()  throws SAXException {}
}

