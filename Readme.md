## Groovy RSpace API client

This project shows some examples of calling the RSpace API from a Groovy script.

To begin with you'll need an account on an RSpace server and an API key which you can get from your profile page.
 *Please note that your RSpace server must be 1.41 or later to support API calls. If you want to run post requests then your RSpace server must be 1.45 or later*.

In these examples we'll be using the HttpBuilder library which provides an abstraction over lower-level libraries.

All the code listed here is in the project. 

For full details of our API specification please visit our [Community site](https://community.researchspace.com/public/apiDocs) or, if you have your own RSpace installation, go to /public/apiDocs at your site.

To run the example scripts in the scripts/ folder, `cd` to that folder, then run

    groovy -classpath ../src -Dkey=MyAPIKey -Durl=https://my.researchspace.com ExampleScript.groovy 
    
 replacing `MyAPIKey` with your key, `https://my.researchspace.com` with the URL of your RSpace instance and `ExampleScript.groovy` with the name of the script you want to run.
 
 E.g.
 
    groovy -classpath ../src -Dkey=abcdefgh -Durl=https://community.researchspace.com DocumentSearchExample.groovy
   
 Finally, you might need to configure your Grapes mechanism to pull in dependencies from MavenCentral.  

### A basic query to list documents

First of all we'll define our URL and get our key from a system property.
```groovy

    @Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
    import groovyx.net.http.*
    import static groovyx.net.http.Method.GET
    import static groovyx.net.http.ContentType.JSON
    import  groovy.json.*

    // from command line -D property
    rspaceUrl = System.getProperty("url") + "/api/v1";
     
    //Set in your RSpace API key here via a -D command line property
    key = System.getProperty("key")
     
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

Using this approach we can iterate through pages of results, getting summary information for each document.

```groovy

    next_link = json._links.find{it.rel == 'next'};
    if (next_link != null) {
        println("next link is ${next_link.link}")
        // fetch next page of results
    }
```

A complete example of this is the `iterateDocs` method in `DocumentSearch.groovy`.


### Searching

RSpace API provides  two sorts of search - a basic search that searches all searchable fields, and an advanced search where more fine-grained queries can be made and combined with boolean operators.

A simple search can be run by adding  a search parameter to the query string:

```groovy
  
    url = "https://myrspace.com/api/v1?query=mysearchQuery"

```

Here are some examples of advanced search constructs:

```groovy

   
    // search by tag:
    search = [terms:[[query:"ATag", queryType:"tag"]]]
    
    // by name
    search = [terms:[[query:"AName", queryType:"name"]]]
    
    // for items created on a given date using IS0-8601 or yyyy-MM-dd format
    search = [terms:[[query:"2016-07-23", queryType:"created"]]]
    
    // for items modified between 2  dates using IS0-8601 or yyyy-MM-dd format
    search = [terms:[[query:"2016-07-23;2016-08-23 ", queryType:"lastModified"]]]
    
    // for items last modified on either of 2  dates:
    search = [operand:"or",terms:[[query:"2015-07-06", queryType:"lastModified"],
                                    [query:"2015-07-07", queryType:"lastModified"] ]

    // search for documents created from a given form:
    search = [terms:[[query:"Basic Document", queryType:"form"]]]
    
    // search for documents created from a given form and a specific tag:
    search = [operand:"and", terms:[[query:"Basic Document", queryType:"form"], [query:"ATag", queryType:"tag"]]]
                                        
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

### Retrieving document content

Content can be retrieved from the endpoint /documents/{id} where {id} is a documentID.

Here is an example retrieving a document in CSV format taken  from `DocumentAsCSV.groovy` script:

```groovy

    rspaceUrl = System.getProperty("url") + "/api/v1";

    //Set in your RSpace API key here via a -D command line property
    key = System.getProperty("apiKey")
 
    DocumentAsCSV csvRetriever = new DocumentAsCSV (key: key, rspaceUrl: rspaceUrl)
      
    //gets all documents created from 'BasicDocument' form in CSV format.
    // This is useful for extracting content to put in a database, for example. 
    csvRetriever.searchByFormAndRetrieveAsCSV("Basic Document")

```

### Getting attached files

Here's an example where we download file attachments associated with some documents. The code is in `FileDownloader.groovy`. 

```groovy

    //construct a download URL from a file id (you can get this from files/ endpoint)
    def url = rspaceUrl + "/files/" + file.id + "/file"
    
    // file name can also be retrieved from files/ listing
    def name = "Myfile.txt" 
    
    // where you want the files to be downlaoded to
    def downloadFolder = File.createTempDir()

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
```

### Uploading content

The script in [DocumentCreationAndFileUpload.groovy](scripts/DocumentCreationAndFileUpload.groovy) illustrates
 how to create a document, upload a file and link a document to a file. It uses 
 [DocumentPoster.groovy](src/com/researchspace/groovy/examples/DocumentPoster.groovy) as
 a helper class.
 
 ```groovy
    DocumentPoster poster = new DocumentPoster (key: key, rspaceUrl: rspaceUrl)
    def toCreate = [name:"SNP analysis", tags:"groovy,api,snp", fields:[ [content:"Some metadata"]]]
	Map newDocument = poster.createDocument(toCreate)
 ```
 will create a new basic document with a name, some tags and initial content. The returned Map contains
 keys and value corresponding to the returned JSON. By default content will be put in the 'API' folder.
 
 You can specify a form  if you want to create a particular document type:
 
 ```groovy
    def toCreate =[
         name:"SNP analysis", tags:"groovy,api,snp",
         fields:[ [content:"Some metadata"]]
         form:[id:2]
         ]
 ```
 
 Here's how to upload a file:
 
 ```groovy
    File fileToPost = new File("resources/2017-05-10_1670091041_CNVts.csv")
    Map newFile = poster.uploadFile(fileToPost, "an optional  caption, can be null")
 ```
 
 which will return a representation of the created file.
 
 To link a document to a file:
 
  ```groovy
    Map alteredDoc = poster.appendFileAttachment(newDocument, newFile.id)
 ```
 Pass in the new document and the id of the file to link to. The file link will be appended to the current content.
 
 To put a link in the content using a PUT request to documents/{id} you just need to put the placeholder:
 `<fileId=xxxx>` replacing 'xxxx' with the id of your uploaded file. RSpace will replace this with HTML of the
  attachment link for viewing in a browser.
 