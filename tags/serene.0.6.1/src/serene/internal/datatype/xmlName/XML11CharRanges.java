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

package serene.internal.datatype.xmlName;

import java.util.Arrays;

class XML11CharRanges{	
	// ranges as open intervals(allows the transparent treatment of ranges and singles)
	// min at even indexes starting from 0; max at corresponding uneven indexes starting from 1
	static final int[] startCharRanges; 
	static final int[] charRanges;
		
	static{		
		startCharRanges = new int[32];
		charRanges = new int[40];
		int i = -1;
		int j = -1;
		
		int single = 0x003A;  //':'
		single--;
		startCharRanges[++i] = single;  //charRanges[++j] = single; merge with 0x0030 - 0x0039 
		single++;
		single++;
		startCharRanges[++i] = single;  charRanges[++j] = single;
		
		int low = 0x0041;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		int high = 0x005A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x005F;  //'_'
		single--;
		startCharRanges[++i] = single;  charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single;  charRanges[++j] = single;
		
		low = 0x0061;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x007A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x00C0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x00D6;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x00D8;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x00F6;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x00F8;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x02FF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0370;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x037D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x037F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FFF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x200C;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x200D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x2070;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x218F;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x2C00;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x2FEF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x3001;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0xD7FF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0xF900;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0xFDCF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0xFDF0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0xFFFD;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x10000;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0xEFFFF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x002D;//'-'
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x002e
		
		single = 0x002E; //'.'
		//single--;
		//charRanges[++j] = single;
		//single++;   					merge with 0x002d
		single++;
		charRanges[++j] = single;
		
		low = 0x0030;
		low--;
		charRanges[++j] = low;
		//high = 0x0039;
		//high++;
		//charRanges[++j] = high;  merge with 0x003a
		
		single = 0x00B7;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x0300;
		low--;
		charRanges[++j] = low;
		high = 0x036F;
		high++;
		charRanges[++j] = high;
		
		low = 0x203F;
		low--;
		charRanges[++j] = low;
		high = 0x2040;
		high++;
		charRanges[++j] = high;
		
		Arrays.sort(startCharRanges);
		Arrays.sort(charRanges);
		
	}
}