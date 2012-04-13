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

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.util.StringNormalizer;

class NativeLibrary implements DatatypeLibrary{
	Datatype tokenDT;
    Datatype stringDT;
    
    DatatypeBuilder tokenBuilder;
    DatatypeBuilder stringBuilder;
    
	StringNormalizer stringNormalizer;    
    	
	NativeLibrary(){
        stringNormalizer = new StringNormalizer();
        
        stringDT = new StringDT();
        tokenDT = new TokenDT(stringNormalizer);
        
        tokenBuilder = new NativeBuilder(stringDT);
        stringBuilder = new NativeBuilder(tokenDT);
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
        if(typeLocalName.equals("string")){            
            return stringDT;
        }else if(typeLocalName.equals("token")){            
            return tokenDT;
        }else throw new DatatypeException("Unsupported type: " + typeLocalName);        
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		if(baseTypeLocalName.equals("string")){
			return stringBuilder;
		}else if(baseTypeLocalName.equals("token")){
			return tokenBuilder;
		}else throw new DatatypeException("Unsupported type: " + baseTypeLocalName+".");
	}
}