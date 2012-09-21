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

import serene.validation.schema.simplified.SimplifiedPattern;
import serene.validation.schema.simplified.components.SGroup;
import serene.validation.schema.simplified.components.SInterleave;
import serene.validation.schema.simplified.components.SMixed;


public class MorePath{
	ArrayList<ArrayList<SimplifiedPattern>> pathes;
	int size;
	
	public MorePath(){
		pathes = new ArrayList<ArrayList<SimplifiedPattern>>();
		size = 0;
	}
	
	
	/*public void push(SZeroOrMore more){
		ArrayList<SimplifiedPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SimplifiedPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(more);
	}
	
	public void push(SOneOrMore more){
		ArrayList<SimplifiedPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SimplifiedPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(more);
	}*/
	
	public void pushMultipleCardinalityPattern(SimplifiedPattern p){
	    ArrayList<SimplifiedPattern> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SimplifiedPattern>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(p);
	}
	
	public void push(SGroup g){
		ArrayList<SimplifiedPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(g);
	}
	
	public void push(SInterleave i){
		ArrayList<SimplifiedPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(i);
	}
	
	public void push(SMixed m){
		ArrayList<SimplifiedPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(m);
	}
	
	public SimplifiedPattern popItem(){
		ArrayList<SimplifiedPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}		
		return path.remove(path.size()-1);
	}
    
    public SimplifiedPattern peekItem(){
		ArrayList<SimplifiedPattern> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}		
		return path.get(path.size()-1);
	}
		
	public ArrayList<SimplifiedPattern> pop(){		
		return pathes.get(--size);
	}
	
	public ArrayList<SimplifiedPattern> peek(){
		return pathes.get(size-1);
	}
	
	public ArrayList<SimplifiedPattern> doublePeek(){
		return pathes.get(size-2);
	}
	
	public void clear(){
		size = 0;
	}
}