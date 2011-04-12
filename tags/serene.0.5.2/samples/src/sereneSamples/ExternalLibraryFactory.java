package sereneSamples;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;


import sereneWrite.MessageWriter;

// This class should be made private when it is prepared for the jar
public class ExternalLibraryFactory implements DatatypeLibraryFactory{			
	String EXTERNAL_LIBRARY = "http://example.com/datatype/external";	
	MessageWriter debugWriter;
	public ExternalLibraryFactory(){}
	public ExternalLibraryFactory(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	public ExternalLibrary createDatatypeLibrary(String namespace){
		if(namespace.equals(EXTERNAL_LIBRARY)){
			return new ExternalLibrary(debugWriter);
		}
		return null;
	} 
}

