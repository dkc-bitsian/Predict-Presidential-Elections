package getTweets;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class Tweets 
{
	
	public static void main(String args[]) throws SQLException
	{
		try {
			getfeed();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static  void getfeed() throws SQLException, InterruptedException 
	{
		ConfigurationBuilder cb= new ConfigurationBuilder();
		
		cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("OBGyJZWHPfsQ7zXDwlBmCl0Z8")
		   .setOAuthConsumerSecret("GttcHuKNEabPQ8vSvKjUmYSmStlNcKEXe2zOGQBg8Va4AKoaKd")
		   .setOAuthAccessToken("1294123628-ZONWNgrSitK6TJMGJ9SOK65eB0yqiCqOr69JHU1")
		   .setOAuthAccessTokenSecret("sWoE7w5SZJlj8WEQiLx0WyRxh37ga3mhRoCgpVb0ioHmI");
		
		Connection myconn= DriverManager.getConnection("jdbc:mysql://localhost:3306/twitter?autoReconnect=true&useSSL=false","krishna","bangalore99");
		System.out.println ("Database connection established");
		
		// the mysql insert statement
	      String q = " insert into data2 (userid, place, tweet,lastid)"
	        + " values (?, ?, ? ,?)";

	      // create the mysql insert preparedstatement
	      PreparedStatement preparedStmt = myconn.prepareStatement(q);
	     
		Statement mystat = myconn.createStatement();
		String[] dates = {"2016-11-07" ,"2016-11-08"};
		int no_of_tweets_calls=100000;  //total tweets queried = no of tweets calls *100 ; 100 is the maximum tweet returned per call

		TwitterFactory tf= new TwitterFactory(cb.build());
		
		Twitter twit=tf.getInstance();
		
		
		String search = "trump";
		Query query = new Query("trump");
		RateLimitStatus rate_status;
		try {
			rate_status = twit.getRateLimitStatus("search").get("/search/tweets");
		
		query.setSince(dates[0]);
		query.setUntil(dates[1]);
		query.setCount((100));
		query.setLang("en");
		
		long maxid=795744006013677568l;
		
		
		for(int i=0;i<dates.length-1;i++)
		{
			int kf=0;
	        for(int ss=0 ; ss<no_of_tweets_calls ;ss++)	
	        {
	        	if(rate_status==null)
	        	{
	        		System.out.println("its null rate status");
	        		
	        	}
	        	else
	        	{
	        	System.out.printf("You have %d hits remaining out of %d, Limit resets in %d seconds\n",rate_status.getRemaining(),rate_status.getLimit(),rate_status.getSecondsUntilReset());
	        	if (rate_status.getRemaining() == 0)
	        	{
	        		System.out.println("No more hits left");
	        		System.out.println("Thread Sleeping to attain more hits");
	        		Thread.sleep((rate_status.getSecondsUntilReset()+20) * 1000l);
	        	}
	        	if(maxid !=-1)
	    		{
	    			query.setMaxId(maxid);
	    			//System.out.println(maxid);
	    			
	    		}
	        	}
	        	
	    			
	        	QueryResult  result;						
	        	for(int att=0 ; att<10 ; att++)
	        	{
	        		try
	        		{
	        			result = twit.search(query);
	        			if (result.getTweets().size() == 0)
	    	    	    {
	    	    	    	System.out.println("no more tweets in result set for i="+i);
	    	    	    	break;
	    	    	    }
	    	    	    for (Status status : result.getTweets()) 
	    	    	    {
	    	    	    	String stats;
	    	    	    	if (maxid == -1 || status.getId() < maxid)
	    	    			{
	    	    				maxid = status.getId();
	    	    			}
	    	    	        //System.out.println("@" + status.getUser().getScreenName() + " - " +status.getUser().getName()); 
	    	    	        //System.out.println("status - "+status.getText());
	    	    	    	stats=status.getText();
	    	    	    	stats = stats.replace("\n", "\\n");
	    	    	    	stats = stats.replace("\t", "\\t");
	    	    	    	Place placeJson = status.getPlace();
	    	    	    	
	    	    	    	
	    	    	    	if(placeJson!=null &&placeJson.getCountryCode().contentEquals("US"))
	    	    	    	{
	    	    	    		//@userid::::place::::tweet
	    	    	    		//output.println("@" + status.getUser().getScreenName()+"::::"+placeJson.getFullName()+"::::"+stats);
	    	    	    		//System.out.println("yes");
	    	    	            kf++;
	    	    	            preparedStmt.setString (1, status.getUser().getScreenName());
	    	    	  	      	preparedStmt.setString (2, placeJson.getFullName());
	    	    	  	      	preparedStmt.setString (3, stats);
	    	    	  	      	preparedStmt.setLong(4, maxid); 
	    	    	  	      	preparedStmt.execute();
	    	    	    	}
	    	                
	    	    	    }
	    	    	    rate_status= result.getRateLimitStatus();
	    	    	    break;
	        		}
	        		catch(TwitterException e){
	        			System.out.println("attemmpting to retry after exception");
	        			Thread.sleep(60 * 1000l);
	        		}
	        	}
	    	    
	    	    
	    	    
	    	    
	    		}
	    	    //output.close();
	    	    myconn.close();
	    	    System.out.println("tweetcount -"+kf);
	        }
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	

}
