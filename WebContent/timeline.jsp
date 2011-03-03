<%@ page language='java' contentType='text/html; charset=ISO-8859-1'
    pageEncoding='ISO-8859-1'
    %>


<jsp:include page="header.jsp" />

<%
//if a user is logged in, include the posting form at the top of the page.
if(session.getAttribute("displayName")!=null)
{
%>
	<jsp:include page="post.jsp" />
<%	
}

if(request.getAttribute("user")!=null)
{
	if(request.getAttribute("fullname").toString().equals("UNKNOWN!"))
	{
		System.out.println("UNKNOWN! - Sending redirect to home");
		response.sendRedirect("/home");
	}
	out.print("<h1>" + request.getAttribute("fullname").toString() + "'s posts.</h1>");
	if(session.getAttribute("following")!=null)
	{
		out.print("<a href='/quotes/unfollow/" + request.getAttribute("user").toString() + "' id='followLink'>Unfollow</a>");
		session.removeAttribute("following");
	}
	else
	{
		out.print("<a href=\"/quotes/follow/" + request.getAttribute("user").toString() + "\" id=\"followLink\">Follow</a>");
		out.print("&nbsp;&nbsp;&nbsp;<a href=\"/quotes/stalk/" + request.getAttribute("user").toString() + "\" id=\"followLink\" title=\"Like following, but they don't know you're doing it.\">Stalk</a>");
	}
	session.removeAttribute("user");
}
else if(request.getAttribute("homeTimeline")!=null)
	out.print("<h1>Quotes Home</h1>");
else
	out.print("<h1>Public Timeline</h1>");
%>

<jsp:include page="footer.jsp" />