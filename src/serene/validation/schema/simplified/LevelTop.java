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

class LevelTop extends Level{
	
	LevelTop(){
		super();
		child = new LevelBottom(this);
	}	
	
	public boolean isTopLevel(){
		return true;
	}
	public boolean isBottomLevel(){
		return false;
	}

	public Level getLevelDown(){
		return child;			
	}
	public Level getLevelUp(){
		throw new IllegalStateException();			
	}
	
	void setChild(Level child){
		this.child = child;
	}	
	
	public void clearAll(){		
		child.clearAll();
		clear();
	}
	
	public String toString(){
		return "0 TOP nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+child.toString(1);		
	}
	
	String toString(int i){
		return i+" TOP nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+child.toString(i+1);		
	}
}
