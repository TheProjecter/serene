# Features #
<br>

<h2>Serene 0.6.3</h2>

No new features are added by this release.<br>
<br>
No old features are removed by this release.<br>
<br>
<h2>Serene 0.6.2</h2>

Features added by this release:<br>
<br>
<b><code>http://serenerng.com/features/processEmbeddedSchematron</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, ensures that ISO Schematron markup embedded in RELAX NG schemas is processed and the resulting <code>javax.xml.validation.Schema</code> instances will produce <code>javax.xml.validation.ValidatorHandler</code> and <code>javax.xml.validation.Validator</code> objects which will apply the Schematron rules. By default it is set to <code>false</code>.<br>
<br>
<br>
<br>
No old features are removed by this release.<br>
<br>
<br>
<br>
<h2>Serene 0.6.1</h2>

Features added by this release:<br>
<br>
<b><code>http://serenerng.com/features/optimizeForResourceSharing</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, ensures that common internal resources are created for and shared by all the threads. It should improve performance in an environement with several threads validating alternativelly. Due to synchronization envolved it should be used carefully. By default it is set to <code>false</code>.<br>
<br>
<br>
<br>
No old features are removed by this release.<br>
<br>
<br>
<h2>Serene 0.6</h2>

Features added by this release:<br>
<br>
<b><code>http://serenerng.org/features/restrictToFileName</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code>, <code>javax.xml.validation.ValidatorHandler</code> and <code>javax.xml.validation.Validator</code> which, when set to <code>true</code>, ensures that the location information in the error messages contains only the file name and not the full path URI for better readability. It only has effect on the error message text and does not influence the location information passed to the <code>SAXParseException</code>. By default it is set to <code>true</code>.<br>
<br>
<br>
<br>
No old features are removed by this release.<br>
<br>
<h2>Serene 0.5.3</h2>

No new features are added by this release.<br>
<br>
No old features are removed by this release.<br>
<br>
<h2>Serene 0.5.2</h2>

New features added by this release:<br>
<br>
<b><code>http://serenerng.org/features//schemaFactory/features/parsedModelSchema</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, ensures that a reference to the initial unsimplified parsed schema model is kept in the <code>javax.xml.validation.Schema</code>. When set to false, the parsed model instance is discarded and the memory freed. By default it is set to <code>false</code>.<br>
<br>
<b><code>http://serenerng.org/features/DTDCompatibility/level1/attributeDefaultValue</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, ensures that the attribute default values present in the schema document are parsed, simplified and it is checked if the <i>compatibility</i> property holds for them. By default it is set to <code>false</code>.<br>
<br>
<b><code>http://serenerng.org/features/DTDCompatibility/level1/attributeIdType</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code>, <code>javax.xml.validation.ValidatorHandler</code> and <code>javax.xml.validation.Validator</code> which, when set to <code>true</code>, ensures that the ID, IDREF, IDREFS datatypes from the DTD compatibility datatype library present in the schema document are parsed, simplified and it is checked if the compatibility property and the <i>soundness</i> relation holds for them. By default it is set to <code>false</code>.<br>
<br>
<b><code>http://serenerng.org/features/DTDCompatibility/level1/documentationElement</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, ensures that, for the documentation elements present in the schema document, it is checked if the <i>compatibility</i> property holds. By default it is set to <code>false</code>.<br>
<br>
<b><code>http://serenerng.org/features/DTDCompatibility/level2/attributeDefaultValue</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.ValidatorHandler</code> and <code>javax.xml.validation.Validator</code>. It is also recognized by the <code>javax.xml.validation.SchemaFactory</code> and transmited further throught the <code>javax.xml.validation.Schema</code> objects created. When set to <code>true</code>, it ensures that the infoset is modified according to the document input and the attribute default values present in the schema. By default it is set to <code>false</code>.<br>
<br>
When this feature is set to true in the <code>javax.xml.validation.SchemaFactory</code>, internally also the <b><code>http://serenerng.org/features/DTDCompatibility/level1/attributeDefaultValue</code></b> feature is set to <code>true</code>, so that the necessary internal model is built from the schema document. When this feature is set to true in the <code>javax.xml.validation.ValidatorHandler</code>, or <code>javax.xml.validation.Validator</code>, controls are performed to ensure that the necessary internal model is present and correct.<br>
<br>
<b><code>http://serenerng.org/features/DTDCompatibility/level2/attributeIdType</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.ValidatorHandler</code> and <code>javax.xml.validation.Validator</code>. It is also recognized by the <code>javax.xml.validation.SchemaFactory</code> and transmited further throught the <code>javax.xml.validation.Schema</code> objects created. When set to <code>true</code>, it ensures that the infoset is modified according to the document input and the ID, IDREF, IDREFS attributes present in the schema. By default it is set to <code>false</code>.<br>
<br>
When this feature is set to true in the <code>javax.xml.validation.SchemaFactory</code>, internally also the <b><code>http://serenerng.org/features/DTDCompatibility/level1/attributeIdType</code></b> feature is set to <code>true</code>, so that the necessary internal model is built from the schema document. When this feature is set to true in the <code>javax.xml.validation.ValidatorHandler</code>, or <code>javax.xml.validation.Validator</code>, controls are performed to ensure that the necessary internal model is present and correct.<br>
<br>
No old features are removed by this release.<br>
<br>
<h2>Serene 0.5.1</h2>

No new features are added by this release.<br>
<br>
No old features are removed by this release.<br>
<br>
<h2>Serene 0.5</h2>

The features implemented by this release are:<br>
<br>
<b><code>http://xml.org/sax/features/namespace-prefixes</code></b>

As per <code>javax.xml.validation.ValidatorHandler</code> contract, this feature controls how a <code>ValidatorHandler</code> introduces namespace bindings that were not present in the original SAX event stream. When this feature is set to <code>true</code>, it must make sure that the user's <code>ContentHandler</code> will see the corresponding <code>xmlns</code> attribute in the <code>Attributes</code> object of the <code>ContentHandler.startElement(String,String,String,Attributes)</code> callback. Otherwise, <code>xmlns</code> attributes must not be added to <code>Attributes</code> that's passed to the user-specified <code>ContentHandler</code>. By default it is set to <code>false</code>. See samples for an example.<br>
<br>
<b><code>http://serenerng.org/features/schemaFactory/replaceMissingDatatypeLibrary</code></b>

This is a <b>Serene</b> feature for the <code>javax.xml.validation.SchemaFactory</code> which, when set to <code>true</code>, makes sure that any unrecognized or unsupported datatype library is replaced with the RELAX NG native datatype library and any datatype from that library is replaced with the <code>token</code> datatype. By default it is set to <code>false</code>.<br>
<br>
Since <code>javax.xml.validation.SchemaFactory</code> specification requires that a non-null <code>javax.xml.validation.Schema</code> object must be returned every time regardless whether errors occurred or not during parsing, and it is the client's responsibility to make sure the returned schema object is usable, <b>Serene</b> uses the abstract class <code>serene.SereneRecoverableException</code> which extends <code>org.xml.sax.SAXParseException</code> to signal the client when an error was recovered from and the schema object returned is meaningful. In this case the implementing class is <code>serene.datatype.MissingLibraryException</code>. See samples for an example.