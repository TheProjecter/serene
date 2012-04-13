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

package serene.validation.handlers.content.util;

import serene.util.SpaceCharsHandler;


public class CharacterContentDescriptorPool{
    int characterContentDescriptorFree, characterContentDescriptorMaxSize;
	CharacterContentDescriptor[] characterContentDescriptor;
	
	ActiveInputDescriptor aid;
	SpaceCharsHandler spaceCharsHandler;
	
	public CharacterContentDescriptorPool(ActiveInputDescriptor aid, SpaceCharsHandler spaceCharsHandler){	
	    this.aid = aid;
	    this.spaceCharsHandler = spaceCharsHandler;
	    
		characterContentDescriptorFree = 0;
		characterContentDescriptorMaxSize = 10;
		characterContentDescriptor = new CharacterContentDescriptor[5];
	}
	
		
	public CharacterContentDescriptor getCharacterContentDescriptor(){
		if(characterContentDescriptorFree == 0){
			return new CharacterContentDescriptor(aid, spaceCharsHandler, this);
		}
		else{				
			return characterContentDescriptor[--characterContentDescriptorFree];
		}
	}
	
	
	
	public void recycle(CharacterContentDescriptor q){
	    if(characterContentDescriptorFree == characterContentDescriptorMaxSize) return;
		if(characterContentDescriptorFree == characterContentDescriptor.length){	
			CharacterContentDescriptor[] increased = new CharacterContentDescriptor[5+characterContentDescriptor.length];
			System.arraycopy(characterContentDescriptor, 0, increased, 0, characterContentDescriptorFree);
			characterContentDescriptor = increased;
		}
		characterContentDescriptor[characterContentDescriptorFree++] = q;
	}
}

