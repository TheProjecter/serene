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

import serene.validation.schema.simplified.SExceptPattern;

import serene.validation.schema.simplified.SElement;
import serene.validation.schema.simplified.SAttribute;
import serene.validation.schema.simplified.SChoicePattern;
import serene.validation.schema.simplified.SInterleave;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SListPattern;
import serene.validation.schema.simplified.SEmpty;
import serene.validation.schema.simplified.SText;
import serene.validation.schema.simplified.SNotAllowed;
import serene.validation.schema.simplified.SRef;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SValue;
import serene.validation.schema.simplified.SGrammar;
import serene.validation.schema.simplified.SDummy;

import serene.validation.schema.simplified.SPattern;

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.simplified.util.AbstractSimplifiedComponentVisitor;

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
public class MissingContentAnalyser extends AbstractSimplifiedComponentVisitor{
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
    
    public BitSet getPartiallyCommon(SPattern[] definitions){
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
    
	public void visit(SExceptPattern exceptSPattern){
		throw new IllegalStateException();
	}

	
	
	public void visit(SElement element){		
	    if(element.getMinOccurs() == 0) return;
		comparableSubtree.add(element.functionalEquivalenceCode());
	}	
	public void visit(SAttribute attribute){
	    if(attribute.getMinOccurs() == 0) return;
		comparableSubtree.add(attribute.functionalEquivalenceCode());
	}
	public void visit(SChoicePattern choice){
	    if(!choice.isRequiredContent()) return;
        SPattern[] children = choice.getChildren();
		
        choiceContext = true;            
        boolean oldGroupContext = groupContext;
        if(groupContext) groupContext = false;
        boolean oldInterleaveContext = interleaveContext;
        if(interleaveContext) interleaveContext = false;
        
        List<SPattern> relevant = relevantCache.remove(choice);
        if(relevant == null)relevant = sortingSubtreeAnalyser.getRelevant(children);
                
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(CHOICE);
            next(relevant);
        }
        
        choiceContext = false;
        if(oldGroupContext) groupContext = oldGroupContext;
        if(oldInterleaveContext) interleaveContext = oldInterleaveContext;
	}
	public void visit(SInterleave interleave){
	    if(interleave.getMinOccurs() == 0) return;
		SPattern[] children = interleave.getChildren();
		
        boolean oldChoiceContext = choiceContext;            
        if(choiceContext) choiceContext = false;            
        boolean oldGroupContext = groupContext;
        if(groupContext) groupContext = false;
        interleaveContext = true;
        
        List<SPattern> relevant = relevantCache.remove(interleave);
        if(relevant == null)relevant = sortingSubtreeAnalyser.getRelevant(children);
        
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(INTERLEAVE);
            next(relevant);
        }
        
        
        if(oldChoiceContext) choiceContext = oldChoiceContext;
        if(oldGroupContext) groupContext = oldGroupContext;
        interleaveContext = false;
	}
	public void visit(SGroup group){
	    if(group.getMinOccurs() == 0) return;		
		SPattern[] children = group.getChildren();
		
        boolean oldChoiceContext = choiceContext;            
        if(choiceContext) choiceContext = false;     
        groupContext = true;
        boolean oldInterleaveContext = interleaveContext;
        if(groupContext) interleaveContext = false;
        
        List<SPattern> relevant = relevantCache.remove(group);
        if(relevant == null)relevant = subtreeAnalyser.getRelevant(children);
        
        if(!relevant.isEmpty()){
            if(relevant.size() > 1) comparableSubtree.add(GROUP);
            next(relevant);
        }
        
        if(oldChoiceContext) choiceContext = oldChoiceContext;
        groupContext = false;
        if(oldInterleaveContext) interleaveContext = oldInterleaveContext;
	}
	public void visit(SListPattern list){
	    if(list.getMinOccurs() == 0) return;
		comparableSubtree.add(list.functionalEquivalenceCode());
	}	
	public void visit(SEmpty empty){}
	public void visit(SText text){}
	public void visit(SNotAllowed notAllowed){
	    throw new IllegalStateException();
	}
	public void visit(SRef ref){	
	    if(ref.getMinOccurs() == 0) return;
	    SPattern child = ref.getChild();
	    if(child != null){
	        List<SPattern> relevant = relevantCache.get(ref);
            if(relevant == null){
                relevant = subtreeAnalyser.getRelevant(child);
                relevantCache.put(ref, relevant);
            }
            if(!relevant.isEmpty())next(relevant);
	    }else{
	        comparableSubtree.add(ref.functionalEquivalenceCode());
	    }
	}
	public void visit(SValue value){
	    if(value.getMinOccurs() == 0) return;
	    comparableSubtree.add(value.functionalEquivalenceCode());
	}
	public void visit(SData data){
	    if(data.getMinOccurs() == 0) return;
		comparableSubtree.add(data.functionalEquivalenceCode());
	}	
	public void visit(SGrammar grammar){
	    if(grammar.getMinOccurs() == 0) return;
		SimplifiedComponent child = grammar.getChild();
		child.accept(this);
	}
	public void visit(SDummy dummy){
        throw new IllegalStateException();
    }	
		
	protected void next(SimplifiedComponent[] children){
		for(SimplifiedComponent child : children){			
			child.accept(this);
		}
	}
	
	void next(List<SPattern> relevantPatterns){
		for(SPattern pattern : relevantPatterns){			
			pattern.accept(this);
		}
	}
	
	class SubtreeAnalyser extends AbstractSimplifiedComponentVisitor{
        ArrayList<SPattern> relevant;
        SubtreeAnalyser subtreeAnalyser;
        
        SubtreeAnalyser(){}
        
        List<SPattern> getRelevant(SPattern pattern){
            relevant = new ArrayList<SPattern>();
            pattern.accept(this);
            return relevant;
        }
        
        List<SPattern> getRelevant(SPattern[] patterns){
            relevant = new ArrayList<SPattern>();
            for(int i = 0; i < patterns.length; i++){
                patterns[i].accept(this);
            }        
            return relevant;
        }
        
        
        public void visit(SExceptPattern exceptSPattern){
            throw new IllegalStateException();
        }

        
        
        public void visit(SElement element){		
            if(element.getMinOccurs() == 0) return;
            relevant.add(element);
        }	
        public void visit(SAttribute attribute){
            if(attribute.getMinOccurs() == 0) return;
            relevant.add(attribute);
        }
        public void visit(SChoicePattern choice){
            if(! choice.isRequiredContent()) return;
            if(!choiceContext){
                relevant.add(choice);
            }else{
                next(choice.getChildren());
            }            
        }
        public void visit(SInterleave interleave){
            if(interleave.getMinOccurs() == 0) return;
            if(!interleaveContext){
                relevant.add(interleave);
            }else{
                next(interleave.getChildren());
            }
        }
        public void visit(SGroup group){
            if(group.getMinOccurs() == 0) return;
            if(!groupContext){
                relevant.add(group);
            }else{
                next(group.getChildren());
            }        
        }
        public void visit(SListPattern list){
            if(list.getMinOccurs() == 0) return;
            relevant.add(list);
        }	
        public void visit(SEmpty empty){}
        public void visit(SText text){}
        public void visit(SNotAllowed notAllowed){
            throw new IllegalStateException();
        }
        public void visit(SDummy dummy){
            throw new IllegalStateException();
        }
        public void visit(SRef ref){	
            if(ref.getMinOccurs() == 0) return;
            SPattern child = ref.getChild();
            if(child != null){
                List<SPattern> relevant = relevantCache.get(ref);
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
        public void visit(SValue value){
            if(value.getMinOccurs() == 0) return;
            relevant.add(value);
        }
        public void visit(SData data){
            if(data.getMinOccurs() == 0) return;
            relevant.add(data);
        }	
        public void visit(SGrammar grammar){
            if(grammar.getMinOccurs() == 0) return;
            SimplifiedComponent child = grammar.getChild();
            child.accept(this);
        }
    }
    
    class SortingSubtreeAnalyser extends SubtreeAnalyser{
        Comparator<SPattern> patternComparator;
        
        SortingSubtreeAnalyser(){
            super();
            patternComparator = new PatternComparator(); 
        }
        
        List<SPattern> getRelevant(SPattern pattern){
            throw new IllegalStateException();
        }
        
        List<SPattern> getRelevant(SPattern[] patterns){
            relevant = new ArrayList<SPattern>();
            for(int i = 0; i < patterns.length; i++){
                patterns[i].accept(this);
            }
            Collections.sort(relevant, patternComparator);
            return relevant;
        }        
    }
    
    class PatternComparator implements Comparator<SPattern>{
        SortingSubtreeAnalyser sortingSubtreeAnalyser;
        Comparator<SPattern> patternComparator;
        SubtreeAnalyser subtreeAnalyser;
        
        public int compare(SPattern p1, SPattern p2){
            int id1;        
            if(p1 instanceof SGroup)id1 = MissingContentAnalyser.GROUP;
            else if(p1 instanceof SChoicePattern)id1 = MissingContentAnalyser.CHOICE;
            else if(p1 instanceof SInterleave)id1 = MissingContentAnalyser.INTERLEAVE;
            else id1 = p1.functionalEquivalenceCode();
            
            
            int id2;        
            if(p2 instanceof SGroup){
                id2 = MissingContentAnalyser.GROUP;
                if(id1 == id2){
                    return compare((SGroup)p1, (SGroup)p2);
                }
            }else if(p2 instanceof SChoicePattern){
                id2 = MissingContentAnalyser.CHOICE;
                if(id1 == id2){
                    return compare((SChoicePattern)p1, (SChoicePattern)p2);
                }
            }else if(p2 instanceof SInterleave){
                id2 = MissingContentAnalyser.INTERLEAVE;
                if(id1 == id2){
                    return compare((SInterleave)p1, (SInterleave)p2);
                }
            }else{
                id2 = p2.functionalEquivalenceCode();
            }
            
            return (id1 < id2 ? -1 : (id1 == id2 ? 0 : 1));
        }
        
        private int compare(SGroup g1, SGroup g2){
            if(subtreeAnalyser == null) subtreeAnalyser = new SubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
        
            List<SPattern> sg1 = subtreeAnalyser.getRelevant(g1.getChildren());
            relevantCache.put(g1, sg1);
            List<SPattern> sg2 = subtreeAnalyser.getRelevant(g2.getChildren());
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
        
        private int compare(SInterleave i1, SInterleave i2){
            if(sortingSubtreeAnalyser == null) sortingSubtreeAnalyser = new SortingSubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
        
            List<SPattern> si1 = sortingSubtreeAnalyser.getRelevant(i1.getChildren());
            relevantCache.put(i1, si1);
            List<SPattern> si2 = sortingSubtreeAnalyser.getRelevant(i2.getChildren());
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
        
        private int compare(SChoicePattern c1, SChoicePattern c2){
            if(sortingSubtreeAnalyser == null) sortingSubtreeAnalyser = new SortingSubtreeAnalyser();
            if(patternComparator == null) patternComparator= new PatternComparator();
            
            
            List<SPattern> sc1 = sortingSubtreeAnalyser.getRelevant(c1.getChildren());
            relevantCache.put(c1, sc1);
            List<SPattern> sc2 = sortingSubtreeAnalyser.getRelevant(c2.getChildren());
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
        HashMap<SPattern, List<SPattern>> compositorRelevantCache;
        IntList refDefinitionIndexes;
        ArrayList<List<SPattern>> refRelevantContent;
        
        CacheMap(){
            compositorRelevantCache = new HashMap<SPattern, List<SPattern>>();
            refDefinitionIndexes = new IntList();
            refRelevantContent = new ArrayList<List<SPattern>>();
        }
        
        void put(SRef ref, List<SPattern> list){
            refDefinitionIndexes.add(ref.getDefinitionIndex());
            refRelevantContent.add(list);
        }
        
        void put(SGroup group, List<SPattern> list){
            compositorRelevantCache.put(group, list);
        }
        
        void put(SInterleave interleave, List<SPattern> list){
            compositorRelevantCache.put(interleave, list);
        }
        
        void put(SChoicePattern choicePattern, List<SPattern> list){
            compositorRelevantCache.put(choicePattern, list);
        }
        
        List<SPattern> get(SRef ref){
            int refIndex = ref.getDefinitionIndex();
            for(int i = 0; i < refDefinitionIndexes.size(); i++){
                if(refIndex == refDefinitionIndexes.get(i)){
                    return refRelevantContent.get(i);
                }
            }
            return null;
        }
        
        List<SPattern> get(SGroup group){
            return compositorRelevantCache.get(group);
        }
        
        List<SPattern> get(SInterleave interleave){
            return compositorRelevantCache.get(interleave);
        }
        
        List<SPattern> get(SChoicePattern choicePattern){
            return compositorRelevantCache.get(choicePattern);
        }
        
        List<SPattern> remove(SRef ref){
            int refIndex = ref.getDefinitionIndex();
            for(int i = 0; i < refDefinitionIndexes.size(); i++){
                if(refIndex == refDefinitionIndexes.get(i)){
                    refDefinitionIndexes.removeFromIndex(i);
                    return refRelevantContent.remove(i);
                }
            }
            return null;
        }
        
        List<SPattern> remove(SGroup group){
            return compositorRelevantCache.remove(group);
        }
        
        List<SPattern> remove(SInterleave interleave){
            return compositorRelevantCache.remove(interleave);
        }
        
        List<SPattern> remove(SChoicePattern choicePattern){
            return compositorRelevantCache.remove(choicePattern);
        }
        
        void clear(){
            compositorRelevantCache.clear();
            refDefinitionIndexes.clear();
            refRelevantContent.clear();   
        }
    }
}
