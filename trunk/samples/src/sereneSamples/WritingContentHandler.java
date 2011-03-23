package sereneSamples;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.Attributes;

import org.xml.sax.helpers.DefaultHandler;

class WritingContentHandler extends DefaultHandler{
    String align = "";
    String line = "";
    String att = "";
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException{			
        att = "";
        for(int i = 0; i < atts.getLength(); i++){
            att += (" "+
                    atts.getQName(i)
                    + "="
                    + "\""+atts.getValue(i)+"\"");
        }			
        line = (align 
                + "<"
                + qName
                + att
                + ">");
        System.out.println(line);
        align +="\t";
    }
    
    public void endElement(String uri, String localName, String qName) throws SAXException{
        align = align.substring(0, align.length()-1);
        line = (align 
                + "</"
                + qName
                + ">");
        System.out.println(line);			
    }
    
    public void characters(char[] chars, int offset, int length){
        line = (new String(chars, offset, length)).trim();
        if(line.equals(""))return;
        System.out.println(align+line);
    }
    
}
