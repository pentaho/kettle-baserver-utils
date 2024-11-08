/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.di.baserver.utils.web;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.Credentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.platform.api.engine.IPluginManager;
import org.pentaho.platform.web.servlet.JAXRSPluginServlet;
import org.springframework.beans.factory.ListableBeanFactory;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class HttpConnectionHelperTest {
  HttpConnectionHelper httpConnectionHelper, httpConnectionHelperSpy;

  @Before
  public void setUp() throws Exception {
    httpConnectionHelper = HttpConnectionHelper.getInstance();
    httpConnectionHelperSpy = spy( httpConnectionHelper );
  }

  @Test
  public void testInvokeEndpoint() throws Exception {
    String serverUrl = "http://localhost:8080/pentaho", userName = "admin", password = "password",
        moduleName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    Response r = mock( Response.class );

    doReturn( r ).when( httpConnectionHelperSpy ).callHttp( any(), anyList(), any(),
        any(), any() );
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );

    serverUrl = "http://localhost:8080/pentaho/";
    endpointPath = "/myEndpoint";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );
    verify( httpConnectionHelperSpy, times( 2 ) ).callHttp(
        "http://localhost:8080/pentaho/api/myEndpoint", httpParameters, httpMethod, userName, password );

    moduleName = "data-access";
    httpConnectionHelperSpy.invokeEndpoint( serverUrl, userName, password, moduleName, endpointPath, httpMethod,
        httpParameters );
    verify( httpConnectionHelperSpy ).callHttp(
        "http://localhost:8080/pentaho/plugin/data-access/api/myEndpoint", httpParameters, httpMethod, userName,
        password );

  }

  @Test
  public void testInvokeEndpoint1() throws Exception {
    String moduleName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    Response r = mock( Response.class );

    doReturn( r ).when( httpConnectionHelperSpy ).invokePlatformEndpoint( any(), any(), Mockito.<List>any() );
    doReturn( r ).when( httpConnectionHelperSpy ).invokePluginEndpoint( any(), any(), any(),
      Mockito.<List>any() );

    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 0 ) )
        .invokePluginEndpoint( moduleName, endpointPath, httpMethod, httpParameters );

    moduleName = "myModule";
    httpConnectionHelperSpy.invokeEndpoint( moduleName, endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    verify( httpConnectionHelperSpy, times( 1 ) ).invokePluginEndpoint( moduleName, endpointPath, httpMethod,
        httpParameters );
  }

  @Test
  public void testInvokePlatformEndpoint() throws Exception {
    Response r;

    String endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1", "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );

    RequestDispatcher requestDispatcher = mock( RequestDispatcher.class );
    ServletContext context = mock( ServletContext.class );
    doThrow( new NoClassDefFoundError() ).when( httpConnectionHelperSpy ).getContext();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doReturn( context ).when( httpConnectionHelperSpy ).getContext();
    doReturn( requestDispatcher ).when( context ).getRequestDispatcher( "/api" + endpointPath );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( requestDispatcher ).forward( Mockito.<InternalHttpServletRequest>any(),
        Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( requestDispatcher ).forward( Mockito.<InternalHttpServletRequest>any(),
      Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( requestDispatcher ).forward( Mockito.<InternalHttpServletRequest>any(),
      Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePlatformEndpoint( endpointPath, httpMethod, httpParameters );
    assertEquals( r.getStatusCode(), 204 );

  }

  @Test
  public void teatInsertDefaultParameters() throws Exception {
    String httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1|" ) );
    httpParameters.add( new HttpParameter( "param2", "value2\\/" ) );
    httpParameters.add( new HttpParameter( "param3", "value3{}" ) );

    InternalHttpServletRequest request = new InternalHttpServletRequest( "", "" );

    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getParameterMap().size(), 3 );
    assertEquals( URLDecoder.decode( request.getParameter( "param1" ), HttpConnectionHelper.UTF_8 ),
        httpParameters.get( 0 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param2" ), HttpConnectionHelper.UTF_8 ),
            httpParameters.get( 1 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param3" ), HttpConnectionHelper.UTF_8 ),
            httpParameters.get( 2 ).getValue() );

    httpMethod = "PUT";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "POST";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );

    httpMethod = "DELETE";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParameters, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( new String( request.getContent() ), "param1=value1%7C&param2=value2%5C%2F&param3=value3%7B%7D" );
  }

  @Test
  public void testInsertParametersWadlAvailable() throws Exception {
    String httpMethod = "GET";
    List<HttpParameter> httpParametersWadl = new ArrayList<>();
    httpParametersWadl.add( new HttpParameter( "param1",  "value1|", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param2", "value2\\/", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param3", "value3{}", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param4", "value4", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param5", "value5", HttpParameter.ParamType.NONE ) );

    InternalHttpServletRequest request = new InternalHttpServletRequest( "", "" );

    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getParameterMap().size(), 5 );
    assertEquals( URLDecoder.decode( request.getParameter( "param1" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 0 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param2" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 1 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param3" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 2 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param4" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 3 ).getValue() );
    assertEquals( URLDecoder.decode( request.getParameter( "param5" ), HttpConnectionHelper.UTF_8 ),
      httpParametersWadl.get( 4 ).getValue() );

    httpMethod = "PUT";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );

    httpMethod = "POST";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );

    httpMethod = "DELETE";
    request = new InternalHttpServletRequest( "", "" );
    httpConnectionHelperSpy.insertParameters( httpMethod, httpParametersWadl, request );
    assertEquals( request.getContentType(), "application/x-www-form-urlencoded" );
    assertEquals( "&param1=value1%7C&param2=value2%5C%2F", request.getQueryString() );
    assertEquals( new String( request.getContent() ), "param3=value3%7B%7D&param4=value4&param5=value5" );
  }

  @Test
  public void testInvokePluginEndpoint() throws Exception {
    Response r;

    String pluginName = "platform", endpointPath = "myEndpoint", httpMethod = "GET";
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1|" ) );
    httpParameters.add( new HttpParameter( "param2", "value2\\/" ) );
    httpParameters.add( new HttpParameter( "param3", "value3{}" ) );


    doReturn( null ).when( httpConnectionHelperSpy ).getPluginManager();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    IPluginManager pluginManager = mock( IPluginManager.class );
    doReturn( pluginManager ).when( httpConnectionHelperSpy ).getPluginManager();
    doReturn( null ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ClassLoader pluginClassLoader = mock( ClassLoader.class );
    doReturn( pluginClassLoader ).when( httpConnectionHelperSpy ).getPluginClassLoader( pluginName, pluginManager );
    doReturn( null ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    ListableBeanFactory beanFactory = mock( ListableBeanFactory.class );
    doReturn( beanFactory ).when( httpConnectionHelperSpy ).getListableBeanFactory( pluginName, pluginManager );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getResult(), ( new Response() ).getResult() );

    JAXRSPluginServlet pluginServlet = mock( JAXRSPluginServlet.class );
    doReturn( pluginServlet ).when( httpConnectionHelperSpy ).getJAXRSPluginServlet( beanFactory );
    doThrow( new MalformedURLException() ).when( httpConnectionHelperSpy ).getUrl();
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    String serverUrl = "http://localhost:8080/pentaho";
    URL url = new URL( serverUrl );
    doReturn( url ).when( httpConnectionHelperSpy ).getUrl();
    doThrow( new ServletException() ).when( pluginServlet ).service( Mockito.<InternalHttpServletRequest>any(),
        Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doThrow( new IOException() ).when( pluginServlet ).service( Mockito.<InternalHttpServletRequest>any(),
      Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertTrue( r.getResult().equals( new Response().getResult() ) );

    doNothing().when( pluginServlet ).service( Mockito.<InternalHttpServletRequest>any(),
      Mockito.<InternalHttpServletResponse>any() );
    r = httpConnectionHelperSpy.invokePluginEndpoint( pluginName, endpointPath, httpMethod, httpParameters );
    assertEquals( r.getStatusCode(), 404 );
    assertEquals( r.getResponseTime(), 0 );
  }

  @Test
  public void testCallHttp() throws Exception {
    String url = "http://localhost:8080/pentaho",
      user = "admin",
      password = "password",
      method = "GET";
    List<HttpParameter> parameters = new ArrayList<>();

    Response r;

    HttpClient httpClient = mock( HttpClient.class );

    Credentials credentials = mock( Credentials.class );
    doReturn( credentials ).when( httpConnectionHelperSpy ).getCredentials( user, password );
    doReturn( httpClient ).when( httpConnectionHelperSpy ).getHttpClient( any(), any() );

    HttpResponse httpResponse = mock( HttpResponse.class );

    BasicHttpEntity httpEntity = new BasicHttpEntity();
    httpEntity.setContent( new ByteArrayInputStream( "content".getBytes() ) );
    doReturn( httpEntity ).when( httpResponse ).getEntity();

    StatusLine statusLine = mock( StatusLine.class );
    doReturn( statusLine ).when( httpResponse ).getStatusLine();
    doReturn( 1 ).when( statusLine ).getStatusCode();
    doReturn( httpResponse ).when( httpClient ).execute( Mockito.<HttpRequestBase>any() );

    parameters.add( new HttpParameter( "Content-Type", "content" ) );
    r = httpConnectionHelperSpy.callHttp( url, parameters, method, user, password );
    assertEquals( r.getResult(), "content" );
  }

  @Test
  public void testGetHttpMethodDefaultParams() throws Exception {
    List<HttpParameter> httpParameters = new ArrayList<>();
    httpParameters.add( new HttpParameter( "param1",  "value1" ) );
    httpParameters.add( new HttpParameter( "param2", "value2" ) );
    httpParameters.add( new HttpParameter( "param3", "value3" ) );
    String url = "http://localhost:8080/pentaho";

    HttpRequestBase method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "GET" );
    assertEquals( method.getClass(), HttpGet.class );
    assertTrue( method.getURI().toString().startsWith( url ) );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "PUT" );
    assertEquals( method.getClass(), HttpPut.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    HttpEntity requestEntity = ( (HttpPut) method ).getEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );
    assertNotNull( ( (StringEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "POST" );
    assertEquals( method.getClass(), HttpPost.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (HttpPost) method ).getEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );
    assertNotNull( ( (StringEntity) requestEntity ).getContent() );

    // POST without parameters
    method = httpConnectionHelperSpy.getHttpMethod( url, null, "POST" );
    assertEquals( method.getClass(), HttpPost.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (HttpPost) method ).getEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );
    assertNotNull( ( (StringEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "DELETE" );
    assertEquals( method.getClass(), HttpDelete.class );
    assertTrue( method.getURI().toString().startsWith( url ) );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "HEAD" );
    assertEquals( method.getClass(), HttpHead.class );
    assertTrue( method.getURI().toString().startsWith( url ) );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParameters, "OPTIONS" );
    assertEquals( method.getClass(), HttpOptions.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
  }

  @Test
  public void testGetHttpMethodWadlAvailable() throws Exception {
    List<HttpParameter> httpParametersWadl = new ArrayList<>();
    httpParametersWadl.add( new HttpParameter( "param1",  "value1|", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param2", "value2\\/", HttpParameter.ParamType.QUERY ) );
    httpParametersWadl.add( new HttpParameter( "param3", "value3{}", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param4", "value4", HttpParameter.ParamType.BODY ) );
    httpParametersWadl.add( new HttpParameter( "param5", "value5", HttpParameter.ParamType.NONE ) );
    String url = "http://localhost:8080/pentaho";

    HttpRequestBase method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "GET" );
    assertEquals( method.getClass(), HttpGet.class );
    assertTrue( method.getURI().toString().startsWith( url ) );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "PUT" );
    assertEquals( method.getClass(), HttpPut.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    HttpEntity requestEntity = ( (HttpPut) method ).getEntity();
    assertNotNull( requestEntity.getContent() );

    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );
    assertNotNull( ( (StringEntity) requestEntity ).getContent() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "POST" );
    assertEquals( method.getClass(), HttpPost.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (HttpPost) method ).getEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );
    assertNotNull( ( (StringEntity) requestEntity ).getContent() );

    // POST without parameters
    method = httpConnectionHelperSpy.getHttpMethod( url, null, "POST" );
    assertEquals( method.getClass(), HttpPost.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    requestEntity = ( (HttpPost) method ).getEntity();
    assertNotNull( requestEntity );
    assertEquals( requestEntity.getClass(), StringEntity.class );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "DELETE" );
    assertEquals( method.getClass(), HttpDelete.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getRequestLine() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "HEAD" );
    assertEquals( method.getClass(), HttpHead.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getRequestLine() );

    method = httpConnectionHelperSpy.getHttpMethod( url, httpParametersWadl, "OPTIONS" );
    assertEquals( method.getClass(), HttpOptions.class );
    assertTrue( method.getURI().toString().startsWith( url ) );
    assertNotNull( method.getRequestLine() );
  }
}
