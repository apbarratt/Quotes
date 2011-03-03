<%@ page language='java' contentType='text/html; charset=ISO-8859-1'
    pageEncoding='ISO-8859-1'%>

<jsp:include page="header.jsp" />

<%

//declare all variables, place "Not received" in them, this should be overwritten
String displayName = "Not received";
String fullName = "Not received";
String email = "Not received";
String following = "Not received";
String followers = "Not received";


	if(session.getAttribute("displayName")==null)
	{
		response.sendRedirect("home");
	}
	else
	{	
		
		displayName = session.getAttribute("displayName").toString();
		if(request.getAttribute("fullName")!=null)
			fullName = request.getAttribute("fullName").toString();
		if(request.getAttribute("email")!=null)
			email = request.getAttribute("email").toString();
		if(request.getAttribute("following")!=null)
			following = request.getAttribute("following").toString();
		if(request.getAttribute("followers")!=null)
			followers = request.getAttribute("followers").toString();

		//out.println("<h2>Get account details code commented out!</h2>");
	}
	
%>
<h1>Your Account</h1>
<table border="1">
<tr>
<td>Username</td>
<td>
<a href="user/<% out.print(displayName); %>" title="Click to see your tweets"><% out.println(displayName); %></a>
</td>
</tr>
<tr>
<td>Full Name</td>
<td>
<% out.println(fullName); %>
</td>
</tr>
<tr>
<td>Email</td>
<td>
<% out.println(email); %>
</td>
</tr>
<tr>
<td>Following</td>
<td>
<% out.println(following); %>
</td>
</tr>
<tr>
<td>Followers</td>
<td>
<% out.println(followers); %>
</td>
</tr>
</table>
<a href="password">Change Password</a><br />
<a href="leave">Close Account</a>

<jsp:include page="footer.jsp" />