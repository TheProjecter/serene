/*
Copyright 2012 Radu Cernuta 

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



package serene.validation.handlers.content.util;

import java.util.Map;

import serene.util.InputDescriptor;

import sereneWrite.MessageWriter;


/**
* Stores location and item identification data for the stack of document items 
* involved in validation. 
*/
public class InputStackDescriptor implements InputDescriptor{  
	
    final String CHARACTER_CONTENT_DESCRIPTION = "character content";
    final String TOKEN_DESCRIPTION = "list token";
	ActiveInputDescriptor activeInputDescriptor;
	int[] inputRecordIndexes;
	int currentRecordIndexIndex;
	
	//boolean currentCharsContent;
	
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
	
	public void push(int recordIndex){
	    if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = recordIndex;
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}	
	public void pop(){
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
		currentRecordIndexIndex--;
	}
	// TODO 
	// Review specific pops, they only exist because of the special treatment 
	// for charatcer content and maybe also remove entirely the pushCharacterContent.
	
	public void pushElement(Map<String, String> declaredXmlns,
	                            String qName,
	                            String namespaceURI, 
                                String localName,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){			
        if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ELEMENT,
                                                                                    declaredXmlns,
                                                                                    qName,                                                                                    
                                                                                    namespaceURI,
                                                                                    localName,
                                                                                    systemId,
                                                                                    publicId, 	
                                                                                    lineNumber,
                                                                                    columnNumber);
        //System.out.println("ELEMENT "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}
	
	public void pushElement(String qName,
	                            String namespaceURI, 
                                String localName,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){			
        if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ELEMENT,
                                                                                    qName,                                                                                    
                                                                                    namespaceURI,
                                                                                    localName,
                                                                                    systemId,
                                                                                    publicId, 	
                                                                                    lineNumber,
                                                                                    columnNumber);
        //System.out.println("ELEMENT "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}
	
	public void popElement(){
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
		currentRecordIndexIndex--;
	}
	
	// Bound.
	public void pushAttribute(String qName,
	                            String namespaceURI, 
                                String localName,
                                String type,
                                String value,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){
		 if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ATTRIBUTE,
                                                                                        qName,
                                                                                        namespaceURI, 
                                                                                        localName,
                                                                                        type,
                                                                                        value,
                                                                                        systemId, 
                                                                                        publicId, 
                                                                                        lineNumber, 
                                                                                        columnNumber);
        //System.out.println("ATTIRBUTE "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}
	
	
	// Not bound.
	public void pushAttribute(String qName,
	                            String namespaceURI, 
                                String localName,
                                String type,
                                //String value,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){
		 if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(ATTRIBUTE,
                                                                                        qName,
                                                                                        namespaceURI, 
                                                                                        localName,
                                                                                        type,
                                                                                        //value,
                                                                                        systemId, 
                                                                                        publicId, 
                                                                                        lineNumber, 
                                                                                        columnNumber);
        //System.out.println("ATTIRBUTE "+itemDescription);
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}
	
	public void popAttribute(){
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
		currentRecordIndexIndex--;
	}
	
	// Bound.
	/*public void pushCharsContent (String value,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){
		//if(currentCharsContent)return;
		//currentCharsContent = true;
		
		if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(CHARACTER_CONTENT,
                                                        CHARACTER_CONTENT_DESCRIPTION,
                                                        value,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("CHARS CONTENT ");
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}*/
	
	// Not bound.
	/*public void pushCharsContent (String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){
		//if(currentCharsContent)return;
		//currentCharsContent = true;
		
		if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(CHARACTER_CONTENT,
                                                        CHARACTER_CONTENT_DESCRIPTION,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("CHARS CONTENT ");
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}*/
		
	/*public void popCharsContent(){	
		//if(!currentCharsContent)return;
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
		currentRecordIndexIndex--;
		//currentCharsContent = false;
	}*/
	
    public void pushListToken(String token,
	                            String systemId, 
                                String publicId, 
                                int lineNumber, 
                                int columnNumber){
		if(++currentRecordIndexIndex == inputRecordIndexes.length){
            int[] increased = new int[inputRecordIndexes.length+10];
            System.arraycopy(inputRecordIndexes, 0, increased, 0, currentRecordIndexIndex);
            inputRecordIndexes = increased;
        }
        inputRecordIndexes[currentRecordIndexIndex] = activeInputDescriptor.recordItem(LIST_TOKEN,
                                                        TOKEN_DESCRIPTION,
                                                        token,
                                                        systemId,
                                                        publicId, 	
                                                        lineNumber,
                                                        columnNumber);
        //System.out.println("LIST TOKEN ");
        activeInputDescriptor.registerClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
	}

    public void popListToken(){	
		activeInputDescriptor.unregisterClientForRecord(inputRecordIndexes[currentRecordIndexIndex], this);
		currentRecordIndexIndex--;
	}
	
	
	public int getItemId(){
	    return activeInputDescriptor.getItemId(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public String getItemDescription(){
		return activeInputDescriptor.getItemDescription(inputRecordIndexes[currentRecordIndexIndex]);
	}
    
    public String getNamespaceURI(){
        int itemId = getItemId(); 
        if(itemId != ATTRIBUTE || itemId != ELEMENT) throw new IllegalStateException();
		return activeInputDescriptor.getNamespaceURI(inputRecordIndexes[currentRecordIndexIndex]);
	}
    
    public String getLocalName(){
        int itemId = getItemId(); 
        if(itemId != ATTRIBUTE || itemId != ELEMENT) throw new IllegalStateException();
		return activeInputDescriptor.getLocalName(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public String getAttributeType(){
	    if(getItemId() != ATTRIBUTE) throw new IllegalStateException();
		return activeInputDescriptor.getAttributeType(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public String getStringValue(){
	    if(getItemId() == ELEMENT) throw new IllegalStateException();
		return activeInputDescriptor.getStringValue(inputRecordIndexes[currentRecordIndexIndex]);
	}
	
	public char[] getCharArrayValue(){
	    if(getItemId() == ELEMENT) throw new IllegalStateException();
		return activeInputDescriptor.getCharArrayValue(inputRecordIndexes[currentRecordIndexIndex]);
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

	
	/**
	* Returns the index corresponding to the record of the current item in the 
	* ActiveInputDescriptor.
	*/
	public int getCurrentItemInputRecordIndex(){
	    return inputRecordIndexes[currentRecordIndexIndex];
	}
}
