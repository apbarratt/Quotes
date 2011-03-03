<%@ page language='java' contentType='text/html; charset=ISO-8859-1'
    pageEncoding='ISO-8859-1'%>

<jsp:include page="header.jsp" />



<h1>Change Password</h1>

<form action="password" method="post">

Current Password<br />
<input type="password" name="current" /><br />

New Password<br />
<input type="password" name="new" /><br />

Confirm<br />
<input type="password" name="confirm" /><br />
<br />
<input type="submit" value="Change Password" />

</form>
		

<jsp:include page="footer.jsp" />