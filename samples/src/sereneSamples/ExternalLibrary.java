package sereneSamples;

//import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;

import serene.datatype.util.StringNormalizer;

import sereneWrite.MessageWriter;

class ExternalLibrary implements DatatypeLibrary{
	StringNormalizer stringNormalizer; 
	MessageWriter debugWriter;
    SimilarTokenBuilder similarTokenBuilder;
	
	ExternalLibrary(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		stringNormalizer = new StringNormalizer(debugWriter);
        similarTokenBuilder  = new SimilarTokenBuilder();
	}
	
	public Datatype createDatatype(String typeLocalName) throws DatatypeException{
		//System.out.println(typeLocalName);
		if(typeLocalName.equals("integer")){
			return new IntegerDT();
		}else if(typeLocalName.equals("similarToken")){
			return similarTokenBuilder.createDatatype();//called when no params were present, will throw an error            
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
