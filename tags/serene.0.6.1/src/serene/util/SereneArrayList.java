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

package serene.util;

import java.util.ArrayList;

public class SereneArrayList<E> extends ArrayList<E>{
		
	public void removeTail(int fromIndex){
		removeRange(fromIndex, size());
	}
	
	public void removeHead(int toIndex){
		removeRange(0, toIndex);
	}
}