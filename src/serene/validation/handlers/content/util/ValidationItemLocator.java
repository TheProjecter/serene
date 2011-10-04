package serene.validation.handlers.content.util;

import org.xml.sax.Locator;

import sereneWrite.MessageWriter;


/**
* Stores location and qName data for the stack of document items involved in 
* validation. It is the responsibility of the client to know about the nature 
* of the current item(element, attribute, character content).
*/
public class ValidationItemLocator implements Locator{
    public static final int NONE = -1;
	public static final int ROOT = 0;
	public static final int ELEMENT = 1;
	public static final int ATTRIBUTE = 2;
	public static final int CHARACTER_CONTENT = 3;
	public static final int LIST_TOKEN = 4;
		
	int maxDepth;
	int depth;
	
	int[] itemType;
	String[] qName;
    String[] localName;
    String[] namespaceURI;
	String[] systemId;
	String[] publicId;
	int[] lineNumber;
	int[] columnNumber;
	
	boolean currentCharsContent;
	
	MessageWriter debugWriter;
	public ValidationItemLocator(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		
		maxDepth = 13;
		depth = ROOT;
		
		itemType = new int[maxDepth];
		qName = new String[maxDepth];
        localName = new String[maxDepth]; 
        namespaceURI = new String[maxDepth];
		systemId = new String[maxDepth];
		publicId = new String[maxDepth];
		lineNumber = new int[maxDepth];
		columnNumber = new int[maxDepth]; 
		
		itemType[depth] = ELEMENT;
		qName[depth] = null;
		systemId[depth] = null;
		publicId[depth] = null;
		lineNumber[depth] = 0;
		columnNumber[depth] = 0; 
	}
	
	public void clear(){
		maxDepth = 13;
		depth = ROOT;
		
		itemType = new int[maxDepth];
		qName = new String[maxDepth];
        localName = new String[maxDepth]; 
        namespaceURI = new String[maxDepth];
		systemId = new String[maxDepth];
		publicId = new String[maxDepth];
		lineNumber = new int[maxDepth];
		columnNumber = new int[maxDepth]; 
		
		itemType[depth] = ELEMENT;
		qName[depth] = null;
		systemId[depth] = null;
		publicId[depth] = null;
		lineNumber[depth] = 0;
		columnNumber[depth] = 0;
	}
	
	public void newElement(String si, String pi, int ln, int cn, String uri, String lN, String qn){
		depth++;
		if(depth == maxDepth){				
			increaseSize(10);
		}		
		itemType[depth] = ELEMENT;
		qName[depth] = qn;
        localName[depth] = lN; 
        namespaceURI[depth] = uri;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
	
	public void closeElement(){
		qName[depth] = null;
        localName[depth] = null; 
        namespaceURI[depth] = null;
		systemId[depth] = null;
		publicId[depth]  = null;
		depth--;
	}
	
	public void newAttribute(String si, String pi, int ln, int cn, String uri, String lN, String qn){
		depth++;
		if(depth == maxDepth){				
			increaseSize(10);
		}
		itemType[depth] = ATTRIBUTE;		
		qName[depth] = qn;
        localName[depth] = lN; 
        namespaceURI[depth] = uri;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
	
	public void closeAttribute(){
		qName[depth] = null;
        localName[depth] = null; 
        namespaceURI[depth] = null;
		systemId[depth] = null;
		publicId[depth]  = null;
		depth--;
	}
	
	public void newCharsContent(String si, String pi, int ln, int cn){
		if(currentCharsContent)return;
		currentCharsContent = true;
		depth++;
		if(depth == maxDepth){				
			increaseSize(10);
		}		
		itemType[depth] = CHARACTER_CONTENT;
		qName[depth] = "character content";
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
		
	public void closeCharsContent(){	
		if(!currentCharsContent)return;
		qName[depth] = null;
		systemId[depth] = null;
		publicId[depth]  = null;
		depth--;
		currentCharsContent = false;
	}
	
    public void newListToken(String token, String si, String pi, int ln, int cn){
		depth++;
		if(depth == maxDepth){				
			increaseSize(10);
		}		
		itemType[depth] = LIST_TOKEN;
		qName[depth] = token;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}

    public void closeListToken(){	
		qName[depth] = null;
		systemId[depth] = null;
		publicId[depth]  = null;
		depth--;
	}
	
	private void increaseSize(int amount){
		maxDepth += amount;
			
		int[] increasedItemType = new int[maxDepth];
		System.arraycopy(itemType, 0, increasedItemType, 0, depth);
		itemType = increasedItemType;
		
		String[] increasedQName = new String[maxDepth];
		System.arraycopy(qName, 0, increasedQName, 0, depth);
		qName = increasedQName;
        
        String[] increasedLocalName = new String[maxDepth];
		System.arraycopy(localName, 0, increasedLocalName, 0, depth);
		localName = increasedLocalName;
        
		String[] increasedNsURI = new String[maxDepth];
		System.arraycopy(namespaceURI, 0, increasedNsURI, 0, depth);
		namespaceURI = increasedNsURI;
        
		String[] increasedSystemId = new String[maxDepth];
		System.arraycopy(systemId, 0, increasedSystemId, 0, depth);
		systemId = increasedSystemId;
		
		String[] increasedPublicId = new String[maxDepth];
		System.arraycopy(publicId, 0, increasedPublicId, 0, depth);
		publicId = increasedPublicId;
		
		int[] increasedLineNumber = new int[maxDepth];
		System.arraycopy(lineNumber, 0, increasedLineNumber, 0, depth);
		lineNumber = increasedLineNumber;
		
		int[] increasedColumnNumber = new int[maxDepth];
		System.arraycopy(columnNumber, 0, increasedColumnNumber, 0, depth);
		columnNumber = increasedColumnNumber;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public String getQName(){
		return qName[depth];
	}
    
    public String getNamespaceURI(){
		return namespaceURI[depth];
	}
    
    public String getLocalName(){
		return localName[depth];
	}
	
	public String getSystemId(){
		return systemId[depth];
	}
	
	public String getPublicId(){
		return publicId[depth];
	}
	
	public int getLineNumber(){
		return lineNumber[depth];
	}
	
	public int getColumnNumber(){
		return columnNumber[depth];
	}

	public int getItemId(){
	    return itemType[depth];
	}
}
