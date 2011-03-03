import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;



/**
 * Servlet implementation class Timelines
 */
@WebServlet({"/timeline", "/homeTimeline", "/user/*"})
public class Timelines extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String servletPath;
	PrintWriter out; //to store our printWriter in.
	
	
	HttpSession mySexySession;
       
	//--------------------------------------------------------------------------------------------------------------
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Timelines() {
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
		
		request.setAttribute("timelinePage", true);
		
		if(servletPath.equals("/homeTimeline"))
		{
			request.setAttribute("homeTimeline", true);
		}
		else if(servletPath.equals("/user"))
		{
			String[] addressElements = SplitRequestPath(request);
			String user = "UNKNOWN!"; //don't worry, all usernames have to be lowercase so there's no one with this name.
			if(addressElements.length>2)
				user = addressElements[2];
			//show relevent Timeline
			request.setAttribute("user", user);
			request.setAttribute("fullname", Tweets.getFullname(user));
		}
		
		RequestDispatcher  rd = request.getRequestDispatcher("/timeline.jsp");
    	rd.forward(request,response);
		
		
	}
	
	//-----------------------------------------------------------------------------
	
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

