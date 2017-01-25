import com.researchspace.groovy.examples.*
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

    //replace this with your RSpace URL
	rspaceUrl = "https://demo.researchspace.com/api/v1";
//	 
//	//Set in your RSpace API key here via a -D command line property
	key = System.getProperty("apiKey")
 

	DocumentAsCSV csvRetriever = new DocumentAsCSV (key: key, rspaceUrl: rspaceUrl)
	
	//get's all documents created from 'BasicDocument' form in CSV format.
	// This is useful for extracting content to put in a database, for example. 
	csvRetriever.searchByFormAndRetrieveAsCSV("Basic Document")
	
	
	