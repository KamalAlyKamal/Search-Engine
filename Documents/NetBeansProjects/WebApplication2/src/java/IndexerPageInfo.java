/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchengine;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author lenovo
 */
public class IndexerPageInfo 
{
    private int ID;
    private String URL;
    private String Content;
    
    IndexerPageInfo()
    {
    }
    
    public int getID()
    {
        return this.ID;
    }
    
    public String getURL()
    {
        return this.URL;
    }
    
    public String getContent()
    {
        return this.Content;
    }
    
    public void setInfo(int id,String url, String content)
    {
        this.ID = id;
        this.URL = url;
        this.Content = content;
    }
}
