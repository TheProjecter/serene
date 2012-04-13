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

package serene.dtdcompatibility;

import javax.xml.XMLConstants;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import serene.util.BooleanStack;
import serene.validation.handlers.error.ErrorDispatcher;
import serene.Constants;

public class DocumentationElementHandler{
    final String PARAM = "param";
    final String VALUE = "value";
    final String NAME = "name";
    
    int documentationDepth;
    boolean nestedDocumentation;
    BooleanStack documentablePosition;
    
    ErrorDispatcher errorDispatcher;
    
    public DocumentationElementHandler(ErrorDispatcher errorDispatcher){
        this.errorDispatcher = errorDispatcher;    
        documentationDepth = 0;
        nestedDocumentation = false;
        documentablePosition = new BooleanStack();
        documentablePosition.push(false);
    }
    
    public void init(){
        documentationDepth = 0;
        nestedDocumentation = false;
        documentablePosition.clear();
        documentablePosition.push(false);
    }
    
    public void startElement(String namespaceURI, String localName, String qName, Attributes attributes, Locator locator) throws SAXException{
        if(documentationDepth == 1){
            if(!nestedDocumentation){
                nestedDocumentation = true;
                if(namespaceURI.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
                     && localName.equals(Constants.DTD_COMPATIBILITY_DOCUMENTATION)){
                     documentationDepth++;                    
                }
                String message = "DTD compatibility error. Unexpected element <"+qName+"> in documentation element content.";
                errorDispatcher.error(new DocumentationElementException(message, locator));
            }
            documentablePosition.push(true);
            return;
        }else if(documentationDepth > 1){
            if(namespaceURI.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
                 && localName.equals(Constants.DTD_COMPATIBILITY_DOCUMENTATION)){
                documentationDepth++;                
            }
            documentablePosition.push(true);
            return;
        }  
        
        if(namespaceURI.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
             && localName.equals(Constants.DTD_COMPATIBILITY_DOCUMENTATION)){
            documentationDepth++;
            if(!documentablePosition.peek()){
                String message = "DTD compatibility error. Misplaced documentation element.";
                errorDispatcher.error(new DocumentationElementException(message, locator));
            }
            int attributesCount = attributes.getLength();
            for(int i = 0; i < attributesCount; i++){
                String attributeNs = attributes.getURI(i);
                if(attributeNs.equals(XMLConstants.NULL_NS_URI)
                    || attributeNs.equals(XMLConstants.RELAXNG_NS_URI)
                    || attributeNs.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)){
                    String message = "DTD compatibility error. Unexpected attribute "+attributes.getQName(i)+" in documentation element content.";
                    errorDispatcher.error(new DocumentationElementException(message, locator));
                }
            }            
        }else if(namespaceURI.equals(XMLConstants.RELAXNG_NS_URI)){
            if(localName.equals(PARAM)
                || localName.equals(VALUE)
                || localName.equals(NAME)){
                documentablePosition.pop();
                documentablePosition.push(true);
            }else{
                documentablePosition.pop();
                documentablePosition.push(false);
            }
        }//else{
            // Do nothing. The elements from other namespaces than RELAXNG don't
            // have any influence on the documentation position.
        //}
        documentablePosition.push(true);
    }
    
    public void endElement(String namespaceURI, String localName, String qName, Locator locator){
        if(namespaceURI.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
            && localName.equals(Constants.DTD_COMPATIBILITY_DOCUMENTATION)){
            if(--documentationDepth == 0) nestedDocumentation = false;
        }
        documentablePosition.pop();
    }  
}
