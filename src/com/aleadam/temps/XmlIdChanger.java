package com.aleadam.temps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 

public class XmlIdChanger {

	private static Hashtable<String, String> table1;
	private static Hashtable<String, String> table2;
	private static int replacements;
	private static String fileIn, fileOut, xmlIn, xmlOut;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
        table1 = new Hashtable<String, String>();
        table2 = new Hashtable<String, String>();
        replacements = 0;
        
        if (args.length < 4)
        	helpAndQuit();
        
        fileIn = args[0];
        fileOut = args[1];
        xmlIn = args[2];
        xmlOut = args[3];
        
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc1 = docBuilder.parse (new File(xmlIn));
            Document doc2 = docBuilder.parse (new File(xmlOut));

            doc1.getDocumentElement ().normalize ();
            NodeList listOfIds1 = doc1.getElementsByTagName("public");
            int totalIds1 = listOfIds1.getLength();
            System.out.println("Total no of ids for 1st public.xml: " + totalIds1);
            for(int i=0; i<listOfIds1.getLength() ; i++){
                Node node = listOfIds1.item(i);
                NamedNodeMap nodeMap = node.getAttributes();
                for (int j=0; j<nodeMap.getLength(); j++){
                	String name = nodeMap.getNamedItem("name").getNodeValue().trim();
                	String id = nodeMap.getNamedItem("id").getNodeValue().trim();
                	table1.put(name, id);
                }//end of for loop with j var
            }//end of for loop with i var
            
            doc2.getDocumentElement ().normalize ();
            NodeList listOfIds2 = doc2.getElementsByTagName("public");
            int totalIds2 = listOfIds2.getLength();
            System.out.println("Total no of ids for 2nd public.xml: " + totalIds2);

            for(int i=0; i<listOfIds2.getLength() ; i++){
                Node node = listOfIds2.item(i);
                NamedNodeMap nodeMap = node.getAttributes();
                for (int j=0; j<nodeMap.getLength(); j++){
                	String name = nodeMap.getNamedItem("name").getNodeValue().trim();
                	String id = nodeMap.getNamedItem("id").getNodeValue().trim();
                	table2.put(name, id);
                }//end of for loop with j var
            }//end of for loop with i var
        }catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " 
             + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());
        }catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();
        }catch (Throwable t) {
        t.printStackTrace ();
        }
    	String line;
    	int ln=1;
        try {
        	Scanner scanner = new Scanner(new FileInputStream(fileIn));
        	Writer out = new OutputStreamWriter(new FileOutputStream(fileOut));

        	while (scanner.hasNextLine()){
        		line = scanner.nextLine();
        		if (line.contains("0x10") && !line.contains("->")) {
        			int index = line.indexOf("0x10");
        			if (line.length() >= index+9) {
        				String id = line.substring(index+2, index+9);
        				String key = findKeyName (id);
        			    String id2 = table2.get(key).substring(3);
        			    if (id2 == null) {
        			    	out.write(line + " # ----  ERROR HERE PLEASE CHECK MANUALLY\n");
        			    	System.err.println ("Error in file: " + fileIn);
        			    	System.err.println ("At line: " + ln + "  (id:" + id + ")");
        			    }
        			    else {
        			    	String line2 = new String (line.substring(0,index+2) + id2 + line.substring(index+9));
        			    	out.write (line2 + "\n");
        			    }
    			    	replacements++;
        			}
        			else
        				out.write (line + "\n");
        		}
        		else
        			out.write (line + "\n");
        		ln++;
        	}
        	System.out.println ("Made " + replacements + " replacements");
        	out.close();
        	scanner.close();
        }
        catch (Exception e) {
        	System.err.println (" *** Exception ***");
        	System.err.println (e.getMessage());
        	e.printStackTrace(System.err);
        	System.err.println ("File: " + fileIn + "\nAt line: " + ln);
        }
  }//end of main
	
	private static String findKeyName (String idValue) {
	    Set set = table1.entrySet();
	    Iterator it = set.iterator();
	    while (it.hasNext()) {
	        Map.Entry<String,String> entry = (Map.Entry<String,String>) it.next();
	        if (entry.getValue().contains(idValue))
	            return entry.getKey();
	    }
	    return null;
	}
	private static void helpAndQuit () {
		System.err.println ("Usage: XmlIdChanger fileIn fileOut xmlIn xmlOut");
		System.err.print ("Where fileIn is the original .smali file and xmlIn is the public.xml file from the corresponding framework");
		System.err.println ("and fileOut is the name for the .smali file that will be generated for the new framework using the");
		System.err.print("public.xml specified by xmlOut");
		System.exit(-1);
	}

}