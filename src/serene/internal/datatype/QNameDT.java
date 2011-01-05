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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeStreamingValidator;

import serene.internal.datatype.xmlName.XMLNameHandler;
import serene.internal.datatype.xmlName.NameInvalidException;
import serene.internal.datatype.xmlName.NameReservedException;

import sereneWrite.MessageWriter;

class QNameDT implements Datatype{
	XMLNameHandler nameHandler;
	QNameDT(){
		nameHandler = new XMLNameHandler();
		nameHandler.version("1.0");
	}
	public boolean isValid(String str, ValidationContext vc) {		
		try{
			// TODO xml version from ValidationContext
            int colon = str.indexOf(':');                        
            if(colon > 0){
                String prefix = str.substring(0, colon);
                if(vc.resolveNamespacePrefix(prefix) == null) return false;
            }
			nameHandler.handleQName(str);
			return true;			
		}catch(NameInvalidException nie){
			return false; 
		}catch(NameReservedException nre){
			return true;
		}
	}

	public void checkValid(String str, ValidationContext vc) throws DatatypeException {
		try{
			// TODO xml version from ValidationContext
            int colon = str.indexOf(':');                        
            if(colon > 0){
                String prefix = str.substring(0, colon);
                if(vc.resolveNamespacePrefix(prefix) == null) throw new DatatypeException("Prefix was not declared.");
            }
			nameHandler.handleQName(str);						
		}catch(NameInvalidException nie){
			throw new DatatypeException(nie.getMessage()); 
		}catch(NameReservedException nre){			
			/*throw new DatatypeException(nre.getMessage());*/
		}
	}

	public Object createValue(String str, ValidationContext vc) {
		return str;
	}

	public boolean isContextDependent() {
		return true;
	}

	public boolean alwaysValid() {
		return false;
	}

	public int getIdType() {
		return ID_TYPE_NULL;
	}

	public boolean sameValue(Object obj1, Object obj2) {
		return obj1.equals(obj2);
	}

	public int valueHashCode(Object obj) {
		return obj.hashCode();
	}

	public DatatypeStreamingValidator createStreamingValidator(ValidationContext vc) {
		throw new UnsupportedOperationException("Serene does not support streaming validation yet");
	}
	
}