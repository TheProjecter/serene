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

class XML10CharRanges{		
		
	static final int[] startCharRanges; 
	static final int[] charRanges;
	
	static{
		startCharRanges = new int[414];
		charRanges = new int[592];
		int i = -1;
		int j = -1;
		
		
		//BaseChar
		int low = 0x0041;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		int high = 0x005A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
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
		high = 0x00FF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0100;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0131;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0134;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x013E;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0141;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0148;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x014A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x017E;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0180;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x01C3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x01CD;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x01F0;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x01F4;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x01F5;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x01FA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0217;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0250;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x02A8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x02BB;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x02C1;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		int single = 0x0386;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0387
		
		low = 0x0388;
		low--;
		startCharRanges[++i] = low; // charRanges[++j] = low; merge with 0x0387
		high = 0x038A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x038C;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x038E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x03A1;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x03A3;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x03CE;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x03D0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x03D6;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x03DA;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x03DC;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x03DE;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x03E0;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x03E2;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x03F3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0401;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x040C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x040E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x044F;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0451;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x045C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x045E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0481;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0490;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04C4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x04C7;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04C8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x04CB;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04CC;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x04D0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04EB;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x04EE;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04F5;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x04F8;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x04F9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0531;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0556;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0559;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0561;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0586;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x05D0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x05EA;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x05F0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x05F2;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0621;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x063A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0641;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merge with 0x0640
		high = 0x064A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0671;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merged with 0x670  
		high = 0x06B7;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x06BA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x06BE;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x06C0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x06CE;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x06D0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x06D3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x06D5;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merged with 0x06d6 - 0x06dc
		
		low = 0x06E5;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x06E6;
		high++;
		startCharRanges[++i] = high; //charRanges[++j] = high; merged with 0x06e7 - 0x06e8
		
		low = 0x0905;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0939;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x093D;
		single--;
		startCharRanges[++i] = single;// charRanges[++j] = single; merged with 0x093c low and 0x093e - x094c high
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merged with 0x093c low and 0x093e - x094c high
		
		low = 0x0958;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0961;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0985;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x098C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x098F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0990;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0993;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09A8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x09AA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09B0;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x09B2;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x09B6;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09B9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x09DC;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09DD;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x09DF;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09E1;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x09F0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x09F1;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A05;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A0A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A0F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A10;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A13;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A28;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A2A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A30;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A32;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A33;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A35;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A36;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A38;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A39;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A59;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A5C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0A5E;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0A72;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merged with 0x0a70 - 0x0a71
		high = 0x0A74;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A85;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A8B;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0A8D;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0A8F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0A91;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0A93;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0AA8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0AAA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0AB0;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0AB2;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0AB3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0AB5;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0AB9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0ABD;
		single--;
		startCharRanges[++i] = single; //charRanges[++j] = single; merged with 0x0abc
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merged with 0x0abe - 0x0ac5
		
		single = 0x0AE0;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0B05;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B0C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B0F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B10;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B13;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B28;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B2A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B30;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B32;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B33;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B36;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B39;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0B3D;
		single--;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0b3c
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0b3e - x0b43
		
		low = 0x0B5C;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B5D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B5F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B61;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B85;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B8A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B8E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B90;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B92;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B95;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0B99;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B9A;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0B9C;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0B9E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0B9F;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0BA3;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0BA4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0BA8;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0BAA;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0BAE;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0BB5;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0BB7;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0BB9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C05;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C0C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C0E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C10;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C12;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C28;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C2A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C33;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C35;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C39;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C60;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C61;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C85;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C8C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C8E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0C90;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0C92;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0CA8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0CAA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0CB3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0CB5;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0CB9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0CDE;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0CE0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0CE1;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0D05;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0D0C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0D0E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0D10;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0D12;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0D28;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0D2A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0D39;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0D60;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0D61;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0E01;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E2E;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0E30;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0e31
		
		low = 0x0E32;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merge with 0x0e31
		high = 0x0E33;
		high++;
		startCharRanges[++i] = high; //charRanges[++j] = high; merge with 0x0e34 - 0x0e3a
		
		low = 0x0E40;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E45;
		high++;
		startCharRanges[++i] = high; //charRanges[++j] = high; merge with 0x0e46
		
		low = 0x0E81;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E82;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0E84;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0E87;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E88;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0E8A;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x0E8D;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0E94;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E97;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0E99;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0E9F;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0EA1;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0EA3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0EA5;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x0EA7;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0EAA;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0EAB;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0EAD;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0EAE;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x0EB0;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0eb1
		
		low = 0x0EB2;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merge with 0x0eb1
		high = 0x0EB3;
		high++;
		startCharRanges[++i] = high; //charRanges[++j] = high; merge with 0x0eb4- 0x0eb9
		
		single = 0x0EBD;
		single--;
		startCharRanges[++i] = single; //charRanges[++j] = single; merge with 0x0ebb - 0x0ebc
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x0EC0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0EC4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0F40;
		low--;
		startCharRanges[++i] = low; //charRanges[++j] = low; merge with 0x0f3f
		high = 0x0F47;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x0F49;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x0F69;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x10A0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x10C5;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x10D0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x10F6;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1100;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x1102;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1103;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1105;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1107;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1109;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x110B;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x110C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x110E;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1112;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x113C;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x113E;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1140;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x114C;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x114E;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1150;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x1154;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1155;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1159;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x115F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1161;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1163;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1165;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1167;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1169;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x116D;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x116E;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1172;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1173;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1175;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x119E;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x11A8;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x11AB;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x11AE;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x11AF;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x11B7;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x11B8;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x11BA;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x11BC;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x11C2;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x11EB;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x11F0;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x11F9;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x1E00;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1E9B;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1EA0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1EF9;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F00;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F15;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F18;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F1D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F20;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F45;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F48;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F4D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F50;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F57;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1F59;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1F5B;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		single = 0x1F5D;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x1F5F;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1F7D;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1F80;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FB4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FB6;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FBC;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x1FBE;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x1FC2;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FC4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FC6;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FCC;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FD0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FD3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FD6;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FDB;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FE0;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FEC;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FF2;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FF4;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x1FF6;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x1FFC;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x2126;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x212A;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x212B;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x212E;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x2180;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x2182;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x3041;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x3094;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x30A1;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x30FA;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0x3105;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x312C;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		low = 0xAC00;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0xD7A3;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		//end BaseChar
		
		//Ideografic
		low = 0x4E00;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x9FA5;			
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		
		single = 0x3007;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		
		low = 0x3021;
		low--;
		startCharRanges[++i] = low; charRanges[++j] = low;
		high = 0x3029;
		high++;
		startCharRanges[++i] = high; charRanges[++j] = high;
		//end Ideografic
		
		//'_'
		single = 0x5f;
		single--;
		startCharRanges[++i] = single; charRanges[++j] = single;
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		//end '_'
		
		//':'
		single = 0x3a;
		single--;
		startCharRanges[++i] = single; //charRanges[++j] = single;  merged with 0x0030 - 0x0039 
		single++;
		single++;
		startCharRanges[++i] = single; charRanges[++j] = single;
		//end ':'
		
		
		//CombiningChar
		low = 0x0300;
		low--;
		charRanges[++j] = low;
		high = 0x0345;
		high++;
		charRanges[++j] = high;
		
		low = 0x0360;
		low--;
		charRanges[++j] = low;
		high = 0x0361;
		high++;
		charRanges[++j] = high;
		
		low = 0x0483;
		low--;
		charRanges[++j] = low;
		high = 0x0486;
		high++;
		charRanges[++j] = high;
		
		low = 0x0591;
		low--;
		charRanges[++j] = low;
		high = 0x05A1;
		high++;
		charRanges[++j] = high;
		
		low = 0x05A3;
		low--;
		charRanges[++j] = low;
		high = 0x05B9;
		high++;
		charRanges[++j] = high;
		
		low = 0x05BB;
		low--;
		charRanges[++j] = low;
		high = 0x05BD;
		high++;
		charRanges[++j] = high;
		
		single = 0x05BF;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x05C1;
		low--;
		charRanges[++j] = low;
		high = 0x05C2;
		high++;
		charRanges[++j] = high;
		
		single = 0x05C4;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x064B;
		low--;
		charRanges[++j] = low;
		high = 0x0652;
		high++;
		charRanges[++j] = high;
		
		single = 0x0670;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merged with 0x671 - 0x0b
		
		//low = 0x06D6;
		//low--;
		//charRanges[++j] = low; merged with 0x06d5
		high = 0x06DC;
		high++;
		charRanges[++j] = high;
		
		low = 0x06DD;
		low--;
		charRanges[++j] = low;
		high = 0x06DF;
		high++;
		charRanges[++j] = high;
		
		low = 0x06E0;
		low--;
		charRanges[++j] = low;
		high = 0x06E4;
		high++;
		charRanges[++j] = high;
		
		//low = 0x06E7;
		//low--;
		//charRanges[++j] = low; merged with 0x06e5 - 0x06e6
		high = 0x06E8;
		high++;
		charRanges[++j] = high;
		
		low = 0x06EA;
		low--;
		charRanges[++j] = low;
		high = 0x06ED;
		high++;
		charRanges[++j] = high;
		
		low = 0x0901;
		low--;
		charRanges[++j] = low;
		high = 0x0903;
		high++;
		charRanges[++j] = high;
		
		single = 0x093C;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merged with 0x093d
		
		//low = 0x093E;
		//low--;
		//charRanges[++j] = low; merged with 0x093d
		//high = 0x094C;
		//high++;
		//charRanges[++j] = high;merged with 0x094d
		
		single = 0x094D;
		//single--;
		//charRanges[++j] = single;
		//single++; merged with 0x093e - 0x094c
		single++;
		charRanges[++j] = single;
		
		low = 0x0951;
		low--;
		charRanges[++j] = low;
		high = 0x0954;
		high++;
		charRanges[++j] = high;
		
		low = 0x0962;
		low--;
		charRanges[++j] = low;
		high = 0x0963;
		high++;
		charRanges[++j] = high;
		
		low = 0x0981;
		low--;
		charRanges[++j] = low;
		high = 0x0983;
		high++;
		charRanges[++j] = high;
		
		single = 0x09BC;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x09BE;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merged with 0x09bf
		
		//single = 0x09BF;
		//single--;
		//charRanges[++j] = single;
		//single++; 				merged with 0x09be
		//single++;
		//charRanges[++j] = single; merged with 0x09c0 - 0x09c4
		
		//low = 0x09C0;
		//low--;
		//charRanges[++j] = low; merged with 0x09bf
		high = 0x09C4;
		high++;
		charRanges[++j] = high;
		
		low = 0x09C7;
		low--;
		charRanges[++j] = low;
		high = 0x09C8;
		high++;
		charRanges[++j] = high;
		
		low = 0x09CB;
		low--;
		charRanges[++j] = low;
		high = 0x09CD;
		high++;
		charRanges[++j] = high;
		
		single = 0x09D7;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x09E2;
		low--;
		charRanges[++j] = low;
		high = 0x09E3;
		high++;
		charRanges[++j] = high;
		
		single = 0x0A02;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x0A3C;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x0A3E;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merged with 0x0a3f
		
		//single = 0x0A3F;
		//single--;
		//charRanges[++j] = single;
		//single++; 				merged with 0x0a3e
		//single++;
		//charRanges[++j] = single; merged with 0x0a40 - 0x0a42
		
		//low = 0x0A40;
		//low--;
		//charRanges[++j] = low; merged with 0x0a3f
		high = 0x0A42;
		high++;
		charRanges[++j] = high;
		
		low = 0x0A47;
		low--;
		charRanges[++j] = low;
		high = 0x0A48;
		high++;
		charRanges[++j] = high;
		
		low = 0x0A4B;
		low--;
		charRanges[++j] = low;
		high = 0x0A4D;
		high++;
		charRanges[++j] = high;
		
		//low = 0x0A70;
		//low--;
		//charRanges[++j] = low; lerged with 0x0a66 - 0x0a6f
		//high = 0x0A71;
		//high++;
		//charRanges[++j] = high; merged with 0x0a72 - 0x0a74
		
		low = 0x0A81;
		low--;
		charRanges[++j] = low;
		high = 0x0A83;
		high++;
		charRanges[++j] = high;
		
		single = 0x0ABC;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merged with 0x0abd
		
		//low = 0x0ABE;
		//low--;
		//charRanges[++j] = low; merged with 0x0abd
		high = 0x0AC5;
		high++;
		charRanges[++j] = high;
		
		low = 0x0AC7;
		low--;
		charRanges[++j] = low;
		high = 0x0AC9;
		high++;
		charRanges[++j] = high;
		
		low = 0x0ACB;
		low--;
		charRanges[++j] = low;
		high = 0x0ACD;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B01;
		low--;
		charRanges[++j] = low;
		high = 0x0B03;
		high++;
		charRanges[++j] = high;
		
		single = 0x0B3C;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x0b3d
		
		//low = 0x0B3E;
		//low--;
		//charRanges[++j] = low; merge with 0x0b3d
		high = 0x0B43;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B47;
		low--;
		charRanges[++j] = low;
		high = 0x0B48;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B4B;
		low--;
		charRanges[++j] = low;
		high = 0x0B4D;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B56;
		low--;
		charRanges[++j] = low;
		high = 0x0B57;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B82;
		low--;
		charRanges[++j] = low;
		high = 0x0B83;
		high++;
		charRanges[++j] = high;
		
		low = 0x0BBE;
		low--;
		charRanges[++j] = low;
		high = 0x0BC2;
		high++;
		charRanges[++j] = high;
		
		low = 0x0BC6;
		low--;
		charRanges[++j] = low;
		high = 0x0BC8;
		high++;
		charRanges[++j] = high;
		
		low = 0x0BCA;
		low--;
		charRanges[++j] = low;
		high = 0x0BCD;
		high++;
		charRanges[++j] = high;
		
		single = 0x0BD7;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x0C01;
		low--;
		charRanges[++j] = low;
		high = 0x0C03;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C3E;
		low--;
		charRanges[++j] = low;
		high = 0x0C44;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C46;
		low--;
		charRanges[++j] = low;
		high = 0x0C48;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C4A;
		low--;
		charRanges[++j] = low;
		high = 0x0C4D;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C55;
		low--;
		charRanges[++j] = low;
		high = 0x0C56;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C82;
		low--;
		charRanges[++j] = low;
		high = 0x0C83;
		high++;
		charRanges[++j] = high;
		
		low = 0x0CBE;
		low--;
		charRanges[++j] = low;
		high = 0x0CC4;
		high++;
		charRanges[++j] = high;
		
		low = 0x0CC6;
		low--;
		charRanges[++j] = low;
		high = 0x0CC8;
		high++;
		charRanges[++j] = high;
		
		low = 0x0CCA;
		low--;
		charRanges[++j] = low;
		high = 0x0CCD;
		high++;
		charRanges[++j] = high;
		
		low = 0x0CD5;
		low--;
		charRanges[++j] = low;
		high = 0x0CD6;
		high++;
		charRanges[++j] = high;
		
		low = 0x0D02;
		low--;
		charRanges[++j] = low;
		high = 0x0D03;
		high++;
		charRanges[++j] = high;
		
		low = 0x0D3E;
		low--;
		charRanges[++j] = low;
		high = 0x0D43;
		high++;
		charRanges[++j] = high;
		
		low = 0x0D46;
		low--;
		charRanges[++j] = low;
		high = 0x0D48;
		high++;
		charRanges[++j] = high;
		
		low = 0x0D4A;
		low--;
		charRanges[++j] = low;
		high = 0x0D4D;
		high++;
		charRanges[++j] = high;
		
		single = 0x0D57;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		//single = 0x0E31;
		//single--;
		//charRanges[++j] = single;
		//single++;  					merge with 0x0e30
		//single++;
		//charRanges[++j] = single;  merge with 0x0e32 - 0x0e33
		
		//low = 0x0E34;
		//low--;
		//charRanges[++j] = low; merge with 0x0e32 - 0x0e33
		high = 0x0E3A;
		high++;
		charRanges[++j] = high;
		
		//low = 0x0E47;
		//low--;
		//charRanges[++j] = low; merge with 0x0e46
		high = 0x0E4E;
		high++;
		charRanges[++j] = high;
		
		//single = 0x0EB1;
		//single--;
		//charRanges[++j] = single;  merge with 0x0eb0
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x0eb2 - 0x0eb3 
		
		//low = 0x0EB4;
		//low--;
		//charRanges[++j] = low; merge with 0x0eb2 - 0x0eb3
		high = 0x0EB9;
		high++;
		charRanges[++j] = high;
		
		low = 0x0EBB;
		low--;
		charRanges[++j] = low;
		//high = 0x0EBC;
		//high++;
		//charRanges[++j] = high; merge with 0x0ebd
		
		low = 0x0EC8;
		low--;
		charRanges[++j] = low;
		high = 0x0ECD;
		high++;
		charRanges[++j] = high;
		
		low = 0x0F18;
		low--;
		charRanges[++j] = low;
		high = 0x0F19;
		high++;
		charRanges[++j] = high;
		
		single = 0x0F35;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x0F37;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x0F39;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x0F3E;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x0f3f
		
		//single = 0x0F3F;
		//single--;
		//charRanges[++j] = single;
		//single++;						merge with 0x0f3e
		//single++;
		//charRanges[++j] = single;     merge with 0x0f40 - 0x0f47
		
		low = 0x0F71;
		low--;
		charRanges[++j] = low;
		high = 0x0F84;
		high++;
		charRanges[++j] = high;
		
		low = 0x0F86;
		low--;
		charRanges[++j] = low;
		high = 0x0F8B;
		high++;
		charRanges[++j] = high;
		
		low = 0x0F90;
		low--;
		charRanges[++j] = low;
		high = 0x0F95;
		high++;
		charRanges[++j] = high;
		
		single = 0x0F97;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x0F99;
		low--;
		charRanges[++j] = low;
		high = 0x0FAD;
		high++;
		charRanges[++j] = high;
		
		low = 0x0FB1;
		low--;
		charRanges[++j] = low;
		high = 0x0FB7;
		high++;
		charRanges[++j] = high;
		
		single = 0x0FB9;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x20D0;
		low--;
		charRanges[++j] = low;
		high = 0x20DC;
		high++;
		charRanges[++j] = high;
		
		single = 0x20E1;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x302A;
		low--;
		charRanges[++j] = low;
		high = 0x302F;
		high++;
		charRanges[++j] = high;
		
		single = 0x3099;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x0309a
		
		single = 0x309A;
		//single--;
		//charRanges[++j] = single;
		//single++;					mergz with 0x03099
		single++;
		charRanges[++j] = single;
		//end CombiningChar
		
		//Digit
		low = 0x0030;
		low--;
		charRanges[++j] = low;
		//high = 0x0039;
		//high++;
		//charRanges[++j] = high;//this range has been merged with ':'
		
		low = 0x0660;
		low--;
		charRanges[++j] = low;
		high = 0x0669;
		high++;
		charRanges[++j] = high;
		
		low = 0x06F0;
		low--;
		charRanges[++j] = low;
		high = 0x06F9;
		high++;
		charRanges[++j] = high;
		
		low = 0x0966;
		low--;
		charRanges[++j] = low;
		high = 0x096F;
		high++;
		charRanges[++j] = high;
		
		low = 0x09E6;
		low--;
		charRanges[++j] = low;
		high = 0x09EF;
		high++;
		charRanges[++j] = high;
		
		low = 0x0A66;
		low--;
		charRanges[++j] = low;
		//high = 0x0A6F;
		//high++;
		//charRanges[++j] = high; merged with 0x0a70 - 0x0a71
		
		low = 0x0AE6;
		low--;
		charRanges[++j] = low;
		high = 0x0AEF;
		high++;
		charRanges[++j] = high;
		
		low = 0x0B66;
		low--;
		charRanges[++j] = low;
		high = 0x0B6F;
		high++;
		charRanges[++j] = high;
		
		low = 0x0BE7;
		low--;
		charRanges[++j] = low;
		high = 0x0BEF;
		high++;
		charRanges[++j] = high;
		
		low = 0x0C66;
		low--;
		charRanges[++j] = low;
		high = 0x0C6F;
		high++;
		charRanges[++j] = high;
		
		low = 0x0CE6;
		low--;
		charRanges[++j] = low;
		high = 0x0CEF;
		high++;
		charRanges[++j] = high;
		
		low = 0x0D66;
		low--;
		charRanges[++j] = low;
		high = 0x0D6F;
		high++;
		charRanges[++j] = high;
		
		low = 0x0E50;
		low--;
		charRanges[++j] = low;
		high = 0x0E59;
		high++;
		charRanges[++j] = high;
		
		low = 0x0ED0;
		low--;
		charRanges[++j] = low;
		high = 0x0ED9;
		high++;
		charRanges[++j] = high;
		
		low = 0x0F20;
		low--;
		charRanges[++j] = low;
		high = 0x0F29;
		high++;
		charRanges[++j] = high;
		//end Digit
		
		
		//Extender
		single = 0x00B7;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x02D0;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x02d1
		
		single = 0x02D1;
		//single--;
		//charRanges[++j] = single;
		//single++;     				merge with 0x02d0
		single++;
		charRanges[++j] = single;  
		
		//single = 0x0387;
		//single--;
		//charRanges[++j] = single;
		//single++;						merge with  0x0386 
		//single++;
		//charRanges[++j] = single;  merge with 0x0388 - 0x038a
		
		single = 0x0640;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x0641 - 0x064a
		
		//single = 0x0E46;
		//single--;
		//charRanges[++j] = single;
		//single++;						merge with 0x0e40 - 0x0e45
		//single++;
		//charRanges[++j] = single; merge with 0x0e47 - 0x0e4e
		
		single = 0x0EC6;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		single = 0x3005;
		single--;
		charRanges[++j] = single;
		single++;
		single++;
		charRanges[++j] = single;
		
		low = 0x3031;
		low--;
		charRanges[++j] = low;
		high = 0x3035;
		high++;
		charRanges[++j] = high;
		
		low = 0x309D;
		low--;
		charRanges[++j] = low;
		high = 0x309E;
		high++;
		charRanges[++j] = high;
		
		low = 0x30FC;
		low--;
		charRanges[++j] = low;
		high = 0x30FE;
		high++;
		charRanges[++j] = high;
		//end Extender
		
		//'-'
		single = 0x002D;
		single--;
		charRanges[++j] = single;
		//single++;
		//single++;
		//charRanges[++j] = single; merge with 0x002e
		//end '-'
		
		//'.'
		single = 0x002E;
		//single--;
		//charRanges[++j] = single;
		//single++;						merge with 0x002d
		single++;
		charRanges[++j] = single;
		//end '.'
		
		
		Arrays.sort(startCharRanges);
		Arrays.sort(charRanges);
		
		
	
	
	}
}