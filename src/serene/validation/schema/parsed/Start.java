
package serene.validation.schema.parsed;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.bind.util.DocumentIndexedData;
import sereneWrite.MessageWriter;

public class Start extends Definition{
	Start(/*Map<String, String> prefixMapping,*/
	        int xmlBase,
            int ns, 
            int datatypeLibrary, 
            int combine,
            ParsedComponent[] children,
            /*AttributeInfo[] foreignAttributes,                    
            String qName, 
            String location,*/
            int recordIndex,
            DocumentIndexedData documentIndexedData,                    
            MessageWriter debugWriter){
		super(/*prefixMapping,*/ xmlBase, ns, datatypeLibrary, combine, children, recordIndex, documentIndexedData, debugWriter);
	}		
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		String s = "Start";
		s = s +" combine = "+getCombine();
		return s;
	}
}
