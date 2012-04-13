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

package serene.validation.schema.active;

import serene.validation.schema.Component;

import serene.validation.schema.simplified.SimplifiedComponent;

public interface ActiveComponent extends Component{
	ActiveComponent getParent();	
	void accept(ActiveComponentVisitor v);	
	String getQName();
	String getLocation(boolean restrictToFileName);
    /**
    * Returns the hash code of the corresponding SimplifiedComponent. Watch out 
    * for recursive definitions, regardless of the depth of the ActiveComponent
    * the code will be the same.
    */
	int functionalEquivalenceCode();
	
	SimplifiedComponent getCorrespondingSimplifiedComponent();
}