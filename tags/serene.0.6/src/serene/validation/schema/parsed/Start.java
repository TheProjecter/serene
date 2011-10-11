
package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.util.AttributeInfo;
import sereneWrite.MessageWriter;

public class Start extends Definition{
	Start(Map<String, String> prefixMapping, 
				String xmlBase, 
				String ns, 
				String datatypeLibrary, 
				String combine,
                AttributeInfo[] foreignAttributes,
				ParsedComponent[] children,				
				String qName, 
				String location, 
				MessageWriter debugWriter){		
		super(prefixMapping, xmlBase, ns, datatypeLibrary, combine, foreignAttributes, children, qName, location, debugWriter);
	}		
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "Start";
		s = s +" combine = "+combine;
		return s;
	}
}
