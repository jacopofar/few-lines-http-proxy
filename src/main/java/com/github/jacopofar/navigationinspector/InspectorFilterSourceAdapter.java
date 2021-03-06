package com.github.jacopofar.navigationinspector;

import java.util.concurrent.atomic.AtomicLong;

import io.netty.handler.codec.http.HttpRequest;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

/**
 * The basic {@link HttpFiltersSourceAdapter} that will be passed to LittleProxy as a filter to HTTP requests
 * This class take care of deciding whether or not to filter requests, and will return the request filter 
 * */
public class InspectorFilterSourceAdapter extends HttpFiltersSourceAdapter {

	private BasicNavigationManipulator navigationManipulator;
	private AtomicLong requestNumber=new AtomicLong();
	public InspectorFilterSourceAdapter(BasicNavigationManipulator nm) {
		this.navigationManipulator=nm;
	}

	public HttpFilters filterRequest(HttpRequest originalRequest) {
		//a distinct InspectorFiltersAdapter is created for each request and the corresponding response
		//the wrapped manipulator is always the same, though, so the requestNumber is passed to match parallel requests and responses if needed
		if(navigationManipulator.isFiltered(originalRequest))
			return new InspectorFiltersAdapter(navigationManipulator,requestNumber.incrementAndGet());
		else return null;
	}

	@Override
	public int getMaximumRequestBufferSizeInBytes() {
		return navigationManipulator.getMaximumRequestBufferSizeInBytes();
	}

	@Override
	public int getMaximumResponseBufferSizeInBytes() {
		return navigationManipulator.getMaximumResponseBufferSizeInBytes();
	}
}
