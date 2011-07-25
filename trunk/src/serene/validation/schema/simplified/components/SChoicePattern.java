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

package serene.validation.schema.simplified.components;

import java.util.ArrayList;

import java.io.File;

import org.xml.sax.SAXException;

import serene.validation.schema.simplified.RestrictingVisitor;
import serene.validation.schema.simplified.SimplifiedComponentVisitor;

import serene.validation.schema.simplified.components.SPattern;

import sereneWrite.MessageWriter;

public class SChoicePattern extends AbstractMultipleChildrenPattern{
    ArrayList<String> allLocations;
	public SChoicePattern(SPattern[] children,
				String qName, 
				ArrayList<String> allLocations, 
				MessageWriter debugWriter){		
		super(children, qName, null, debugWriter);
        this.allLocations = allLocations;
	}

    public SChoicePattern(SPattern[] children,
				String qName, 
				String location, 
				MessageWriter debugWriter){		
		super(children, qName, location, debugWriter);
	}
    
	public void accept(SimplifiedComponentVisitor v){
		v.visit(this);
	}
	public void accept(RestrictingVisitor v) throws SAXException{
		v.visit(this);        
	}
    
    public ArrayList<String> getAllLocations(){
        return allLocations;
    }    
    public String getLocation(boolean restrictToFileName){
        if(location == null)return getAllLocations(restrictToFileName);
        if(!restrictToFileName)return location;
        int nameIndex = location.lastIndexOf(File.separatorChar)+1;
        if(nameIndex == 0) nameIndex = location.lastIndexOf('/')+1;
        return location.substring(nameIndex);
	}
    
    private String getAllLocations(boolean restrictToFileName){
        if(allLocations == null || allLocations.isEmpty())return null;
        if(!restrictToFileName)return allLocations.toString();
        boolean first = true;
        String loc = "";
        for(String l : allLocations){
            int nameIndex = l.lastIndexOf(File.separatorChar)+1;
            if(nameIndex == 0) nameIndex = l.lastIndexOf('/')+1;            
            l = l.substring(nameIndex);
            if(first){
                first = false;
                loc += "["+l;
            }else{
                loc += ", "+l;
            }
        }
        return loc+"]";
    }
	public String toString(){
		String s = "SChoicePattern";
		return s;
	}
}