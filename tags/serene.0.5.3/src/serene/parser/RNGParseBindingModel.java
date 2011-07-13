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

package serene.parser;

import java.util.Map;
import java.util.Set;

import serene.validation.schema.simplified.components.SElement;
import serene.validation.schema.simplified.components.SAttribute;


import serene.bind.BindingModel;
import serene.bind.ElementBinder;
import serene.bind.AttributeBinder;
import serene.bind.CharacterContentBinder;

import serene.validation.schema.parsed.ParsedComponentBuilder;

import sereneWrite.MessageWriter;

public class RNGParseBindingModel extends BindingModel{
	RNGParseBindingPool pool;
	ParsedComponentBuilder builder;
	
	RNGParseBindingModel(Map<SElement, ElementBinder> selementBinder,
					Map<SAttribute, AttributeBinder> sattributeBinder,
					RNGParseBindingPool pool,
					ParsedComponentBuilder builder,
					MessageWriter debugWriter){
		super(selementBinder, sattributeBinder, debugWriter);
		this.pool = pool;
		this.builder = builder;
	}
	
	public ParsedComponentBuilder getParsedComponentBuilder(){
		return builder;
	}
	public void recycle(){
		pool.recycle(this);	
	}
}
	