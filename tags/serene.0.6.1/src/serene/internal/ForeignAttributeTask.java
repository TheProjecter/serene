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

package serene.internal;

import javax.xml.XMLConstants;

import serene.bind.util.DocumentIndexedData;

import serene.Constants;

class ForeignAttributeTask extends RNGParseAttributeTask{
    final String BASE = "base";
    
	ForeignAttributeTask(){
		super();
	}
	public void execute(){
	    DocumentIndexedData did = context.getDocumentIndexedData();
	    int recordIndex = context.getAttributeInputRecordIndex();
	    
	    String nsURI = did.getNamespaceURI(recordIndex);
	    String ln = did.getLocalName(recordIndex);
		if(nsURI.equals(XMLConstants.XML_NS_URI) && ln.equals(BASE)){
		    builder.setXMLBase(recordIndex);
		}else if(nsURI.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE) && ln.equals(Constants.DTD_COMPATIBILITY_DEFAULT_VALUE)){
		    builder.setDefaultValue(recordIndex);
		}		
	}
}
