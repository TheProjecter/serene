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

class RNGSVRLParser extends SVRLParser{
    void reportAssertError() throws SAXException{
	    String message = null;
	    if(activePatternName != null){
	        message = "Error in Schematron pattern \""+activePatternName+"\", "+getRule()+".";
	    }else if(activePatternId != null){
	        message = "Error in Schematron pattern \""+activePatternId+"\", "+getRule()+".";
	    }else{
	        message = "Error in unidentified Schematron pattern, "+getRule()+".";
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
	        message = "Error in Schematron pattern \""+activePatternName+"\", "+getRule()+".";
	    }else if(activePatternId != null){
	        message = "Error in Schematron pattern \""+activePatternId+"\", "+getRule()+".";
	    }else{
	        message = "Error in unidentified Schematron pattern, "+getRule()+".";
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

}
