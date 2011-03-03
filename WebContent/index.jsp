<%@ page language='java' contentType='text/html; charset=ISO-8859-1'
    pageEncoding='ISO-8859-1'%>


<jsp:include page="header.jsp" />


<%

if(session.getAttribute("displayName")==null)
{
%>
	<div class='box' id='register'>
	<form action='register' name='signup' method='post'>

	<h1>Signup</h1>
	<table>
	<tr>
	<td>Fullname</td>
	<td><input type='text' name='fullname' /></td>
	</tr>
	<tr>
	<td>Username</td>
	<td><input type='text' name='displayName' /></td>
	</tr>
	<tr>
	<td>Email</td>
	<td><input type='text' name='email' /></td>
	</tr>
	<tr>
	<td>Password</td>
	<td><input type='password' name='password' /></td>
	</tr>
	<tr>
	<td>Confirm</td>
	<td><input type='password' name='confirm' /></td>
	</tr>
	<tr>
	<td colspan='2'><input type='submit' value='Let Me In' /></td>
	</tr>
	</table>
	</form>
	</div>
	
	<h1>Welcome to Quotes</h1>
	<p>
	Quotes is a place to share what you've read or heard in art, music, literature or even just what the guy at the bar said.  If it can be written, you can share it with the world here.
	</p>
	
	<center>
	<img id="frontPageQuote" src="Quote.png" alt="I might repeat to myself, slowly and soothingly, a list of quotations beautiful from minds profound; if I can remember any of the damn things. by Dorothy Parker" />
	</center>
<%
}
else
{
%>
	<jsp:include page="post.jsp" />
<%
}
%>

<jsp:include page="footer.jsp" />