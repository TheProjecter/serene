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


package serene.bind;

import java.util.Map;

/**
* The context of the current element task. Used when executing the queued 
* tasks, so it assumes that the order is known and no arguments are passed to 
* the getter methods.
*/
public interface ElementTaskContext extends TaskContext{
	/*String getStartLocation();
	String getEndLocation();
    NameInfo getElementNameInfo();// TODO Consider replacing with getElementRecordIndex 
    AttributeInfo[] getAttributeInfo(SAttribute attribute);// TODO Consider replacing with getAttributeRecordIndex
	String getCharacterContent();*/
	
	int getElementInputRecordIndex();
	String getCharacterContent();	
	/*Map<String, String> getDeclaredXmlns();*/
}
