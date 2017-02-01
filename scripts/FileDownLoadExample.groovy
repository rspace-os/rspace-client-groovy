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

// choose a folder to download into
File downloadFolder = File.createTempDir()
println ("Files will be downloaded to $downloadFolder.absolutePath")

// this class has code to download files from RSpace to a folder
FileDownloader downloader = new FileDownloader (key:key, rspaceUrl:rspaceUrl)

// Retrieve 2 most recently created files
downloader.getFiles(downloadFolder, 2)
