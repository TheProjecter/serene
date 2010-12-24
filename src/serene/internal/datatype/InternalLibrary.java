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

//import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.util.StringNormalizer;

import sereneWrite.MessageWriter;

// TODO 
// must not be public when jarred 
public class InternalLibrary implements DatatypeLibrary{
	//HashMap datatypes; there's no way yet
	StringNormalizer stringNormalizer; 
	MessageWriter debugWriter;
	
	public InternalLibrary(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		stringNormalizer = new StringNormalizer(debugWriter);
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
		if(typeLocalName.equals("QName")){
			return new QNameDT();
		}else if(typeLocalName.equals("NCName")){
			return new NCNameDT();
		}else if(typeLocalName.equals("hrefURI")){
			return new HrefURIDT();
		}else if(typeLocalName.equals("datatypeLibraryURI")){
			return new DatatypeLibraryURIDT();
		}else if(typeLocalName.equals("combine")){
			return new CombineDT();
		}else throw new DatatypeException("Unsupported type: " + typeLocalName);
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		throw new IllegalStateException("Not needed");
	}
}