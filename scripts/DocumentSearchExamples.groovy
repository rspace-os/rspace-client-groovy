import com.researchspace.groovy.examples.*
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

//replace this with your RSpace URL
rspaceUrl = "https://demo.researchspace.com/api/v1";

//Set in your RSpace API key here via a -D command line property
key = System.getProperty("apiKey")

// this class has some example canned search methods
DocumentSearch searcher = new DocumentSearch (key:key, rspaceUrl:rspaceUrl)

searcher.searchByDateRangeLastModified("2016-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
searcher.searchByDateRangeCreated("2015-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")

getDocsModifiedOn6thOr7thJuly = [operand:"or", terms:[[query:"2015-07-06", queryType:"lastModified"],
		[query:"2015-07-07", queryType:"lastModified"] ]]

getBasicDocuments = [terms:[[query:"Basic Document", queryType:"form"]]]

searcher.doSearch (getBasicDocuments)

searcher.doSearch (getDocsModifiedOn6thOr7thJuly)
