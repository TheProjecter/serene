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

package serene.validation.schema.active.util;

import java.util.Arrays;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ANameClass;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AExceptNameClass;

import sereneWrite.MessageWriter;

class LevelIntermediary extends Level{
	Level parent;
	LevelIntermediary(Level parent,
		 				MessageWriter debugWriter){		
		super( debugWriter);
		this.parent = parent;
		child = new LevelBottom(this, debugWriter);
	}
		
	LevelIntermediary(int ncIndex,
						int ncSize,
						ANameClass[] nameClasses,
						int ptIndex,
						int ptSize,
						APattern[] patterns,
						AExceptNameClass exceptNameClass,
						AExceptPattern exceptPattern, 
						Level parent,
						MessageWriter debugWriter){		
		super(debugWriter);
		this.parent = parent;		
		this.ncIndex = ncIndex;		
		this.ncSize = ncSize;		
		this.nameClasses = nameClasses;
		this.ptIndex = ptIndex;
		this.ptSize = ptSize;	
		this.patterns = patterns;
		this.exceptNameClass  = exceptNameClass;
		this.exceptPattern = exceptPattern;
		child = new LevelBottom(this, debugWriter);	
	}
	public boolean isTopLevel(){
		return false;
	}
	public boolean isBottomLevel(){
		return false;
	}
	public Level getLevelUp(){
		return parent;
	}
	
	public Level getLevelDown(){
		return child;
	}
	
	void setChild(Level child){
		this.child = child;
	}
	
	
	public String toString(){
		return "0 INTERMEDIARY "+componentsToString()
				+child.toString(1);		
	}
	
	String toString(int i){
		return i +" INTERMEDIARY "+componentsToString()
				+child.toString(i+1);
	}

	String allToString(){
		return parent.allToString();
	}	
}