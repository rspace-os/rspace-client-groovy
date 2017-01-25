package com.researchspace.groovy.examples
//@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*
/**
* Helper methods for searching documents and iterating over pages of results
*/
class DocumentSearch extends BaseClient {


    // A Fixed DELAY_MILLIS between requests in millis
    def DELAY_MILLIS = 1000

    // by default we'll order results by name
    def documents = rspaceUrl + "/documents?orderBy=name%20asc"

    // retrieves documents one page at a time by looking for URLs in 'next' links
    def iterateDocs (String url) {
        def http = new HTTPBuilder(url)
        http.request(GET, JSON) { req ->
            headers.'apiKey' = key
            response.success = { resp, json ->
                println "Query response: ${json}"
                println "Looking at page ${json.pageNumber}"
                println(String.format("%-50s%-10s%-30s%-20s" , "Name", "Id", "LastModified", "Owner"))
                json.documents.each  {
                    println(String.format("%-50s%-10s%-30s%-20s",
                            it.name, it.id, it.lastModified, it.owner.username))}
                def next_link = json._links.find{it.rel == 'next'}
                // Get next page if it exists
                Thread.currentThread().sleep(DELAY_MILLIS);
                if (next_link != null) {
                    println("next link is ${next_link}")
                    iterateDocs(next_link.link)
                }
            }
            // handlers for error conditions
            response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
            response.'404' = { resp -> println " Resource Not found " }
            response.'429' = { resp -> println " Too many requests, please allow 1 request per second " }
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
                def ids = json.documents.collect {it.id}
                // Iterate over IDs and retrieve whole document by its id
                ids.each {
                    def docUrl = rspaceUrl + "/documents/" + it 
                    println "doc url is ${docUrl}"
                    getContentsAsCSV (docUrl)
                }
            }
            response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
            response.'404' = { resp -> println " Resource Not found " }
            response.'500' = { resp -> println " Internal server error " }
        }
    }
    /**
    * Gets document in CSV format
    */
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


    /**
    * Searches by the supplied name
    *
    */ 
    def searchByName (String name) {
        def search = [terms:[
                [query:name, queryType:"name"]
            ]]
        doSearch(search)
    }

    /**
    * Searches by tag
    *
    */ 
    def searchByTag (String name) {
        def search = [terms:[
                [query:name, queryType:"tag"]
            ]]
        doSearch(search)
    }

    /**
    *Searches for documents last modified on the give date
    * @param iso8601Date A String date in ISO-8601 format
    */ 
    def searchByExactDateLastModified (String iso8601Date) {
        def search = [terms:[
                [query: iso8601Date, queryType:"lastModified"]
            ]]
        doSearch(search)
    }

    /**
    *Searches for documents last modified between the given date range
    * @param iso8601DateFrom A String date in ISO-8601 format 
    * @param iso8601DateTo A String date in ISO-8601 format 
    */ 
    void searchByDateRangeLastModified (String iso8601DateFrom, String iso8601DateTo) {
        def search = [terms:[
                [query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]
            ]]
        doSearch(search)
    }

    def searchByExactDateCreated (String iso8601Date) {
        def search = [terms:[
                [query: iso8601Date, queryType:"created"]
            ]]
        doSearch(search)
    }

    // Date syntax is dateFrom;dateTo
    void searchByDateRangeCreated (String iso8601DateFrom, iso8601DateTo) {
        def search = [terms:[
                [query: iso8601DateFrom+";" + iso8601DateTo, queryType:"created"]
            ]]
        doSearch(search)
    }

    void searchByFormAndRetrieveAsCSV (String term) {
        def search = [terms:[
                [query: term, queryType:"form"]
            ]]
        def documentSearchUrl = generateSearchUrl (search)
        getDocuments(documentSearchUrl)
    }

    void doSearch(Map search) {
        def documentSearchUrl = generateSearchUrl (search)

        // now return lists one page at a time.
        iterateDocs(documentSearchUrl)
    }
}
