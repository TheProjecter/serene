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

import org.xml.sax.SAXParseException;
import org.xml.sax.Locator;


public class RNGSchematronSuccessfulReport extends SchematronSuccessfulReport{
    
    RNGSchematronSuccessfulReport(String activePatternName,
                    String activePatternId,
                    String activePatternRole,                        
                    String firedRuleId,
                    String firedRuleContext,
                    String firedRuleRole,
                    String firedRuleFlag,
                    String id,
                    String location,
                    String test,
                    String role,
                    String flag,                    
                    String text,
                    List<String> diagnostics,
                    List<String> diagnosticTexts,
                    Locator locator){
        super(activePatternName,
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
                    locator);
    }
    
    RNGSchematronSuccessfulReport(String activePatternName,
                    String activePatternId,
                    String activePatternRole,                        
                    String firedRuleId,
                    String firedRuleContext,
                    String firedRuleRole,
                    String firedRuleFlag,
                    String id,
                    String location,
                    String test,
                    String role,
                    String flag,                    
                    String text,
                    List<String> diagnostics,
                    List<String> diagnosticTexts,
                    Locator locator,
                    Exception e){
        super(activePatternName,
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
                    locator, 
                    e);
    }
    
    RNGSchematronSuccessfulReport(String activePatternName,
                    String activePatternId,
                    String activePatternRole,                        
                    String firedRuleId,
                    String firedRuleContext,
                    String firedRuleRole,
                    String firedRuleFlag,
                    String id,
                    String location,
                    String test,
                    String role,
                    String flag,                    
                    String text,
                    List<String> diagnostics,
                    List<String> diagnosticTexts,
                    String publicId, 
                    String systemId, 
                    int lineNumber, 
                    int columnNumber){
        super(activePatternName,
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
                    publicId, 
                    systemId, 
                    lineNumber, 
                    columnNumber);
    }
    
    RNGSchematronSuccessfulReport(String activePatternName,
                    String activePatternId,
                    String activePatternRole,                        
                    String firedRuleId,
                    String firedRuleContext,
                    String firedRuleRole,
                    String firedRuleFlag,
                    String id,
                    String location,
                    String test,
                    String role,
                    String flag,                    
                    String text,
                    List<String> diagnostics,
                    List<String> diagnosticTexts,
                    String publicId, 
                    String systemId, 
                    int lineNumber, 
                    int columnNumber,
                    Exception e){
        super(activePatternName,
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
                    publicId, 
                    systemId, 
                    lineNumber, 
                    columnNumber, 
                    e);
    } 
    
    public String getMessage(){
        String message = null;
	    if(activePatternName != null){
	        message = "Error in Schematron pattern \""+activePatternName+"\", "+getRule()+".";
	    }else if(activePatternId != null){
	        message = "Error in Schematron pattern \""+activePatternId+"\", "+getRule()+".";
	    }else{
	        message = "Error in unidentified Schematron pattern, "+getRule()+".";
	    }
	    
	    
	    
	    if(id != null){
	        message += "\nSuccessful report: "+id+"=\""+text+"\".";
	    }else{
	        message += "\nSuccessful report: \""+text+"\".";
	    }
	    
	    
	    if(location != null){
	        message += "\nLocation: "+location+".";
	    }else{
	        message += "\nLocation: not available.";
	    }
	    
	    if(test != null){
	        message += "\nTest: "+test+".";
	    }else{
	        message += "\nTest: not available.";
	    }
	    
	    if(diagnostics == null || diagnostics.isEmpty()){
	        return message+"\nNo diagnostics present.";
	    }else{
	        return message+getDiagnosticsMessage();
	    }
    }       
}
