Index: lib/int/sereneWrite.jar
===================================================================
Cannot display: file marked as a binary type.
svn:mime-type = application/octet-stream
Index: lib/int/sereneTest.jar
===================================================================
Cannot display: file marked as a binary type.
svn:mime-type = application/octet-stream
Index: lib/int/serene.jar
===================================================================
Cannot display: file marked as a binary type.
svn:mime-type = application/octet-stream
Index: src/serene/restrictor/RController.java
===================================================================
--- src/serene/restrictor/RController.java	(revision 71)
+++ src/serene/restrictor/RController.java	(working copy)
@@ -121,15 +121,14 @@
 	ErrorDispatcher errorDispatcher;
 	MessageWriter debugWriter;
 	
-	public RController(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
+	public RController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
 		this.errorDispatcher = errorDispatcher;
 		this.debugWriter = debugWriter;
+        this.pool = pool;
 				
 		handledDefinitions = new IntList();
 		definitionsContentTypes = new ObjectIntHashMap(debugWriter);
-        
-		pool = new ControllerPool(errorDispatcher, debugWriter);
-		
+        		
 		attributesPath = new Stack<SAttribute>();
 		morePath = new MorePath();
 		listsPath = new Stack<SListPattern>();
@@ -137,8 +136,8 @@
 		
 		texts = new SereneArrayList<SPattern>();
 	}
+		
 	
-	
 	public void control(SimplifiedModel simplifiedModel)throws SAXException{		
 		init(simplifiedModel);		
 		if(topPatterns != null && topPatterns.length != 0){//to catch situations where start element was missing
@@ -189,7 +188,7 @@
 		
 		contentType = ContentType.EMPTY;
 	}
-	void close() throws SAXException{		
+	void close() throws SAXException{
 		elementNamingController.control();
 		elementNamingController.recycle();
 		
@@ -249,7 +248,7 @@
 
 	//------------------
 	//  !!! subclass !!!
-	public void visit(SElement element) throws SAXException{		
+	public void visit(SElement element) throws SAXException{	
 		if(attributeContext){
 			// error 7.1.1	
 			String message = "Restrictions 7.1.1 error. Forbiden path:"
Index: src/serene/restrictor/RestrictionController.java
===================================================================
--- src/serene/restrictor/RestrictionController.java	(revision 71)
+++ src/serene/restrictor/RestrictionController.java	(working copy)
@@ -22,26 +22,49 @@
 import serene.validation.schema.simplified.RecursionModel;
 import serene.validation.handlers.error.ErrorDispatcher;
 
+import serene.Constants;
+
 import sereneWrite.MessageWriter;
 
 public class RestrictionController{
 	RRController rrController;
 	RController rController;
 		
+    ControllerPool controllerPool;
+    ErrorDispatcher errorDispatcher;
 	MessageWriter debugWriter;
 	
 	public RestrictionController(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){		
 		this.debugWriter = debugWriter;
-		
-		rrController = new RRController(errorDispatcher, debugWriter);
-		rController = new RController(errorDispatcher, debugWriter); 
+		this.errorDispatcher = errorDispatcher;
+        controllerPool = new ControllerPool(errorDispatcher, debugWriter); 
 	}
 	
 	public void control(SimplifiedModel simplifiedModel)throws SAXException{
 		if(simplifiedModel.hasRecursions()){
+            if(rrController == null) rrController = new RRController(controllerPool, errorDispatcher, debugWriter);
 			rrController.control(simplifiedModel);
 		}else{
+            if(rController == null) rController = new RController(controllerPool, errorDispatcher, debugWriter);
 			rController.control(simplifiedModel);
 		}
 	}
+    
+    public void setProperty(String name, Object object){
+		if (name == null) {
+            throw new NullPointerException();
+        }else if(name.equals(Constants.CONTROLLER_POOL_PROPERTY)){
+           // recognized but not set, only for retrieval
+        }        
+		throw new IllegalArgumentException("Unknown property.");
+	}    
+    
+	public Object getProperty(String name){
+		if (name == null) {
+            throw new NullPointerException();
+        }else if(name.equals(Constants.CONTROLLER_POOL_PROPERTY)){
+            return controllerPool;
+        }
+		throw new IllegalArgumentException("Unknown property.");
+	}
 }
\ No newline at end of file
Index: src/serene/restrictor/OverlapController.java
===================================================================
--- src/serene/restrictor/OverlapController.java	(revision 71)
+++ src/serene/restrictor/OverlapController.java	(working copy)
@@ -52,7 +52,7 @@
 
 import sereneWrite.MessageWriter; 
 
-class OverlapController implements SimplifiedComponentVisitor, Reusable{
+public class OverlapController implements SimplifiedComponentVisitor, Reusable{
 	
 	SNameClass test;
 	boolean overlap;
@@ -65,7 +65,7 @@
 	
 	MessageWriter debugWriter;
 	
-	OverlapController(ControllerPool pool, MessageWriter debugWriter){
+	public OverlapController(ControllerPool pool, MessageWriter debugWriter){
 		this.debugWriter = debugWriter;
 		this.pool = pool;
 		
@@ -86,7 +86,7 @@
 		pool.recycle(this);
 	}
 	
-	boolean overlap(SNameClass nc1, SNameClass nc2){
+	public boolean overlap(SNameClass nc1, SNameClass nc2){
 		this.test = nc1;
 		nc2.accept(this);
 		return overlap;
Index: src/serene/restrictor/ControllerPool.java
===================================================================
--- src/serene/restrictor/ControllerPool.java	(revision 71)
+++ src/serene/restrictor/ControllerPool.java	(working copy)
@@ -19,7 +19,7 @@
 import serene.validation.handlers.error.ErrorDispatcher;
 import sereneWrite.MessageWriter;
 
-class ControllerPool{
+public class ControllerPool{
 	ContentTypeController[] ctc;
 	int ctcFree;
 	int ctcSize;
Index: src/serene/restrictor/RRController.java
===================================================================
--- src/serene/restrictor/RRController.java	(revision 71)
+++ src/serene/restrictor/RRController.java	(working copy)
@@ -86,8 +86,8 @@
 	OpenAlternativesHandler openAlternativesHandler;
 	
 	
-	public RRController(ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
-		super(errorDispatcher, debugWriter);
+	public RRController(ControllerPool pool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
+		super(pool, errorDispatcher, debugWriter);
 		
 		loopElement = new ArrayList<BooleanStack>();
 		loopOptional = new ArrayList<BooleanStack>();
@@ -101,6 +101,7 @@
 		openAlternativesHandler = new OpenAlternativesHandler(debugWriter);
 	}
 	
+	
 	public void control(SimplifiedModel simplifiedModel) throws SAXException{
 		init(simplifiedModel);
 		
Index: src/serene/simplifier/Simplifier.java
===================================================================
--- src/serene/simplifier/Simplifier.java	(revision 71)
+++ src/serene/simplifier/Simplifier.java	(working copy)
@@ -98,17 +98,20 @@
 import serene.validation.schema.simplified.components.SNameClass;
 import serene.validation.schema.simplified.components.SPattern;
 import serene.validation.schema.simplified.components.SRef;
+import serene.validation.schema.simplified.components.SAttribute;
 
 import serene.internal.InternalRNGFactory;
 
 import serene.validation.handlers.error.ErrorDispatcher;
 
-import serene.Constants;
+import serene.util.AttributeInfo;
 
 import sereneWrite.MessageWriter;
 import sereneWrite.ParsedComponentWriter;
 
-abstract class Simplifier implements SimplifyingVisitor{
+import serene.Constants;
+
+abstract class Simplifier implements SimplifyingVisitor{	
 	Map<Grammar, Map<String, ArrayList<Definition>>> grammarDefinitions;	
 	Map<ExternalRef, URI> externalRefs;
 	Map<URI, ParsedModel> docParsedModels;
@@ -149,9 +152,11 @@
 	SimplifiedComponentBuilder builder;
 	
 	ErrorDispatcher errorDispatcher;
+
+    boolean level1AttributeDefaultValue;
+	boolean replaceMissingDatatypeLibrary;
+     
 		
-	boolean replaceMissingDatatypeLibrary;
-	
 	MessageWriter debugWriter;
 	ParsedComponentWriter pcw;
 	
@@ -163,12 +168,17 @@
 		pcw = new ParsedComponentWriter();
 		
 		replaceMissingDatatypeLibrary =  true;
+        level1AttributeDefaultValue  = false;
         
         paramStack = new Stack<ArrayList<Param>>();
 	}
 	
 	public abstract void setReplaceMissingDatatypeLibrary(boolean value);
-	
+	public void setCompatibilityAttributeDefaultValue(boolean level1AttributeDefaultValue){
+        this.level1AttributeDefaultValue = level1AttributeDefaultValue;
+    }
+    
+    
 	public void visit(Include include){
 		throw new IllegalStateException();
 	}
@@ -186,23 +196,23 @@
 		int emptyCount = 0;
 		builder.startLevel();        
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild) {
+                    allowedChildrenCount--;
+                    notAllowedChild = false;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }
 			else{
 				childrenCount--;
 				allowedChildrenCount--;				
 			}
-			if(notAllowedChild) {
-				allowedChildrenCount--;
-				notAllowedChild = false;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
 		}		
 		if(allowedChildrenCount == 0){
-			notAllowedChild = true;
-			emptyChild = false;	
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
@@ -212,15 +222,19 @@
 			builder.buildEmpty("empty", emptyComponent.getLocation());
 			allowedChildrenCount = allowedChildrenCount - emptyCount + 1;					
 		}
-		if(allowedChildrenCount > 1){
-			builder.endLevel();
-			builder.buildChoicePattern("children choice", exceptPattern.getLocation());
+        if(builder.getCurrentPatternsCount() > 1){
+			builder.buildReplacementChoicePattern("choice added by except simplification", exceptPattern.getLocation());
 		}
 		builder.endLevel();
 		builder.buildExceptPattern( exceptPattern.getQName(), exceptPattern.getLocation());
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
 	public void visit(ExceptNameClass exceptNameClass) throws SAXException{
+        ParsedComponent[] children = exceptNameClass.getChildren();
+        if(children == null) {
+			builder.buildExceptNameClass(exceptNameClass.getQName(), exceptNameClass.getLocation());
+			return;
+		}        
         Map<String, String> prefixMapping = exceptNameClass.getXmlns();
         if(prefixMapping != null) startXmlnsContext(prefixMapping);
         
@@ -231,12 +245,13 @@
 			nsNameExceptContext = true;
 		}
 		attributeContext = false;
-		ParsedComponent[] children = exceptNameClass.getChildren();
-		if(children != null) nextLevel(children);
-		if(builder.getContentNameClassesCount() > 1){
-			builder.buildChoiceNameClass("children choice", exceptNameClass.getLocation());
-			builder.endLevel();
+		
+        builder.startLevel();
+		next(children);		
+        if(builder.getCurrentNameClassesCount() > 1){
+			builder.buildReplacementChoiceNameClass("choice added by except simplification", exceptNameClass.getLocation());
 		}
+        builder.endLevel();
 		builder.buildExceptNameClass(exceptNameClass.getQName(), exceptNameClass.getLocation());
 		
 		anyNameExceptContext = false;	
@@ -317,7 +332,7 @@
         
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
-	public void visit(NsName nsName) throws SAXException{	
+	public void visit(NsName nsName) throws SAXException{
 		if(nsNameExceptContext){
 			// 4.16 error
 			String message = "Simplification 4.16 error. "
@@ -326,7 +341,7 @@
 			// TODO Q: do you need to build a dummy?
 			return;
 		}        
-		String ns = namespaceInheritanceHandler.getNsURI(nsName);				
+		String ns = namespaceInheritanceHandler.getNsURI(nsName);	
 		if(ns == null)ns ="";
 		if(attributeContext){			
 			if(ns.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)){
@@ -375,27 +390,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = define;
-			notAllowedChild = false;
             namespaceInheritanceHandler.endXmlnsContext(simplificationContext, define);
 			return;
 		}
@@ -421,7 +436,7 @@
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			builder.endLevel();
-			//notAllowedChild = true;
+			// notAllowedChild = true;
 			emptyChild = false;
             namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
 			return;
@@ -430,7 +445,7 @@
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			//emptyChild = true;
-			notAllowedChild = false;
+			//notAllowedChild = false;
             namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
 			return;
 		}		
@@ -440,7 +455,7 @@
         namespaceInheritanceHandler.endXmlnsContext(simplificationContext, start);
 	}
 		
-	public void visit(ElementWithNameClass element)  throws SAXException{	
+	public void visit(ElementWithNameClass element)  throws SAXException{
 		ParsedComponent[] children = element.getChildren();		
 		        		
 		if(children == null) {			
@@ -452,35 +467,31 @@
         if(prefixMapping != null) startXmlnsContext(prefixMapping);
         
 		int notAllowedCount = 0;
-		int emptyCount = 0;
         builder.startLevel();
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			if(notAllowedChild){
-				notAllowedCount++;
-				notAllowedChild = false;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){
+                    notAllowedCount++;
+                    notAllowedChild = false;
+                }
+                if(emptyChild){
+                    emptyChild = false;
+                }
+            }
 		}		
 		if(notAllowedCount > 0){
-			notAllowedChild = false;
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
-		}		
-		if(builder.getCurrentPatternsCount() - emptyCount > 1){
+		}        
+		if(builder.getCurrentPatternsCount() > 1){
 			builder.buildReplacementGroup("group added by element simplification", element.getLocation());
 		}
 		builder.endLevel();
 		builder.buildElement(element.getQName(), element.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
-        
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}	
 	public void visit(ElementWithNameInstance element)  throws SAXException{
@@ -495,28 +506,27 @@
         if(prefixMapping != null) startXmlnsContext(prefixMapping);
 		
 		int notAllowedCount = 0;
-		int emptyCount = 0;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			if(notAllowedChild){
-				notAllowedCount++;
-				notAllowedChild = false;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){
+                    notAllowedCount++;
+                    notAllowedChild = false;
+                }
+                if(emptyChild){
+                    emptyChild = false;
+                }
+            }
 		}		
 		if(notAllowedCount > 0){
-			notAllowedChild = false;
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
             
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
-		if(builder.getCurrentPatternsCount() - emptyCount > 1){
+		if(builder.getCurrentPatternsCount() > 1){
 			builder.buildReplacementGroup("group added by element simplification", element.getLocation());
 		}
 		String name = element.getName().trim();
@@ -530,24 +540,29 @@
 		
 		builder.endLevel();
 		builder.buildElement(element.getQName(), element.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
-        
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}	
 	public void visit(AttributeWithNameClass attribute)  throws SAXException{
 		ParsedComponent[] children = attribute.getChildren();		
 		
-       Map<String, String> prefixMapping = attribute.getXmlns();
+        Map<String, String> prefixMapping = attribute.getXmlns();
         if(prefixMapping != null) startXmlnsContext(prefixMapping);
-        		
-		if(children == null){
+                
+        if(children == null){
             builder.startLevel();
             builder.buildText("default text", attribute.getLocation());
-			builder.endLevel();           
-            builder.buildAttribute(attribute.getQName(), attribute.getLocation());
-            
+			builder.endLevel();
+            if(level1AttributeDefaultValue){
+                AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
+                if(foreignAttributes != null){
+                    builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
+                }else{
+                    builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+                }                
+            }else{
+                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+            }
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
             return;
         }
@@ -563,19 +578,27 @@
 			return;
 		}
 		if(emptyChild){
-		    builder.buildEmpty("empty", emptyComponent.getLocation());	
+            builder.buildEmpty("empty", emptyComponent.getLocation());
+            emptyChild = false;
 		}else if(builder.getCurrentPattern() == null){
             builder.buildText("default text", attribute.getLocation());            
-        }				
+        }       
+        
 		builder.endLevel();
-		builder.buildAttribute(attribute.getQName(), attribute.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
+		if(level1AttributeDefaultValue){
+            AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
+            if(foreignAttributes != null){
+                builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
+            }else{
+                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+            }                
+        }else{
+            builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+        }
         
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
-	public void visit(AttributeWithNameInstance attribute)  throws SAXException{				
+	public void visit(AttributeWithNameInstance attribute)  throws SAXException{
 		ParsedComponent[] children = attribute.getChildren();		
 		
         Map<String, String> prefixMapping = attribute.getXmlns();
@@ -594,7 +617,16 @@
 			if(ns == null)ns = "";		
 			builder.buildName(ns, localPart, "name", attribute.getLocation());
 			builder.endLevel();
-			builder.buildAttribute(attribute.getQName(), attribute.getLocation());
+			if(level1AttributeDefaultValue){
+                AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
+                if(foreignAttributes != null){
+                    builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
+                }else{
+                    builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+                }                
+            }else{
+                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+            }
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}				
@@ -607,8 +639,10 @@
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}
+		
 		if(emptyChild){
-			builder.buildEmpty("empty", emptyComponent.getLocation());
+            builder.buildEmpty("empty", emptyComponent.getLocation());
+            emptyChild = false;
 		}				
 		
 		String name = attribute.getName().trim();		
@@ -633,14 +667,20 @@
 		builder.buildName(ns, localPart, "name", attribute.getLocation());
 		
 		builder.endLevel();
-		builder.buildAttribute(attribute.getQName(), attribute.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;		
-        
+		if(level1AttributeDefaultValue){
+            AttributeInfo[] foreignAttributes = attribute.getForeignAttributes();
+            if(foreignAttributes != null){
+                builder.buildAttribute(getDefaultValue(foreignAttributes), attribute.getQName(), attribute.getLocation());
+            }else{
+                builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+            }                
+        }else{
+            builder.buildAttribute(null, attribute.getQName(), attribute.getLocation());
+        }
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
-	public void visit(ChoicePattern choice)  throws SAXException{				
+	public void visit(ChoicePattern choice)  throws SAXException{
 		ParsedComponent[] children = choice.getChildren();	
 		if(children == null){
 			builder.buildChoicePattern(choice.getQName(), choice.getLocation());
@@ -655,19 +695,20 @@
 		int emptyCount = 0;
 		builder.startLevel();
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null) next(children[i]);
-			else{
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){
+                    allowedChildrenCount--;
+                    notAllowedChild = false;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else{
 				childrenCount--;
 				allowedChildrenCount--;
-			}
-			if(notAllowedChild){
-				allowedChildrenCount--;
-				notAllowedChild = false;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			}			
 		}		
 		if(allowedChildrenCount == 0){						
 			builder.endLevel();
@@ -680,7 +721,6 @@
 		if(emptyCount == allowedChildrenCount){			
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-			notAllowedChild = false;
 			emptyChild = true;
 			emptyComponent = choice;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
@@ -691,9 +731,6 @@
 		}
 		builder.endLevel();
 		builder.buildChoicePattern(choice.getQName(), choice.getLocation());
-		
-		emptyChild = false;
-		notAllowedChild = false;
         
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
@@ -701,7 +738,7 @@
 		ParsedComponent[] children = interleave.getChildren();
 		
 		if(children == null) {
-			builder.buildInterleave(interleave.getQName(), interleave.getLocation());			
+			builder.buildInterleave(interleave.getQName(), interleave.getLocation());
 			return;
 		}
         
@@ -712,27 +749,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;                
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;                
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = interleave;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}	
@@ -740,16 +777,13 @@
 		builder.endLevel();
 		builder.buildInterleave(interleave.getQName(), interleave.getLocation());
 		
-		notAllowedChild = false;
-		emptyChild = false;		
-        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
 	public void visit(Group group)  throws SAXException{
 		ParsedComponent[] children = group.getChildren();
 		
 		if(children == null) {
-			builder.buildGroup(group.getQName(), group.getLocation());			
+			builder.buildGroup(group.getQName(), group.getLocation());
 			return;
 		}
         
@@ -760,44 +794,41 @@
 		int childrenCount = children.length;
 		builder.startLevel();
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = group;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}
 		
 		builder.endLevel();
 		builder.buildGroup(group.getQName(), group.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
-        
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
 	public void visit(ZeroOrMore zeroOrMore)  throws SAXException{
 		ParsedComponent[] children = zeroOrMore.getChildren();
 		
 		if(children == null) {
-			builder.buildZeroOrMore(zeroOrMore.getQName(), zeroOrMore.getLocation());			
+			builder.buildZeroOrMore(zeroOrMore.getQName(), zeroOrMore.getLocation());
 			return;
 		}
         
@@ -808,27 +839,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = zeroOrMore;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
@@ -837,9 +868,6 @@
 		}	
 		builder.endLevel();
 		builder.buildZeroOrMore(zeroOrMore.getQName(), zeroOrMore.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
         
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
@@ -847,7 +875,7 @@
 		ParsedComponent[] children = oneOrMore.getChildren();
 		
 		if(children == null) {
-			builder.buildOneOrMore(oneOrMore.getQName(), oneOrMore.getLocation());			
+			builder.buildOneOrMore(oneOrMore.getQName(), oneOrMore.getLocation());
 			return;
 		}
         
@@ -858,27 +886,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = oneOrMore;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
@@ -887,17 +915,14 @@
 		}		
 		builder.endLevel();
 		builder.buildOneOrMore(oneOrMore.getQName(), oneOrMore.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;
-        
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}
 	public void visit(Optional optional)  throws SAXException{
 		ParsedComponent[] children = optional.getChildren();
 		
 		if(children == null) {
-			builder.buildOptional(optional.getQName(), optional.getLocation());			
+			builder.buildOptional(optional.getQName(), optional.getLocation());
 			return;
 		}
         
@@ -908,29 +933,28 @@
 		int childrenCount = children.length;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
-			emptyComponent = optional;
-			notAllowedChild = false;
-            
+			emptyComponent = optional;            
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
@@ -939,17 +963,14 @@
 		}		
 		builder.endLevel();
 		builder.buildOptional(optional.getQName(), optional.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;		
-                
+		    
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}	
 	public void visit(ListPattern list)  throws SAXException{
 		ParsedComponent[] children = list.getChildren();
 		
 		if(children == null) {
-			builder.buildListPattern(list.getQName(), list.getLocation());			
+			builder.buildListPattern(list.getQName(), list.getLocation());
 			return;
 		}
         
@@ -960,27 +981,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			emptyChild = true;
 			emptyComponent = list;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
@@ -989,17 +1010,14 @@
 		}		
 		builder.endLevel();
 		builder.buildListPattern(list.getQName(), list.getLocation());
-		
-		notAllowedChild = false;
-		emptyChild = false;		
-        
+		        
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}	
 	public void visit(Mixed mixed)  throws SAXException{
 		ParsedComponent[] children = mixed.getChildren();
 		
 		if(children == null) {
-			builder.buildMixed(mixed.getQName(), mixed.getLocation());			
+			builder.buildMixed(mixed.getQName(), mixed.getLocation());
 			return;
 		}
         
@@ -1010,27 +1028,27 @@
 		int childrenCount = children.length;
 		builder.startLevel();//children
 		for(int i = 0; i < children.length; i++){
-			if(children[i] != null)next(children[i]);
-			else childrenCount--;
-			if(notAllowedChild){				
-				builder.endLevel();
-				builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
-				//notAllowedChild = true;
-				emptyChild = false;
-                if(prefixMapping != null) endXmlnsContext(prefixMapping);
-				return;
-			}
-			if(emptyChild){
-				emptyCount++;
-				emptyChild = false;
-			}
+			if(children[i] != null){
+                next(children[i]);
+                if(notAllowedChild){				
+                    builder.endLevel();
+                    builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
+                    //notAllowedChild = true;
+                    emptyChild = false;
+                    if(prefixMapping != null) endXmlnsContext(prefixMapping);
+                    return;
+                }
+                if(emptyChild){
+                    emptyCount++;
+                    emptyChild = false;
+                }
+            }else childrenCount--;			
 		}		
 		if(emptyCount == childrenCount){
 			builder.endLevel();
 			builder.clearContent();//why? Because SRef is built anyway, even if notAllowed or empty.
 			builder.buildText("default text", mixed.getLocation());
 			emptyChild = false;
-			notAllowedChild = false;
             if(prefixMapping != null) endXmlnsContext(prefixMapping);
 			return;
 		}		
@@ -1040,8 +1058,6 @@
 		builder.endLevel();
 		builder.buildMixed(mixed.getQName(), mixed.getLocation());
 		
-		notAllowedChild = false;
-		emptyChild = false;		
         if(prefixMapping != null) endXmlnsContext(prefixMapping);
 	}	
 	
@@ -1090,9 +1106,9 @@
 		index = definitionTopPatterns.size();
 		
 		definitionTopPatterns.add(null);
-		definitionEmptyChild.add(false);
+		definitionEmptyChild.add(false);        
 		definitionNotAllowedChild.add(false);
-		
+        
 		referencePath.push(index);
 		indexes.put(docTopPattern, index);
 		
@@ -1112,7 +1128,7 @@
             return;
         }
 		builder.buildRef(index, externalRef.getQName(), externalRef.getLocation());
-        if(prefixMapping != null) endXmlnsContext(prefixMapping);		
+        if(prefixMapping != null) endXmlnsContext(prefixMapping);	
 	}
 	public void visit(Ref ref) throws SAXException{
 		ArrayList<Definition> definitions = getReferencedDefinition(currentGrammar, ref.getName().trim());
@@ -1170,12 +1186,13 @@
 			indexes,
 			referencePath,
 			definitionEmptyChild,
-			definitionNotAllowedChild,	
+			definitionNotAllowedChild,
 			recursionModel,			
 			currentGrammar,
 			previousGrammars,
             simplificationContext);
 		
+        ds.setCompatibilityAttributeDefaultValue(true);
 		ds.simplify(definitions);
 		
 		SPattern topPattern = ds.getCurrentPattern();
@@ -1249,12 +1266,13 @@
 			indexes,
 			referencePath,
 			definitionEmptyChild,
-			definitionNotAllowedChild,	
+			definitionNotAllowedChild,
 			recursionModel,			
 			currentGrammar,
 			previousGrammars,
             simplificationContext);
 		
+        ds.setCompatibilityAttributeDefaultValue(true);
 		ds.simplify(definitions);
 		
 		SPattern topPattern = ds.getCurrentPattern();
@@ -1266,6 +1284,7 @@
 		notAllowedChild = ds.getNotAllowedChild();
 		definitionNotAllowedChild.set(index, notAllowedChild);
 		
+        
 		referencePath.pop();
 		
 		if(emptyChild || notAllowedChild){
@@ -1273,7 +1292,7 @@
             return;
         }
 		builder.buildRef(index, parentRef.getQName(), parentRef.getLocation());
-        if(prefixMapping != null) endXmlnsContext(prefixMapping);		
+        if(prefixMapping != null) endXmlnsContext(prefixMapping);	
 	}
 	public void visit(Value value) throws SAXException{
         Map<String, String> prefixMapping = value.getXmlns();
@@ -1506,12 +1525,12 @@
 			indexes,
 			referencePath,
 			definitionEmptyChild,
-			definitionNotAllowedChild,	
+			definitionNotAllowedChild,
 			recursionModel,			
 			currentGrammar,
 			previousGrammars,
             simplificationContext);
-		
+		ds.setCompatibilityAttributeDefaultValue(true);
 		ds.simplify(start);		
 		
 		notAllowedChild = ds.getNotAllowedChild();
@@ -1545,7 +1564,7 @@
 		ParsedComponent[] children = dummy.getChildren();
 		
 		if(children == null) {
-			builder.buildDummy(dummy.getQName(), dummy.getLocation());			
+			builder.buildDummy(dummy.getQName(), dummy.getLocation());
 			return;
 		}
         
@@ -1660,5 +1679,17 @@
         for(String prefix : prefixes){
             simplificationContext.endPrefixMapping(prefix);
         }
+    }
+
+    private String getDefaultValue(AttributeInfo[] foreignAttributes){
+        for(AttributeInfo attributeInfo : foreignAttributes){
+            String uri = attributeInfo.getNamespaceURI(); 
+            String ln = attributeInfo.getLocalName();
+            if( uri != null && uri.equals(Constants.DTD_COMPATIBILITY_ANNOTATIONS_NAMESPACE)
+                && ln != null && ln.equals(Constants.DTD_COMPATIBILITY_DEFAULT_VALUE)) 
+            return attributeInfo.getValue();
+        }
+    
+        return null;
     }	
 }
Index: src/serene/simplifier/RNGSimplifier.java
===================================================================
--- src/serene/simplifier/RNGSimplifier.java	(revision 71)
+++ src/serene/simplifier/RNGSimplifier.java	(working copy)
@@ -116,6 +116,8 @@
         
         simplificationContext.reset();
 		
+        paramStack.clear();
+        
 		this.topPattern = topPattern;
 		inclusionPath.push(base);
 		docParsedModels.put(base, parsedModel);
Index: src/serene/simplifier/DefinitionSimplifier.java
===================================================================
--- src/serene/simplifier/DefinitionSimplifier.java	(revision 71)
+++ src/serene/simplifier/DefinitionSimplifier.java	(working copy)
@@ -88,7 +88,7 @@
 			ObjectIntHashMap indexes,
 			IntStack referencePath,
 			BooleanList definitionEmptyChild,
-			BooleanList definitionNotAllowedChild,	
+			BooleanList definitionNotAllowedChild,
 			RecursionModel recursionModel,			
 			Grammar currentGrammar,
 			Stack<Grammar> previousGrammars,
@@ -106,13 +106,13 @@
 		this.indexes = indexes;	
 		this.referencePath = referencePath;
 		this.definitionEmptyChild = definitionEmptyChild;
-		this.definitionNotAllowedChild = definitionNotAllowedChild;	
+		this.definitionNotAllowedChild = definitionNotAllowedChild;
 		this.recursionModel = recursionModel;
 	
 		this.currentGrammar = currentGrammar;
 		this.previousGrammars = previousGrammars;
         
-        this.simplificationContext = simplificationContext;
+        this.simplificationContext = simplificationContext;      
 	}
 	
 	public void recycle(){
@@ -133,7 +133,7 @@
 		
 		pool.recycle(this);
 	}
-	void simplify(ArrayList<Definition> definitions) throws SAXException{
+	void simplify(ArrayList<Definition> definitions) throws SAXException{        
 		emptyChild = false;
 		notAllowedChild = false;
 		
@@ -142,11 +142,13 @@
 		nsNameContext = false;
 		nsNameExceptContext = false;
 		attributeContext = false;
-		
+        
 		combine = null;
-		nullCombine.clear();
+		nullCombine.clear();        
 		if(otherCombine != null) otherCombine.clear();
 		
+        paramStack.clear();
+         
 		builder.startBuild();		
 		
 		for(int i = 0; i < definitions.size(); i++){
@@ -234,5 +236,5 @@
 	
 	boolean getNotAllowedChild(){
 		return notAllowedChild;
-	}
+	}    
 }
\ No newline at end of file
Index: src/serene/dtdcompatibility/AttributeDefaultValueException.java
===================================================================
--- src/serene/dtdcompatibility/AttributeDefaultValueException.java	(revision 0)
+++ src/serene/dtdcompatibility/AttributeDefaultValueException.java	(revision 0)
@@ -0,0 +1,35 @@
+/*
+Copyright 2011 Radu Cernuta 
+
+Licensed under the Apache License, Version 2.0 (the "License");
+you may not use this file except in compliance with the License.
+You may obtain a copy of the License at
+
+http://www.apache.org/licenses/LICENSE-2.0
+
+Unless required by applicable law or agreed to in writing, software
+distributed under the License is distributed on an "AS IS" BASIS,
+WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+See the License for the specific language governing permissions and
+limitations under the License.
+*/
+
+package serene.dtdcompatibility;
+
+import org.xml.sax.Locator;
+
+public class AttributeDefaultValueException extends DTDCompatibilityException{
+
+	public AttributeDefaultValueException(String message, Locator locator){
+		super(message, locator);
+	}
+	public AttributeDefaultValueException(String message, Locator locator, Exception e){
+		super(message, locator, e);
+	}
+	public AttributeDefaultValueException(String message, String publicId, String systemId, int lineNumber, int columnNumber){
+		super(message, publicId, systemId, lineNumber, columnNumber);
+	}
+	public AttributeDefaultValueException(String message, String publicId, String systemId, int lineNumber, int columnNumber, Exception e){
+		super(message, publicId, systemId, lineNumber, columnNumber, e);
+	} 
+}
Index: src/serene/dtdcompatibility/DTDCompatibilityException.java
===================================================================
--- src/serene/dtdcompatibility/DTDCompatibilityException.java	(revision 0)
+++ src/serene/dtdcompatibility/DTDCompatibilityException.java	(revision 0)
@@ -0,0 +1,37 @@
+/*
+Copyright 2011 Radu Cernuta 
+
+Licensed under the Apache License, Version 2.0 (the "License");
+you may not use this file except in compliance with the License.
+You may obtain a copy of the License at
+
+http://www.apache.org/licenses/LICENSE-2.0
+
+Unless required by applicable law or agreed to in writing, software
+distributed under the License is distributed on an "AS IS" BASIS,
+WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+See the License for the specific language governing permissions and
+limitations under the License.
+*/
+
+package serene.dtdcompatibility;
+
+import org.xml.sax.Locator;
+
+import serene.SereneRecoverableException;
+
+public class DTDCompatibilityException extends SereneRecoverableException{
+
+	public DTDCompatibilityException(String message, Locator locator){
+		super(message, locator);
+	}
+	public DTDCompatibilityException(String message, Locator locator, Exception e){
+		super(message, locator, e);
+	}
+	public DTDCompatibilityException(String message, String publicId, String systemId, int lineNumber, int columnNumber){
+		super(message, publicId, systemId, lineNumber, columnNumber);
+	}
+	public DTDCompatibilityException(String message, String publicId, String systemId, int lineNumber, int columnNumber, Exception e){
+		super(message, publicId, systemId, lineNumber, columnNumber, e);
+	} 
+}
Index: src/serene/dtdcompatibility/CompatibilityHandler.java
===================================================================
--- src/serene/dtdcompatibility/CompatibilityHandler.java	(revision 0)
+++ src/serene/dtdcompatibility/CompatibilityHandler.java	(revision 0)
@@ -0,0 +1,557 @@
+/*
+Copyright 2011 Radu Cernuta 
+
+Licensed under the Apache License, Version 2.0 (the "License");
+you may not use this file except in compliance with the License.
+You may obtain a copy of the License at
+
+http://www.apache.org/licenses/LICENSE-2.0
+
+Unless required by applicable law or agreed to in writing, software
+distributed under the License is distributed on an "AS IS" BASIS,
+WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+See the License for the specific language governing permissions and
+limitations under the License.
+*/
+
+package serene.dtdcompatibility;
+
+import java.util.Arrays;
+import java.util.Stack;
+import java.util.ArrayList;
+
+import org.xml.sax.SAXException;
+
+import org.relaxng.datatype.Datatype;
+
+import serene.util.BooleanList;
+
+import serene.validation.BaseSchema;
+
+import serene.validation.schema.simplified.SimplifiedModel;
+import serene.validation.schema.simplified.RestrictingVisitor;
+
+import serene.validation.schema.simplified.components.SPattern;
+import serene.validation.schema.simplified.components.SNameClass;
+
+import serene.validation.schema.simplified.components.SExceptPattern;
+import serene.validation.schema.simplified.components.SExceptNameClass;
+
+import serene.validation.schema.simplified.components.SElement;
+import serene.validation.schema.simplified.components.SAttribute;
+import serene.validation.schema.simplified.components.SChoicePattern;
+import serene.validation.schema.simplified.components.SInterleave;
+import serene.validation.schema.simplified.components.SGroup;
+import serene.validation.schema.simplified.components.SZeroOrMore;
+import serene.validation.schema.simplified.components.SOneOrMore;
+import serene.validation.schema.simplified.components.SOptional;
+import serene.validation.schema.simplified.components.SListPattern;
+import serene.validation.schema.simplified.components.SEmpty;
+import serene.validation.schema.simplified.components.SText;
+import serene.validation.schema.simplified.components.SNotAllowed;
+import serene.validation.schema.simplified.components.SRef;
+import serene.validation.schema.simplified.components.SData;
+import serene.validation.schema.simplified.components.SValue;
+import serene.validation.schema.simplified.components.SGrammar;
+import serene.validation.schema.simplified.components.SMixed;
+import serene.validation.schema.simplified.components.SDummy;
+
+import serene.validation.schema.simplified.components.SName;
+import serene.validation.schema.simplified.components.SAnyName;
+import serene.validation.schema.simplified.components.SNsName;
+import serene.validation.schema.simplified.components.SChoiceNameClass;
+
+import serene.validation.schema.simplified.SimplifiedModel;
+import serene.validation.schema.simplified.RecursionModel;
+import serene.validation.schema.simplified.SimplifiedComponent;
+import serene.validation.schema.simplified.RestrictingVisitor;
+
+import serene.validation.schema.active.ActiveModelPool;
+import serene.validation.schema.active.ActiveModel;
+import serene.validation.schema.active.ActiveGrammarModel;
+
+
+import serene.validation.schema.active.Rule;
+import serene.validation.schema.active.components.AAttribute;
+import serene.validation.schema.active.components.AElement;
+import serene.validation.schema.active.components.APattern;
+import serene.validation.schema.active.components.AValue;
+import serene.validation.schema.active.components.AData;
+import serene.validation.schema.active.components.CharsActiveTypeItem;
+import serene.validation.schema.active.components.DatatypedActiveTypeItem;
+
+
+import serene.validation.handlers.content.CharactersEventHandler;
+import serene.validation.handlers.content.impl.ContentHandlerPool;
+import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
+
+import serene.validation.handlers.content.CharactersEventHandler;
+
+import serene.validation.handlers.error.ErrorDispatcher;
+import serene.validation.handlers.error.ErrorCatcher;
+import serene.validation.handlers.error.ValidatorErrorHandlerPool;
+import serene.validation.handlers.error.AttributeDefaultValueErrorHandler;
+
+import serene.validation.handlers.content.DefaultValueAttributeHandler;
+import serene.validation.handlers.content.impl.ValidatorEventHandlerPool;
+import serene.validation.handlers.content.util.ValidationItemLocator;
+
+import serene.restrictor.ControllerPool;
+
+import sereneWrite.MessageWriter;
+
+public class CompatibilityHandler implements RestrictingVisitor{
+    SPattern[] startTopPattern;
+	SPattern[] refDefinitionTopPattern;
+    RecursionModel recursionModel;
+    
+    boolean level1AttributeDefaultValue;
+    
+    CompatibilityControlAttribute ccAttribute;
+    DefaultValueAttributeHandler defaultValueHandler;
+    
+    ActiveGrammarModel grammarModel;
+    
+    ValidatorErrorHandlerPool errorHandlerPool;
+    ValidatorEventHandlerPool eventHandlerPool;
+    ValidationItemLocator validationItemLocator;
+        
+    ErrorDispatcher errorDispatcher;
+
+    AttributeDefaultValueErrorHandler defaultValueErrorHandler;   
+
+    boolean defaultedAttributeContent; // for element level handling
+    boolean defaultedAttributeContext; // for datatype    
+    boolean controlAlternative;// for choice handling 
+    boolean hasAlternative;// for choice handling
+    boolean hasName; // for name class control
+    
+    ControllerPool controllerPool;
+    CompetitionSimetryController simetryController;
+    Stack<ArrayList<SAttribute>> attributeListsStack;   
+    ArrayList<SAttribute> currentAttributesList;
+    Stack<BooleanList> isRequiredBranchStack;
+    BooleanList isRequiredBranch;
+    Stack<BooleanList> needsOptionalChoiceStack;
+    BooleanList needsOptionalChoice;  
+    
+    MessageWriter debugWriter;
+    
+    public CompatibilityHandler(ControllerPool controllerPool,
+                                ValidatorErrorHandlerPool errorHandlerPool,
+                                ValidatorEventHandlerPool eventHandlerPool,
+                                ValidationItemLocator validationItemLocator,
+                                ErrorDispatcher errorDispatcher, 
+                                MessageWriter debugWriter){
+        this.debugWriter = debugWriter;
+        this.controllerPool = controllerPool;
+        this.errorHandlerPool = errorHandlerPool;
+        this.eventHandlerPool = eventHandlerPool;
+        this.validationItemLocator = validationItemLocator;        
+        this.errorDispatcher = errorDispatcher; 
+        defaultValueErrorHandler = new AttributeDefaultValueErrorHandler(errorDispatcher, debugWriter);
+        simetryController = new CompetitionSimetryController(controllerPool, errorDispatcher, debugWriter);
+        attributeListsStack = new Stack<ArrayList<SAttribute>>();
+        isRequiredBranchStack = new Stack<BooleanList>();
+        needsOptionalChoiceStack = new Stack<BooleanList>();
+    }
+    
+    
+    public void setCompatibilityAttributeDefaultValue(boolean value){
+        level1AttributeDefaultValue = value;
+        if(level1AttributeDefaultValue){
+            if(ccAttribute == null) ccAttribute = new CompatibilityControlAttribute(debugWriter);
+            if(defaultValueHandler == null) defaultValueHandler = eventHandlerPool.getDefaultValueAttributeValidationHandler();  
+        }
+    }
+    
+    public void handle(BaseSchema schema) throws SAXException{        
+        if(schema == null)return;
+        SimplifiedModel simplifiedModel = schema.getSimplifiedModel();
+        ActiveModelPool activeModelPool = schema.getActiveModelPool();
+        ActiveModel activeModel = activeModelPool.getActiveModel(validationItemLocator, errorDispatcher);
+        grammarModel = activeModel.getGrammarModel();
+        ccAttribute.init(grammarModel,
+                        activeModel.getStackHandlerPool(),
+                        activeModel.getRuleHandlerPool());
+                
+        startTopPattern = simplifiedModel.getStartTopPattern();
+        refDefinitionTopPattern = simplifiedModel.getRefDefinitionTopPattern();
+        recursionModel = simplifiedModel.getRecursionModel();
+        
+        if(level1AttributeDefaultValue){        
+            simetryController.clear();
+            attributeListsStack.clear();
+            isRequiredBranchStack.clear();
+            needsOptionalChoiceStack.clear();
+            
+            currentAttributesList = new ArrayList<SAttribute>();            
+            isRequiredBranch = new BooleanList();
+            needsOptionalChoice = new BooleanList();
+            defaultedAttributeContent = false;
+            defaultedAttributeContext = false;    
+            controlAlternative = false; 
+            hasAlternative = false;
+            hasName = false;                
+        }
+        for(SPattern start : startTopPattern){
+            start.accept(this);
+        }        
+    }
+    
+    public void visit(SExceptPattern exceptPattern)throws SAXException{
+		SimplifiedComponent child = exceptPattern.getChild();
+        if(child == null) return;
+        if(controlAlternative){
+            controlAlternative = false;
+        }
+		child.accept(this);        
+	}
+	public void visit(SExceptNameClass exceptNameClass)throws SAXException{
+		SimplifiedComponent child = exceptNameClass.getChild();
+		if(child != null) child.accept(this);
+        hasName = false;
+	}
+		
+	public void visit(SName component){
+        hasName = true;
+    }
+	public void visit(SAnyName anyName)throws SAXException{
+        hasName = false;
+	}
+	public void visit(SNsName nsName)throws SAXException{
+        hasName = false;
+	}
+	public void visit(SChoiceNameClass choice)throws SAXException{
+        hasName = false;
+	}	
+	
+	
+	public void visit(SElement element)throws SAXException{
+        SimplifiedComponent child = element.getChild();
+        if(child == null){
+            ArrayList<SAttribute> attributesList = new ArrayList<SAttribute>();
+            simetryController.control(element, attributesList);           
+            return;
+        }
+        
+        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
+        boolean defaultedAttributeContentMemo = defaultedAttributeContent;
+        if(level1AttributeDefaultValue){
+            attributeListsStack.push(currentAttributesList);
+            currentAttributesList = new ArrayList<SAttribute>();
+            
+            isRequiredBranchStack.push(isRequiredBranch);
+            isRequiredBranch = new BooleanList();
+            
+            needsOptionalChoiceStack.push(needsOptionalChoice);
+            needsOptionalChoice = new BooleanList();
+            
+            defaultedAttributeContent = false;
+            defaultedAttributeContext = false;    
+            controlAlternative = false; 
+            hasAlternative = false;
+        } 
+            
+        child.accept(this);
+        //see about this: only necessary when compatibility       
+        if(level1AttributeDefaultValue){
+            if(defaultedAttributeContent){                
+                hasName = false;
+                SimplifiedComponent nameClass = element.getNameClass();
+                if(nameClass != null) nameClass.accept(this);
+                String attributes = "";
+                if(!hasName){                    
+                    for(int i = 0; i < currentAttributesList.size(); i++){
+                        SAttribute attr = currentAttributesList.get(i);
+                        if(attr.getDefaultValue() != null){
+                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
+                        }
+                    }                                        
+                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the content model of an element definition without a <name> name class, <"+element.getQName()+"> at "+element.getLocation()+" :"+attributes+".";
+                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                }
+                
+                attributes = "";
+                for(int i = 0; i < isRequiredBranch.size(); i++){
+                    if(isRequiredBranch.get(i)){
+                        SAttribute attr = currentAttributesList.get(i);
+                        attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
+                    }
+                }
+                
+                if(!attributes.equals("")){                    
+                    String message = "DTD compatibility error. Default value not allowed for required attribute definitions in the content model of <"+element.getQName()+"> at "+element.getLocation()+" :"
+                                    +attributes+".";
+                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                }
+            }                   
+            simetryController.control(element, currentAttributesList);
+            currentAttributesList = attributeListsStack.pop();
+            
+            isRequiredBranch = isRequiredBranchStack.pop();
+            needsOptionalChoice = needsOptionalChoiceStack.pop();
+            
+            defaultedAttributeContext = defaultedAttributeContextMemo;
+            defaultedAttributeContent = defaultedAttributeContentMemo;
+        }
+	}	
+	public void visit(SAttribute attribute)throws SAXException{
+        currentAttributesList.add(attribute);
+        		
+		SimplifiedComponent[] children = attribute.getChildren();
+        
+        if(children == null) return;        
+		if(children.length > 1) return; // syntax error
+        
+        String defaultValue = null;
+        boolean defaultedAttributeContextMemo = defaultedAttributeContext;
+        if(level1AttributeDefaultValue){
+            
+            defaultValue = attribute.getDefaultValue();
+            if(defaultValue != null){                
+                isRequiredBranch.add(true);
+                needsOptionalChoice.add(true);
+                
+                ccAttribute.init(grammarModel.getIndex(attribute), attribute.getQName(), attribute.getLocation());
+                defaultValueErrorHandler.setAttribute(attribute.getQName(), attribute.getLocation());                
+                defaultValueHandler.init(ccAttribute, defaultValueErrorHandler);
+                defaultValueHandler.handleAttribute(defaultValue);
+                defaultValueHandler.reset();
+                                
+                defaultedAttributeContext = true;    
+                controlAlternative = false; 
+                hasAlternative = false;
+                hasName = false;
+            }else{
+                isRequiredBranch.add(false);
+                needsOptionalChoice.add(false);
+            }
+        }
+        
+        //see that this is done only if necessary
+        children[0].accept(this);
+        
+        if(level1AttributeDefaultValue){
+            if(defaultedAttributeContext){
+                SNameClass nc = attribute.getNameClass();
+                nc.accept(this);                
+                if(!hasName){
+                    String message = "DTD compatibility error. Default value not allowed for an attribute definition without a <name> name class, <"+attribute.getQName()+"> at "+attribute.getLocation()+" .";
+                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                }                
+                defaultedAttributeContent = true;
+            }
+            
+            defaultValueErrorHandler.report();
+            defaultValueErrorHandler.clear();            
+            defaultedAttributeContext = defaultedAttributeContextMemo;
+        }     
+	}
+	public void visit(SChoicePattern choice)throws SAXException{
+		SimplifiedComponent[] children = choice.getChildren();
+        if(children == null)return;
+        
+        int attributesOffset;        
+        
+        if(level1AttributeDefaultValue){
+            boolean defaultedAttributeContentMemo = defaultedAttributeContent;            
+            attributesOffset = currentAttributesList.size();
+            hasAlternative = false;
+            for(SimplifiedComponent child : children){
+                if(!hasAlternative)controlAlternative = true;
+                else controlAlternative = false;
+                child.accept(this);
+            }
+            if(defaultedAttributeContent){
+                if(hasAlternative){
+                    for(int i = attributesOffset; i < isRequiredBranch.size(); i++){
+                        isRequiredBranch.set(i, false);
+                    }
+                }else{
+                    String attributes = "";
+                    for(;attributesOffset < needsOptionalChoice.size(); attributesOffset++){
+                        if(needsOptionalChoice.get(attributesOffset)){
+                            SAttribute attr = currentAttributesList.get(attributesOffset);
+                            attributes += "\n<"+attr.getQName()+"> at "+attr.getLocation();
+                            
+                            isRequiredBranch.set(attributesOffset, false);//needs no more checking, an error was reported already
+                        }
+                    }
+                    String message = "DTD compatibility error. Default value not allowed for attribute definitions in the context of a choice without <empty> alternative, <"+choice.getQName()+"> at "+choice.getLocation()+" :"+attributes+".";
+                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                }
+            }else{
+                defaultedAttributeContent = defaultedAttributeContentMemo;
+            }
+        }else{
+            for(SimplifiedComponent child : children){
+                child.accept(this); 
+            }
+        }
+	}
+	public void visit(SInterleave interleave)throws SAXException{
+		SimplifiedComponent[] children = interleave.getChildren();
+        if(children == null)return;
+        
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+        for(SimplifiedComponent child : children){
+            child.accept(this); 
+        }           
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}
+	public void visit(SGroup group)throws SAXException{
+		SimplifiedComponent[] children = group.getChildren();		
+        if(children == null) return;
+        
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+        for(SimplifiedComponent child : children){
+            child.accept(this); 
+        }
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}
+	public void visit(SZeroOrMore zeroOrMore)throws SAXException{
+		SimplifiedComponent child = zeroOrMore.getChild();
+        if(child == null) return;
+        
+        boolean controlAlternativeMemo = false;
+        int attributesOffset = currentAttributesList.size();
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		child.accept(this);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+        for(;attributesOffset < isRequiredBranch.size(); attributesOffset++){
+            isRequiredBranch.set(attributesOffset, false);
+        }
+	}
+	public void visit(SOneOrMore oneOrMore)throws SAXException{
+		SimplifiedComponent child = oneOrMore.getChild();
+        if(child == null) return;
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		child.accept(this);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}
+	public void visit(SOptional optional)throws SAXException{
+		SimplifiedComponent child = optional.getChild();
+        if(child == null) return;
+        
+        int attributesOffset = currentAttributesList.size();
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		child.accept(this);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+        for(;attributesOffset < isRequiredBranch.size(); attributesOffset++){
+            isRequiredBranch.set(attributesOffset, false);
+        }
+	}
+	public void visit(SMixed mixed)throws SAXException{
+		SimplifiedComponent child = mixed.getChild();
+        if(child == null) return;
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		child.accept(this);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}	
+	public void visit(SListPattern list)throws SAXException{
+		SimplifiedComponent child = list.getChild();
+        if(child == null) return;
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		child.accept(this);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}	
+	public void visit(SEmpty empty){
+        if(controlAlternative){
+            hasAlternative = true;
+        }
+    }
+	public void visit(SText text){
+    }
+	public void visit(SNotAllowed notAllowed){}
+	public void visit(SRef ref)throws SAXException{
+        if(recursionModel.isRecursiveReference(ref)){
+            return;
+        }
+        int index = ref.getDefinitionIndex();        
+        SPattern defTopPattern = refDefinitionTopPattern[index];
+        if(defTopPattern != null)defTopPattern.accept(this);
+    }
+	public void visit(SValue value)throws SAXException{
+        if(defaultedAttributeContext){
+            Datatype datatype = value.getDatatype();
+            if(datatype.isContextDependent()){
+                defaultValueErrorHandler.contextDependentDatatypeError(value);
+            }            
+        }        
+    }
+	public void visit(SData data)throws SAXException{	
+		if(defaultedAttributeContext){
+            Datatype datatype = data.getDatatype();
+            if(datatype.isContextDependent()){
+                defaultValueErrorHandler.contextDependentDatatypeError(data);
+            }            
+        }
+	}	
+	public void visit(SGrammar grammar)throws SAXException{
+		SimplifiedComponent child = grammar.getChild();
+		if(child != null) child.accept(this);
+	}
+    
+    public void visit(SDummy dummy)throws SAXException{
+		SimplifiedComponent[] children = dummy.getChildren();
+        if(children == null) return;
+        boolean controlAlternativeMemo = false;
+        if(controlAlternative){
+            controlAlternativeMemo = controlAlternative;
+            controlAlternative = false;
+        }
+		next(children);
+        if(controlAlternativeMemo){
+            controlAlternative = controlAlternativeMemo;
+        }
+	}
+		
+		
+	protected void next(SimplifiedComponent[] children)throws SAXException{
+		for(SimplifiedComponent child : children){            
+			child.accept(this);
+		}
+	} 
+}
Index: src/serene/dtdcompatibility/CompetitionSimetryController.java
===================================================================
--- src/serene/dtdcompatibility/CompetitionSimetryController.java	(revision 0)
+++ src/serene/dtdcompatibility/CompetitionSimetryController.java	(revision 0)
@@ -0,0 +1,141 @@
+/*
+Copyright 2011 Radu Cernuta 
+
+Licensed under the Apache License, Version 2.0 (the "License");
+you may not use this file except in compliance with the License.
+You may obtain a copy of the License at
+
+http://www.apache.org/licenses/LICENSE-2.0
+
+Unless required by applicable law or agreed to in writing, software
+distributed under the License is distributed on an "AS IS" BASIS,
+WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+See the License for the specific language governing permissions and
+limitations under the License.
+*/
+
+package serene.dtdcompatibility;
+
+import java.util.ArrayList;
+
+import org.xml.sax.SAXException;
+
+import serene.validation.schema.simplified.components.SElement;
+import serene.validation.schema.simplified.components.SAttribute;
+import serene.validation.schema.simplified.components.SNameClass;
+
+import serene.validation.handlers.error.ErrorDispatcher;
+
+import serene.restrictor.ControllerPool;
+import serene.restrictor.OverlapController;
+
+import sereneWrite.MessageWriter;
+
+class CompetitionSimetryController{
+    ArrayList<ElementRecord> records;
+    OverlapController overlapController;
+    
+    ErrorDispatcher errorDispatcher;
+    
+    MessageWriter debugWriter;
+    
+    CompetitionSimetryController(ControllerPool controllerPool, ErrorDispatcher errorDispatcher, MessageWriter debugWriter){
+        this.debugWriter = debugWriter;
+        this.errorDispatcher = errorDispatcher;
+        records = new ArrayList<ElementRecord>();
+        overlapController = new OverlapController(controllerPool, debugWriter);
+    }
+    
+    void clear(){
+        records.clear();
+    }
+    
+    void control(SElement element, ArrayList<SAttribute> attributes) throws SAXException{
+        SNameClass nameClass = element.getNameClass();        
+        for(ElementRecord record : records){
+            SNameClass recordNameClass = record.element.getNameClass();
+            if(overlapController.overlap(nameClass, recordNameClass)){
+                for(SAttribute attribute : attributes){
+                    String defaultValue = attribute.getDefaultValue();
+                    boolean foundCorrespondent = false;
+                    SNameClass attributeNC = attribute.getNameClass();
+                    for(SAttribute recordAttribute : record.attributes){
+                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
+                        if(attributeNC.equals(recordAttributeNC)){
+                            String recordDefaultValue = recordAttribute.getDefaultValue();
+                            foundCorrespondent = true;
+                            if(defaultValue == null){                                
+                                if(recordDefaultValue == null){                                    
+                                    break; // attribute handling done, move to next
+                                }else{
+                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
+                                    +"\n<"+element.getQName()+"> at "+element.getLocation()+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation()+" without default value;"
+                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation()+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation()+" and default value \""+recordDefaultValue+"\".";
+                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                                    break; // attribute handling done, move to next
+                                }
+                            }else{
+                                if(recordDefaultValue != null && recordDefaultValue.equals(defaultValue)){
+                                    break; // attribute handling done, move to next 
+                                }else if(recordDefaultValue == null){
+                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
+                                    +"\n<"+element.getQName()+"> at "+element.getLocation()+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation()+" and default value \""+defaultValue+"\";"
+                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation()+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation()+" without default value .";
+                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                                    break; // attribute handling done, move to next
+                                }else{
+                                    String message = "DTD compatibility error. Competing element definitions contain attribute definitions with the same name and different default values:"
+                                    +"\n<"+element.getQName()+"> at "+element.getLocation()+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation()+" and default value \""+defaultValue+"\";"
+                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation()+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation()+" and default value \""+recordDefaultValue+"\".";
+                                    errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                                    break; // attribute handling done, move to next                                    
+                                }
+                            }
+                        }
+                    }
+                    if(!foundCorrespondent){
+                        String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
+                                +"\n<"+element.getQName()+"> at "+element.getLocation()+" with attribute definition <"+attribute.getQName()+"> at "+attribute.getLocation()
+                                +"\n<"+record.element.getQName()+"> at "+record.element.getLocation()+" without corresponding attribute definition.";
+                        errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                    }                    
+                }
+                for(SAttribute recordAttribute : record.attributes){
+                    String defaultValue = recordAttribute.getDefaultValue();                                        
+                    if(defaultValue != null){
+                        boolean foundCorrespondent = false;
+                        SNameClass recordAttributeNC = recordAttribute.getNameClass();
+                        for(SAttribute attribute : attributes){
+                            SNameClass attributeNC = attribute.getNameClass();
+                            if(attributeNC.equals(recordAttributeNC)){
+                                foundCorrespondent = true;// already handled previously
+                            }
+                        }
+                        if(!foundCorrespondent){
+                            String message = "DTD compatibility error. Competing element definitions without corresponding attribute definitions with default values:"
+                                    +"\n<"+element.getQName()+"> at "+element.getLocation()+" without corresponding attribute definition."
+                                    +"\n<"+record.element.getQName()+"> at "+record.element.getLocation()+" with attribute definition <"+recordAttribute.getQName()+"> at "+recordAttribute.getLocation();
+                            errorDispatcher.error(new AttributeDefaultValueException(message, null));
+                        }
+                    }
+                }
+            }
+        }
+        ElementRecord er = new ElementRecord(element, attributes);
+        records.add(er);        
+    }
+    
+    class ElementRecord{
+        SElement element;
+        ArrayList<SAttribute> attributes;
+        
+        ElementRecord(SElement element, ArrayList<SAttribute> attributes){
+            this.element = element;
+            this.attributes = attributes;
+        }
+
+        public String toString(){
+            return element.toString()+" "+attributes.toString();
+        }        
+    }
+}
Index: src/serene/dtdcompatibility/CompatibilityControlAttribute.java
===================================================================
--- src/serene/dtdcompatibility/CompatibilityControlAttribute.java	(revision 0)
+++ src/serene/dtdcompatibility/CompatibilityControlAttribute.java	(revision 0)
@@ -0,0 +1,51 @@
+/*
+Copyright 2011 Radu Cernuta 
+
+Licensed under the Apache License, Version 2.0 (the "License");
+you may not use this file except in compliance with the License.
+You may obtain a copy of the License at
+
+http://www.apache.org/licenses/LICENSE-2.0
+
+Unless required by applicable law or agreed to in writing, software
+distributed under the License is distributed on an "AS IS" BASIS,
+WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
+See the License for the specific language governing permissions and
+limitations under the License.
+*/
+
+package serene.dtdcompatibility;
+
+import serene.validation.schema.active.components.AAttribute;
+import serene.validation.schema.active.ActiveGrammarModel;
+
+import serene.validation.handlers.structure.impl.ActiveModelRuleHandlerPool;
+import serene.validation.handlers.stack.impl.ActiveModelStackHandlerPool;
+
+import sereneWrite.MessageWriter;
+
+class CompatibilityControlAttribute extends AAttribute{
+    
+    CompatibilityControlAttribute(MessageWriter debugWriter){
+            super(-1, null, null, null, null, null, debugWriter);                
+    }
+    
+    void init(ActiveGrammarModel grammarModel,
+				ActiveModelStackHandlerPool stackHandlerPool,
+				ActiveModelRuleHandlerPool ruleHandlerPool){
+        this.grammarModel = grammarModel;
+        this.stackHandlerPool = stackHandlerPool;
+        this.ruleHandlerPool = ruleHandlerPool;
+    }
+    
+    void init(int index, String qName, String location){
+        this.qName = qName;
+        this.location = location;
+        this.index = index;
+    }
+    
+    public String toString(){
+		String s = "CompatibilityControlAttribute "+getNameClass()+" "+index+ " min "+minOccurs+" max "+maxOccurs;		
+		return s;
+	}
+}
