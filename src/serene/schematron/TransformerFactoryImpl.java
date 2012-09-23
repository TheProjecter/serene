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


package serene.schematron;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;

import javax.xml.transform.sax.TransformerHandler;

//import net.sf.saxon.TransformerFactoryImpl;

import serene.validation.jaxp.SchematronSchemaFactory;

public class TransformerFactoryImpl extends net.sf.saxon.TransformerFactoryImpl{

    public TransformerHandler newTransformerHandler(Source source, SchematronSchemaFactory schematronSchemaFactory)throws TransformerConfigurationException {
        Templates templates = newTemplates(source);
        return newTransformerHandler(templates, schematronSchemaFactory);
    }
    
    public TransformerHandler newTransformerHandler(Templates templates, SchematronSchemaFactory schematronSchemaFactory) throws TransformerConfigurationException {
        if (!(templates instanceof net.sf.saxon.PreparedStylesheet)) {
            throw new TransformerConfigurationException("Templates object was not created by Saxon");
        }
        net.sf.saxon.Controller controller = (net.sf.saxon.Controller)templates.newTransformer();
        return new TransformerHandlerImpl(controller, schematronSchemaFactory);
    }
    
}
