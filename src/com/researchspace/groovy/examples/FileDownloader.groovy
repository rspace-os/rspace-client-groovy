package com.researchspace.groovy.examples

import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

class FileDownloader extends BaseClient {
    
   /**
    * 
    * @param downloadFolder A Directory to hold the downlaoded files
    * @param pageSize Number of files to download at once.
    */
    def getFiles (File downloadFolder, int pageSize) {
        def http = new HTTPBuilder(filesUrl(pageSize))
        http.request(GET, JSON) { req ->
            headers.'apiKey' = key
            response.success = { resp, json ->
                println "Query response: ${json}"
                def ids = json.files.collect {[ id :it.id, name: it.name]}
                ids.each {
                    def docUrl = rspaceUrl + "/files/" + it.id + "/file"
					println "Downloading $it.name ... "      
                    downloadFile (docUrl, it.name, downloadFolder)
                }
				println "Downloaded successfully:"
				downloadFolder.traverse  { println " $it.absolutePath " }
            }
            handleError(response)
        }
    }
	
	/**
	 * Downloads an individual file
	 */
    def downloadFile (String url, String name, File downloadFolder) {
        def http = new HTTPBuilder(url)
        http.request(GET, "application/octet-stream") { req ->
            headers.'apiKey' = key
            response.success = { resp, content ->
				def file = new File(downloadFolder, name).newOutputStream()
				file << content
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
