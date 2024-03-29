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

package serene.validation.schema.simplified;

import org.xml.sax.SAXException;

import serene.validation.schema.Component;

import serene.bind.util.DocumentIndexedData;

public interface SimplifiedComponent extends Component{
	SimplifiedComponent getParent();				
	void accept(SimplifiedComponentVisitor v);
	void accept(RestrictingVisitor v) throws SAXException;
	
	String getQName();
	String getLocation(boolean restrictToFileName);	
	
	//TODO 
	// Consider moving to Component. 
	int getRecordIndex();	
	DocumentIndexedData getDocumentIndexedData();
}