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

package serene.simplifier;

import java.util.Map;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.ParsedComponentVisitor;

import serene.validation.schema.parsed.components.Definition;
import serene.validation.schema.parsed.components.Pattern;
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

import sereneWrite.MessageWriter;

class IncludedDefinitionCopier implements ParsedComponentVisitor{
	Definition copy;
	
	MessageWriter debugWriter;
	
	IncludedDefinitionCopier(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
	}
	
	Definition copy(Definition definition){
		copy = null;
		definition.accept(this);		
		return copy;
	}
	
	public void visit(Define define){
		Map<String, String> prefixMapping = define.getXmlns();
		String xmlBase = define.getXmlBaseAttribute();
		String ns = define.getNsAttribute();
		String datatypeLibrary = define.getDatatypeLibrary();
		String name = define.getName();
		String combine = define.getCombine();
		Pattern[] children = define.getChildren();
		String qName = define.getQName();
		String location = define.getLocation();
		
		copy = new Define(prefixMapping,
							xmlBase,
							ns,
							datatypeLibrary,
							name,
							combine,
							children,
							qName,
							location,
							debugWriter);	 		
	}
	public void visit(Start start){
		Map<String, String> prefixMapping = start.getXmlns();
		String xmlBase = start.getXmlBaseAttribute();
		String ns = start.getNsAttribute();
		String datatypeLibrary = start.getDatatypeLibrary();
		String combine = start.getCombine();
		Pattern[] children = start.getChildren();
		String qName = start.getQName();
		String location = start.getLocation();
		
		copy = new Start(prefixMapping,
							xmlBase,
							ns,
							datatypeLibrary,
							combine,
							children,
							qName,
							location,
							debugWriter);		
	}
	
	public void visit(Param param){
		throw new IllegalStateException();
	}
	public void visit(Include include){
		throw new IllegalStateException();
	}
	public void visit(ExceptPattern exceptPattern){
		throw new IllegalStateException();
	}
	public void visit(ExceptNameClass exceptNameClass){
		throw new IllegalStateException();		
	}
	public void visit(DivGrammarContent div){
		throw new IllegalStateException();		
	}
	public void visit(DivIncludeContent div){
		throw new IllegalStateException();		
	}
		
	public void visit(Name name){
		throw new IllegalStateException();
	}
	public void visit(AnyName anyName){
		throw new IllegalStateException();		
	}
	public void visit(NsName nsName){
		throw new IllegalStateException();		
	}
	public void visit(ChoiceNameClass choice){
		throw new IllegalStateException();		
	}	
	public void visit(ElementWithNameClass element){
		throw new IllegalStateException();		
	}	
	public void visit(ElementWithNameInstance element){		
		throw new IllegalStateException();		
	}	
	public void visit(AttributeWithNameClass attribute){
		throw new IllegalStateException();				
	}
	public void visit(AttributeWithNameInstance attribute){				
		throw new IllegalStateException();						
	}
	public void visit(ChoicePattern choice){
		throw new IllegalStateException();				
	}
	public void visit(Interleave interleave){
		throw new IllegalStateException();				
	}
	public void visit(Group group){
		throw new IllegalStateException();			
	}
	public void visit(ZeroOrMore zeroOrMore){
		throw new IllegalStateException();
	}
	public void visit(OneOrMore oneOrMore){
		throw new IllegalStateException();			
	}
	public void visit(Optional optional){
		throw new IllegalStateException();			
	}	
	public void visit(ListPattern list){
		throw new IllegalStateException();			
	}	
	public void visit(Mixed mixed){
		throw new IllegalStateException();			
	}	
	public void visit(Empty empty){
		throw new IllegalStateException();
	}
	public void visit(Text text){
		throw new IllegalStateException();
	}
	public void visit(NotAllowed notAllowed){
		throw new IllegalStateException();
	}
	public void visit(ExternalRef externalRef){
		throw new IllegalStateException();
	}
	public void visit(Ref ref){
		throw new IllegalStateException();
	}
	public void visit(ParentRef parentRef){
		throw new IllegalStateException();
	}
	public void visit(Value value){
		throw new IllegalStateException();
	}
	public void visit(Data data){	
		throw new IllegalStateException();
	}	
	public void visit(Grammar grammar){
		throw new IllegalStateException();
	}
	
	public void visit(Dummy dummy){
		throw new IllegalStateException();
	}
}