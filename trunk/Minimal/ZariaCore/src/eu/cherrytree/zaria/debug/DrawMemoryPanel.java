/****************************************/
/* DrawMemoryPanel.java					*/
/* Created on: 13-Jun-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class DrawMemoryPanel extends JPanel
{
	//--------------------------------------------------------------------------
	
	private class UpdateMemory extends TimerTask
	{
		@Override
		public void run()
		{
			memory.add(0, (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
			
			if(memory.size() > DrawMemoryPanel.this.getWidth())
				memory.remove(memory.size()-1);
			
			DrawMemoryPanel.this.repaint();
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private ArrayList<Double> memory = new ArrayList<>();	
	private Timer timer = new Timer();
	
	private DecimalFormat format = new DecimalFormat("#.00");
	
	private Font font = new Font("Cantarell", Font.BOLD, 12);
	
	private Color safeColor = new Color(0x0ec2f6);
	private Color warningColor = new Color(0xd91616);

	//--------------------------------------------------------------------------
	
	public DrawMemoryPanel()
	{
		timer.scheduleAtFixedRate(new UpdateMemory(), 500, 500);
		memory.add(0, (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()));
	}

	//--------------------------------------------------------------------------
	
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setFont(font);	
		
		double tot_mem = Runtime.getRuntime().totalMemory();		
														
		for(int i = 0 ; i < memory.size() ; i++)
		{
			double ratio = memory.get(i) / tot_mem;
			int h = (int)(ratio * getHeight());
			
			if(ratio > 0.85)
				g2.setColor(warningColor);
			else
				g2.setColor(safeColor);
			
			g2.drawLine(getWidth()-i, getHeight(), getWidth()-i, getHeight() - h);		
		}				
		
		tot_mem /= 1024*1024;
		double taken = memory.get(0) / (1024*1024);
		
		g2.setColor(Color.LIGHT_GRAY);
		
		g2.drawLine(0, 0, getWidth(), 0);
		g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
		g2.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);				
		
		StringBuilder str_build = new StringBuilder();		
		str_build.append(format.format(taken)).append(" / ").append(format.format(tot_mem)).append(" MB");
		
		String mem_text = str_build.toString();
		
		int width = g2.getFontMetrics(font).stringWidth(mem_text);
		int height = g2.getFontMetrics(font).getHeight();
		
		g2.setColor(Color.DARK_GRAY);
		g2.drawString(mem_text, getWidth()/2 - width/2 + 1, getHeight()/2 + height/4 + 1);
		
		g2.setColor(Color.WHITE);
		g2.drawString(mem_text, getWidth()/2 - width/2, getHeight()/2 + height/4);
	}
	
	//--------------------------------------------------------------------------
}
