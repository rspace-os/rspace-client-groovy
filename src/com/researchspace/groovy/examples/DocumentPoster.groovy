package com.researchspace.groovy.examples

//@Grab('org.codehaus.groovy.modules.http-builder:http-builder:0.7')
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;

import  groovy.json.*
import groovyx.net.http.*

class DocumentPoster extends BaseClient {
    
    // Creates a document, returning it as Map of json structure
    def Map createDocument (Map postBody) {
		def docId = null;
		CloseableHttpClient client = HttpClients.createDefault();
		CloseableHttpResponse resp = null;
		HttpUriRequest req = RequestBuilder.post(rspaceUrl + "/documents/")
		.addHeader("apiKey", key)
		.setEntity(new StringEntity(JsonOutput.toJson(postBody)))
		.setHeader("Content-Type", "application/json;charset=UTF-8").build();
		resp = client.execute(req);
		Map data = response2Map(resp)
		println " id is ${data.id}"
		return data
    }
	
	def Map uploadFile (File toPost, String optionalCaption) {
		CloseableHttpClient client = HttpClients.createDefault()
		HttpPost post = new HttpPost(rspaceUrl + "/files/")
		MultipartEntityBuilder builder = MultipartEntityBuilder.create()
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addBinaryBody("file", toPost, ContentType.DEFAULT_BINARY, toPost.getName())
		builder.addTextBody("caption", optionalCaption);
		
		
		HttpEntity entity = builder.build()
		post.setEntity(entity)
		post.addHeader("apiKey", key)
		HttpResponse resp = client.execute(post)
		Map data = response2Map(resp)
		println " file id is ${data.id}"
		return data
	}
	
	def Map response2Map (CloseableHttpResponse resp) {
		def jsonSlurper = new JsonSlurper()
		def jsonText = EntityUtils.toString(resp.getEntity());
		println "response is $jsonText"
		def map = jsonSlurper.parseText(jsonText)
		return map
	}
	
	def Map appendFileAttachment (Map basicDocument, Long fileId) {
		String oldText = basicDocument.fields[0].content
		String newText = oldText + "<fileId=" + fileId+">"
		CloseableHttpClient client = HttpClients.createDefault();
		String body = JsonOutput.toJson([fields:[[content:newText]]])
		println("body is $body")
		HttpUriRequest req = RequestBuilder.put(rspaceUrl + "/documents/" + basicDocument.id)
		.addHeader("apiKey", key)
		.setEntity(new StringEntity(body))
		.setHeader("Content-Type", "application/json;charset=UTF-8").build()
		
		CloseableHttpResponse resp = client.execute(req);
		Map data = response2Map(resp)
		println " id is ${data.id}"
		return data
	}

}
