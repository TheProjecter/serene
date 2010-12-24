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

import serene.validation.schema.active.components.AParam;
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


public interface RuleVisitor{
	
	void visit(AParam rule);
	void visit(AExceptPattern rule);
	
	void visit(AElement rule);
	void visit(AAttribute rule);
	void visit(AChoicePattern rule);
	void visit(AInterleave rule);
	void visit(AGroup rule);
	void visit(AListPattern rule);
	void visit(AEmpty rule);
	void visit(AText rule);
	void visit(ANotAllowed rule);
	void visit(ARef rule);
	void visit(AValue rule);
	void visit(AData rule);
	void visit(AGrammar rule);		
}