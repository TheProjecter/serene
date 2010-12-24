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

package serene.validation.schema.parsed.util;

import java.util.Arrays;

import serene.validation.schema.parsed.components.Pattern;
import serene.validation.schema.parsed.components.NameClass;
import serene.validation.schema.parsed.components.Param;
import serene.validation.schema.parsed.components.ExceptNameClass;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.IncludeContent;
import serene.validation.schema.parsed.components.GrammarContent;

import sereneWrite.MessageWriter;

class LevelIntermediary extends Level{
	Level parent;	
	LevelIntermediary(Level parent,
		 				MessageWriter debugWriter){		
		super(debugWriter);
		this.parent = parent;
		child = new LevelBottom(this, debugWriter);
	}
	
	LevelIntermediary(int ncIndex,
						int ncSize,
						NameClass[] nameClasses,
						int ptIndex,
						int ptSize,
						Pattern[] patterns,	
						int prIndex,
						int prSize,
						Param[] params,
						ExceptNameClass exceptNameClass,
						ExceptPattern[] exceptPatterns,
						int icIndex,
						int icSize,
						IncludeContent[] includeContent,
						int gcIndex,
						int gcSize,
						GrammarContent[] grammarContent,
						Level parent,
						MessageWriter debugWriter){		
		super( debugWriter);
		this.parent = parent;
		this.ncIndex = ncIndex;
		this.ncSize = ncSize;		
		this.nameClasses = nameClasses;		
		this.ptIndex = ptIndex;
		this.ptSize = ptSize;	
		this.patterns = patterns;		
		this.prIndex = prIndex;
		this.prSize = prSize;	
		this.params = params;
		this.exceptNameClass = exceptNameClass;
		this.exceptPatterns = exceptPatterns;
		this.icIndex = icIndex;
		this.icSize = icSize;
		this.includeContent = includeContent;
		this.gcIndex = gcIndex;
		this.gcSize = gcSize;
		this.grammarContent = grammarContent;
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
	
	public void clearAll(){
		child.clearAll();
		clear();
	}
	
	public String toString(){
		return "0 INTERMEDIARY nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+" exceptPatterns "+Arrays.toString(exceptPatterns)				
				+child.toString(1);		
	}
	
	String toString(int i){
		return i +" INTERMEDIARY nameClasses "+Arrays.toString(nameClasses)
				+"  patterns "+Arrays.toString(patterns)
				+" exceptPatterns "+Arrays.toString(exceptPatterns)
				+child.toString(i+1);
	}	
}