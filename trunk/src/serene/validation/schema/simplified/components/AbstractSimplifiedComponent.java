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

package serene.validation.schema.simplified.components;

import java.io.File;

import serene.validation.schema.simplified.SimplifiedComponentVisitor;
import serene.validation.schema.simplified.SimplifiedComponent;

import sereneWrite.MessageWriter;

abstract class AbstractSimplifiedComponent implements SimplifiedComponent{
	protected int childIndex;	
	protected SimplifiedComponent parent;
		
	protected String qName;
	protected String location;	
	protected MessageWriter debugWriter;	
	
	public AbstractSimplifiedComponent(String qName, String location, MessageWriter debugWriter){		
		this.debugWriter = debugWriter;
		this.qName = qName;
		this.location = location;
		
		childIndex = -1;		
	}

	void setParent(SimplifiedComponent parent){		 
		this.parent = parent;				
	}
	
	public SimplifiedComponent getParent(){
		return parent;
	}
	
	void setChildIndex(int childIndex){			
		this.childIndex = childIndex;
	}	
	
	public int getChildIndex(){
		return childIndex;
	}
	
	public String getQName(){
		return qName;
	}
	public String getLocation(boolean restrictToFileName){
        if(location == null || !restrictToFileName)return location;
        int nameIndex = location.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = location.lastIndexOf('/')+1;
        return location.substring(nameIndex);
	}
}	