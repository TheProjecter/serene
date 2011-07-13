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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeStreamingValidator;

import sereneWrite.MessageWriter;

class StringDT implements Datatype{
	public boolean isValid(String str, ValidationContext vc) {
		return true;
	}

	public void checkValid(String str, ValidationContext vc) throws DatatypeException {
		if (!isValid(str, vc))
			throw new DatatypeException();
	}

	public Object createValue(String str, ValidationContext vc) {
		return str;
	}

	public boolean isContextDependent() {
		return false;
	}

	public boolean alwaysValid() {
		return true;
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