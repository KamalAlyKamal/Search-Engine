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
import java.util.ArrayList;
import org.json.*;
/**
 *
 * @author lenovo
 */
@WebServlet(urlPatterns = {"/getqueries"})
public class getqueries extends HttpServlet {
    
           //Connection c1 = null;
           //Connection c2 = null;
           Connection c3 = null;
           //PreparedStatement pstmt1 = null;
           //PreparedStatement pstmt2 = null;
           PreparedStatement pstmt3 = null;
           //ResultSet rs1 = null;
           //ResultSet rs2 = null;
           ResultSet rs3 = null;
           //Statement stmt1 = null;
           //Statement stmt2 = null;
           Statement stmt3 = null;
           
           JSONObject json;
           JSONArray jsonArray;
           JSONObject query;
    
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
        //response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json");
        
          ArrayList<String> queries = new ArrayList<String>();
           try
           {
               Class.forName("org.sqlite.JDBC");
               //c1 = DriverManager.getConnection("jdbc:sqlite:C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backup.db");
               //c2 = DriverManager.getConnection("jdbc:sqlite:C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backupindexer.db");
               c3 = DriverManager.getConnection("jdbc:sqlite:C:/Users/lenovo/Documents/NetBeansProjects/WebApplication2/backupinterface.db");
               String query3 = "SELECT Query FROM Queries WHERE Query LIKE ? ORDER BY Frequency DESC";
               pstmt3 = c3.prepareStatement(query3);
               if(request.getParameter("query") != null && request.getParameter("query") != "")
               {
                    pstmt3.setString(1, request.getParameter("query")+"%");
                    rs3 = pstmt3.executeQuery();
               }
               while(rs3.next())
               {
                   queries.add(rs3.getString("Query"));
               }
               
               if(queries.size()>0 && queries != null)
               {
                  jsonArray = new JSONArray(queries);
                  //System.out.print(jsonArray);
               }
               String result = jsonArray.toString();
               response.getOutputStream().print(result);
               c3.close();
           }
           catch ( Exception e ) 
            {
                //System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                //System.exit(0);
            }
           
           
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
