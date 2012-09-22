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

package serene.validation.schema.simplified;

import java.util.Arrays;


class LevelIntermediary extends Level{
	Level parent;	
	LevelIntermediary(Level parent){		
		super();
		this.parent = parent;
		child = new LevelBottom(this);		
	}
	
	LevelIntermediary(int ncIndex,
						int ncSize,
						SNameClass[] nameClasses,
						int ptIndex,
						int ptSize,
						SPattern[] patterns,
						SExceptNameClass exceptNameClass,
						SExceptPattern[] exceptPatterns, 
						Level parent){		
		super();
		this.parent = parent;
		this.ncIndex = ncIndex;
		this.ncSize = ncSize;		
		this.nameClasses = nameClasses;
		this.ptIndex = ptIndex;
		this.ptSize = ptSize;	
		this.patterns = patterns;
		this.exceptNameClass  = exceptNameClass;
		this.exceptPatterns = exceptPatterns;
		child = new LevelBottom(this);
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
		return "0 INTERMEDIARY nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+child.toString(1);		
	}
	
	public void clearAll(){		
		child.clearAll();
		clear();
	}
	
	String toString(int i){
		return i +" INTERMEDIARY nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+child.toString(i+1);
	}	
}