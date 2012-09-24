package sereneSamples;

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeStreamingValidator;

import sereneWrite.MessageWriter;

/**
*<p>
* Implements a datatype that accepts text tokens(no inner whitespacespace) that 
* are within a certain Levenshtein distance from a another given token. The 
* <code>data</code> element is required to have two params:
* <ul>
*       <li>
*       comparisonBase - a token containing no inner whitespace
*       </li>
*       <li>
*       distance - a natural number
*       </li>
*   </ul>
* otherwise an error message is issued.
* </p>
* <p>
* Levenshtein distance algorithm from an article by Michael Gilleland, Merriam 
* Park Software from http://www.merriampark.com/ld.htm .
* </p>
*/

class SimilarTokenDT implements Datatype{
    Levenshtein levenshtein; 
    String base;
    int distance;
    
    SimilarTokenDT(){
        levenshtein = new Levenshtein();
    }
    
    void setBase(String base) throws DatatypeException{
        base = base.trim();
        if(base.indexOf(' ') >= 0
           || base.indexOf('\n') >= 0
           || base.indexOf('\t') >= 0
           || base.indexOf('\r') >= 0){
            throw new DatatypeException("No whitespace allowed inside the token.");   
        }
        this.base = base;
    }
    
    void setDistance(String d) throws DatatypeException{
        
        try{
            distance = Integer.parseInt(d);
        }catch(NumberFormatException e){
            throw new DatatypeException("Distance must be a natural number. "+e.getMessage());
        }
        if(distance < 0){
            throw new DatatypeException("Distance must be a natural number.");
        }
    }
    
	public boolean isValid(String str, ValidationContext vc) {
        str = str.trim();
        if(!isToken(str)){
            return false;   
        }        
		if(levenshtein.getDistance(str, base) <= distance)return true;
		return false;
	}

	public void checkValid(String str, ValidationContext vc) throws DatatypeException {
        str = str.trim();
        if(!isToken(str)){
            throw new DatatypeException("No whitespace allowed inside the token.");   
        }
		if(!(levenshtein.getDistance(str, base) <= distance))
            throw new DatatypeException("Token not similar enough, Levenshtein distance to base \""+base+"\" should be at most "+distance);			
	}
    
	public Object createValue(String str, ValidationContext vc){
        return str.trim();
	}

	public boolean isContextDependent() {
		return false;
	}

	public boolean alwaysValid() {
		return false;
	}

	public int getIdType() {
		return ID_TYPE_NULL;
	}

	public boolean sameValue(Object obj1, Object obj2) {
        String s1 = null;
        String s2 = null;
        try{
            s1 = (String)obj1;
            s2 = (String)obj2;
        }catch(ClassCastException c){
            return false;
        }        
        
        s1 = s1.trim();
        if(!isToken(s1)) return false;
        s2 = s2.trim();
        if(!isToken(s2)) return false;
        return s1.equals(s2);
	}

	public int valueHashCode(Object obj) {
		return obj.hashCode();
	}

	public DatatypeStreamingValidator createStreamingValidator(ValidationContext vc) {
		throw new UnsupportedOperationException();
	}
    
    private boolean isToken(String str){
        if(str.indexOf(' ') >= 0
           || str.indexOf('\n') >= 0
           || str.indexOf('\t') >= 0
           || str.indexOf('\r') >= 0){
            return false;   
        }
        return true;
    }
    
    class Levenshtein{    
        private int getMin(int a, int b, int c) {
            int mi;    
            mi = a;
            if (b < mi) {
                mi = b;
            }
            if (c < mi) {
                mi = c;
            }
            return mi;    
        }
    
          
        public int getDistance(String s, String t) {
            int d[][]; 
            int n; // length of s
            int m; // length of t
            int i; // iterates through s
            int j; // iterates through t
            char s_i; // ith character of s
            char t_j; // jth character of t
            int cost; // cost
        
            n = s.length ();
            m = t.length ();
            if (n == 0) {
                return m;
            }
            if (m == 0) {
                return n;
            }
            d = new int[n+1][m+1];
        
            for (i = 0; i <= n; i++) {
                d[i][0] = i;
            }
    
            for (j = 0; j <= m; j++) {
                d[0][j] = j;
            }
        
            for (i = 1; i <= n; i++) {    
                s_i = s.charAt(i - 1);    
        
                for (j = 1; j <= m; j++) {    
                    t_j = t.charAt(j - 1);
        
                    if (s_i == t_j) {
                        cost = 0;
                    }else {
                        cost = 1;
                    }     
                    d[i][j] = getMin(d[i-1][j]+1, d[i][j-1]+1, d[i-1][j-1] + cost);        
                }
    
            }
            return d[n][m];    
        }
    }
}
