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

package serene.internal.datatype;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;

import serene.Constants;

import sereneWrite.MessageWriter;

public class InternalLibraryFactory implements DatatypeLibraryFactory{	
	MessageWriter debugWriter;
	public InternalLibraryFactory(){}
	public InternalLibraryFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	public InternalLibrary createDatatypeLibrary(String namespace){
		if(namespace.equals(Constants.INTERNAL_DATATYPE_LIBRARY)){
			return new InternalLibrary(debugWriter);
		}
		return null;
	} 
}
