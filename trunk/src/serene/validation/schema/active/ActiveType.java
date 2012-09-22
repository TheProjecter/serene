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

package serene.validation.schema.active;

import java.util.Map;

import serene.validation.handlers.error.ErrorCatcher;

import serene.validation.handlers.stack.StackHandler;
import serene.validation.handlers.stack.ConcurrentStackHandler;

import serene.validation.handlers.structure.StructureHandler;

import serene.validation.handlers.conflict.ExternalConflictHandler;

import serene.validation.schema.active.components.AElement;
import serene.validation.schema.active.components.AAttribute;

public interface ActiveType extends Rule{
	
	StructureHandler getStructureHandler(ErrorCatcher errorCatcher, StackHandler stackHandler);
	
	/*StackHandler getStackHandler(ErrorCatcher ec);	
	ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher);*/	
	
	/*ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues);
	ConcurrentStackHandler getStackHandler(StackHandler originalHandler, ErrorCatcher errorCatcher, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders);
	StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher);
	StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher, Queue targetQueue, int targetEntry,  Map<AElement, Queue> candidateQueues);
	StackHandler getStackHandler(StackHandler originalHandler, int candidatesCount, ExternalConflictHandler conflictHandler, ErrorCatcher errorCatcher, String value, Queue queue, int entry, Map<AAttribute, AttributeBinder> attributeBinders);*/
}