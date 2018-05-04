/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package searchengine;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
/**
 *
 * @author Ahmed
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        Ranker x = new Ranker ("\"Business Delivery\"");
        x.getSearchResult(0);
        x.DisplayAll( x.getSearchResult(0) );
        
        System.out.println("Done...");
        
        
        //String y = "\"John an Likes a a a , , a +Food\"";
        //Ranker x = new Ranker(y);
        //x.doFunction();
        
        /*
        Indexer2 indexerEngine = new Indexer2();    //Indexer
        SQLiteDBHelper sqlManagerCrawler = new SQLiteDBHelper(0,"backup.db",1); //Connection to crawler Database
        SQLiteDBHelper sqlManagerIndexer = new SQLiteDBHelper(1,"",0);  //Connection to Indexer Database
        //sqlManagerIndexer.BackupIndexer();
        sqlManagerIndexer.RestoreIndexer();
        
        sqlManagerIndexer.InitializeIndexerTables();
        
        int LastIndexCounter = 0;
        int newIndexCounter;
        int IDFcalculateTimer = 5;
        
       // sqlManagerIndexer.CalculateIDF();
       // sqlManagerIndexer.BackupIndexer();
        
        while(true)
        {
            System.out.println("INDEXER MAIN: Reading a new batch of urls !!!");
            newIndexCounter  = indexerEngine.TakeWordInfo(sqlManagerCrawler.GetIndexerPageInfo(LastIndexCounter));  //Query
            
            if(newIndexCounter != -1)
                LastIndexCounter += newIndexCounter;
            
            if(newIndexCounter == -1)   //Means nothing new to take
            {
                try {
                    System.out.println("INDEXER MAIN: Nothing to read. Gonna go sleep");
                    TimeUnit.MINUTES.sleep(1);  //Wait for 1 minutes be4 trying again
                    System.out.println("INDEXER MAIN: I woke up! Time to work !!");
                } catch (InterruptedException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                sqlManagerIndexer.InsertWords(indexerEngine.getOriginalWordRank(), indexerEngine.getIndexerFullInfo());
                
                if(IDFcalculateTimer == 0)
                {
                    sqlManagerIndexer.CalculateIDF();
                    IDFcalculateTimer = 5;
                }
                else
                    IDFcalculateTimer--;
                
                
                sqlManagerIndexer.BackupIndexer();
                
                //System.out.println("\n\n");
                //indexerEngine.DisplayRank();
                //System.out.println("\n\n");
                //indexerEngine.DisplayOriginalWord();
            
                indexerEngine.EmptyIndexer();
            }
            
            
        }
        */
        
    }
    
}
