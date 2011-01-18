package sereneSamples;


import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

import serene.datatype.DefaultBuilder;

class SimilarTokenBuilder implements DatatypeBuilder{
    boolean baseSet;
    boolean distanceSet;
    SimilarTokenDT datatype; 
    
	public SimilarTokenBuilder(){
        datatype = new SimilarTokenDT();
        baseSet = false;
        distanceSet = false;
    }
	
	public void addParameter( String name, String strValue, ValidationContext context )
			throws DatatypeException {
        if(name.equals("comparisonBase")){
            datatype.setBase(strValue);
            baseSet = true;
        }else if(name.equals("distance")){
            datatype.setDistance(strValue);
            distanceSet = true;
        }
	}	
    
    public Datatype createDatatype() throws DatatypeException{
        if(!(baseSet && distanceSet)){
            throw new DatatypeException("Missing parameters.");
        }else if(!baseSet){
            throw new DatatypeException("Missing comparison base parameter.");
        }else if(!distanceSet){
            throw new DatatypeException("Missing distance parameter.");
        }
        return datatype;
    }
}
