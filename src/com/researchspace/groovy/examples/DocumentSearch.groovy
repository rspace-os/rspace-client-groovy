package com.researchspace.groovy.examples
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import java.util.Map

class DocumentSearch {
	
	
	def iterateDocs (String url) {
		def http = new HTTPBuilder(url)
		http.request(GET,JSON) { req ->
			headers.'apiKey' = key
			response.success = { resp, json ->
				println "Query response: ${json}"
				println "Looking at page ${json.pageNumber}"
				println(String.format("%-50s%-20s%-20s" , "Name", "Id", "LastModified"))
				json.documents.each  {println(String.format("%-50s%-20s%-20s", it.name, it.id, it.lastModified))}
				next_link = json._links.findAll{it.rel == 'next'}.collect {it.link};
				Thread.currentThread().sleep(DELAY);
				if(!next_link.empty) {
					println("next link is ${next_link}")
					iterateDocs(next_link[0])
				}
			}
			response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
			response.'404' = { resp -> println " Resource Not found " }
			response.'500' = { resp -> println " Internal server error " }
		}
	}
	//iterateDocs(documents);
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
	
	def searchByDateRangeLastModified (String iso8601DateFrom, iso8601DateTo) {
		search = [terms:[[query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]]]
		doSearch(search)
	}
	
	def searchByExactDateCreated (String iso8601Date) {
		search = [terms:[[query: iso8601Date, queryType:"created"]]]
		doSearch(search)
	}
	
	def searchByDateRangeCreated (String iso8601DateFrom, iso8601DateTo) {
		search = [terms:[[query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]]]
		doSearch(search)
	}
	
	private doSearch(Map search) {
		searchAsJson = JsonOutput.toJson(search)
		println"json query is " + searchAsJson
		encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")
		documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		iterateDocs(documentSearchUrl)
	}

}
