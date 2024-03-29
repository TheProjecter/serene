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

import serene.Constants;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.handlers.error.ErrorDispatcher;

public class RestrictionController{
    
	RRController rrController;
	RController rController;
		
    ControllerPool controllerPool;
    ErrorDispatcher errorDispatcher;
    
    boolean restrictToFileName;

	public RestrictionController(ErrorDispatcher errorDispatcher){
		this.errorDispatcher = errorDispatcher;
        controllerPool = new ControllerPool(errorDispatcher);
	}
	
    public void setRestrictToFileName(boolean restrictToFileName){
        this.restrictToFileName = restrictToFileName;
        if(rrController != null)rrController.setRestrictToFileName(restrictToFileName);
        if(rController != null)rController.setRestrictToFileName(restrictToFileName);
    }
    
	public void control(SimplifiedModel simplifiedModel)throws SAXException{
		if(simplifiedModel.hasRecursions()){
            if(rrController == null){
                rrController = new RRController(controllerPool, errorDispatcher);
                rrController.setRestrictToFileName(restrictToFileName);
            }
			rrController.control(simplifiedModel);
		}else{
            if(rController == null){
                rController = new RController(controllerPool, errorDispatcher);
                rController.setRestrictToFileName(restrictToFileName);
            }
			rController.control(simplifiedModel);
		}
	}
    
    public void setProperty(String name, Object object){
		if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.CONTROLLER_POOL_PROPERTY)){
           // recognized but not set, only for retrieval
        }        
		throw new IllegalArgumentException("Unknown property.");
	}    
    
	public Object getProperty(String name){
		if (name == null) {
            throw new NullPointerException();
        }else if(name.equals(Constants.CONTROLLER_POOL_PROPERTY)){
            return controllerPool;
        }
		throw new IllegalArgumentException("Unknown property.");
	}
}