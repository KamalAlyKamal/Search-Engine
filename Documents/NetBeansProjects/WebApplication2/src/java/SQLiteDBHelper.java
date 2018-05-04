/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 package searchengine;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author lenovo
 */
public final class SQLiteDBHelper 
{
        Connection c = null;
        
        int count = 0; //used for updating pages
        
        public SQLiteDBHelper(int memory, String path,int isCrawler)
        {
            //Starts connection with in-memory DB
            StartConnection(memory,path);
            try
            {
                Statement stmt = c.createStatement();
                String query;
                if (isCrawler == 1)
                {
                    //Creating Crawler Tables
                    query = "CREATE TABLE IF NOT EXISTS Documents(ID INTEGER PRIMARY KEY AUTOINCREMENT, URL TEXT UNIQUE, Content TEXT, RANK FLOAT DEFAULT 0, InLinksCount INT DEFAULT 0, OutLinksCount INT DEFAULT 0, Visited TINYINT);";
                    stmt.execute(query);
                    //query = "CREATE TABLE IF NOT EXISTS InLinks(ID INTEGER PRIMARY KEY AUTOINCREMENT, URL TEXT, urlID INTEGER REFERENCES Documents(ID) ON DELETE CASCADE);";
                    //stmt.execute(query);
                    //query = "CREATE TABLE IF NOT EXISTS OutLinks(ID INTEGER PRIMARY KEY AUTOINCREMENT, URL TEXT, urlID INTEGER REFERENCES Documents(ID) ON DELETE CASCADE);";
                    //stmt.execute(query);
                    query = "CREATE TABLE IF NOT EXISTS Robots(ID INTEGER PRIMARY KEY AUTOINCREMENT, Host TEXT);";
                    stmt.execute(query);
                    query = "CREATE TABLE IF NOT EXISTS RobotsUrls(ID INTEGER PRIMARY KEY AUTOINCREMENT, HostID INTEGER REFERENCES Robots(ID) ON DELETE CASCADE, URL TEXT);";
                    stmt.execute(query);
                }
                else if (isCrawler == 0)
                {
                    //Creating Indexer Tables
                    query = "CREATE TABLE IF NOT EXISTS Words(ID INTEGER PRIMARY KEY AUTOINCREMENT, StemmedWord TEXT NOT NULL, OriginalWord TEXT UNIQUE NOT NULL,IDF FLOAT);";
                    stmt.execute(query);
                    query = "CREATE TABLE IF NOT EXISTS WordInfo(ID INTEGER PRIMARY KEY AUTOINCREMENT, Original TEXT, URL TEXT, TF FLOAT, RANK INT, Positions TEXT, Sentence TEXT);";
                    stmt.execute(query);
                    query = "CREATE TABLE IF NOT EXISTS Images(ID INTEGER PRIMARY KEY AUTOINCREMENT, URL TEXT, Original INTEGER REFERENCES Words(ID) ON DELETE CASCADE, urlImg TEXT);";
                    stmt.execute(query);
                }
                
            }
            catch ( Exception e ) 
            {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            }
            
        }
        
        public void test()
        {
            String query = "INSERT INTO Words(StemmedWord,OriginalWord) VALUES(?,?)";
            PreparedStatement pstmt;
            try {
                pstmt = c.prepareStatement(query);
                pstmt.setString(1, "asdasda");
                pstmt.setString(2, "asdasda");
                pstmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //c = DriverManager.getConnection("jdbc:sqlite:test.db:memory");
        //Starts Connection with an existing DB name, if it does not exist it creates the DB. (deprecated)
        //Now starts connection with an in-memory DB.
        public void StartConnection(int memory,String path)
        {
            try 
            {
                Class.forName("org.sqlite.JDBC");
                if(memory == 1) 
                    c = DriverManager.getConnection("jdbc:sqlite::memory:");
                else if (memory == 0)
                {
                    c = DriverManager.getConnection("jdbc:sqlite:"+path);
                }
                
                System.out.println("Opened DB successfully");
            } 
            catch ( Exception e ) 
            {
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                System.exit(0);
            }
        }
        
        //Creates a new table with a given query.
        public void CreateTable(String query)
        {
            try 
            {
                Statement stmt = c.createStatement();
                stmt.executeUpdate(query);
                stmt.close();
            } catch (SQLException ex) 
            {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("Created table successfully");
        }
        
        //Backups in-memory database to a file.
        public void Backup()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("backup to backup.db");
                System.out.println("backup database to backup.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void BackupIndexer()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("backup to backupindexer.db");
                System.out.println("backup database to backupindexer.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        
        
        //Restores in-memory database from a file.
        public void Restore()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("restore from backup.db");
                System.out.println("Restored database from backup.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public void RestoreIndexer()
        {
            try {
                Statement stmt = c.createStatement();
                stmt.executeUpdate("restore from backupindexer.db");
                System.out.println("Restored database from backupindexer.db");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Returns all records from a table.
        public ResultSet SelectAll(String tableName)
        {
            String query = "SELECT * FROM " + tableName + ";";
            ResultSet rs = null;
            try {
                Statement stmt = c.createStatement();
                rs = stmt.executeQuery(query);
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            return rs;
        }
        
        //Inserts a single page with its URL and document content.
        public void InsertPage(String URL, String Document, String tableName)
        {
            try {
                String query = "INSERT INTO crawler (URL,Document) VALUES (?,?)";
                
                
                PreparedStatement pstmt = c.prepareStatement(query);
                pstmt.setString(1, URL);
                pstmt.setString(2, Document);
                pstmt.executeUpdate();
                
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        //Inserts Pages using normal insert query for each page.
        /*
        public void InsertPages(List<HtmlPage> Pages) throws SQLException
        {
            String query = "INSERT INTO crawler (URL,Document) VALUES(?,?)";
            
            //Using prepared statement to preven SQL INJECTION.
            PreparedStatement pstmt = c.prepareStatement(query);
            long startTime = System.currentTimeMillis();
            for (HtmlPage Page : Pages)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.executeUpdate();
            }
            
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 SQL INSERT queries using PreparedStatement without JDBC batch update is :" + elapsedTime);
        }
        */
        
        //Sets AutoCommit status to true/false
        public void SetAutoCommit(boolean state)
        {
            try {
                c.setAutoCommit(state);
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Inserts pages in a batch into crawler table in DB.
        
        public void InsertPagesToVisitBatch(Set<webPage> pagesToVisit) throws SQLException
        {
            SetAutoCommit(true);
            String query;
            PreparedStatement pstmt;
            
            query = "INSERT INTO Documents (URL,Visited) VALUES(?,?)";
            pstmt = c.prepareStatement(query);
            
            for (webPage pageToVisit : pagesToVisit)
            {
                pstmt.setString(1, pageToVisit.getUrl());
                pstmt.setInt(2, 0);
                try
                {
                    pstmt.executeQuery();
                }
                catch (Exception ex)
                {
                    
                }
            }
            pstmt.close();
            /*
            //Setting auto commit to false to make all incoming inserts into one transaction.
            SetAutoCommit(false);
            String query;
            PreparedStatement pstmt;
            
           
            //For PagesToVisit
            query = "INSERT INTO Documents (URL,Visited) VALUES(?,?)";
            pstmt = c.prepareStatement(query);
            for (webPage pageToVisit : pagesToVisit)
            {
                pstmt.setString(1, pageToVisit.getUrl());
                pstmt.setInt(2, 0);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            c.commit();
            pstmt.close();
            */
            /*
            long startTime = System.currentTimeMillis();
            for (HtmlPage Page : Pages)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            c.commit();
            pstmt.close();
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 queries using PreparedStatement with JDBC batch insert is :" + elapsedTime);
            
            */
            /////////////////////////////////////////////////////
            //TO BE USED FOR THE 5000 PAGES PROJECT ( BATCH WITHIN BATCH )
            //Inserting each 500 pages together at once
            
            /*
            //Setting auto commit to false to make all incoming inserts into one transaction.
            SetAutoCommit(false);
            String query = "INSERT INTO crawler (URL,Document) VALUES(?,?)";
            
            //Single Batch size
            int batchSize = 500;
            
            //Using prepared statement to preven SQL INJECTION.
            PreparedStatement pstmt = c.prepareStatement(query);
            long startTime = System.currentTimeMillis();
            for (int i=0; i<Pages.size(); i++)
            {
                pstmt.setString(1, Page.getDomainUrlObject().getDomainUrl());
                pstmt.setString(2, Page.getHtml());
                pstmt.addBatch();
            
                if(i % batchSize == 0)
                    pstmt.executeBatch();
            }
            
            pstmt.executeBatch();
            long endTime = System.currentTimeMillis();
            long elapsedTime = (endTime - startTime); //in seconds
            System.out.println("Total time required to execute 1000 queries using PreparedStatement with JDBC batch insert is :" + elapsedTime);
            c.commit();
            pstmt.close();
            
            /////////////////////////////////////////////////////
*/
            
        }
        
        //insert pages to visit
        //1 get pages to visit
        //2- update pages to visit -> pages visited every 500 pages ,, first time 1->500, second 501->1000
        //3- get count of robots hosts table
        //4- insert robots takes HashMap<String Host, List<>urls)
        
        public List<IndexerPageInfo> GetIndexerPageInfo(int id)
        {
            String query = "SELECT ID,URL,Content FROM Documents WHERE Visited = 1 AND Content != \"NULL\" AND ID>? LIMIT 100";
            ResultSet rs = null;
            List<IndexerPageInfo> pagesVisited = new ArrayList<IndexerPageInfo>();
            try {
                PreparedStatement pstmt = c.prepareStatement(query);
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();
                while (rs.next()) 
                {
                    IndexerPageInfo pageVisited = new IndexerPageInfo();
                    pageVisited.setInfo(rs.getInt("ID"),rs.getString("URL"),  rs.getString("Content"));
                    pagesVisited.add(pageVisited);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return pagesVisited;
        }
        
        public void CalculateIDF()
        {
            String NumberOfDocuments = "select Count(URL) as TotalCount from WordInfo;";
            Statement stmt;
            ResultSet rs = null;
            int TotalNumberOfDocuments=1;
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(NumberOfDocuments);
                if(rs.next())
                    TotalNumberOfDocuments = rs.getInt("TotalCount");
                else
                    return;
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            System.out.println("Total Number of documents that have words: "+ TotalNumberOfDocuments);
            
            
            String query2 = "select OriginalWord from Words;";
            String query3 = "select Count(*) AS totalcount2 from WordInfo where Original = ?;";
            String query4 = "UPDATE Words SET IDF = LOG10(CAST(? AS FLOAT)/CAST(? AS FLOAT)) WHERE OriginalWord = ?";
            int count2;
            PreparedStatement pstmt;
            PreparedStatement pstmt2;
            Statement stmt2;
            ResultSet rs2 = null;
            ResultSet rs3 = null;
            try {
                stmt = c.createStatement();
                pstmt = c.prepareStatement(query3);
                pstmt2 = c.prepareStatement(query4);
                rs2 = stmt.executeQuery(query2);
                while(rs2.next())
                {
                    String temppppp = rs2.getString("OriginalWord");
                    pstmt.setString(1, rs2.getString("OriginalWord"));
                    rs3 = pstmt.executeQuery();
                    count2 = rs3.getInt("totalcount2");
                    
                    pstmt2.setInt(1, TotalNumberOfDocuments);
                    pstmt2.setInt(2, count2);
                    pstmt2.setString(3, rs2.getString("OriginalWord"));
                    pstmt2.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        public void InitializeIndexerTables()
        {
            String query1 = "delete from Words;";
            String query2 = "delete from sqlite_sequence where name='Words';";
            String query3 = "delete from WordInfo;";
            String query4 = "delete from sqlite_sequence where name='WordInfo';";
            
            try {
                Statement stmt1 = c.createStatement();
                Statement stmt2 = c.createStatement();
                Statement stmt3 = c.createStatement();
                Statement stmt4 = c.createStatement();
                
                stmt1.executeUpdate(query1);
                stmt2.executeUpdate(query2);
                stmt3.executeUpdate(query3);
                stmt4.executeUpdate(query4);
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        //Get count of robots hosts table
        public int CountRobotsHosts()
        {
            String query = "SELECT COUNT(*) AS totalcount FROM Robots;";
            int count = 0;
            Statement stmt;
            ResultSet rs = null;
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                count = rs.getInt("totalcount");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return count;
        }
        
        //Update pages to visit -> pages visited
        public void UpdatePagesToVisit(Set<webPage> pagesToVisit)
        {
            SetAutoCommit(false);
            String query3 = "SELECT COUNT(*) AS totalcount FROM Documents WHERE URL = ?";
            PreparedStatement pstmt3;
            
            String query2 = "INSERT INTO Documents (URL,Content,Visited) VALUES(?,?,?)";
            PreparedStatement pstmt2;
            
            
            String query = "UPDATE Documents SET Visited = ?, Content = ? , ID = ? WHERE URL = ?";
            PreparedStatement pstmt;
            
            String query4 = "SELECT MAX(ID) AS MAXID FROM Documents";
            Statement stmt;
            int maxid;
            ResultSet rs = null;
            ResultSet rs2 = null;
            int count=0;
            try {
                pstmt = c.prepareStatement(query);
                pstmt2 = c.prepareStatement(query2);
                pstmt3 = c.prepareStatement(query3);
                stmt = c.createStatement();
                rs2 = stmt.executeQuery(query4);
                        maxid = rs2.getInt("MAXID");
                for (webPage pageToVisit : pagesToVisit)
                    {
                        pstmt3.setString(1, pageToVisit.getUrl());
                        rs = pstmt3.executeQuery();
                        count = rs.getInt("totalcount");
                        maxid = maxid+1;
                        if(count > 0)
                        {
                            pstmt.setInt(1, 1);
                            pstmt.setString(2, pageToVisit.getPage()); //content string in crawler now
                            pstmt.setInt(3, maxid);
                            pstmt.setString(4, pageToVisit.getUrl());
                            pstmt.addBatch();
                        }
                        else if(count == 0)
                        {
                            pstmt2.setString(1,pageToVisit.getUrl());
                            pstmt2.setString(2, pageToVisit.getPage());
                            pstmt2.setInt(3, 1);
                            pstmt2.addBatch();
                        }
                        
                    }
                    
                pstmt.executeBatch();
                pstmt2.executeBatch();
                c.commit();
                pstmt.close();
                pstmt2.close();
                pstmt3.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Update ranks of given pages
        public void UpdateRanks(Set<webPage> pages)
        {
            String query = "UPDATE Documents SET Rank = ? WHERE URL = ?";
            PreparedStatement pstmt;
            SetAutoCommit(false);
            try {
                pstmt = c.prepareStatement(query);
                for (webPage page : pages )
                {
                    pstmt.setFloat(1, page.getRank());
                    pstmt.setString(2, page.getUrl());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                c.commit();
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        
        //Updates Content, InLinksCount, OutLinksCount when recrawling
        public void UpdateContentInLinksCountOutLinksCount(Set<webPage> pages)
        {
            String query = "UPDATE Documents SET Content = ?, InLinksCount = ?, OutLinksCount = ? WHERE URL = ?";
            PreparedStatement pstmt;
            SetAutoCommit(false);
            try {
                pstmt = c.prepareStatement(query);
                for (webPage page : pages)
                {
                    pstmt.setString(1, page.getPage());
                    System.out.println("i will insert parent with "+page.getParentPages().size() +"i am "+ page.getUrl());
                    pstmt.setInt(2, page.getParentPages().size());
                    pstmt.setInt(3, page.getChildPages().size());
                    pstmt.setString(4, page.getUrl());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                c.commit();
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Returns a list of web pages to visit
        public Set<webPage> GetPagesToVisit()
        {
            String query = "SELECT URL,Content,Rank,InLinksCount,OutLinksCount FROM Documents WHERE Visited = 0 ORDER BY RANK DESC limit 1000;";
            ResultSet rs = null;
            Set<webPage> pagesToVisit = new HashSet<webPage>();
            try {
                Statement stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) 
                {
                    webPage pageToVisit = new webPage();
                    pageToVisit.setUrl(rs.getString("URL"));
                    pageToVisit.setPage(rs.getString("Content"));
                    pageToVisit.setRank(rs.getFloat("Rank"));
                    pageToVisit.setInLinksCount(rs.getInt("InLinksCount"));
                    pageToVisit.setOutLinksCount(rs.getInt("OutLinksCount"));
                    pagesToVisit.add(pageToVisit);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return pagesToVisit;
        }
        
        //Returns a list of web pages visited sorted descendingly by Rank
        public Set<webPage> GetPagesVisited()
        {
            String query = "SELECT URL,Content,Rank,InLinksCount,OutLinksCount FROM Documents WHERE Visited = 1 ORDER BY Rank DESC;";
            ResultSet rs = null;
            Set<webPage> pagesVisited = new HashSet<webPage>();
            try {
                Statement stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                while (rs.next()) 
                {
                    webPage pageVisited = new webPage();
                    pageVisited.setUrl(rs.getString("URL"));
                    pageVisited.setPage(rs.getString("Content"));
                    pageVisited.setRank(rs.getFloat("Rank"));
                    pageVisited.setInLinksCount(rs.getInt("InLinksCount"));
                    pageVisited.setOutLinksCount(rs.getInt("OutLinksCount"));
                    pagesVisited.add(pageVisited);
                }
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return pagesVisited;
        }
        
        
        //Returns number of pages visited
        public int CountPagesVisited()
        {
            String query = "SELECT COUNT(*) AS totalcount FROM Documents WHERE Visited = 1;";
            int count = 0;
            Statement stmt;
            ResultSet rs = null;
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                count = rs.getInt("totalcount");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return count;
        }
        
        //Returns number of pages to visit
        public int CountPagesToVisit()
        {
            String query = "SELECT COUNT(*) AS totalcount FROM Documents WHERE Visited = 0;";
            int count = 0;
            Statement stmt;
            ResultSet rs = null;
            try {
                stmt = c.createStatement();
                rs = stmt.executeQuery(query);
                count = rs.getInt("totalcount");
            } catch (SQLException ex) {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            return count;
        }
        
        //Insert Robots Hosts
        public void InsertRobots(HashMap<String, ArrayList> Robots)
        {
            String query = "INSERT INTO Robots(Host) VALUES(?)";
            String query2 = "INSERT INTO RobotsUrls(HostID,URL) VALUES(?,?)";
            String query3 = "SELECT ID FROM Robots WHERE Host = ?";
            ResultSet rs = null;
            int id;
            try {
                //Using prepared statement to preven SQL INJECTION.
                PreparedStatement pstmt1 = c.prepareStatement(query);
                PreparedStatement pstmt2 = c.prepareStatement(query2);
                PreparedStatement pstmt3 = c.prepareStatement(query3);
                
                for (String Host : Robots.keySet())
                {
                    pstmt1.setString(1, Host);
                    pstmt1.execute();
                    
                    pstmt3.setString(1, Host);
                    rs = pstmt3.executeQuery();
                    id = rs.getInt("ID");
                    ArrayList<String> URLS = Robots.get(Host);
               // System.out.println(Host);
                    for (String url : URLS)
                    {
                     //   System.out.println(url);
                        pstmt2.setInt(1, id);
                        pstmt2.setString(2, url);
                        pstmt2.execute();
                    }
                }
            } catch (SQLException ex) {
                //Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        //Closes current connection.
        public void CloseConnection()
        {
            try 
            {
                c.close();
            } catch (SQLException ex) 
            {
                Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        
        public void InsertWords(HashMap<String, HashMap<String, Double>> OriginalWordRank, HashMap<String, HashMap<String, HashMap<String, wordInfo>>> IndexInfo)
        {
            try {
                String query;
                String query2;
                PreparedStatement pstmt;
                PreparedStatement pstmt2;
                SetAutoCommit(true);
                ResultSet rs = null;
                //For PagesToVisit
                query = "INSERT INTO Words (StemmedWord,OriginalWord) VALUES(?,?)";
                //query2 = "SELECT ID FROM Words WHERE OriginalWord = ?";
                pstmt = c.prepareStatement(query);
                //pstmt2 = c.prepareStatement(query2);
                
                for( String stemmedWord : IndexInfo.keySet())
                {
                    HashMap<String,HashMap<String, wordInfo>> originalHashMap = IndexInfo.get(stemmedWord);
                    for( String originalWord  : originalHashMap.keySet())
                    {
                        //pstmt2.setString(1, originalWord);
                        //rs = pstmt2.executeQuery();
                        pstmt.setString(1, stemmedWord);
                        pstmt.setString(2, originalWord);
                        try
                        {
                            pstmt.execute();
                            // c.commit();
                        }
                        catch (Exception ex)
                        {
                        }
                    }
                }
                pstmt.close();
            } catch (SQLException ex) {
            }
            
            try {
                String query;
                ////String query2;
                ////String query3;
                String query4;
                PreparedStatement pstmt;
                ////PreparedStatement pstmt2;
                ////PreparedStatement pstmt3;
                PreparedStatement pstmt4;
                SetAutoCommit(true);
                ResultSet rs = null;
                ResultSet rs2 = null;
                //For PagesToVisit
                query = "INSERT INTO WordInfo (Original,URL,TF,RANK,Positions,Sentence) VALUES(?,?,?,?,?,?)";
                //get positions
                ////query2 = "SELECT ID FROM WordInfo WHERE URL = ? AND Original = ?";
                ////query3 = "DELETE FROM WordInfo WHERE ID = ?";
                //query4 = "SELECT ID FROM Words WHERE OriginalWord = ?";
                pstmt = c.prepareStatement(query);
                ////pstmt2 = c.prepareStatement(query2);
                ////pstmt3 = c.prepareStatement(query3);
               // pstmt4 = c.prepareStatement(query4);
                for( String stemmedWord : IndexInfo.keySet())
                {
                    HashMap<String,HashMap<String, wordInfo>> originalHashMap = IndexInfo.get(stemmedWord);
                    for( String originalWord  : originalHashMap.keySet())
                    {
                        HashMap<String,wordInfo> urlIDHashMap = originalHashMap.get(originalWord);
                        for(String url : urlIDHashMap.keySet())
                        {
                            ////pstmt2.setString(1, url);
                            ////pstmt2.setString(2, originalWord);
                            ////rs = pstmt2.executeQuery();
                            /*
                            while(rs.next())
                            {
                                pstmt3.setInt(1, rs.getInt("ID"));
                                
                                try
                                {
                                    pstmt3.executeUpdate();
                                    
                                   // c.commit();
                                }
                                catch (Exception ex)
                                {
                                    //Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                            */
                           // pstmt4.setString(1, originalWord);
                           // rs2 = pstmt4.executeQuery();
                            wordInfo wordInfoResult = urlIDHashMap.get(url);
                            
                            pstmt.setString(1,originalWord);
                            pstmt.setString(2, url);
                            //INSERT TF HERE
                            pstmt.setDouble(3, wordInfoResult.getTF());
                            //INSERT RANK HERE
                            pstmt.setDouble(4, OriginalWordRank.get(originalWord).get(url));
                            //GET POSITIONS COMMA SEPARATED AND INSERT HERE
                            String inputPosition = "", inputSentence = "";
                            for( int PositionIndex : wordInfoResult.getPosition().keySet())
                            {
                                inputPosition += (PositionIndex + ",");
                                inputSentence += (wordInfoResult.getPosition().get(PositionIndex) + ",");
                            }
                            pstmt.setString(5, inputPosition.substring(0, inputPosition.length() - 1));
                            //INSERT SENTENCE HERE
                            pstmt.setString(6,inputSentence.substring(0, inputSentence.length() - 1));
                            pstmt.executeUpdate();
                        }
                    }
                }
                pstmt.close();
            } catch (SQLException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

public void Resetvistied()
{
 String query = "UPDATE Documents SET visited = 0";

Statement stmt ;
            try {
                stmt = c.createStatement();
                stmt.execute(query);
                stmt.close();
            } catch (SQLException ex) {
                //Logger.getLogger(SQLiteDBHelper.class.getName()).log(Level.SEVERE, null, ex);
            }


}
}
