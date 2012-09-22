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

import serene.validation.schema.simplified.SimplifiedComponent;
import serene.validation.schema.simplified.SData;
import serene.validation.schema.simplified.SExceptPattern;


public class DataPath{
	ArrayList<ArrayList<SimplifiedComponent>> pathes;
	int size;
	
	public DataPath(){
		pathes = new ArrayList<ArrayList<SimplifiedComponent>>();
		size = 0;
	}
	
	public void push(SData data){
		ArrayList<SimplifiedComponent> path;
		if(pathes.size() < ++size){
			path = new ArrayList<SimplifiedComponent>();
			pathes.add(path);
		}else{
			path = pathes.get(size-1);
			path.clear();
		}
		
		path.add(data);
	}
		
	public void push(SExceptPattern ep){
		ArrayList<SimplifiedComponent> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}
		
		path.add(ep);
	}
	
	public SimplifiedComponent popItem(){
		ArrayList<SimplifiedComponent> path = pathes.get(size-1);
		if(path == null){
			throw new IllegalStateException();
		}		
		return path.remove(path.size()-1);
	}
		
	public ArrayList<SimplifiedComponent> pop(){		
		return pathes.get(--size);
	}
	
	public ArrayList<SimplifiedComponent> peek(){
		return pathes.get(size-1);
	}
	
	public void clear(){
		size = 0;
	}
}