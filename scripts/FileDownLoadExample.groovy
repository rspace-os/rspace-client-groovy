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

// choose a folder to download into
File downloadFolder = File.createTempDir();
println ("Files will be downloaded to $downloadFolder.absolutePath")

// this class has code to download files from RSpace to a folder
FileDownloader downloader = new FileDownloader (key:key, rspaceUrl:rspaceUrl)

// Retrieve 2 most recently created files
downloader.getFiles(downloadFolder, 2)
