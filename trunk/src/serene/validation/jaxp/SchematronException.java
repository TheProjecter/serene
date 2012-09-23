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


abstract class SchematronException extends SAXParseException{
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
    
    SchematronException(String activePatternName,
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
        super(null, locator);
        this.activePatternName = activePatternName;
        this.activePatternId = activePatternId;
        this.activePatternRole = activePatternRole;
            
        this.firedRuleId = firedRuleId;
        this.firedRuleContext = firedRuleContext;
        this.firedRuleRole = firedRuleRole;
        this.firedRuleFlag = firedRuleFlag;
        
        
        this.id = id;
        this.location = location;
        this.test = test;
        this.role = role;
        this.flag = flag;
        
        this.text = text;
           
        this.diagnostics = diagnostics;
        this.diagnosticTexts = diagnosticTexts;
    }
    
    SchematronException(String activePatternName,
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
        super(null, locator, e);
        this.activePatternName = activePatternName;
        this.activePatternId = activePatternId;
        this.activePatternRole = activePatternRole;
            
        this.firedRuleId = firedRuleId;
        this.firedRuleContext = firedRuleContext;
        this.firedRuleRole = firedRuleRole;
        this.firedRuleFlag = firedRuleFlag;
        
        
        this.id = id;
        this.location = location;
        this.test = test;
        this.role = role;
        this.flag = flag;
        
        this.text = text;
           
        this.diagnostics = diagnostics;
        this.diagnosticTexts = diagnosticTexts;
    }
    
    SchematronException(String activePatternName,
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
        super(null, publicId, systemId, lineNumber, columnNumber);
        this.activePatternName = activePatternName;
        this.activePatternId = activePatternId;
        this.activePatternRole = activePatternRole;
            
        this.firedRuleId = firedRuleId;
        this.firedRuleContext = firedRuleContext;
        this.firedRuleRole = firedRuleRole;
        this.firedRuleFlag = firedRuleFlag;
        
        
        this.id = id;
        this.location = location;
        this.test = test;
        this.role = role;
        this.flag = flag;
        
        this.text = text;
           
        this.diagnostics = diagnostics;
        this.diagnosticTexts = diagnosticTexts;
    }
    
    SchematronException(String activePatternName,
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
        super(null, publicId, systemId, lineNumber, columnNumber, e);
        this.activePatternName = activePatternName;
        this.activePatternId = activePatternId;
        this.activePatternRole = activePatternRole;
            
        this.firedRuleId = firedRuleId;
        this.firedRuleContext = firedRuleContext;
        this.firedRuleRole = firedRuleRole;
        this.firedRuleFlag = firedRuleFlag;
        
        
        this.id = id;
        this.location = location;
        this.test = test;
        this.role = role;
        this.flag = flag;
        
        this.text = text;
                   
        this.diagnostics = diagnostics;
        this.diagnosticTexts = diagnosticTexts;
    }


    public String getActivePatternName(){
        return activePatternName;
    }
    public String getActivePatternId(){
        return activePatternId;
    }
    public String getActivePatternRole(){
        return activePatternRole;
    }
        
    public String getFiredRuleId(){
        return firedRuleId;
    }
    public String getFiredRuleContext(){
        return firedRuleContext;
    }
    public String getFiredRuleRole(){
        return firedRuleRole;
    }
    public String getFiredRuleFlag(){
        return firedRuleFlag;
    }
    
    
    public String getId(){
        return id;
    }
    public String getLocation(){
        return location;
    }
    public String getTest(){
        return test;
    }
    public String getRole(){
        return role;
    }
    public String getFlag(){
        return flag;
    }
    
    public String getText(){
        return text;
    }
       
    public List<String> getDiagnostics(){
        return diagnostics;
    }
    public List<String> getDiagnosticTexts(){
        return diagnosticTexts;
    }    
}
