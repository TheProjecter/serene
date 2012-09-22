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

package serene.validation.handlers.match;

public abstract class StructuredDataMatchPath extends CharsMatchPath{
    StructuredDataMatchPath(MatchPathPool pool){
        super(pool);
    }
    
    public boolean isRequiredBranch(){
        for(int i = 0; i < lastIndex; i++){//last record is type, do not question
            if(!list[i].isRequired())return false;
        }        
        return true;
    } 
}
