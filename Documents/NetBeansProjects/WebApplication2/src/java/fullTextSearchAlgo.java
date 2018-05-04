/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package searchengine;

import java.util.Vector;

/**
 *
 * @author Ahmed
 */
public class fullTextSearchAlgo {
     Vector<Integer>  index_found  = new Vector<Integer>()  ;   
      void checkfullTextSearch( String positions [],int count )
     {
     
     int row=0  ;
     Vector <Vector<Integer>> postion_integers  = new Vector <Vector<Integer>>() ;
    
     for (int i = 0 ;i < positions.length ;i++)
     {
     String splittied [] = positions[i].split(",");
     Vector<Integer> newone = new Vector<Integer>();
     for (int k = 0 ; k < splittied.length ; k++)
     {
         
     newone.add( Integer.parseInt(splittied[k]));
     
              if (row < newone.lastElement())
            row =  newone.lastElement() ;
     }
     postion_integers.add(newone);
     }


        solve(0  ,postion_integers,count);
    
         return ;
     
     }
     
 void solve (int start ,   Vector <Vector<Integer>> find_in,int count  )
 {

     for ( int i = 0 ; i <find_in.get(start).size() ; i++)
     {
         int matched=1 ;
       int  elementtosearch = find_in.get(start).get(i);
         for (int j =1 ; j<= find_in.size() ;j++)
         {
            
             
         if( matched==find_in.size())
         {
        index_found.add(find_in.get(start).get(i));
        count--;
        if(count ==0)
            return ;
        matched=1;
         }
         else if ( j !=find_in.size() && find_in.elementAt(j).contains(elementtosearch+1) )
         {
             matched++;
             elementtosearch ++ ;
         }
         }
     }
     
 
 }



  
  Vector<Integer>  get_indexes (String positions [],int count )
  {
  checkfullTextSearch(positions,count);

 return index_found;
  }
}
