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

package serene.parser;

import serene.validation.schema.simplified.components.SAttribute;
import serene.util.AttributeInfo;
import sereneWrite.MessageWriter;

class ExternalRefTask extends RNGParseEndElementTask{
	SAttribute href;
	
	ExternalRefPool pool;
	
	ExternalRefTask(SAttribute ns,
						SAttribute datatypeLibrary,
						SAttribute href,
                        SAttribute foreign, 
						ExternalRefPool pool, 
						MessageWriter debugWriter){
		super(ns, datatypeLibrary, foreign, debugWriter);
		this.pool = pool;
		this.href = href;
	}
	
	public void recycle(){
		context = null;
		builder = null;
		pool.recycle(this);
	}
	public void execute(){
		builder.buildExternalRef(getPrefixMapping(), getXmlBase(), getNs(), getDatatypeLibrary(), getHref(), getOtherAttributes(), getQName(), getLocation());
	}
	
	String getHref(){
		AttributeInfo[] ai = context.getAttributeInfo(href);
        if(ai == null)return null;
		return ai[0].getValue();
	}
}