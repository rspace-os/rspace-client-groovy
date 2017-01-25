## Groovy RSpace API client

This project shows some examples of calling the RSpace API from a Groovy script.

To begin with you'll need an account on an RSpace server and an API key which you can get from your profile page.

In these examples we'll be using the HttpBuilder library which provides an abstraction over lower-level libraries.

All the code listed here is in the project. 

For full details of our API spec please see https://your.rspace.com/public/apiDocs

### A basic query to list documents

First of all we'll define our URL and get our key from a system property.
```groovy

    @Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
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
	def http = new HTTPBuilder(url)
	
	// This is the simplest request - it will retrieve a first page of documents ordered by name.
	http.request(GET,JSON) { req ->
		headers.'apiKey' = key
		response.success = { resp, json ->
			println "Query response: ${json}"
		}
	}

```

In the above example, the 'json' variable is a JSON Map object that easily be accessed for data:

```groovy
    
    println "Looking at page ${json.pageNumber}"
    json.documents.each  {println(String.format("%-50s%-20s%-20s", it.name, it.id, it.lastModified))}

```

#### Iterating over pages of results 
The JSON response also contains a `_links` field that uses HATEOAS conventions to provide links to related content. For document listings and searches, links to `previous`, `next`, `first` and `last` pages are provided when needed.

Using this approach we can iterate through pages of results:

```groovy
    next_link = json._links.find{it.rel == 'next'};
    if (next_link != null) {
	    println("next link is ${next_link.link}")
		// fetch next page of results
	}
```

A complete example of this is the `iterateDocs` method in `DocumentSearchExamples.groovy`.


### Searching

RSpace API provides  two sorts of search - a basic search that searches all searchable fields, and an advanced search where more fine-grained queries can be made and combined with boolean operators.

A simple search can be run by adding  a search parameter to the query string:

```groovy
  
    url = "https://myrspace.com?query=mysearchQuery"

```


Here are some examples of advanced search constructs:

```groovy

   
	// search by tag:
	search = [terms:[[query:name, queryType:"tag"]]]
	
	// by name
	search = [terms:[[query:name, queryType:"name"]]]
	
	// for items created on a given date using IS0-8601 or yyyy-MM-dd format
	search = [terms:[[query:"2016-07-23", queryType:"created"]]]
	
	// for items modified between 2  dates using IS0-8601 or yyyy-MM-dd format
	search = [terms:[[query:"2016-07-23;2016-08-23 ", queryType:"lastModified"]]]
	
	// for items last modified on either of 2  dates:
	search = [operand:"or",terms:[[query:"2015-07-06", queryType:"lastModified"],
		                            [query:"2015-07-07", queryType:"lastModified"] ]

    // search for documents created from a given form:
    search = [terms:[[query:"Basic Document", queryType:"form"]]]
		                            	
```

To submit these queries, serialise them to JSON, URL escape and submit:

```groovy

        // convert your search object to JSON
        searchAsJson = JsonOutput.toJson(search);
        	
		//remember to URL-encode your JSON string!
		encoded = java.net.URLEncoder.encode(searchAsJson, "UTF-8")
		
		// construct the URL
		documentSearchUrl = rspaceUrl + "/documents?advancedQuery="+encoded
		
		// now list one page at a time.
		iterateDocs(documentSearchUrl)
```