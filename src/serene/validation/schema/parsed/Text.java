
package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;
import sereneWrite.MessageWriter;

public class Text extends MultipleChildrenPattern{	
	Text(/*Map<String, String> prefixMapping,*/
	        int xmlBase,
            int ns, 
            int datatypeLibrary, 
            ParsedComponent[] children, 
            int recordIndex,
            DocumentIndexedData documentIndexedData,
            MessageWriter debugWriter){		
		super(/*prefixMapping,*/ xmlBase, ns, datatypeLibrary, children, recordIndex, documentIndexedData, debugWriter);
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
