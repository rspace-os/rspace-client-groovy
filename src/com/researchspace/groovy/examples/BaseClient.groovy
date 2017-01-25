package com.researchspace.groovy.examples

import java.util.Map
import  groovy.json.*
/**
 * Common methods for all example classes
 * @author rspace
 *
 */
abstract class BaseClient {

	def rspaceUrl = "https://demo.researchspace.com/api/v1";

	//Set in your RSpace API key here via a -D command line property
	def  key = System.getProperty("apiKey")

	// A Fixed DELAY_MILLIS between requests in millis
	def DELAY_MILLIS = 1000

	// by default we'll order results by name
	def documents = rspaceUrl + "/documents?orderBy=name%20asc"

	String generateSearchUrl (Map search) {
		//generate JSON from search object
		def searchAsJson = JsonOutput.toJson(search);

		//remember to URL encode your JSON string!
		def encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")

		//construct the URL
		def documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		return documentSearchUrl
	}

}
