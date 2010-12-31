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

package serene.datatype;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.HashMap;

import org.relaxng.datatype.DatatypeLibrary;
import org.relaxng.datatype.DatatypeLibraryFactory;


public class DatatypeLibraryFinder implements DatatypeLibraryFactory{
	/** debug support code. */
    //private static boolean debug;
	
    /**
     *<p> Take care of restrictions imposed by java security model </p>
     */
    private static SecuritySupport ss = new SecuritySupport();    
	
	private static final Class SERVICE_CLASS = DatatypeLibraryFactory.class;
    private static final String SERVICE_ID = "META-INF/services/" + SERVICE_CLASS.getName();
	
	/** 
	* Map every type of repository to the corresponding factory instance.
	*/
	private HashMap<String, DatatypeLibraryFactory> factoryCache;
	
	/**
     * <p>Constructor that specifies <code>ClassLoader</code> to use
     * to find <code>SchemaFactory</code>.</p>
     * 
     * @param loader
     *      to be used to load resource, {@link SchemaFactory}, and
     *      {@link SchemaFactoryLoader} implementations during
     *      the resolution process.
     *      If this parameter is null, the default system class loader
     *      will be used.
     */
	public DatatypeLibraryFinder(ClassLoader loader){
		this.classLoader = loader;
		factoryCache = new HashMap<String, DatatypeLibraryFactory>();
		//debug = true;
        //if( debug ) {
        //    debugDisplayClassLoader();
        //}
	} 
	
	public DatatypeLibrary createDatatypeLibrary(String type){
		DatatypeLibraryFactory lrf = factoryCache.get(type);
		if(lrf != null)return lrf.createDatatypeLibrary(type);
		return _getDatatypeLibrary(type);	
	}
	
	    
    /**
     * <p>Conditional debug printing.</p>
     * 
     * @param msg to print
     */
    private static void debugPrintln(String msg) {
        //if (debug) {
        //    System.err.println("DatatypeLibraryFinder: " + msg);
        //}
    }
    
    /**
     * <p><code>ClassLoader</code> to use to find <code>SchemaFactory</code>.</p>
     */
    private final ClassLoader classLoader;
        
    
    private void debugDisplayClassLoader() {
        try {
            if( classLoader == ss.getContextClassLoader() ) {
                debugPrintln("using thread context class loader ("+classLoader+") for search");
                return;
            }
        } catch( Throwable _ ) {
            ; // getContextClassLoader() undefined in JDK1.1 
        }
        
        if( classLoader==ClassLoader.getSystemClassLoader() ) {
            debugPrintln("using system class loader ("+classLoader+") for search");
            return;
        }

        debugPrintln("using class loader ("+classLoader+") for search");
    }    
    
    
    /**
     * <p>Lookup a <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.</p>
     * 
     * @param schemaLanguage Schema language to lookup <code>SchemaFactory</code> for.
     *  
     * @return <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.
     */
    private DatatypeLibrary _getDatatypeLibrary(String type) {
        DatatypeLibrary literateRepository;
        		
        // try META-INF/services files
        Iterator sitr = createServiceFileIterator();
        while(sitr.hasNext()) {
            URL resource = (URL)sitr.next();
            debugPrintln("looking into " + resource);
            try {
                literateRepository = loadFromService(type, 
											resource.toExternalForm(),
											ss.getURLInputStream(resource));
                if(literateRepository!=null)    return literateRepository;
            } catch(IOException e) {
                //if( debug ) {
                //    debugPrintln("failed to read "+resource);
                //    e.printStackTrace();
                //}
            }
        }        
        
        debugPrintln("all things were tried, but none was found. bailing out.");
        return null;
    }
    
    /**
     * <p>Look up a value in a property file.</p>
     * 
     * <p>Set <code>debug</code> to <code>true</code> to trace property evaluation.</p>
     *
     * @param schemaLanguage Schema Language to support.
     * @param providerConfigFile Name of <code>InputStream</code>.
     * @param in <code>InputStream</code> of properties.
     * 
     * @return <code>SchemaFactory</code> as determined by <code>keyName</code> value or <code>null</code> if there was an error.
     * 
     * @throws IOException If IO error reading from <code>in</code>.
     */
    private DatatypeLibrary loadFromService(
            String type,
            String providerConfigFile,
            InputStream in)
            throws IOException {

            DatatypeLibraryFactory literateRepositoryFactory = null;
			DatatypeLibrary literateRepository = null;
			
            debugPrintln("Reading " + providerConfigFile);

            // read from InputStream until a match is found
            BufferedReader configFileReader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = configFileReader.readLine()) != null) {					
                    // '#' is comment char
                    int comment = line.indexOf("#");
                    switch (comment) {
                            case -1: break; // no comment
                            case 0: line = ""; break; // entire line is a comment
                            default: line = line.substring(0, comment); break; // trim comment
                    }
						
                    // trim whitespace
                    line = line.trim();
					debugPrintln("line "+line);
					
                    // any content left on line?
                    if (line.length() == 0) {
                            continue;
                    }

                    // line content is now the name of the class
                    Class clazz = createClass(line);					
					debugPrintln("line class "+clazz);
					
                    if (clazz == null) {
                            continue;
                    } 

                    // create an instance of the Class
                    try {
                            literateRepositoryFactory = (DatatypeLibraryFactory) clazz.newInstance();
                    } catch (ClassCastException classCastException) {
                            literateRepositoryFactory = null;
							debugPrintln("ClassCastException "+classCastException);
                            continue;
                    } catch (InstantiationException instantiationException) {
                            literateRepositoryFactory = null;
							debugPrintln("InstantiationException "+instantiationException);
                            continue;
                    } catch (IllegalAccessException IllegalAccessException) {
                            literateRepositoryFactory = null;
							debugPrintln("IllegalAccessException "+IllegalAccessException);
                            continue;
                    }
                   
					if(literateRepositoryFactory != null){
						literateRepository = literateRepositoryFactory.createDatatypeLibrary(type);						
					}                    					
					if(literateRepository != null) {
						factoryCache.put(type, literateRepositoryFactory);
						break;
					}
            }

            // clean up
            configFileReader.close();

            // return new instance of SchemaFactory or null
            return literateRepository;
    }

    /**
     * Returns an {@link Iterator} that enumerates all 
     * the META-INF/services files that we care.
     */
    private Iterator createServiceFileIterator() {
        if (classLoader == null) {
            return new SingleIterator() {
                protected Object value() {
                    ClassLoader classLoader = DatatypeLibraryFinder.class.getClassLoader();                   
                    return ss.getResourceAsURL(classLoader, SERVICE_ID);
                }
            };
        } else {
            try {
                //final Enumeration e = classLoader.getResources(SERVICE_ID);
                final Enumeration e = ss.getResources(classLoader, SERVICE_ID);
                if(!e.hasMoreElements()) {
                    debugPrintln("no "+SERVICE_ID+" file was found");
                }
                
                // wrap it into an Iterator.
                return new Iterator() {
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    public Object next() {
                        return e.nextElement();
                    }
                };
            } catch (IOException e) {
                debugPrintln("failed to enumerate resources "+SERVICE_ID);
                //if(debug)   e.printStackTrace();
                return new ArrayList().iterator();  // empty iterator
            }
        }
    }

	/** <p>Create class using appropriate ClassLoader.</p>
     * 
     * @param className Name of class to create.
     * @return Created class or <code>null</code>.
     */
    private Class createClass(String className) {
            Class clazz;
            // use approprite ClassLoader
            try {
                    if (classLoader != null) {
                            clazz = classLoader.loadClass(className);
                    } else {
                            clazz = Class.forName(className);
                    }
            } catch (Throwable t) {
                //if(debug)   t.printStackTrace();
                    return null;
            }

            return clazz;
    }
    
    /** Iterator that lazily computes one value and returns it. */
    private static abstract class SingleIterator implements Iterator {
        private boolean seen = false;
        
        public final void remove() { throw new UnsupportedOperationException(); }
        public final boolean hasNext() { return !seen; }
        public final Object next() {
            if(seen)    throw new NoSuchElementException();
            seen = true;
            return value();
        }
        
        protected abstract Object value();
    }    
}