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

package serene.validation.schema.parsed;

import serene.validation.schema.parsed.components.Param;
import serene.validation.schema.parsed.components.Include;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.ExceptNameClass;
import serene.validation.schema.parsed.components.DivGrammarContent;
import serene.validation.schema.parsed.components.DivIncludeContent;

import serene.validation.schema.parsed.components.ElementWithNameClass;
import serene.validation.schema.parsed.components.ElementWithNameInstance;
import serene.validation.schema.parsed.components.AttributeWithNameClass;
import serene.validation.schema.parsed.components.AttributeWithNameInstance;
import serene.validation.schema.parsed.components.ChoicePattern;
import serene.validation.schema.parsed.components.Interleave;
import serene.validation.schema.parsed.components.Group;
import serene.validation.schema.parsed.components.ZeroOrMore;
import serene.validation.schema.parsed.components.OneOrMore;
import serene.validation.schema.parsed.components.Optional;
import serene.validation.schema.parsed.components.ListPattern;
import serene.validation.schema.parsed.components.Mixed;
import serene.validation.schema.parsed.components.Empty;
import serene.validation.schema.parsed.components.Text;
import serene.validation.schema.parsed.components.NotAllowed;
import serene.validation.schema.parsed.components.ExternalRef;
import serene.validation.schema.parsed.components.Ref;
import serene.validation.schema.parsed.components.ParentRef;
import serene.validation.schema.parsed.components.Data;
import serene.validation.schema.parsed.components.Value;
import serene.validation.schema.parsed.components.Grammar;
import serene.validation.schema.parsed.components.Dummy;

import serene.validation.schema.parsed.components.Name;
import serene.validation.schema.parsed.components.AnyName;
import serene.validation.schema.parsed.components.NsName;
import serene.validation.schema.parsed.components.ChoiceNameClass;

import serene.validation.schema.parsed.components.Define;
import serene.validation.schema.parsed.components.Start;


public interface ParsedComponentVisitor{
	
	void visit(Param component);
	void visit(Include component);
	void visit(ExceptPattern component);
	void visit(ExceptNameClass component);
	void visit(DivGrammarContent component);
	void visit(DivIncludeContent component);
	
	void visit(ElementWithNameClass component);
	void visit(ElementWithNameInstance component);
	void visit(AttributeWithNameClass component);
	void visit(AttributeWithNameInstance component);
	void visit(ChoicePattern component);
	void visit(Interleave component);
	void visit(Group component);	
	void visit(ZeroOrMore component);
	void visit(OneOrMore component);
	void visit(Optional component);
	void visit(ListPattern component);
	void visit(Mixed component);
	void visit(Empty component);
	void visit(Text component);
	void visit(NotAllowed component);
	void visit(ExternalRef component);
	void visit(Ref component);
	void visit(ParentRef component);
	void visit(Value component);
	void visit(Data component);
	void visit(Grammar component);
	void visit(Dummy component);
	
	void visit(Name component);
	void visit(AnyName component);
	void visit(NsName component);
	void visit(ChoiceNameClass component);	
	
	void visit(Define component);
	void visit(Start component);
		
}