package sereneSamples;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeStreamingValidator;

import sereneWrite.MessageWriter;

class IntegerDT implements Datatype{
	public boolean isValid(String str, ValidationContext vc) {
		try{
			Integer.parseInt(str);		
			return true;
		}catch(NumberFormatException nfe){
			return false;
		}
	}

	public void checkValid(String str, ValidationContext vc) throws DatatypeException {
		try{
			Integer.parseInt(str);		
		}catch(NumberFormatException nfe){
			throw new DatatypeException(nfe.getMessage());
		}
			
	}

	public Object createValue(String str, ValidationContext vc){
		try{
			return new Integer(str);		
		}catch(NumberFormatException nfe){
			return null;
		}		
	}

	public boolean isContextDependent() {
		return false;
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
		throw new UnsupportedOperationException();
	}
	
}
