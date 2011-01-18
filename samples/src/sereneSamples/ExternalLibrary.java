package sereneSamples;

//import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.util.StringNormalizer;

import sereneWrite.MessageWriter;

class ExternalLibrary implements DatatypeLibrary{
	//HashMap datatypes; there's no way yet
	StringNormalizer stringNormalizer; 
	MessageWriter debugWriter;
	
	ExternalLibrary(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		stringNormalizer = new StringNormalizer(debugWriter);
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
		//System.out.println(typeLocalName);
		if(typeLocalName.equals("integer")){
			return new IntegerDT();
		}else if(typeLocalName.equals("similarToken")){
			return new SimilarTokenDT();
		}else throw new DatatypeException("Unsupported type: " + typeLocalName);
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		if(baseTypeLocalName.equals("integer")){
			return new DefaultBuilder(new IntegerDT());
		}else if(baseTypeLocalName.equals("similarToken")){
			return new SimilarTokenBuilder();
		}else throw new DatatypeException("Unsupported type: " + baseTypeLocalName);
	}
}
