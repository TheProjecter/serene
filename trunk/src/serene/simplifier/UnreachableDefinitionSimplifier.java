/*
Copyright 2011 Radu Cernuta 

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

import java.util.ArrayList;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


import serene.validation.handlers.error.ErrorDispatcher;

import serene.validation.schema.parsed.Definition;
import serene.validation.schema.parsed.Define;
import serene.validation.schema.parsed.Start;
import serene.validation.schema.parsed.Ref;
import serene.validation.schema.parsed.ParentRef;
import serene.validation.schema.parsed.Grammar;

import serene.util.IntList;
import sereneWrite.MessageWriter;

class UnreachableDefinitionSimplifier extends DefinitionSimplifier{

    UnreachableDefinitionSimplifier(DefinitionSimplifierPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
		super(pool, errorDispatcher, debugWriter);
	}       
	
	public void recycle(){
		grammarDefinitions = null;	
		externalRefs = null;
		docParsedModels = null;	
		namespaceInheritanceHandler = null;	
		
		componentAsciiDL = null;	
		asciiDlDatatypeLibrary = null;
		
		currentGrammar = null;
		previousGrammars = null;	
		
		indexes = null;	
		
		pool.recycle(this);
	}
    
    void simplify(ArrayList<Definition> definitions) throws SAXException{        
		emptyChild = false;
        emptyComponent = null;
		notAllowedChild = false;
        patternChild = false;
		notAllowedElement = false;
        
		anyNameContext = false;
		anyNameExceptContext = false;
		nsNameContext = false;
		nsNameExceptContext = false;
		attributeContext = false;
        		
		combine = null;
		nullCombine.clear();        
		if(otherCombine != null) otherCombine.clear();
		
        paramStack.clear();
         
		builder.startBuild();		
        
        currentDefinitionTopPatterns.clear();		
        String location = "";
		for(int i = 0; i < definitions.size(); i++){
			Definition d = definitions.get(i);
			String c = d.getCombine();
			
			if(c == null) nullCombine.add(i);
            else{ 
                c = c.trim();            
                if(c.equals("choice") || c.equals("interleave")){
                    if(combine == null)combine = c;
                    else if(!combine.equals(c)){
                        if(otherCombine == null) otherCombine = new IntList();
                        otherCombine.add(i);
                    }
                }
            }
            if(i > 0)location += ", "+d.getLocation();
            else location += d.getLocation();
			d.accept(this);
		}
		if(nullCombine.size() > 1){
			// error 4.17
			String message = "Simplification 4.17 error.";
			Definition d = definitions.get(0);			
			if(d instanceof Define){
				message += " Several define elements with the same name attribute and without combine attribute: ";
			}else{
				message += " Several start elements without combine attribute: ";
			}
			for(int i = 0; i < nullCombine.size(); i++){
				d = definitions.get(nullCombine.get(i));
				message += "\n\t<"+d.getQName()+"> at "+d.getLocation();
			}
			message += ".";
			errorDispatcher.error(new SAXParseException(message, null));
		}
		if(otherCombine != null && !otherCombine.isEmpty()){
			// error 4.17
			String message = "Simplification 4.17 error.";
			Definition d = definitions.get(0);			
			if(d instanceof Define){
				message += " define elements with the same name attribute and different combine attributes: ";
			}else{
				message += " start elements with different combine attribute: ";
			}			
			String oCombine = combine.equals("choice") ? "interleave" : "choice";
			String oDefinitions = "\n\t\""+oCombine+"\"";
			message += "\n\t\""+combine+"\"";
			for(int i = 0; i < definitions.size(); i++){
				d = definitions.get(i);
				if(otherCombine.contains(i)){
					oDefinitions += "\n\t<"+d.getQName()+"> at "+d.getLocation();
				}else{
					message += "\n\t<"+d.getQName()+"> at "+d.getLocation();
				}
			}
			message += oDefinitions+".";
			errorDispatcher.error(new SAXParseException(message, null));
		}	
        
        // the current level is that of the definitions,
		// they were not built, remove the rest
        builder.clearContent();
	}
        
    public void visit(Ref ref) throws SAXException{
        builder.buildRef(-1, ref.getQName(), ref.getLocation());
        patternChild = true;
        
        String name = ref.getName() == null ? "*" : ref.getName().trim()+'*';
		ArrayList<Definition> definitions = getReferencedDefinition(currentGrammar, name);
		
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding definition was found for element <"+ref.getQName()+"> at "+ref.getLocation()+".";
			errorDispatcher.error(new SAXParseException(message, null));
		}
	}
    
	public void visit(ParentRef parentRef) throws SAXException{
        builder.buildRef(-1, parentRef.getQName(), parentRef.getLocation());
        patternChild = true;
        
        String name = parentRef.getName() == null ? "*" : parentRef.getName().trim()+'*';        
		ArrayList<Definition> definitions = getReferencedDefinition(previousGrammars.peek(), name);
        
		if(definitions == null){
			// error 4.18
			String message = "Simplification 4.18 error. "
				+"No correspoding definition was found for element <"+parentRef.getQName()+"> at "+parentRef.getLocation()+".";
			errorDispatcher.error(new SAXParseException(message, null));
		}       		
	}
}
