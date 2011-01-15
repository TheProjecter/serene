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

import org.xml.sax.SAXException;

public interface SimplifyingVisitor{

	void visit(Include component) throws SAXException;
	void visit(ExceptPattern component) throws SAXException;
	void visit(ExceptNameClass component) throws SAXException;
	void visit(DivGrammarContent component) throws SAXException;
	void visit(DivIncludeContent component) throws SAXException;
	
	void visit(ElementWithNameClass component) throws SAXException;
	void visit(ElementWithNameInstance component) throws SAXException;
	void visit(AttributeWithNameClass component) throws SAXException;
	void visit(AttributeWithNameInstance component) throws SAXException;
	void visit(ChoicePattern component) throws SAXException;
	void visit(Interleave component) throws SAXException;
	void visit(Group component) throws SAXException;	
	void visit(ZeroOrMore component) throws SAXException;
	void visit(OneOrMore component) throws SAXException;
	void visit(Optional component) throws SAXException;
	void visit(ListPattern component) throws SAXException;
	void visit(Mixed component) throws SAXException;
	void visit(Empty component) throws SAXException;
	void visit(Text component) throws SAXException;
	void visit(NotAllowed component) throws SAXException;
	void visit(ExternalRef component) throws SAXException;
	void visit(Ref component) throws SAXException;
	void visit(ParentRef component) throws SAXException;
	void visit(Value component) throws SAXException;
	void visit(Data component) throws SAXException;
	void visit(Grammar component) throws SAXException;
	void visit(Dummy component) throws SAXException;
	
	void visit(Name component) throws SAXException;
	void visit(AnyName component) throws SAXException;
	void visit(NsName component) throws SAXException;
	void visit(ChoiceNameClass component) throws SAXException;	
	
	void visit(Define component) throws SAXException;
	void visit(Start component) throws SAXException;
		
}