import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

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
@WebServlet({"/post", "/delete/*", "/jsonTimeline", "/jsonUser/*", "/jsonHomeTimeline"})
public class Tweets extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String servletPath;
	PrintWriter out; //to store our printWriter in.
	
	HttpSession mySexySession;
	
	int numTweets = 20; //for timelines
       
	//--------------------------------------------------------------------------------------------------------------
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tweets() {
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
		
		System.out.println(servletPath + " accessed.");
		
		if(servletPath.equals("/post"))
		{
			response.sendRedirect("home");
		}
		if(servletPath.equals("/delete"))
		{
			doGetDelete(request, response);
		}
		else if(servletPath.equals("/jsonTimeline"))
		{
			goToTimeline(request, response);
		}
		else if(servletPath.startsWith("/jsonUser"))
		{
			goToUserTimeline(request, response);
		}
		else if(servletPath.startsWith("/jsonHomeTimeline"))
		{
			goToHomeTimeline(request, response);
		}
		else
		{
			response.sendRedirect("/QuoteByAndyBarratt/home");
		}
		
		
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGetDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String[] addressElements = SplitRequestPath(request);
		String tweetID = "UNKNOWN";
		if(addressElements.length>2)
			tweetID = addressElements[2];
		
		if(mySexySession.getAttribute("displayName")!=null)
		{
			String displayName = mySexySession.getAttribute("displayName").toString();
			boolean permissionToDelete = false;
			
			//if tweet is in displayNames's UserTweets permissionToDelete = true
				
			Keyspace keyspace = Connection.getKeyspace();
			StringSerializer se = StringSerializer.get();
			
			try
			{
				// given Keyspace keyspace and StringSerializer se
				SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
				q.setColumnFamily("UserTweets") .setKey(displayName).setColumnNames(tweetID);
				QueryResult<ColumnSlice<String, String>> r = q.execute();

				r.get().getColumnByName(tweetID).getValue();
				permissionToDelete = true;
			}
			catch (Exception e)
			{
				permissionToDelete = false;
			}
			
			if(permissionToDelete)
			{	
				Mutator<String> mutator = HFactory.createMutator(keyspace, se);
				
				mutator.delete(tweetID, "Tweets", null, se);
				
				mySexySession.setAttribute("message", "Quote deleted.");
			}
			else
			{
				mySexySession.setAttribute("message", "Permission denied.");
			}
			
			
		}
		else
		{
			mySexySession.setAttribute("message", "You must be logged in to delete posts.");
		}
		
		response.sendRedirect("/quotes/home");
	}
	
	//----------------------------------------------------------------------------------------------

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		mySexySession = request.getSession(true);
		out = response.getWriter();
		
		servletPath = request.getServletPath();
		if(servletPath.equals("/post"))
			doPostPost(request, response);
		else
			System.out.println("I've no idea how you could possibly ever see this text, if you can though, please copy the contents of the address bar and email it to apbarratt@me.com along with a description of where you came from you magical pixie you ;]");
	}
	
	
	//--------------------------------------------------------------------------------------------------------------

	/**
	 * Runs the /post page
	 */
	protected void doPostPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String quote = request.getParameter("quote");
		quote = StringEscapeUtils.escapeHtml(quote);
        String by = request.getParameter("by");
        by = StringEscapeUtils.escapeHtml(by);
        String displayName = "to be set";
        long unixTime = getUnixTime();
		
		
		
		//Cluster = Servers that database is running on
		//Keyspace = Database = ShutItCluster
		//Collumn Family = Table = Users
		//Key = Primary Key for record
		
        Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		out.println("Golly Gosh, it's the post page!");
		
		Mutator<String> mutator = HFactory.createMutator(keyspace, se);
		
		//mutator.insert(displayName, "Users", HFactory.createStringColumn("fullname", fullname));
		
		if(mySexySession.getAttribute("displayName")!=null)
		{
			displayName = mySexySession.getAttribute("displayName").toString();
			
			out.println("Poster:   " + displayName);
			out.println("Quote:    " + quote);
			out.println("by:       " + by);
			out.println("Unix Time " + unixTime);
			
			String tweetID = Long.toString(unixTime);
			
			mutator.addInsertion(tweetID, "Tweets", HFactory.createStringColumn("displayName", displayName))
			.addInsertion(tweetID, "Tweets", HFactory.createStringColumn("quote", quote))
			.addInsertion(tweetID, "Tweets", HFactory.createStringColumn("by", by))
			.addInsertion(displayName, "UserTweets", HFactory.createStringColumn(tweetID, tweetID));
			mutator.execute();
			
			System.out.println(displayName + " posted a quote.");
			
			response.sendRedirect("home");
		}
		else
		{
			mySexySession.setAttribute("message", "You must be logged in to post quotes.");
			response.sendRedirect("home");
		}
		
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	/**
	 * Returns unixTime as long.
	 */
	public long getUnixTime()
	{
		return new Date().getTime();
	}
	
	//--------------------------------------------------------------------------------------------------------------
	
	protected void goToTimeline(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{	
		//connection settings
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		//get tweets
		RangeSlicesQuery<String, String, String> rangeSlicesQuery =
			HFactory.createRangeSlicesQuery(keyspace, se, se, se);

		rangeSlicesQuery.setColumnFamily("Tweets");
		rangeSlicesQuery.setKeys("", "");
		rangeSlicesQuery.setRange("", "", true, numTweets);

		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
		
		OrderedRows<String, String, String> tweets = result.get();
		
		//store tweets in linkedList
		LinkedList<Tweet> tweetsList = new LinkedList<Tweet>();
		
			for(Row<String, String, String> tweet : tweets)
			{
				try
				{
					String tweetID = tweet.getKey();
					String quote = tweet.getColumnSlice().getColumnByName("quote").getValue();
					String by = tweet.getColumnSlice().getColumnByName("by").getValue();
					String displayName = tweet.getColumnSlice().getColumnByName("displayName").getValue();
					String howLong = howLong(Long.parseLong(tweetID));
					
					tweetsList.add(new Tweet(tweetID, quote, by, displayName, howLong));
				}
				catch(Exception e)
				{
					//likely a left over key from a tweet that's since been deleted.
					//if caught, we simply won't be adding this naughty little tweet to the linked list.
				}
			}
			
			
			//sort the linked list in reverse order
			Collections.sort(tweetsList, new TweetComparitor());
			Collections.reverse(tweetsList);
			
			//render json using linked list
			out.println("A lovely linked list has been made filled with pretty tweets.  Isn't that nice to know :)");
			
			out.println(tweetsList);
			
			
			request.setAttribute("Data", tweetsList);
			RequestDispatcher  rd = request.getRequestDispatcher("/RenderJson");
		    rd.forward(request,response);
	}
	
//--------------------------------------------------------------------------------------------------------------
	
	protected void goToUserTimeline(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		String[] addressElements = SplitRequestPath(request);
		String user = "UNKNOWN"; //don't worry, all usernames have to be lowercase so there's no one with this name.
		if(addressElements.length>2)
			user = addressElements[2];
		
		if(Account.userExists(user))
		{
			
			request.setAttribute("user", (user));
			request.setAttribute("fullname", getFullname(user));

			
				if(isFollowing(user))
				{
					mySexySession.setAttribute("following", true);
				}
				
				
				//connection settings
				Keyspace keyspace = Connection.getKeyspace();
				StringSerializer se = StringSerializer.get();
				
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
					s.setKeys(user, user);
					s.setRange("", "", false, numTweets);
					QueryResult<OrderedRows<String, String, String>> result = s
					.execute();
					tweets = result.get();
				}
				catch (Exception e) {
					mySexySession.setAttribute("message", user + " does not exist or has never posted.");
				}
				String[] tweetIDs = null;
				//for each row found
				for (Row<String, String, String> tweet : tweets)
				{
			
					slice = tweet.getColumnSlice();
			
					//get number of columns (tweetIDs) found.
					int numFound = slice.getColumns().size();
					tweetIDs = new String[numFound];
					
					int i = 0;
					//for each column found in this row
					for (HColumn<String, String> column : slice.getColumns())
					{
						// Parse column value to long
						tweetIDs[i] = column.getValue().toString();
						i++;
					}
				}
				
				
				//------------------------------------------
				//get tweets and store in linked list
				//------------------------------------------
				LinkedList<Tweet> tweetsList = new LinkedList<Tweet>();
				//for each tweet ID
				for(int i=0; i<tweetIDs.length; i++)
				{
					try
					{
					// given Keyspace keyspace and StringSerializer se
					SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
					q.setColumnFamily("Tweets") .setKey(tweetIDs[i]).setColumnNames("quote", "by");
					QueryResult<ColumnSlice<String, String>> r = q.execute();
					
					String tweetID = tweetIDs[i];
					String quote = r.get().getColumnByName("quote").getValue();
					String by = r.get().getColumnByName("by").getValue();
					String howLong = howLong(Long.parseLong(tweetID));
					tweetsList.add(new Tweet(tweetID, quote, by, user, howLong));
					}
					catch(Exception e)
					{
						//likely a left over key from a tweet that's since been deleted.
						//if caught, we simply won't be adding this naughty little tweet to the linked list.
					}
				}
				
				//RenderJson
				request.setAttribute("Data", tweetsList);
				RequestDispatcher  rd = request.getRequestDispatcher("/RenderJson");
			    rd.forward(request,response);
				
		}
		else
		{
			mySexySession.setAttribute("message", "No user with that name.");
			response.sendRedirect("/QuoteByAndyBarratt/home");
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	protected void goToHomeTimeline(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{	
		
		String [] displayNames = null;
		//declare a linked list that uses Strings
		LinkedList<String> tweetIDsList = new LinkedList<String>();
		
		//---------------------------------------
		//redirect home if no user logged in.
		//---------------------------------------
		if(mySexySession.getAttribute("displayName")==null)
		{
			mySexySession.setAttribute("message", "You must be logged in to view the tweets of people you're following.");
			response.sendRedirect("/QuoteByAndyBarratt/home");
		}
		else
		{
			
			//---------------------------------------
			//get displayName of currently logged in user
			//---------------------------------------
			String displayName = mySexySession.getAttribute("displayName").toString();
			
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
				mySexySession.setAttribute("message", "I couldn't find you... eep.");
			}
			
			
			
			
			
			//---------------------------------------
			//get up to 20 tweetIDs for each person
			//---------------------------------------
			
			for(int i=0; i<displayNames.length; i++)
			{
				try
				{
					RangeSlicesQuery<String, String, String> s = HFactory.createRangeSlicesQuery(keyspace, se, se, se);
					// Set CF
					s.setColumnFamily("UserTweets");
					// Set CF key
					s.setKeys(displayNames[i], displayNames[i]);
					s.setRange("", "", false, numTweets);
					QueryResult<OrderedRows<String, String, String>> result = s
					.execute();
					people = result.get();
				}
				catch (Exception e) {
					System.out.println("No tweets from requested user.");
				}
				
				//---------------------------------------
				//store those tweetIDs in a linked list.
				//---------------------------------------
				
				//for each row found
				for (Row<String, String, String> tweet : people)
				{
					slice = tweet.getColumnSlice();
					
					System.out.println("Number of tweetIDs found: " + slice.getColumns().size());
					
					//for each column found in this row
					for (HColumn<String, String> column : slice.getColumns())
					{
						tweetIDsList.add(column.getValue().toString());
						System.out.println("added a tweetID to the linked list");
					}
				}
			}
			
			//---------------------------------------
			//sort linked list
			//---------------------------------------
			
			Collections.sort(tweetIDsList);
			Collections.reverse(tweetIDsList);
			
			//---------------------------------------
			//at this point the linked list could theoretically have a size of subscriptions*20!
			//store the most recent 20 (numTweets) tweetIDs in an array called tweetIDs
			//---------------------------------------
			
			//create array numTweets of correct size.
			String [] tweetIDs;
			int numFound = tweetIDsList.size();
			
			System.out.println("Found " + numFound + " TweetIDs in linked list.");
			
			if(numFound<numTweets)
				tweetIDs=new String[numFound];
			else
				tweetIDs=new String[numTweets];
				
			System.out.println("Created array for them of size " + tweetIDs.length);
			
			//move the tweetIDs from the list into the array.
			for(int i=0; i<tweetIDs.length; i++)
			{
				tweetIDs[i] = tweetIDsList.pop();
			}
			
			//---------------------------------------
			//continue as with UserTimeline
			//---------------------------------------
			
			//------------------------------------------
			//get tweets and store in linked list
			//------------------------------------------
			LinkedList<Tweet> tweetsList = new LinkedList<Tweet>();
			//for each tweet ID
			for(int i=0; i<tweetIDs.length; i++)
			{
				try
				{
					// given Keyspace keyspace and StringSerializer se
					SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
					q.setColumnFamily("Tweets") .setKey(tweetIDs[i]).setColumnNames("quote", "by", "displayName");
					QueryResult<ColumnSlice<String, String>> r = q.execute();
					
					String tweetID = tweetIDs[i];
					String quote = r.get().getColumnByName("quote").getValue();
					String by = r.get().getColumnByName("by").getValue();
					String user = r.get().getColumnByName("displayName").getValue();
					String howLong = howLong(Long.parseLong(tweetID));
					tweetsList.add(new Tweet(tweetID, quote, by, user, howLong));
				}
				catch(Exception e)
				{
					//likely a left over key from a tweet that's since been deleted.
					//if caught, we simply won't be adding this naughty little tweet to the linked list.
				}
			}
			
			//RenderJson
			request.setAttribute("Data", tweetsList);
			RequestDispatcher  rd = request.getRequestDispatcher("/RenderJson");
		    rd.forward(request,response);
			
		}
	}
	
	//-------------------------------------------------------------------------------------
	
	/**
	 * Method to bubble sort the tweets into the correct order.
	 */
	public String[][] sortTweets(String[][] x)
	{
	    boolean doMore = true;
	    while (doMore)
	    {
	        doMore = false;  // assume this is last pass over array
	        for (int i=0; i<x.length-1; i++)
	        {
	            if (Long.parseLong(x[i][0]) > Long.parseLong(x[i+1][0]))
	            {
	               // exchange elements
	               String temp;
	            	
	               temp = x[i][0];
	               x[i][0] = x[i+1][0];
	               x[i+1][0] = temp;

	               temp = x[i][1];
	               x[i][1] = x[i+1][1];
	               x[i+1][1] = temp;
	               
	               temp = x[i][2];
	               x[i][2] = x[i+1][2];
	               x[i+1][2] = temp;
	               
	               temp = x[i][3];
	               x[i][3] = x[i+1][3];
	               x[i+1][3] = temp;
	               
	               doMore = true;  // after an exchange, must look again 
	            }
	        }
	    }
	    return x;
	}
	
	//-------------------------------------------------------------------------------------
	
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
	
    //-------------------------------------------------------------------------------------
	
	public static String getFullname(String displayName)
	{
		String fullname = "UNKNOWN!";
		
		Keyspace keyspace = Connection.getKeyspace();
		StringSerializer se = StringSerializer.get();
		
		try
		{
			// given Keyspace keyspace and StringSerializer se
			SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
			q.setColumnFamily("Users") .setKey(displayName).setColumnNames("fullname");
			QueryResult<ColumnSlice<String, String>> r = q.execute();

			fullname = r.get().getColumnByName("fullname").getValue();
		}
		catch (Exception e)
		{
			
		}
		return fullname;
	}
	
	//-------------------------------------------------------------------------------------
	
	/*
	 * Method to say whether the current user is following another user.
	 * @param user to check as String
	 * @return boolean: true if following.  Returns false if no user is logged in.
	 */
	private boolean isFollowing(String user)
	{
		if(mySexySession.getAttribute("displayName")!=null)
		{
			String displayName = mySexySession.getAttribute("displayName").toString();
			
			Keyspace keyspace = Connection.getKeyspace();
			StringSerializer se = StringSerializer.get();
			
			try
			{
				// given Keyspace keyspace and StringSerializer se
				SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, se, se, se);
				q.setColumnFamily("Subscriptions") .setKey(displayName).setColumnNames(user);
				QueryResult<ColumnSlice<String, String>> r = q.execute();

				r.get().getColumnByName(user).getValue();
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}
		
		return false;
	}
	
	//-------------------------------------------------------------------------------------
	
	/**
	 * Takes a unix time and returns a nice little string telling you, generally, how long ago it was.
	 * @param unixTime as Long (get it, Long? HowLong?  Ha!  I'm just brilliant.
	 * @return String: e.g. a moment ago, 2 minutes ago, 3 hours ago, 4 weeks ago, 5 months ago, 6 years ago.
	 */
	public String howLong(Long unixTime)
	{
		
		//time elapsed in seconds
		Long timeElapsed = (getUnixTime() - unixTime) / 1000;
		
		if(timeElapsed<60)
			return "a moment ago.";
		else if(timeElapsed<120)
			return "a minute ago.";
		else if(timeElapsed<3000)
			return Long.toString(timeElapsed/60) + " minutes ago.";
		else if(timeElapsed<7200)
			return "an hour ago.";
		else if(timeElapsed<72000)
			return Long.toString(timeElapsed/3600) + " hours ago.";
		else if(timeElapsed<172800)
			return "a day ago.";
		else if(timeElapsed<561600)
			return Long.toString(timeElapsed/86400) + " days ago.";
		else if(timeElapsed<1000000)
			return "about a week ago.";
		else if(timeElapsed<2419200)
			return Long.toString(timeElapsed/604800) + " weeks ago.";
		else if(timeElapsed<4838400)
			return "a month ago.";
		else if(timeElapsed<31449600)
			return Long.toString(timeElapsed/2419200) + " months ago.";
		else if(timeElapsed<62899200)
			return "a year ago.";
		else
			return Long.toString(timeElapsed/31449600) + " years ago.";
	}
	
}

