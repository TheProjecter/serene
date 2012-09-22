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

package serene.restrictor.util;

import java.util.ArrayList;

import serene.validation.schema.simplified.SPattern;
import serene.validation.schema.simplified.SGroup;
import serene.validation.schema.simplified.SInterleave;


public class MorePath{
	ArrayList<ArrayList<SPattern>> pathes;
	int size;
	
	public MorePath(){
		pathes = new ArrayList<ArrayList<SPattern>>();
		size = 0;
	}
	
	
	/*public void push(SZeroOrMore more){
		ArrayList<SPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(more);
	}
	
	public void push(SOneOrMore more){
		ArrayList<SPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(more);
	}*/
	
	public void pushMultipleCardinalityPattern(SPattern p){
	    ArrayList<SPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(p);
	}
	
	public void push(SGroup g){
		ArrayList<SPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(g);
	}
	
	public void push(SInterleave i){
		ArrayList<SPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(i);
	}
	

	public SPattern popItem(){
		ArrayList<SPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}		
		return path.remove(path.size()-1);
	}
    
    public SPattern peekItem(){
		ArrayList<SPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}		
		return path.get(path.size()-1);
	}
		
	public ArrayList<SPattern> pop(){		
		return pathes.get(--size);
	}
	
	public ArrayList<SPattern> peek(){
		return pathes.get(size-1);
	}
	
	public ArrayList<SPattern> doublePeek(){
		return pathes.get(size-2);
	}
	
	public void clear(){
		size = 0;
	}
}