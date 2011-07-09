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

package serene.validation.handlers.content.impl;

import java.util.List;
import java.util.ArrayList;

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AText;
import serene.validation.schema.active.components.AListPattern;
import serene.validation.schema.active.components.AExceptPattern;
import serene.validation.schema.active.components.AElement;


import serene.validation.handlers.content.MarkupEventHandler;
import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.match.MatchHandler;

import sereneWrite.MessageWriter;

class CharacterContentValidationHandler extends AbstractCVH{
	ElementValidationHandler parent;
	
	CharacterContentValidationHandler(MessageWriter debugWriter){
		super(debugWriter);
		charsItemMatches = new ArrayList<CharsActiveTypeItem>();
		textMatches = new ArrayList<AText>();
		dataMatches = new ArrayList<AData>();
		valueMatches = new ArrayList<AValue>();
		listMatches = new ArrayList<AListPattern>();
	}
	
	void init(ElementValidationHandler parent, ValidationContext validationContext, ErrorCatcher errorCatcher){
		this.parent = parent;
		this.validationContext = validationContext;
		this.errorCatcher = errorCatcher; 
	}
	
	public void recycle(){
		pool.recycle(this);
	}

	public ElementValidationHandler getParentHandler(){
		return parent;
	}
	
	void handleAddToParent(DataActiveType type){		
		int matchesCount = charsItemMatches.size();		
		if(totalCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 0){
			//throw new IllegalStateException();
            return; //the input was whitespace and the match was optional
		}else if(totalCount == 1 && matchesCount == 1){
			// if errors: already reported, that's why error before
			//just shift
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount == 0){
			// ambiguity error
			// shift all for in context validation
			if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
			if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
			if(!listMatches.isEmpty())charsItemMatches.addAll(listMatches);
			errorCatcher.ambiguousCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
			parent.addChars(charsItemMatches);
		}else if(totalCount > 1 && matchesCount == 1){
			//just shift
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount > 1){
			// ambiguity warning, later maybe
			// shift all for in context validation			
			parent.addChars(charsItemMatches);
		}
	}

	void handleAddToParent(StructuredDataActiveType type){		
		int matchesCount = charsItemMatches.size();
		if(totalCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 0){
			//throw new IllegalStateException();
            return; //the input was whitespace and the match was optional
		}else if(totalCount == 1 && matchesCount == 1){
			// if errors: already reported, that's why error before
			//just shift
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount == 0){
			// ambiguity error
			// shift all for in context validation
			if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
			if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
			if(!listMatches.isEmpty())charsItemMatches.addAll(listMatches);
			errorCatcher.ambiguousCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
			parent.addChars(charsItemMatches);
		}else if(totalCount > 1 && matchesCount == 1){
			//just shift
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount > 1){
			// ambiguity warning, later maybe
			// shift all for in context validation			
			parent.addChars(charsItemMatches);
		}
	}	
	
	void handleAddToParent(CharsActiveType type){		
		int matchesCount = charsItemMatches.size();		
		if(totalCount == 0){
			throw new IllegalStateException();
		}else if(totalCount == 1 && matchesCount == 0){
			//throw new IllegalStateException();
            return; //the input was whitespace and the match was optional
		}else if(totalCount == 1 && matchesCount == 1){
			// if errors: already reported, that's why error before
			//just shift
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount == 0){
			// ambiguity error
			// shift all for in context validation
			if(!dataMatches.isEmpty())charsItemMatches.addAll(dataMatches);
			if(!valueMatches.isEmpty())charsItemMatches.addAll(valueMatches);
			if(!listMatches.isEmpty())charsItemMatches.addAll(listMatches);
			errorCatcher.ambiguousCharacterContent(validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), charsItemMatches.toArray(new CharsActiveTypeItem[matchesCount]));
			parent.addChars(charsItemMatches);
		}else if(totalCount > 1 && matchesCount == 1){
			//just shift			
			parent.addChars(charsItemMatches.get(0));
		}else if(totalCount > 1 && matchesCount > 1){
			// ambiguity warning, later maybe
			// shift all for in context validation						
			parent.addChars(charsItemMatches);
		}
	}
	
    void reportDatatypeError(DatatypedActiveTypeItem item, String message){
		errorCatcher.characterContentDatatypeError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), item, message);
	}
    
	void reportValueError(AValue value){
		errorCatcher.characterContentValueError(validationItemLocator.getQName(), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber(), value);
	}
}



