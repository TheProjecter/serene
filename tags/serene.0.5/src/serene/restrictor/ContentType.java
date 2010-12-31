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

package serene.restrictor;

abstract class ContentType{
	//The ids are chosen to respect the order described in the RNG specification 7.2 
	static final int ERROR = -2;
	static final int EMPTY = -1;	
	static final int COMPLEX = 0;
	static final int SIMPLE = 1;
}