# Custom Datatype Libraries #
<br>

<h2>Serene 0.6.3</h2>

Interface <code>serene.datatype.DatatypeErrorHandler</code> has been created to provide direct access to <code>serene.datatype.MissingLibraryException</code>.<br>
<br>
<h2>Serene 0.6.2</h2>

No changes with this release.<br>
<br>
<h2>Serene 0.6.1</h2>

No changes with this release.<br>
<br>
<h2>Serene 0.6</h2>

No changes with this release.<br>
<br>
<h2>Serene 0.5.3</h2>

No changes with this release.<br>
<br>
<h2>Serene 0.5.2</h2>

No changes with this release.<br>
<br>
<h2>Serene 0.5.1</h2>


Support is added for <code>param</code> elements. External datatypes may use <code>param</code> elements, but it is their responsibility to implement the <a href='http://www.oasis-open.org/committees/relax-ng/spec-20011203.html#constraints'>Relax NG Simplification 4.16.Constraints</a> rule. See samples source code for an example.<br>
<br>
<br>
<h2>Serene 0.5</h2>

This release supports the vendor independent API for RELAX NG datatype libraries developed by James Clark and Kohsuke Kawaguchi, <code>org.relaxng.datatypes</code>, so you can create your own libraries, pack them as jar files adding the necessary <code>META-INF/services/org.relaxng.datatype.DatatypeLibraryFactory</code>  file containing the names of your service providers for the <code>org.relaxng.datatype.DatatypeLibraryFactory</code> service, add them in the classpath, and they will be found dynamically at runtime. See samples for an example.