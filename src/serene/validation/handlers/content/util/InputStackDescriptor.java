package serene.validation.handlers.content.util;

import org.xml.sax.Locator;

import sereneWrite.MessageWriter;


/**
* Stores location and item identification data for the stack of document items 
* involved in validation. 
*/
public class InputStackDescriptor implements Locator{
    public static final int NONE = -1;
	public static final int ROOT = 0;
	public static final int ELEMENT = 1;
	public static final int ATTRIBUTE = 2;
	public static final int CHARACTER_CONTENT = 3;
	public static final int LIST_TOKEN = 4;
		
	ActiveInputDescriptor activeInputDescriptor;
	int[] inputRecordIndexes;
	int currentRecordIndexIndex;
	
	boolean currentCharsContent;
	
	MessageWriter debugWriter;
	public InputStackDescriptor(ActiveInputDescriptor activeInputDescriptor, MessageWriter debugWriter){
	    this.activeInputDescriptor = activeInputDescriptor;
		this.debugWriter = debugWriter;
			
        inputRecordIndexes = new int[10];
        currentRecordIndexIndex = -1;
	}
	
	public void clear(){
	    currentRecordIndexIndex = -1;
	}
	
	public void pushElement(String systemId, String publicId, int lineNumber, int columnNumber, String namespaceURI, String localName, String itemDescription){			
        if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ELEMENT,
                                                        itemDescription,
                                                        localName,
                                                        namespaceURI,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("ELEMENT "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public void popElement(){
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
		currentRecordIndexIndex--;
	}
	
	public void pushAttribute(String systemId, String publicId, int lineNumber, int columnNumber, String namespaceURI, String localName, String itemDescription){
		 if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ATTRIBUTE,
                                                        itemDescription,
                                                        localName,
                                                        namespaceURI,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("ATTIRBUTE "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public void popAttribute(){
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
		currentRecordIndexIndex--;
	}
	
	public void pushCharsContent (String systemId, String publicId, int lineNumber, int columnNumber){
		if(currentCharsContent)return;
		currentCharsContent = true;
		
		if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(CHARACTER_CONTENT,
                                                        "character content",
                                                        null,
                                                        null,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("CHARS CONTENT ");
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
	}
		
	public void popCharsContent(){	
		if(!currentCharsContent)return;
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
		currentRecordIndexIndex--;
		currentCharsContent = false;
	}
	
    public void pushListToken(String token, String systemId, String publicId, int lineNumber, int columnNumber){
		if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(LIST_TOKEN,
                                                        token,
                                                        null,
                                                        null,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("LIST TOKEN ");
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
	}

    public void popListToken(){	
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex]);
		currentRecordIndexIndex--;
	}
	
	public String getItemDescription(){
		return activeInputDescriptor.getItemDescription(inputRecordIndexes[currentRecordIndexIndex]);
	}
    
    public String getNamespaceURI(){
		return activeInputDescriptor.getNamespaceURI(inputRecordIndexes[currentRecordIndexIndex]);
	}
    
    public String getLocalName(){
		return activeInputDescriptor.getLocalName(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public String getSystemId(){
		return activeInputDescriptor.getSystemId(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public String getPublicId(){
		return activeInputDescriptor.getPublicId(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public int getLineNumber(){
		return activeInputDescriptor.getLineNumber(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public int getColumnNumber(){
		return activeInputDescriptor.getColumnNumber(inputRecordIndexes[currentRecordIndexIndex]);
	}

	public int getItemId(){
	    return activeInputDescriptor.getItemId(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	/**
	* Returns the index corresponding to the record of the current item in the 
	* ActiveInputDescriptor.
	*/
	public int getCurrentItemInputRecordIndex(){
	    return inputRecordIndexes[currentRecordIndexIndex];
	}
}
