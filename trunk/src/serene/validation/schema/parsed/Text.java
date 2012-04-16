
package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;

public class Text extends MultipleChildrenPattern{	
	Text(
	        int xmlBase,
            int ns, 
            int datatypeLibrary, 
            ParsedComponent[] children, 
            int recordIndex,
            DocumentIndexedData documentIndexedData){		
		super( xmlBase, ns, datatypeLibrary, children, recordIndex, documentIndexedData);
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
