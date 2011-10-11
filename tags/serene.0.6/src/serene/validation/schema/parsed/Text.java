
package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.util.AttributeInfo;
import sereneWrite.MessageWriter;

public class Text extends MultipleChildrenPattern{	
	Text(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, AttributeInfo[] foreignAttributes, ParsedComponent[] children, String qName, String location, MessageWriter debugWriter){
		super(prefixMapping, xmlBase, ns, datatypeLibrary, foreignAttributes, children, qName, location, debugWriter);
	}	
			
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}	
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	
	public String toString(){
		String s = "Text";		
		return s;
	}
}
