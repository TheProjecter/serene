# Limitations #
<br>

<h2>Serene 0.6.3</h2>

In this stage of the development there are still some limitations:<br>
<br>
<ul><li>overlapping name classes in <code>attribute</code>s or <code>element</code>s in the context of a <code>group</code> that has multiple cardinality and is in the context of an <code>interleave</code> are not supported. Example:</li></ul>

<pre><code>    &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
    &lt;grammar xmlns="http://relaxng.org/ns/structure/1.0"&gt;<br>
     &lt;start&gt;<br>
       &lt;element name = "root"&gt;<br>
         &lt;interleave&gt;<br>
           &lt;oneOrMore&gt;<br>
               &lt;element name = "aa"&gt;<br>
                 &lt;empty/&gt;<br>
               &lt;/element&gt;<br>
               &lt;element name = "bb"&gt;<br>
                 &lt;empty/&gt;<br>
               &lt;/element&gt;<br>
               &lt;element name = "aa"&gt;<br>
                 &lt;empty/&gt;<br>
               &lt;/element&gt;<br>
           &lt;/oneOrMore&gt;<br>
           &lt;element name = "cc"&gt;<br>
             &lt;empty/&gt;<br>
           &lt;/element&gt;<br>
         &lt;/interleave&gt;<br>
       &lt;/element&gt;<br>
     &lt;/start&gt;<br>
   &lt;/grammar&gt;<br>
</code></pre>

Attempting to use such a schema configuration would result in an error message similar to those issued when a restriction rule is violated.<br>
<br>
<ul><li><code>group</code> or <code>interleave</code> with multiple cardinality in the context of an <code>interleave</code> with multiple cardinality are not supported. Example:</li></ul>

<pre><code>    &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
    &lt;grammar xmlns="http://relaxng.org/ns/structure/1.0"&gt;<br>
     &lt;start&gt;<br>
       &lt;element name = "root"&gt;<br>
         &lt;oneOrMore&gt;<br>
           &lt;interleave&gt;<br>
             &lt;oneOrMore&gt;<br>
               &lt;element name = "aa"&gt;<br>
                 &lt;empty/&gt;<br>
               &lt;/element&gt;<br>
               &lt;element name = "bb"&gt;<br>
                 &lt;empty/&gt;<br>
               &lt;/element&gt;<br>
             &lt;/oneOrMore&gt;<br>
             &lt;element name = "cc"&gt;<br>
               &lt;empty/&gt;<br>
             &lt;/element&gt;<br>
           &lt;/interleave&gt;<br>
         &lt;/oneOrMore&gt;<br>
       &lt;/element&gt;<br>
     &lt;/start&gt;<br>
   &lt;/grammar&gt;<br>
</code></pre>

Attempting to use such a schema configuration would result in an error message similar to those issued when a restriction rule is violated.<br>
<br>
<ul><li><code>empty</code> in the context of <code>attribute</code>  is not supported. Example:</li></ul>

<pre><code>     &lt;?xml version="1.0" encoding="UTF-8"?&gt;<br>
    &lt;grammar xmlns="http://relaxng.org/ns/structure/1.0"&gt;<br>
     &lt;start&gt;<br>
       &lt;element name = "root"&gt;         <br>
         &lt;attribute name = "attribute"&gt;<br>
           &lt;empty/&gt;<br>
         &lt;/attribute&gt;<br>
       &lt;/element&gt;<br>
     &lt;/start&gt;<br>
   &lt;/grammar&gt;<br>
</code></pre>

Attempting to use such a schema configuration would result in an error message similar to those issued when a restriction rule is violated.<br>
<br>
<ul><li>compact syntax is not supported.</li></ul>

<h3>Note on XML Schema Datatypes Implementation</h3>

The XML Schema datatypes are implemented by wrapping Apache Xerces2 Java. It comes with version 2.10.0 which has the following limitations:<br>
<br>
<ul><li>length, minLength, and maxLength facets are limited to the value 2147483647. Items larger than this limit will not be validated correctly.<br>
</li><li>The values of minimum and maximum quantifiers in the pattern regular expressions are limited to the value 2147483647. Items larger than this limit will not be validated correctly.<br>
</li><li>The absolute values of the fraction portion of the second values in date/time datatypes are limited to the value 2147483647. Items larger than this limit will not be validated correctly. i.e. 11.2147483648 is not supported as a second value.<br>
</li><li>Leap seconds are not supported in the values of date/time datatypes.</li></ul>

Also in the original XML Schema recommendation, the lexical space of the simple type gMonth is --MM--. An erratum E2-12 changed that to --MM. For compatibility reasons, Xerces currently supports both forms. To align with the recommendation, the old form is deprecated, and support for that form is planed to be discontinued in a future release.<br>
<br>
<h2>Serene 0.6.2</h2>

All of the above.<br>
<br>
<h2>Serene 0.6.1</h2>

All of the above.<br>
<br>
<h2>Serene 0.6</h2>

All of the above.<br>
<br>
<h2>Serene 0.5.3</h2>

All of the above.<br>
<br>
<h2>Serene 0.5.2</h2>

All of the above.<br>
<br>
<h2>Serene 0.5.1</h2>

All of the above plus:<br>
<ul><li>DTD compatibility is not supported</li></ul>

<h2>Serene 0.5</h2>

All of the above plus:<br>
<ul><li>only the RELAX NG native datatypes are supported