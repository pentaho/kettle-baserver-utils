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
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;

public class ServletOutputStreamWrapper extends ServletOutputStream {

  private final OutputStream outputStream;

  public OutputStream getOutputStream() {
    return outputStream;
  }

  public ServletOutputStreamWrapper( OutputStream outputStream ) {
    this.outputStream = outputStream;
  }

  @Override
  public void write( int b ) throws IOException {
    this.outputStream.write( b );
  }

  @Override
  public void flush() throws IOException {
    super.flush();
    this.outputStream.flush();
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.outputStream.close();
  }

  @Override
  public boolean isReady() {
    return false;
  }

  @Override
  public void setWriteListener( WriteListener writeListener ) {

  }
}
