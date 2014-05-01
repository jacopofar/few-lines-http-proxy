package com.github.jacopofar.navigationinspector;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class HttpRequestWrapper{

	private DefaultFullHttpRequest httpObject;

	public HttpRequestWrapper(DefaultFullHttpRequest httpObject) {
		this.httpObject=httpObject;
	}

	public String getUri() {
		return this.httpObject.getUri();
	}

	public HttpHeaders getHeaders() {
		return httpObject.headers();
	}

}
