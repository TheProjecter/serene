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

package serene.validation.schema.parsed.components;

import java.util.Map;

import org.xml.sax.SAXException;

import serene.validation.schema.parsed.components.Pattern;
import serene.validation.schema.parsed.components.ExceptPattern;
import serene.validation.schema.parsed.components.Param;

import serene.validation.schema.parsed.ParsedComponentVisitor;
import serene.validation.schema.parsed.SimplifyingVisitor;

import sereneWrite.MessageWriter;

public class Data extends Pattern{
	String type;	
	Param[] param;
	ExceptPattern[] exceptPattern; 	
		
	public Data(Map<String, String> prefixMapping, String xmlBase, String ns, String datatypeLibrary, String type, Param[] param, ExceptPattern[] exceptPattern, String qName, String location, MessageWriter debugWriter){
		super(prefixMapping, xmlBase, ns, datatypeLibrary, qName, location, debugWriter);
		this.type = type; 
		asParent(param);
		asParent(exceptPattern);
	}
	public String getType(){
		return type;	
	}
	protected void asParent(Param[] param){
		this.param = param;
		if(param != null){		
			for(int i = 0; i< param.length; i++){
				param[i].setParent(this);
				param[i].setChildIndex(i);
			}
		}
	}	
	
	protected void asParent(ExceptPattern[] exceptPattern){		
		this.exceptPattern = exceptPattern;
		if(exceptPattern != null){		
			for(int i = 0; i< exceptPattern.length; i++){
				exceptPattern[i].setParent(this);
				exceptPattern[i].setChildIndex(i);
			}
		}
	}	
	
	public Param[] getParam(){
		return param;
	}
	
	public Param getParam(int childIndex){
		if(param == null || param.length == 0)return null;
		return param[childIndex];
	}
	
	public ExceptPattern[] getExceptPattern(){
		return exceptPattern;
	}
	
	public ExceptPattern getExceptPattern(int childIndex){
		if(exceptPattern == null || exceptPattern.length == 0)return null;
		return exceptPattern[childIndex];
	}
	
	public void accept(ParsedComponentVisitor v){
		v.visit(this);
	}
	public void accept(SimplifyingVisitor v) throws SAXException{
		v.visit(this);
	}
	
	public String toString(){
		return "Data type "+type;
	}
}