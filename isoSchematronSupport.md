# ISO Schematron Support #
<br>
Starting with release <b>0.6.2</b>, Serene introduces support for ISO Schematron. This includes support for pure Schematron schemas, as well as support for Schematron markup embedded in the RELAX NG schemas and is based on the official ISO Schematron implementation for XSLT 1.0 and XSLT 2.0 from <b><code>http://www.schematron.com</code></b> written by Rick Jelliffe, using the skeleton based on Oliver Becker's work.<br>
<br>
<h2>Schematron Schemas</h2>

Validation using pure Schematron schemas can be done through the JAXP interface by obtaining a <code>javax.xml.validation.Schema</code> object from the <code>javax.xml.validation.SchemaFactory</code> using the Schematron namespace URI, <code>http://purl.oclc.org/dsdl/schematron</code>.<br>
<br>
<h2>Schematron Embedded in RELAX NG Schemas</h2>

In order to use Schematron markup embedded in the RELAX NG, the feature <code>http://serenerng.com/features/processEmbeddedSchematron</code> must be set to true in the <code>SchemaFactory</code> before obtaining a <code>Schema</code> object for RELAX NG using its namespace URI, <code>http://relaxng.org/ns/structure/1.0</code>.<br>
<br>
<br>
Serene supports the following Schematron elements: <code>schema</code>, <code>pattern</code>, <code>rule</code>, <code>diagnostics</code>, <code>ns</code> and <code>let</code>. During parsing all the <code>rule</code> elements embedded directly in one RELAX NG document are grouped in one, internally generated, <code>pattern</code>. In a similar way, all the <code>pattern</code> elements are grouped in one <code>schema</code>. This will also contain the <code>ns</code>, <code>let</code> and <code>diagnostics</code> elements, if present. When using the RELAX NG <code>include</code> element, the Schematron markup found in any overriden definitions will not be represented in the compiled Schematron schema.<br>
<br>
In order to specify a query binding language for the Schematron embedded in RELAX NG schemas, the attribute <code>schematronQueryBinding</code> from the namespace <code>http://serenerng.com/schematron</code> can be used. This must be specified on the top element of the RELAX NG schema and is used for all the documents the schema comprises. The presence of this attribute on any other element, including top elements of schema documents referenced using <code>include</code> or <code>externalRef</code> will have no effect. When this attribute is absent, the default query binding language as per ISO Schematron specification in used. The ISO Schematron specification applies to the embedded Schematron <code>schema</code> elements and the query binding specified by them will be used for their content. This can be different from the one specified on the top RELAX NG element.<br>
<br>
<h2>Error Handling</h2>

In order to be able to access all the SVRL information available for errors, the classes <code>serene.schematron.FailedAssertException</code> and <code>serene.schematron.SuccessfulReportException</code> have been created. They have specific accessor methods for the SVRL information. You can either cast the exceptions caught by the <code>org.xml.sax.ErrorHandler</code>, or implement the <code>serene.schematron.SchematronErrorHandler</code> which extends it for the aforementioned exception classes. See samples for an example.<br>
<br>
Alternativelly, error messages parsed from the SVRL information can be obtained. The information consists of pattern name, or id if any present, rule context, assert or report text, location, test and diagnostics, if any present, as seen in the following example:<br>
<br>
<br>
<pre><code>file:///D:/sereneProject/dev/testSuiteRNGSchematron/tests/externalRef/document1.xml:14:1 Error in Schematron pattern "Numbers", rule context "//bar".<br>
Failed assertion: "Content of &lt;sum&gt; represents the sum of the contents of &lt;bar1&gt; and &lt;bar2&gt;.".<br>
Location: /root/bar.<br>
Test: number(bar1) + number(bar2) = number(sum).<br>
Diagnostics: Error_in_sum: expected 1 found 2. </code></pre>

<h2>Testing</h2>

The testing has been, for the moment, limited. For running the tests yourself, see documentation Testing.