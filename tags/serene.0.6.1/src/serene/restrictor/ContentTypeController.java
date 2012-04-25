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
import org.xml.sax.SAXParseException;

import serene.util.IntList;

import serene.validation.schema.simplified.components.SPattern;

import serene.validation.handlers.error.ErrorDispatcher;

import serene.Reusable;

class ContentTypeController extends ContentType implements Reusable{
	
	IntList simpleContent;
	IntList complexContent;
	
	ControllerPool pool;	
	ErrorDispatcher errorDispatcher;
	
    boolean restrictToFileName;
	
	ContentTypeController(ControllerPool pool, ErrorDispatcher errorDispatcher){
		this.errorDispatcher = errorDispatcher;
		this.pool= pool;
		
		simpleContent = new IntList();
		complexContent = new IntList();
	}	
	
	public void recycle(){
		simpleContent.clear();
		complexContent.clear();
		pool.recycle(this);
	}
	
    public void setRestrictToFileName(boolean value){
        restrictToFileName = value;
    }
    
	void add(int childIndex, int contentType){
		switch (contentType){
			case ERROR:
				break;
			case EMPTY:
				break;
			case SIMPLE: 
				simpleContent.add(childIndex);
				break;
			case COMPLEX: 
				complexContent.add(childIndex);
				break;
			default: throw new IllegalArgumentException();
		}
	}
	
	int handle(SPattern context, SPattern[] children)throws SAXException{
		int contentType = EMPTY;
		if(!simpleContent.isEmpty()){
			contentType = SIMPLE;
			if(simpleContent.size() > 1){
				// error 7.2 several simple content types in the same context
				String message = "Restrictions 7.2 error. "
				+"In the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)
				+" several children result in simple content types: ";
				for(int i = 0; i < simpleContent.size(); i++){
					message += "\n<"+children[simpleContent.get(i)].getQName()+"> at "+children[simpleContent.get(i)].getLocation(restrictToFileName);
				}
				message +=".";
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				contentType = ERROR;
			}
			if(!complexContent.isEmpty()){
				// error 7.2 simple and complex content types in the same context
				String message = "Restrictions 7.2 error. "
				+"In the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)
				+" both simple and complex content types were found: "
				+"\nsimple content ";
				for(int i = 0; i < simpleContent.size(); i++){
					message += "\n<"+children[simpleContent.get(i)].getQName()+"> at "+children[simpleContent.get(i)].getLocation(restrictToFileName);
				}
				message+="\ncomplex content ";
				for(int i = 0; i < complexContent.size(); i++){
					message += "\n<"+children[complexContent.get(i)].getQName()+"> at "+children[complexContent.get(i)].getLocation(restrictToFileName);
				}
				message +=".";
				//System.out.println(message);
				errorDispatcher.error(new SAXParseException(message, null));
				contentType = ERROR;
			}
			return contentType;
		}else if(!complexContent.isEmpty()){
			contentType = COMPLEX;
		}
		return contentType;
	}
	
	int handle(SPattern context, SPattern child, int extraContentType)throws SAXException{//for Mixed			
		if(!simpleContent.isEmpty()){
			// error 7.2 simple and complex content types in the same context
			String message = "Restrictions 7.2 error. "
			+"Simple content type in the context of <"+context.getQName()+"> at "+context.getLocation(restrictToFileName)+":"
			+ "\n<"+child.getQName()+"> at "+child.getLocation(restrictToFileName)			
			+".";
			//System.out.println(message);
			errorDispatcher.error(new SAXParseException(message, null));
			return ERROR;
		}else if(!complexContent.isEmpty()){
			return COMPLEX;
		}
		return EMPTY;
	}
}