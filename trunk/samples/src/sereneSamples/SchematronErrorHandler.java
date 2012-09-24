package sereneSamples;

import org.xml.sax.SAXParseException;

import serene.schematron.FailedAssertException;
import serene.schematron.SuccessfulReportException;

class SchematronErrorHandler implements serene.schematron.SchematronErrorHandler{
    
    public void warning(SAXParseException spe){
        System.out.println("WARNING");
        System.out.println(spe.getMessage());
    }
    public void error(SAXParseException spe){
        System.out.println("ERROR");
        System.out.println(spe.getMessage());
    }
    public void fatalError(SAXParseException spe){
        System.out.println("FATAL ERROR");
        System.out.println(spe.getMessage());
    }
    
    
    public void error(FailedAssertException ssr){
        System.out.println("FAILED ASSERT");
        
        System.out.println("PATTERN NAME "+ssr.getActivePatternName());
        System.out.println("PATTERN ID "+ssr.getActivePatternId());
        System.out.println("PATTERN ROLE "+ssr.getActivePatternRole());
            
        System.out.println("RULE ID "+ssr.getFiredRuleId());
        System.out.println("RULE CONTEXT "+ssr.getFiredRuleContext());
        System.out.println("RULE ROLE "+ssr.getFiredRuleRole());
        System.out.println("RULE FLAG "+ssr.getFiredRuleFlag());
        
        
        System.out.println("ASSERT ID "+ssr.getId());
        System.out.println("ASSERT LOCATION "+ssr.getLocation());
        System.out.println("ASSERT TEST "+ssr.getTest());
        System.out.println("ASSERT ROLE "+ssr.getRole());
        System.out.println("ASSERT FLAG "+ssr.getFlag());
        
        System.out.println("ASSERT TEXT "+ssr.getText());
           
        System.out.println("ASSERT DIAGNOSTICS "+ssr.getDiagnostics());
        System.out.println("ASSERT DIAGNOSTICS TEXTS "+ssr.getDiagnosticTexts());
    }
    public void error(SuccessfulReportException ssr){
        System.out.println("SUCCESSFUL REPORT");
        
        System.out.println("PATTERN NAME "+ssr.getActivePatternName());
        System.out.println("PATTERN ID "+ssr.getActivePatternId());
        System.out.println("PATTERN ROLE "+ssr.getActivePatternRole());
            
        System.out.println("RULE ID "+ssr.getFiredRuleId());
        System.out.println("RULE CONTEXT "+ssr.getFiredRuleContext());
        System.out.println("RULE ROLE "+ssr.getFiredRuleRole());
        System.out.println("RULE FLAG "+ssr.getFiredRuleFlag());
        
        
        System.out.println("REPORT ID "+ssr.getId());
        System.out.println("REPORT LOCATION "+ssr.getLocation());
        System.out.println("REPORT TEST "+ssr.getTest());
        System.out.println("REPORT ROLE "+ssr.getRole());
        System.out.println("REPORT FLAG "+ssr.getFlag());
        
        System.out.println("REPORT TEXT "+ssr.getText());
           
        System.out.println("REPORT DIAGNOSTICS "+ssr.getDiagnostics());
        System.out.println("REPORT DIAGNOSTICS TEXTS "+ssr.getDiagnosticTexts());
    }
}

