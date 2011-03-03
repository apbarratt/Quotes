<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC '-//W3C//DTD HTML 4.01 Transitional//EN' 'http://www.w3.org/TR/html4/loose.dtd'>
<html>
<head>
<meta http-equiv='Content-Type' content='text/html; charset=ISO-8859-1'>
<link rel='stylesheet' type='text/css' href='/quotes/style1.css' />
<title>Quotes - Sharing quotations with the world.</title>

<%
//if we're on a timeline page, add the following stuff

String displayName = "";
if(session.getAttribute("displayName")!=null)
	displayName = session.getAttribute("displayName").toString();

if(request.getAttribute("timelinePage")!=null)
{
	String jsonAddress = "";
	
	//get the correct jsonAddress
	if(request.getAttribute("user")!=null)
	{
		jsonAddress = "/quotes/jsonUser/" + request.getAttribute("user").toString();
	}
	else if(request.getAttribute("homeTimeline")!=null)
	{
		jsonAddress = "/quotes/jsonHomeTimeline";
	}
	else
	{
		jsonAddress = "/quotes/jsonTimeline";
	}
	
%>
<script type="text/javascript" src="/quotes/jquery-1.5.1.min.js"></script>


<script type="text/javascript">
	//every 30 seconds
	var viewer = "<% out.print(displayName); %>";
	var user = "<% out.print(request.getAttribute("user")); %>";
	$(document).ready(function(){
		setInterval("location.reload(true)", 300000);

	     $.getJSON('<% out.print(jsonAddress); %>',function(result){
	       var obj = result.Data;

	      for (var i in obj)
	 	  {
				var quote = obj[i].Quote;
				var by = obj[i].By;
				var displayName = obj[i].DisplayName;
				var tweetID = obj[i].TweetID;
				var howLong = obj[i].HowLong;
				
				
				byPart = 'by <a href="/quotes/user/' + displayName + '">' + displayName + '</a> ';
				if(viewer==displayName)
					byPart = 'by <a href="/quotes/user/' + displayName + '">you</a> ';
				if(user==displayName)
					byPart = '';
				
				newDiv = document.createElement('div');
				
				$(newDiv).append('<div class="tweet">');
					$(newDiv).append('<span class="quote">');
						$(newDiv).append('"');
						$(newDiv).append(quote);
						$(newDiv).append('"<br />');
					$(newDiv).append('</span>');
					$(newDiv).append('<span class="quoteInfo">');
						$(newDiv).append('originally by ');
						$(newDiv).append(by);
						$(newDiv).append(', posted ');
						$(newDiv).append(byPart);
						$(newDiv).append(howLong);
						if(viewer==displayName)
						{
							$(newDiv).append(' <a href="/quotes/delete/' + tweetID + '">Delete</a>');
						}
					$(newDiv).append('</span>');
				$(newDiv).append('</div>');
				
				$('#content').append(newDiv); 
	 	  }//end of for loop
	 	  
	     });//end of getJSON
	     
	 });//end of timed refresher
		
</script>
<%
}
%>


</head>
<body>

<%
//if attribute "message" exists
if(session.getAttribute("message")!=null)
{
	%>
	
	<div id="message">
	<% out.println(session.getAttribute("message")); %>
	</div>
	
	<%
	session.removeAttribute("message");
}

%>

<div id="wrapper">

<div id='topbanner'>

<%
if(session.getAttribute("displayName")!=null)
{
	out.println("<div class='box' id='account'>");
	out.println("Welcome " + session.getAttribute("fullname") + "<br />");
	out.println("<a href='/quotes/home'>Home</a> <a href='/quotes/timeline'>Timeline</a> <a href='/quotes/account'>Account</a> <a href='/quotes/logout'>Logout</a>");
	out.println("</div>");
}
else
{
%>
	<form action='/quotes/login' name='login' method='post'>

	<table class='box' id='login'>
	<tr>
	<td>Username</td>
	<td><input type='text' name='displayName' /></td>
	</tr>
	<tr>
	<td>Password</td>
	<td><input type='password' name='password' /></td>
	</tr>
	<tr>
	<td colspan='2'><input type='submit' value='Login' /> <a href="/quotes/timeline">Public Timeline</a></td>
	</tr>
	</table>

	</form>
<% } %>

<a href="/quotes/home"><img id="title" alt="Quotes" src="/quotes/Quotes.png" /></a>
</div>

<div id="content">