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

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Locale;

public class InternalHttpServletResponse implements HttpServletResponse {

  private static final String CHARSET_PREFIX = "charset=";

  private final ByteArrayOutputStream content;
  private final ServletOutputStreamWrapper servletOutputStream;

  private String characterEncoding = null;
  private PrintWriter writer;
  private int contentLength = 0;
  private String contentType;
  private int bufferSize = 4096;

  private boolean committed;
  //private int status = HttpServletResponse.SC_OK;
  private int status = HttpServletResponse.SC_NO_CONTENT;
  private String errorMessage;
  private String redirectedUrl;
  private String forwardedUrl;
  private String includedUrl;

  public InternalHttpServletResponse( ByteArrayOutputStream outputStream ) {
    this.content = outputStream;
    this.servletOutputStream = new ServletOutputStreamWrapper( this.content );
    this.writer = new PrintWriter( this.content );
  }

  public InternalHttpServletResponse() {
    this( new ByteArrayOutputStream() );
  }

  public boolean isOutputStreamAccessAllowed() {
    return true;
  }

  public boolean isWriterAccessAllowed() {
    return true;
  }


  @Override
  public ServletOutputStream getOutputStream() {
    if ( !isOutputStreamAccessAllowed() ) {
      throw new IllegalStateException( "OutputStream access not allowed" );
    }
    return this.servletOutputStream;
  }

  @Override
  public PrintWriter getWriter() throws UnsupportedEncodingException {
    if ( !isWriterAccessAllowed() ) {
      throw new IllegalStateException( "Writer access not allowed" );
    }

    /*
    if ( this.writer == null ) {
      Writer targetWriter = ( this.characterEncoding != null ?
        new OutputStreamWriter( this.content, this.characterEncoding ) : new OutputStreamWriter( this.content ) );
      this.writer = new PrintWriter( targetWriter );
    }
    return this.writer;
    */

    return this.writer;
  }

  public byte[] getContentAsByteArray() {
    flushBuffer();
    return this.content.toByteArray();
  }

  public String getContentAsString() throws UnsupportedEncodingException {
    flushBuffer();
    return ( this.characterEncoding != null ) ? this.content.toString( this.characterEncoding )
        : this.content.toString();
  }

  public int getContentLength() {
    return this.contentLength;
  }

  @Override
  public void setContentLength( int contentLength ) {
    this.contentLength = contentLength;
  }

  @Override
  public void setContentLengthLong( long l ) {

  }

  @Override public String getCharacterEncoding() {
    return null;
  }

  @Override public void setCharacterEncoding( String s ) {

  }

  @Override public String getHeader( String name ) {
    return "";
  }

  @Override public Collection<String> getHeaders( String name ) {
    return null;
  }

  @Override public Collection<String> getHeaderNames() {
    return null;
  }

  @Override
  public String getContentType() {
    return this.contentType;
  }

  @Override
  public void setContentType( String contentType ) {
    this.contentType = contentType;
    if ( contentType != null ) {
      int charsetIndex = contentType.toLowerCase().indexOf( CHARSET_PREFIX );
      if ( charsetIndex != -1 ) {
        String encoding = contentType.substring( charsetIndex + CHARSET_PREFIX.length() );
        setCharacterEncoding( encoding );
      }
    }
  }

  @Override
  public int getBufferSize() {
    return this.bufferSize;
  }

  @Override
  public void setBufferSize( int bufferSize ) {
    this.bufferSize = bufferSize;
  }

  @Override
  public void flushBuffer() {
    if ( this.writer != null ) {
      this.writer.flush();
    }
    if ( this.servletOutputStream != null ) {
      try {
        this.servletOutputStream.flush();
      } catch ( IOException ex ) {
        throw new IllegalStateException( "Could not flush OutputStream: " + ex.getMessage() );
      }
    }
    this.committed = true;
  }

  @Override
  public void resetBuffer() {
    if ( this.committed ) {
      throw new IllegalStateException( "Cannot reset buffer - response is already committed" );
    }
    this.content.reset();
  }

  @Override
  public boolean isCommitted() {
    return this.committed;
  }

  @Override
  public void reset() {
    resetBuffer();
    this.characterEncoding = null;
    this.contentLength = 0;
    this.contentType = null;
    this.status = HttpServletResponse.SC_OK;
    this.errorMessage = null;
  }

  @Override public Locale getLocale() {
    return null;
  }

  @Override public void setLocale( Locale locale ) {

  }

  @Override
  public void sendError( int status, String errorMessage ) throws IOException {
    if ( this.committed ) {
      throw new IllegalStateException( "Cannot set error status - response is already committed" );
    }
    this.status = status;
    this.errorMessage = errorMessage;
    this.committed = true;
  }

  @Override
  public void sendError( int status ) throws IOException {
    if ( this.committed ) {
      throw new IllegalStateException( "Cannot set error status - response is already committed" );
    }

    this.status = status;
    this.committed = true;
  }

  @Override
  public void sendRedirect( String url ) throws IOException {
    if ( this.committed ) {
      throw new IllegalStateException( "Cannot send redirect - response is already committed" );
    }

    this.redirectedUrl = url;
    this.committed = true;
  }

  @Override public void setDateHeader( String s, long l ) {

  }

  @Override public void addDateHeader( String s, long l ) {

  }

  @Override public void setHeader( String s, String s2 ) {

  }

  @Override public void addHeader( String s, String s2 ) {

  }

  @Override public void setIntHeader( String s, int i ) {

  }

  @Override public void addIntHeader( String s, int i ) {

  }

  public void setStatus( int status, String errorMessage ) {
    this.status = status;
    this.errorMessage = errorMessage;
  }

  @Override public void addCookie( Cookie cookie ) {

  }

  @Override public boolean containsHeader( String s ) {
    return false;
  }

  public String encodeURL( String url ) {
    return url;
  }

  public String encodeRedirectURL( String url ) {
    return url;
  }

  public String encodeUrl( String url ) {
    return url;
  }

  public String encodeRedirectUrl( String url ) {
    return url;
  }

  public String getRedirectedUrl() {
    return this.redirectedUrl;
  }

  public int getStatus() {
    return this.status;
  }

  @Override
  public void setStatus( int status ) {
    this.status = status;
  }

  public String getErrorMessage() {
    return this.errorMessage;
  }

  public String getForwardedUrl() {
    return this.forwardedUrl;
  }

  public void setForwardedUrl( String forwardedUrl ) {
    this.forwardedUrl = forwardedUrl;
  }

  public String getIncludedUrl() {
    return this.includedUrl;
  }

  public void setIncludedUrl( String includedUrl ) {
    this.includedUrl = includedUrl;
  }
}
