/*
Copyright 2010 Radu Cernuta 

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

package serene.bind;

import java.util.Map;
import java.util.Set;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;

import serene.util.ObjectIntHashMap;

import serene.Reusable;

import sereneWrite.MessageWriter;

public interface BindingModel extends Reusable{
    
	void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException;	
	Object getProperty(String name)  throws SAXNotRecognizedException, SAXNotSupportedException;

	void setFeature(String name, boolean value)  throws SAXNotRecognizedException, SAXNotSupportedException;
	boolean getFeature(String name)  throws SAXNotRecognizedException, SAXNotSupportedException;
	
	DocumentTask getStartDocumentTask();
	DocumentTask getEndDocumentTask();
    	
	ElementTask getStartElementTask(SElement element);
	ElementTask getEndElementTask(SElement element);	
	AttributeTask getAttributeTask(SAttribute attribute);
	
	ElementTask getGenericStartElementTask();
	ElementTask getGenericEndElementTask();	
	AttributeTask getGenericAttributeTask();
}