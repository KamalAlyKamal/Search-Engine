/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;

/**
 *
 * @author ahmos
 */
class Carawler {
    //this is the links visited

    Set<webPage> pagesVisited = new HashSet<webPage>();
    //this is the links is going to be visited
    Set<webPage> pagesToVisit = new HashSet<webPage>();
    // html pages of the visited links 
    // List<HtmlPage> pages = new LinkedList<HtmlPage>(); //da ana haselooooo we ha5leha gowa el pages  
    //storing the rank of each page 
    //  HashMap<String,ArrayList<Integer>> linksToPage=  new HashMap<String,ArrayList<Integer>>();
    HashMap<String, ArrayList> robotTxtFiles = new HashMap<String, ArrayList>();
         public HashMap <String,Integer> domaindepth = new HashMap<String,Integer>();

    public HashMap<String, Integer> getDomaindepth() {
        return domaindepth;
    }

    public void setDomaindepth(HashMap<String, Integer> domaindepth) {
        this.domaindepth = domaindepth;
    }

    int numberOfPages = 1500;

    //   public List<HtmlPage> getPages() {
    //     return pages;
    //}
    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    // public void setPages(List<HtmlPage> pages) {
    //    this.pages = pages;
    //}
    public HashMap<String, ArrayList> getRobotTxtFiles() {
        return robotTxtFiles;
    }

    public Set<webPage> getPagesVisited() {
        return pagesVisited;
    }

    public void setPagesVisited(Set<webPage> pagesVisited) {
        this.pagesVisited = pagesVisited;
    }

    public Set<webPage> getPagesToVisit() {
        return pagesToVisit;
    }

    public void setPagesToVisit(Set<webPage> pagesToVisit) {
        this.pagesToVisit = pagesToVisit;
    }

    public boolean isInVisited(webPage link) {
        for (webPage w : pagesVisited) {
            if (w.equals(link)) {
                System.out.println("this link comes before : " + link.Url);
                return true;
            }

        }
        return false;
    }
    

    public boolean updateVistedList(webPage link, webPage parent) {

        for (webPage w : pagesVisited) {
            if (w.getUrl() != null && link != null) {
                if (w.getUrl().equals(link.getUrl())) {
                    w.getParentPages().add(parent);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean updateToVistedList(webPage link, webPage parent) {

        for (webPage w : pagesToVisit) {
            if (w.getUrl().equals(link.getUrl())) {
                w.getParentPages().add(parent);
                return true;
            }
        }
        return false;
    }

    void CalculateRank() {
       
         for(int i =0 ; i<50 ;i++)
        for (webPage w : this.pagesVisited) {   
            w.setrealpop();
        }
    }
    
     static void  StartCarawling() throws InterruptedException
    {
  
    }
}

public class SearchEngine {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
       
        
      

Scanner reader = new Scanner(System.in);  // Reading from System.in
System.out.println("Enter a number: ");
int n = reader.nextInt(); // Scans the next token of the input as an int.
//once finished
reader.close();
        
    //  while(true)
      {
        SQLiteDBHelper sqlManager = new SQLiteDBHelper(1,"",1);
        Carawler carawler = new Carawler();
       //sqlManager.Backup();
      sqlManager.Restore();
        carawler.setPagesToVisit(sqlManager.GetPagesToVisit());
        carawler.setPagesVisited(sqlManager.GetPagesVisited());
        carawler.setNumberOfPages(carawler.getNumberOfPages()+carawler.getPagesVisited().size());  // to update the new number we want to reach as if we first carwal 5000 next the number will be 100000
        System.out.println("carawel start with  "+carawler.getPagesToVisit().size());
        System.out.println(carawler.getPagesVisited().size());
        for (int i=0 ; i < n ; i++)
        {
        Thread producer = new Thread(new Producer(carawler));
        producer.start();
        }
        /*
        Thread producer = new Thread(new Producer(carawler));
        Thread producer2 = new Thread(new Producer(carawler));
        Thread producer3 = new Thread(new Producer(carawler));
        Thread producer4 = new Thread(new Producer(carawler));
        producer.start();
        producer2.start();
        producer3.start();
        producer4.start();
        */
        int count = carawler.getPagesVisited().size()+ 3000;
        int index1 = 0;
        int index2 = 0;
        while (true) {
          
         /*     synchronized (carawler)
            {
            if ( carawler.getPagesVisited().size()%100 == 0 )
            {
                System.out.println("<------------------------updating ranking --------------------->");
          
            carawler.CalculateRank();
            }
            else 
                                  carawler.notifyAll();
            }
*/
            synchronized (carawler) {
               //   carawler.CalculateRank();
                if (carawler.getPagesVisited().size() >= carawler.getNumberOfPages()) {
                    System.out.println("====================================main is going to finish every thing right now============================");
                    carawler.CalculateRank();
                    List<webPage> listtovisit = new ArrayList<>(carawler.getPagesToVisit());
                    if(index1 >carawler.getPagesToVisit().size())
                        index1 =carawler.getPagesToVisit().size();
                    Set<webPage> subSet = new HashSet<>(listtovisit.subList(index1, carawler.getPagesToVisit().size() ));
                    index1 = carawler.getPagesToVisit().size();
                    List<webPage> listvisited = new ArrayList<>(carawler.getPagesVisited());
                    Set<webPage> subSet2 = new HashSet<>(listvisited.subList(index2, carawler.getPagesVisited().size() ));
                    index2 = carawler.getPagesVisited().size();
                    //insert into database and backup we nebreak 
                
                    try {
                        sqlManager.UpdatePagesToVisit(subSet2);
                        sqlManager.UpdateContentInLinksCountOutLinksCount(carawler.getPagesVisited());
                        sqlManager.UpdateRanks(carawler.getPagesVisited());
                        System.out.println("robots size"+carawler.getRobotTxtFiles().size());
                        sqlManager.InsertRobots(carawler.getRobotTxtFiles());
                        sqlManager.InsertPagesToVisitBatch(subSet);
                        sqlManager.Resetvistied();
                     
                    } catch (SQLException ex) {
                        Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    sqlManager.Backup();
                    System.out.println("================== FINISHED LOADING ======================");
                    break;
                }
                if (carawler.getPagesVisited().size() > count) {
                    // peridodic insert
                      System.out.println("================== PERIODIC INSERT ======================");
                   //  carawler.CalculateRank();    
                    List<webPage> listtovisit = new ArrayList<>(carawler.getPagesToVisit());
                    Set<webPage> subSet = new HashSet<>(listtovisit.subList(index1, carawler.getPagesToVisit().size() ));
                    index1 = carawler.getPagesToVisit().size();
                    List<webPage> listvisited = new ArrayList<>(carawler.getPagesVisited());
                    Set<webPage> subSet2 = new HashSet<>(listvisited.subList(index2, carawler.getPagesVisited().size() ));
                    index2 = carawler.getPagesVisited().size();

                    try {
                           sqlManager.UpdatePagesToVisit(subSet2);
                        sqlManager.InsertRobots(carawler.robotTxtFiles);
                        sqlManager.InsertPagesToVisitBatch(subSet);
                     
                        
                    } catch (SQLException ex) {
                        Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    count += 2500;
                    sqlManager.Backup();
                     System.out.println("====================================================== PERIODIC INSERT ==================================");
                    
                } else {
                     System.out.println("i am going to sleeep dudeees");
                     carawler.notifyAll();
                    carawler.wait();
                }
            }
        }
        
        }
      }
    }
          

    //}


