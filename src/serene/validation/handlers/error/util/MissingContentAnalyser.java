/*
Copyright 2011 Radu Cernuta 

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

package serene.validation.handlers.error.util;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import serene.validation.schema.active.components.AExceptPattern;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AChoicePattern;
import serene.validation.schema.active.components.AInterleave;
import serene.validation.schema.active.components.AGroup;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AEmpty;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.ANotAllowed;
import serene.validation.schema.active.components.ARef;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AGrammar;

import serene.validation.schema.active.components.APattern;

import serene.validation.schema.active.ActiveComponent;
import serene.validation.schema.active.ActiveComponentVisitor;

import serene.validation.schema.active.util.AbstractActiveComponentVisitor;

import serene.util.IntList;

/**
*
* It creates flattened representations of the missing definitions' required 
* subtree and compares them in sort to find the set of definitions that
* represent a subset of all the others.
*
* Creating the subtree IntList representations:
*
* Integer codes for every required component are added in an IntList in presort.
*
* Every type of compositor will be represented by an id.
*
* Compositors that are direct descendants of a similar compositor are not recorded.
*
* Compositors that have only one relevant descendant are not recorded.
*
* For the purpose of comparing the descendants of the interleave and choice 
* compositors are sorted before their respective subtrees are walked. The 
* sorting uses a Comparator based on the same ids as those recorded. There are
* two cases when equalities could be encountered: ref components and compositors.
* The ref components are equivalent, but the compositors will be sorted by 
* considering the first two corresponding relevant descendants with different 
* ids, sorted first where necessary. In case all descendants are equals, the 
* compositors are considered equals, unless there is one that has more 
* descendants than the other, in which case, the one with less descendants is 
* considered lesser than the other.
*
* All ref components that are not recursive (have a non null child) are 
* considered intermediary components and not recorded. 
* 
*/

// No check for nulls, but if there were any you wouldn't be here.
public class MissingContentAnalyser extends AbstractActiveComponentVisitor{
    final static int GROUP = -1;
    final static int INTERLEAVE = -2;
    final static int CHOICE = -3;
    
    boolean groupContext;
    boolean choiceContext;
    boolean interleaveContext;
    
    IntList comparableSubtree;
       
    CacheMap relevantCache;
    SubtreeAnalyser subtreeAnalyser;
    SortingSubtreeAnalyser sortingSubtreeAnalyser;
    
    public MissingContentAnalyser(){
        relevantCache = new CacheMap();
        subtreeAnalyser = new SubtreeAnalyser();
        sortingSubtreeAnalyser = new SortingSubtreeAnalyser();
    }
    
    public BitSet getPartiallyCommon(APattern[] definitions){
        IntList[] comparableSubtrees = new IntList[definitions.length];
        for(int i = 0; i < definitions.length; i++){
            comparableSubtrees[i] = new IntList();
            comparableSubtree = comparableSubtrees[i];
            
            groupContext = false;
            choiceContext = false;
            interleaveContext = false;
            
            definitions[i].accept(this);
        }
        relevantCache.clear();     
        
        
        BitSet partiallyCommon = new BitSet();
        for(int i = 0; i < comparableSubtrees.length; i++){
            if(isShortEnough(i, comparableSubtrees[i].size(), comparableSubtrees)){
                compare(partiallyCommon, i, comparableSubtrees);
            }
        }
        return partiallyCommon;
    }
       
    private boolean isShortEnough(int index, int size, IntList[] lists){
        for(int i = 0; i < lists.length; i++){
            if(i != index && size > lists[i].size())return false; 
        }
        return true;
    }
    
    private void compare(BitSet partiallyCommon, int index, IntList[]  comparableSubtrees){
        for(int i = 0; i < comparableSubtrees.length; i++){
            if(i != index && !matches(comparableSubtrees[index], comparableSubtrees[i])) return;
        }
        partiallyCommon.set(index);
    }
    
    private boolean matches(IntList lesser, IntList greater){
        int utilLength = greater.size() - lesser.size() + 1;        
        int start = lesser.get(0);
        for(int i = 0; i < utilLength; i++){
            if(start == greater.get(i)){                
                if(lesser.size() == 1) return true;
                else if(matches(i, lesser, greater)) return true;
            }
        }
        return false;
    }
    
    private boolean matches(int offset, IntList lesser, IntList greater){
        for(int i = 1; i < lesser.size(); i++){
            if(lesser.get(i) != greater.get(i+offset)) return false;
        }
        return true;
    }
    
	public void visit(AExceptPattern exceptAPattern){
		throw new IllegalStateException();
	}

	
	
	public void visit(AElement element){		
	    if(element.getMinOccurs() == 0) return;
		comparableSubtree.add(element.functionalEquivalenceCode());
	}	
	public void visit(AAttribute attribute){
	    if(attribute.getMinOccurs() == 0) return;
		comparableSubtree.add(attribute.functionalEquivalenceCode());
	}
	public void visit(AChoicePattern choice){
	    if(! choice.isRequiredContent()) return;
        APattern[] children = choice.getChildren();
		
        choiceContext = true;            
        boolean oldGroupContext = groupContext;
        if(groupContext) groupContext = false;
        boolean oldInterleaveContext = interleaveContext;
        if(interleaveContext) interleaveContext = false;
        
        List<APattern> relevant = relevantCache.remove(choice);
        if(relevant == null)relevant = sortingSubtreeAnalyser.getRelevant(children);
                
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(CHOICE);
            next(relevant);
        }
        
        choiceContext = false;
        if(oldGroupContext) groupContext = oldGroupContext;
        if(oldInterleaveContext) interleaveContext = oldInterleaveContext;
	}
	public void visit(AInterleave interleave){
	    if(interleave.getMinOccurs() == 0) return;
		APattern[] children = interleave.getChildren();
		
        boolean oldChoiceContext = choiceContext;            
        if(choiceContext) choiceContext = false;            
        boolean oldGroupContext = groupContext;
        if(groupContext) groupContext = false;
        interleaveContext = true;
        
        List<APattern> relevant = relevantCache.remove(interleave);
        if(relevant == null)relevant = sortingSubtreeAnalyser.getRelevant(children);
        
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(INTERLEAVE);
            next(relevant);
        }
        
        
        if(oldChoiceContext) choiceContext = oldChoiceContext;
        if(oldGroupContext) groupContext = oldGroupContext;
        interleaveContext = false;
	}
	public void visit(AGroup group){
	    if(group.getMinOccurs() == 0) return;		
		APattern[] children = group.getChildren();
		
        boolean oldChoiceContext = choiceContext;            
        if(choiceContext) choiceContext = false;     
        groupContext = true;
        boolean oldInterleaveContext = interleaveContext;
        if(groupContext) interleaveContext = false;
        
        List<APattern> relevant = relevantCache.remove(group);
        if(relevant == null)relevant = subtreeAnalyser.getRelevant(children);
        
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(GROUP);
            next(relevant);
        }
        
        if(oldChoiceContext) choiceContext = oldChoiceContext;
        groupContext = false;
        if(oldInterleaveContext) interleaveContext = oldInterleaveContext;
	}
	public void visit(AListPattern list){
	    if(list.getMinOccurs() == 0) return;
		comparableSubtree.add(list.functionalEquivalenceCode());
	}	
	public void visit(AEmpty empty){}
	public void visit(AText text){}
	public void visit(ANotAllowed notAllowed){
	    throw new IllegalStateException();
	}
	public void visit(ARef ref){	
	    if(ref.getMinOccurs() == 0) return;
	    APattern child = ref.getChild();
	    if(child != null){
	        List<APattern> relevant = relevantCache.get(ref);
            if(relevant == null){
                relevant = subtreeAnalyser.getRelevant(child);
                relevantCache.put(ref, relevant);
            }
            if(!relevant.isEmpty())next(relevant);
	    }else{
	        comparableSubtree.add(ref.functionalEquivalenceCode());
	    }
	}
	public void visit(AValue value){
	    if(value.getMinOccurs() == 0) return;
	    comparableSubtree.add(value.functionalEquivalenceCode());
	}
	public void visit(AData data){
	    if(data.getMinOccurs() == 0) return;
		comparableSubtree.add(data.functionalEquivalenceCode());
	}	
	public void visit(AGrammar grammar){
	    if(grammar.getMinOccurs() == 0) return;
		ActiveComponent child = grammar.getChild();
		child.accept(this);
	}
		
		
	protected void next(ActiveComponent[] children){
		for(ActiveComponent child : children){			
			child.accept(this);
		}
	}
	
	void next(List<APattern> relevantPatterns){
		for(APattern pattern : relevantPatterns){			
			pattern.accept(this);
		}
	}
	
	class SubtreeAnalyser extends AbstractActiveComponentVisitor{
        ArrayList<APattern> relevant;
        SubtreeAnalyser subtreeAnalyser;
        
        SubtreeAnalyser(){}
        
        List<APattern> getRelevant(APattern pattern){
            relevant = new ArrayList<APattern>();
            pattern.accept(this);
            return relevant;
        }
        
        List<APattern> getRelevant(APattern[] patterns){
            relevant = new ArrayList<APattern>();
            for(int i = 0; i < patterns.length; i++){
                patterns[i].accept(this);
            }        
            return relevant;
        }
        
        
        public void visit(AExceptPattern exceptAPattern){
            throw new IllegalStateException();
        }

        
        
        public void visit(AElement element){		
            if(element.getMinOccurs() == 0) return;
            relevant.add(element);
        }	
        public void visit(AAttribute attribute){
            if(attribute.getMinOccurs() == 0) return;
            relevant.add(attribute);
        }
        public void visit(AChoicePattern choice){
            if(! choice.isRequiredContent()) return;
            if(!choiceContext){
                relevant.add(choice);
            }else{
                next(choice.getChildren());
            }            
        }
        public void visit(AInterleave interleave){
            if(interleave.getMinOccurs() == 0) return;
            if(!interleaveContext){
                relevant.add(interleave);
            }else{
                next(interleave.getChildren());
            }
        }
        public void visit(AGroup group){
            if(group.getMinOccurs() == 0) return;
            if(!groupContext){
                relevant.add(group);
            }else{
                next(group.getChildren());
            }        
        }
        public void visit(AListPattern list){
            if(list.getMinOccurs() == 0) return;
            relevant.add(list);
        }	
        public void visit(AEmpty empty){}
        public void visit(AText text){}
        public void visit(ANotAllowed notAllowed){
            throw new IllegalStateException();
        }
        public void visit(ARef ref){	
            if(ref.getMinOccurs() == 0) return;
            APattern child = ref.getChild();
            if(child != null){
                List<APattern> relevant = relevantCache.get(ref);
                if(relevant == null){
                    if(subtreeAnalyser == null) subtreeAnalyser = new SubtreeAnalyser();
                    relevant = subtreeAnalyser.getRelevant(child);
                    relevantCache.put(ref, relevant);
                }
                if(!relevant.isEmpty())this.relevant.addAll(relevant);
            }else{
                relevant.add(ref);
            }
        }
        public void visit(AValue value){
            if(value.getMinOccurs() == 0) return;
            relevant.add(value);
        }
        public void visit(AData data){
            if(data.getMinOccurs() == 0) return;
            relevant.add(data);
        }	
        public void visit(AGrammar grammar){
            if(grammar.getMinOccurs() == 0) return;
            ActiveComponent child = grammar.getChild();
            child.accept(this);
        }
    }
    
    class SortingSubtreeAnalyser extends SubtreeAnalyser{
        Comparator<APattern> patternComparator;
        
        SortingSubtreeAnalyser(){
            super();
            patternComparator = new PatternComparator(); 
        }
        
        List<APattern> getRelevant(APattern pattern){
            throw new IllegalStateException();
        }
        
        List<APattern> getRelevant(APattern[] patterns){
            relevant = new ArrayList<APattern>();
            for(int i = 0; i < patterns.length; i++){
                patterns[i].accept(this);
            }
            Collections.sort(relevant, patternComparator);
            return relevant;
        }        
    }
    
    class PatternComparator implements Comparator<APattern>{
        SortingSubtreeAnalyser sortingSubtreeAnalyser;
        Comparator<APattern> patternComparator;
        SubtreeAnalyser subtreeAnalyser;
        
        public int compare(APattern p1, APattern p2){
            int id1;        
            if(p1 instanceof AGroup)id1 = MissingContentAnalyser.GROUP;
            else if(p1 instanceof AChoicePattern)id1 = MissingContentAnalyser.CHOICE;
            else if(p1 instanceof AInterleave)id1 = MissingContentAnalyser.INTERLEAVE;
            else id1 = p1.functionalEquivalenceCode();
            
            
            int id2;        
            if(p2 instanceof AGroup){
                id2 = MissingContentAnalyser.GROUP;
                if(id1 == id2){
                    return compare((AGroup)p1, (AGroup)p2);
                }
            }else if(p2 instanceof AChoicePattern){
                id2 = MissingContentAnalyser.CHOICE;
                if(id1 == id2){
                    return compare((AChoicePattern)p1, (AChoicePattern)p2);
                }
            }else if(p2 instanceof AInterleave){
                id2 = MissingContentAnalyser.INTERLEAVE;
                if(id1 == id2){
                    return compare((AInterleave)p1, (AInterleave)p2);
                }
            }else{
                id2 = p2.functionalEquivalenceCode();
            }
            
            return (id1 < id2 ? -1 : (id1 == id2 ? 0 : 1));
        }
        
        private int compare(AGroup g1, AGroup g2){
            if(subtreeAnalyser == null) subtreeAnalyser = new SubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
        
            List<APattern> sg1 = subtreeAnalyser.getRelevant(g1.getChildren());
            relevantCache.put(g1, sg1);
            List<APattern> sg2 = subtreeAnalyser.getRelevant(g2.getChildren());
            relevantCache.put(g2, sg2);
            if(sg1.size() < sg2.size()){
                for(int i = 0; i < sg1.size(); i++){
                    int comp = patternComparator.compare(sg1.get(i), sg2.get(i));
                    if(comp != 0) return comp;
                }
                return -1;
            }else if(sg1.size() == sg2.size()){
                for(int i = 0; i < sg1.size(); i++){
                    int comp = patternComparator.compare(sg1.get(i), sg2.get(i));
                    if(comp != 0) return comp;
                }
                return 0;
            }else{
                for(int i = 0; i < sg2.size(); i++){
                    int comp = patternComparator.compare(sg1.get(i), sg2.get(i));
                    if(comp != 0) return comp;
                }
                return 1;
            }
        }
        
        private int compare(AInterleave i1, AInterleave i2){
            if(sortingSubtreeAnalyser == null) sortingSubtreeAnalyser = new SortingSubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
        
            List<APattern> si1 = sortingSubtreeAnalyser.getRelevant(i1.getChildren());
            relevantCache.put(i1, si1);
            List<APattern> si2 = sortingSubtreeAnalyser.getRelevant(i2.getChildren());
            relevantCache.put(i2, si2);
            if(si1.size() < si2.size()){
                for(int i = 0; i < si1.size(); i++){
                    int comp = patternComparator.compare(si1.get(i), si2.get(i));
                    if(comp != 0) return comp;
                }
                return -1;
            }else if(si1.size() == si2.size()){
                for(int i = 0; i < si1.size(); i++){
                    int comp = patternComparator.compare(si1.get(i), si2.get(i));
                    if(comp != 0) return comp;
                }
                return 0;
            }else{
                for(int i = 0; i < si2.size(); i++){
                    int comp = patternComparator.compare(si1.get(i), si2.get(i));
                    if(comp != 0) return comp;
                }
                return 1;
            }
        }
        
        private int compare(AChoicePattern c1, AChoicePattern c2){
            if(sortingSubtreeAnalyser == null) sortingSubtreeAnalyser = new SortingSubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
            
            
            List<APattern> sc1 = sortingSubtreeAnalyser.getRelevant(c1.getChildren());
            relevantCache.put(c1, sc1);
            List<APattern> sc2 = sortingSubtreeAnalyser.getRelevant(c2.getChildren());
            relevantCache.put(c2, sc2);
            if(sc1.size() < sc2.size()){
                for(int i = 0; i < sc1.size(); i++){
                    int comp = patternComparator.compare(sc1.get(i), sc2.get(i));
                    if(comp != 0) return comp;
                }
                return -1;
            }else if(sc1.size() == sc2.size()){
                for(int i = 0; i < sc1.size(); i++){
                    int comp = patternComparator.compare(sc1.get(i), sc2.get(i));
                    if(comp != 0) return comp;
                }
                return 0;
            }else{
                for(int i = 0; i < sc2.size(); i++){
                    int comp = patternComparator.compare(sc1.get(i), sc2.get(i));
                    if(comp != 0) return comp;
                }
                return 1;
            }
        }
    }
    
    class CacheMap{
        HashMap<APattern, List<APattern>> compositorRelevantCache;
        IntList refDefinitionIndexes;
        ArrayList<List<APattern>> refRelevantContent;
        
        CacheMap(){
            compositorRelevantCache = new HashMap<APattern, List<APattern>>();
            refDefinitionIndexes = new IntList();
            refRelevantContent = new ArrayList<List<APattern>>();
        }
        
        void put(ARef ref, List<APattern> list){
            refDefinitionIndexes.add(ref.getDefinitionIndex());
            refRelevantContent.add(list);
        }
        
        void put(AGroup group, List<APattern> list){
            compositorRelevantCache.put(group, list);
        }
        
        void put(AInterleave interleave, List<APattern> list){
            compositorRelevantCache.put(interleave, list);
        }
        
        void put(AChoicePattern choicePattern, List<APattern> list){
            compositorRelevantCache.put(choicePattern, list);
        }
        
        List<APattern> get(ARef ref){
            int refIndex = ref.getDefinitionIndex();
            for(int i = 0; i < refDefinitionIndexes.size(); i++){
                if(refIndex == refDefinitionIndexes.get(i)){
                    return refRelevantContent.get(i);
                }
            }
            return null;
        }
        
        List<APattern> get(AGroup group){
            return compositorRelevantCache.get(group);
        }
        
        List<APattern> get(AInterleave interleave){
            return compositorRelevantCache.get(interleave);
        }
        
        List<APattern> get(AChoicePattern choicePattern){
            return compositorRelevantCache.get(choicePattern);
        }
        
        List<APattern> remove(ARef ref){
            int refIndex = ref.getDefinitionIndex();
            for(int i = 0; i < refDefinitionIndexes.size(); i++){
                if(refIndex == refDefinitionIndexes.get(i)){
                    refDefinitionIndexes.removeFromIndex(i);
                    return refRelevantContent.remove(i);
                }
            }
            return null;
        }
        
        List<APattern> remove(AGroup group){
            return compositorRelevantCache.remove(group);
        }
        
        List<APattern> remove(AInterleave interleave){
            return compositorRelevantCache.remove(interleave);
        }
        
        List<APattern> remove(AChoicePattern choicePattern){
            return compositorRelevantCache.remove(choicePattern);
        }
        
        void clear(){
            compositorRelevantCache.clear();
            refDefinitionIndexes.clear();
            refRelevantContent.clear();   
        }
    }
}
