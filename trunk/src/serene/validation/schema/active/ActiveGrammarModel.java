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

package serene.validation.schema.active;

import java.util.Map;
import java.util.List;
import java.util.Arrays;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeException;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.simplified.SimplifiedPattern;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SDummy;

import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.ActiveComponentBuilder;

import serene.validation.schema.Identifier;

import serene.util.ObjectIntHashMap;

public class ActiveGrammarModel implements SimplifiedComponentVisitor{
	
	int startElementIndex;
	AElement startElement;
	
	SimplifiedPattern[] refOriginalTopPatterns;
	APattern[][] refDefinitionTopPattern;
	int[] refDefinitionTopPatternFree;
	
	// Definitions corresponding to the same element are placed 
	// at the same indexes.
	SElement[] originalElementDefinitions;
	APattern[][] elementDefinitionTopPattern;
	int[] elementDefinitionTopPatternFree;
	
	SAttribute[] originalAttributeDefinitions;
	APattern[][] attributeDefinitionTopPattern;
	int[] attributeDefinitionTopPatternFree;
	
	
	SExceptPattern[] originalExceptPatternDefinitions;
	APattern[][] exceptPatternDefinitionTopPattern;
	int[] exceptPatternDefinitionTopPatternFree;
	
	ActiveComponentBuilder builder;
	final int TOP_PATTERN_POOL_MAX_SIZE = 20;
	
	boolean currentAllowsElements;
    boolean currentAllowsAttributes;
    boolean currentAllowsDatas;
    boolean currentAllowsValues;	
    boolean currentAllowsListPatterns;
    boolean currentAllowsText;
    
    boolean isBuilding;
	
	ActiveGrammarModel(int startElementIndex,
	                SimplifiedPattern[] refOriginalTopPatterns,
	                SElement[] originalElementDefinitions,
	                SAttribute[] originalAttributeDefinitions,
	                SExceptPattern[] originalExceptPatternDefinitions,
	                ActiveComponentBuilder builder){
	    this.builder = builder;
	    this.startElementIndex = startElementIndex;
	    
	    this.refOriginalTopPatterns = refOriginalTopPatterns;
	    this.originalElementDefinitions = originalElementDefinitions;
	    this.originalAttributeDefinitions = originalAttributeDefinitions;
	    this.originalExceptPatternDefinitions = originalExceptPatternDefinitions;
	    
	    if(refOriginalTopPatterns != null){
            refDefinitionTopPattern = new APattern[refOriginalTopPatterns.length][];
            refDefinitionTopPatternFree = new int[refOriginalTopPatterns.length];
            Arrays.fill(refDefinitionTopPatternFree, 0);
        }
        
        if(originalElementDefinitions != null){
            elementDefinitionTopPattern = new APattern[originalElementDefinitions.length][];
            elementDefinitionTopPatternFree = new int[originalElementDefinitions.length];
            Arrays.fill(elementDefinitionTopPatternFree, 0);
        }
                
        if(originalAttributeDefinitions != null){
            attributeDefinitionTopPattern = new APattern[originalAttributeDefinitions.length][];
            attributeDefinitionTopPatternFree = new int[originalAttributeDefinitions.length];
            Arrays.fill(attributeDefinitionTopPatternFree, 0);
        }
        
        if(originalExceptPatternDefinitions != null){
            exceptPatternDefinitionTopPattern = new APattern[originalExceptPatternDefinitions.length][];
            exceptPatternDefinitionTopPatternFree = new int[originalExceptPatternDefinitions.length];
            Arrays.fill(exceptPatternDefinitionTopPatternFree, 0);
        }
	}
	
	public AElement getStartElement(){
	    if(startElement != null )return startElement;	    
	    
	    builder.startBuild();
	    startElement = builder.buildElement(startElementIndex, null, this, originalElementDefinitions[startElementIndex]);
		
		return startElement;
	} 
	
    public void setSimplifiedElementDefinitions(String namespace, String name, List<SimplifiedComponent> unexpectedMatches){
        if(originalElementDefinitions == null || originalElementDefinitions.length == 0) return;
        
        for(int i = 0; i < originalElementDefinitions.length; i++){
            if(originalElementDefinitions[i] != null){
                Identifier ii = originalElementDefinitions[i].getNameClass();
                if(ii != null
                    && ii.matches(namespace, name)){
                    unexpectedMatches.add(originalElementDefinitions[i]);
                }
            }
        }
    }
        
    public void setSimplifiedAttributeDefinitions(String namespace, String name, List<SimplifiedComponent> unexpectedMatches){
        if(originalAttributeDefinitions == null || originalAttributeDefinitions.length == 0) return;
        
        for(int i = 0; i < originalAttributeDefinitions.length; i++){
            if(originalAttributeDefinitions[i] != null){
                Identifier ii = originalAttributeDefinitions[i].getNameClass();
                if(ii != null
                    && ii.matches(namespace, name)){
                    unexpectedMatches.add(originalAttributeDefinitions[i]);
                }
            }
        }
    }
    
    
    public APattern getRefDefinitionTopPattern(int definitionIndex){
        if(refDefinitionTopPatternFree[definitionIndex] == 0){
            if(refOriginalTopPatterns[definitionIndex] == null) return null;
            return createDefinitionTopPattern(refOriginalTopPatterns[definitionIndex]);
        }else{
            refDefinitionTopPatternFree[definitionIndex] -= 1;
            return refDefinitionTopPattern[definitionIndex][refDefinitionTopPatternFree[definitionIndex]]; 
        }
    }
    
    public void recycleRefDefinitionTopPattern(int definitionIndex, APattern pattern){
        if(refDefinitionTopPattern[definitionIndex] == null) refDefinitionTopPattern[definitionIndex] = new APattern[TOP_PATTERN_POOL_MAX_SIZE];
        else if(refDefinitionTopPatternFree[definitionIndex] == TOP_PATTERN_POOL_MAX_SIZE) return;
        
        refDefinitionTopPattern[definitionIndex][refDefinitionTopPatternFree[definitionIndex]] = pattern;
        refDefinitionTopPatternFree[definitionIndex] += 1;
    }
    
    public APattern getElementDefinitionTopPattern(int definitionIndex){
        if(elementDefinitionTopPatternFree[definitionIndex] == 0){
            SimplifiedPattern top = originalElementDefinitions[definitionIndex].getChild(); 
            if(top == null)return null;
            return createDefinitionTopPattern(top);
        }else{
            elementDefinitionTopPatternFree[definitionIndex] -= 1;
            return elementDefinitionTopPattern[definitionIndex][elementDefinitionTopPatternFree[definitionIndex]];
        }
    }
    
    public void recycleElementDefinitionTopPattern(int definitionIndex, APattern pattern){        
        if(elementDefinitionTopPattern[definitionIndex] == null) elementDefinitionTopPattern[definitionIndex] = new APattern[TOP_PATTERN_POOL_MAX_SIZE];
        else if(elementDefinitionTopPatternFree[definitionIndex] == TOP_PATTERN_POOL_MAX_SIZE) return; 
        
        elementDefinitionTopPattern[definitionIndex][elementDefinitionTopPatternFree[definitionIndex]] = pattern;
        elementDefinitionTopPatternFree[definitionIndex] += 1;
    }
    
    public APattern getAttributeDefinitionTopPattern(int definitionIndex){
        if(attributeDefinitionTopPatternFree[definitionIndex] == 0){
            SimplifiedPattern top = originalAttributeDefinitions[definitionIndex].getChild(0);// TODO review this 
            if(top == null)return null;
            return createDefinitionTopPattern(top);
        }else{
            attributeDefinitionTopPatternFree[definitionIndex] -= 1;
            return attributeDefinitionTopPattern[definitionIndex][attributeDefinitionTopPatternFree[definitionIndex]]; 
        }
    }
    
    public void recycleAttributeDefinitionTopPattern(int definitionIndex, APattern pattern){
        if(attributeDefinitionTopPattern[definitionIndex] == null) attributeDefinitionTopPattern[definitionIndex] = new APattern[TOP_PATTERN_POOL_MAX_SIZE];
        else if(attributeDefinitionTopPatternFree[definitionIndex] == TOP_PATTERN_POOL_MAX_SIZE) return; 
        
        attributeDefinitionTopPattern[definitionIndex][attributeDefinitionTopPatternFree[definitionIndex]] = pattern;
        attributeDefinitionTopPatternFree[definitionIndex] += 1;
    }
    
    
    public APattern getExceptPatternDefinitionTopPattern(int definitionIndex){
        if(exceptPatternDefinitionTopPatternFree[definitionIndex] == 0){
            SimplifiedPattern top = originalExceptPatternDefinitions[definitionIndex].getChild(); 
            if(top == null)return null;
            return createDefinitionTopPattern(top);
        }else{
            exceptPatternDefinitionTopPatternFree[definitionIndex] -= 1;
            return exceptPatternDefinitionTopPattern[definitionIndex][exceptPatternDefinitionTopPatternFree[definitionIndex]]; 
        }
    }
    
    public void recycleExceptPatternDefinitionTopPattern(int definitionIndex, APattern pattern){
        if(exceptPatternDefinitionTopPattern[definitionIndex] == null) exceptPatternDefinitionTopPattern[definitionIndex] = new APattern[TOP_PATTERN_POOL_MAX_SIZE];
        else if(exceptPatternDefinitionTopPatternFree[definitionIndex] == TOP_PATTERN_POOL_MAX_SIZE) return; 
        
        exceptPatternDefinitionTopPattern[definitionIndex][exceptPatternDefinitionTopPatternFree[definitionIndex]] = pattern;
        exceptPatternDefinitionTopPatternFree[definitionIndex] += 1;
    }
    
    
    APattern createDefinitionTopPattern(SimplifiedPattern originalTopPattern){
        
        boolean telescope = false; 
        boolean oldIsBuilding = isBuilding;
        
        if(isBuilding){       
            builder = builder.getChild();
            telescope = true;
        }else{            
            isBuilding = true;
            
            currentAllowsElements = false;
            currentAllowsAttributes = false;
            currentAllowsDatas = false;
            currentAllowsValues = false;	
            currentAllowsListPatterns = false;
            currentAllowsText = false;
        }
        
        builder.startBuild();
        originalTopPattern.accept(this);		
        APattern definitionTopPattern = builder.getCurrentPattern();
        
        isBuilding = oldIsBuilding;
        if(telescope) builder = builder.getParent();
		return definitionTopPattern;
    }
    
        
    public void visit(SExceptPattern exceptPattern){		
		builder.buildExceptPattern(exceptPattern.getDefinitionIndex(), this, exceptPattern);
	}
	public void visit(SExceptNameClass exceptNameClass){
		/*SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) nextLevel(child);
		builder.buildExceptNameClass(exceptNameClass);*/
		throw new IllegalStateException();
	}
		
	public void visit(SName name){
		throw new IllegalStateException();
	}
	public void visit(SAnyName anyName){		
		throw new IllegalStateException();
	}
	public void visit(SNsName nsName){
		throw new IllegalStateException();
	}	
	public void visit(SChoiceNameClass choice){
		throw new IllegalStateException();
	}	
	
	
	public void visit(SElement element){	
		builder.buildElement(element.getDefinitionIndex(), element.getNameClass(), this, element);
		currentAllowsElements = true;
	}	
	public void visit(SAttribute attribute){	
		builder.buildAttribute(attribute.getDefinitionIndex(), attribute.getNameClass(), this, attribute);
		currentAllowsAttributes = true;
	}
	public void visit(SChoicePattern choice){
	    boolean allowsElements = false;
        boolean allowsAttributes = false;
        boolean allowsDatas = false;
        boolean allowsValues = false;	
        boolean allowsListPatterns = false;
        boolean allowsText = false;        
        
		SimplifiedComponent[] children = choice.getChildren();
		if(children != null){
		    builder.startLevel();{			
                for(SimplifiedComponent child : children){            
                    child.accept(this);
                    if(!allowsElements && currentAllowsElements) allowsElements = currentAllowsElements;
                    if(!allowsAttributes && currentAllowsAttributes) allowsAttributes = currentAllowsAttributes;
                    if(!allowsDatas && currentAllowsDatas) allowsDatas = currentAllowsDatas;
                    if(!allowsValues && currentAllowsValues) allowsValues = currentAllowsValues;
                    if(!allowsListPatterns && currentAllowsListPatterns) allowsListPatterns = currentAllowsListPatterns;
                    if(!allowsText && currentAllowsText) allowsText = currentAllowsText;
                }
            }builder.endLevel();
		}
		builder.buildChoicePattern(allowsElements,
                            allowsAttributes,
                            allowsDatas,
                            allowsValues,	
                            allowsListPatterns,
                            allowsText,
                            choice);
        
        currentAllowsElements = allowsElements;
        currentAllowsAttributes = allowsAttributes;
        currentAllowsDatas = allowsDatas;
        currentAllowsValues = allowsValues;
        currentAllowsListPatterns = allowsListPatterns;
        currentAllowsText = allowsListPatterns;
	}
	public void visit(SInterleave interleave){
	    boolean allowsElements = false;
        boolean allowsAttributes = false;
        boolean allowsDatas = false;
        boolean allowsValues = false;	
        boolean allowsListPatterns = false;
        boolean allowsText = false;        
        
		SimplifiedComponent[] children = interleave.getChildren();
		if(children != null){
		    builder.startLevel();{			
                for(SimplifiedComponent child : children){            
                    child.accept(this);
                    if(!allowsElements && currentAllowsElements) allowsElements = currentAllowsElements;
                    if(!allowsAttributes && currentAllowsAttributes) allowsAttributes = currentAllowsAttributes;
                    if(!allowsDatas && currentAllowsDatas) allowsDatas = currentAllowsDatas;
                    if(!allowsValues && currentAllowsValues) allowsValues = currentAllowsValues;
                    if(!allowsListPatterns && currentAllowsListPatterns) allowsListPatterns = currentAllowsListPatterns;
                    if(!allowsText && currentAllowsText) allowsText = currentAllowsText;
                }
            }builder.endLevel();
		}
		builder.buildInterleave(allowsElements,
                            allowsAttributes,
                            allowsDatas,
                            allowsValues,	
                            allowsListPatterns,
                            allowsText,
                            interleave);
		
		currentAllowsElements = allowsElements;
        currentAllowsAttributes = allowsAttributes;
        currentAllowsDatas = allowsDatas;
        currentAllowsValues = allowsValues;
        currentAllowsListPatterns = allowsListPatterns;
        currentAllowsText = allowsListPatterns;
	}
	public void visit(SGroup group){
		boolean allowsElements = false;
        boolean allowsAttributes = false;
        boolean allowsDatas = false;
        boolean allowsValues = false;	
        boolean allowsListPatterns = false;
        boolean allowsText = false;        
        
        SimplifiedComponent[] children = group.getChildren();
		if(children != null){
		    builder.startLevel();{			
                for(SimplifiedComponent child : children){            
                    child.accept(this);
                    if(!allowsElements && currentAllowsElements) allowsElements = currentAllowsElements;
                    if(!allowsAttributes && currentAllowsAttributes) allowsAttributes = currentAllowsAttributes;
                    if(!allowsDatas && currentAllowsDatas) allowsDatas = currentAllowsDatas;
                    if(!allowsValues && currentAllowsValues) allowsValues = currentAllowsValues;
                    if(!allowsListPatterns && currentAllowsListPatterns) allowsListPatterns = currentAllowsListPatterns;
                    if(!allowsText && currentAllowsText) allowsText = currentAllowsText;
                }
            }builder.endLevel();
		}
		builder.buildGroup(allowsElements,
                            allowsAttributes,
                            allowsDatas,
                            allowsValues,	
                            allowsListPatterns,
                            allowsText,
                            group);
		
		currentAllowsElements = allowsElements;
        currentAllowsAttributes = allowsAttributes;
        currentAllowsDatas = allowsDatas;
        currentAllowsValues = allowsValues;
        currentAllowsListPatterns = allowsListPatterns;
        currentAllowsText = allowsListPatterns;
	}
	public void visit(SMixed mixed){	    
		builder.startLevel();
		SimplifiedComponent child = mixed.getChild();
		if(child != null) next(child);
		builder.buildText(mixed);		
		builder.endLevel();			
		
		currentAllowsText = true;
		
		builder.buildInterleave(currentAllowsElements,
                            currentAllowsAttributes,
                            currentAllowsDatas,
                            currentAllowsValues,	
                            currentAllowsListPatterns,
                            currentAllowsText,
                            mixed);
	}	
	public void visit(SListPattern list){	    
		SimplifiedComponent child = list.getChild();
		if(child != null) nextLevel(child);
		builder.buildListPattern(currentAllowsElements,
                            currentAllowsAttributes,
                            currentAllowsDatas,
                            currentAllowsValues,	
                            currentAllowsListPatterns,
                            currentAllowsText,
                            list);
		
		currentAllowsListPatterns = true;
	}	
	public void visit(SEmpty empty){
		builder.buildEmpty(empty);
	}
	public void visit(SText text){	    
		builder.buildText(text);		
		currentAllowsText = true;
	}
	public void visit(SNotAllowed notAllowed){
        // TODO review, 
        // NOTE if it changes, endValidation must change too
        builder.buildEmpty(notAllowed);
		//builder.buildNotAllowed(notAllowed.getQName(), notAllowed.getLocation());
	}
	public void visit(SRef ref){
	    
		builder.buildRef(ref.getDefinitionIndex(), 
                            this, 
                            ref);
        ARef aref = (ARef)builder.getCurrentPattern();
        
        aref.assembleDefinition();
	}
	public void visit(SValue value){
		builder.buildValue(value.getNamespaceURI(), value.getDatatype(), value.getCharContent(), this, value);
		
		currentAllowsValues = true;
	}
	public void visit(SData data){	
		SimplifiedComponent[] exceptPattern = data.getExceptPattern();
		if(exceptPattern != null) nextLevel(exceptPattern[0]);//if it's a correct schema ther should be only one
		builder.buildData(data.getDatatype(), this, data);
		
		currentAllowsDatas = true;
	}	
	public void visit(SGrammar grammar){
		SimplifiedComponent child = grammar.getChild();
		if(child != null) nextLevel(child);
		builder.buildGrammar(currentAllowsElements,
                            currentAllowsAttributes,
                            currentAllowsDatas,
                            currentAllowsValues,	
                            currentAllowsListPatterns,
                            currentAllowsText,
                            grammar);
	}
		
	public void visit(SDummy dummy){
		throw new IllegalStateException();
	}
	
	private void next(SimplifiedComponent child){
		child.accept(this);
	}

	private void nextLevel(SimplifiedComponent child){
		builder.startLevel();{			
			child.accept(this);
		}builder.endLevel();
	}
	
}