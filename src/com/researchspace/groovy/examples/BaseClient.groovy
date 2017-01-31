package com.researchspace.groovy.examples

import java.util.Map
import  groovy.json.*
/**
 * Common methods for all example classes
 * @author rspace
 *
 */
abstract class BaseClient {

	def rspaceUrl

	def  key 

	// A Fixed DELAY_MILLIS between requests in millis
	def DELAY_MILLIS = 1000

	// by default we'll order results by name
	def documents = rspaceUrl + "/documents?orderBy=name%20asc"
	
	// just 2 files per page in this example
	
	def filesUrl ( int pageSize) {
		return rspaceUrl + "/files?pageSize=" + pageSize		
	}

	String generateSearchUrl (Map search) {
		//generate JSON from search object
		def searchAsJson = JsonOutput.toJson(search);

		//remember to URL encode your JSON string!
		def encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")

		//construct the URL
		def documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		return documentSearchUrl
	}
	
	def handleError(response) {
        response.'401' = { resp -> println " Not found or  unauthorised - are you sure your API key is correct?" }
        response.'404' = { resp -> println " Resource Not found " }
        response.'429' = { resp -> println " Too many requests, please allow 1 request per second " }
        response.'500' = { resp -> println " Internal server error " }
	}

}
