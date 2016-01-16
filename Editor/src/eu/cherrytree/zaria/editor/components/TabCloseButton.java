/****************************************/
/* TabCloseButton.java					*/
/* Created on: 04-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class TabCloseButton extends JButton
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private class TabCloseButtonMouseListener extends MouseAdapter
	{
		@Override
		public void mouseEntered(MouseEvent e)
		{
			Component component = e.getComponent();
			if(component instanceof AbstractButton)
			{
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
			Component component = e.getComponent();
			if(component instanceof AbstractButton)
			{
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	}
			
	//--------------------------------------------------------------------------
	
	private TabCloseButtonMouseListener buttonMouseListener = new TabCloseButtonMouseListener();

	//--------------------------------------------------------------------------

	public TabCloseButton(JTabbedPane pane)
	{
		setPreferredSize(new Dimension(17, 17));
		setToolTipText("Close tab");
		
		setUI(new BasicButtonUI());
		
		setContentAreaFilled(false);

		setFocusable(false);
		setBorder(BorderFactory.createEtchedBorder());
		setBorderPainted(false);

		addMouseListener(buttonMouseListener);
		setRolloverEnabled(true);	
	}

	//--------------------------------------------------------------------------

	@Override
	public void updateUI()
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g.create();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		
		int delta = 6;

		if(getModel().isPressed())
			g2.setColor(Color.BLACK);
		else
			g2.setColor(Color.GRAY);
		
		g2.drawLine(delta + 1 - 1, delta - 1, getWidth() - delta + 1, getHeight() - delta);
		g2.drawLine(getWidth() - delta + 1, delta - 1, delta + 1 - 1, getHeight() - delta);
		
		if(getModel().isPressed())
			g2.setColor(Color.LIGHT_GRAY);
		else
			g2.setColor(Color.GRAY);
		
		g2.drawLine(delta - 1 - 1, delta - 1, getWidth() - delta - 1, getHeight() - delta);
		g2.drawLine(getWidth() - delta - 1, delta - 1, delta - 1 - 1, getHeight() - delta);
					
		if(getModel().isPressed())
			g2.setColor(Color.LIGHT_GRAY);
		else
			g2.setColor(Color.BLACK);
				
		g2.drawLine(delta - 1, delta - 1, getWidth() - delta, getHeight() - delta);
		g2.drawLine(getWidth() - delta, delta - 1, delta - 1, getHeight() - delta);

		g2.dispose();
	}
	
	//--------------------------------------------------------------------------
}