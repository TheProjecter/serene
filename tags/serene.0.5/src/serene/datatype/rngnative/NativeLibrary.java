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

package serene.datatype.rngnative;

//import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.util.StringNormalizer;

import sereneWrite.MessageWriter;

class NativeLibrary implements DatatypeLibrary{
	//HashMap datatypes; there's no way yet
	StringNormalizer stringNormalizer; 
	MessageWriter debugWriter;
	
	NativeLibrary(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		stringNormalizer = new StringNormalizer(debugWriter);
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
		//System.out.println(typeLocalName);
		if(typeLocalName.equals("string")){
			return new StringDT();
		}else if(typeLocalName.equals("token")){
			return new TokenDT(stringNormalizer, debugWriter);
		}else throw new DatatypeException("Unsupported type: " + typeLocalName);
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		throw new IllegalStateException("Not needed");
	}
}