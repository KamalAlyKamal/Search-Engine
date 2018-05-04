/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package searchengine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ahmos
 */
// handling the case www in the first 
public class HtmlTools {

    public static String fixUrl(String url) {
      
          
         // System.out.println( "host of "+ur.getHost());
         // to get the subdomains 

       
          if(url.contains("?"))
        {
             url = url.substring(0,url.indexOf('?'));
        }
        if(url.contains("#"))
        {
        url = url.substring(0, url.indexOf('#'));
        }

        if(url.endsWith("/")){
            
        url = url.substring(0,url.length()-1);
        }
        if(url.contains("www."))
        {
        url = url.replace("www.", "");
        }
          if(url.contains("ww."))
        {
        url = url.replace("ww.","");
        }
        if(url.contains("w."))
        {
        url = url.replace("w.","");
        }
        return url;
    }
     public static String absUrl(String urlString,String baseUrlString )  {
         
        if (urlString == null || urlString.trim().length() == 0) urlString = "";
        
        URL baseUrl = null ;
        try {
            if (!baseUrlString.contains("http://"))
                baseUrlString = "http://" +baseUrlString ;
            baseUrl = new URL(baseUrlString);
               URL url = null;
            try {
            url = new URL(baseUrl, urlString);
        urlString = url.toString().replaceAll("\\\\+", "/");
        try {
            url = new URL(urlString);
              String uri = url.getPath();
               
        String uriString = uri.replaceAll("/+", "/");
      uri = uri.replace("(", "/(").replace(")", "/)");
        uri = uri.replace("*", "/*");
        uri = uri.replace("&", "/&");
        uri = uri.replace("-", "/-");
        uri = uri.replace("+", "/+");
       
        try{
        urlString = url.toString().replaceAll(uri, uriString);
        }
        catch(Exception e)
        {
            return null ;
        }
        int index = urlString.indexOf("/..");
        if (index < 0) return fixUrl(urlString);
        String urlStringLeft = urlString.substring(0, index) + "/";
        String urlStringRight = urlString.substring(index + 1);
        
        return absUrl(urlStringLeft, urlStringRight);
        } catch (IOException ex) {
         //  Logger.getLogger(HtmlTools.class.getName()).log(Level.SEVERE, null, ex);
       //throw new UncheckedIOException (e);
        }
            
            }catch (IOException ex) {
           // Logger.getLogger(HtmlTools.class.getName()).log(Level.SEVERE, null, ex);
      // throw new UncheckedIOException (e);
        }
        } catch (IOException ex) {
           // Logger.getLogger(HtmlTools.class.getName()).log(Level.SEVERE, null, ex);
       // throw new UncheckedIOException (e);
        }
        return null;   
    }
     public static String absUrl(String url) {
       
            return absUrl(url, null);
       
    }
     
     public static String escapeMetaCharacters(String inputString){
   final String[] metaCharacters = {"^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&"};
    String outputString="";
    for (int i = 0 ; i < metaCharacters.length ; i++){
        if(inputString.contains(metaCharacters[i])){
            outputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            inputString = outputString;
        }
    }
    return fixUrl(outputString);
}
}
    
    
    

