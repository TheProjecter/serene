/*
Copyright 2010 Radu Cernuta 

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

package serene.validation.handlers.content.impl;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import sereneWrite.MessageWriter;

class BoundStartValidationHandler extends BoundElementValidationHandler{
								
	BoundStartValidationHandler(MessageWriter debugWriter){
		super(debugWriter);		
	}
		
	public void recycle(){	
		if(stackHandler != null){
			stackHandler.recycle();
			stackHandler = null;
		}
		recycleErrorHandlers();
		//internalConflicts = null; 
		if(contextConflictPool != null)contextConflictPool.clear();
		element.releaseDefinition();
		pool.recycle(this);
	}
	
	public void handleEndElement(Locator locator) throws SAXException{		
		validateContext();
		reportContextErrors(locator);
	}

	//--------------------------------------------------------------------------
	public String toString(){
		String s = "";
		if(stackHandler == null)s= " null";
		else s= stackHandler.toString();
		return "BoundStartValidationHandler "+element.toString()+" "+s;
	}
	
}
