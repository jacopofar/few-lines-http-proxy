package com.github.jacopofar.navigationinspector;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

import org.littleshoot.proxy.HttpFilters;

public class InspectorFiltersAdapter implements HttpFilters {

	private BasicNavigationManipulator navigationManipulator;
	private long requestNumber;

	public InspectorFiltersAdapter(
			BasicNavigationManipulator nm, long requestNumber) {
		this.navigationManipulator=nm;
		this.requestNumber=requestNumber;
	}

	public HttpResponse requestPre(HttpObject httpObject) {
		//from client to proxy
		if(httpObject instanceof DefaultFullHttpRequest){
			return navigationManipulator.onRequest(new HttpRequestWrapper((DefaultFullHttpRequest)httpObject),requestNumber);
			//ignore images
//			String uri=((HttpRequest) httpObject).getUri();
//			String accept=((HttpRequest) httpObject).headers().get("accept");
			//System.out.println("URI:"+uri+"accept: "+accept);
			//if(accept.contains("image/")){

				//System.err.println("URI "+uri+" blocked");
				//				return new HttpResponse(){
				//					
				//					public HttpVersion getProtocolVersion() {
				//						// TODO Auto-generated method stub
				//						return null;
				//					}
				//
				//					public HttpHeaders headers() {
				//						// TODO Auto-generated method stub
				//						return null;
				//					}
				//
				//					public DecoderResult getDecoderResult() {
				//						// TODO Auto-generated method stub
				//						return null;
				//					}
				//
				//					public void setDecoderResult(DecoderResult result) {
				//						// TODO Auto-generated method stub
				//
				//					}
				//
				//					public HttpResponseStatus getStatus() {
				//						//let's say the image wasn't found
				//						return HttpResponseStatus.NOT_FOUND;
				//					}
				//
				//					public HttpResponse setStatus(HttpResponseStatus status) {
				//						// TODO Auto-generated method stub
				//						return null;
				//					}
				//
				//					public HttpResponse setProtocolVersion(HttpVersion version) {
				//						// TODO Auto-generated method stub
				//						return null;
				//					}};
			//}

			//System.out.println("--requestPre:"+((HttpRequest) httpObject).getUri());
		}
		else{
			//System.err.println("--requestPre CLASS: "+httpObject.getClass().getCanonicalName());
			return navigationManipulator.onBigRequest(httpObject,requestNumber);
		}
	}
	//TODO rimuovere, apparentemente non ci serve
	//	public HttpResponse requestPost(HttpObject httpObject) {
	//		//from proxy to server
	//		if(httpObject instanceof HttpRequest){
	//			System.out.println("requestPost:"+((HttpRequest) httpObject).getUri());
	//		}
	//		else
	//			System.out.println("requestPost CLASS: "+httpObject.getClass().getCanonicalName());
	//		return null;
	//	}

	public HttpObject responsePre(HttpObject httpObject) {
		
		//from server to proxy
		if(httpObject instanceof HttpResponse){
			//TODO come modifico una response?
			//DecoderResult decodedContent = ((HttpResponse) httpObject)
			//System.out.println("responsePre:"+((HttpResponse) httpObject).getDecoderResult());
		}
		if(httpObject instanceof DefaultFullHttpResponse){
			return navigationManipulator.onResponse(new HttpResponseWrapper((DefaultFullHttpResponse)httpObject),requestNumber);
			//System.out.println("responsePre:"+((DefaultFullHttpResponse)httpObject).headers().entries());
		}
		else
			return navigationManipulator.onBigResponse(httpObject,requestNumber);
		//System.out.println("responsePre CLASS: "+httpObject.getClass().getCanonicalName());

	}


	public HttpObject responsePost(HttpObject httpObject) {
//		//from proxy to client
//		if(httpObject instanceof HttpResponse){
//			//System.out.println("responsePost:"+((HttpResponse) httpObject).getDecoderResult());
//		}
//		//System.out.println("responsePost CLASS: "+httpObject.getClass().getCanonicalName());
//
//		if(httpObject instanceof DefaultFullHttpResponse){
//			System.out.println("--responsePost entries:"+((DefaultFullHttpResponse)httpObject).headers().entries());
//		}
		//not used in this library
		return httpObject;
	}

	public HttpResponse requestPost(HttpObject httpObject) {
		//not used in this library
		return null;
	}

}
