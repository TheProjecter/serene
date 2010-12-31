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

package serene.validation.schema.simplified.components;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.simplified.components.SPattern;
import serene.validation.schema.simplified.components.SExceptPattern;
import serene.validation.schema.simplified.components.SParam;

import sereneWrite.MessageWriter;

public class SData extends SPattern{
	String datatypeLibrary;
	String type;	
	SParam[] param;
	SExceptPattern[] exceptPattern; 	
		
	public SData(String datatypeLibrary, String type, SParam[] param, SExceptPattern[] exceptPattern, String qName, String location, MessageWriter debugWriter){
		super(qName, location, debugWriter);
		this.datatypeLibrary = datatypeLibrary;
		this.type = type; 
		asParent(param);
		asParent(exceptPattern);
	}
	
	public String getDatatypeLibrary(){
		return datatypeLibrary;
	}
	public String getType(){
		return type;
	}
	protected void asParent(SParam[] param){
		this.param = param;
		if(param != null){		
			for(int i = 0; i< param.length; i++){
				param[i].setParent(this);
				param[i].setChildIndex(i);
			}
		}
	}	
	
	protected void asParent(SExceptPattern[] exceptPattern){		
		this.exceptPattern = exceptPattern;
		if(exceptPattern != null){		
			for(int i = 0; i< exceptPattern.length; i++){
				exceptPattern[i].setParent(this);
				exceptPattern[i].setChildIndex(i);
			}
		}
	}	
	
	public SParam[] getParam(){
		return param;
	}
	
	public SParam getParam(int childIndex){
		if(param == null || param.length == 0)return null;
		return param[childIndex];
	}
	
	public SExceptPattern[] getExceptPattern(){
		return exceptPattern;
	}
	
	public SExceptPattern getExceptPattern(int childIndex){
		if(exceptPattern == null || exceptPattern.length == 0)return null;
		return exceptPattern[childIndex];
	}
	
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);
	}
	public String toString(){
		return "SData type "+type;
	}
}