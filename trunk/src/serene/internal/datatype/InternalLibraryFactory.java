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

import serene.internal.datatype.InternalLibrary;

import sereneWrite.MessageWriter;

// This class should be made private when it is prepared for the jar
public class InternalLibraryFactory implements DatatypeLibraryFactory{	
	String INTERNAL_LIBRARY = "http://serenerng.org/datatype/internal";
	MessageWriter debugWriter;
	public InternalLibraryFactory(){}
	public InternalLibraryFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	public InternalLibrary createDatatypeLibrary(String namespace){
		if(namespace.equals(INTERNAL_LIBRARY)){
			return new InternalLibrary(debugWriter);
		}
		return null;
	} 
}
