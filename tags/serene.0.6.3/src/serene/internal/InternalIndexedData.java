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


package serene.internal;

import java.util.Map;

import serene.bind.util.DocumentIndexedData;

class InternalIndexedData implements DocumentIndexedData{

    //int[] itemIdTable;
    int[] itemDescriptionIndexTable;
    //int[] localNameIndexTable;
    //int[] namespaceURIIndexTable;    
    //int[] attributeTypeIndexTable;
    //int[] valueIndexTable;    
    int[] systemIdIndexTable;
    //int[] publicIdIndexTable; 	
	//int[] lineNumberTable;
	//int[] columnNumberTable;
	//Map<String, String>[] declaredXmlns;
	
	String[] itemDescriptionTable;	
	//String[] localNameTable;
	//String[] namespaceURITable;
	//String[] attributeTypeTable;
	//String[] valueTable;
	String[] systemIdTable;
	//String[] publicIdTable;
	
	
	//int initialSize;
    
    //item record index
    static final int REF_PATTERN = 0;
    static final int DEFINE_PATTERN = 1;
    
    //<element>nameClass pattern+</element>
    //**********************************************************************
    static final int ELEMENT_NC_ELEMENT = 2;
    static final int ELEMENT_NC_ELEMENT_NAME = 3;
    static final int ELEMENT_NC_ELEMENT_CONTENT = 4;    
    static final int ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 5;
    static final int ELEMENT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE = 6;
    static final int ELEMENT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE = 7;
    static final int ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS = 8;
    static final int ELEMENT_NC_ELEMENT_CONTENT_PATTERN = 9;
    static final int ELEMENT_NC_ELEMENT_CONTENT_PATTERN_PLUS = 10;
    static final int ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP = 11;
    static final int ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 12;
    static final int ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 13;
    static final int ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 14; 
    
    //<element name="QName">pattern+</element>
    //**********************************************************************
    static final int ELEMENT_NI_ELEMENT = 15;
    static final int ELEMENT_NI_ELEMENT_NAME = 16;
    static final int ELEMENT_NI_ELEMENT_CONTENT = 17;    
    static final int ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 18;
    static final int ELEMENT_NI_ELEMENT_CONTENT_NS_ATTRIBUTE = 19;
    static final int ELEMENT_NI_ELEMENT_CONTENT_DL_ATTRIBUTE = 20;
    static final int ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE = 21;
    static final int ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 22;
    static final int ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 23;
    static final int ELEMENT_NI_ELEMENT_CONTENT_PATTERN = 24;
    static final int ELEMENT_NI_ELEMENT_CONTENT_PATTERN_PLUS = 25;
    static final int ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT = 26;
    static final int ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 27;
    static final int ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 28; 
    
    //<attribute>nameClass pattern+</attribute>
    //**********************************************************************
    static final int ATTRIBUTE_NC_ELEMENT = 29;
    static final int ATTRIBUTE_NC_ELEMENT_NAME = 30;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT = 31;    
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 32;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE = 33;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE = 34;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS = 35;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN = 36;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN_SQUARE = 37;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP = 38;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 39;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 40;
    static final int ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 41; 
    
    //<attribute name="QName">pattern+</attribute>
    //**********************************************************************
    static final int ATTRIBUTE_NI_ELEMENT = 42;
    static final int ATTRIBUTE_NI_ELEMENT_NAME = 43;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT = 44;    
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 45;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_NS_ATTRIBUTE = 46;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_DL_ATTRIBUTE = 47;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE = 48;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 49;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 50;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN = 51;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN_SQUARE = 52;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT = 53;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 54;
    static final int ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 55; 
    
    //<group>pattern+</group>
    //**********************************************************************
    static final int GROUP_ELEMENT = 56;
    static final int GROUP_ELEMENT_NAME = 57;
    static final int GROUP_ELEMENT_CONTENT = 58;
    static final int GROUP_ELEMENT_CONTENT_PATTERN = 59;
    static final int GROUP_ELEMENT_CONTENT_PATTERN_PLUS = 60;
    static final int GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT = 61;
    static final int GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 62;
    static final int GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 63; 
    static final int GROUP_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 64;
    static final int GROUP_ELEMENT_CONTENT_NS_ATTRIBUTE = 65;
    static final int GROUP_ELEMENT_CONTENT_DL_ATTRIBUTE = 66;
    
    //<interleave>pattern+</interleave>
    //**********************************************************************
    static final int INTERLEAVE_ELEMENT = 67;
    static final int INTERLEAVE_ELEMENT_NAME = 68;
    static final int INTERLEAVE_ELEMENT_CONTENT = 69;
    static final int INTERLEAVE_ELEMENT_CONTENT_PATTERN = 70;
    static final int INTERLEAVE_ELEMENT_CONTENT_PATTERN_PLUS = 71;
    static final int INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT = 72;
    static final int INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 73;
    static final int INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 74; 
    static final int INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 75;
    static final int INTERLEAVE_ELEMENT_CONTENT_NS_ATTRIBUTE = 76;
    static final int INTERLEAVE_ELEMENT_CONTENT_DL_ATTRIBUTE = 77;
        
    //<choice>pattern+</choice>
    //**********************************************************************
    static final int CHOICE_PATTERN_ELEMENT = 78;
    static final int CHOICE_PATTERN_ELEMENT_NAME = 79;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT = 80;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN = 81;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS = 82;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT = 83;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 84;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 85; 
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 86;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE = 87;
    static final int CHOICE_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE = 88;
    
    //<optional>pattern+</optional>
    //**********************************************************************
    static final int OPTIONAL_ELEMENT = 89;
    static final int OPTIONAL_ELEMENT_NAME = 90;
    static final int OPTIONAL_ELEMENT_CONTENT = 91;
    static final int OPTIONAL_ELEMENT_CONTENT_PATTERN = 92;
    static final int OPTIONAL_ELEMENT_CONTENT_PATTERN_PLUS = 93;
    static final int OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT = 94;
    static final int OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 95;
    static final int OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 96; 
    static final int OPTIONAL_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 97;
    static final int OPTIONAL_ELEMENT_CONTENT_NS_ATTRIBUTE = 98;
    static final int OPTIONAL_ELEMENT_CONTENT_DL_ATTRIBUTE = 99;
    
    //<zeroOrMore>pattern+</zeroOrMore>
    //**********************************************************************
    static final int ZERO_OR_MORE_ELEMENT = 100;
    static final int ZERO_OR_MORE_ELEMENT_NAME = 101;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT = 102;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN = 103;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS = 104;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT = 105;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 106;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 107; 
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 108;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE = 109;
    static final int ZERO_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE = 110;
    
    //<oneOrMore>pattern+</oneOrMore>
    //**********************************************************************
    static final int ONE_OR_MORE_ELEMENT = 111;
    static final int ONE_OR_MORE_ELEMENT_NAME = 112;
    static final int ONE_OR_MORE_ELEMENT_CONTENT = 113;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_PATTERN = 114;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS = 115;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT = 116;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 117;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 118; 
    static final int ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 119;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE = 120;
    static final int ONE_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE = 121;
    
    //<list>pattern+</list>
    //**********************************************************************
    static final int LIST_ELEMENT = 122;
    static final int LIST_ELEMENT_NAME = 123;
    static final int LIST_ELEMENT_CONTENT = 124;
    static final int LIST_ELEMENT_CONTENT_PATTERN = 125;
    static final int LIST_ELEMENT_CONTENT_PATTERN_PLUS = 126;
    static final int LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT = 127;
    static final int LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 128;
    static final int LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 129; 
    static final int LIST_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 130;
    static final int LIST_ELEMENT_CONTENT_NS_ATTRIBUTE = 131;
    static final int LIST_ELEMENT_CONTENT_DL_ATTRIBUTE = 132;
    
    //<mixed>pattern+</mixed>
    //**********************************************************************
    static final int MIXED_ELEMENT = 133;
    static final int MIXED_ELEMENT_NAME = 134;
    static final int MIXED_ELEMENT_CONTENT = 135;
    static final int MIXED_ELEMENT_CONTENT_PATTERN = 136;
    static final int MIXED_ELEMENT_CONTENT_PATTERN_PLUS = 137;
    static final int MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT = 138;
    static final int MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 139;
    static final int MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 140; 
    static final int MIXED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 141;
    static final int MIXED_ELEMENT_CONTENT_NS_ATTRIBUTE = 142;
    static final int MIXED_ELEMENT_CONTENT_DL_ATTRIBUTE = 143;
    
    
    //<ref name="NCName"/>
    //**********************************************************************
    static final int REF_ELEMENT = 144;
    static final int REF_ELEMENT_NAME = 145;
    static final int REF_ELEMENT_CONTENT = 146;
    static final int REF_ELEMENT_CONTENT_NAME_ATTRIBUTE = 147;
    static final int REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 148;
    static final int REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 149;
    static final int REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 150;
    static final int REF_ELEMENT_CONTENT_NS_ATTRIBUTE = 151;
    static final int REF_ELEMENT_CONTENT_DL_ATTRIBUTE = 152;
    static final int REF_ELEMENT_CONTENT_FOREIGN_ELEMENT = 153;
    static final int REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 154;
    
        
    //<parentRef name="NCName"/>
    //**********************************************************************
    static final int PARENT_REF_ELEMENT = 155;
    static final int PARENT_REF_ELEMENT_NAME = 156;
    static final int PARENT_REF_ELEMENT_CONTENT = 157;
    static final int PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE = 158;
    static final int PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 159;
    static final int PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 160;
    static final int PARENT_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 161;
    static final int PARENT_REF_ELEMENT_CONTENT_NS_ATTRIBUTE = 162;
    static final int PARENT_REF_ELEMENT_CONTENT_DL_ATTRIBUTE = 163;
    static final int PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT = 164;
    static final int PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 165;
    
    
    //<empty/>
    //**********************************************************************
    static final int EMPTY_ELEMENT = 166;
    static final int EMPTY_ELEMENT_NAME = 167;
    static final int EMPTY_ELEMENT_CONTENT = 168;
    static final int EMPTY_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 169;
    static final int EMPTY_ELEMENT_CONTENT_NS_ATTRIBUTE = 170;
    static final int EMPTY_ELEMENT_CONTENT_DL_ATTRIBUTE = 171;
    static final int EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT = 172;
    static final int EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 173;
    
    
    //<text/>
    //**********************************************************************
    static final int TEXT_ELEMENT = 174;
    static final int TEXT_ELEMENT_NAME = 175;
    static final int TEXT_ELEMENT_CONTENT = 176;
    static final int TEXT_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 177;
    static final int TEXT_ELEMENT_CONTENT_NS_ATTRIBUTE = 178;
    static final int TEXT_ELEMENT_CONTENT_DL_ATTRIBUTE = 179;
    static final int TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT = 180;
    static final int TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 181;
      
    
    //<value [type="NCName"]>string</value>
    //**********************************************************************
    static final int VALUE_ELEMENT = 182;
    static final int VALUE_ELEMENT_NAME = 183;
    static final int VALUE_ELEMENT_CONTENT = 184;
    static final int VALUE_ELEMENT_CONTENT_TEXT = 185;
    static final int VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME = 186;
    static final int VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE = 187;    
    static final int VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE = 188;
    static final int VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_SQUARE = 189;
    static final int VALUE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 190;
    static final int VALUE_ELEMENT_CONTENT_NS_ATTRIBUTE = 191;
    static final int VALUE_ELEMENT_CONTENT_DL_ATTRIBUTE = 192;
    
     
    //<data type="NCName">param* exceptPattern</data>
    //**********************************************************************
    static final int DATA_ELEMENT = 193;
    static final int DATA_ELEMENT_NAME = 194;
    static final int DATA_ELEMENT_CONTENT = 195;
    static final int DATA_ELEMENT_CONTENT_PARAM = 196;
    static final int DATA_ELEMENT_CONTENT_PARAM_STAR = 197;    
    static final int DATA_ELEMENT_CONTENT_EXCEPT_PATTERN = 198;
    static final int DATA_ELEMENT_CONTENT_EXCEPT_PATTERN_SQUARE = 199;
    static final int DATA_ELEMENT_CONTENT_PARAM_EXCEPT_PATTERN_GROUP = 200;
    static final int DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT = 201;
    static final int DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 202;
    static final int DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 203;
    static final int DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME = 204;
    static final int DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE = 205;
    static final int DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE = 206;
    static final int DATA_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 207;
    static final int DATA_ELEMENT_CONTENT_NS_ATTRIBUTE = 208;
    static final int DATA_ELEMENT_CONTENT_DL_ATTRIBUTE = 209;
    
    
    //<notAllowed/>
    //**********************************************************************
    static final int NOT_ALLOWED_ELEMENT = 210;
    static final int NOT_ALLOWED_ELEMENT_NAME = 211;
    static final int NOT_ALLOWED_ELEMENT_CONTENT = 212;
    static final int NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 213;
    static final int NOT_ALLOWED_ELEMENT_CONTENT_NS_ATTRIBUTE = 214;
    static final int NOT_ALLOWED_ELEMENT_CONTENT_DL_ATTRIBUTE = 215;
    static final int NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT = 216;
    static final int NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 217;
    
    
    //<externalRef href="anyURI"/>
    //**********************************************************************
    static final int EXTERNAL_REF_ELEMENT = 218;
    static final int EXTERNAL_REF_ELEMENT_NAME = 219;
    static final int EXTERNAL_REF_ELEMENT_CONTENT = 220;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE = 221;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE = 222;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME = 223;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 224;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_NS_ATTRIBUTE = 225;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_DL_ATTRIBUTE = 226;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT = 227;
    static final int EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 228;
    
    
    //<grammar>grammarContent*</grammar>
    //**********************************************************************
    static final int GRAMMAR_ELEMENT = 229;    
    static final int GRAMMAR_ELEMENT_NAME = 230;
    static final int GRAMMAR_ELEMENT_CONTENT = 231;
    static final int GRAMMAR_ELEMENT_CONTENT_GC = 232;
    static final int GRAMMAR_ELEMENT_CONTENT_GC_STAR = 233;
    static final int GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT = 234;
    static final int GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 235;
    static final int GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC = 236;
    static final int GRAMMAR_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 237;
    static final int GRAMMAR_ELEMENT_CONTENT_NS_ATTRIBUTE = 238;
    static final int GRAMMAR_ELEMENT_CONTENT_DL_ATTRIBUTE = 239;
    
    
    //<param name="NCName">string</param>
    //**********************************************************************
    static final int PARAM_ELEMENT = 240;
    static final int PARAM_ELEMENT_NAME = 241;
    static final int PARAM_ELEMENT_CONTENT = 242;
    static final int PARAM_ELEMENT_CONTENT_TEXT = 243;
    static final int PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 244;
    static final int PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 245;    
    static final int PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE = 246;
    static final int PARAM_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 247;
    static final int PARAM_ELEMENT_CONTENT_NS_ATTRIBUTE = 248;
    static final int PARAM_ELEMENT_CONTENT_DL_ATTRIBUTE = 249;
    
    
    //<except>pattern+</except>
    //**********************************************************************
    static final int EXCEPT_PATTERN_ELEMENT = 250;
    static final int EXCEPT_PATTERN_ELEMENT_NAME = 251;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT = 252;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN = 253;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS = 254;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT = 255;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 256;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 257; 
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 258;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE = 259;
    static final int EXCEPT_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE = 260;
    
    
    //grammarContent
    //**********************************************************************
    static final int DEFINE_GRAMMAR_CONTENT_START = 261;   
    static final int DEFINE_GRAMMAR_CONTENT_DEFINE = 262;
    static final int DEFINE_GRAMMAR_CONTENT = 263;
        
        
    //<div>grammarContent*</div>
    //**********************************************************************
    static final int DIV_GC_ELEMENT = 264;    
    static final int DIV_GC_ELEMENT_NAME = 265;
    static final int DIV_GC_ELEMENT_CONTENT = 266;
    static final int DIV_GC_ELEMENT_CONTENT_GC = 267;
    static final int DIV_GC_ELEMENT_CONTENT_GC_STAR = 268;
    static final int DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 269;
    static final int DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 270;
    static final int DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC = 271;
    static final int DIV_GC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 272;
    static final int DIV_GC_ELEMENT_CONTENT_NS_ATTRIBUTE = 273;
    static final int DIV_GC_ELEMENT_CONTENT_DL_ATTRIBUTE = 274;
    
    
    //<include href="anyURI"> includeContent </include>
    //**********************************************************************
    static final int INCLUDE_ELEMENT = 275;
    static final int INCLUDE_ELEMENT_NAME = 276;
    static final int INCLUDE_ELEMENT_CONTENT = 277;
    static final int INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE = 278;
    static final int INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE = 279;
    static final int INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME = 280;
    static final int INCLUDE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 281;
    static final int INCLUDE_ELEMENT_CONTENT_NS_ATTRIBUTE = 282;
    static final int INCLUDE_ELEMENT_CONTENT_DL_ATTRIBUTE = 283;
    static final int INCLUDE_ELEMENT_CONTENT_IC = 284;
    static final int INCLUDE_ELEMENT_CONTENT_IC_STAR = 285;
    static final int INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT = 286;
    static final int INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 287;
    static final int INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC = 288;
    
        
    //includeContent
    //**********************************************************************
    static final int DEFINE_INCLUDE_CONTENT_START = 289;   
    static final int DEFINE_INCLUDE_CONTENT_DEFINE = 290;
    static final int DEFINE_INCLUDE_CONTENT = 291;
    
    
    //<div>includeContent*</div>
    //**********************************************************************
    static final int DIV_IC_ELEMENT = 292;    
    static final int DIV_IC_ELEMENT_NAME = 293;
    static final int DIV_IC_ELEMENT_CONTENT = 294;
    static final int DIV_IC_ELEMENT_CONTENT_IC = 295;
    static final int DIV_IC_ELEMENT_CONTENT_IC_STAR = 296;
    static final int DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 297;
    static final int DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 298;
    static final int DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC = 299;
    static final int DIV_IC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 300;
    static final int DIV_IC_ELEMENT_CONTENT_NS_ATTRIBUTE = 301;
    static final int DIV_IC_ELEMENT_CONTENT_DL_ATTRIBUTE = 302;
    
    
    //<start name="NCName" [combine="method"]>pattern+</define>
    //**********************************************************************
    static final int START_ELEMENT = 303;
    static final int START_ELEMENT_NAME = 304;
    static final int START_ELEMENT_CONTENT = 305;
    static final int START_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 306;
    static final int START_ELEMENT_CONTENT_NS_ATTRIBUTE = 307;
    static final int START_ELEMENT_CONTENT_DL_ATTRIBUTE = 308;
    static final int START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE = 309;
    static final int START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE = 310;
    static final int START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME = 311;
    static final int START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE = 312;
    static final int START_ELEMENT_CONTENT_PATTERN = 313;
    static final int START_ELEMENT_CONTENT_FOREIGN_ELEMENT = 314;
    static final int START_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 315;
    static final int START_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 316;
        
    
    
    //<define combine="method">pattern</start>
    //**********************************************************************
    static final int DEFINE_ELEMENT = 317;
    static final int DEFINE_ELEMENT_NAME = 318;
    static final int DEFINE_ELEMENT_CONTENT = 319;
    static final int DEFINE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 320;
    static final int DEFINE_ELEMENT_CONTENT_NS_ATTRIBUTE = 321;
    static final int DEFINE_ELEMENT_CONTENT_DL_ATTRIBUTE = 322;
    
    static final int DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE = 323;
    static final int DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE = 324;
    static final int DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME = 325;
    
    static final int DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE = 326;
    static final int DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE = 327;
    static final int DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME = 328;
    static final int DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE = 329;
    
    static final int DEFINE_ELEMENT_CONTENT_PATTERN = 330;
    static final int DEFINE_ELEMENT_CONTENT_PATTERN_PLUS = 331;
    static final int DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT = 332;
    static final int DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 333;
    static final int DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE = 334;
        
    
    //nameClass
    //**********************************************************************
    static final int DEFINE_NAME_CLASS = 335;
    
    
    
    //<name>QName</name>
    //**********************************************************************
    static final int NAME_ELEMENT = 336;
    static final int NAME_ELEMENT_NAME = 337;
    static final int NAME_ELEMENT_CONTENT = 338;
    static final int NAME_ELEMENT_CONTENT_DATA = 339;
    static final int NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 340;
    static final int NAME_ELEMENT_CONTENT_NS_ATTRIBUTE = 341;
    static final int NAME_ELEMENT_CONTENT_DL_ATTRIBUTE = 342;
    
        
    //<anyName>[exceptPattern]</anyName>
    //**********************************************************************
    static final int ANY_NAME_ELEMENT = 343;
    static final int ANY_NAME_ELEMENT_NAME = 344;
    static final int ANY_NAME_ELEMENT_CONTENT = 345;
    static final int ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC = 346;
    static final int ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE = 347;
    static final int ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT = 348;
    static final int ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 349;
    static final int ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC = 350;
    static final int ANY_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 351;
    static final int ANY_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE = 352;
    static final int ANY_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE = 353;
    
    
    //<nsName>[exceptPattern]</nsName>
    //**********************************************************************
    static final int NS_NAME_ELEMENT = 354;
    static final int NS_NAME_ELEMENT_NAME = 355;
    static final int NS_NAME_ELEMENT_CONTENT = 356;
    static final int NS_NAME_ELEMENT_CONTENT_EXCEPT_NC = 357;
    static final int NS_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE = 358;
    static final int NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT = 359;
    static final int NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 360;
    static final int NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC = 361;
    static final int NS_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 362;
    static final int NS_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE = 363;
    static final int NS_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE = 364;
    
    
    //<choice>exceptNameClass+</choice>
    //**********************************************************************
    static final int CHOICE_NC_ELEMENT = 365;
    static final int CHOICE_NC_ELEMENT_NAME = 366;
    static final int CHOICE_NC_ELEMENT_CONTENT = 367;
    static final int CHOICE_NC_ELEMENT_CONTENT_NC = 368;
    static final int CHOICE_NC_ELEMENT_CONTENT_NC_PLUS = 369;
    static final int CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 370;
    static final int CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 371;
    static final int CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC = 372; 
    static final int CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 373;
    static final int CHOICE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE = 374;
    static final int CHOICE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE = 375;
    
    
    //<except>exceptNameClass+</except>
    //**********************************************************************
    static final int EXCEPT_NC_ELEMENT = 376;
    static final int EXCEPT_NC_ELEMENT_NAME = 377;
    static final int EXCEPT_NC_ELEMENT_CONTENT = 378;
    static final int EXCEPT_NC_ELEMENT_CONTENT_NC = 379;
    static final int EXCEPT_NC_ELEMENT_CONTENT_NC_PLUS = 380;
    static final int EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT = 381;
    static final int EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR = 382;
    static final int EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC = 383; 
    static final int EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES = 384;
    static final int EXCEPT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE = 385;
    static final int EXCEPT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE = 386;
    
      
    //foreignElement
    //**********************************************************************
    static final int FOREIGN_ELEMENT = 387;
    static final int FOREIGN_ELEMENT_ANY_NAME = 388;
    static final int FOREIGN_ELEMENT_ANY_NAME_EXCEPT = 389;
    static final int FOREIGN_ELEMENT_ANY_NAME_EXCEPT_NS = 390;
    static final int FOREIGN_ELEMENT_CONTENT_TEXT = 391;
    static final int FOREIGN_ELEMENT_CONTENT_ANY_ATTRIBUTE = 392;
    static final int FOREIGN_ELEMENT_CONTENT_ANY_ELEMENT = 393;
    static final int FOREIGN_ELEMENT_CONTENT_CHOICE = 394;
    static final int FOREIGN_ELEMENT_CONTENT_CHOICE_STAR = 395;
        
    
    //anyElement
    //**********************************************************************
    static final int ANY_ELEMENT = 396;
    static final int ANY_ELEMENT_NAME = 397;
    static final int ANY_ELEMENT_CONTENT_TEXT = 398;
    static final int ANY_ELEMENT_CONTENT_ANY_ATTRIBUTE = 399;
    static final int ANY_ELEMENT_CONTENT_ANY_ELEMENT = 400;
    static final int ANY_ELEMENT_CONTENT_CHOICE = 401;
    static final int ANY_ELEMENT_CONTENT_CHOICE_STAR = 402;
    
    
    //foreignAttribute
    //**********************************************************************
    static final int FOREIGN_ATTRIBUTE = 403;
    static final int FOREIGN_ATTRIBUTE_ANY_NAME = 404;
    static final int FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT = 405;
    static final int FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE = 406;
    static final int FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_RNG = 407;
    static final int FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_NULL = 408;
    static final int FOREIGN_ATTRIBUTE_VALUE = 409;
    static final int FOREIGN_ATTRIBUTE_STAR = 410;
    
    
    //anyAttribute
    //**********************************************************************
    static final int ANY_ATTRIBUTE = 411;
    static final int ANY_ATTRIBUTE_NAME = 412; 
    static final int ANY_ATTRIBUTE_VALUE = 413;
    
    
    //ns attribute
    //**********************************************************************
    static final int NS_ATTRIBUTE = 414;
    static final int NS_ATTRIBUTE_NAME = 415; 
    static final int NS_ATTRIBUTE_VALUE = 416;
    static final int NS_ATTRIBUTE_SQUARE = 417;
    
        
    //datatypeLibrary attribute
    //**********************************************************************
    static final int DL_ATTRIBUTE = 418;
    static final int DL_ATTRIBUTE_NAME = 419; 
    static final int DL_ATTRIBUTE_VALUE = 420;
    static final int DL_ATTRIBUTE_SQUARE = 421;
    
    static final int RECORDS_TOTAL_COUNT = 422;
    
    
    
    //item descriptions
    final int DESCRIPTION_PATTERN = 0;
    final int DESCRIPTION_CHOICE_OF_PATTERN_ELEMENTS = 1;
    final int DESCRIPTION_ELEMENT = 2;
    final int DESCRIPTION_NAME = 3;
    final int DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP = 4;
    final int DESCRIPTION_FOREIGN_ATTRIBUTES = 5;
    final int DESCRIPTION_NS_ATTRIBUTE = 6; 
    final int DESCRIPTION_DL_ATTRIBUTE = 7; 
    final int DESCRIPTION_NAME_CLASS = 8;
    final int DESCRIPTION_ONE_OR_MORE = 9;
    final int DESCRIPTION_ZERO_OR_MORE = 10;
    final int DESCRIPTION_FOREIGN_ELEMENT = 11;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS = 12;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS_NC = 13;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN = 14;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN_NC = 15;
    final int DESCRIPTION_NC_PATTERN_PLUS_GROUP = 16;
    final int DESCRIPTION_DATA = 17;    
    final int DESCRIPTION_OPTIONAL = 18;
    final int DESCRIPTION_NAME_ATTRIBUTE = 19;
    final int DESCRIPTION_TEXT = 20;
    final int DESCRIPTION_TYPE_ATTRIBUTE = 21;
    final int DESCRIPTION_PARAM = 22;
    final int DESCRIPTION_EXCEPT_PATTERN = 23;
    final int DESCRIPTION_PARAM_EXCEPT_PATTERN_GROUP = 24;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_DATA = 25;
    final int DESCRIPTION_HREF_ATTRIBUTE = 26;
    final int DESCRIPTION_GRAMMAR_CONTENT = 27;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC = 28;
    final int DESCRIPTION_START = 29;
    final int DESCRIPTION_DEFINE = 30;
    final int DESCRIPTION_CHOICE_OF_GRAMMAR_CONTENT_DEFINITIONS = 31;
    final int DESCRIPTION_INCLUDE_CONTENT = 32;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC = 33;
    final int DESCRIPTION_CHOICE_OF_INCLUDE_CONTENT_DEFINITIONS = 34;
    final int DESCRIPTION_COMBINE_ATTRIBUTE = 35;
    final int DESCRIPTION_CHOICE_OF_NAME_CLASS_ELEMENTS = 36;
    final int DESCRIPTION_ATTRIBUTES_AND_DATA_GROUP = 37;
    final int DESCRIPTION_EXCEPT_NC = 38; 
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT = 39;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_NC = 40;
    final int DESCRIPTION_ANY_NAME = 41;
    final int DESCRIPTION_NS_NAME = 42;
    final int DESCRIPTION_ANY_ATTRIBUTE = 43;
    final int DESCRIPTION_ANY_ELEMENT = 44;
    final int DESCRIPTION_CHOICE_OF_ANY = 45;
    final int DESCRIPTION_FOREIGN_ATTRIBUTE = 46;
    final int DESCRIPTION_CHOICE = 47;
    final int DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERN = 48;
    final int DESCRIPTION_NC_PATTERN_GROUP = 49;
    
    final int DESCRIPTION_TOTAL_COUNT = 50;
    
    //locations
    final int SPECIFICATION_PATTERN = 0;
    final int SPECIFICATION_ELEMENT_NC_ELEMENT = 1;
    final int SPECIFICATION_ELEMENT_NI_ELEMENT = 2;
    final int SPECIFICATION_ATTRIBUTE_NC_ELEMENT = 3;
    final int SPECIFICATION_ATTRIBUTE_NI_ELEMENT = 4;
    final int SPECIFICATION_GROUP_ELEMENT = 5;
    final int SPECIFICATION_INTERLEAVE_ELEMENT = 6;    
    final int SPECIFICATION_CHOICE_PATTERN_ELEMENT = 7;
    final int SPECIFICATION_OPTIONAL_ELEMENT = 8;
    final int SPECIFICATION_ZERO_OR_MORE_ELEMENT = 9;
    final int SPECIFICATION_ONE_OR_MORE_ELEMENT = 10;
    final int SPECIFICATION_LIST_ELEMENT = 11;
    final int SPECIFICATION_MIXED_ELEMENT = 12;   
    final int SPECIFICATION_REF_ELEMENT = 13;
    final int SPECIFICATION_PARENT_REF_ELEMENT = 14;
    final int SPECIFICATION_EMPTY_ELEMENT = 15;
    final int SPECIFICATION_TEXT_ELEMENT = 16;
    final int SPECIFICATION_VALUE_ELEMENT = 17;
    final int SPECIFICATION_DATA_ELEMENT = 18;    
    final int SPECIFICATION_NOT_ALLOWED_ELEMENT = 19;
    final int SPECIFICATION_EXTERNAL_REF_ELEMENT = 20;
    final int SPECIFICATION_GRAMMAR_ELEMENT = 21;
    final int SPECIFICATION_PARAM_ELEMENT = 22;
    final int SPECIFICATION_EXCEPT_PATTERN_ELEMENT = 23; 
    final int SPECIFICATION_DEFINE_GRAMMAR_CONTENT = 24;
    final int SPECIFICATION_DIV_GC_ELEMENT = 25;
    final int SPECIFICATION_INCLUDE_ELEMENT = 26;
    final int SPECIFICATION_DEFINE_INCLUDE_CONTENT = 27;
    final int SPECIFICATION_DIV_IC_ELEMENT = 28;
    final int SPECIFICATION_START_ELEMENT = 30;
    final int SPECIFICATION_DEFINE_ELEMENT = 31;
    final int SPECIFICATION_NAME_CLASS = 32;
    final int SPECIFICATION_NAME_ELEMENT = 33;
    final int SPECIFICATION_ANY_NAME_ELEMENT = 34;
    final int SPECIFICATION_NS_NAME_ELEMENT = 35;
    final int SPECIFICATION_CHOICE_NC_ELEMENT = 36;
    final int SPECIFICATION_EXCEPT_NC_ELEMENT = 37;
    final int SPECIFICATION_FOREIGN_ELEMENT = 38;
    final int SPECIFICATION_ANY_ELEMENT = 39;
    final int SPECIFICATION_FOREIGN_ATTRIBUTE = 40;
    final int SPECIFICATION_ANY_ATTRIBUTE = 41;
    final int SPECIFICATION_NS_ATTRIBUTE = 42;
    final int SPECIFICATION_DL_ATTRIBUTE = 43;
    
    
    final int SPECIFICATION_TOTAL_COUNT = 44;
    
    InternalIndexedData(){
        //itemIdTable = new int[initialSize];        
       	 
	    // itemIdTable = new int[RECORDS_TOTAL_COUNT];	   
        itemDescriptionIndexTable = new int[RECORDS_TOTAL_COUNT];
        // localNameIndexTable = new int[RECORDS_TOTAL_COUNT];
        // namespaceURIIndexTable = new int[RECORDS_TOTAL_COUNT];
        // attributeTypeIndexTable = new int[RECORDS_TOTAL_COUNT];
        // valueIndexTable = new int[RECORDS_TOTAL_COUNT];
        systemIdIndexTable = new int[RECORDS_TOTAL_COUNT];
        // publicIdIndexTable = new int[RECORDS_TOTAL_COUNT]; 	
        // lineNumberTable = new int[RECORDS_TOTAL_COUNT];
        // columnNumberTable = new int[RECORDS_TOTAL_COUNT];
        // declaredXmlns = new Map[RECORDS_TOTAL_COUNT];
        
        
        itemDescriptionTable = new String[DESCRIPTION_TOTAL_COUNT];
        // localNameTable = new String[5];
        // namespaceURITable = new String[5];
        // attributeTypeTable = new String[5];
        // valueTable = new String[5];
        systemIdTable = new String[SPECIFICATION_TOTAL_COUNT];
        // publicIdTable = new String[5];
        
        
        itemDescriptionTable = new String[DESCRIPTION_TOTAL_COUNT];
        systemIdTable = new String[SPECIFICATION_TOTAL_COUNT];
        
        //pattern
        //**********************************************************************
        itemDescriptionIndexTable[REF_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[REF_PATTERN] = SPECIFICATION_PATTERN;        
        
        itemDescriptionIndexTable[DEFINE_PATTERN] = DESCRIPTION_CHOICE_OF_PATTERN_ELEMENTS;
        systemIdIndexTable[DEFINE_PATTERN] = SPECIFICATION_PATTERN;
        //**********************************************************************
        
        
        //<element>nameClass pattern+</element>
        //**********************************************************************
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ELEMENT_NC_ELEMENT] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_NAME] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS] = DESCRIPTION_NAME_CLASS;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP] = DESCRIPTION_NC_PATTERN_PLUS_GROUP;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP] = SPECIFICATION_ELEMENT_NC_ELEMENT;
                
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS_NC;
        systemIdIndexTable[ELEMENT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ELEMENT_NC_ELEMENT;
        //**********************************************************************
        
        
        //<element name="QName">pattern+</element>
        //**********************************************************************
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ELEMENT_NI_ELEMENT] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_NAME] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_ELEMENT_NI_ELEMENT;
              
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        
        itemDescriptionIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[ELEMENT_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ELEMENT_NI_ELEMENT;
        //**********************************************************************
        
        
        //<attribute>nameClass [pattern]</attribute>
        //**********************************************************************
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_NAME] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS] = DESCRIPTION_NAME_CLASS;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_PATTERN_SQUARE] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP] = DESCRIPTION_NC_PATTERN_GROUP;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_NAME_CLASS_PATTERN_GROUP] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
                
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN_NC;
        systemIdIndexTable[ATTRIBUTE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ATTRIBUTE_NC_ELEMENT;
        //**********************************************************************
        
        
        //<attribute name="QName">[pattern]</element>
        //**********************************************************************
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_NAME] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_PATTERN_SQUARE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
              
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        
        itemDescriptionIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN;
        systemIdIndexTable[ATTRIBUTE_NI_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ATTRIBUTE_NI_ELEMENT;
        //**********************************************************************
        
        
        //<group>pattern+</group>
        //**********************************************************************
        itemDescriptionIndexTable[GROUP_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[GROUP_ELEMENT] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[GROUP_ELEMENT_NAME] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_GROUP_ELEMENT;
              
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_GROUP_ELEMENT;
        
        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_GROUP_ELEMENT;

        itemDescriptionIndexTable[GROUP_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[GROUP_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_GROUP_ELEMENT;        
        //**********************************************************************
        
        //<interleave>pattern+</interleave>
        //**********************************************************************
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[INTERLEAVE_ELEMENT] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[INTERLEAVE_ELEMENT_NAME] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_INTERLEAVE_ELEMENT;
              
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_INTERLEAVE_ELEMENT;
        
        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_INTERLEAVE_ELEMENT;

        itemDescriptionIndexTable[INTERLEAVE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[INTERLEAVE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_INTERLEAVE_ELEMENT;        
        //**********************************************************************
        
        //<choice>pattern+</choice>
        //**********************************************************************
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_NAME] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
              
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;

        itemDescriptionIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[CHOICE_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_CHOICE_PATTERN_ELEMENT;        
        //**********************************************************************
                
        //<optional>pattern+</optional>
        //**********************************************************************
        itemDescriptionIndexTable[OPTIONAL_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[OPTIONAL_ELEMENT] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[OPTIONAL_ELEMENT_NAME] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_OPTIONAL_ELEMENT;
              
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_OPTIONAL_ELEMENT;
        
        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_OPTIONAL_ELEMENT;

        itemDescriptionIndexTable[OPTIONAL_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[OPTIONAL_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_OPTIONAL_ELEMENT;        
        //**********************************************************************
        
        //<zeroOrMore>pattern+</zeroOrMore>
        //**********************************************************************
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_NAME] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
              
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;

        itemDescriptionIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ZERO_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ZERO_OR_MORE_ELEMENT;        
        //**********************************************************************
        
        //<oneOrMore>pattern+</oneOrMore>
        //**********************************************************************
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_NAME] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
              
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ONE_OR_MORE_ELEMENT;
        
        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ONE_OR_MORE_ELEMENT;

        itemDescriptionIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ONE_OR_MORE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ONE_OR_MORE_ELEMENT;        
        //**********************************************************************
        
        
        //<list>pattern+</list>
        //**********************************************************************
        itemDescriptionIndexTable[LIST_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[LIST_ELEMENT] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[LIST_ELEMENT_NAME] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[LIST_ELEMENT_CONTENT] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_LIST_ELEMENT;
              
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_LIST_ELEMENT;
        
        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_LIST_ELEMENT;

        itemDescriptionIndexTable[LIST_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[LIST_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_LIST_ELEMENT;        
        //**********************************************************************
        
        
        //<mixed>pattern+</mixed>
        //**********************************************************************
        itemDescriptionIndexTable[MIXED_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[MIXED_ELEMENT] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[MIXED_ELEMENT_NAME] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_MIXED_ELEMENT;
              
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_MIXED_ELEMENT;
        
        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_MIXED_ELEMENT;

        itemDescriptionIndexTable[MIXED_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[MIXED_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_MIXED_ELEMENT;        
        //**********************************************************************
        
        
        //<ref name="NCName"/>
        //**********************************************************************
        itemDescriptionIndexTable[REF_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[REF_ELEMENT] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[REF_ELEMENT_NAME] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[REF_ELEMENT_CONTENT] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_REF_ELEMENT;        
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_REF_ELEMENT;        
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_REF_ELEMENT;
        
        itemDescriptionIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_REF_ELEMENT;
        //**********************************************************************
        
        
        //<parentRef name="NCName"/>
        //**********************************************************************
        itemDescriptionIndexTable[PARENT_REF_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[PARENT_REF_ELEMENT] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[PARENT_REF_ELEMENT_NAME] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_PARENT_REF_ELEMENT;        
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_PARENT_REF_ELEMENT;        
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_PARENT_REF_ELEMENT;
        
        itemDescriptionIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[PARENT_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_PARENT_REF_ELEMENT;
        //**********************************************************************
        
        
        //<empty/>
        //**********************************************************************
        itemDescriptionIndexTable[EMPTY_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[EMPTY_ELEMENT] = SPECIFICATION_EMPTY_ELEMENT;
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[EMPTY_ELEMENT_NAME] = SPECIFICATION_EMPTY_ELEMENT;
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT] = SPECIFICATION_EMPTY_ELEMENT;
                
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_EMPTY_ELEMENT;
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_EMPTY_ELEMENT;        
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_EMPTY_ELEMENT;        
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_EMPTY_ELEMENT;
        
        itemDescriptionIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[EMPTY_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_EMPTY_ELEMENT;
        //**********************************************************************
        
        
        //<text/>
        //**********************************************************************
        itemDescriptionIndexTable[TEXT_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[TEXT_ELEMENT] = SPECIFICATION_TEXT_ELEMENT;
        
        itemDescriptionIndexTable[TEXT_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[TEXT_ELEMENT_NAME] = SPECIFICATION_TEXT_ELEMENT;
        
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT] = SPECIFICATION_TEXT_ELEMENT;
                
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_TEXT_ELEMENT;
        
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_TEXT_ELEMENT;        
        
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_TEXT_ELEMENT;        
        
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_TEXT_ELEMENT;
        
        itemDescriptionIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[TEXT_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_TEXT_ELEMENT;
        //**********************************************************************
        
        
        //<value [type="NCName"]>string</value>
        //**********************************************************************
        itemDescriptionIndexTable[VALUE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[VALUE_ELEMENT] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[VALUE_ELEMENT_NAME] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_TEXT] = DESCRIPTION_TEXT;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_TEXT] = SPECIFICATION_VALUE_ELEMENT;
                
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME] = SPECIFICATION_VALUE_ELEMENT;
                
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE] = DESCRIPTION_TYPE_ATTRIBUTE;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_TYPE_ATTRIBUTE_SQUARE] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_VALUE_ELEMENT;
        
        itemDescriptionIndexTable[VALUE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[VALUE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_VALUE_ELEMENT;
        //**********************************************************************
        
        
        //<data type="NCName">param* [exceptPattern]</data>
        //**********************************************************************
        itemDescriptionIndexTable[DATA_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[DATA_ELEMENT] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DATA_ELEMENT_NAME] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[DATA_ELEMENT_CONTENT] = SPECIFICATION_DATA_ELEMENT;
                
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_PARAM] = DESCRIPTION_PARAM;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_PARAM] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_PARAM_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_PARAM_STAR] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_EXCEPT_PATTERN] = DESCRIPTION_EXCEPT_PATTERN;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_EXCEPT_PATTERN] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_EXCEPT_PATTERN_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_EXCEPT_PATTERN_SQUARE] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_PARAM_EXCEPT_PATTERN_GROUP] = DESCRIPTION_PARAM_EXCEPT_PATTERN_GROUP;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_PARAM_EXCEPT_PATTERN_GROUP] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_DATA;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_NAME] = SPECIFICATION_DATA_ELEMENT;
                
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE_VALUE] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE] = DESCRIPTION_TYPE_ATTRIBUTE;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_TYPE_ATTRIBUTE] = SPECIFICATION_DATA_ELEMENT;
       
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_DATA_ELEMENT;
        
        itemDescriptionIndexTable[DATA_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[DATA_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_DATA_ELEMENT;
        //**********************************************************************
        
        
        //<notAllowed/>
        //**********************************************************************
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_NAME] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
                
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_NOT_ALLOWED_ELEMENT;        
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_NOT_ALLOWED_ELEMENT;        
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
        
        itemDescriptionIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[NOT_ALLOWED_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_NOT_ALLOWED_ELEMENT;
        //**********************************************************************
        
        
        //<externalRef href="anyURI"/>
        //**********************************************************************
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_NAME] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE] = DESCRIPTION_HREF_ATTRIBUTE;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_EXTERNAL_REF_ELEMENT;        
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_EXTERNAL_REF_ELEMENT;        
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        
        itemDescriptionIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[EXTERNAL_REF_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_EXTERNAL_REF_ELEMENT;
        //**********************************************************************
        
        
        //<grammar>grammarContent*</grammar>
        //**********************************************************************
        itemDescriptionIndexTable[GRAMMAR_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[GRAMMAR_ELEMENT] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[GRAMMAR_ELEMENT_NAME] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_GC] = DESCRIPTION_GRAMMAR_CONTENT;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_GC] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_GC_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_GC_STAR] = SPECIFICATION_GRAMMAR_ELEMENT;
              
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_GRAMMAR_ELEMENT;
        
        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_GRAMMAR_ELEMENT;

        itemDescriptionIndexTable[GRAMMAR_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[GRAMMAR_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_GRAMMAR_ELEMENT;        
        //**********************************************************************
        
        
        //<param name="NCName">string</param>
        //**********************************************************************
        itemDescriptionIndexTable[PARAM_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[PARAM_ELEMENT] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[PARAM_ELEMENT_NAME] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_TEXT] = DESCRIPTION_TEXT;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_TEXT] = SPECIFICATION_PARAM_ELEMENT;
                
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_PARAM_ELEMENT;
                
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_PARAM_ELEMENT;
        
        itemDescriptionIndexTable[PARAM_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[PARAM_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_PARAM_ELEMENT;
        //**********************************************************************
        
        
        //<except>pattern+</except>
        //**********************************************************************
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_NAME] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
              
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;

        itemDescriptionIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[EXCEPT_PATTERN_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_EXCEPT_PATTERN_ELEMENT;        
        //**********************************************************************
        
        
        //grammarContent
        //**********************************************************************
        itemDescriptionIndexTable[DEFINE_GRAMMAR_CONTENT_START] = DESCRIPTION_START;
        systemIdIndexTable[DEFINE_GRAMMAR_CONTENT_START] = SPECIFICATION_DEFINE_GRAMMAR_CONTENT;
        
        itemDescriptionIndexTable[DEFINE_GRAMMAR_CONTENT_DEFINE] = DESCRIPTION_DEFINE;
        systemIdIndexTable[DEFINE_GRAMMAR_CONTENT_DEFINE] = SPECIFICATION_DEFINE_GRAMMAR_CONTENT;
        
        itemDescriptionIndexTable[DEFINE_GRAMMAR_CONTENT] = DESCRIPTION_CHOICE_OF_GRAMMAR_CONTENT_DEFINITIONS;
        systemIdIndexTable[DEFINE_GRAMMAR_CONTENT] = SPECIFICATION_DEFINE_GRAMMAR_CONTENT;
        //**********************************************************************
        
        
        //<div>grammarContent*</div>
        //**********************************************************************
        itemDescriptionIndexTable[DIV_GC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[DIV_GC_ELEMENT] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DIV_GC_ELEMENT_NAME] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_GC] = DESCRIPTION_GRAMMAR_CONTENT;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_GC] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_GC_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_GC_STAR] = SPECIFICATION_DIV_GC_ELEMENT;
              
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_DIV_GC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_DIV_GC_ELEMENT;

        itemDescriptionIndexTable[DIV_GC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[DIV_GC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_DIV_GC_ELEMENT;        
        //**********************************************************************
        
        
        //<include href="anyURI"> includeContent </include>
        //**********************************************************************
        itemDescriptionIndexTable[INCLUDE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[INCLUDE_ELEMENT] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[INCLUDE_ELEMENT_NAME] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE] = DESCRIPTION_HREF_ATTRIBUTE;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_VALUE] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_HREF_ATTRIBUTE_NAME] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_INCLUDE_ELEMENT;

        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_INCLUDE_ELEMENT;        
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_IC] = DESCRIPTION_INCLUDE_CONTENT;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_IC] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_IC_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_IC_STAR] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_INCLUDE_ELEMENT;
        
        itemDescriptionIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC;
        systemIdIndexTable[INCLUDE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC] = SPECIFICATION_INCLUDE_ELEMENT;
        //**********************************************************************
        
        
        //includeContent
        //**********************************************************************
        itemDescriptionIndexTable[DEFINE_INCLUDE_CONTENT_START] = DESCRIPTION_START;
        systemIdIndexTable[DEFINE_INCLUDE_CONTENT_START] = SPECIFICATION_DEFINE_INCLUDE_CONTENT;
        
        itemDescriptionIndexTable[DEFINE_INCLUDE_CONTENT_DEFINE] = DESCRIPTION_DEFINE;
        systemIdIndexTable[DEFINE_INCLUDE_CONTENT_DEFINE] = SPECIFICATION_DEFINE_INCLUDE_CONTENT;
        
        itemDescriptionIndexTable[DEFINE_INCLUDE_CONTENT] = DESCRIPTION_CHOICE_OF_INCLUDE_CONTENT_DEFINITIONS;
        systemIdIndexTable[DEFINE_INCLUDE_CONTENT] = SPECIFICATION_DEFINE_INCLUDE_CONTENT;
        //**********************************************************************
        
        
        //<div>includeContent*</div>
        //**********************************************************************
        itemDescriptionIndexTable[DIV_IC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[DIV_IC_ELEMENT] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DIV_IC_ELEMENT_NAME] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_IC] = DESCRIPTION_INCLUDE_CONTENT;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_IC] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_IC_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_IC_STAR] = SPECIFICATION_DIV_IC_ELEMENT;
              
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_DIV_IC_ELEMENT;
        
        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_DIV_IC_ELEMENT;

        itemDescriptionIndexTable[DIV_IC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[DIV_IC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_DIV_IC_ELEMENT;        
        //**********************************************************************
        
        
        //<start [combine="method"]>pattern</start>
        //**********************************************************************
        itemDescriptionIndexTable[START_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[START_ELEMENT] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[START_ELEMENT_NAME] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[START_ELEMENT_CONTENT] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[START_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[START_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[START_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE] = DESCRIPTION_COMBINE_ATTRIBUTE;
        systemIdIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE] = DESCRIPTION_COMBINE_ATTRIBUTE;
        systemIdIndexTable[START_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[START_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_START_ELEMENT;
        
        itemDescriptionIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERN;
        systemIdIndexTable[START_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_START_ELEMENT;
        //**********************************************************************
        
        
        //<define name=""NCName [combine="method"]>pattern+</define>
        //**********************************************************************
        itemDescriptionIndexTable[DEFINE_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[DEFINE_ELEMENT] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DEFINE_ELEMENT_NAME] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_DEFINE_ELEMENT;
        
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE] = DESCRIPTION_NAME_ATTRIBUTE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_VALUE] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_NAME_ATTRIBUTE_NAME] = SPECIFICATION_DEFINE_ELEMENT;
        
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE] = DESCRIPTION_COMBINE_ATTRIBUTE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_VALUE] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_NAME] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE] = DESCRIPTION_COMBINE_ATTRIBUTE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_COMBINE_ATTRIBUTE_SQUARE] = SPECIFICATION_DEFINE_ELEMENT;
        
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_PATTERN] = DESCRIPTION_PATTERN;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_PATTERN] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_PATTERN_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_PATTERN_PLUS] = SPECIFICATION_DEFINE_ELEMENT;
        
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_DEFINE_ELEMENT;
        
        itemDescriptionIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS;
        systemIdIndexTable[DEFINE_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE] = SPECIFICATION_DEFINE_ELEMENT;
        //**********************************************************************
        
        //nameClass
        //**********************************************************************
        itemDescriptionIndexTable[DEFINE_NAME_CLASS] = DESCRIPTION_CHOICE_OF_NAME_CLASS_ELEMENTS;
        systemIdIndexTable[DEFINE_NAME_CLASS] = SPECIFICATION_NAME_CLASS;
        //**********************************************************************
        
        
        //<name>QName</name>
        //**********************************************************************
        itemDescriptionIndexTable[NAME_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[NAME_ELEMENT] = SPECIFICATION_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NAME_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[NAME_ELEMENT_NAME] = SPECIFICATION_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NAME_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_DATA_GROUP;
        systemIdIndexTable[NAME_ELEMENT_CONTENT] = SPECIFICATION_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NAME_ELEMENT_CONTENT_DATA] = DESCRIPTION_DATA;
        systemIdIndexTable[NAME_ELEMENT_CONTENT_DATA] = SPECIFICATION_NAME_ELEMENT;                
        
        itemDescriptionIndexTable[NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_NAME_ELEMENT;
        //**********************************************************************
        
        
        //<anyName>[exceptPattern]</anyName>
        //**********************************************************************
        itemDescriptionIndexTable[ANY_NAME_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[ANY_NAME_ELEMENT] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[ANY_NAME_ELEMENT_NAME] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC] = DESCRIPTION_EXCEPT_NC;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE] = SPECIFICATION_ANY_NAME_ELEMENT;
              
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_ANY_NAME_ELEMENT;
        
        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_ANY_NAME_ELEMENT;

        itemDescriptionIndexTable[ANY_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[ANY_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_ANY_NAME_ELEMENT;        
        //**********************************************************************
        
        
        //<nsName>[exceptPattern]</nsName>
        //**********************************************************************
        itemDescriptionIndexTable[NS_NAME_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[NS_NAME_ELEMENT] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[NS_NAME_ELEMENT_NAME] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_EXCEPT_NC] = DESCRIPTION_EXCEPT_NC;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_EXCEPT_NC] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_EXCEPT_NC_SQUARE] = SPECIFICATION_NS_NAME_ELEMENT;
              
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT_NC] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_NS_NAME_ELEMENT;
        
        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_NS_NAME_ELEMENT;

        itemDescriptionIndexTable[NS_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[NS_NAME_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_NS_NAME_ELEMENT;        
        //**********************************************************************
        
        
     
        //<choice>exceptPattern+</choice>
        //**********************************************************************
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[CHOICE_NC_ELEMENT] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[CHOICE_NC_ELEMENT_NAME] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_NC] = DESCRIPTION_NAME_CLASS;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_NC] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_NC_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_NC_PLUS] = SPECIFICATION_CHOICE_NC_ELEMENT;
              
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_NC;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_CHOICE_NC_ELEMENT;
        
        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_CHOICE_NC_ELEMENT;

        itemDescriptionIndexTable[CHOICE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[CHOICE_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_CHOICE_NC_ELEMENT;        
        //**********************************************************************
                
        
        //<choice>exceptPattern+</choice>
        //**********************************************************************
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT] = DESCRIPTION_ELEMENT;
        systemIdIndexTable[EXCEPT_NC_ELEMENT] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_NAME] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT] = DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NC] = DESCRIPTION_NAME_CLASS;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NC] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NC_PLUS] = DESCRIPTION_ONE_OR_MORE;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NC_PLUS] = SPECIFICATION_EXCEPT_NC_ELEMENT;
              
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_STAR] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC] = DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_NC;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ELEMENT_INTERLEAVE_NC] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = DESCRIPTION_FOREIGN_ATTRIBUTES;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_FOREIGN_ATTRIBUTES] = SPECIFICATION_EXCEPT_NC_ELEMENT;
        
        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_NS_ATTRIBUTE] = SPECIFICATION_EXCEPT_NC_ELEMENT;

        itemDescriptionIndexTable[EXCEPT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[EXCEPT_NC_ELEMENT_CONTENT_DL_ATTRIBUTE] = SPECIFICATION_EXCEPT_NC_ELEMENT;        
        //**********************************************************************
                
        
        //foreignElement
        //**********************************************************************
        itemDescriptionIndexTable[FOREIGN_ELEMENT] = DESCRIPTION_FOREIGN_ELEMENT;
        systemIdIndexTable[FOREIGN_ELEMENT] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_ANY_NAME] = DESCRIPTION_ANY_NAME;
        systemIdIndexTable[FOREIGN_ELEMENT_ANY_NAME] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_ANY_NAME_EXCEPT] = DESCRIPTION_EXCEPT_NC;
        systemIdIndexTable[FOREIGN_ELEMENT_ANY_NAME_EXCEPT] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_ANY_NAME_EXCEPT_NS] = DESCRIPTION_NS_NAME;
        systemIdIndexTable[FOREIGN_ELEMENT_ANY_NAME_EXCEPT_NS] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_CONTENT_TEXT] = DESCRIPTION_TEXT;
        systemIdIndexTable[FOREIGN_ELEMENT_CONTENT_TEXT] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_CONTENT_ANY_ATTRIBUTE] = DESCRIPTION_ANY_ATTRIBUTE;
        systemIdIndexTable[FOREIGN_ELEMENT_CONTENT_ANY_ATTRIBUTE] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_CONTENT_ANY_ELEMENT] = DESCRIPTION_ANY_ELEMENT;
        systemIdIndexTable[FOREIGN_ELEMENT_CONTENT_ANY_ELEMENT] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_CONTENT_CHOICE] = DESCRIPTION_CHOICE_OF_ANY;
        systemIdIndexTable[FOREIGN_ELEMENT_CONTENT_CHOICE] = SPECIFICATION_FOREIGN_ELEMENT;
        
        itemDescriptionIndexTable[FOREIGN_ELEMENT_CONTENT_CHOICE_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[FOREIGN_ELEMENT_CONTENT_CHOICE_STAR] = SPECIFICATION_FOREIGN_ELEMENT;
        
        
        
        //anyElement
        //**********************************************************************
        itemDescriptionIndexTable[ANY_ELEMENT] = DESCRIPTION_ANY_ELEMENT;
        systemIdIndexTable[ANY_ELEMENT] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_NAME] = DESCRIPTION_ANY_NAME;
        systemIdIndexTable[ANY_ELEMENT_NAME] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_CONTENT_TEXT] = DESCRIPTION_TEXT;
        systemIdIndexTable[ANY_ELEMENT_CONTENT_TEXT] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_CONTENT_ANY_ATTRIBUTE] = DESCRIPTION_ANY_ATTRIBUTE;
        systemIdIndexTable[ANY_ELEMENT_CONTENT_ANY_ATTRIBUTE] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_CONTENT_ANY_ELEMENT] = DESCRIPTION_ANY_ELEMENT;
        systemIdIndexTable[ANY_ELEMENT_CONTENT_ANY_ELEMENT] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_CONTENT_CHOICE] = DESCRIPTION_CHOICE_OF_ANY;
        systemIdIndexTable[ANY_ELEMENT_CONTENT_CHOICE] = SPECIFICATION_ANY_ELEMENT;
        
        itemDescriptionIndexTable[ANY_ELEMENT_CONTENT_CHOICE_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[ANY_ELEMENT_CONTENT_CHOICE_STAR] = SPECIFICATION_ANY_ELEMENT;
        
        
        //foreignAttribute
        //**********************************************************************
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE] = DESCRIPTION_FOREIGN_ATTRIBUTE;
        systemIdIndexTable[FOREIGN_ATTRIBUTE] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME] = DESCRIPTION_ANY_NAME;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT] = DESCRIPTION_EXCEPT_NC;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE] = DESCRIPTION_CHOICE;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_RNG] = DESCRIPTION_NS_NAME;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_RNG] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_NULL] = DESCRIPTION_NS_NAME;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_ANY_NAME_EXCEPT_CHOICE_NS_NULL] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_VALUE] = DESCRIPTION_TEXT;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_VALUE] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        itemDescriptionIndexTable[FOREIGN_ATTRIBUTE_STAR] = DESCRIPTION_ZERO_OR_MORE;
        systemIdIndexTable[FOREIGN_ATTRIBUTE_STAR] = SPECIFICATION_FOREIGN_ATTRIBUTE;
        
        
        //anyAttribute
        //**********************************************************************
        itemDescriptionIndexTable[ANY_ATTRIBUTE] = DESCRIPTION_ANY_ATTRIBUTE;
        systemIdIndexTable[ANY_ATTRIBUTE] = SPECIFICATION_ANY_ATTRIBUTE;
        
        itemDescriptionIndexTable[ANY_ATTRIBUTE_NAME] = DESCRIPTION_ANY_NAME;
        systemIdIndexTable[ANY_ATTRIBUTE_NAME] = SPECIFICATION_ANY_ATTRIBUTE;
        
        itemDescriptionIndexTable[ANY_ATTRIBUTE_VALUE] = DESCRIPTION_TEXT;
        systemIdIndexTable[ANY_ATTRIBUTE_VALUE] = SPECIFICATION_ANY_ATTRIBUTE;
        
        
        
        //ns attribute
        //**********************************************************************
        itemDescriptionIndexTable[NS_ATTRIBUTE] = DESCRIPTION_NS_ATTRIBUTE;
        systemIdIndexTable[NS_ATTRIBUTE] = SPECIFICATION_NS_ATTRIBUTE;
        
        itemDescriptionIndexTable[NS_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[NS_ATTRIBUTE_NAME] = SPECIFICATION_NS_ATTRIBUTE;
        
        itemDescriptionIndexTable[NS_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[NS_ATTRIBUTE_VALUE] = SPECIFICATION_NS_ATTRIBUTE;
        
        itemDescriptionIndexTable[NS_ATTRIBUTE_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[NS_ATTRIBUTE_SQUARE] = SPECIFICATION_NS_ATTRIBUTE;
        
        
        //datatypeLibrary attribute
        //**********************************************************************
        itemDescriptionIndexTable[DL_ATTRIBUTE] = DESCRIPTION_DL_ATTRIBUTE;
        systemIdIndexTable[DL_ATTRIBUTE] = SPECIFICATION_DL_ATTRIBUTE;
        
        itemDescriptionIndexTable[DL_ATTRIBUTE_NAME] = DESCRIPTION_NAME;
        systemIdIndexTable[DL_ATTRIBUTE_NAME] = SPECIFICATION_DL_ATTRIBUTE;
        
        itemDescriptionIndexTable[DL_ATTRIBUTE_VALUE] = DESCRIPTION_DATA;
        systemIdIndexTable[DL_ATTRIBUTE_VALUE] = SPECIFICATION_DL_ATTRIBUTE;
        
        itemDescriptionIndexTable[DL_ATTRIBUTE_SQUARE] = DESCRIPTION_OPTIONAL;
        systemIdIndexTable[DL_ATTRIBUTE_SQUARE] = SPECIFICATION_DL_ATTRIBUTE;
        
                
        
        itemDescriptionTable[DESCRIPTION_PATTERN] = "pattern";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_PATTERN_ELEMENTS] = "choice of pattern elements";
        itemDescriptionTable[DESCRIPTION_ELEMENT] = "element";
        itemDescriptionTable[DESCRIPTION_NAME] = "name";
        itemDescriptionTable[DESCRIPTION_ATTRIBUTES_AND_ELEMENTS_GROUP] = "attributes and elements group";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ATTRIBUTES] = "foreign attributes";
        itemDescriptionTable[DESCRIPTION_NS_ATTRIBUTE] = "ns attribute";
        itemDescriptionTable[DESCRIPTION_DL_ATTRIBUTE] = "datatypeLibrary attributes";
        itemDescriptionTable[DESCRIPTION_NAME_CLASS] = "name class";    
        itemDescriptionTable[DESCRIPTION_ONE_OR_MORE] = "one or more";
        itemDescriptionTable[DESCRIPTION_ZERO_OR_MORE] = "zero or more";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT] = "foreign element";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS] = "interleaving of pattern elements with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERN] = "interleaving of a pattern element with optional foreign elements";        
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_PATTERNS_NC] = "interleaving of name class with pattern elements and optional foreign elements";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN] = "interleaving of optional pattern element with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_OPTIONAL_PATTERN_NC] = "interleaving of name class with optional pattern element and optional foreign elements";
        itemDescriptionTable[DESCRIPTION_NC_PATTERN_PLUS_GROUP] = "group of name class and pattern elements";
        itemDescriptionTable[DESCRIPTION_NC_PATTERN_GROUP] = "group of a name class and a pattern element";
        itemDescriptionTable[DESCRIPTION_DATA] = "data";
        itemDescriptionTable[DESCRIPTION_OPTIONAL] = "optional";
        itemDescriptionTable[DESCRIPTION_NAME_ATTRIBUTE] = "name attribute";
        itemDescriptionTable[DESCRIPTION_TEXT] = "text";
        itemDescriptionTable[DESCRIPTION_TYPE_ATTRIBUTE] = "type attribute";
        itemDescriptionTable[DESCRIPTION_PARAM] = "param";
        itemDescriptionTable[DESCRIPTION_EXCEPT_PATTERN] = "except in pattern context"; 
        itemDescriptionTable[DESCRIPTION_PARAM_EXCEPT_PATTERN_GROUP] = "group of param and except elements";        
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_DATA] = "interleaving of optional param and except elements group with foreign elements";
        itemDescriptionTable[DESCRIPTION_HREF_ATTRIBUTE] = "href attribute";
        itemDescriptionTable[DESCRIPTION_GRAMMAR_CONTENT] = "grammar content";        
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_GC] = "interleaving of grammar content elements with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_GRAMMAR_CONTENT_DEFINITIONS] = "choice of grammar content definitions";
        itemDescriptionTable[DESCRIPTION_START] = "start";
        itemDescriptionTable[DESCRIPTION_DEFINE] = "define";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_GRAMMAR_CONTENT_DEFINITIONS] = "choice of grammar content elements";
        itemDescriptionTable[DESCRIPTION_INCLUDE_CONTENT] = "include content";        
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_FOR_IC] = "interleaving of include content elements with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_INCLUDE_CONTENT_DEFINITIONS] = "choice of include content elements";
        itemDescriptionTable[DESCRIPTION_COMBINE_ATTRIBUTE] = "combine attribute";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_NAME_CLASS_ELEMENTS] = "choice of name class elements";
        itemDescriptionTable[DESCRIPTION_ATTRIBUTES_AND_DATA_GROUP] = "attributes and data";
        itemDescriptionTable[DESCRIPTION_EXCEPT_NC] = "except in name class context";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_EXCEPT] = "interleaving of optional except element with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ELEMENT_INTERLEAVE_NC] = "interleaving of name class elements with optional foreign elements";
        itemDescriptionTable[DESCRIPTION_ANY_NAME] = "anyName";
        itemDescriptionTable[DESCRIPTION_NS_NAME] = "nsName";        
        itemDescriptionTable[DESCRIPTION_ANY_ATTRIBUTE] = "any attribute";
        itemDescriptionTable[DESCRIPTION_ANY_ELEMENT] = "any element";
        itemDescriptionTable[DESCRIPTION_CHOICE_OF_ANY] = "choice of text, any attribute or element ";
        itemDescriptionTable[DESCRIPTION_FOREIGN_ATTRIBUTE] = "foreign attribute";
        itemDescriptionTable[DESCRIPTION_CHOICE] = "choice";
        
                    
        systemIdTable[SPECIFICATION_PATTERN] = "RELAXNG Specification 3.Full Syntax: pattern";
        systemIdTable[SPECIFICATION_ELEMENT_NC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: element with name class child";
        systemIdTable[SPECIFICATION_ELEMENT_NI_ELEMENT] = "RELAXNG Specification 3.Full Syntax: element with name attribute";
        systemIdTable[SPECIFICATION_ATTRIBUTE_NC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: attribute with name class child";
        systemIdTable[SPECIFICATION_ATTRIBUTE_NI_ELEMENT] = "RELAXNG Specification 3.Full Syntax: attribute with name attribute";
        systemIdTable[SPECIFICATION_GROUP_ELEMENT] = "RELAXNG Specification 3.Full Syntax: group";
        systemIdTable[SPECIFICATION_INTERLEAVE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: interleave";
        systemIdTable[SPECIFICATION_CHOICE_PATTERN_ELEMENT] = "RELAXNG Specification 3.Full Syntax: choice in pattern context";
        systemIdTable[SPECIFICATION_OPTIONAL_ELEMENT] = "RELAXNG Specification 3.Full Syntax: optional";
        systemIdTable[SPECIFICATION_ZERO_OR_MORE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: zeroOrMore";
        systemIdTable[SPECIFICATION_ONE_OR_MORE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: oneOrMore";
        systemIdTable[SPECIFICATION_LIST_ELEMENT] = "RELAXNG Specification 3.Full Syntax: list";
        systemIdTable[SPECIFICATION_MIXED_ELEMENT] = "RELAXNG Specification 3.Full Syntax: mixed";
        systemIdTable[SPECIFICATION_REF_ELEMENT] = "RELAXNG Specification 3.Full Syntax: ref";
        systemIdTable[SPECIFICATION_PARENT_REF_ELEMENT] = "RELAXNG Specification 3.Full Syntax: parentRef";
        systemIdTable[SPECIFICATION_EMPTY_ELEMENT] = "RELAXNG Specification 3.Full Syntax: empty";
        systemIdTable[SPECIFICATION_TEXT_ELEMENT] = "RELAXNG Specification 3.Full Syntax: text";
        systemIdTable[SPECIFICATION_VALUE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: value";
        systemIdTable[SPECIFICATION_DATA_ELEMENT] = "RELAXNG Specification 3.Full Syntax: data";
        systemIdTable[SPECIFICATION_NOT_ALLOWED_ELEMENT] = "RELAXNG Specification 3.Full Syntax: notAllowed";
        systemIdTable[SPECIFICATION_EXTERNAL_REF_ELEMENT] = "RELAXNG Specification 3.Full Syntax: externalRef";
        systemIdTable[SPECIFICATION_GRAMMAR_ELEMENT] = "RELAXNG Specification 3.Full Syntax: grammar";
        systemIdTable[SPECIFICATION_PARAM_ELEMENT] = "RELAXNG Specification 3.Full Syntax: param";
        systemIdTable[SPECIFICATION_EXCEPT_PATTERN_ELEMENT] = "RELAXNG Specification 3.Full Syntax: except in pattern context";
        systemIdTable[SPECIFICATION_DEFINE_GRAMMAR_CONTENT] = "RELAXNG Specification 3.Full Syntax: grammar content";
        systemIdTable[SPECIFICATION_DIV_GC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: div in grammar context";
        systemIdTable[SPECIFICATION_INCLUDE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: include";
        systemIdTable[SPECIFICATION_DEFINE_INCLUDE_CONTENT] = "RELAXNG Specification 3.Full Syntax: include content";
        systemIdTable[SPECIFICATION_DIV_IC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: div in include context";
        systemIdTable[SPECIFICATION_START_ELEMENT] = "RELAXNG Specification 3.Full Syntax: start";
        systemIdTable[SPECIFICATION_DEFINE_ELEMENT] = "RELAXNG Specification 3.Full Syntax: define";
        systemIdTable[SPECIFICATION_NAME_CLASS] = "RELAXNG Specification 3.Full Syntax: nameClass";
        systemIdTable[SPECIFICATION_NAME_ELEMENT] = "RELAXNG Specification 3.Full Syntax: name";
        systemIdTable[SPECIFICATION_ANY_NAME_ELEMENT] = "RELAXNG Specification 3.Full Syntax: anyName";
        systemIdTable[SPECIFICATION_NS_NAME_ELEMENT] = "RELAXNG Specification 3.Full Syntax: nsName";
        systemIdTable[SPECIFICATION_CHOICE_NC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: choice in name class context";
        systemIdTable[SPECIFICATION_EXCEPT_NC_ELEMENT] = "RELAXNG Specification 3.Full Syntax: except in name class context";
        systemIdTable[SPECIFICATION_FOREIGN_ELEMENT] = "RELAXNG Specification 3.Full Syntax: foreign element";
        systemIdTable[SPECIFICATION_ANY_ELEMENT] = "RELAXNG Specification 3.Full Syntax: any element";
        systemIdTable[SPECIFICATION_FOREIGN_ATTRIBUTE] = "RELAXNG Specification 3.Full Syntax: foreign attribute";
        systemIdTable[SPECIFICATION_ANY_ATTRIBUTE] = "RELAXNG Specification 3.Full Syntax: any attribute";
        systemIdTable[SPECIFICATION_NS_ATTRIBUTE] = "RELAXNG Specification 3.Full Syntax: ns attribute";
        systemIdTable[SPECIFICATION_DL_ATTRIBUTE] = "RELAXNG Specification 3.Full Syntax: datatypeLibrary attribute";
                
    }
    
    
    
    
	public int getItemId(int recordIndex){
	    throw new IllegalStateException();
	}
	
	public Map<String, String> getDeclaredXmlns(int recordIndex){
	    throw new IllegalStateException();
	}
	
	public String getItemDescription(int recordIndex){
		return itemDescriptionTable[itemDescriptionIndexTable[recordIndex]];
	}
    
    public String getNamespaceURI(int recordIndex){
		throw new IllegalStateException();
	}
	
    public String getLocalName(int recordIndex){
		throw new IllegalStateException();
	}
		
	public String getAttributeType(int recordIndex){
		throw new IllegalStateException();
	}
    	
	public String getStringValue(int recordIndex){
		throw new IllegalStateException();
	}

    public char[] getCharArrayValue(int recordIndex){
		throw new IllegalStateException();
	}    
	
	public String getSystemId(int recordIndex){
		return systemIdTable[systemIdIndexTable[recordIndex]];
	}
		
    public String getPublicId(int recordIndex){
		throw new IllegalStateException();
	}
	
	public int getLineNumber(int recordIndex){
		return UNKNOWN;
	}
	
	public int getColumnNumber(int recordIndex){
		return UNKNOWN;
	}

	public DocumentIndexedData getDocumentIndexedData(){
	    throw new IllegalStateException();
	}
	
}
