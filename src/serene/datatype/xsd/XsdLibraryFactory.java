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


package serene.datatype.xsd;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import serene.Constants;

public class XsdLibraryFactory implements DatatypeLibraryFactory{
	public XsdLibraryFactory(){}
	public XsdLibrary createDatatypeLibrary(String namespace){
		if(namespace.equals(Constants.XSD_DATATYPE_LIBRARY)){
			return new XsdLibrary();
		}
		return null;
	} 
}
