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

package serene.datatype.xsd;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Locale;

import org.relaxng.datatype.ValidationContext;

import org.apache.xerces.util.SymbolTable;

public class XsdValidationContext implements org.apache.xerces.impl.dv.ValidationContext{
    ValidationContext rngValidationContext;
    
    boolean needsExtraChecking;
    boolean needsFacetChecking;
    boolean needsToNormalize;
    
    HashMap<String, String> ids;
    HashMap<String, String> idRefs;
    
    SymbolTable symbolTable = null;
    
    public XsdValidationContext(){        
        needsExtraChecking = false;
        needsFacetChecking = true;
        needsToNormalize = true;
        
        ids = new HashMap<String, String>();
        idRefs = new HashMap<String, String>();
           
        symbolTable = new SymbolTable(); 
    }
    
    
    public void setRngValidationContext(ValidationContext rngValidationContext){
        if(rngValidationContext == null) throw new IllegalArgumentException();
        this.rngValidationContext = rngValidationContext;
    }
        
    public void setNeedsExtraChecking(boolean value){
        needsExtraChecking = value;
    }
    
    public void setNeedsFacetChecking(boolean value){
        needsFacetChecking = value;
    }
    
    public void setNeedsToNormalize(boolean value){
        needsToNormalize = value;
    }
    
    public boolean isEntityDeclared(String name){
        if(rngValidationContext.isUnparsedEntity(name)) return true;
        throw new UnsupportedOperationException();
    }
    
    public boolean isEntityUnparsed(String name){
        return rngValidationContext.isUnparsedEntity(name);
    }
    
    public boolean needExtraChecking(){
        return needsExtraChecking;
    }
    
    public boolean needFacetChecking(){
        return needsFacetChecking;
    }
    
    public boolean needToNormalize(){
        return needsToNormalize;
    }
    
    // id
    public boolean isIdDeclared(String name) {
        return ids.containsKey(name);
    }
    public void addId(String name) {
        ids.put(name, null);
    }

    // idref
    public void addIdRef(String name) {
        idRefs.put(name, null);
    }
    
    // get symbols
    public String getSymbol (String symbol) {
        return symbolTable.addSymbol(symbol);
    }
    // qname, notation
    public String getURI(String prefix) {
        return rngValidationContext.resolveNamespacePrefix(prefix);
    }
    
    public Locale getLocale(){
        return Locale.ENGLISH;
    }
    
    public boolean useNamespaces(){
        return true;
    }
        
}
