package com.riktamtech.android.chitti;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.util.Log;


public class XMLfunctions {

	public final static Document XMLfromString(String xml){
		
		Document doc = null;
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
        	
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));
	        doc = db.parse(is); 
	        
		} catch (ParserConfigurationException e) {
//			System.out.println("XML parse error: " + e.getMessage());
			return null;
		} catch (SAXException e) {
//			System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;
		} catch (IOException e) {
//			System.out.println("I/O exeption: " + e.getMessage());
			return null;
		}
		       
        return doc;
        
	}
	
	/** Returns element value
	  * @param elem element (it is XML tag)
	  * @return Element value otherwise empty String
	  */
	 public final static String getElementValue( Node elem ) {
	     Node kid;
	     if( elem != null){
	         if (elem.hasChildNodes()){
	             for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){
	                 if( kid.getNodeType() == Node.TEXT_NODE  ){
	                     return kid.getNodeValue();
	                 }
	             }
	         }
	     }
	     return "";
	 }
		 
	 public static String getXML(String link){	 
			String line = null;

			try {
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpGet httpPost = new HttpGet(link);

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				line = EntityUtils.toString(httpEntity);
//				System.out.println(line);
				
			} catch (UnsupportedEncodingException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			} catch (MalformedURLException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			} catch (IOException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			}

			return line;

	}
	 
	public static int numResults(Document doc){		
		NodeList nl = doc.getElementsByTagName("food");
//		Log.i("Number of tags", " "+nl.getLength());
		return nl.getLength();
	}

	public static String getValue(Element item, String str) {		
		NodeList n = item.getElementsByTagName(str);
		String s = null;
		if(n.item(0).getNodeType()== Node.TEXT_NODE )
			s = n.item(0).getNodeValue();
//		Log.i("Node Value", " "+s);
		return XMLfunctions.getElementValue(n.item(0));
	}
	
	public static String get_attribute_value(Node n)
	{
		 NamedNodeMap named_node_map=n.getAttributes();					
		Node data = named_node_map.getNamedItem("data");						       
        String value = data.getNodeValue();						       
//        System.out.println(value);						       
       return value;
	}
}
