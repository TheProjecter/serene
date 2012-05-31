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

import serene.validation.schema.active.components.APattern;


class LevelBottom extends Level{
	Level parent;
	LevelBottom(Level parent){		
		super( );
		this.parent = parent;		
	}
	
	public boolean isTopLevel(){
		return false;
	}
	public boolean isBottomLevel(){
		return true;
	}
	
	public Level getLevelUp(){
		return parent;
	}
	
	public Level getLevelDown(){
		Level intermediary = new LevelIntermediary(
													ptIndex,
													ptSize,
													patterns,
													exceptPattern,
													parent);
		parent.setChild(intermediary);
		return intermediary.getLevelDown();
	}
	
	void setChild(Level next){
		throw new IllegalStateException();
	}
		
	public void clearAll(){
		clear();	
	}
	
	public String toString(){
		return "0 BOTTOM "+componentsToString();	
	}
	
	String toString(int i){
		return i +" BOTTOM "+componentsToString();
	}
	
	String allToString(){
		return parent.allToString();
	}
}