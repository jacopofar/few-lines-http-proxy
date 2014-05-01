package com.github.jacopofar.navigationinspector.examples;

import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import com.github.jacopofar.navigationinspector.BasicNavigationManipulator;
import com.github.jacopofar.navigationinspector.HttpRequestWrapper;
import com.github.jacopofar.navigationinspector.HttpResponseWrapper;
import com.github.jacopofar.navigationinspector.InspectorFilterSourceAdapter;

public class LogPages implements BasicNavigationManipulator{

	private Connection conn;
	private PreparedStatement insertReq;
	private PreparedStatement insertRes;

	public LogPages() throws ClassNotFoundException, SQLException{
		Class.forName("org.h2.Driver");
		this.conn = DriverManager.getConnection("jdbc:h2:~/http_logs", "log_user", "" );
		/**
		 * The REQ_ID and RES_ID fields allows to match responses and requests.
		 * However, these numbers are relative to the InspectorFilterSourceAdapter instance,
		 * creating another one will reset the counter
		 * Notice also that a response may never arrive from the server.
		 * Both requests and responses, moreover, may be over the maximum buffer size like the request
		 * */
		conn.createStatement().execute("CREATE TABLE IF NOT EXISTS requests (REQ_ID BIGINT, REQ_URI VARCHAR(2000), REQ_TIME TIMESTAMP);");
		conn.createStatement().execute("CREATE TABLE IF NOT EXISTS responses (RES_ID BIGINT, RES_STATUS SMALLINT, HEADERS CLOB, RES_TIME TIMESTAMP);");
		insertReq=conn.prepareStatement("INSERT INTO requests(REQ_ID,REQ_URI,REQ_TIME) VALUES(?,?,?)");
		insertRes=conn.prepareStatement("INSERT INTO responses(RES_ID,RES_STATUS,HEADERS,RES_TIME) VALUES(?,?,?,?)");

	}
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
		System.out.println("req_"+requestNumber+":"+httpRequestWrapper.getUri()+":"+httpRequestWrapper.getHeaders().entries());	
		try {
			insertReq.setLong(1, requestNumber);
			insertReq.setString(2, httpRequestWrapper.getUri());
			insertReq.setTimestamp(3, new Timestamp(new Date().getTime()));
			insertReq.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public HttpResponse onBigRequest(HttpObject httpObject, long requestNumber) {
		return null;
	}

	public HttpObject onResponse(HttpResponseWrapper httpResponseWrapper, long requestNumber) {
		System.out.println("res_"+requestNumber+":"+httpResponseWrapper.getHeaders().entries());
		try {
			insertRes.setLong(1, requestNumber);
			insertRes.setInt(2, httpResponseWrapper.getStatus());
			insertRes.setClob(3, new StringReader(httpResponseWrapper.getHeaders().toString()));
			insertRes.setTimestamp(4, new Timestamp(new Date().getTime()));
			insertRes.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return httpResponseWrapper.getOriginalHttpObject();
	}

	public HttpObject onBigResponse(HttpObject httpObject, long requestNumber) {
		return httpObject;
	}

	public static void main(String[] args) {
		//create an adapter, is the component that returns the actual content inspector/manipulator, if any
		System.err.println("Instantiating and staring an example proxy...");
		HttpFiltersSourceAdapter fsadapter;
		try {
			fsadapter = new InspectorFilterSourceAdapter(new LogPages());
			HttpProxyServer server =
					DefaultHttpProxyServer.bootstrap()
					//.withPort(8080).withManInTheMiddle(new SelfSignedMitmManager())
					.withFiltersSource(fsadapter).withTransparent(true)
					.start();
			System.err.println("Example proxy running on port "+server.getListenAddress().getPort());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("This feature requires H2 database libraries to be in your classpath");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}

