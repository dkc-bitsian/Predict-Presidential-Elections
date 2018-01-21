package getTweets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Get_results {
	
	static HashMap<String,Result> map= new HashMap<String,Result>();
	
	
	public static void main(String[] args) throws SQLException
	{
		
		String state_codes=" 'AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DC', 'DE', 'FL', 'GA', 'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME', 'MI', 'MN', 'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM', 'NV', 'NY', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY'";
		String[] sc = state_codes.split(",");
		
		for(int i=0;i<sc.length;i++)
		{
			sc[i]=sc[i].substring(2, sc[i].length()-1);
			map.put(sc[i], new Result());
		}
		
		get_stats();
		
		for(String key:map.keySet())
		{
			Result r=map.get(key);
			double h=r.hillary;
			double t=r.trump;
			double n=r.neutral;
			double total= h+t+n;
		}
		write_result();
	}


	private static void write_result() throws SQLException 
	{
		// TODO Auto-generated method stub

	Connection myconn= DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter?autoReconnect=true&useSSL=false","krishna","bangalore99");
	System.out.println ("Database connection established");
	
	// the mysql insert statement
    String q = " insert into results (state , trump , hillary ,neutral , total )"
      + " values (? , ? , ? ,? , ? )";

    // create the mysql insert preparedstatement
    PreparedStatement preparedStmt = myconn.prepareStatement(q);
   
	Statement mystat = myconn.createStatement();

	for(String key:map.keySet())
	{
		
		Result re = map.get(key);
		double total=re.hillary+re.neutral+re.trump;
	    preparedStmt.setString (1,key);
		preparedStmt.setDouble(2, re.trump);
		preparedStmt.setDouble(3, re.hillary);
		preparedStmt.setDouble(4, re.neutral);
		preparedStmt.setDouble(5, total);

	      	preparedStmt.execute();
	}
	
	
	}


	private static void get_stats() throws SQLException {
		// TODO Auto-generated method stub
		Connection myconn= DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter?autoReconnect=true&useSSL=false","usderName","Password");
		System.out.println ("Database connection established");
		int i=0;
		// the mysql insert statement
	      String q = " select * from data2";
		Statement mystat = myconn.createStatement();
		ResultSet result=mystat.executeQuery(q);
		
		
		// the mysql insert statement
	    q = " select * from data3 where category is not null";
		Statement mysta = myconn.createStatement();
		result=mystat.executeQuery(q);
		while ( result.next() ) 
		{
			i++;
			System.out.println(i);
			String state = result.getString("state");
			String category= result.getString("category");
			int sentiment= result.getInt("sentiment");
			if(map.containsKey(state))
			{
				Result r = map.get(state);

				if(category.contains("trump"))
				{
					if(sentiment>2)
					{
						r.trump=r.trump+1;
					}
					else if(sentiment==2)
					{
						r.neutral=r.neutral+1.0;
					}
					else
					{
						r.hillary=r.hillary+1.0;
					}
				}
				else
				{
					if(sentiment>2)
					{
						r.hillary=r.hillary+1;
					}
					else if(sentiment==2)
					{
						r.neutral=r.neutral+1.0;
					}
					else
					{
						r.trump=r.trump+1.0;
					}
				}
				
				
				
				map.put(state, r);
			}	
			
			
          
      }
		
        myconn.close();
		
		
	
	}
	
}

class Result
{
	double trump=0.0;
	double hillary=0.0;
	double neutral=0.0;
}
