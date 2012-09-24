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
	    
	    errorDispatcher.error(new RNGFailedAssertException(activePatternName,
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
	    errorDispatcher.error(new RNGSuccessfulReportException(activePatternName,
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
}
