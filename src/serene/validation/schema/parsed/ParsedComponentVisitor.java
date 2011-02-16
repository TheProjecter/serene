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
    
    void visit(ForeignComponent component);
		
}
