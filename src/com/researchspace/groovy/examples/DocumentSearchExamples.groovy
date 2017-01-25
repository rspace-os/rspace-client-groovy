package com.researchspace.groovy.examples
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import java.util.Map
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

    //replace this with your RSpace URL
	rspaceUrl = "https://demo.researchspace.com/api/v1";
	 
	//Set in your RSpace API key here via a -D command line property
	key = System.getProperty("apiKey")
	 
	// A Fixed delay between requests
	DELAY = 1000
	 
	// by default we'll order results by name
	documents = rspaceUrl + "/documents?orderBy=name%20asc"
	 
	searchByDateRangeLastModified("2016-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
    searchByDateRangeCreated("2015-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
	 
//	search = [operand:"or",terms:[[query:"2015-07-06", queryType:"lastModified"],
//		                            [query:"2015-07-07", queryType:"lastModified"] ]]
	 
	search = [terms:[[query:"Basic Document", queryType:"form"]]]
	          
	doSearch (search)
	
	// retrieves documents one page at a time by looking for URLs in 'next' links
	def iterateDocs (String url) {
		def http = new HTTPBuilder(url)
		http.request(GET,JSON) { req ->
			headers.'apiKey' = key
			response.success = { resp, json ->
				println "Query response: ${json}"
				println "Looking at page ${json.pageNumber}"
				println(String.format("%-50s%-10s%-30s%-20s" , "Name", "Id", "LastModified", "Owner"))
				json.documents.each  {println(String.format("%-50s%-10s%-30s%-20s", it.name, it.id, it.lastModified, it.owner.username))}
				next_link = json._links.find{it.rel == 'next'}
				Thread.currentThread().sleep(DELAY);
				if(next_link != null) {
					println("next link is ${next_link}")
					iterateDocs(next_link.link)
				}
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
	
	private doSearch(Map search) {
		searchAsJson = JsonOutput.toJson(search);
		println "json query is ${searchAsJson}"
		
		//remember to URL encode your JSON string!
		encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")
		
		//construct the URL
		documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		// now return lists one page at a time.
		iterateDocs(documentSearchUrl)
	}
	
	


