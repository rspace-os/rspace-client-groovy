import com.researchspace.groovy.examples.*
@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import groovyx.net.http.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.ContentType.JSON
import  groovy.json.*

String key = System.getProperty("key")?:""
String rspaceUrl = System.getProperty("url")?:""
if (key.empty || rspaceUrl.empty ){
	println " Please supply a  key [${key.empty?'':'supplied'}] and a URL [$rspaceUrl]"
	return
}

// this class has some example canned search methods
DocumentSearch searcher = new DocumentSearch (key:key, rspaceUrl:rspaceUrl)

searcher.searchByDateRangeLastModified("2016-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")
searcher.searchByDateRangeCreated("2015-01-03T09:18:45.224Z", "2017-02-05T09:18:45.224Z")

getDocsModifiedOn6thOr7thJuly = [operand:"or", terms:[
		[query:"2015-07-06", queryType:"lastModified"],
		[query:"2015-07-07", queryType:"lastModified"]
	]]

getBasicDocuments = [terms:[
		[query:"Basic Document", queryType:"form"]
	]]

searcher.doSearch (getBasicDocuments)

searcher.doSearch (getDocsModifiedOn6thOr7thJuly)
