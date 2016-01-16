/********************************************/
/* PercentLayout.java						*/
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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * PercentLayout. <BR>Constraint based layout which allow the space to be
 * splitted using percentages. The following are allowed when adding components
 * to container:
 * <ul>
 * <li>container.add(component); <br>in this case, the component will be
 * sized to its preferred size
 * <li>container.add(component, "100"); <br>in this case, the component will
 * have a width (or height) of 100
 * <li>container.add(component, "25%"); <br>in this case, the component will
 * have a width (or height) of 25 % of the container width (or height) <br>
 * <li>container.add(component, "*"); <br>in this case, the component will
 * take the remaining space. if several components use the "*" constraint the
 * space will be divided among the components.
 * </ul>
 * 
 * @javabean.class
 *          name="PercentLayout"
 *          shortDescription="A layout supports constraints expressed in percent."
 */
public final class PercentLayout implements LayoutManager2
{
	//--------------------------------------------------------------------------
	
	/** Useful constant to layout the components horizontally (from top to bottom). */
	public final static int HORIZONTAL = 0;
	
	/** Useful constant to layout the components vertically (from left to right). */
	public final static int VERTICAL = 1;

	//--------------------------------------------------------------------------
	
	private static class Constraint
	{
		protected Object value;

		private Constraint(Object value)
		{
			this.value = value;
		}
	}

	//--------------------------------------------------------------------------
	
	private static class NumberConstraint extends Constraint
	{
		NumberConstraint(int d)
		{
			this(new Integer(d));
		}

		NumberConstraint(Integer d)
		{
			super(d);
		}

		public int intValue()
		{
			return ((Integer) value).intValue();
		}
	}
	
	//--------------------------------------------------------------------------

	private static class PercentConstraint extends Constraint
	{
		PercentConstraint(float d)
		{
			super(new Float(d));
		}

		public float floatValue()
		{
			return ((Float) value).floatValue();
		}
	}
	
	//--------------------------------------------------------------------------
	
	private final static Constraint REMAINING_SPACE = new Constraint("*");
	private final static Constraint PREFERRED_SIZE = new Constraint("");
	
	//--------------------------------------------------------------------------
	
	private int orientation;
	private int gap;
	
	private HashMap<Component, Constraint> componentToConstraint = new HashMap<>();

	//--------------------------------------------------------------------------

	/**
	 * Creates a new HORIZONTAL PercentLayout with a gap of 0.
	 */
	public PercentLayout()
	{
		this(HORIZONTAL, 0);
	}

	//--------------------------------------------------------------------------

	public PercentLayout(int orientation, int gap)
	{
		setOrientation(orientation);
		this.gap = gap;
	}

	//--------------------------------------------------------------------------

	public void setGap(int gap)
	{
		this.gap = gap;
	}

	//--------------------------------------------------------------------------

	public int getGap()
	{
		return gap;
	}

	//--------------------------------------------------------------------------

	public void setOrientation(int orientation)
	{
		if(orientation != HORIZONTAL && orientation != VERTICAL)
		{
			throw new IllegalArgumentException("Orientation must be one of HORIZONTAL or VERTICAL");
		}
		this.orientation = orientation;
	}

	//--------------------------------------------------------------------------
	
	public int getOrientation()
	{
		return orientation;
	}

	//--------------------------------------------------------------------------

	public void setConstraint(Component component, Object constraints)
	{
		if(constraints instanceof Constraint)
		{
			componentToConstraint.put(component, (Constraint) constraints);
		}
		else if(constraints instanceof Number)
		{
			setConstraint(component, new NumberConstraint(((Number) constraints).intValue()));
		}
		else if("*".equals(constraints))
		{
			setConstraint(component, REMAINING_SPACE);
		}
		else if("".equals(constraints))
		{
			setConstraint(component, PREFERRED_SIZE);
		}
		else if(constraints instanceof String)
		{
			String s = (String) constraints;
			if(s.endsWith("%"))
			{
				float value = Float.valueOf(s.substring(0, s.length() - 1))
						.floatValue() / 100;
				if(value > 1 || value < 0)
					throw new IllegalArgumentException("percent value must be >= 0 and <= 100");
				setConstraint(component, new PercentConstraint(value));
			}
			else
			{
				setConstraint(component, new NumberConstraint(Integer.valueOf(s)));
			}
		}
		else if(constraints == null)
		{
			// null constraint means preferred size
			setConstraint(component, PREFERRED_SIZE);
		}
		else
		{
			throw new IllegalArgumentException("Invalid Constraint");
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public void addLayoutComponent(Component component, Object constraints)
	{
		setConstraint(component, constraints);
	}

	//--------------------------------------------------------------------------

	@Override
	public float getLayoutAlignmentX(Container target)
	{
		return 1.0f / 2.0f;
	}

	//--------------------------------------------------------------------------

	@Override
	public float getLayoutAlignmentY(Container target)
	{
		return 1.0f / 2.0f;
	}

	//--------------------------------------------------------------------------

	@Override
	public void invalidateLayout(Container target)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void addLayoutComponent(String name, Component comp)
	{
		// Intentionally emopty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void removeLayoutComponent(Component comp)
	{
		componentToConstraint.remove(comp);
	}

	//--------------------------------------------------------------------------

	@Override
	public Dimension minimumLayoutSize(Container parent)
	{
		return preferredLayoutSize(parent);
	}

	//--------------------------------------------------------------------------

	@Override
	public Dimension maximumLayoutSize(Container parent)
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	//--------------------------------------------------------------------------

	@Override
	public Dimension preferredLayoutSize(Container parent)
	{
		Component[] components = parent.getComponents();
		Insets insets = parent.getInsets();
		int width = 0;
		int height = 0;
		Dimension componentPreferredSize;
		boolean firstVisibleComponent = true;
		
		for(int i = 0, c = components.length ; i < c ; i++)
		{
			if(components[i].isVisible())
			{
				componentPreferredSize = components[i].getPreferredSize();
				if(orientation == HORIZONTAL)
				{
					height = Math.max(height, componentPreferredSize.height);
					width += componentPreferredSize.width;
					if(firstVisibleComponent)
					{
						firstVisibleComponent = false;
					}
					else
					{
						width += gap;
					}
				}
				else
				{
					height += componentPreferredSize.height;
					width = Math.max(width, componentPreferredSize.width);
					if(firstVisibleComponent)
					{
						firstVisibleComponent = false;
					}
					else
					{
						height += gap;
					}
				}
			}
		}
		return new Dimension(
				width + insets.right + insets.left,
				height + insets.top + insets.bottom);
	}

	//--------------------------------------------------------------------------

	@Override
	public void layoutContainer(Container parent)
	{
		Insets insets = parent.getInsets();
		Dimension d = parent.getSize();

		// calculate the available sizes
		d.width = d.width - insets.left - insets.right;
		d.height = d.height - insets.top - insets.bottom;

		// pre-calculate the size of each components
		Component[] components = parent.getComponents();
		int[] sizes = new int[components.length];

		// calculate the available size
		int totalSize = (HORIZONTAL == orientation ? d.width : d.height) - (components.length - 1) * gap;
		int availableSize = totalSize;

		// PENDING(fred): the following code iterates 4 times on the component
		// array, need to find something more efficient!

		// give priority to components who want to use their preferred size or who
		// have a predefined size
		for(int i = 0, c = components.length ; i < c ; i++)
		{
			if(components[i].isVisible())
			{
				Constraint constraint = componentToConstraint.get(components[i]);
				
				if(constraint == null || constraint == PREFERRED_SIZE)
				{
					sizes[i] = (HORIZONTAL == orientation ? components[i].getPreferredSize().width : components[i].getPreferredSize().height);
					availableSize -= sizes[i];
				}
				else if(constraint instanceof NumberConstraint)
				{
					sizes[i] = ((NumberConstraint) constraint).intValue();
					availableSize -= sizes[i];
				}
			}
		}

		// then work with the components who want a percentage of the remaining
		// space
		int remainingSize = availableSize;
		for(int i = 0, c = components.length ; i < c ; i++)
		{
			if(components[i].isVisible())
			{
				Constraint constraint = componentToConstraint.get(components[i]);
				
				if(constraint instanceof PercentConstraint)
				{
					sizes[i] = (int) (remainingSize * ((PercentConstraint) constraint).floatValue());
					availableSize -= sizes[i];
				}
			}
		}

		// finally share the remaining space between the other components    
		ArrayList remaining = new ArrayList();
		for(int i = 0, c = components.length ; i < c ; i++)
		{
			if(components[i].isVisible())
			{
				Constraint constraint = componentToConstraint.get(components[i]);
				
				if(constraint == REMAINING_SPACE)
				{
					remaining.add(new Integer(i));
					sizes[i] = 0;
				}
			}
		}

		if(remaining.size() > 0)
		{
			int rest = availableSize / remaining.size();
			
			for(Iterator iter = remaining.iterator() ; iter.hasNext() ;)
				sizes[((Integer) iter.next()).intValue()] = rest;
		}

		// all calculations are done, apply the sizes
		int currentOffset = (HORIZONTAL == orientation ? insets.left : insets.top);

		for(int i = 0, c = components.length ; i < c ; i++)
		{
			if(components[i].isVisible())
			{
				if(HORIZONTAL == orientation)
					components[i].setBounds(currentOffset, insets.top, sizes[i], d.height);
				else
					components[i].setBounds(insets.left, currentOffset, d.width, sizes[i]);
				
				currentOffset += gap + sizes[i];
			}
		}
	}

	//--------------------------------------------------------------------------
}
