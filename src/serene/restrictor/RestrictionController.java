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

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.handlers.error.ErrorDispatcher;

import sereneWrite.MessageWriter;

public class RestrictionController{
	RRController rrController;
	RController rController;
		
	MessageWriter debugWriter;
	
	public RestrictionController(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		
		rrController = new RRController(errorDispatcher, debugWriter);
		rController = new RController(errorDispatcher, debugWriter); 
	}
	
	public void control(SimplifiedModel simplifiedModel)throws SAXException{
		if(simplifiedModel.hasRecursions()){
			rrController.control(simplifiedModel);
		}else{
			rController.control(simplifiedModel);
		}
	}
}