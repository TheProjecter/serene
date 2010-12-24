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

package serene.validation.schema.simplified;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.components.SParam;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SExceptNameClass;


import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;
import serene.validation.schema.simplified.components.SChoicePattern;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SZeroOrMore;
import serene.validation.schema.simplified.components.SOneOrMore;
import serene.validation.schema.simplified.components.SOptional;
import serene.validation.schema.simplified.components.SListPattern;
import serene.validation.schema.simplified.components.SEmpty;
import serene.validation.schema.simplified.components.SText;
import serene.validation.schema.simplified.components.SNotAllowed;
import serene.validation.schema.simplified.components.SRef;
import serene.validation.schema.simplified.components.SData;
import serene.validation.schema.simplified.components.SValue;
import serene.validation.schema.simplified.components.SGrammar;
import serene.validation.schema.simplified.components.SMixed;
import serene.validation.schema.simplified.components.SDummy;


import serene.validation.schema.simplified.components.SName;
import serene.validation.schema.simplified.components.SAnyName;
import serene.validation.schema.simplified.components.SNsName;
import serene.validation.schema.simplified.components.SChoiceNameClass;

public interface RestrictingVisitor{
	
	void visit(SParam component) throws SAXException;
	void visit(SExceptPattern component) throws SAXException;
	void visit(SExceptNameClass component) throws SAXException;
	
	void visit(SElement component) throws SAXException;
	void visit(SAttribute component) throws SAXException;
	void visit(SChoicePattern component) throws SAXException;
	void visit(SInterleave component) throws SAXException;
	void visit(SGroup component) throws SAXException;	
	void visit(SZeroOrMore component) throws SAXException;
	void visit(SOneOrMore component) throws SAXException;
	void visit(SOptional component) throws SAXException;
	void visit(SMixed component) throws SAXException;
	void visit(SListPattern component) throws SAXException;
	void visit(SEmpty component) throws SAXException;
	void visit(SText component) throws SAXException;
	void visit(SNotAllowed component) throws SAXException;
	void visit(SRef component) throws SAXException;
	void visit(SValue component) throws SAXException;
	void visit(SData component) throws SAXException;
	void visit(SGrammar component) throws SAXException;
	void visit(SDummy component) throws SAXException;
	
	void visit(SName component) throws SAXException;
	void visit(SAnyName component) throws SAXException;
	void visit(SNsName component) throws SAXException;
	void visit(SChoiceNameClass component) throws SAXException;	
	
}