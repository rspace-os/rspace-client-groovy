package com.researchspace.groovy.examples

//@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

class DocumentAsCSV extends BaseClient {
    
    // retrieves documents in CSV format
    def getDocuments (String url) {
        def http = new HTTPBuilder(url)
        http.request(GET, JSON) { req ->
            headers.'apiKey' = key
            response.success = { resp, json ->
                println "Query response: ${json}"
                def ids = json.documents.collect {it.id}
                ids.each {
                    def docUrl = rspaceUrl + "/documents/" + it
                    println "Getting Document with ID ${it}  as CSV: url is ${docUrl}"
                    getContentsAsCSV (docUrl)
                }
            }
            handleError(response)
        }
    }
    def getContentsAsCSV (String url) {
        def http = new HTTPBuilder(url)
        http.request(GET, "text/csv") { req ->
            headers.'apiKey' = key
            response.success = { resp, content ->
                println "Query response: ${content}"
                
            }
            handleError(response)
        }
    }
    
    void searchByFormAndRetrieveAsCSV (String term) {
        def search = [terms:[[query: term, queryType:"form"]]]
        def documentSearchUrl = generateSearchUrl (search) + "&pageSize=5"
        getDocuments(documentSearchUrl)
    }

}
