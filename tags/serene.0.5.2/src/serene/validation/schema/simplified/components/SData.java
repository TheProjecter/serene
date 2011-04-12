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

import org.relaxng.datatype.Datatype;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import sereneWrite.MessageWriter;

public class SData extends SPattern{
    Datatype datatype;
	SExceptPattern[] exceptPattern; 	
		
	public SData(Datatype datatype, SExceptPattern[] exceptPattern, String qName, String location, MessageWriter debugWriter){
		super(qName, location, debugWriter);
		this.datatype = datatype;
		asParent(exceptPattern);
	}
	
	public Datatype getDatatype(){
		return datatype;
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
		return "SData datatype "+datatype;
	}
}