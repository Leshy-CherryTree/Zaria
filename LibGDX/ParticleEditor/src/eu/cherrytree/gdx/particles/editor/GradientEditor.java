/****************************************/
/* GradientEditor.java					*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class GradientEditor extends JPanel implements MouseListener, MouseMotionListener
{
	//--------------------------------------------------------------------------

	private ArrayList<Color> colors = new ArrayList();
	private ArrayList<Float> percentages = new ArrayList();
	private int handleWidth = 12;
	private int handleHeight = 12;
	private int gradientX = handleWidth / 2;
	private int gradientY = 0;
	private int gradientWidth;
	private int gradientHeight;
	private int dragIndex = -1;
	private int selectedIndex;
	
	private GradientPanel gradientPanel;
	
	//--------------------------------------------------------------------------

	public GradientEditor(GradientPanel gradientPanel)
	{
		this.gradientPanel = gradientPanel;
		
		setPreferredSize(new Dimension(100, 30));

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	//--------------------------------------------------------------------------
	
	public ArrayList<Color> getColors()
	{
		return colors;
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<Float> getPercentages()
	{
		return percentages;
	}
	
	//--------------------------------------------------------------------------

	public void setColor(Color color)
	{
		if (selectedIndex == -1)
			return;
		
		colors.set(selectedIndex, color);
		
		repaint();
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		int width = getWidth() - 1;
		int height = getHeight();

		gradientWidth = width - handleWidth;
		gradientHeight = height - 16;

		g.translate(gradientX, gradientY);
		for (int i = 0, n = colors.size() == 1 ? 1 : colors.size() - 1; i < n; i++)
		{
			Color color1 = colors.get(i);
			Color color2 = colors.size() == 1 ? color1 : colors.get(i + 1);
			float percent1 = percentages.get(i);
			float percent2 = colors.size() == 1 ? 1 : percentages.get(i + 1);
			int point1 = (int) (percent1 * gradientWidth);
			int point2 = (int) Math.ceil(percent2 * gradientWidth);
			g.setPaint(new GradientPaint(point1, 0, color1, point2, 0, color2, false));
			g.fillRect(point1, 0, point2 - point1, gradientHeight);
		}
		
		g.setPaint(null);
		g.setColor(Color.black);
		g.drawRect(0, 0, gradientWidth, gradientHeight);

		int y = gradientHeight;
		int[] yPoints = new int[3];
		yPoints[0] = y;
		yPoints[1] = y + handleHeight;
		yPoints[2] = y + handleHeight;
		int[] xPoints = new int[3];
		
		for (int i = 0, n = colors.size(); i < n; i++)
		{
			int x = (int) (percentages.get(i) * gradientWidth);
			xPoints[0] = x;
			xPoints[1] = x - handleWidth / 2;
			xPoints[2] = x + handleWidth / 2;
			if (i == selectedIndex)
			{
				g.setColor(colors.get(i));
				g.fillPolygon(xPoints, yPoints, 3);
				g.fillRect(xPoints[1], yPoints[1] + 2, handleWidth + 1, 2);
				g.setColor(Color.black);
			}
			g.drawPolygon(xPoints, yPoints, 3);
		}
		
		g.translate(-gradientX, -gradientY);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mousePressed(MouseEvent event)
	{
		dragIndex = -1;
		int mouseX = event.getX();
		int mouseY = event.getY();
		int y = gradientY + gradientHeight;
		
		for (int i = 0, n = colors.size(); i < n; i++)
		{
			int x = gradientX + (int) (percentages.get(i) * gradientWidth) - handleWidth / 2;
			
			if (mouseX >= x && mouseX <= x + handleWidth && mouseY >= gradientY && mouseY <= y + handleHeight)
			{
				dragIndex = selectedIndex = i;
				handleSelected(colors.get(selectedIndex));
				repaint();
				break;
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseReleased(MouseEvent event)
	{
		if (dragIndex != -1)
		{
			dragIndex = -1;
			repaint();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent event)
	{
		int mouseX = event.getX();
		int mouseY = event.getY();
		
		if (event.getClickCount() == 2)
		{
			if (percentages.size() <= 1)
				return;
			if (selectedIndex == -1 || selectedIndex == 0)
				return;
			int y = gradientY + gradientHeight;
			int x = gradientX + (int) (percentages.get(selectedIndex) * gradientWidth) - handleWidth / 2;
			if (mouseX >= x && mouseX <= x + handleWidth && mouseY >= gradientY && mouseY <= y + handleHeight)
			{
				percentages.remove(selectedIndex);
				colors.remove(selectedIndex);
				selectedIndex--;
				dragIndex = selectedIndex;
				if (percentages.size() == 2)
					percentages.set(1, 1f);
				handleSelected(colors.get(selectedIndex));
				repaint();
			}
			return;
		}
		
		if (mouseX < gradientX || mouseX > gradientX + gradientWidth)
			return;
		
		if (mouseY < gradientY || mouseY > gradientY + gradientHeight)
			return;
		
		float percent = (event.getX() - gradientX) / (float) gradientWidth;
		if (percentages.size() == 1)
			percent = 1f;
		
		for (int i = 0, n = percentages.size(); i <= n; i++)
		{
			if (i == n || percent < percentages.get(i))
			{
				percentages.add(i, percent);
				colors.add(i, colors.get(i - 1));
				dragIndex = selectedIndex = i;
				handleSelected(colors.get(selectedIndex));
				gradientPanel.updateColor();
				repaint();
				break;
			}
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseDragged(MouseEvent event)
	{
		if (dragIndex == -1 || dragIndex == 0 || dragIndex == percentages.size() - 1)
			return;
		
		float percent = (event.getX() - gradientX) / (float) gradientWidth;
		percent = Math.max(percent, percentages.get(dragIndex - 1) + 0.01f);
		percent = Math.min(percent, percentages.get(dragIndex + 1) - 0.01f);
		percentages.set(dragIndex, percent);
		gradientPanel.updateColor();
		repaint();
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void mouseEntered(MouseEvent e)
	{
		// Intentionally empty.
	}
		
	//--------------------------------------------------------------------------

	@Override
	public void mouseExited(MouseEvent e)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseMoved(MouseEvent e)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
	
	public void handleSelected(Color color)
	{
		gradientPanel.setColor(color);
	}
	
	//--------------------------------------------------------------------------
}