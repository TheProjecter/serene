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

package serene.dtdcompatibility;

import java.util.Arrays;
import java.util.Stack;
import java.util.ArrayList;

import org.xml.sax.SAXException;

import org.relaxng.datatype.Datatype;

import serene.util.BooleanList;
import serene.util.IntList;

import serene.BaseSchema;

import serene.restrictor.ControllerPool;

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RestrictingVisitor;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SNameClass;

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

import serene.validation.schema.simplified.SimplifiedModel;
import serene.validation.schema.simplified.RecursionModel;
import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.RestrictingVisitor;

import serene.validation.schema.active.ActiveModelPool;
import serene.validation.schema.active.ActiveModel;
import serene.validation.schema.active.ActiveGrammarModel;


import serene.validation.schema.active.Rule;
import serene.validation.schema.active.Identifier;
import serene.validation.schema.active.components.AAttribute;
import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.APattern;
import serene.validation.schema.active.components.AValue;
import serene.validation.schema.active.components.AData;
import serene.validation.schema.active.components.CharsActiveTypeItem;
import serene.validation.schema.active.components.DatatypedActiveTypeItem;


import serene.validation.handlers.content.CharactersEventHandler;
import serene.validation.handlers.content.impl.ContentHandlerPool;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;

import serene.validation.handlers.content.CharactersEventHandler;

import serene.validation.handlers.error.ErrorDispatcher;
import serene.validation.handlers.error.ErrorCatcher;
import serene.validation.handlers.error.ValidatorErrorHandlerPool;
import serene.validation.handlers.error.AttributeDefaultValueErrorHandler;

import serene.validation.handlers.content.DefaultValueAttributeHandler;
import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
import serene.validation.handlers.content.util.ValidationItemLocator;

import serene.SchemaModel;
import serene.validation.schema.ValidationModel;

import serene.util.AttributeInfo;

import sereneWrite.MessageWriter;

public class CompatibilityHandler implements RestrictingVisitor{
    SPattern[] startTopPattern;
	SPattern[] refDefinitionTopPattern;
    RecursionModel recursionModel;
        
    boolean level1AttributeDefaultValue;
    boolean level1IdIdrefIdrefs;
    
    CompatibilityControlAttribute ccAttribute;
    DefaultValueAttributeHandler defaultValueHandler;
    
    ActiveModel activeModel;
    ActiveGrammarModel grammarModel;
    
    ValidatorErrorHandlerPool errorHandlerPool;
    ValidatorEventHandlerPool eventHandlerPool;
    ValidationItemLocator validationItemLocator;
        
    ErrorDispatcher errorDispatcher;

    AttributeDefaultValueErrorHandler defaultValueErrorHandler;   

    
    //for competition simetry
    Stack<ArrayList<SAttribute>> attributeListsStack;   
    ArrayList<SAttribute> currentAttributesList;
    
    
    // for attribute default value
    boolean defaultedAttributeContent; // for element level handling: handle choices, required branches, name 
    boolean defaultedAttributeContext; // for datatype: report context dependent   
    boolean controlAlternative;// for choice handling 
    boolean hasAlternative;// for choice handling
    boolean hasName; // for name class control
    
    Stack<ArrayList<AttributeInfo>> attributesDVListsStack;
    ArrayList<AttributeInfo> currentAttributesDVList;
    
    Stack<BooleanList> isRequiredBranchStack;
    BooleanList isRequiredBranch;
    
    Stack<BooleanList> needsOptionalChoiceStack;
    BooleanList needsOptionalChoice;
    
    AttributeDefaultValueModel attributeDefaultValueModel;
    
    
    
    
    // for  ID IDREF IDREFS
    boolean attributeContext; // must be true when idType is not null
    boolean idTypeNull; // when not true must control the name of attribute
    boolean idTypeAttributeContent; // for element level handling: name
    
    Stack<ArrayList<SAttribute>> idAttributeListsStack;
    ArrayList<SAttribute> currentIdAttributesList;

    IntList currentAttributeIdTypesList;
    Stack<IntList> attributeIdTypeListsStack;
    
    SElement currentElement;

    AttributeIdTypeModel attributeIdTypeModel;
    
    
    
    
    ControllerPool controllerPool;
    CompetitionSimetryController simetryController;
        
    MessageWriter debugWriter;
    
    public CompatibilityHandler(ControllerPool controllerPool,
                                ValidatorErrorHandlerPool errorHandlerPool,
                                ValidatorEventHandlerPool eventHandlerPool,
                                ValidationItemLocator validationItemLocator,
                                ErrorDispatcher errorDispatcher, 
                                MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.controllerPool = controllerPool;
        this.errorHandlerPool = errorHandlerPool;
        this.eventHandlerPool = eventHandlerPool;
        this.validationItemLocator = validationItemLocator;        
        this.errorDispatcher = errorDispatcher; 
        defaultValueErrorHandler = new AttributeDefaultValueErrorHandler(errorDispatcher, debugWriter);
        simetryController = new CompetitionSimetryController(controllerPool, errorDispatcher, debugWriter);
        
        attributesDVListsStack = new Stack<ArrayList<AttributeInfo>>();
        attributeListsStack = new Stack<ArrayList<SAttribute>>();
        isRequiredBranchStack = new Stack<BooleanList>();
        needsOptionalChoiceStack = new Stack<BooleanList>();
        
        idAttributeListsStack = new Stack<ArrayList<SAttribute>>();
        currentIdAttributesList = new ArrayList<SAttribute>();
        
        currentAttributeIdTypesList = new IntList();
        attributeIdTypeListsStack = new Stack<IntList>();
    }
    
    public void setLevel1AttributeDefaultValue(boolean value){
        level1AttributeDefaultValue = value;
        if(level1AttributeDefaultValue){
            if(ccAttribute == null) ccAttribute = new CompatibilityControlAttribute(debugWriter);
            if(defaultValueHandler == null) defaultValueHandler = eventHandlerPool.getDefaultValueAttributeValidationHandler();  
        }
    }
    
    public void setLevel1IdIdrefIdrefs(boolean value){
        level1IdIdrefIdrefs = value;
    }
    
    public SchemaModel handle(ValidationModel validationModel) throws SAXException{     
        SimplifiedModel simplifiedModel = validationModel.getSimplifiedModel();
        if(simplifiedModel == null)return new SchemaModel(validationModel, new InfosetModificationModelImpl(null, debugWriter), debugWriter);
        activeModel = validationModel.getActiveModel(validationItemLocator, errorDispatcher);
        grammarModel = activeModel.getGrammarModel();       
                
        startTopPattern = simplifiedModel.getStartTopPattern();
        refDefinitionTopPattern = simplifiedModel.getRefDefinitionTopPattern();
        recursionModel = simplifiedModel.getRecursionModel();
        
        attributeDefaultValueModel = null;
        
        InfosetModificationModel infosetModificationModel = null;
        if(level1AttributeDefaultValue){
            
            attributeDefaultValueModel = new AttributeDefaultValueModel(debugWriter);
            
            ccAttribute.init(grammarModel,
                        activeModel.getStackHandlerPool(),
                        activeModel.getRuleHandlerPool());
            
            attributeListsStack.clear();
            isRequiredBranchStack.clear();
            needsOptionalChoiceStack.clear();
            
            simetryController.clear();
            
            currentAttributesList = new ArrayList<SAttribute>();
            currentAttributesDVList = new ArrayList<AttributeInfo>();                        
            isRequiredBranch = new BooleanList();
            needsOptionalChoice = new BooleanList();
            defaultedAttributeContent = false;
            defaultedAttributeContext = false;    
            controlAlternative = false; 
            hasAlternative = false;
            hasName = false;                
        }
        if(level1IdIdrefIdrefs){
            attributeIdTypeModel = new AttributeIdTypeModel(debugWriter);
            
            attributeContext = false;
            idTypeNull = true;
            idTypeAttributeContent = false;
            
            idAttributeListsStack.clear();
            currentIdAttributesList = new ArrayList<SAttribute>();

            attributeIdTypeListsStack.clear();
            currentAttributeIdTypesList = new IntList();
        }
        
        for(SPattern start : startTopPattern){
            start.accept(this);
        }
        activeModel.recycle();
        if(errorDispatcher.hasAttributeDefaultValueError()) infosetModificationModel = new InfosetModificationModelImpl(null, debugWriter);
        else infosetModificationModel = new InfosetModificationModelImpl(attributeDefaultValueModel, debugWriter);
        return new SchemaModel(validationModel, infosetModificationModel, debugWriter);        
    }
    
    public void visit(SExceptPattern exceptPattern)throws SAXException{
		SimplifiedComponent child = exceptPattern.getChild();
        if(child == null) return;
        if(controlAlternative){
            controlAlternative = false;
        }
		child.accept(this);        
	}
	public void visit(SExceptNameClass exceptNameClass)throws SAXException{
		SimplifiedComponent child = exceptNameClass.getChild();
		if(child != null) child.accept(this);
        hasName = false;
	}
		
	public void visit(SName component){
        hasName = true;
    }
	public void visit(SAnyName anyName)throws SAXException{
        hasName = false;
	}
	public void visit(SNsName nsName)throws SAXException{
        hasName = false;
	}
	public void visit(SChoiceNameClass choice)throws SAXException{
        hasName = false;
	}	
	
	
	public void visit(SElement element)throws SAXException{
        SimplifiedComponent child = element.getChild();
        if(child == null){
            ArrayList<SAttribute> attributesList = new ArrayList<SAttribute>();
            //simetryController = new CompetitionSimetryController(controllerPool, errorDispatcher, debugWriter);
            simetryController.control(element, attributesList);           
            return;
        }
        
        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
        boolean defaultedAttributeContentMemo = defaultedAttributeContent;
        boolean idTypeAttributeContentMemo = idTypeAttributeContent;
        if(level1AttributeDefaultValue){            
            attributesDVListsStack.push(currentAttributesDVList);
            currentAttributesDVList = new ArrayList<AttributeInfo>();
            
            attributeListsStack.push(currentAttributesList);
            currentAttributesList = new ArrayList<SAttribute>();
            
            isRequiredBranchStack.push(isRequiredBranch);
            isRequiredBranch = new BooleanList();
            
            needsOptionalChoiceStack.push(needsOptionalChoice);
            needsOptionalChoice = new BooleanList();
            
            defaultedAttributeContent = false;
            defaultedAttributeContext = false;    
            controlAlternative = false; 
            hasAlternative = false;
        } 
            
        if(level1IdIdrefIdrefs){
            currentElement = element;
            
            idAttributeListsStack.push(currentIdAttributesList);
            currentIdAttributesList = new ArrayList<SAttribute>();
            
            attributeIdTypeListsStack.push(currentAttributeIdTypesList);
            currentAttributeIdTypesList = new IntList();
            
            idTypeAttributeContent = false;
        }
            
        child.accept(this);
        //see about this: only necessary when compatibility
        
        if(level1AttributeDefaultValue){
            if(defaultedAttributeContent){                
                hasName = false;
                SimplifiedComponent nameClass = element.getNameClass();
                if(nameClass != null) nameClass.accept(this);
                String attributes = "";
                if(!hasName){                    
                    for(int i = 0; i < currentAttributesList.size(); i++){
                        SAttribute attr = currentAttributesList.get(i);
                        if(attr.getDefaultValue() != null){
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
                        }
                    }                                        
                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation()+" :"+attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
                
                attributes = "";
                for(int i = 0; i < isRequiredBranch.size(); i++){
                    if(isRequiredBranch.get(i)){
                        SAttribute attr = currentAttributesList.get(i);
                        attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
                    }
                }
                
                if(!attributes.equals("")){                    
                    String message = "DTD compatibility error. Default value not allowed for required attribute definitions in the content model of <"+element.getQName()+"> at "+element.getLocation()+" :"
                                    +attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
            }      
            
            attributeDefaultValueModel.addAttributeInfo(activeModel.getActiveNameClass(element), 
                                                    currentAttributesDVList.toArray(new AttributeInfo[currentAttributesDVList.size()]));
            currentAttributesDVList = attributesDVListsStack.pop();
            
            if(!level1IdIdrefIdrefs){
                simetryController.control(element, currentAttributesList);
                currentAttributesList = attributeListsStack.pop();
            }
            
            isRequiredBranch = isRequiredBranchStack.pop();
            needsOptionalChoice = needsOptionalChoiceStack.pop();
            
            defaultedAttributeContext = defaultedAttributeContextMemo;
            defaultedAttributeContent = defaultedAttributeContentMemo;
        }
        
        if(level1IdIdrefIdrefs){
            if(idTypeAttributeContent){
                if(level1AttributeDefaultValue && defaultedAttributeContent){
                    if(!hasName){
                        String attributes = "";
                        for(int i = 0; i < currentIdAttributesList.size(); i++){
                            SAttribute attr = currentIdAttributesList.get(i);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
                        }                                        
                        String message = "DTD compatibility error. Attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation()+" may not have non-null ID type: "+attributes+".";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }
                }else{
                    hasName = false;
                    SimplifiedComponent nameClass = element.getNameClass();
                    if(nameClass != null) nameClass.accept(this);
                    if(!hasName){
                        String attributes = "";
                        for(int i = 0; i < currentIdAttributesList.size(); i++){
                            SAttribute attr = currentIdAttributesList.get(i);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
                        }                                        
                        String message = "DTD compatibility error. Attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation()+" may not have non-null ID type: "+attributes+".";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }
                }
            }
            
            if(level1AttributeDefaultValue){
                simetryController.control(element, currentAttributesList, currentAttributeIdTypesList);
                currentAttributesList = attributeListsStack.pop();
            }else{
                simetryController.control(currentAttributesList, currentAttributeIdTypesList);
                currentAttributesList = attributeListsStack.pop();
            }
                
            idTypeAttributeContent = idTypeAttributeContentMemo;            
            currentIdAttributesList = idAttributeListsStack.pop();
        }       
	}	
	public void visit(SAttribute attribute)throws SAXException{
        currentAttributesList.add(attribute);
        		
		SimplifiedComponent[] children = attribute.getChildren();
        
        if(children == null) return;        
		if(children.length > 1) return; // syntax error
        
        String defaultValue = null;
        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
        boolean idTypeAttributeContentMemo = idTypeAttributeContent;  
        if(level1AttributeDefaultValue){
            defaultValue = attribute.getDefaultValue();
            if(defaultValue != null){                
                isRequiredBranch.add(true);
                needsOptionalChoice.add(true);
                
                ccAttribute.init(grammarModel.getIndex(attribute), attribute.getQName(), attribute.getLocation());
                defaultValueErrorHandler.setAttribute(attribute.getQName(), attribute.getLocation());                
                defaultValueHandler.init(ccAttribute, defaultValueErrorHandler);
                defaultValueHandler.handleAttribute(defaultValue);
                defaultValueHandler.reset();
                                
                defaultedAttributeContext = true;    
                controlAlternative = false; 
                hasAlternative = false;
                hasName = false;
            }else{
                isRequiredBranch.add(false);
                needsOptionalChoice.add(false);
            }
        }
        if(level1IdIdrefIdrefs){
            attributeContext = true;
            idTypeNull = true;
        }
        //see that this is done only if necessary
        children[0].accept(this);
        
        
        if(level1AttributeDefaultValue){
            if(defaultedAttributeContext){
                SNameClass nc = attribute.getNameClass();                
                nc.accept(this);                
                if(!hasName){
                    String message = "DTD compatibility error. Default value not allowed for an attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation()+" .";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }else{
                    SName name = (SName)nc;
                    currentAttributesDVList.add(new AttributeInfo(name.getNamespaceURI(), 
                                                    name.getLocalPart(), 
                                                    null, 
                                                    defaultValue,
                                                    Datatype.ID_TYPE_NULL,//not always correct, but it doesn't matter here
                                                    debugWriter));
                }                
                defaultedAttributeContent = true;                
            }
            defaultValueErrorHandler.report();
            defaultValueErrorHandler.clear();            
            defaultedAttributeContext = defaultedAttributeContextMemo;
        }
        
        if(level1IdIdrefIdrefs){
            if(!idTypeNull){                
                if(level1AttributeDefaultValue && defaultedAttributeContext){
                    if(!hasName) {
                        String message = "DTD compatibility error. Attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation()+" may not have non-null ID type.";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }
                }else{
                    hasName = false;
                    SNameClass nc = attribute.getNameClass();                
                    nc.accept(this);
                    if(hasName){
                        // TODO see about mapping
                    }else{
                        String message = "DTD compatibility error. Attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation()+" may not have non-null ID type.";
                        errorDispatcher.error(new AttributeIdTypeException(message, null));
                    }
                }
                
                currentIdAttributesList.add(attribute);
                idTypeAttributeContent = true;
            }else{
                idTypeAttributeContent = idTypeAttributeContentMemo;
            }
        }
	}
    
	public void visit(SChoicePattern choice)throws SAXException{
		SimplifiedComponent[] children = choice.getChildren();
        if(children == null)return;
        
        int attributesOffset;        
        
        if(level1AttributeDefaultValue){
            boolean defaultedAttributeContentMemo = defaultedAttributeContent;            
            attributesOffset = currentAttributesList.size();
            hasAlternative = false;
            for(SimplifiedComponent child : children){
                if(!hasAlternative)controlAlternative = true;
                else controlAlternative = false;
                child.accept(this);
            }
            if(defaultedAttributeContent){
                if(hasAlternative){
                    for(int i = attributesOffset; i < isRequiredBranch.size(); i++){
                        isRequiredBranch.set(i, false);
                    }
                }else{
                    String attributes = "";
                    for(;attributesOffset < needsOptionalChoice.size(); attributesOffset++){
                        if(needsOptionalChoice.get(attributesOffset)){
                            SAttribute attr = currentAttributesList.get(attributesOffset);
                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
                            
                            isRequiredBranch.set(attributesOffset, false);//needs no more checking, an error was reported already
                        }
                    }
                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the context of a choice without <empty> alternative, <"+choice.getQName()+"> at "+choice.getLocation()+" :"+attributes+".";
                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
                }
            }else{
                defaultedAttributeContent = defaultedAttributeContentMemo;
            }
        }else{
            for(SimplifiedComponent child : children){
                child.accept(this); 
            }
        }
	}
	public void visit(SInterleave interleave)throws SAXException{
		SimplifiedComponent[] children = interleave.getChildren();
        if(children == null)return;
        
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
        for(SimplifiedComponent child : children){
            child.accept(this); 
        }           
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}
	public void visit(SGroup group)throws SAXException{
		SimplifiedComponent[] children = group.getChildren();		
        if(children == null) return;
        
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
        for(SimplifiedComponent child : children){
            child.accept(this); 
        }
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}
	public void visit(SZeroOrMore zeroOrMore)throws SAXException{
		SimplifiedComponent child = zeroOrMore.getChild();
        if(child == null) return;
        
        boolean controlAlternativeMemo = false;
        int attributesOffset = currentAttributesList.size();
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        for(;attributesOffset < isRequiredBranch.size(); attributesOffset++){
            isRequiredBranch.set(attributesOffset, false);
        }
	}
	public void visit(SOneOrMore oneOrMore)throws SAXException{
		SimplifiedComponent child = oneOrMore.getChild();
        if(child == null) return;
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}
	public void visit(SOptional optional)throws SAXException{
		SimplifiedComponent child = optional.getChild();
        if(child == null) return;
        
        int attributesOffset = currentAttributesList.size();
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
        for(;attributesOffset < isRequiredBranch.size(); attributesOffset++){
            isRequiredBranch.set(attributesOffset, false);
        }
	}
	public void visit(SMixed mixed)throws SAXException{
		SimplifiedComponent child = mixed.getChild();
        if(child == null) return;
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}	
	public void visit(SListPattern list)throws SAXException{
		SimplifiedComponent child = list.getChild();
        if(child == null) return;
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		child.accept(this);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}	
	public void visit(SEmpty empty){
        if(controlAlternative){
            hasAlternative = true;
        }
    }
	public void visit(SText text){
        if(level1IdIdrefIdrefs && attributeContext){
            currentAttributeIdTypesList.add(Datatype.ID_TYPE_NULL);
        }
    }
	public void visit(SNotAllowed notAllowed){}
	public void visit(SRef ref)throws SAXException{        
        if(recursionModel.isRecursiveReference(ref)){
            return;
        }
        int index = ref.getDefinitionIndex(); 
        SPattern defTopPattern = refDefinitionTopPattern[index];
        if(defTopPattern != null)defTopPattern.accept(this);
    }
	public void visit(SValue value)throws SAXException{
        if(defaultedAttributeContext){
            Datatype datatype = value.getDatatype();
            if(datatype.isContextDependent()){
                defaultValueErrorHandler.contextDependentDatatypeError(value);
            }            
        }        
        
        if(level1IdIdrefIdrefs){
            Datatype dt = value.getDatatype();
            int idType = dt.getIdType();           
            
            if(!attributeContext){
                if(idType != Datatype.ID_TYPE_NULL){
                    String message = "DTD compatibility error. Definition <"+value.getQName()+"> at "+value.getLocation()+" specifying the content of element <"+currentElement.getQName()+"> at "+currentElement.getLocation() +" may not have a non-null ID type.";
                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                }
            }else{
                if(idType != Datatype.ID_TYPE_NULL){
                    idTypeNull = false;            
                }
                currentAttributeIdTypesList.add(idType);
            }
        }
    }    
    
	public void visit(SData data)throws SAXException{	
		if(defaultedAttributeContext){
            Datatype datatype = data.getDatatype();
            if(datatype.isContextDependent()){
                defaultValueErrorHandler.contextDependentDatatypeError(data);
            }            
        }
 
        if(level1IdIdrefIdrefs){
            Datatype dt = data.getDatatype();
            int idType = dt.getIdType();
            
            if(!attributeContext){
                if(idType != Datatype.ID_TYPE_NULL){
                    String message = "DTD compatibility error. Definition <"+data.getQName()+"> at "+data.getLocation()+" specifying the content of element <"+currentElement.getQName()+"> at "+currentElement.getLocation() +" may not have a non-null ID type.";
                    errorDispatcher.error(new AttributeIdTypeException(message, null));
                }
            }else{
                if(idType != Datatype.ID_TYPE_NULL){
                    idTypeNull = false;
                }
                currentAttributeIdTypesList.add(idType);
            }
        }
	}	
	public void visit(SGrammar grammar)throws SAXException{
		SimplifiedComponent child = grammar.getChild();
		if(child != null) child.accept(this);
	}
    
    public void visit(SDummy dummy)throws SAXException{
		SimplifiedComponent[] children = dummy.getChildren();
        if(children == null) return;
        boolean controlAlternativeMemo = false;
        if(controlAlternative){
            controlAlternativeMemo = controlAlternative;
            controlAlternative = false;
        }
		next(children);
        if(controlAlternativeMemo){
            controlAlternative = controlAlternativeMemo;
        }
	}
		
		
	protected void next(SimplifiedComponent[] children)throws SAXException{
		for(SimplifiedComponent child : children){            
			child.accept(this);
		}
	} 
}
