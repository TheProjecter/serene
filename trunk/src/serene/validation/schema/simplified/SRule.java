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

import serene.bind.util.DocumentIndexedData;

public abstract class SRule extends SComponent implements SimplifiedComponent{
    public static final int UNBOUNDED = -1;
	SRule parent;
	SRule(int recordIndex, DocumentIndexedData documentIndexedData){
		super(recordIndex, documentIndexedData);
	}
	
	public abstract void accept(SimplifiedRuleVisitor v);
	
	public int functionalEquivalenceCode(){
        return hashCode();
    } 
	public SRule getParent(){		 
		return parent;				
	}
	public abstract int getChildrenCount();
	public abstract SPattern getChild(int childIndex);
	
	abstract public boolean isRequired();
	public abstract boolean isChildRequired(int childIndex);
	
	public boolean specifiesOrder(){
	    return false;
	}
	public boolean specifiesInterleaving(){
	    return false;
	}
	public boolean isIntermediary(){
	    return false;
	}
	public boolean hasMultipleCardinality(){
	    return false;
	}
	
	void setParent(SRule parent, int childIndex){		 
		this.parent = parent;				
		this.childIndex = childIndex;
	}
	
	boolean containsRecursion(){
        return false;
    }
    
 	void setAllowsElements(){
 	    throw new IllegalStateException();
 	}
	void setAllowsAttributes(){
 	    throw new IllegalStateException();
 	}
	void setAllowsDatas(){
 	    throw new IllegalStateException();
 	}
	void setAllowsValues(){
 	    throw new IllegalStateException();
 	}	
	void setAllowsListPatterns(){
 	    throw new IllegalStateException();
 	}
	void setAllowsText(){
 	    throw new IllegalStateException();
 	}
 	void setContainsRecursion(){
 	    throw new IllegalStateException();
 	}
}	
