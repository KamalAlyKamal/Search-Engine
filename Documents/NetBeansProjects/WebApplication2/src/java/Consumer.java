/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import searchengine.HtmlTools;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author ahmos
 */
public class Consumer {

    private Set<webPage> anchors = new HashSet<webPage>();
    private String page;
    private Connection connection;
    private Response response;
    private String userAgent;
    private Producer producer;

    public Consumer(String userAgent, Producer producer) {
        this.userAgent = userAgent;
        this.producer = producer;
    }

    public boolean Start(webPage crawlDomain) {

        try {

            // connecting to the website 
            connection = Jsoup.connect(crawlDomain.Url).userAgent(userAgent)
                    .referrer("http://www.google.com").ignoreHttpErrors(true);
            try {

                if (connection.execute().url().getHost() == null) {
                    return false; // if it has wrong host or it's host is null we will add it as bad link
                }
                response = connection.execute();
            } catch (IllegalArgumentException ex) {
                return false;
            }
            if (response.statusCode() % 100 == 4) {   // status code with 4xx will be added also as bad links so we return false if we have it 
                return false;
            }
            if (response.statusCode() == HttpURLConnection.HTTP_MOVED_PERM) {
                String url = response.header("location");
                url = HtmlTools.absUrl(url);
                webPage temp = new webPage();
                temp.setUrl(url);
                temp.setRank(crawlDomain.getRank());
                temp.setParentPages((LinkedList<webPage>) crawlDomain.getParentPages());
                temp.setChildPages(crawlDomain.getChildPages());
                anchors.add(temp);
                return false;

            }
           else if (response.statusCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                String url = response.header("location");
                url = HtmlTools.absUrl(url);
                webPage temp = new webPage();
                temp.setUrl(url);
                temp.setRank(crawlDomain.getRank());
                temp.setParentPages((LinkedList<webPage>) crawlDomain.getParentPages());
                temp.setChildPages(crawlDomain.getChildPages());
                anchors.add(temp);
                anchors.add(crawlDomain);
                return false;

            }
           else if (response.statusCode() == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
                anchors.add(crawlDomain);
                return false;

            }

            String contentType;

            contentType = response.contentType();
            String LastModified = response.header("Last-Modified");                // getting the last modified data for recarwling in the carteria 
            if (contentType == null) {
                return false;
            }

            if (contentType.contains("text/html")) {                                 // to make sure it is html document 
                Document doc;
                try {
                    doc = connection.get();
                } catch (Throwable e ) {
                    return false;
                }
              
                page = doc.html();
                if (page == "")
                    return false;

                Elements hrefs = doc.select("a");
                synchronized (this.producer.getCarawler()) {

                    for (Element e : hrefs) {
                        String anchor = e.attr("href").trim();
                        try {
                            anchor = HtmlTools.absUrl(anchor, crawlDomain.Url); //de 3lshan lw nafs el page teb2a et7t mara wa7da
                        } catch (Exception ee) {
                            anchor = null;
                        }
                        if (anchor == null) {
                            continue;
                        }
                        if (anchor != "" && anchor != null) {
                            webPage temp = new webPage();
                            temp.setUrl(anchor);
                            if (!(producer.getCarawler().updateVistedList(temp, crawlDomain))) {
                                temp.Rank = 0;
                                temp.ParentPages.add(crawlDomain);
                                anchors.add(temp);
                            }

                            /*  if( producer.getCarawler().getPagesVisited().contains()!= null)
                        producer.getCarawler().linksToPage.get(anchor).set(0, producer.getCarawler().linksToPage.get(anchor).get(0)+1);
                    else
                    {
                    producer.getCarawler().linksToPage.put(anchor, new ArrayList <Integer>());
                    producer.getCarawler().linksToPage.get(anchor).add(1);
                    producer.getCarawler().linksToPage.get(anchor).add(0);
                    
                    }*/
                        }/*
                     if( producer.getCarawler().linksToPage.get(crawlDomain)== null)
                     {
                     producer.getCarawler().linksToPage.put(crawlDomain, new ArrayList <Integer>());
                     
                     producer.getCarawler().linksToPage.get(crawlDomain).add(0);
                     producer.getCarawler().linksToPage.get(crawlDomain).add(hrefs.size());
                     }
                     else
                    producer.getCarawler().linksToPage.get(crawlDomain).set(1, hrefs.size());*/

                    }

                    crawlDomain.setLastModification(LastModified);
                    System.out.println(LastModified);
                }
                return true;
            } else {
                return false;
            }
        } catch (IOException ex) {
            return false;
            // 3lshan lw portocal 3'lt
        }

    }

    public Set<webPage> getLinks() {
        return this.anchors;
    }

    public String getpage() {
        return page;
    }

}
