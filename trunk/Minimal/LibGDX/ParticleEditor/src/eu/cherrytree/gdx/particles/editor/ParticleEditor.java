/****************************************/
/* ParticleEditor.java					*/
/* Created on: 27-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.border.CompoundBorder;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import java.io.IOException;
import javax.imageio.ImageIO;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

import eu.cherrytree.gdx.particles.ParticleEmitter;
import eu.cherrytree.zaria.utilities.Random;

/**
 * 
 * Branched from Particle Editor of libGDX in gdx-tools.
 */
public class ParticleEditor extends JFrame
{
	//--------------------------------------------------------------------------
	
	private LwjglCanvas lwjglCanvas;
	private Renderer renderer;
	private JPanel rowsPanel;
	private EffectPanel effectPanel;
	private JSplitPane splitPane;
	
	private HashMap<ParticleEmitter, ParticleData> particleData = new HashMap();
	
	//--------------------------------------------------------------------------

	public ParticleEditor()
	{
		super("Zaria Particle Editor for libGDX");
		
		try
		{
			setIconImage(ImageIO.read(getClass().getResourceAsStream("/eu/cherrytree/gdx/particles/editor/res/zone32.png")));
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		Random.init();
		ParticleEffectZoneContainer.createNew();
		
		renderer = new Renderer(this);
		lwjglCanvas = new LwjglCanvas(renderer);
		
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent event)
			{
				System.exit(0);
			}
		});

		splitPane = new JSplitPane();
		splitPane.setDividerSize(4);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		
		{
			JPanel propertiesPanel = new JPanel(new GridBagLayout());
			splitPane.add(propertiesPanel, JSplitPane.RIGHT);
			propertiesPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
			.createTitledBorder("Emitter Properties")));
			{
				JScrollPane scroll = new JScrollPane();
				propertiesPanel.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
				
				{
					rowsPanel = new JPanel(new GridBagLayout());
					scroll.setViewportView(rowsPanel);
					scroll.getVerticalScrollBar().setUnitIncrement(70);
				}
			}
		}
		
		{
			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			leftSplit.setDividerSize(4);
			splitPane.add(leftSplit, JSplitPane.LEFT);
			
			{
				JPanel spacer = new JPanel(new BorderLayout());
				leftSplit.add(spacer, JSplitPane.TOP);
				spacer.add(lwjglCanvas.getCanvas());
				spacer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
			}			
			{
				JPanel emittersPanel = new JPanel(new BorderLayout());
				leftSplit.add(emittersPanel, JSplitPane.BOTTOM);
				emittersPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0), BorderFactory.createTitledBorder("Effect Emitters")));
				
				{
					effectPanel = new EffectPanel(this);
					emittersPanel.add(effectPanel);
				}
			}
			leftSplit.setDividerLocation(355);
		}
		
		splitPane.setDividerLocation(325);

		setSize(1150, 700);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	//--------------------------------------------------------------------------

	public void reloadRows()
	{
		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					rowsPanel.removeAll();
					ParticleEmitter emitter = getEmitter();
					
					addRow(new ImagePanel(ParticleEditor.this, "Image", ""));
					addRow(new CountPanel(ParticleEditor.this, "Count", "Min number of particles at all times, max number of particles allowed."));
					addRow(new RangedNumericPanel(emitter.getDefinition().getDelayValue(), "Delay", "Time from beginning of effect to emission start, in milliseconds."));
					addRow(new RangedNumericPanel(emitter.getDefinition().getDurationValue(), "Duration", "Time particles will be emitted, in milliseconds."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getEmissionValue(), "Duration", "Emission", "Number of particles emitted per second."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getLifeValue(), "Duration", "Life", "Time particles will live, in milliseconds."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getLifeOffsetValue(), "Duration", "Life Offset", "Particle starting life consumed, in milliseconds."));
					addRow(new RangedNumericPanel(emitter.getDefinition().getXOffsetValue(), "X Offset", "Amount to offset a particle's starting X location, in world units."));
					addRow(new RangedNumericPanel(emitter.getDefinition().getYOffsetValue(), "Y Offset", "Amount to offset a particle's starting Y location, in world units."));
					addRow(new SpawnPanel(ParticleEditor.this, emitter.getDefinition().getSpawnShapeValue(), "Spawn", "Shape used to spawn particles."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getSpawnWidthValue(), "Duration", "Spawn Width", "Width of the spawn shape, in world units."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getSpawnHeightValue(), "Duration", "Spawn Height", "Height of the spawn shape, in world units."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getScaleValue(), "Life", "Size", "Particle size, in world units."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getVelocityValue(), "Life", "Velocity", "Particle speed, in world units per second."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getAngleValue(), "Life", "Angle", "Particle emission angle, in degrees."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getRotationValue(), "Life", "Rotation", "Particle rotation, in degrees."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getWindValue(), "Life", "Wind", "Wind strength, in world units per second."));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getGravityValue(), "Life", "Gravity", "Gravity strength, in world units per second."));
					addRow(new GradientPanel(emitter.getDefinition().getTintValue(), "Tint", "", false));
					addRow(new ScaledNumericPanel(emitter.getDefinition().getTransparencyValue(), "Life", "Transparency", "Transparency of particles"));
					addRow(new OptionsPanel(ParticleEditor.this, "Options", ""));
					
					for (Component component : rowsPanel.getComponents())
					{
						if (component instanceof EditorPanel)
							((EditorPanel) component).update(ParticleEditor.this);
					}
					
					rowsPanel.repaint();
				}
				catch (SecurityException | NoSuchFieldException ex)
				{
					ex.printStackTrace();
				}
			}
		});
	}
	
	//--------------------------------------------------------------------------

	public Renderer getRenderer()
	{
		return renderer;
	}
	
	//--------------------------------------------------------------------------
	
	public ParticleEmitter getEmitter()
	{
		return ParticleEffectZoneContainer.getEmitter(effectPanel.getEditIndex());
	}
	
	//--------------------------------------------------------------------------

	private void addRow(JPanel row)
	{
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		rowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
	}

	//--------------------------------------------------------------------------
	
	public void setVisible(String name, boolean visible)
	{
		for (Component component : rowsPanel.getComponents())
		{
			if (component instanceof EditorPanel && ((EditorPanel) component).getName().equals(name))
				component.setVisible(visible);
		}
	}
	
	//--------------------------------------------------------------------------

	public ImageIcon getIcon(ParticleEmitter emitter)
	{
		ParticleData data = particleData.get(emitter);
		
		if (data == null)
			particleData.put(emitter, data = new ParticleData());
		
		if (emitter.getDefinition().getTexture() == null)
			return null;
		
		String imagePath = ParticleEffectZoneContainer.getAssetPath() + emitter.getDefinition().getTexture().getTexture();
		
		if (data.icon == null && imagePath != null)
		{
			try
			{
				BufferedImage image = ImageIO.read(new File(imagePath));
				BufferedImage icon = new BufferedImage(emitter.getDefinition().getTexture().getW()+2, emitter.getDefinition().getTexture().getH()+2, image.getType());
				
				Graphics2D g = icon.createGraphics();
				
				g.drawImage(image, -emitter.getDefinition().getTexture().getX()+1, -emitter.getDefinition().getTexture().getY()+1, null);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, icon.getWidth()-1, icon.getHeight()-1);
				
				data.icon = new ImageIcon(icon);
				
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		return data.icon;
	}
	
	//--------------------------------------------------------------------------

	public void setIcon(ParticleEmitter emitters, ImageIcon icon)
	{
		ParticleData data = particleData.get(emitters);
		
		if (data == null)
			particleData.put(emitters, data = new ParticleData());
		
		data.icon = icon;
	}
	
	//--------------------------------------------------------------------------

	public void setEnabled(ParticleEmitter emitter, boolean enabled)
	{
		ParticleData data = particleData.get(emitter);
		
		if (data == null)
			particleData.put(emitter, data = new ParticleData());
		
		data.enabled = enabled;
		emitter.reset();
	}
	
	//--------------------------------------------------------------------------

	public boolean isEnabled(ParticleEmitter emitter)
	{
		ParticleData data = particleData.get(emitter);
		
		if (data == null)
			return true;
		
		return data.enabled;
	}

	//--------------------------------------------------------------------------
}
