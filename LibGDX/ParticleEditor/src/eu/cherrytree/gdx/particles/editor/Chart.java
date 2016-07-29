/****************************************/
/* Chart.java							*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public abstract class Chart extends JPanel implements MouseListener, MouseMotionListener
{
	//--------------------------------------------------------------------------

	static private final int POINT_SIZE = 6;
	static private final int POINT_SIZE_EXPANDED = 10;
	private ArrayList<Point> points = new ArrayList();
	private int numberHeight;
	private int chartX, chartY;
	private int chartWidth, chartHeight;
	private int maxX, maxY;
	private int overIndex = -1;
	private int movingIndex = -1;
	private boolean isExpanded;
	private String title;
	private boolean moveAll = false;
	private boolean moveAllProportionally = false;
	private int moveAllPrevY;
	
	//--------------------------------------------------------------------------

	public Chart(String title)
	{
		this.title = title;

		setLayout(new GridBagLayout());

		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	//--------------------------------------------------------------------------

	public void addPoint(float x, float y)
	{
		points.add(new Point(x, y));
	}
	
	//--------------------------------------------------------------------------
	
	public float[] getValuesX()
	{
		float[] values = new float[points.size()];
		int i = 0;
		for (Point point : points)
			values[i++] = point.x;
		return values;
	}
	
	//--------------------------------------------------------------------------

	public float[] getValuesY()
	{
		float[] values = new float[points.size()];
		int i = 0;
		for (Point point : points)
			values[i++] = point.y;
		return values;
	}
	
	//--------------------------------------------------------------------------

	public void setValues(float[] x, float[] y)
	{
		points.clear();
		for (int i = 0; i < x.length; i++)
			points.add(new Point(x[i], y[i]));
	}
	
	//--------------------------------------------------------------------------

	private Point pixelToPoint(float x, float y)
	{
		Point point = new Point();
		point.x = Math.min(maxX, Math.max(0, x - chartX) / (float) chartWidth * maxX);
		point.y = Math.min(maxY, Math.max(0, chartHeight - (y - chartY)) / (float) chartHeight * maxY);
		return point;
	}
	
	//--------------------------------------------------------------------------

	private Point pointToPixel(Point point)
	{
		Point pixel = new Point();
		pixel.x = chartX + (int) (chartWidth * (point.x / (float) maxX));
		pixel.y = chartY + chartHeight - (int) (chartHeight * (point.y / (float) maxY));
		return pixel;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);

		Graphics2D g = (Graphics2D) graphics;
		FontMetrics metrics = g.getFontMetrics();
		
		if (numberHeight == 0)
			numberHeight = getFont().layoutGlyphVector(g.getFontRenderContext(), new char[]{'0'}, 0, 1, Font.LAYOUT_LEFT_TO_RIGHT).getGlyphPixelBounds(0, g.getFontRenderContext(), 0, 0).height;

		int width = getWidth();
		
		if (!isExpanded)
			width = Math.min(150, width);
		
		width = Math.max(100, width);
		int height = getHeight();
		int maxAxisLabelWidth;
		int yAxisWidth;
		
		if (isExpanded)
		{
			maxAxisLabelWidth = metrics.stringWidth("100%");
			yAxisWidth = maxAxisLabelWidth + 8;
			chartX = yAxisWidth;
			chartY = numberHeight / 2 + 1;
			chartWidth = width - yAxisWidth - 2;
			chartHeight = height - chartY - numberHeight - 8;
		}
		else
		{
			maxAxisLabelWidth = 0;
			yAxisWidth = 2;
			chartX = yAxisWidth;
			chartY = 2;
			chartWidth = width - yAxisWidth - 2;
			chartHeight = height - chartY - 3;
		}

		g.setColor(Color.DARK_GRAY);
		g.fillRect(chartX, chartY, chartWidth, chartHeight);
		g.setColor(Color.BLACK);
		g.drawRect(chartX, chartY, chartWidth, chartHeight);

		maxX = 1;
		{
			int y = height;
			
			if (isExpanded)
				y -= numberHeight;
			else
				y += 5;
			
			int xSplit = (int) Math.min(10, chartWidth / (maxAxisLabelWidth * 1.5f));
			
			for (int i = 0; i <= xSplit; i++)
			{
				float percent = i / (float) xSplit;
				String label = axisLabel(maxX * percent);
				int labelWidth = metrics.stringWidth(label);
				int x = (int) (yAxisWidth + chartWidth * percent);
				
				if (i != 0 && i != xSplit)
				{
					g.setColor(Color.BLACK);
					g.drawLine(x, chartY + 1, x, chartY + chartHeight);
				}
				
				g.drawLine(x, y - 4, x, y - 8);
				
				if (isExpanded)
				{
					g.setColor(Color.LIGHT_GRAY);
					x -= labelWidth / 2;
					
					if (i == xSplit)
						x = Math.min(x, width - labelWidth);
					
					g.drawString(label, x, y + numberHeight);
				}
			}
		}

		maxY = 1;
		
		{
			int ySplit = isExpanded ? Math.min(10, chartHeight / (numberHeight * 3)) : 4;
			for (int i = 0; i <= ySplit; i++)
			{
				float percent = i / (float) ySplit;
				String label = axisLabel(maxY * percent);
				int labelWidth = metrics.stringWidth(label);
				int y = (int) (chartY + chartHeight - chartHeight * percent);
				
				if (isExpanded)
				{
					g.setColor(Color.LIGHT_GRAY);
					g.drawString(label, yAxisWidth - 6 - labelWidth, y + numberHeight / 2);
				}
				
				if (i != 0 && i != ySplit)
				{
					g.setColor(Color.BLACK);
					g.drawLine(chartX, y, chartX + chartWidth - 1, y);
				}
				
				g.drawLine(yAxisWidth - 4, y, yAxisWidth, y);
			}
		}
		
		{
			int titleWidth = metrics.stringWidth(title);
			int x = yAxisWidth + chartWidth / 2 - titleWidth / 2;
			int y = chartY + chartHeight / 2 - numberHeight / 2;
			g.setColor(Color.DARK_GRAY);
			g.fillRect(x - 2, y - 2, titleWidth + 4, numberHeight + 4);
			g.setColor(Color.BLACK);
			g.drawString(title, x, y + numberHeight);
		}

		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(isExpanded ? 2 : 1));
		
		int lastX = -1, lastY = -1;
		
		for (Point point : points)
		{
			Point pixel = pointToPixel(point);
			if (lastX != -1)
				g.drawLine(lastX, lastY, (int) pixel.x, (int) pixel.y);
			lastX = (int) pixel.x;
			lastY = (int) pixel.y;
		}
		
		g.drawLine(lastX, lastY, chartX + chartWidth - 1, lastY);
		
		for (int i = 0, n = points.size(); i < n; i++)
		{
			Point point = points.get(i);
			Point pixel = pointToPixel(point);
			
			if (overIndex == i)
				g.setColor(Color.ORANGE);
			else
				g.setColor(Color.LIGHT_GRAY);
			
			String label = valueLabel(point.y);
			int labelWidth = metrics.stringWidth(label);
			int pointSize = isExpanded ? POINT_SIZE_EXPANDED : POINT_SIZE;
			int x = (int) pixel.x - pointSize / 2;
			int y = (int) pixel.y - pointSize / 2;
			
			g.fillOval(x, y, pointSize, pointSize);
			
			if (isExpanded)
			{
				g.setColor(Color.LIGHT_GRAY);
				x = Math.max(chartX + 2, Math.min(chartX + chartWidth - labelWidth, x));
				y -= 3;
				
				if (y < chartY + numberHeight + 3)
				{
					y += 27;
				}
				else if (n > 1)
				{
					Point comparePoint = i == n - 1 ? points.get(i - 1) : points.get(i + 1);
					if (y < chartY + chartHeight - 27 && comparePoint.y > point.y)
						y += 27;
				}
				
				g.drawString(label, x, y);
			}
		}
	}
	
	//--------------------------------------------------------------------------

	private String valueLabel(float value)
	{
		value = (int) (value * 1000) / 10f;
		
		if (value % 1 == 0)
			return String.valueOf((int) value) + '%';
		else
			return String.valueOf(value) + '%';
	}
	
	//--------------------------------------------------------------------------

	private String axisLabel(float value)
	{
		value = (int) (value * 100);
		
		if (value % 1 == 0)
			return String.valueOf((int) value) + '%';
		else
			return String.valueOf(value) + '%';
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public void mousePressed(MouseEvent event)
	{
		movingIndex = overIndex;
		moveAll = event.isControlDown();
		
		if (moveAll)
		{
			moveAllProportionally = event.isShiftDown();
			moveAllPrevY = event.getY();
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseReleased(MouseEvent event)
	{
		movingIndex = -1;
		moveAll = false;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseClicked(MouseEvent event)
	{
		if (event.getClickCount() == 2)
		{
			if (overIndex <= 0 || overIndex >= points.size())
				return;
			points.remove(overIndex);
			pointsChanged();
			repaint();
			return;
		}
		
		if (movingIndex != -1)
			return;
		
		if (overIndex != -1)
			return;
		
		int mouseX = event.getX();
		int mouseY = event.getY();
		
		if (mouseX < chartX || mouseX > chartX + chartWidth)
			return;
		
		if (mouseY < chartY || mouseY > chartY + chartHeight)
			return;
		
		Point newPoint = pixelToPoint(mouseX, mouseY);
		int i = 0;
		Point lastPoint = null;
		
		for (Point point : points)
		{
			if (point.x > newPoint.x)
			{
				if (Math.abs(point.x - newPoint.x) < 0.001f)
					return;
				
				if (lastPoint != null && Math.abs(lastPoint.x - newPoint.x) < 0.001f)
					return;
				
				points.add(i, newPoint);
				overIndex = i;
				pointsChanged();
				repaint();
				
				return;
			}
			
			lastPoint = point;
			i++;
		}
		
		overIndex = points.size();
		points.add(newPoint);
		pointsChanged();
		repaint();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseDragged(MouseEvent event)
	{
		if (movingIndex == -1 || movingIndex >= points.size())
			return;
		if (moveAll)
		{
			int newY = event.getY();
			float deltaY = (moveAllPrevY - newY) / (float) chartHeight * maxY;
			for (Point point : points)
			{
				point.y = Math.min(maxY, Math.max(0, point.y + (moveAllProportionally ? deltaY * point.y : deltaY)));
			}
			moveAllPrevY = newY;
		}
		else
		{
			float nextX = movingIndex == points.size() - 1 ? maxX : points.get(movingIndex + 1).x - 0.001f;
			if (movingIndex == 0)
				nextX = 0;
			float prevX = movingIndex == 0 ? 0 : points.get(movingIndex - 1).x + 0.001f;
			Point point = points.get(movingIndex);
			point.x = Math.min(nextX, Math.max(prevX, (event.getX() - chartX) / (float) chartWidth * maxX));
			point.y = Math.min(maxY, Math.max(0, chartHeight - (event.getY() - chartY)) / (float) chartHeight * maxY);
		}
		pointsChanged();
		repaint();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseMoved(MouseEvent event)
	{
		int mouseX = event.getX();
		int mouseY = event.getY();
		int oldIndex = overIndex;
		overIndex = -1;
		int pointSize = isExpanded ? POINT_SIZE_EXPANDED : POINT_SIZE;
		int i = 0;
		for (Point point : points)
		{
			int x = chartX + (int) (chartWidth * (point.x / (float) maxX));
			int y = chartY + chartHeight - (int) (chartHeight * (point.y / (float) maxY));
			if (Math.abs(x - mouseX) <= pointSize && Math.abs(y - mouseY) <= pointSize)
			{
				overIndex = i;
				break;
			}
			i++;
		}
		if (overIndex != oldIndex)
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

	public boolean isExpanded()
	{
		return isExpanded;
	}
	
	//--------------------------------------------------------------------------

	public void setExpanded(boolean isExpanded)
	{
		this.isExpanded = isExpanded;
	}
	
	//--------------------------------------------------------------------------

	public void setTitle(String title)
	{
		this.title = title;
	}
	
	//--------------------------------------------------------------------------
	
	public abstract void pointsChanged();
	
	//--------------------------------------------------------------------------
}
