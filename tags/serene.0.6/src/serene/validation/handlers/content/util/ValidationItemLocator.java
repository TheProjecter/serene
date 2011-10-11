package serene.validation.handlers.content.util;

import org.xml.sax.Locator;

import sereneWrite.MessageWriter;


/**
* Stores location and item identification data for the stack of document items 
* involved in validation. 
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
	
	int[] itemId;
	/**
	* It is stored in order to be used for error messages, it is the qName for 
	* elements and attributes and the value for list tokens. For attribute value 
	* and element character content it should also the actual be the actual 
	* string value, but that could be unpractical, so it is not stored. 
	*/
	String[] itemIdentifier;
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
		
		itemId = new int[maxDepth];
		itemIdentifier = new String[maxDepth];
        localName = new String[maxDepth]; 
        namespaceURI = new String[maxDepth];
		systemId = new String[maxDepth];
		publicId = new String[maxDepth];
		lineNumber = new int[maxDepth];
		columnNumber = new int[maxDepth]; 
		
		itemId[depth] = ELEMENT;
		itemIdentifier[depth] = null;
		systemId[depth] = null;
		publicId[depth] = null;
		lineNumber[depth] = 0;
		columnNumber[depth] = 0; 
	}
	
	public void clear(){
		maxDepth = 13;
		depth = ROOT;
		
		itemId = new int[maxDepth];
		itemIdentifier = new String[maxDepth];
        localName = new String[maxDepth]; 
        namespaceURI = new String[maxDepth];
		systemId = new String[maxDepth];
		publicId = new String[maxDepth];
		lineNumber = new int[maxDepth];
		columnNumber = new int[maxDepth]; 
		
		itemId[depth] = ELEMENT;
		itemIdentifier[depth] = null;
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
		itemId[depth] = ELEMENT;
		itemIdentifier[depth] = qn;
        localName[depth] = lN; 
        namespaceURI[depth] = uri;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
	
	public void closeElement(){
		itemIdentifier[depth] = null;
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
		itemId[depth] = ATTRIBUTE;		
		itemIdentifier[depth] = qn;
        localName[depth] = lN; 
        namespaceURI[depth] = uri;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
	
	public void closeAttribute(){
		itemIdentifier[depth] = null;
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
		itemId[depth] = CHARACTER_CONTENT;
		itemIdentifier[depth] = "character content";
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}
		
	public void closeCharsContent(){	
		if(!currentCharsContent)return;
		itemIdentifier[depth] = null;
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
		itemId[depth] = LIST_TOKEN;
		itemIdentifier[depth] = token;
		systemId[depth] = si;
		publicId[depth]  = pi;
		lineNumber[depth] = ln;
		columnNumber[depth] = cn;
	}

    public void closeListToken(){	
		itemIdentifier[depth] = null;
		systemId[depth] = null;
		publicId[depth]  = null;
		depth--;
	}
	
	private void increaseSize(int amount){
		maxDepth += amount;
			
		int[] increasedItemType = new int[maxDepth];
		System.arraycopy(itemId, 0, increasedItemType, 0, depth);
		itemId = increasedItemType;
		
		String[] increasedQName = new String[maxDepth];
		System.arraycopy(itemIdentifier, 0, increasedQName, 0, depth);
		itemIdentifier = increasedQName;
        
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
	
	public String getItemIdentifier(){
		return itemIdentifier[depth];
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
	    return itemId[depth];
	}
}
