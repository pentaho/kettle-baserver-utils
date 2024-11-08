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

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ServletInputStreamWrapper extends ServletInputStream {

  private final InputStream inputStream;

  public InputStream getInputStream() {
    return this.inputStream;
  }

  public ServletInputStreamWrapper( InputStream inputStream ) {
    this.inputStream = inputStream;
  }

  @Override
  public int read() throws IOException {
    return this.inputStream.read();
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.inputStream.close();
  }

  @Override
  public boolean isFinished() {
    return false;
  }

  @Override
  public boolean isReady() {
    return false;
  }

  @Override
  public void setReadListener( ReadListener readListener ) {

  }
}
