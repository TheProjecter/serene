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

package serene.restrictor;

import serene.validation.handlers.error.ErrorDispatcher;
import sereneWrite.MessageWriter;

class ControllerPool{
	ContentTypeController[] ctc;
	int ctcFree;
	int ctcSize;
	
	AttributeNamingController[] anc;
	int ancFree;
	int ancSize;
	
	ElementNamingController[] enc;
	int encFree;
	int encSize;
	
	
	AttributeLimitationNamingController[] alnc;
	int alncFree;
	int alncSize;
	
	ElementLimitationNamingController[] elnc;
	int elncFree;
	int elncSize;
		
	OverlapController[] oc;
	int ocFree;
	int ocSize;
	
	ErrorDispatcher errorDispatcher;
	MessageWriter debugWriter;
	
	ControllerPool(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		this.errorDispatcher = errorDispatcher;
		
		ctcFree = 0;
		ctcSize = 5;
		ctc = new ContentTypeController[ctcSize];
		
		ancFree = 0;
		ancSize = 5;
		anc = new AttributeNamingController[ancSize];
		
		encFree = 0;
		encSize = 5;
		enc = new ElementNamingController[encSize];
		
		alncFree = 0;
		alncSize = 5;
		alnc = new AttributeLimitationNamingController[alncSize];
		
		elncFree = 0;
		elncSize = 5;
		elnc = new ElementLimitationNamingController[elncSize];
				
		ocFree = 0;
		ocSize = 5;
		oc = new OverlapController[ocSize];
	}
	
	ContentTypeController getContentTypeController(){
		if(ctcFree == 0){
			return new ContentTypeController(this, errorDispatcher, debugWriter);
		}else{			
			return ctc[--ctcFree];
		}		
	}
	
	void recycle(ContentTypeController controller){
		if(ctcFree == ctcSize){
			ContentTypeController[] increased = new ContentTypeController[++ctcSize];
			System.arraycopy(ctc, 0, increased, 0, ctcFree);
			ctc = increased;
		}
		ctc[ctcFree++] = controller;
	}
	
	AttributeNamingController getAttributeNamingController(){
		if(ancFree == 0){
			return new AttributeNamingController(this, errorDispatcher, debugWriter);
		}else{			
			return anc[--ancFree];
		}		
	}
	
	void recycle(AttributeNamingController controller){
		if(ancFree == ancSize){
			AttributeNamingController[] increased = new AttributeNamingController[++ancSize];
			System.arraycopy(anc, 0, increased, 0, ancFree);
			anc = increased;
		}
		anc[ancFree++] = controller;
	}
	
	ElementNamingController getElementNamingController(){
		if(encFree == 0){
			return new ElementNamingController(this, errorDispatcher, debugWriter);
		}else{			
			return enc[--encFree];
		}		
	}
	
	void recycle(ElementNamingController controller){
		if(encFree == encSize){
			ElementNamingController[] increased = new ElementNamingController[++encSize];
			System.arraycopy(enc, 0, increased, 0, encFree);
			enc = increased;
		}
		enc[encFree++] = controller;
	}
	
	
	AttributeLimitationNamingController getAttributeLimitationNamingController(){
		if(alncFree == 0){
			return new AttributeLimitationNamingController(this, errorDispatcher, debugWriter);
		}else{			
			return alnc[--alncFree];
		}		
	}
	
	void recycle(AttributeLimitationNamingController controller){
		if(alncFree == alncSize){
			AttributeLimitationNamingController[] ilncreased = new AttributeLimitationNamingController[++alncSize];
			System.arraycopy(alnc, 0, ilncreased, 0, alncFree);
			alnc = ilncreased;
		}
		alnc[alncFree++] = controller;
	}
	
	ElementLimitationNamingController getElementLimitationNamingController(){
		if(elncFree == 0){
			return new ElementLimitationNamingController(this, errorDispatcher, debugWriter);
		}else{			
			return elnc[--elncFree];
		}		
	}
	
	void recycle(ElementLimitationNamingController controller){
		if(elncFree == elncSize){
			ElementLimitationNamingController[] ilncreased = new ElementLimitationNamingController[++elncSize];
			System.arraycopy(elnc, 0, ilncreased, 0, elncFree);
			elnc = ilncreased;
		}
		elnc[elncFree++] = controller;
	}
		
	OverlapController getOverlapController(){
		if(ocFree == 0){
			return new OverlapController(this, debugWriter);
		}else{			
			return oc[--ocFree];
		}		
	}
	
	void recycle(OverlapController controller){
		if(ocFree == ocSize){
			OverlapController[] increased = new OverlapController[++ocSize];
			System.arraycopy(oc, 0, increased, 0, ocFree);
			oc = increased;
		}
		oc[ocFree++] = controller;
	}
}