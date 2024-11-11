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


package org.pentaho.di.baserver.utils.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.pentaho.di.ui.core.PropsUI;

import java.util.ArrayList;
import java.util.List;

public class CheckBoxBuilder extends WidgetBuilder<Button> {

  private String text;
  private List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();

  public CheckBoxBuilder( Composite parent, PropsUI props ) {
    super( parent, props );
  }

  public CheckBoxBuilder setText( String text ) {
    this.text = text;
    return this;
  }

  public CheckBoxBuilder addSelectionListener( SelectionListener selectionListener ) {
    selectionListeners.add( selectionListener );
    return this;
  }

  @Override
  protected Button createWidget( Composite parent ) {
    Button checkBox = createButton( parent, SWT.CHECK );
    checkBox.setText( text );
    for ( SelectionListener selectionListener : selectionListeners ) {
      checkBox.addSelectionListener( selectionListener );
    }
    return checkBox;
  }

  protected Button createButton( Composite parent, int i ) {
    return new Button( parent, i );
  }
}
