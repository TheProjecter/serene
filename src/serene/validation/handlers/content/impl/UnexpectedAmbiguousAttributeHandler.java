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
import java.util.Arrays;

import serene.validation.schema.active.components.AAttribute;

import sereneWrite.MessageWriter;

class UnexpectedAmbiguousAttributeHandler extends ErrorAEH{
	AAttribute[] attributes;
	
	UnexpectedAmbiguousAttributeHandler(MessageWriter debugWriter){
		super(debugWriter);
	}
	
	public void recycle(){
		pool.recycle(this);
	}

	void init(List<AAttribute> attributes, ElementValidationHandler parent){
		this.parent = parent;
		this.attributes = attributes.toArray(new AAttribute[attributes.size()]);
	}
	
	void validateInContext(){		
		parent.unexpectedAmbiguousAttribute(validationItemLocator.getQName(), Arrays.copyOf(attributes, attributes.length), validationItemLocator.getSystemId(), validationItemLocator.getLineNumber(), validationItemLocator.getColumnNumber());
	}	
}