### Groovy RSpace API client

This project shows some examples of calling the RSpace API from a Groovy script.

To begin with you'll need an account on an RSpace server and an API key which you can get from your profile page.

In these examples we'll be using the HttpBuilder library which provides an abstraction over lower-level libraries.

All the code listed here is in the project. 

For full details of our API spec please see https://your.rspace.com/public/apiDocs

First of all we'll define our URL and get our key from a system property.
```groovy

    //replace this with your RSpace URL
	 rspaceUrl = "https://demo.researchspace.com/api/v1";
	 
	 //Set in your RSpace API key here via a -D command line property
	 key = System.getProperty("apiKey")
	 
	 // A Fixed delay between requests
	 DELAY = 1000
	 
	 // by default we'll order results by name
	 documents = rspaceUrl + "/documents?orderBy=name%20asc"
	def http = new HTTPBuilder(url)
	
	// This is the simplest request - it will retrieve a first page of documents ordered by name.
	http.request(GET,JSON) { req ->
			headers.'apiKey' = key
			response.success = { resp, json ->
				println "Query response: ${json}"
			}
	}

```