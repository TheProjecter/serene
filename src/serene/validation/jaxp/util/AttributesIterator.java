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

package serene.validation.jaxp.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.namespace.QName;

import javax.xml.stream.events.Attribute;

public class AttributesIterator implements Iterator{    
    int position = 0;
    boolean removed = true;
    ArrayList<Attribute> attributes;
    
    public AttributesIterator(ArrayList<Attribute> attributes){
        this.attributes = attributes;
    }
    
    public boolean hasNext(){
        return attributes.size() > position;
    }
    
    public Object next(){            
        if(!hasNext()) throw new NoSuchElementException();
        removed = false;
        return attributes.get(position++);
    }
    
    public void remove(){
        if(removed)throw new IllegalStateException();
        removed = true;
        attributes.remove(--position);
    }
}
