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


package serene.validation.handlers.error;

public interface ErrorType{
    int UNKNOWN_ELEMENT = 0;    
	int UNEXPECTED_ELEMENT = 1;    
	int UNEXPECTED_AMBIGUOUS_ELEMENT = 2;  
    
	int UNKNOWN_ATTRIBUTE = 3;    
	int UNEXPECTED_ATTRIBUTE = 4;    
	int UNEXPECTED_AMBIGUOUS_ATTRIBUTE = 5;
    
	int MISPLACED_ELEMENT = 6;    
	int EXCESSIVE_CONTENT = 7;            
    
	int AMBIGUOUS_ELEMENT_CONTENT_ERROR = 8;    
	int AMBIGUOUS_ATTRIBUTE_CONTENT_ERROR = 9;    
	int AMBIGUOUS_CHARS_CONTENT_ERROR = 10;
    
	int MISSING_CONTENT = 11;    
	int ILLEGAL_CONTENT = 12;    
	int UNDETERMINED_BY_CONTENT = 13;
    
	int CHARACTER_CONTENT_DATATYPE_ERROR = 14;    
	int ATTRIBUTE_VALUE_DATATYPE_ERROR = 15;
    
	int CHARACTER_CONTENT_VALUE_ERROR = 16;        
	int ATTRIBUTE_VALUE_VALUE_ERROR = 17;
    
	int CHARACTER_CONTENT_EXCEPTED_ERROR = 18;    
	int ATTRIBUTE_VALUE_EXCEPTED_ERROR = 19;
    
	int UNEXPECTED_CHARACTER_CONTENT = 20;    
	int UNEXPECTED_ATTRIBUTE_VALUE = 21;
    
	int AMBIGUOUS_CHARACTER_CONTENT = 22;    
	int AMBIGUOUS_ATTRIBUTE_VALUE = 23;
    
	int LIST_TOKEN_DATATYPE_ERROR = 24;        
	int LIST_TOKEN_VALUE_ERROR = 25;    
	int LIST_TOKEN_EXCEPTED_ERROR = 26;    
	int AMBIGUOUS_LIST_TOKEN = 27;        
    int AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_ERROR = 28;
    
	int MISSING_COMPOSITOR_CONTENT = 29;
    
    int CONFLICT = 30;// TODO remove UNDETERMINED_BY_CONTENT!!!  
    
    int ERROR_COUNT = 31;
    
    
    
    int AMBIGUOUS_ELEMENT_CONTENT_WARNING = 0;
	int AMBIGUOUS_ATTRIBUTE_CONTENT_WARNING = 1;
	int AMBIGUOUS_CHARS_CONTENT_WARNING = 2;
	
    int AMBIGUOUS_LIST_TOKEN_IN_CONTEXT_WARNING = 3;
    
    int WARNING_COUNT = 4;    
}
