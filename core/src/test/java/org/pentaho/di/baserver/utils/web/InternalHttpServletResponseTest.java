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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class InternalHttpServletResponseTest {
  InternalHttpServletResponse response, responseSpy;

  @Before
  public void setUp() {
    ByteArrayOutputStream stream = mock( ByteArrayOutputStream.class );
    response = new InternalHttpServletResponse( stream );
    responseSpy = spy( response );
  }

  @Test
  public void testDefaults() throws Exception {
    assertTrue( response.isOutputStreamAccessAllowed() );
    assertTrue( response.isWriterAccessAllowed() );
    assertNull( response.getCharacterEncoding() );
    assertEquals( "", response.getHeader( "" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    assertNull( response.getHeaders( "" ) ); //$NON-NLS-1$
    assertNull( response.getHeaderNames() );
    assertFalse( response.containsHeader( "" ) ); //$NON-NLS-1$
    assertEquals( "url", response.encodeURL( "url" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals( "url", response.encodeRedirectURL( "url" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals( "url", response.encodeUrl( "url" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    assertEquals( "url", response.encodeRedirectUrl( "url" ) ); //$NON-NLS-1$ //$NON-NLS-2$
    assertNull( response.getLocale() );
  }

  @Test
  public void testSetStatus() throws Exception {
    response.setStatus( 200 );
    assertEquals( 200, response.getStatus() );
  }

  @Test
  public void testSetStatusWithErrorMessage() throws Exception {
    response.setStatus( 404, "Not Found" ); //$NON-NLS-1$
    assertEquals( 404, response.getStatus() );
    assertEquals( "Not Found", response.getErrorMessage() ); //$NON-NLS-1$
  }

  @Test
  public void testSetIncludeUrl() throws Exception {
    assertNull( response.getIncludedUrl() );
    response.setIncludedUrl( "includeUrl" ); //$NON-NLS-1$
    assertEquals( "includeUrl", response.getIncludedUrl() ); //$NON-NLS-1$
  }

  @Test
  public void testSetForwardedUrl() throws Exception {
    assertNull( response.getForwardedUrl() );
    response.setForwardedUrl( "forwardedUrl" ); //$NON-NLS-1$
    assertEquals( "forwardedUrl", response.getForwardedUrl() ); //$NON-NLS-1$
  }

  @Test
  public void testSetContentLength() throws Exception {
    assertEquals( 0, response.getContentLength() );
    response.setContentLength( 10 );
    assertEquals( 10, response.getContentLength() );
  }

  @Test
  public void testSetBufferSize() throws Exception {
    assertEquals( 4096, response.getBufferSize() );
    response.setBufferSize( 10 );
    assertEquals( 10, response.getBufferSize() );
  }

  @Test
  public void testResetBuffer() throws Exception {
    ByteArrayOutputStream stream = spy( new ByteArrayOutputStream() );
    InternalHttpServletResponse response = new InternalHttpServletResponse( stream );
    response.resetBuffer();
    verify( stream, times( 1 ) ).reset();
  }

  @Test
  public void testReset() throws Exception {
    responseSpy.reset();
    verify( responseSpy, times( 1 ) ).resetBuffer();
    assertNull( responseSpy.getCharacterEncoding() );
    assertEquals( 0, responseSpy.getContentLength() );
    assertNull( responseSpy.getContentType() );
    assertEquals( HttpServletResponse.SC_OK, responseSpy.getStatus() );
    assertNull( responseSpy.getErrorMessage() );
  }

  @Test
  public void testSetContentType() throws Exception {
    assertNull( response.getContentType() );
    response.setContentType( "charset=utf8" );
    assertEquals( "charset=utf8", response.getContentType() );
  }

  @Test
  public void testSendRedirect() throws Exception {
    response.sendRedirect( "url" ); //$NON-NLS-1$
    assertEquals( "url", response.getRedirectedUrl() ); //$NON-NLS-1$
    assertTrue( response.isCommitted() );

    try {
      response.sendRedirect( "url" ); //$NON-NLS-1$
    } catch ( IllegalStateException ex ) {
      assertTrue( true );
    }
  }

  @Test
  public void testSendError() throws Exception {
    response.sendError( 404 );
    assertEquals( 404, response.getStatus() );
    assertTrue( response.isCommitted() );

    try {
      response.sendError( 404 );
    } catch ( IllegalStateException ex ) {
      assertTrue( true );
    }
  }

  @Test
  public void testSendErrorWithErrorMessage() throws Exception {
    response.sendError( 404, "Not Found" ); //$NON-NLS-1$
    assertEquals( 404, response.getStatus() );
    assertEquals( "Not Found", response.getErrorMessage() ); //$NON-NLS-1$
    assertTrue( response.isCommitted() );

    try {
      response.sendError( 404, "Not Found" ); //$NON-NLS-1$
    } catch ( IllegalStateException ex ) {
      assertTrue( true );
    }
  }

  @Test
  public void testGetOutputStream() throws Exception {
    ByteArrayOutputStream stream = mock( ByteArrayOutputStream.class );
    response = new InternalHttpServletResponse( stream );
    ServletOutputStream outputStream = response.getOutputStream();
    assertNotNull( outputStream );
  }

  @Test
  public void testGetContentAsByteArray() throws Exception {
    byte[] result = responseSpy.getContentAsByteArray();
    verify( responseSpy, times( 1 ) ).flushBuffer();
  }

  @Test
  public void testGetWriter() throws Exception {
    PrintWriter writer = response.getWriter();
    assertNotNull( writer );
  }
}
