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
		}else throw new DatatypeException("Unsupported type: " + typeLocalName);
	}
	
	public DatatypeBuilder createDatatypeBuilder(String baseTypeLocalName) throws DatatypeException{
		throw new IllegalStateException("Not needed");
	}
}
