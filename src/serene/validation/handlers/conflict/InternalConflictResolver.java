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

package serene.validation.handlers.conflict;

import java.util.BitSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.CharsActiveTypeItem;

import serene.validation.handlers.content.util.ValidationItemLocator;
import serene.validation.handlers.error.ErrorCatcher;

import sereneWrite.MessageWriter;

/*
* In the ConcurrentStackHandler each level handles a conflict and every handler 
* on that level gets a reference to an InternalConflictResolver instance. This 
* counts the qualified candidates and when asked to resolve() theconflict if the
* number is greater than 1, it reports ambiguous content.   
*/
public class InternalConflictResolver implements ConflictResolver{
	BitSet qualified;
		
	
	String qName;
	String systemId;
	int lineNumber;
	int columnNumber;
	ValidationItemLocator validationItemLocator;
	
	State state;
	State elementConflict;
	State attributeConflict;
	State charsConflict;
	
	ActiveModelConflictHandlerPool pool;
	MessageWriter debugWriter;
	
	public InternalConflictResolver(MessageWriter debugWriter){				
		this.debugWriter = debugWriter;
		qualified = new BitSet();
		createStates();
	}
	
	
	void createStates(){
		elementConflict = new ElementConflict();
		attributeConflict = new AttributeConflict();
		charsConflict = new CharsConflict();		
	}
	void init(ActiveModelConflictHandlerPool pool, ValidationItemLocator validationItemLocator){
		this.validationItemLocator = validationItemLocator;
		this.pool = pool;
	}
	
	void init(){		 
		this.systemId = validationItemLocator.getSystemId();
		this.lineNumber = validationItemLocator.getLineNumber();
		this.columnNumber = validationItemLocator.getColumnNumber();
		this.qName = validationItemLocator.getQName();
	}

	public void recycle(){
		qualified.clear();		
		state.reset();
		state = null;
		systemId = null;
		lineNumber = -1;
		columnNumber = -1;
		qName = null;
		pool.recycle(this);
	}

	public void addCandidate(AElement candidate){
		if(state == null)state = elementConflict;
		state.addCandidate(candidate);
	}
	
	public void addCandidate(AAttribute candidate){
		if(state == null)state = attributeConflict;
		state.addCandidate(candidate);
	}
	
	public void addCandidate(CharsActiveTypeItem candidate){
		if(state == null)state = charsConflict;
		state.addCandidate(candidate);
	}
	
	public void qualify(int candidateIndex){
		qualified.set(candidateIndex);
	}
	
	public void resolve(ErrorCatcher errorCatcher){
		state.resolve(errorCatcher);
	}
	
	
	
	abstract class State{
		abstract void reset();
		abstract void addCandidate(AElement candidate);
		abstract void addCandidate(AAttribute candidate);
		abstract void addCandidate(CharsActiveTypeItem candidate);
		abstract void resolve(ErrorCatcher errorCatcher);
	}
	
	class ElementConflict extends State{
		List<AElement> candidateDefinitions;
		ElementConflict(){
			candidateDefinitions = new ArrayList<AElement>();
		}
		void reset(){
			candidateDefinitions.clear();
		}
		void addCandidate(AElement candidate){
			candidateDefinitions.add(candidate);
		}
		void addCandidate(AAttribute attribute){
			throw new IllegalStateException();
		}
		void addCandidate(CharsActiveTypeItem chars){
			throw new IllegalStateException();
		}
		void resolve(ErrorCatcher errorCatcher){
			if(qualified.cardinality() == 0){				
				AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
				errorCatcher.ambiguousElementContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));				
			}else if(qualified.cardinality() > 1){
				int j = 0;
				for(int i = 0; i < candidateDefinitions.size(); i++){			
					if(!qualified.get(j++)){
						candidateDefinitions.remove(i);
						i--;
					}
				}
				AElement[] definitions = candidateDefinitions.toArray(new AElement[candidateDefinitions.size()]);
				errorCatcher.ambiguousElementContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}
		}	
		
		public String toString(){
			return "Element candidates "+candidateDefinitions;
		}
	}
	
	class AttributeConflict extends State{
		List<AAttribute> candidateDefinitions;
		AttributeConflict(){
			candidateDefinitions = new ArrayList<AAttribute>();
		}
		void reset(){
			candidateDefinitions.clear();
		}
		void addCandidate(AElement candidate){
			throw new IllegalStateException();
		}
		void addCandidate(AAttribute candidate){
			candidateDefinitions.add(candidate);
		}
		void addCandidate(CharsActiveTypeItem candidate){
			throw new IllegalStateException();
		}
		void resolve(ErrorCatcher errorCatcher){			
			if(qualified.cardinality()== 0){
				AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
				errorCatcher.ambiguousAttributeContentError(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}else if(qualified.cardinality() > 1){
				int j = 0;
				for(int i = 0; i < candidateDefinitions.size(); i++){			
					if(!qualified.get(j++)){
						candidateDefinitions.remove(i);
						i--;
					}
				}
				AAttribute[] definitions = candidateDefinitions.toArray(new AAttribute[candidateDefinitions.size()]);
				errorCatcher.ambiguousAttributeContentWarning(qName, systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}
		}
		
		public String toString(){
			return "Attribute candidates "+candidateDefinitions;
		}
	}
	
	class CharsConflict extends State{
		List<CharsActiveTypeItem> candidateDefinitions;
		CharsConflict(){
			candidateDefinitions = new ArrayList<CharsActiveTypeItem>();
		}
		void reset(){
			candidateDefinitions.clear();
		}
		void addCandidate(AElement candidate){
			throw new IllegalStateException();
		}
		void addCandidate(AAttribute candidate){
			throw new IllegalStateException();
		}
		void addCandidate(CharsActiveTypeItem candidate){
			candidateDefinitions.add(candidate);
		}
		void resolve(ErrorCatcher errorCatcher){
			if(qualified.cardinality() == 0){
				CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
				errorCatcher.ambiguousCharsContentError(systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}else if(qualified.cardinality() > 1){ 
				int j = 0;
				for(int i = 0; i < candidateDefinitions.size(); i++){			
					if(!qualified.get(j++)){
						candidateDefinitions.remove(i);
						i--;
					}
				}
				CharsActiveTypeItem[] definitions = candidateDefinitions.toArray(new CharsActiveTypeItem[candidateDefinitions.size()]);
				errorCatcher.ambiguousCharsContentWarning(systemId, lineNumber, columnNumber, Arrays.copyOf(definitions, definitions.length));
			}
		}	
		
		public String toString(){
			return "Chars candidates "+candidateDefinitions;
		}
	}
	public String toString(){
		//return "InternalConflictResolver "+hashCode();
		return "InternalConflictResolver qualified "+qualified+" STATE "+state.toString();
	}
	
	public String outerToString(){
		//return "InternalConflictResolver "+hashCode();
		return "InternalConflictResolver qualified "+qualified+" STATE "+state.toString();
	}
}