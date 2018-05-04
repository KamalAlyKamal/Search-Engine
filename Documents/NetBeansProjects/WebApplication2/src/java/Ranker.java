/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package searchengine;

import org.jsoup.Jsoup;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Ahmed
 */

class wordDetails
{
    public double ExistsInQueryFactor;
    
    public double TF; 
    public int Rank;  
    public String Positions;
    public String Sentences;
 };
class WebInterFace
{
    public String title;
    public String URL;
    public String Snippet;
}

public class Ranker {
    
    /*************************** GLOBAL VARIABLES Start *********************************/
    String input;
    Boolean isFullTextSearch;
    
    Connection c1 = null;
    Connection c2 = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs2 = null;
    
    double NotInQueryFactor = 0.2;
    int SnippetSize = 15;
    int maxFullTextSearchRank = 5;
    
    String chosenWords[];
    
    public static String[] stopWordsList = {         
        "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't", "as", 
        "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can't", "cannot", 
        "could", "couldn't", "did", "didn't", "do", "does", "doesn't", "doing", "don't", "down", "during", "each", 
        "few", "for", "from", "further", "had", "hadn't", "has", "hasn't", "have", "haven't", "having", "he", "he'd", 
        "he'll", "he's", "her", "here", "here's", "hers", "herself", "him", "himself", "his", "how", "how's", "i", 
        "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "isn't", "it", "it's", "its", "itself", "let's", "me", 
        "more", "most", "mustn't", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", 
        "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "shan't", "she", "she'd", "she'll", "she's",
        "should", "shouldn't", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", 
        "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", 
        "those", "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "we'd", "we'll", "we're",
        "we've", "were", "weren't", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", 
        "who's", "whom", "why", "why's", "with", "won't", "would", "wouldn't", "you", "you'd", "you'll", "you're", 
        "you've", "your", "yours", "yourself", "yourselves"
    };
    
    private  HashMap<String, Double> Word_IDF = new HashMap<>(); // <Word: IDF>
    private  HashMap<String, HashMap<String, wordDetails>> URL_WORD = new HashMap<>(); // <URL: <Word: TF, Rank, Positions, Sentence, (EXISTING_In_Query?)>>
    private  HashMap<String, Double> URL_URLINFO = new HashMap<>();   // <URL: PopularityRank>
    
    private  HashMap<Double, String> URL_Result = new HashMap<>(); //<(IDF*TF + PopularityRank) * EXISTING_In_Query  : URL>
    private  HashMap<String, Double> URL_Snippet = new HashMap<>(); //<URL: Title, SURROUNDINGWORDS!!>
        
    /*************************** GLOBAL VARIABLES Start *********************************/
    
    //Constructor
    public Ranker(String input) {
        this.input = input;
        
        try {
            c1 = DriverManager.getConnection("jdbc:sqlite:E:\\CUFE\\APT\\SearchEngine\\backup.db");
            c2 = DriverManager.getConnection("jdbc:sqlite:E:\\CUFE\\APT\\SearchEngine\\backupindexer.db");
        } catch (SQLException ex) {
            Logger.getLogger(Ranker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Cleans The Input
    public String cleanTheText(String content, int RemoveStopWordsBool){
        //Remove All Special Characters
        content = content.replaceAll("[^a-zA-Z0-9 ]","");
        //Making All Small Letter
        content = content.toLowerCase();
        
        //Removing Stop Words
        if(RemoveStopWordsBool != 0)
            for(String stopWord : stopWordsList)
                content = content.replaceAll("\\b" + stopWord + "\\b\\s*", "");     //   \\b gives you the word boundaries.  \\s* sops up any white space on either side of the word being removed 
        
        //Removes Extra Spaces
        content = content.trim().replaceAll(" +", " ");   
        
        return content;
    }
    
    //Gets Stemmed Word
    public String GetStemmedWord(String Word){
        try {
            String query2 = "SELECT StemmedWord FROM Words WHERE OriginalWord = ?";
            
            pstmt2 = c2.prepareStatement(query2);
            pstmt2.setString(1, Word);
            
            rs2 = pstmt2.executeQuery();
            
            return rs2.getString("StemmedWord");
         
        } catch (SQLException ex) {
        }
        
        return "";
    }
    
    //Gets Alternative Words for the stemmed words
    public void GetAlternativeWords(String Word){
        try {
            String query2 = "SELECT OriginalWord, IDF FROM Words WHERE StemmedWord = ?";
            
            pstmt2 = c2.prepareStatement(query2);
            pstmt2.setString(1, Word);
            
            rs2 = pstmt2.executeQuery();
            
            while(rs2.next())
            {
                Word_IDF.put( rs2.getString("OriginalWord") , rs2.getDouble("IDF"));
            }
        } catch (SQLException ex) {
        }
    }
    
    //Gets Information about the Words
    public void GetWordsInfo(String Word, double existsInQueryFactor){
        try {
            String query2 = "SELECT URL, TF, RANK, Positions, Sentence FROM WordInfo WHERE Original = ?";
            
            pstmt2 = c2.prepareStatement(query2);
            pstmt2.setString(1, Word);
            
            rs2 = pstmt2.executeQuery();
            
            while(rs2.next())
            {
                //Adds Url if new
                if(URL_WORD.get(rs2.getString("URL")) == null)
                    URL_WORD.put(rs2.getString("URL"), new HashMap<>());
                HashMap<String, wordDetails> wordMap = URL_WORD.get(rs2.getString("URL"));
                
                wordDetails worddetail = new wordDetails();
                worddetail.ExistsInQueryFactor = existsInQueryFactor;
                worddetail.TF = rs2.getDouble("TF");
                worddetail.Rank = rs2.getInt("RANK");
                worddetail.Positions = rs2.getString("Positions");
                worddetail.Sentences = rs2.getString("Sentence");
                
                //Adds Word if new (Always new.. just incase. As URL-Word is always unique)
                if(wordMap.get(Word) == null)
                    wordMap.put(Word, worddetail);
            }
        } catch (SQLException ex) {
        }
    }
    
    //Gets URL information from Documents Table
    public void GetURLInfo(String URL){
        try {
            String query2 = "SELECT RANK FROM Documents WHERE URL = ?";
            
            pstmt2 = c1.prepareStatement(query2);
            pstmt2.setString(1, URL);
            
            rs2 = pstmt2.executeQuery();
            
            while(rs2.next())
            {
                //Adds Url if new
                if(URL_URLINFO.get(URL) == null)
                    URL_URLINFO.put(URL, rs2.getDouble("RANK"));
            }
        } catch (SQLException ex) {
        }
    }
    
    //Calculates Rank and everything
    public List<String> doFunction(){
        List<String> returnValue = new ArrayList<>();
      
        //Check full text search
        if(input.startsWith("%") && input.endsWith("%"))
            isFullTextSearch = true;
        else
            isFullTextSearch = false;
        
        
        /******* Check To Remove Stopped Words or Not Start ************/
        String inputWithStopped = cleanTheText(input, 1);
        String inputNoStopped = cleanTheText(input, 0);
        
        String wordsWithStopped[] = inputWithStopped.split(" ");
        String wordsNoStopped[] = inputNoStopped.split(" ");
        
        if(isFullTextSearch == true)
            chosenWords = wordsWithStopped;
        else
        {
            if(wordsWithStopped.length < wordsNoStopped.length/2)
                chosenWords = wordsNoStopped;
            else
                chosenWords = wordsWithStopped;
        }
        /******* Check To Remove Stopped Words or Not End   ************/
        
        
        //Add Words with Their IDFs.    Word_IDF
        for(String word : chosenWords)
            GetAlternativeWords( GetStemmedWord(word) );
        
        //Loops through all words alternatives. Assigns higher factor for original words.   URL_WORD
        for( String word : Word_IDF.keySet())
        {
            if(Arrays.asList(chosenWords).contains(word))
                GetWordsInfo(word, 1);
            else
                GetWordsInfo(word, NotInQueryFactor);                        
        }
        
        //Adds Urls Populairty Ranks    URL_URLINFO
        for( String URL : URL_WORD.keySet())
            GetURLInfo(URL);
        
        
        /*************************** CALCULATE RANK Start ***************************/
        //Traverse through the URLS and calculate the new Rank.     URL_Result
        double rank;
        for(String URL: URL_URLINFO.keySet())
        {
            rank = 0;
            HashMap<String, wordDetails> wordMap = URL_WORD.get(URL);
            
            //Traverse through all words in this URL. Calculate Relevance. TF*Rank*IDF*ExistsInQueryFactor
            for(String WORD : wordMap.keySet())
            {                
                wordDetails worddetail = wordMap.get(WORD);
                rank += (double)(worddetail.TF) * (double)(worddetail.Rank) * Word_IDF.get(WORD) * worddetail.ExistsInQueryFactor;
            }
            
            //Add Popularity Rank
            rank += (URL_URLINFO.get(URL) * 10);
            
            //Check If Full Text Search Website
            if(isFullTextSearch == true)
            {
                boolean containsFullTextSearch = true;
                
                String [] testarray = new String[chosenWords.length];
                for(int i =0; i< testarray.length; i++)
                {
                    if (URL.equals("http://aafes.com"))
                        System.out.println(" sadasd");
                    if(URL_WORD.get(URL).get(chosenWords[i]) == null)
                    {
                        containsFullTextSearch =false;
                        break;
                    }
                    testarray[i] = URL_WORD.get(URL).get(chosenWords[i]).Positions;
                }
                
                if(containsFullTextSearch)
                {
                     fullTextSearchAlgo obj = new fullTextSearchAlgo(); 
                    Vector<Integer> fullTextPositions =  obj.get_indexes(testarray,1); //More Than 5 is unreasonable.
                    if(!fullTextPositions.isEmpty())    //FOR TEST
                       System.out.println(URL);
                    //Full text exists in this website
                    if(!fullTextPositions.isEmpty())
                        rank += (1000.0 * fullTextPositions.size());  //To make sure the results are shown first according to repetance.
                }
            }
            
            //Add URL To that Rank
            if(URL_Result.get(rank) == null)
                URL_Result.put(rank, URL);
            else
            {
                String valueURL = URL_Result.get(rank);
                valueURL = valueURL + "," + URL;
                URL_Result.put(rank, valueURL);
            }
        }
        /*************************** CALCULATE RANK End   ***************************/
        
        
        /********************* SORT URLs according to Rank Start ********************/
        List<Double> KeyRanks = new ArrayList<>(URL_Result.keySet());
        Collections.sort(KeyRanks);
        Collections.reverse(KeyRanks);
        
        for( Double listValeRank : KeyRanks)
        {
            //Incase several URLs have same rank
            String urlValue = URL_Result.get(listValeRank);
            String urlValueArray[] = urlValue.split(",");
            for(String url : urlValueArray)
            {
                returnValue.add(url);
            }
        }
        /********************* SORT URLs according to Rank Start ********************/
        
        return returnValue;
    }
    
    public int getTotalSearchResults(){
        return URL_Result.keySet().size();
    }
    
    
    
    
    public String cleanTheHTMLText(String HTMLContent, int RemoveStopWordsBool)
    {
        //1) Add Space After tags
        HTMLContent = HTMLContent.replaceAll("<(.*?)>","<$1> ");
        //1') Remove HTML Tags
        HTMLContent = Jsoup.parse(HTMLContent).text();
        
        //2) Remove All Special Characters
        HTMLContent = HTMLContent.replaceAll("[^a-zA-Z0-9 ]","");
        //3) Making All Small Letter
        HTMLContent = HTMLContent.toLowerCase();
        
        //4) Removing Stop Words
        if(RemoveStopWordsBool != 0)
            for(String stopWord : stopWordsList)
                HTMLContent = HTMLContent.replaceAll("\\b" + stopWord + "\\b\\s*", "");     //   \\b gives you the word boundaries.  \\s* sops up any white space on either side of the word being removed 

        //5) Removes Extra Spaces
        HTMLContent = HTMLContent.trim().replaceAll(" +", " ");   
        
        return HTMLContent;
    }
    
    //Gets Title And Snippet from database
    public WebInterFace GetURLContent(String URL){
        WebInterFace returnValue = new WebInterFace();
        returnValue.URL = URL;
        
        try {
            String query2 = "SELECT Content FROM Documents WHERE URL = ?";
            pstmt2 = c1.prepareStatement(query2);
            pstmt2.setString(1, URL);
            rs2 = pstmt2.executeQuery();
            
            String HTMLcontent = rs2.getString("Content");
            //Get Title
            returnValue.title = Jsoup.parse(HTMLcontent).title();
            
            /********************* Get Snippet Start **********************/
            //Get Body
            if(Jsoup.parse(HTMLcontent).body() == null)
                return null;
            String BodyContent = Jsoup.parse(HTMLcontent).body().toString();
            BodyContent = cleanTheHTMLText(BodyContent, 0);
            String BodyContentArray[] = BodyContent.split(" ");
            
            //Traverse on words of this url to get the word and put its surrounding as a snippet
            String snippet = "";
            HashMap<String, wordDetails> wordInURL =  URL_WORD.get(URL);
            
            //Check If Full Text Search Website
            if(isFullTextSearch == true)
            {
                boolean containsFullTextSearch = true;
                
                String [] testarray = new String[chosenWords.length];
                for(int i =0; i< testarray.length; i++)
                {
                    if(URL_WORD.get(URL).get(chosenWords[i]) == null)
                    {
                        containsFullTextSearch = false;
                        break;
                    }
                    testarray[i] = URL_WORD.get(URL).get(chosenWords[i]).Positions;
                }
                
                Vector<Integer> fullTextPositions = new Vector<>();
                if(containsFullTextSearch)
                {
                        fullTextSearchAlgo obj = new fullTextSearchAlgo();
                         fullTextPositions =  obj.get_indexes(testarray,3); 
                }
                
                
                //If Full text search exists.
                if(!fullTextPositions.isEmpty())
                {
                    //Loop through indices and display them. (Max 3 occurence) 
                    //for( Integer positionFullText : fullTextPositions)
                    //{
                String x=   BodyContentArray[fullTextPositions.get(0)];
                        for( int i=(Integer.max(0, fullTextPositions.get(0)-SnippetSize)); i<(Integer.min(BodyContentArray.length, fullTextPositions.get(0)+SnippetSize)); i++)
                        {
                            if(wordInURL.keySet().contains(BodyContentArray[i]))
                                snippet += "<b>" + BodyContentArray[i] + "</b> ";
                            else
                                snippet+= BodyContentArray[i] + " ";
                        }
                        //snippet += ".... ";
                    //}
                }
                else
                {
                    for(String words : wordInURL.keySet())
                    {
                        wordDetails worddetail = wordInURL.get(words);
                        String positionsPlace = worddetail.Positions;
                        String positionsPlaceArray[] = positionsPlace.split(",");

                        for( int i=(Integer.max(0, Integer.parseInt(positionsPlaceArray[0])-SnippetSize)); i<(Integer.min(BodyContentArray.length, Integer.parseInt(positionsPlaceArray[0])+SnippetSize)); i++)
                        {
                            if(wordInURL.keySet().contains(BodyContentArray[i]))
                                snippet += "<b>" + BodyContentArray[i] + "</b> ";
                            else
                                snippet+= BodyContentArray[i] + " ";
                        }
                    }
                }
            }
            else
            {
                for(String words : wordInURL.keySet())
                {
                    wordDetails worddetail = wordInURL.get(words);
                    String positionsPlace = worddetail.Positions;
                    String positionsPlaceArray[] = positionsPlace.split(",");

                    for( int i=(Integer.max(0, Integer.parseInt(positionsPlaceArray[0])-SnippetSize)); i<(Integer.min(BodyContentArray.length, Integer.parseInt(positionsPlaceArray[0])+SnippetSize)); i++)
                    {
                        if(wordInURL.keySet().contains(BodyContentArray[i]))
                            snippet += "<b>" + BodyContentArray[i] + "</b> ";
                        else
                            snippet+= BodyContentArray[i] + " ";
                    }
                }
            }
            returnValue.Snippet = snippet;
            /********************* Get Snippet End   **********************/
        } catch (SQLException ex) {
            System.out.println("ERRRRRORRRRRRRR");
        }
        return returnValue;
    }    
    
    //Paginates the results. Assuming 10 per page
    public List<WebInterFace> getSearchResult(int offset)
    {
        List<String> inputURLs = doFunction();
        List<WebInterFace> returnValue = new ArrayList<>();
        
        //No more than 10 or till end of URLs 
        for(int i = offset; ((i< inputURLs.size()) && (i< offset+10)); i++)
        {
            WebInterFace temp = GetURLContent(inputURLs.get(i));
            if(temp==null)
                continue;
            returnValue.add( temp );
        }
        
        return returnValue;
    }
    
    public void DisplayAll( List<WebInterFace> returnValue )
    {
        for(WebInterFace search: returnValue)
        {
            System.out.println(search.URL);
            System.out.println(search.title);
            System.out.println(search.Snippet);
            System.out.println("\n");
        }
    }
}
 