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

import org.junit.Before;
import org.junit.Test;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

public class InternalHttpServletRequestTest {
  InternalHttpServletRequest request;

  @Before
  public void setUp() {
    request = new InternalHttpServletRequest( "GET", "http://localhost:8080/pentaho" ); //$NON-NLS-1$ //$NON-NLS-2$
  }

  @Test
  public void testSetProtocol() throws Exception {
    assertEquals( "http", request.getProtocol() ); //$NON-NLS-1$
    request.setProtocol( "https" ); //$NON-NLS-1$
    assertEquals( "https", request.getProtocol() ); //$NON-NLS-1$
  }

  @Test
  public void testSetScheme() throws Exception {
    assertEquals( "http", request.getScheme() ); //$NON-NLS-1$
    request.setScheme( "https" ); //$NON-NLS-1$
    assertEquals( "https", request.getScheme() ); //$NON-NLS-1$
  }

  @Test
  public void testSetServerName() throws Exception {
    assertEquals( "localhost", request.getServerName() ); //$NON-NLS-1$
    request.setServerName( "127.0.0.1" ); //$NON-NLS-1$
    assertEquals( "127.0.0.1", request.getServerName() ); //$NON-NLS-1$
  }

  @Test
  public void testSetServerPort() throws Exception {
    assertEquals( 8080, request.getServerPort() ); //$NON-NLS-1$
    request.setServerPort( 80 ); //$NON-NLS-1$
    assertEquals( 80, request.getServerPort() ); //$NON-NLS-1$
  }

  @Test
  public void testSetLocalName() throws Exception {
    assertEquals( "localhost", request.getLocalName() ); //$NON-NLS-1$
    request.setLocalName( "localname" ); //$NON-NLS-1$
    assertEquals( "localname", request.getLocalName() ); //$NON-NLS-1$
  }

  @Test
  public void testSetLocalAddr() throws Exception {
    assertEquals( "127.0.0.1", request.getLocalAddr() ); //$NON-NLS-1$
    request.setLocalAddr( "127.0.0.2" ); //$NON-NLS-1$
    assertEquals( "127.0.0.2", request.getLocalAddr() ); //$NON-NLS-1$
  }

  @Test
  public void testSetLocalPort() throws Exception {
    assertEquals( 8080, request.getLocalPort() ); //$NON-NLS-1$
    request.setLocalPort( 80 ); //$NON-NLS-1$
    assertEquals( 80, request.getLocalPort() ); //$NON-NLS-1$
  }

  @Test
  public void testSetRemoteAddr() throws Exception {
    assertEquals( "127.0.0.1", request.getRemoteAddr() ); //$NON-NLS-1$
    request.setRemoteAddr( "127.0.0.2" ); //$NON-NLS-1$
    assertEquals( "127.0.0.2", request.getRemoteAddr() ); //$NON-NLS-1$
  }

  @Test
  public void testSetRemoteHost() throws Exception {
    assertEquals( "localhost", request.getRemoteHost() ); //$NON-NLS-1$
    request.setRemoteHost( "remotehost" ); //$NON-NLS-1$
    assertEquals( "remotehost", request.getRemoteHost() ); //$NON-NLS-1$
  }

  @Test
  public void testSetRemotePort() throws Exception {
    assertEquals( 8080, request.getRemotePort() ); //$NON-NLS-1$
    request.setRemotePort( 80 ); //$NON-NLS-1$
    assertEquals( 80, request.getRemotePort() ); //$NON-NLS-1$
  }

  @Test
  public void testSetMethod() throws Exception {
    assertEquals( "GET", request.getMethod() ); //$NON-NLS-1$
    request.setMethod( "POST" ); //$NON-NLS-1$
    assertEquals( "POST", request.getMethod() ); //$NON-NLS-1$
  }

  @Test
  public void testSetPathInfo() throws Exception {
    assertNull( request.getPathInfo() );
    request.setPathInfo( "pathinfo" ); //$NON-NLS-1$
    assertEquals( "pathinfo", request.getPathInfo() ); //$NON-NLS-1$
  }

  @Test
  public void testSetContextPath() throws Exception {
    assertEquals( "", request.getContextPath() ); //$NON-NLS-1$
    request.setContextPath( "contextpath" ); //$NON-NLS-1$
    assertEquals( "contextpath", request.getContextPath() ); //$NON-NLS-1$
  }

  @Test
  public void testSetQueryString() throws Exception {
    assertEquals( "", request.getQueryString() ); //$NON-NLS-1$
    request.setParameter( "param1", "1" ); //$NON-NLS-1$ //$NON-NLS-2$
    request.setParameter( "param2", "2" ); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals( "&param1=1&param2=2", request.getQueryString() ); //$NON-NLS-1$
  }

  @Test
  public void testSetRequestURI() throws Exception {
    assertEquals( "http://localhost:8080/pentaho", request.getRequestURI() ); //$NON-NLS-1$
    request.setRequestURI( "http://127.0.0.1:80/pentaho" ); //$NON-NLS-1$
    assertEquals( "http://127.0.0.1:80/pentaho", request.getRequestURI() ); //$NON-NLS-1$
  }

  @Test
  public void testGetPathTranslated() throws Exception {
    request.setPathInfo( "pathinfo" ); //$NON-NLS-1$
    assertNull( request.getPathTranslated() );
  }

  @Test
  public void testServletPath() throws Exception {
    assertNull( request.getServletPath() );
    request.setServletPath( "servletpath" ); //$NON-NLS-1$
    assertEquals( "servletpath", request.getServletPath() ); //$NON-NLS-1$
  }

  @Test
  public void testGetRequestURL() throws Exception {
    request.setScheme( "http" ); //$NON-NLS-1$
    request.setServerName( "localhost" ); //$NON-NLS-1$
    request.setServerPort( 8080 );
    request.setRequestURI( "pentaho" ); //$NON-NLS-1$
    request.setParameter( "param1", "1" ); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals( "http://localhost:8080pentaho?&param1=1", request.getRequestURL().toString() ); //$NON-NLS-1$
  }

  @Test
  public void testSetContent() throws Exception {
    request.setContent( "test".getBytes() );
    assertEquals( 4, request.getContentLength() );
  }

  @Test
  public void testGetInputStream() throws Exception {
    assertNull( request.getInputStream() );
    request.setContent( "test".getBytes() );
    assertNotNull( request.getInputStream() );
  }

  @Test
  public void testGetreader() throws Exception {
    assertNull( request.getReader() );
    request.setContent( "test".getBytes() );
    assertNotNull( request.getReader() );
  }

  @Test
  public void testGetParameterValues() throws Exception {
    assertNull( request.getParameterValues( "test" ) );
    request.setParameter( "param", "value" );
    String[] result = request.getParameterValues( "param" );
    assertEquals( "value", result[ 0 ] );
  }

  @Test
  public void testGetParameterNames() throws Exception {
    request.setParameter( "param 1", "value 1" );
    request.setParameter( "param 2", "value 2" );
    Enumeration<String> result = request.getParameterNames();
    assertEquals( "param 1", result.nextElement().toString() );
    assertEquals( "param 2", result.nextElement().toString() );
  }

  @Test
  public void testSetAttribute() throws Exception {
    request.setAttribute( "attr 1", "value 1" );
    request.setAttribute( "attr 2", "value 2" );
    assertEquals( "value 1", request.getAttribute( "attr 1" ) );
    assertEquals( "value 2", request.getAttribute( "attr 2" ) );

    Enumeration<String> result = request.getAttributeNames();
    assertEquals( "attr 2", result.nextElement().toString() );
    assertEquals( "attr 1", result.nextElement().toString() );
  }

  @Test
  public void testDefaultValues() throws Exception {
    assertNull( request.getAuthType() );
    assertNull( request.getCookies() );
    assertEquals( 0, request.getDateHeader( "" ) ); //$NON-NLS-1$
    assertNull( request.getHeader( "" ) ); //$NON-NLS-1$
    assertFalse( request.getHeaders( "" ).hasMoreElements() );
    assertFalse( request.getHeaderNames().hasMoreElements() );
    assertEquals( 0, request.getIntHeader( "" ) ); //$NON-NLS-1$
    assertNull( request.getRemoteUser() );
    assertFalse( request.isUserInRole( "" ) ); //$NON-NLS-1$
    assertNull( request.getUserPrincipal() );
    assertNull( request.getRequestedSessionId() );
    assertNull( request.getSession( false ) );
    assertNull( request.getSession() );
    assertFalse( request.isRequestedSessionIdValid() );
    assertFalse( request.isRequestedSessionIdFromCookie() );
    assertFalse( request.isRequestedSessionIdFromURL() );
    assertFalse( request.isRequestedSessionIdFromUrl() );
    assertNull( request.getAsyncContext() );
    assertNull( request.startAsync() );
    assertNull( request.startAsync( mock( ServletRequest.class ), mock( ServletResponse.class ) ) );
    assertFalse( request.isAsyncSupported() );
    assertFalse( request.isAsyncStarted() );
    assertNull( request.getServletContext() );
    assertNull( request.getDispatcherType() );
    assertNull( request.getPart( "" ) ); //$NON-NLS-1$
    assertNull( request.getParts() );
    assertFalse( request.authenticate( mock( HttpServletResponse.class ) ) );
    assertNull( request.getCharacterEncoding() );
    assertNull( request.getLocales() );
    assertFalse( request.isSecure() );
    assertNull( request.getRequestDispatcher( "" ) ); //$NON-NLS-1$) );
    assertNull( request.getRealPath( "" ) ); //$NON-NLS-1$) );
  }
}
