package com.github.jacopofar.navigationinspector.examples;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.github.jacopofar.navigationinspector.BasicNavigationManipulator;
import com.github.jacopofar.navigationinspector.HttpRequestWrapper;
import com.github.jacopofar.navigationinspector.HttpResponseWrapper;
import com.github.jacopofar.navigationinspector.InspectorFilterSourceAdapter;

public class AliceToBobProxy implements BasicNavigationManipulator{

	public int getMaximumRequestBufferSizeInBytes() {
		return 1024*1024*4;
	}

	public int getMaximumResponseBufferSizeInBytes() {
		return 1024*1024*4;
	}

	public boolean isFiltered(HttpRequest originalRequest) {
		return true;
	}

	public HttpResponse onRequest(HttpRequestWrapper httpRequestWrapper, long requestNumber) {
		return null;
	}

	public HttpResponse onBigRequest(HttpObject httpObject, long requestNumber) {
		return null;
	}

	public HttpObject onResponse(HttpResponseWrapper httpResponseWrapper, long requestNumber) {
		if(httpResponseWrapper.isHTML()){
			Document parsedPage = httpResponseWrapper.parse();
			
			if(parsedPage.outerHtml().contains("Alice")){
				System.out.println("FOUND 'Alice' on page with title :"+parsedPage.title());
				/*as an example, let's look for all of the text elements containing the word "Alice"
				 * and replace it with Bob then set the text itself red and bold.
				 * The original style attribute, if present, is lost
				 */
				for(Element e:parsedPage.getElementsContainingOwnText("Alice")){
					
					for(TextNode k:e.textNodes()){
						k.text(k.text().replace("Alice", "Bob"));
						//the attribute must be applied to e, not the text node, and not to the <title> tag
						if(e.tagName().equalsIgnoreCase("title")) continue;
						e.attr("style", "font-weight:bold;color:red;");
					}
				}
				httpResponseWrapper.setDocument(parsedPage);
			}	
		}
		return httpResponseWrapper.getOriginalHttpObject();
	}

	public HttpObject onBigResponse(HttpObject httpObject, long requestNumber) {
		return httpObject;
	}

	public static void main(String[] args) {
		//create an adapter, is the component that returns the actual content inspector/manipulator, if any
		System.err.println("Instantiating and staring an example proxy...");
		HttpFiltersSourceAdapter fsadapter=new InspectorFilterSourceAdapter(new AliceToBobProxy());
		HttpProxyServer server =
				DefaultHttpProxyServer.bootstrap()
				.withFiltersSource(fsadapter).withTransparent(true)
				.start();
		System.err.println("Example proxy running on port "+server.getListenAddress().getPort());
	}

}
