<%-- 
    Document   : index
    Created on : Apr 27, 2018, 4:57:13 PM
    Author     : Kamal
--%>
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
        
        
        
        <div style="display: flex; justify-content: center; padding-top: 200px">
            <img src="logo_final.png" height="150px" width="300px" title="Made By: Ahmed Kamal Ahmed">
        </div>
        
        <div style="display: flex; justify-content: center; padding-top: 20px">
            <form action="http://localhost:8080/WebApplication2/getpages" method="GET">
                <div class="input-effect input-group mb-3">
                    <input class="effect-16" type="text" placeholder="" name="query" id="queryinput" style="width: 300px;" list="queries">
                    <datalist id="queries">
                    </datalist>
                    <label>Search...</label>
                    <span class="focus-border"></span>
                    <div class="input-group-append">
                        <button class="btn btn-outline-info" type="submit" style="border: 0;" id="submitbutton"><i class="fas fa-search"></i></button>
                    </div>
                </div>
            </form>
        </div>
        
        
        
        <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js" integrity="sha384-cs/chFZiN24E4KMATLdqdvsezGxaGsi4hLGOzlXwp5UZB1LY//20VyM2taTB4QvJ" crossorigin="anonymous"></script>
        <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.0/js/bootstrap.min.js" integrity="sha384-uefMccjFJAIv6A+rW+L4AHf99KvxDjWSu1z9VI8SKNVmz4sk7buKt/6v9KI65qnm" crossorigin="anonymous"></script>
        <script src="scripts2.js"></script>
    </body>
</html>

