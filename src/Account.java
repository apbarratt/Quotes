import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;



/**
 * Servlet implementation class Account
 */
@WebServlet({"/register", "/login", "/logout", "/account", "/home", "/leave", "/password" })
public class Account extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String servletPath;
	PrintWriter out; //to store our printWriter in.
	
	//possible parameters
	String fullname;
	String displayName;
	String email;
	String password;
	String confirm;
	
	
	HttpSession mySexySession;
       
	//--------------------------------------------------------------------------------------------------------------
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Account() {
        //super();
        
    }
    
  //--------------------------------------------------------------------------------------------------------------

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		mySexySession = request.getSession(true);

		
		servletPath = request.getServletPath();
		if(servletPath.equals("/logout"))
		{
			mySexySession.invalidate();
			response.sendRedirect("/quotes/home");
		}
		else if(servletPath.equals("/account"))
		{
			goToAccount(request, response);
		}
		else if(servletPath.equals("/password"))
		{
			RequestDispatcher  rd = request.getRequestDispatcher("password.jsp");
	    	rd.forward(request,response);
		}
		else if(servletPath.equals("/leave"))
		{
			goToCloseAccount(request, response);
		}
		else if(servletPath.equals("/home"))
		{
			goToHome(request, response);
		}
		else
		{
			response.sendRedirect("/quotes/home");
		}
		
		
	}
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		mySexySession = request.getSession(true);
		
		
		out = response.getWriter();
		servletPath = request.getServletPath();
		if(servletPath.equals("/login"))
			doPostLogin(request, response);
		else if(servletPath.equals("/register"))
			doPostRegister(request, response);
		else if(servletPath.equals("/leave"))
			closeAccount(request, response);
		else if(servletPath.equals("/password"))
		{
			changePassword(request, response);
		}
		else
			System.out.println("I've no idea how you could possibly ever see this text, if you can though, please copy the contents of the address bar and email it to apbarratt@me.com along with a description of where you came from you magical pixie you ;]");
	}
	
	
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * Runs the /login page
	 */
	protected void doPostLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		out.println("Look where we are, it's the login page");
		
		//get parameter Names
		setParameters(request);
		
		
		
		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		try
		{
			// given Keyspace keyspace and StringSerializer se
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
			q.setColumnFamily("Users") .setKey(displayName).setColumnNames("fullname", "email", "password");
			QueryResult<ColumnSlice<String, String>> r = q.execute();

			fullname = r.get().getColumnByName("fullname").getValue();
			email = r.get().getColumnByName("email").getValue();

			if(password.equals(r.get().getColumnByName("password").getValue()))
			{
				//LOGGED IN CORRECTLY
				
				mySexySession.setAttribute("displayName", displayName);
				mySexySession.setAttribute("fullname", fullname);
			}
			else
			{
				mySexySession.setAttribute("message", "Incorrect Password.");
			}
		}
		catch (Exception e)
		{
			//catches happen when the key is not found (i.e. user doesn't exist)
			mySexySession.setAttribute("message", "User does not exist.");
		}
		response.sendRedirect("/quotes/home");
	}
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * Runs the /register page
	 */
	protected void doPostRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Cluster = Servers that database is running on
		//Keyspace = Database = ShutItCluster
		//Collumn Family = Table = Users
		//Key = Primary Key for record
		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		out.println("Goodness me, it's the register page!");
		
		//get parameter Names
		setParameters(request);
		
		if(!Account.userExists(displayName))
		{
			
			
			boolean validated=true;
			if(!Validator.validateEmail(email))
			{
				mySexySession.setAttribute("message", "Not a valid email address.");
				validated=false;
			}
			else if(!Validator.validateAlphanumeric(displayName, false))
			{
				mySexySession.setAttribute("message", "Not a valid display name.");
				validated=false;
			}
			else if(!Validator.validateAlphanumeric(fullname, true))
			{
				mySexySession.setAttribute("message", "Not a valid fullname.");
				validated=false;
			}
			
			if(validated)
			{
				//String tweetsColumnFamily = displayName + "sTweets";
				
				//HFactory.createColumnFamilyDefinition(keyspace, tweetsColumnFamily);
				
				Mutator<String> mutator = HFactory.createMutator(keyspace, se);
				
				//mutator.insert(displayName, "Users", HFactory.createStringColumn("fullname", fullname));
				
				if(password.equals(confirm))
				{
					
					out.println("Display Name: " + displayName);  //Key
					out.println("Full Name:    " + fullname);     //
					out.println("Email:        " + email);        //
					
					mutator.addInsertion(displayName, "Users", HFactory.createStringColumn("fullname", fullname))
					.addInsertion(displayName, "Users", HFactory.createStringColumn("email", email))
					.addInsertion(displayName, "Users", HFactory.createStringColumn("password", password))
					.addInsertion(displayName, "Subscriptions", HFactory.createStringColumn(displayName, displayName))
					.addInsertion(displayName, "Followers", HFactory.createStringColumn(displayName, displayName));
					mutator.execute();
		
					
					//REGISTERED CORRECTLY
					mySexySession.setAttribute("displayName", displayName);
					mySexySession.setAttribute("fullname", fullname);
					
					response.sendRedirect("home");
				}
				else
				{
					mySexySession.setAttribute("message", "The passwords you entered did not match.");
					response.sendRedirect("home");
				}
			
			}
			else
			{
				response.sendRedirect("home");
			}
		
		}
		
		else
		{
			mySexySession.setAttribute("message", "Username already taken.");
			response.sendRedirect("/quotes/home");
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * Sets the received parameter Names
	 * @param request
	 */
	protected void setParameters(HttpServletRequest request)
	{
		
		fullname = request.getParameter("fullname");
		displayName = request.getParameter("displayName").toLowerCase();
		email = request.getParameter("email");
		password = request.getParameter("password");
		confirm = request.getParameter("confirm");
	}

	//--------------------------------------------------------------------------------------------------------------
	
	protected String getFullname(String displayName)
	{
		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		try
		{
			// given Keyspace keyspace and StringSerializer se
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
			q.setColumnFamily("Users") .setKey(displayName).setColumnNames("fullname");
			QueryResult<ColumnSlice<String, String>> r = q.execute();

			return r.get().getColumnByName("fullname").getValue();
			
		}
		catch (Exception e)
		{
			//out.println("User not found.");
		}
		return "Error.";
		
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected String getEmail(String displayName)
	{
		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		try
		{
			// given Keyspace keyspace and StringSerializer se
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
			q.setColumnFamily("Users") .setKey(displayName).setColumnNames("email");
			QueryResult<ColumnSlice<String, String>> r = q.execute();

			return r.get().getColumnByName("email").getValue();
			
		}
		catch (Exception e)
		{
			//out.println("User not found.");
		}
		return "Error.";
		
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void goToAccount(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		if(mySexySession.getAttribute("displayName")==null)
		{
			response.sendRedirect("home");
		}
		else
		{	
			displayName = mySexySession.getAttribute("displayName").toString();
			request.setAttribute("fullName", getFullname(displayName));
			request.setAttribute("email", getEmail(displayName));
			
			String [] subscriptions = Subscriptions.getSubscriptions(displayName);
			String [] followersArray = Subscriptions.getFollowers(displayName);
			String following = "";
			String followers = "";
			for(int i=0; i<subscriptions.length; i++)
			{
				following += "<a href='/quotes/user/" + subscriptions[i] + "'>" + subscriptions[i] + "</a> ";
			}
			for(int i=0; i<followersArray.length; i++)
			{
				followers += "<a href='/quotes/user/" + followersArray[i] + "'>" + followersArray[i] + "</a> ";
			}
			
			
			request.setAttribute("following", following);
			request.setAttribute("followers", followers);
			
			
			try
			{
				RequestDispatcher  rd = request.getRequestDispatcher("account.jsp");
		    	rd.forward(request,response);
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void changePassword(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		password = request.getParameter("current");
		String newPassword = request.getParameter("new");
		confirm = request.getParameter("confirm");
		
		if(newPassword.equals(confirm))
		{
			
			if(checkPassword())
			{
				Keyspace keyspace = Connection.getKeyspace();
				StringSerializer se = StringSerializer.get();
				
				Mutator<String> mutator = HFactory.createMutator(keyspace, se);
				
				mutator.addInsertion(displayName, "Users", HFactory.createStringColumn("password", newPassword));
				mutator.execute();
				
				mySexySession.setAttribute("message", "Password changed.");
				response.sendRedirect("/quotes/account");
			}
			else
			{
				mySexySession.setAttribute("message", "Wrong Password.");
				response.sendRedirect("/quotes/password");
			}
			
		}
		else
		{
			mySexySession.setAttribute("message", "New passwords don't match.");
			response.sendRedirect("/quotes/password");
		}
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void goToCloseAccount(HttpServletRequest request, HttpServletResponse response)
	{
		try
		{
			RequestDispatcher  rd = request.getRequestDispatcher("closeAccount.jsp");
	    	rd.forward(request,response);
		}
		catch(Exception e)
		{
			
		}
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void goToHome(HttpServletRequest request, HttpServletResponse response)
	{
		if(mySexySession.getAttribute("displayName")==null)
		{
			System.out.println("Home -> index.jsp");
			try
			{
				RequestDispatcher  rd = request.getRequestDispatcher("/index.jsp");
		    	rd.forward(request,response);
			}
			catch(Exception e)
			{
				
			}
		}
		else
		{
			System.out.println("Home -> ");
			try
			{
				RequestDispatcher  rd = request.getRequestDispatcher("/homeTimeline");
		    	rd.forward(request,response);
			}
			catch(Exception e)
			{
				
			}
		}
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void closeAccount(HttpServletRequest request, HttpServletResponse response)
	{
		displayName = mySexySession.getAttribute("displayName").toString();
		password = request.getParameter("password");
		
		if(checkPassword())
		{
			Keyspace keyspace = Connection.getKeyspace();
			StringSerializer se = StringSerializer.get();
			
			Mutator<String> mutator = HFactory.createMutator(keyspace, se);
			
			mutator.delete(displayName, "Users", null, se);
			
			//---------------------------------
			//delete all tweets by this user
			//---------------------------------
			
			//------------------------------------------
			//get tweetIDs
			//------------------------------------------
			OrderedRows<String, String, String> tweets = null;
			ColumnSlice<String, String> slice = null;
			
			try
			{
				RangeSlicesQuery<String, String, String> s = HFactory.createRangeSlicesQuery(keyspace, se, se, se);
				// Set CF
				s.setColumnFamily("UserTweets");
				// Set CF key
				s.setKeys(displayName, displayName);
				s.setRange("", "", false, 100);
				QueryResult<OrderedRows<String, String, String>> result = s
				.execute();
				tweets = result.get();
			}
			catch (Exception e) {
				
			}
			
			//for each row found
			for (Row<String, String, String> tweet : tweets)
			{
				slice = tweet.getColumnSlice();
				
				//for each column found in this row
				for (HColumn<String, String> column : slice.getColumns())
				{
					String tweetID = column.getValue().toString();
					mutator.delete(tweetID, "Tweets", null, se);
				}
			}
			
			mySexySession.setAttribute("closed", "true");
		}
		else
		{
			mySexySession.setAttribute("closed", "false");
		}
		goToCloseAccount(request, response);
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected boolean checkPassword()
	{
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		try
		{
			// given Keyspace keyspace and StringSerializer se
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
			q.setColumnFamily("Users") .setKey(displayName).setColumnNames("password");
			QueryResult<ColumnSlice<String, String>> r = q.execute();

			if(password.equals(r.get().getColumnByName("password").getValue()))
			{
				return true;
			}
		}
		catch (Exception e)
		{
			
		}
		return false;
	}
	
	//--------------------------------------------------------------------------------------------------
	
	/*
	 * Method to say whether the specified user exists.
	 * @param user to check as String
	 * @return boolean: true if exists.
	 */
	public static boolean userExists(String user)
	{		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
			
			try
			{
				// given Keyspace keyspace and StringSerializer se
				SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
				q.setColumnFamily("Users") .setKey(user).setColumnNames("fullname");
				QueryResult<ColumnSlice<String, String>> r = q.execute();

				r.get().getColumnByName("fullname").getValue();
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
	}
	
}

