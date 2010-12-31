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

package serene.validation.handlers.content;

import serene.validation.schema.active.DataActiveType;
import serene.validation.schema.active.StructuredDataActiveType;
import serene.validation.schema.active.CharsActiveType;

import sereneWrite.MessageWriter;

public interface CharactersEventHandler extends EventHandler{	 
	MarkupEventHandler getParentHandler(); 
	
	void handleChars(char[] chars, DataActiveType context);
	void handleChars(char[] chars, StructuredDataActiveType context);
	void handleChars(char[] chars, CharsActiveType context);
	
	void handleString(String value, DataActiveType context);
	void handleString(String value, StructuredDataActiveType context);
	void handleString(String value, CharsActiveType context);
}