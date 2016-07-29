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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;

import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.border.CompoundBorder;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import eu.cherrytree.gdx.particles.ParticleEffect;
import eu.cherrytree.gdx.particles.ParticleEmitter;

import com.jtattoo.plaf.hifi.HiFiLookAndFeel;
import eu.cherrytree.zaria.utilities.Random;
import java.io.IOException;
import javax.imageio.ImageIO;

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

	private ParticleEffect effect = new ParticleEffect();
	private File effectFile;
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
				propertiesPanel.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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
				emittersPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0), BorderFactory
				.createTitledBorder("Effect Emitters")));
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
				rowsPanel.removeAll();
				ParticleEmitter emitter = getEmitter();
				addRow(new ImagePanel(ParticleEditor.this, "Image", ""));
				addRow(new CountPanel(ParticleEditor.this, "Count",
				"Min number of particles at all times, max number of particles allowed."));
				addRow(new RangedNumericPanel(emitter.getDelay(), "Delay",
				"Time from beginning of effect to emission start, in milliseconds."));
				addRow(new RangedNumericPanel(emitter.getDuration(), "Duration", "Time particles will be emitted, in milliseconds."));
				addRow(new ScaledNumericPanel(emitter.getEmission(), "Duration", "Emission",
				"Number of particles emitted per second."));
				addRow(new ScaledNumericPanel(emitter.getLife(), "Duration", "Life", "Time particles will live, in milliseconds."));
				addRow(new ScaledNumericPanel(emitter.getLifeOffset(), "Duration", "Life Offset",
				"Particle starting life consumed, in milliseconds."));
				addRow(new RangedNumericPanel(emitter.getXOffsetValue(), "X Offset",
				"Amount to offset a particle's starting X location, in world units."));
				addRow(new RangedNumericPanel(emitter.getYOffsetValue(), "Y Offset",
				"Amount to offset a particle's starting Y location, in world units."));
				addRow(new SpawnPanel(ParticleEditor.this, emitter.getSpawnShape(), "Spawn", "Shape used to spawn particles."));
				addRow(new ScaledNumericPanel(emitter.getSpawnWidth(), "Duration", "Spawn Width",
				"Width of the spawn shape, in world units."));
				addRow(new ScaledNumericPanel(emitter.getSpawnHeight(), "Duration", "Spawn Height",
				"Height of the spawn shape, in world units."));
				addRow(new ScaledNumericPanel(emitter.getScale(), "Life", "Size", "Particle size, in world units."));
				addRow(new ScaledNumericPanel(emitter.getVelocity(), "Life", "Velocity", "Particle speed, in world units per second."));
				addRow(new ScaledNumericPanel(emitter.getAngle(), "Life", "Angle", "Particle emission angle, in degrees."));
				addRow(new ScaledNumericPanel(emitter.getRotation(), "Life", "Rotation", "Particle rotation, in degrees."));
				addRow(new ScaledNumericPanel(emitter.getWind(), "Life", "Wind", "Wind strength, in world units per second."));
				addRow(new ScaledNumericPanel(emitter.getGravity(), "Life", "Gravity", "Gravity strength, in world units per second."));
				addRow(new GradientPanel(emitter.getTint(), "Tint", "", false));
				addRow(new PercentagePanel(emitter.getTransparency(), "Life", "Transparency", ""));
				addRow(new OptionsPanel(ParticleEditor.this, "Options", ""));
				for (Component component : rowsPanel.getComponents())
					if (component instanceof EditorPanel)
						((EditorPanel) component).update(ParticleEditor.this);
				rowsPanel.repaint();
			}
		});
	}
	
	//--------------------------------------------------------------------------
	
	public void createNewEmitter()
	{
		effectPanel.createNewEmitter("Untitled", true);
	}
	
	//--------------------------------------------------------------------------

	public ParticleEffect getEffect()
	{
		return effect;
	}
	
	//--------------------------------------------------------------------------

	public File getEffectFile()
	{
		return effectFile;
	}
	
	//--------------------------------------------------------------------------

	public Renderer getRenderer()
	{
		return renderer;
	}
	
	//--------------------------------------------------------------------------
	
	public ParticleEmitter getEmitter()
	{
		return effect.getEmitters().get(effectPanel.getEditIndex());
	}
	
	//--------------------------------------------------------------------------

	private void addRow(JPanel row)
	{
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		rowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
		new Insets(0, 0, 0, 0), 0, 0));
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
		
		String imagePath = emitter.getImagePath();
		
		if (data.icon == null && imagePath != null)
		{
			try
			{
				URL url;
				File file = new File(imagePath);
				if (file.exists())
					url = file.toURI().toURL();
				else
				{
					url = ParticleEditor.class.getResource(imagePath);
					if (url == null)
						return null;
				}
				data.icon = new ImageIcon(url);
			}
			catch (MalformedURLException ex)
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
	
	public static void main(String[] args)
	{
		Properties props = new Properties();

		props.put("controlTextFont", "Dialog " + 13);
		props.put("systemTextFont", "Dialog " + 13);
		props.put("userTextFont", "Dialog " + 13);
		props.put("menuTextFont", "Dialog " + 13);
		props.put("windowTitleFont", "Dialog bold " + 13);
		props.put("subTextFont", "Dialog " + 10);

		props.put("logoString", "Zone Editor");

		props.put("backgroundColorLight", "32 32 32");
		props.put("backgroundColor", "32 32 32");
		props.put("backgroundColorDark", "32 32 32");

		props.put("foregroundColor", "192 192 192");
		props.put("inputForegroundColor", "192 192 192");
		props.put("buttonForegroundColor", "192 192 192");
		props.put("controlForegroundColor", "192 192 192");
		props.put("menuForegroundColor", "192 192 192");
		props.put("windowTitleForegroundColor", "192 192 192");

		try
		{
			HiFiLookAndFeel.setTheme(props);
			UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		}
		catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				new ParticleEditor();
			}
		});
	}
	
	//--------------------------------------------------------------------------
}
