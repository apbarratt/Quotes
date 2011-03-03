import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.lang.reflect.*;
import java.io.PrintWriter;


//public class RenderJson {



/**
 * Servlet implementation class RenderJson
 */

@SuppressWarnings("unused")
@WebServlet("/RenderJson")
public class RenderJson extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RenderJson() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("rawtypes")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//System.out.print("It actually works");
		Object temp=request.getAttribute("Data");
		Class c = temp.getClass();
		String className=c.getName();
		if (className.compareTo("java.util.LinkedList")==0){ //Deal with a linked list
			List Data = (List)request.getAttribute("Data");
			Iterator iterator;
			JSONObject JSONObj=new JSONObject();
			JSONArray Parts=new JSONArray();
			iterator = Data.iterator();     
			while (iterator.hasNext()){
				Object Value=iterator.next();
				JSONObject obj =ProcessObject(Value);
				try {
					Parts.put(obj);
					//System.out.println("obj put in parts");
				}catch (Exception JSONet){
         			System.out.println("JSON Fault"+ JSONet);
         		}
			}
			try{
				JSONObj.put("Data",Parts);
				//System.out.println("parts put in JSONObj");
			}catch (Exception JSONet){
     			System.out.println("JSON Fault"+ JSONet);
     		}
			if (JSONObj!=null){
				//System.out.println("printing JSONObj to screen");
				PrintWriter out = response.getWriter();
				out.print(JSONObj);
			}	
			
		}else{
			System.out.println("JSONObj was null");
			Object Data=request.getAttribute("Data");
			JSONObject obj =ProcessObject(Data);
			if (obj!=null){
				//System.out.println("printing obj to screen");
				PrintWriter out = response.getWriter();
				out.print(obj);
			}	
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONObject  ProcessObject(Object Value){
		JSONObject Record=new JSONObject();
		
		try {
            Class c = Value.getClass();
            Method methlist[] = c.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++) {  
            	 Method m = methlist[i];
            	 //System.out.println(m.toString());
            	 String mName=m.getName();
            	
                 if (mName.startsWith("get")==true){
                	 String Name=mName.replaceFirst("get", "");
                	 //Class pvec[] = m.getParameterTypes(); //Get the Parameter types
	                 //for (int j = 0; j < pvec.length; j++)
	                 //   System.out.println("param #" + j + " " + pvec[j]);
	                 //System.out.println(mName+" return type = " +  m.getReturnType());
	                 Class partypes[] = new Class[0];
	                 Method meth = c.getMethod(mName, partypes);
	                
	                 Object rt= meth.invoke(Value);
	                 if (rt!=null){
	                	 //System.out.println(Name+" Return "+ rt);
	                	 try{
	                		 Record.put(Name,rt);
	                	 }catch (Exception JSONet){
	             			System.out.println("JSON Fault"+ JSONet);
	             			return null;
	             		}
	             	
	                 }
                 }
            }
            
            
         }
         catch (Throwable e) {
            System.err.println(e);
         }
         return Record;
	}


}
