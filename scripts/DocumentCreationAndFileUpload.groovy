import com.researchspace.groovy.examples.*
@Grapes([
@Grab(group='org.apache.httpcomponents', module='httpclient', version='4.5.2'),
@Grab(group='org.apache.httpcomponents', module='httpmime', version='4.5.2')
]
)
import groovyx.net.http.*
import  groovy.json.*
    // this script creates a document, uploads a file, and links the file to the document.
    // Since we're not specifying folderIds in these examples, they will go to the API folders
    // by default
 
    String key = System.getProperty("key")?:""
	String rspaceUrl = System.getProperty("url")?:""
	if (key.empty || rspaceUrl.empty ){
		println " Please supply a  key [${key.empty?'':'supplied'}] and a URL [$rspaceUrl]"
		return
	}
	// this is a file of SNP analysis but could be anything
	File fileToPost = new File("../resources/2017-05-10_1670091041_CNVts.csv")

	DocumentPoster poster = new DocumentPoster (key: key, rspaceUrl: rspaceUrl)
	// create a new basic document 
	def toCreate =[name:"SNP analysis", tags:"groovy,api,snp", fields:[ [content:"Some metadata"]]]
	Map newDocument = poster.createDocument(toCreate)
	//upload a file, this will go to the gallery initially
	Map newFile = poster.uploadFile(fileToPost, "some caption")
	// attach file to document
	Map alteredDoc = poster.appendFileAttachment(newDocument, newFile.id)
	println "Attachment link inserted into text:"
	println "-----------------------------------"
	println alteredDoc.fields[0].content
	
