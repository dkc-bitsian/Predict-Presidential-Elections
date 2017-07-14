package getTweets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;
import edu.stanford.nlp.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import twitter4j.conf.ConfigurationBuilder;


public class Make_sentiments
{
	public static void main (String[] args) throws SQLException
	{
		//String tweet ="if ur for hillary...go vote\nif ur for trump..go vote\nif ur unsure, read on them before, at least you know you chose for what you believe in";
		//System.out.println(tweet);
		//System.out.println(analyze_hashtags(tweet_process(tweet.toLowerCase())));
		//System.out.println(find_category(tweet.toLowerCase()));
		update_table();
	}
	static void update_table() throws SQLException
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
	      String q = " select * from data2";
	      
	      String update = "UPDATE data2 SET state = ?, sentiment = ? ,category = ? where userid=? and tweet=?";

	  	// create the mysql insert preparedstatement
	  	PreparedStatement preparedStmt = myconn.prepareStatement(update);
		Statement mystat = myconn.createStatement();
		ResultSet result=mystat.executeQuery(q);
		while ( result.next() ) 
		{
			String place = result.getString("place");
			String tweet= result.getString("tweet").toLowerCase();
			String userid= result.getString("userid");
			String category=null;
			String processed_tweet = tweet_process(tweet);
			int sentiment = analyze_hashtags(processed_tweet);
			if(sentiment!=100)
			{
				if(sentiment<0)
				{
					category= "hillary";
					sentiment= sentiment*-1;
					
				}
				else
				{
					category="trump";
												
							
				}
			}
			else
			{
				category=find_category(tweet);
				sentiment= findSentiment(processed_tweet);
				
			}
			 
			String state = find_state(place);
			
            
            	preparedStmt.setString (1,state);
	  	      	preparedStmt.setInt (2,sentiment);
	  	      	preparedStmt.setString (3,category);
            	preparedStmt.setString (4,userid);
            	preparedStmt.setString (5,tweet);

	  	      	preparedStmt.execute();
            
            //one more thing is to be done
            
        }
        myconn.close();
		
		
	}
	private static String find_category(String tweet) {
		// TODO Auto-generated method stub
		
		//tweet=tweet.toLowerCase();
		if((tweet.contains("trump")||tweet.contains("donald")||tweet.contains("dtrump"))&&(tweet.contains("clinton")||tweet.contains("hillary")||tweet.contains("hilary")))
		{
			
			int a= count("trump",tweet)+count("donald",tweet)+count("him",tweet);
			int b= count("clinton",tweet)+count("hillary",tweet)+count("hilary",tweet)+count("her",tweet)+count("she",tweet);
			if(a>b)
			{
				return "trump";
			}
			else
			{
				return "hillary";
			}
			
		}
		else if(tweet.contains("trump")||tweet.contains("donald")||tweet.contains("dtrump"))
		{
			return "trump";
		}
		else
		{
			return "hillary";
		}
		
	}
	private static int count(String word, String tw) {
		// TODO Auto-generated method stub
		String tweet=tw;
		int i = tweet.indexOf(word);
		int c=0;
		while (i != -1) {
		    c++;
		    tweet = tweet.substring(i+1);
		    i = tweet.indexOf(word);
		}
				
		return c;
	}
	private static int analyze_hashtags(String tweet) {
		// TODO Auto-generated method stub
		
		String[] protrump ={"trumpforpresident","americansfortrump","blacksfortrump","hillaryforprison","donaldjtrump","americafirst","votetrump","hillaryforprison2016","crookedhillary","lockherup","trumppresident","makeamericagreatagain","maga","votefortrump","TrumpTrain","DonaldTrump","hillary4prison","draintheswamp","buildthewall"};
		String[] prohillary={"nevertrump","hillary","clinton","imwithher","imwither","MakeDonaldDrumpfAgain","dictatortrump","voteforhillary","LoveTrumpsHate","NeverTrump","hillary2016"};
	    String[] tw= tweet.split(" ");	
		
		for(String tt :tw)
		{
			if(tt.contains("#"))
			{
				for(String s: protrump)
				{
					String ss=s.toLowerCase();
					if(tt.contains(ss))
					{
						return 3;
					}
				}
				for(String ww:prohillary)
					{
						String lo =ww.toLowerCase();
						if(tt.contains(lo))
							return -3;
					}
				
			}
		}
		
		return 100;
	}
	private static String tweet_process(String tweet) {
		// TODO Auto-generated method stub
		//System.out.println(tweet);
		tweet = tweet.replaceAll("((www\\.[\\s]+)|(https?://[^\\s]+))", "");
		tweet = tweet.replaceAll("@([^\\s]+)", "");
		tweet = tweet.replaceAll("[1-9]\\s*(\\w+)", "");
		tweet = tweet.replaceAll("([a-z])\\1{1,}", "$1");
		//System.out.println(tweet);
		//tweet=tweet.toLowerCase();
		return tweet;
		
	}
	private static String find_state(String place) {
		// TODO Auto-generated method stub
		String state_names =" 'Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado', 'Connecticut', 'Delaware', 'District of Columbia', 'Florida', 'Georgia', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana', 'Maine', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'Mississippi', 'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire', 'New Jersey', 'New Mexico', 'New York', 'North Carolina', 'North Dakota', 'Ohio', 'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island', 'South Carolina', 'South Dakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 'Virginia', 'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'";
		String state_codes=" 'AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DC', 'DE', 'FL', 'GA', 'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME', 'MI', 'MN', 'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM', 'NV', 'NY', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY'";
		String[] sn = state_names.split(",");
		String[] sc = state_codes.split(",");
		for(int i=0;i<sn.length;i++)
		{
			//System.out.println(sn[i].substring(2, sn[i].length()-1));
			sn[i]=sn[i].substring(2, sn[i].length()-1);
		}
		for(int i=0;i<sc.length;i++)
		{
			//System.out.println(sn[i].substring(2, sn[i].length()-1));
			sc[i]=sc[i].substring(2, sc[i].length()-1);
		}
		if(place.contains(","))
		{
		String[] s=place.split(",");
		if(s[1].contains("USA"))
		{
			for(int i=0; i<sn.length;i++)
			{
				if(s[0].contains(sn[i]))
				{
					return sc[i];
				}

			}
		}
		else
		{
			return(s[1].substring(1, s[1].length()));
		}
		}
		return place;
		
	}
	public static int findSentiment(String tweet) {
		Properties property = new Properties();
        property.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(property);
        int sentiment = 0;
        if (tweet != null && tweet.length() > 0) 
        {
            int max = 0;
            Annotation annot_tree = pipeline.process(tweet);
            for (CoreMap sent_tree : annot_tree.get(CoreAnnotations.SentencesAnnotation.class)) 
            {
                Tree new_tree = sent_tree.get(SentimentAnnotatedTree.class);
                int raw_prediction = RNNCoreAnnotations.getPredictedClass(new_tree);
                String raw_text = sent_tree.toString();
                if (raw_text.length() > max) 
                {
                    sentiment = raw_prediction;
                    max = raw_text.length();
                }
            }
        }
       //System.out.println(mainSentiment);
       return sentiment;
    }
        
    }

