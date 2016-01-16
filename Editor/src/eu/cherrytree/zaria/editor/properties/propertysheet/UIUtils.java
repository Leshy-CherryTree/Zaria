/********************************************/
/* UIUtils.java								*/
/*											*/
/* Initial version released by L2FProd.com	*/
/* under the Apache License, Version 2.0	*/
/*											*/
/* Adapted, modified and released			*/
/* by Cherry Tree Studio under EUPL v1.1	*/
/*											*/
/* Copyright Cherry Tree Studio 2013		*/
/********************************************/

package eu.cherrytree.zaria.editor.properties.propertysheet;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.io.IOException;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;


public class UIUtils
{
	//--------------------------------------------------------------------------
	
	public final static Border PANEL_BORDER = BorderFactory.createEmptyBorder(3, 3, 3, 3);
	public final static Border EMPTY_BORDER = BorderFactory.createEmptyBorder();

	//--------------------------------------------------------------------------

	public static void setBorder(JComponent component)
	{
		if(component instanceof JPanel)
		{
			component.setBorder(PANEL_BORDER);
		}
	}

	//--------------------------------------------------------------------------

	public static void setBorderLayout(Container container)
	{
		container.setLayout(new BorderLayout(3, 3));
	}

	//--------------------------------------------------------------------------

	public static void htmlize(JComponent component)
	{
		htmlize(component, UIManager.getFont("Button.font"));
	}

	//--------------------------------------------------------------------------

	public static void htmlize(JComponent component, Font font)
	{
		String stylesheet =
				"body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
				+ font.getName()
				+ "; font-size: "
				+ font.getSize()
				+ "pt;	}"
				+ "a, p, li { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0; font-family: "
				+ font.getName()
				+ "; font-size: "
				+ font.getSize()
				+ "pt;	}";

			HTMLDocument doc = null;
			
			if(component instanceof JEditorPane)
			{
				if(((JEditorPane) component).getDocument() instanceof HTMLDocument)
					doc = (HTMLDocument) ((JEditorPane) component).getDocument();
			}
			else
			{
				View v = (View) component.getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey);
				
				if(v != null && v.getDocument() instanceof HTMLDocument)
					doc = (HTMLDocument) v.getDocument();
			}
			
			if(doc != null)
			{
				try
				{
					doc.getStyleSheet().loadRules(new java.io.StringReader(stylesheet), null);
				}
				catch(IOException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}
			} 

	}

	//--------------------------------------------------------------------------

	public static Border addMargin(Border border)
	{
		return new CompoundBorder(border, PANEL_BORDER);
	}

	//--------------------------------------------------------------------------
}
