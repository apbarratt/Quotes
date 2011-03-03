<%@ page language='java' contentType='text/html; charset=ISO-8859-1'
    pageEncoding='ISO-8859-1'%>

<jsp:include page="header.jsp" />

<%

boolean printForm = true;

String displayName = "Unknown";

	//if not logged in
	if(session.getAttribute("displayName")==null)
	{
			response.sendRedirect("home");
	}
	//logged in
	else
	{
		displayName = session.getAttribute("displayName").toString();
		
		//if attempted closure has happened
		if(session.getAttribute("closed")!=null)
		{
			if(session.getAttribute("closed").toString().equals("false"))
			{
				out.println("<h2>INCORRECT PASSWORD</h2>");
				session.removeAttribute("closed");
			}
			else
			{
					printForm=false;
					session.invalidate();
					%>
					<h1>Account Closed.</h1>
					We're sorry to see you go :'(<br />
					<a href="logout">Home</a>
					<%
			}
		}
		//if printForm True
		if(printForm)
		{%>
		
			<h1>But we'll miss you :'(</h1>
		
			<p>We hope you don't really want to leave, but if you do, just type your password below and click the button.</p>
			
			<p>This will delete all your posts, your account and make your display name available for others to register.</p>
			
			<form action="leave" method="post" name="leave">
			
			Password <input type="password" name="password" />
			
			<input type="submit" value="Close Account: <% out.print(displayName); %>" />
			
			</form>
		
	<%  }
	}
%>


		

<jsp:include page="footer.jsp" />