/****************************************/
/* GraphPanel.java						*/
/* Created on: 14-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.curves;

import eu.cherrytree.zaria.editor.ReflectionTools;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.math.Curve;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import javax.swing.JPanel;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GraphPanel extends JPanel
{
	//--------------------------------------------------------------------------
	
	// TODO Undo/Redo
	
	//--------------------------------------------------------------------------
	
	public static class Point
	{
		private float x;
		private float y;

		public Point(float x, float y)
		{
			this.x = x;
			this.y = y;
		}				

		public float getX()
		{
			return x;
		}

		public float getY()
		{
			return y;
		}
		
		public void set(float x, float y)
		{
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString()
		{
			return "[" + x + " ; " + y + "]";
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public interface GraphPanelSeclectionListener
	{
		public void onPointSelected(int index);		
	}
	
	//--------------------------------------------------------------------------
		
	private ArrayList<Point> points = new ArrayList<>();	
	
	private Comparator<Point> comparator = new Comparator<Point>() {
		@Override
		public int compare(Point p1 ,Point p2)
		{
			return Double.compare(p1.getX(), p2.getX());
		}

	};
	
	private Curve.CurveType type = Curve.CurveType.Linear;
	
	private UnivariateFunction function;
	
	private float maxX,maxY,minX,minY;
	
	private PointListModel listModel;
	
	private float scaleX = 400.0f;
	private float scaleY = 400.0f;
	private boolean scaleWidth = true;
	private boolean scaleHeight= true;
	
	private int shiftX = 50;
	private int shiftY = -50;
	
	private boolean dragStarted = false;
	private int prevMouseX = 0;
	private int prevMouseY = 0;
	
	private DecimalFormat decimalFormat = new DecimalFormat("0.#####");
	
	private Point draggedPoint;
	private Point highlightedPoint;
	
	private GraphPanelSeclectionListener listener;

	//--------------------------------------------------------------------------
	
	public GraphPanel()
	{
		listModel = new PointListModel(points);
		
		enableEvents(AWTEvent.MOUSE_WHEEL_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK);				
	}

	//--------------------------------------------------------------------------
	
	public void init(Curve curve)
	{				
		float[] x = {};
		float[] y = {};
		Curve.CurveType c_type = Curve.CurveType.Linear;
		
		Field f_x = ReflectionTools.getField(Curve.class, "x");
		Field f_y = ReflectionTools.getField(Curve.class, "y");
		Field f_type = ReflectionTools.getField(Curve.class, "type");
		
		try
		{
			x = (float[]) f_x.get(curve);
			y = (float[]) f_y.get(curve);
			
			c_type = (Curve.CurveType) f_type.get(curve);
		}
		catch (IllegalArgumentException | IllegalAccessException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
		
		if (c_type != null)
			type = c_type;
		
		if (x.length == y.length && x.length >= type.getMinPoints())
		{
			for (int i = 0 ; i < x.length ; i++)
				points.add(new GraphPanel.Point(x[i], y[i]));
		}
		else
		{
			points.add(new GraphPanel.Point(0.0f, 0.0f));
			points.add(new GraphPanel.Point(0.5f, 0.5f));
			points.add(new GraphPanel.Point(1.0f, 1.0f));
		}
		
		scaleX = 400.0f;
		scaleY = 400.0f;

		shiftX = 50;
		shiftY = -50;

		dragStarted = false;
		prevMouseX = 0;
		prevMouseY = 0;
		
		updateGraph();
	}
	
	//--------------------------------------------------------------------------
	
	public void save(Curve curve) throws SecurityException, IllegalArgumentException, NoSuchFieldException
	{
		float[] x = new float[points.size()];
		float[] y = new float[points.size()];

		for (int i = 0 ; i < points.size() ; i++)
		{
			x[i] = points.get(i).getX();
			y[i] = points.get(i).getY();
		}
		
		ReflectionTools.setDefinitionFieldValue(curve, "x", x);
		ReflectionTools.setDefinitionFieldValue(curve, "y", y);
		ReflectionTools.setDefinitionFieldValue(curve, "type", type);
	}
	
	//--------------------------------------------------------------------------

	public Curve.CurveType getType()
	{
		return type;
	}
		
	//--------------------------------------------------------------------------
	
	public PointListModel getListModel()
	{
		return listModel;
	}
	
	//--------------------------------------------------------------------------

	public Point getPoint(int index)
	{
		return points.get(index);
	}
	
	//--------------------------------------------------------------------------
	
	public void setListener(GraphPanelSeclectionListener listener)
	{
		this.listener = listener;
	}
	
	//--------------------------------------------------------------------------
	
	public void setType(Curve.CurveType type)
	{
		if (this.type != type && points.size() >= type.getMinPoints())
		{
			this.type = type;
			
			update();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void update()
	{
		updateGraph();
		repaint();
	}
	
	//--------------------------------------------------------------------------
	
	public void removePointAtIndex(int index)
	{
		if (points.size() > type.getMinPoints() && index >= 0 && index < points.size())
		{
			Point p = points.remove(index);
			
			if (p == highlightedPoint)
			{
				highlightedPoint = null;
				
				if (listener != null)
					listener.onPointSelected(-1);
			}
			
			update();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void highlightPoint(int index)
	{
		if (index >= 0 && index < points.size())
		{
			highlightedPoint = points.get(index);		
			repaint();
		}
		else
		{
			highlightedPoint = null;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private void updateGraph()
	{					
		Collections.sort(points, comparator);
		
		if (highlightedPoint != null && listener != null)
			listener.onPointSelected(points.indexOf(highlightedPoint));
		
		recalculateFunction(points);
		
		listModel.update();
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g)
	{
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		float min = (0 - shiftX) / scaleX;
		float max = (getWidth() - shiftX) / scaleX;
		float delta = max - min;
		float spacing = 0.0f;
		
		for (double d = 0.001f ; d < delta ; d = d * 10.0f)
			spacing = (float) d / 10.0f;
		
		if (spacing != 0.0f)
		{		
			float start = (float) Math.floor(min / spacing) * spacing;
			for (float x = start ; x < max + spacing ; x += spacing)
			{
				int p_x = (int) (x * scaleX + shiftX);

				if (Math.abs(x) < 0.00001f)
					g.setColor(Color.GRAY);
				else
					g.setColor(Color.BLACK);
				
				g.drawLine(p_x, 0, p_x, getHeight());
				
				g.setColor(Color.GRAY);
				g.drawString("(" + decimalFormat.format(x) + ")", p_x + 5, getHeight() - 11);
			}
			
			min = (getHeight() + shiftY) / scaleY;
			max = (shiftY) / scaleY;
			start = (float) Math.floor(min / spacing) * spacing;

			for (float y = start ; y > max - spacing ; y -= spacing)
			{
				int p_y = (int) (getHeight() - y * scaleY + shiftY);

				if (Math.abs(y) < 0.00001f)
					g.setColor(Color.GRAY);
				else
					g.setColor(Color.BLACK);
				
				g.drawLine(0, p_y, getWidth(), p_y);
				
				g.setColor(Color.GRAY);
				g.drawString("(" + decimalFormat.format(y) + ")", 5, p_y + 11);
			}
		}
			
		g.setColor(Color.LIGHT_GRAY);
				
		float prev_x = (-1 - shiftX) / scaleX;
		float prev_y = getValue(prev_x);
		int p_j = (int) (getHeight() - prev_y * scaleY + shiftY);
		
		for (int i = 0 ; i < getWidth() ; i++)
		{
			float x = (i - shiftX) / scaleX;
			float y = getValue(x);
			
			int j = (int) (getHeight() - y * scaleY + shiftY);
						
			g.drawLine(i-1, p_j, i, j);
			
			p_j = j;
		}
						
		for (Point point : points)
		{
			if (point == draggedPoint)
			{
				g.setColor(Color.ORANGE);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-3), shiftY + (int) ((getHeight() - scaleY * point.getY())-3), 7, 7);

				g.setColor(Color.YELLOW);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-1), shiftY + (int) ((getHeight() - scaleY * point.getY())-1), 3, 3);						
			}
			else if (point == highlightedPoint)
			{
				g.setColor(Color.CYAN);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-3), shiftY + (int) ((getHeight() - scaleY * point.getY())-3), 7, 7);

				g.setColor(Color.BLUE);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-1), shiftY + (int) ((getHeight() - scaleY * point.getY())-1), 3, 3);	
			}
			else
			{
				g.setColor(Color.GRAY);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-3), shiftY + (int) ((getHeight() - scaleY * point.getY())-3), 7, 7);

				g.setColor(Color.LIGHT_GRAY);
				g.fillOval(shiftX + (int) (scaleX * point.getX()-1), shiftY + (int) ((getHeight() - scaleY * point.getY())-1), 3, 3);			
			}

		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e)
	{
		super.processMouseWheelEvent(e);
				
		if (scaleWidth)
			scaleX = Math.min(1000.0f, Math.max(0.0001f, scaleX - e.getUnitsToScroll()));
		
		if (scaleHeight)
			scaleY = Math.min(1000.0f, Math.max(0.0001f, scaleY - e.getUnitsToScroll()));
		
		repaint();
	}
	
	//--------------------------------------------------------------------------
	
	private Point getPoint(int x, int y)
	{
		for (Point p : points)
		{
			double p_x = p.getX() * scaleX + shiftX;
			double p_y = getHeight() - p.getY() * scaleY + shiftY;
						
			if (Math.abs(p_x - x) < 5 && Math.abs(p_y - y) < 5)
				return p;
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	protected void processMouseMotionEvent(MouseEvent e)
	{
		super.processMouseMotionEvent(e);
		
		if (dragStarted)
		{
			shiftX -= prevMouseX - e.getX();
			shiftY -= prevMouseY - e.getY();
			
			prevMouseX = e.getX();
			prevMouseY = e.getY();

			repaint();
		}
		
		if (draggedPoint != null)
		{
			draggedPoint.set((e.getX() - shiftX) / scaleX, (getHeight() - (e.getY() - shiftY)) / scaleY);
			updateGraph();
			repaint();
		}
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	protected void processMouseEvent(MouseEvent e)
	{
		super.processMouseEvent(e);

		if (e.getID() == MouseEvent.MOUSE_CLICKED && e.getButton() == MouseEvent.BUTTON1)
		{
			if (getPoint(e.getX(), e.getY()) == null)
			{
				Point p = new Point((e.getX() - shiftX) / scaleX, (getHeight() - (e.getY() - shiftY)) / scaleY);
				
				points.add(p);
				
				highlightedPoint = p;
				
				update();
			}
		}
		else if (e.getID() == MouseEvent.MOUSE_PRESSED)
		{
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				draggedPoint = getPoint(e.getX(), e.getY());
				highlightedPoint = draggedPoint;	
			}
			else if (e.getButton() == MouseEvent.BUTTON2)
			{
				prevMouseX = e.getX();
				prevMouseY = e.getY();
				dragStarted = true;
			}
		}
		else if (e.getID() == MouseEvent.MOUSE_RELEASED)
		{
			if (e.getButton() == MouseEvent.BUTTON1 && draggedPoint != null)
			{
				draggedPoint.set((e.getX() - shiftX) / scaleX, (getHeight() - (e.getY() - shiftY)) / scaleY);				
				draggedPoint = null;
			
				update();
			}
			else if (e.getButton() == MouseEvent.BUTTON2)
			{
				dragStarted = false;
			}
		}
	}	
	
	//--------------------------------------------------------------------------
	
	private void recalculateFunction(ArrayList<Point> points)
	{
		switch (type)
		{
			case Linear:
			{
				double[] x = new double[points.size()];
				double[] y = new double[points.size()];

				for (int i = 0 ; i < points.size() ; i++)
				{
					x[i] = points.get(i).getX();
					y[i] = points.get(i).getY();
				}

				minX = (float) x[0];
				minY = (float) y[0];

				maxX = (float) x[x.length-1];
				maxY = (float) y[y.length-1];
				
				function = new LinearInterpolator().interpolate(x, y);
			}
				break;
				
			case Spline:
			{
				double[] x = new double[points.size()];
				double[] y = new double[points.size()];

				for (int i = 0 ; i < points.size() ; i++)
				{
					x[i] = points.get(i).getX();
					y[i] = points.get(i).getY();
				}

				minX = (float) x[0];
				minY = (float) y[0];

				maxX = (float) x[x.length-1];
				maxY = (float) y[y.length-1];
				
				function = new SplineInterpolator().interpolate(x, y);
			}
				break;
				
			case Polynomial:
			{
				WeightedObservedPoints wob = new WeightedObservedPoints();
		
				minX = -Float.MAX_VALUE;
				minY = -Float.MAX_VALUE;

				maxX = Float.MAX_VALUE;
				maxY = Float.MAX_VALUE;
				
				for (Point p : points)
					wob.add(p.getX(), p.getY());

				function = new PolynomialFunction(PolynomialCurveFitter.create(points.size()).fit(wob.toList()));	
			}
				break;
		}
	}

	//--------------------------------------------------------------------------
	
	@Override
	protected void processKeyEvent(KeyEvent e)
	{
		super.processKeyEvent(e);
	
		if (e.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
				scaleWidth = false;
			else if (e.getID() == KeyEvent.KEY_RELEASED)
				scaleWidth = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			if (e.getID() == KeyEvent.KEY_PRESSED)
				scaleHeight = false;
			else if (e.getID() == KeyEvent.KEY_RELEASED)
				scaleHeight = true;
		}
	}
	
	//--------------------------------------------------------------------------
	
	private float getValue(float x)
	{
		if (x <= minX)
			return minY;
		else if (x >= maxX)
			return maxY;
		else
			return (float) function.value(x);
	}	
	
	//--------------------------------------------------------------------------
}
