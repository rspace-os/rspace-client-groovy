import com.researchspace.groovy.examples.*
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

    //replace this with your RSpace URL
//	rspaceUrl = "https://demo.researchspace.com/api/v1";
//	 
//	//Set in your RSpace API key here via a -D command line property
//	key = System.getProperty("apiKey")
//	 
//	// A Fixed DELAY_MILLIS between requests in millis
//	DELAY_MILLIS = 1000
//	 
//	// by default we'll order results by name
//	documents = rspaceUrl + "/documents?orderBy=name%20asc"
	 
	//searchByDateRangeLastModified("2016-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
  //  searchByDateRangeCreated("2015-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
	 
//	search = [operand:"or",terms:[[query:"2015-07-06", queryType:"lastModified"],
//		                            [query:"2015-07-07", queryType:"lastModified"] ]]
	 
//	search = [terms:[[query:"Basic Document", queryType:"form"]]]
//	          
//	doSearch (search)
	
	
	//searchByFormAndRetrieveAsCSV("Basic Document")
	
	new DocumentAsCSV ().searchByFormAndRetrieveAsCSV("Basic Document")
	
	
	// retrieves documents one page at a time by looking for URLs in 'next' links
	def iterateDocs (String url) {
		def http = new HTTPBuilder(url)
		http.request(GET, JSON) { req ->
			headers.'apiKey' = key
			response.success = { resp, json ->
				println "Query response: ${json}"
				println "Looking at page ${json.pageNumber}"
				println(String.format("%-50s%-10s%-30s%-20s" , "Name", "Id", "LastModified", "Owner"))
				json.documents.each  {println(String.format("%-50s%-10s%-30s%-20s", 
					 it.name, it.id, it.lastModified, it.owner.username))}
				next_link = json._links.find{it.rel == 'next'}
				Thread.currentThread().sleep(DELAY_MILLIS);
				if (next_link != null) {
					println("next link is ${next_link}")
					iterateDocs(next_link.link)
				}
			}
			response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
			response.'404' = { resp -> println " Resource Not found " }
			response.'500' = { resp -> println " Internal server error " }
		}
	}
	
	
	// retrieves documents in CSV format
	def getDocuments (String url) {
		def http = new HTTPBuilder(url)
		http.request(GET, JSON) { req ->
			headers.'apiKey' = key
			response.success = { resp, json ->
				println "Query response: ${json}"
				println "Looking at page ${json.pageNumber}"
				println(String.format("%-50s%-10s%-30s%-20s" , "Name", "Id", "LastModified", "Owner"))
				ids = json.documents.collect {it.id}
				ids.each {
					docUrl = rspaceUrl + "/documents/" + it
					println "doc url is ${docUrl}"
					getContentsAsCSV (docUrl)
				}
			}
			response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
			response.'404' = { resp -> println " Resource Not found " }
			response.'500' = { resp -> println " Internal server error " }
		}
	}
	def getContentsAsCSV (String url) {
		def http = new HTTPBuilder(url)
		http.request(GET, "text/csv") { req ->
			headers.'apiKey' = key
			response.success = { resp, content ->
				println "Query response: ${content}"
				
			}
			response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
			response.'404' = { resp -> println " Resource Not found " }
			response.'500' = { resp -> println " Internal server error " }
		}
	}
	
	
	
	def searchByName (String name) {
		search = [terms:[[query:name, queryType:"name"]]]
		doSearch(search)
	}
	
	def searchByTag (String name) {
		search = [terms:[[query:name, queryType:"tag"]]]
		doSearch(search)
	}
	
	def searchByExactDateLastModified (String iso8601Date) {
		search = [terms:[[query: iso8601Date, queryType:"lastModified"]]]
		doSearch(search)
	}
	
	void searchByDateRangeLastModified (String iso8601DateFrom, iso8601DateTo) {
		search = [terms:[[query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]]]
		doSearch(search)
	}
	
	def searchByExactDateCreated (String iso8601Date) {
		search = [terms:[[query: iso8601Date, queryType:"created"]]]
		doSearch(search)
	}
	
	// Date syntax is dateFrom;dateTo
	void searchByDateRangeCreated (String iso8601DateFrom, iso8601DateTo) {
		search = [terms:[[query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]]]
		doSearch(search)
	}
	
	void searchByFormAndRetrieveAsCSV (String term) {
		search = [terms:[[query: term, queryType:"form"]]]
		documentSearchUrl = generateSearchUrl (search)
		getDocuments(documentSearchUrl)
	}
	
	private doSearch(Map search) {
		documentSearchUrl = generateSearchUrl (search)
		
		// now return lists one page at a time.
		iterateDocs(documentSearchUrl)
	}
	
	private String generateSearchUrl (Map search) {
		//generate JSON from search object
		def searchAsJson = JsonOutput.toJson(search);
		
		//remember to URL encode your JSON string!
		def encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")
		
		//construct the URL
		def documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		return documentSearchUrl
	}