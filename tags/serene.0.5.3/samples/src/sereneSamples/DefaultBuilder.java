package sereneSamples;


import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

public class DefaultBuilder implements DatatypeBuilder {
	
	private final Datatype baseType;
	
	public DefaultBuilder( Datatype baseType ) {
		this.baseType = baseType;
	}
	
	public void addParameter( String name, String strValue, ValidationContext context )
			throws DatatypeException {
		throw new DatatypeException("Param not allowed.");
	}
	
	public Datatype createDatatype() throws DatatypeException {
		return baseType;
	}
}
