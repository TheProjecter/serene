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

import org.xml.sax.Locator;

import serene.SereneRecoverableException;

public class DTDCompatibilityException extends SereneRecoverableException{

	public DTDCompatibilityException(String message, Locator locator){
		super(message, locator);
	}
	public DTDCompatibilityException(String message, Locator locator, Exception e){
		super(message, locator, e);
	}
	public DTDCompatibilityException(String message, String publicId, String systemId, int lineNumber, int columnNumber){
		super(message, publicId, systemId, lineNumber, columnNumber);
	}
	public DTDCompatibilityException(String message, String publicId, String systemId, int lineNumber, int columnNumber, Exception e){
		super(message, publicId, systemId, lineNumber, columnNumber, e);
	} 
}
