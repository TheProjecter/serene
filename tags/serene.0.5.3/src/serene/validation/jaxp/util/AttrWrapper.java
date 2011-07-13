/*
Copyright 2011 Radu Cernuta 

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

package serene.validation.jaxp.util;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.UserDataHandler;

import sereneWrite.MessageWriter;

public class AttrWrapper implements Attr{
    Attr attr;
    boolean isSpecified;
    
    MessageWriter debugWriter;
    
    public AttrWrapper(Attr attr, boolean isSpecified, MessageWriter debugWriter){
        this.debugWriter = debugWriter;
        this.attr = attr;
        this.isSpecified = isSpecified;
    }
    
    public Node appendChild(Node newChild){
        return attr.appendChild(newChild);
    }
    public Node cloneNode(boolean deep){
        return attr.cloneNode(deep);
    }
    public short compareDocumentPosition(Node other){
        return attr.compareDocumentPosition(other);
    }
    public NamedNodeMap getAttributes(){
        return attr.getAttributes();
    }
    public String getBaseURI(){
        return attr.getBaseURI();
    }
    public NodeList getChildNodes(){
        return attr.getChildNodes();
    }
    public Object getFeature(String feature, String version){
        return attr.getFeature(feature, version);
    }
    public Node getFirstChild(){
        return attr.getFirstChild();
    }
    public Node getLastChild(){
        return attr.getLastChild();
    }
    public String getLocalName(){
        return attr.getLocalName();
    }
    public String getNamespaceURI(){
        return attr.getNamespaceURI();
    }
    public Node getNextSibling(){
        return attr.getNextSibling();
    }
    public String getNodeName(){
        return attr.getNodeName();
    }
    public short getNodeType(){
        return attr.getNodeType();
    }
    public String getNodeValue(){
        return attr.getNodeValue();
    }
    public Document getOwnerDocument(){
        return attr.getOwnerDocument();
    }
    public Node getParentNode(){
        return attr.getParentNode();
    }
    public String getPrefix(){
        return attr.getPrefix();
    }
    public Node getPreviousSibling(){
        return attr.getPreviousSibling();
    }
    public String getTextContent(){
        return attr.getTextContent();
    }
    public Object getUserData(String key){
        return attr.getUserData(key);
    }
    public boolean hasAttributes(){
        return attr.hasAttributes();
    }
    public boolean hasChildNodes(){
        return attr.hasChildNodes();
    }
    public Node insertBefore(Node newChild, Node refChild){
        return attr.insertBefore(newChild, refChild);
    }
    public boolean isDefaultNamespace(String namespaceURI){
        return attr.isDefaultNamespace(namespaceURI);
    }
    public boolean isEqualNode(Node arg){
        return attr.isEqualNode(arg);
    }
    public boolean isSameNode(Node other){
        return attr.isSameNode(other);
    }
    public boolean isSupported(String feature, String version){
        return attr.isSupported(feature, version);
    }
    public String lookupNamespaceURI(String prefix){
        return attr.lookupNamespaceURI(prefix);
    }
    public String lookupPrefix(String namespaceURI){
        return attr.lookupPrefix(namespaceURI);
    }
    public void normalize(){
        attr.normalize();
    }
    public Node removeChild(Node oldChild){
        return attr.removeChild(oldChild);
    }
    public Node replaceChild(Node newChild, Node oldChild){
        return attr.replaceChild(newChild, oldChild);
    }
    public void setNodeValue(String nodeValue){
        attr.setNodeValue(nodeValue);
    }
    public void setPrefix(String prefix){
        attr.setPrefix(prefix);
    }
    public void setTextContent(String textContent){
        attr.setTextContent(textContent);
    }
    public Object setUserData(String key, Object data, UserDataHandler handler){
        return attr.setUserData(key, data, handler);
    }
    
    
    public String getName(){
        return attr.getName();
    }
    public Element getOwnerElement(){
        return attr.getOwnerElement();
    }
    public TypeInfo getSchemaTypeInfo(){
        return attr.getSchemaTypeInfo();
    }
    public boolean getSpecified(){
        return isSpecified;
    }
    public String getValue(){
        return attr.getValue();
    }
    public boolean isId(){
        return attr.isId();
    }
    public void setValue(String value){
        attr.setValue(value);
    } 
}
