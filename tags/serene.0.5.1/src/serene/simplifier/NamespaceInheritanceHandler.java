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
import java.util.HashMap;

import serene.validation.schema.parsed.ParsedComponent;
import serene.validation.schema.parsed.components.Definition;

import sereneWrite.MessageWriter;

class NamespaceInheritanceHandler{
		
	Map<ParsedComponent, ParsedComponent> descendanceMap;
	
	NsInheritanceHandler nsInheritanceHandler;
    
	DefinitionStartXmlnsContextHandler definitionStartXmlnsContextHandler;
    DefinitionEndXmlnsContextHandler definitionEndXmlnsContextHandler;
	
	MessageWriter debugWriter;
	
	NamespaceInheritanceHandler(MessageWriter debugWriter){
		this.debugWriter = debugWriter;
		descendanceMap = new HashMap<ParsedComponent, ParsedComponent>();
		
		nsInheritanceHandler = new NsInheritanceHandler(descendanceMap, debugWriter);
        
        definitionStartXmlnsContextHandler = new DefinitionStartXmlnsContextHandler(descendanceMap, debugWriter);
        definitionEndXmlnsContextHandler = new DefinitionEndXmlnsContextHandler(descendanceMap, debugWriter);
	}
	
	void put(ParsedComponent descendant, ParsedComponent ancestor){//key inherits from value
		descendanceMap.put(descendant, ancestor);
	}
	
	String getNsURI(ParsedComponent component){				
		return nsInheritanceHandler.getURI(component);
	}
	
	void startXmlnsContext(SimplificationEventContext simplificationContext, Definition definition){				
		definitionStartXmlnsContextHandler.handle(simplificationContext, definition);
	}
    
    void endXmlnsContext(SimplificationEventContext simplificationContext, Definition definition){				
		definitionEndXmlnsContextHandler.handle(simplificationContext, definition);
	}
}