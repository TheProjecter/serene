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


package serene.schematron;

import java.net.URI;

import javax.xml.transform.Source;

import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.TransformerException;

public class URIResolver implements javax.xml.transform.URIResolver{
    static final String EMPTY_STRING = "";    
    public Source resolve(String href, String base) throws TransformerException{        
        //System.out.println("URI RESOLVER RESOLVE href="+href+"   base="+base);
        
        if(base == null || base.equals(EMPTY_STRING))return new StreamSource(URIResolver.class.getResourceAsStream("/"+href));
        
        URI uri = null;
        try{
            uri = new URI(base);
            uri = uri.resolve(new URI(href));
        }catch(Exception e){
            throw new TransformerException(e);
        }
        return new StreamSource(URIResolver.class.getResourceAsStream(uri.toASCIIString()));
    }
}

