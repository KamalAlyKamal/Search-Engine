<%-- 
    Document   : search
    Created on : Apr 27, 2018, 10:36:33 PM
    Author     : Kamal
--%>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.sql.*"%>
<%@page import="java.sql.ResultSet"%>
<%@ page import="org.sqlite.*" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>AKA</title>
        
        
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/css/bootstrap.min.css" integrity="sha384-9gVQ4dYFwwWSjIDZnLEWnxCjeSWFphJiwGPXr1jddIhOegiu1FwO5qRGvFXOdJZ4" crossorigin="anonymous">
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.0.10/css/all.css" integrity="sha384-+d0P83n9kaQMCwj8F4RJB66tzIwOKmrdb46+porD/OvrJ+37WqIM7UoBtwHO6Nlg" crossorigin="anonymous">
        <link rel="stylesheet" href="styles2.css">
    </head>
    <body>
       
        
        
        <div id="logo-search-top-bar">
            <form action="/WebApplication2/getpages" method="GET">
                <div id="logo-search-top-maindiv">
                    <div id="logo-search-top-innerdiv">
                        <a href="index.jsp" title="Go to AKA Home" id="logo-search">
                            <img src="logo_final.png" id="logo-search-image">
                        </a>
                    </div>
                    <div class="input-effect input-group mb-3">
                        <input class="effect-16-search" name="query" type="text" placeholder="" id="queryinput" value="<% if(request.getParameter("query") != null) out.print(request.getParameter("query")); %>" style="width: 589px;" list="queries">
                        <datalist id="queries"></datalist>
                        <span class="focus-border-search"></span>
                        <div class="input-group-append">
                            <button class="btn btn-outline-info" type="submit" style="border: 0;"><i class="fas fa-search"></i></button>
                        </div>
                    </div>
                </div>
            </form>
        </div>
        
        <div id="results-maindiv">
            <!--PAGINATION AND LOOP ON RESULTS HERE-->
            <%  
List<String> titles = (ArrayList<String>) request.getAttribute("titlesList");
List<String> urls = (ArrayList<String>) request.getAttribute("urlsList");
List<String> snippets = (ArrayList<String>) request.getAttribute("snippetsList");

for(int i=0; i<titles.size(); i++) {
    out.println("<div id=\"result-item-outerdiv\">");
	out.println("<div id=\"result-item-maindiv\">");
	out.println("<h3 id=\"title-heading\">");
	//out.println("<a id=\"title-anchor-link\" href=\"+page.URL+\">");
        out.print("<a id=\"title-anchor-link\" href=\""+urls.get(i)+"\">");
        out.println("");
	out.println(titles.get(i));
	out.println("</a>");
	out.println("</h3>");
	out.println("<div id=\"result-item-innerdiv\">");
	out.println("<div id=\"result-item-url\">");
	out.println("<cite>");
	out.println(urls.get(i));
	out.println("</cite>");
	out.println("</div>");
	out.println("<span id=\"result-item-snippet\">");
	out.println(snippets.get(i));
	out.println("</span>");
	out.println("</div>");
	out.println("</div>");
	out.println("</div>");
}
%>
        </div>
        
        <footer class="footer" style="margin-left: 45%;left:50;padding-bottom: 30px;">
            <img src="logo_final.png" id="logo-search-image">
            <!--PAGE NUMBERS DYNAMICALLY BY PAGINATION-->
            <div>
                <%
                    for(int i=1; i<= (Integer)request.getAttribute("noOfPages"); i++)
                    {
                        if((Integer)request.getAttribute("currentPage") == i)
                        {%>
                            <a class="page-number" style="color: purple;" href="/WebApplication2/getpages?pageNumber=<% out.print(i); %>&query=<% out.print(request.getParameter("query")); %>"><% out.print(i);%></a>
                        <%}
                        else
                        {%>
                            <a class="page-number" style="color: blue;" href="/WebApplication2/getpages?pageNumber=<% out.print(i); %>&query=<% out.print(request.getParameter("query")); %>"><% out.print(i); %></a>
                        <%}
                    %>
                            
                    <%}
                        //out.print((Integer)request.getAttribute("noOfPages"));
                %>
            </div>
        </footer>        
        
        <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js" integrity="sha384-cs/chFZiN24E4KMATLdqdvsezGxaGsi4hLGOzlXwp5UZB1LY//20VyM2taTB4QvJ" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js" integrity="sha384-uefMccjFJAIv6A+rW+L4AHf99KvxDjWSu1z9VI8SKNVmz4sk7buKt/6v9KI65qnm" crossorigin="anonymous"></script>
        <script src="scripts2.js"></script>
    </body>
</html>
