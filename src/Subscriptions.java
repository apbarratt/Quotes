import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.StringTokenizer;

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



/**
 * Servlet implementation class Account
 */
@WebServlet({"/follow/*", "/unfollow/*", "/stalk/*", "/easteregg" })
public class Subscriptions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String servletPath;
	PrintWriter out; //to store our printWriter in.
	
	
	HttpSession mySexySession;
       
	//--------------------------------------------------------------------------------------------------------------
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Subscriptions() {
        //super();
        
    }
    
  //--------------------------------------------------------------------------------------------------------------

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		
		mySexySession = request.getSession(true);
		out = response.getWriter();
		
		servletPath = request.getServletPath();
		
		if(servletPath.equals("/follow"))
		{
			doGetFollow(request, response);
		}
		else if(servletPath.equals("/stalk"))
		{
			doGetFollow(request, response);
		}
		else if(servletPath.equals("/unfollow"))
		{
			doGetUnfollow(request, response);
		}
		else if(servletPath.equals("/following"))
		{
			response.sendRedirect("home");
		}
		else if(servletPath.equals("/followers"))
		{
			response.sendRedirect("home");
		}
		else
		{
			out.println("<html><META HTTP-EQUIV='Refresh' CONTENT='0;URL=http://bit.ly/77X1oO'><head><title>Easter Egg</title><body></body></html>");
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
		
		
		
	}
	
	
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * stuff to do when Follow link is used.
	 */
	protected void doGetFollow(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		
		
		boolean stalk = false;
		String user = "UNKNOWN";
		String[] addressElements = SplitRequestPath(request);
		if(addressElements.length>2)
			user = addressElements[2];
		if(addressElements[1].equals("stalk"))
			stalk = true;
		
		//----------------------------------
		
		if(Account.userExists(user))
		{
		
		
			if(mySexySession.getAttribute("displayName")==null)
			{
				mySexySession.setAttribute("message", "You must be logged in to follow people.");
				response.sendRedirect("/quotes/user/" + user);
			}
			else
			{
			
				mySexySession = request.getSession(true);
				out = response.getWriter();	
				servletPath = request.getServletPath();
				String displayName = mySexySession.getAttribute("displayName").toString();
				
				System.out.println("Request to follow " + user + " made by " + displayName + ".");
				
				//------------------------------------
				
				//Cassandra settings
				Keyspace keyspace = Connection.getKeyspace();
				StringSerializer se = StringSerializer.get();
				
				Mutator<String> mutator = HFactory.createMutator(keyspace, se);
				
				mutator.addInsertion(displayName, "Subscriptions", HFactory.createStringColumn(user, user));
				if(!stalk)
					mutator.addInsertion(user, "Followers", HFactory.createStringColumn(displayName, displayName));
				mutator.execute();
				
				System.out.println("Subscription Added");
				
				if(stalk)
					mySexySession.setAttribute("message", "You are now stalking " + user + ".");
				else
					mySexySession.setAttribute("message", "You are now following " + user + ".");
				
				mySexySession.setAttribute("following", "true");
				response.sendRedirect("/quotes/user/" + user);
			
			}
		
		}
		else
		{
			mySexySession.setAttribute("message", "No user with that name.");
			response.sendRedirect("/quotes/home");
		}
		
		
	}
	
	
//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * stuff to do when Follow link is used.
	 */
	protected void doGetUnfollow(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		String user = "UNKNOWN";
		String[] addressElements = SplitRequestPath(request);
		if(addressElements.length>2)
			user = addressElements[2];
		
		//----------------------------------
		
		if(Account.userExists(user))
		{
		
			if(mySexySession.getAttribute("displayName")==null)
			{
				mySexySession.setAttribute("message", "You must be logged in to unfollow people.");
				response.sendRedirect("/quotes/user/" + user);
			}
			else
			{
			
				mySexySession = request.getSession(true);
				out = response.getWriter();	
				servletPath = request.getServletPath();
				String displayName = mySexySession.getAttribute("displayName").toString();
				
				System.out.println("Request to unfollow " + user + " made by " + displayName + ".");
				
				//------------------------------------
				
				//Cassandra settings
				Keyspace keyspace = Connection.getKeyspace();
				StringSerializer se = StringSerializer.get();
				
				Mutator<String> mutator = HFactory.createMutator(keyspace, se);
				
				mutator.addDeletion(displayName, "Subscriptions", user, se)
					   .addDeletion(user, "Followers", displayName, se);
				mutator.execute();
				
				System.out.println("Subscription Removed");
				
				mySexySession.removeAttribute("following");
				mySexySession.setAttribute("message", "You are no longer following " + user + ".");
				response.sendRedirect("/quotes/user/" + user);
			
			}
		
		}
		else
		{
			mySexySession.setAttribute("message", "No user with that name.");
			response.sendRedirect("/quotes/home");
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	/*
	 * returns the people the provided displayName is following
	 */
	public static String[] getSubscriptions(String displayName)
	{
		String [] displayNames = null;
		
		//---------------------------------------
		//connection settings
		//---------------------------------------
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		//---------------------------------------
		//get names of people the user is following.
		//---------------------------------------
		
		OrderedRows<String, String, String> people = null;
		ColumnSlice<String, String> slice = null;
		
		try
		{
			RangeSlicesQuery<String, String, String> s = HFactory.createRangeSlicesQuery(keyspace, se, se, se);
			// Set CF
			s.setColumnFamily("Subscriptions");
			// Set CF key
			s.setKeys(displayName, displayName);
			s.setRange("", "", false, 100);
			QueryResult<OrderedRows<String, String, String>> result = s
			.execute();
			people = result.get();
			
			System.out.println("Number of rows found: " + people.getCount());
			
			//---------------------------------------
			//Store those people in an array called displayNames.
			//---------------------------------------
			
			
			
			//for each row found
			for (Row<String, String, String> tweet : people)
			{
				slice = tweet.getColumnSlice();
				
				System.out.println("Number of people found: " + slice.getColumns().size());
				//initialise array with size.
				displayNames = new String[slice.getColumns().size()];
				
				//for each column found in this row
				int i = 0;
				for (HColumn<String, String> column : slice.getColumns())
				{
					displayNames[i] = column.getValue().toString();
					System.out.println("added a person to the displayNames Array");
					i++;
				}
			}
			
			
		}
		catch (Exception e) {
			System.out.println("Couldn't find requested displayName in getSubscriptions()");
		}
		
		
		return displayNames;
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * returns the people following the provided displayName.
	 */
	public static String[] getFollowers(String displayName)
	{
		String [] displayNames = null;
		
		//---------------------------------------
		//connection settings
		//---------------------------------------
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		//---------------------------------------
		//get names of people the user is following.
		//---------------------------------------
		
		OrderedRows<String, String, String> people = null;
		ColumnSlice<String, String> slice = null;
		
		try
		{
			RangeSlicesQuery<String, String, String> s = HFactory.createRangeSlicesQuery(keyspace, se, se, se);
			// Set CF
			s.setColumnFamily("Followers");
			// Set CF key
			s.setKeys(displayName, displayName);
			s.setRange("", "", false, 100);
			QueryResult<OrderedRows<String, String, String>> result = s
			.execute();
			people = result.get();
			
			System.out.println("Number of rows found: " + people.getCount());
			
			//---------------------------------------
			//Store those people in an array called displayNames.
			//---------------------------------------
			
			
			
			//for each row found
			for (Row<String, String, String> tweet : people)
			{
				slice = tweet.getColumnSlice();
				
				System.out.println("Number of people found: " + slice.getColumns().size());
				//initialise array with size.
				displayNames = new String[slice.getColumns().size()];
				
				//for each column found in this row
				int i = 0;
				for (HColumn<String, String> column : slice.getColumns())
				{
					displayNames[i] = column.getValue().toString();
					System.out.println("added a person to the displayNames Array");
					i++;
				}
			}
			
			
		}
		catch (Exception e) {
			System.out.println("Couldn't find requested displayName in getFollowers()");
		}
		
		
		return displayNames;
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	private String[] SplitRequestPath(HttpServletRequest request)
    {
    	String args[] = null;


    	StringTokenizer st = SplitString(request.getRequestURI());
    	args = new String[st.countTokens()];
    	//Lets assume the number is the last argument

    	int argv=0;
    	while (st.hasMoreTokens ()) {;
    	args[argv]=new String();

    	args[argv]=st.nextToken();
    	try{
    	System.out.println("String was "+URLDecoder.decode(args[argv],"UTF-8"));
    	args[argv]=URLDecoder.decode(args[argv],"UTF-8");

    	}catch(Exception et){
    	System.out.println("Bad URL Encoding"+args[argv]);
    	}
    	argv++;
    	}

    	//so now they'll be in the args array.
    	// argv[0] should be the user directory

    	return args;
    }

	private StringTokenizer SplitString(String str)
	{
		return new StringTokenizer (str,"/");
	}
	
}

