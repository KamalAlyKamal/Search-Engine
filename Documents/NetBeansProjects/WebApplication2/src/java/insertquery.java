/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

/**
 *
 * @author lenovo
 */
@WebServlet(urlPatterns = {"/insertquery"})
public class insertquery extends HttpServlet {

           Connection c1 = null;
           Connection c2 = null;
           Connection c3 = null;
           PreparedStatement pstmt1 = null;
           PreparedStatement pstmt2 = null;
           PreparedStatement pstmt3 = null;
           PreparedStatement pstmt4 = null;
           PreparedStatement pstmt5 = null;
           ResultSet rs1 = null;
           ResultSet rs2 = null;
           ResultSet rs3 = null;
           ResultSet rs4 = null;
           Statement stmt1 = null;
           Statement stmt2 = null;
           Statement stmt3 = null;
    
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try
           {
               Class.forName("org.sqlite.JDBC");
               //c1 = DriverManager.getConnection("jdbc:sqlite:C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backup.db");
               //c2 = DriverManager.getConnection("jdbc:sqlite:C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backupindexer.db");
               //String query2 = "SELECT StemmedWord FROM Words WHERE OriginalWord = ?";
               //pstmt2 = c3.prepareStatement(query2);
               //C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backupinterface.db
               //C:\Users\lenovo\Documents\NetBeansProjects\WebApplication2
               c3 = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\lenovo\\Documents\\NetBeansProjects\\WebApplication2\\backupinterface.db");
               String query3 = "INSERT INTO Queries(Query,Frequency) VALUES(?,?);";
               String query4 = "SELECT COUNT(*) AS TotalCount FROM Queries WHERE Query=?";
               String query5 = "UPDATE Queries SET Frequency=Frequency+1 WHERE Query=?";
               pstmt3 = c3.prepareStatement(query3);
               pstmt4 = c3.prepareStatement(query4);
               pstmt5 = c3.prepareStatement(query5);
               //if(request.getParameter("query") != null && !"".equals(request.getParameter("query")))
               //{
                   String x = request.getParameter("query");
                   if(x==null)
                       x="ana yaalaaa";
                   pstmt4.setString(1, x);
                   rs4 = pstmt4.executeQuery();
                    if(rs4.getInt("TotalCount") == 0) //NOT FOUND, INSERT IT
                    {
                        pstmt3.setString(1, x);
                        pstmt3.setInt(2, 1);
                        pstmt3.executeUpdate();
                        System.out.println("====================asdasdsad==========");
                    }
                   
                    else if(rs4.getInt("TotalCount") > 0) //FOUND , INCREMENT FREQUENCY
                    {
                        pstmt5.setString(1, x);
                        pstmt5.executeUpdate();
                    }
                    //response.getWriter().print("qwrqwrqwr");
                    c3.close();
               //}
               
           }
           catch ( Exception e ) 
            {
                //String temp = null;
                System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                //System.exit(0);
            }
           
            
            //Set the query attribute and save it into the request and forward thsi request to the search page
            request.setAttribute("query", request.getParameter("query"));
            request.getRequestDispatcher("search.jsp").forward(request, response);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    
}
