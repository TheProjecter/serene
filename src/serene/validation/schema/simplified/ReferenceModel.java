/*
Copyright 2012 Radu Cernuta 

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

package serene.validation.schema.simplified;


public class ReferenceModel{
	SPattern[] refDefinitionTopPattern;

	public void init(SPattern[] refDefinitionTopPattern){
	    this.refDefinitionTopPattern = refDefinitionTopPattern;
	}
	
	public SPattern getRefDefinitionTopPattern(int index){
	    return refDefinitionTopPattern[index];
	}
	
	public SPattern[] getRefDefinitionTopPatterns(){
	    return refDefinitionTopPattern;
	}
}
