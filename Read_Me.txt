Files Submitted -

1. Source code
2. Project Report
3. Output Screenshots
4. Project Presentation
5. Data
6. Readme



Executing

1. Create a backend mysql database engine running with a user acces id and password. Replace this in all the java codes.
2. Create a twitter developer account and obtain an authoziation and other access keys. replace the keys in the code with the new authorizations
3. The jar files are too large (500 mb) to attach in submission . Hence i have provided a google drive link to  download all the required jars

Link -  https://drive.google.com/drive/folders/0B2yCALEhyyVVRFRiVGF4QnBkOWM?usp=sharing
place all the jar files in the build path in eclpise as external libraries

4. 3 java files are submitted - Get_results.java , Make_sentiments.java , Tweets.java
5. Tweets.java is the source code for extraction of the tweets and writes the results to a mysql table .
6. Make_sentiments.java create sentiments for each of the tweets by using stanford NLP toolkit and writes the results to the original data table in mysql.
7. Get_results.java creates aggregates the sentiments and writes the results into a results table in mysql
8. to run the the java code , copy the entire source code into eclipse and create a jar file for executing a particular class. 
