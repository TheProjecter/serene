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

package serene.validation.schema.active.components;

import java.util.List;

import serene.validation.schema.active.Rule;

import serene.validation.handlers.structure.ChildEventHandler;
import serene.validation.handlers.structure.impl.ParticleHandler;

import serene.validation.handlers.error.ErrorCatcher;

public interface APattern extends Rule{	
	public static final int UNBOUNDED = -1;
	
	int getMinOccurs();
	int getMaxOccurs();
	
	void setReleased();
	
	ParticleHandler getParticleHandler(ChildEventHandler ceh, ErrorCatcher ec);
	
	boolean isRequiredContent();
    boolean isRequiredBranch();
    
    
    boolean isElementContent();
	boolean isAttributeContent();
	boolean isDataContent();
	boolean isValueContent();
	boolean isListPatternContent();
	boolean isTextContent();
	
	boolean isCharsContent();
    boolean isStructuredDataContent();
    boolean isUnstructuredDataContent();
    
    
    void setElementMatches(String ns, String name, List<AElement> elements);
    void setAttributeMatches(String ns, String name, List<AAttribute> attributes);    
    
    void setMatches(List<AText> texts);    
    void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns, List<AText> texts);
    void setMatches(List<AData> datas, List<AValue> values, List<AListPattern> listPatterns);
    void setMatches(List<AData> datas, List<AValue> values);
}	