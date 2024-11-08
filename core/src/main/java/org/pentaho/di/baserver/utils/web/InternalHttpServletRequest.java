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

import jakarta.servlet.ServletConnection;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.Collections;
import java.util.Locale;
import java.util.Collection;

public class InternalHttpServletRequest implements HttpServletRequest {

  public static final String DEFAULT_PROTOCOL = "http";
  public static final String DEFAULT_SERVER_ADDR = "127.0.0.1";
  public static final String DEFAULT_SERVER_NAME = "localhost";
  public static final int DEFAULT_SERVER_PORT = 8080;
  public static final String DEFAULT_REMOTE_ADDR = "127.0.0.1";
  public static final String DEFAULT_REMOTE_HOST = "localhost";

  private String protocol = DEFAULT_PROTOCOL;
  private String scheme = DEFAULT_PROTOCOL;
  private String serverName = DEFAULT_SERVER_NAME;
  private int serverPort = DEFAULT_SERVER_PORT;
  private String localName = DEFAULT_SERVER_NAME;
  private String localAddr = DEFAULT_SERVER_ADDR;
  private int localPort = DEFAULT_SERVER_PORT;
  private String remoteAddr = DEFAULT_REMOTE_ADDR;
  private String remoteHost = DEFAULT_REMOTE_HOST;
  private int remotePort = DEFAULT_SERVER_PORT;
  private String servletPath;

  private HashMap<String, String[]> parameters = new HashMap<String, String[]>();
  private HashMap<String, Object> attributes = new HashMap<String, Object>();
  private HashMap<String, String> headers = new HashMap<String, String>();


  @Override
  public String getProtocol() {
    return this.protocol;
  }

  protected void setProtocol( String protocol ) {
    this.protocol = protocol;
  }

  @Override
  public String getScheme() {
    return this.scheme;
  }

  public void setScheme( String scheme ) {
    this.scheme = scheme;
  }

  @Override
  public String getServerName() {
    return this.serverName;
  }

  public void setServerName( String serverName ) {
    this.serverName = serverName;
  }

  @Override
  public int getServerPort() {
    return this.serverPort;
  }

  public void setServerPort( int serverPort ) {
    this.serverPort = serverPort;
  }


  @Override
  public String getLocalName() {
    return this.localName;
  }

  public void setLocalName( String localName ) {
    this.localName = localName;
  }

  @Override
  public String getLocalAddr() {
    return this.localAddr;
  }

  public void setLocalAddr( String localAddr ) {
    this.localAddr = localAddr;
  }


  @Override
  public int getLocalPort() {
    return this.localPort;
  }

  public void setLocalPort( int localPort ) {
    this.localPort = localPort;
  }


  @Override
  public String getRemoteAddr() {
    return this.remoteAddr;
  }

  public void setRemoteAddr( String remoteAddr ) {
    this.remoteAddr = remoteAddr;
  }

  @Override
  public String getRemoteHost() {
    return this.remoteHost;
  }

  public void setRemoteHost( String remoteHost ) {
    this.remoteHost = remoteHost;
  }

  @Override
  public int getRemotePort() {
    return this.remotePort;
  }

  public void setRemotePort( int remotePort ) {
    this.remotePort = remotePort;
  }


  /////////////////////////
  /////////////////////////


  /////////////////////////
  /////////////////////////

  private String method;
  private String pathInfo;
  private String requestURI;
  private String contextPath = "";
  private String queryString;

  @Override
  public String getMethod() {
    return this.method;
  }

  public void setMethod( String method ) {
    this.method = method;
  }

  @Override
  public String getPathInfo() {
    return this.pathInfo;
  }

  public void setPathInfo( String pathInfo ) {
    this.pathInfo = pathInfo;
  }

  @Override
  public String getContextPath() {
    return this.contextPath;
  }

  public void setContextPath( String contextPath ) {
    this.contextPath = contextPath;
  }

  @Override
  public String getQueryString() {
    String queryString = "";
    for ( Map.Entry<String, String[]> entry : this.parameters.entrySet() ) {
      queryString = queryString + "&";
      queryString = queryString + entry.getKey() + "=" + entry.getValue()[ 0 ];
    }
    return queryString;
  }

  public void setQueryString( String queryString ) {
    this.queryString = queryString;
  }

  @Override
  public String getRequestURI() {
    return this.requestURI;
  }

  public void setRequestURI( String requestURI ) {
    this.requestURI = requestURI;
  }


  /////////////////////////
  /////////////////////////

  @Override
  public String getPathTranslated() {
    return ( this.pathInfo != null ? getRealPath( this.pathInfo ) : null );
  }


  /////////////////////////
  /////////////////////////


  @Override
  public String getAuthType() {
    return null;
  }

  @Override
  public Cookie[] getCookies() {
    return null;
  }

  @Override public long getDateHeader( String s ) {
    return 0;
  }

  public void putHeader( String name, String value ) {
    headers.put( name, value );
  }

  @Override public String getHeader( String s ) {
    return headers.get( s );
  }

  @Override public Enumeration getHeaders( String s ) {
    return Collections.enumeration( headers.values() );
  }

  @Override public Enumeration getHeaderNames() {
    Set<String> set = new HashSet<String>( headers.keySet() );
    return Collections.enumeration( set );
  }

  @Override public int getIntHeader( String s ) {
    return 0;
  }

  @Override public String getRemoteUser() {
    return null;
  }

  @Override public boolean isUserInRole( String s ) {
    return false;
  }

  @Override public Principal getUserPrincipal() {
    return null;
  }

  @Override public String getRequestedSessionId() {
    return null;
  }

  @Override public HttpSession getSession( boolean b ) {
    return null;
  }

  @Override public HttpSession getSession() {
    return null;
  }

  @Override
  public String changeSessionId() {
    return null;
  }

  @Override public boolean isRequestedSessionIdValid() {
    return false;
  }

  @Override public boolean isRequestedSessionIdFromCookie() {
    return false;
  }

  @Override public boolean isRequestedSessionIdFromURL() {
    return false;
  }

  public boolean isRequestedSessionIdFromUrl() {
    return false;
  }

  @Override public AsyncContext getAsyncContext() {
    return null;
  }

  @Override public AsyncContext startAsync() {
    return null;
  }

  @Override public AsyncContext startAsync( ServletRequest servletRequest, ServletResponse servletResponse ) {
    return null;
  }

  @Override public boolean isAsyncSupported() {
    return false;
  }

  @Override public boolean isAsyncStarted() {
    return false;
  }

  @Override public ServletContext getServletContext() {
    return null;
  }

  @Override public DispatcherType getDispatcherType() {
    return null;
  }

  @Override
  public String getRequestId() {
    return null;
  }

  @Override
  public String getProtocolRequestId() {
    return null;
  }

  @Override
  public ServletConnection getServletConnection() {
    return null;
  }

  @Override public Part getPart( String name ) throws IOException, ServletException {
    return null;
  }

  @Override
  public <T extends HttpUpgradeHandler> T upgrade( Class<T> aClass ) throws IOException, ServletException {
    return null;
  }

  @Override public Collection<Part> getParts() throws IOException, ServletException {
    return null;
  }

  @Override public void logout() throws ServletException {
  }

  @Override public void login( String username, String password ) throws ServletException {
  }

  @Override public boolean authenticate( HttpServletResponse httpServletResponse )
    throws IOException, ServletException {
    return false;
  }


  @Override public Object getAttribute( String s ) {
    return attributes.get( s );
  }

  @Override public void setAttribute( String s, Object o ) {
    attributes.put( s, o );
  }


  @Override public Enumeration getAttributeNames() {
    return Collections.enumeration( attributes.keySet() );
  }

  @Override public String getCharacterEncoding() {
    return null;
  }

  @Override public void setCharacterEncoding( String s ) throws UnsupportedEncodingException {

  }


  @Override public void removeAttribute( String s ) {

  }

  @Override public Locale getLocale() {
    return Locale.getDefault();
  }

  @Override public Enumeration getLocales() {
    return null;
  }

  @Override public boolean isSecure() {
    return false;
  }

  @Override public RequestDispatcher getRequestDispatcher( String s ) {
    return null;
  }

  public String getRealPath( String path ) {
    return null;
  }


  private byte[] content;
  private String contentType;


  public InternalHttpServletRequest( final String method,
      final URL fullyQualifiedServerURL,
      final String servletPath,
      final String pathInfo ) {
    this.method = method;
    this.scheme = fullyQualifiedServerURL.getProtocol();
    this.serverName = fullyQualifiedServerURL.getHost();
    this.serverPort = fullyQualifiedServerURL.getPort();
    this.contextPath = fullyQualifiedServerURL.getPath();
    this.servletPath = servletPath;
    this.pathInfo = pathInfo;
    this.requestURI = contextPath + servletPath + pathInfo;
    this.parameters = new HashMap<String, String[]>();
  }

  public InternalHttpServletRequest( String method, String requestURI ) {
    this.method = method;
    this.requestURI = requestURI;
    this.parameters = new HashMap<String, String[]>();
  }

  @Override
  public StringBuffer getRequestURL() {
    StringBuffer url = new StringBuffer( this.scheme );
    url.append( "://" ).append( this.serverName ).append( ':' ).append( this.serverPort );
    url.append( getRequestURI() );
    url.append( '?' );
    url.append( getQueryString() );
    return url;
  }

  @Override
  public String getServletPath() {
    return this.servletPath;
  }

  public void setServletPath( String servletPath ) {
    this.servletPath = servletPath;
  }

  @Override
  public int getContentLength() {
    return ( this.content != null ? this.content.length : -1 );
  }

  @Override
  public long getContentLengthLong() {
    return 0;
  }

  @Override
  public String getContentType() {
    return this.contentType;
  }

  public void setContentType( String contentType ) {
    this.contentType = contentType;
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if ( this.content != null ) {
      return new ServletInputStreamWrapper( new ByteArrayInputStream( this.content ) );
    } else {
      return null;
    }
  }


  @Override
  public Map<String, String[]> getParameterMap() {
    return Collections.unmodifiableMap( this.parameters );
  }

  @Override
  public Enumeration<String> getParameterNames() {
    return Collections.enumeration( this.parameters.keySet() );
  }

  @Override
  public String getParameter( String name ) {
    String[] arr = (String[]) this.parameters.get( name );
    return ( arr != null && arr.length > 0 ? arr[ 0 ] : null );
  }

  @Override
  public String[] getParameterValues( String name ) {
    return this.parameters.get( name );
  }

  public void setParameter( String name, String value ) {
    setParameter( name, new String[] { value } );
  }

  public void setParameter( String name, String[] values ) {
    this.parameters.put( name, values );
  }


  @Override
  public BufferedReader getReader() throws IOException {
    if ( this.content != null ) {
      InputStream sourceStream = new ByteArrayInputStream( this.content );
      Reader sourceReader = new InputStreamReader( sourceStream );
      return new BufferedReader( sourceReader );
    } else {
      return null;
    }
  }

  public byte[] getContent() {
    return content;
  }

  public void setContent( byte[] content ) {
    this.content = content;
  }
}
