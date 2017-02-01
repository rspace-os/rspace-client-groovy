import com.researchspace.groovy.examples.*
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

 
    String key = System.getProperty("key")?:""
	String rspaceUrl = System.getProperty("url")?:""
	if (key.empty || rspaceUrl.empty ){
		println " Please supply a  key [${key.empty?'':'supplied'}] and a URL [$rspaceUrl]"
		return
	}

	DocumentAsCSV csvRetriever = new DocumentAsCSV (key: key, rspaceUrl: rspaceUrl)
	
	//get's all documents created from 'BasicDocument' form in CSV format.
	// This is useful for extracting content to put in a database, for example. 
	csvRetriever.searchByFormAndRetrieveAsCSV("Basic Document")
	
	
	