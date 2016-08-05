/****************************************/
/* ZoneContainer.java					*/
/* Created on: 01-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;


import eu.cherrytree.gdx.particles.ParticleEffect;
import eu.cherrytree.gdx.particles.ParticleEffectDefinition;
import eu.cherrytree.gdx.particles.ParticleEmitter;
import eu.cherrytree.gdx.particles.ParticleEmitterDefinition;
import eu.cherrytree.gdx.particles.editor.assets.AssetResolver;
import eu.cherrytree.gdx.particles.editor.datamodel.ZoneFileFilter;
import eu.cherrytree.zaria.serialization.Link;
import eu.cherrytree.zaria.serialization.LinkArray;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;
import eu.cherrytree.zaria.texture.TextureArea;
import java.io.BufferedWriter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.UUID;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ParticleEffectZoneContainer
{
	//--------------------------------------------------------------------------
	
	private static AssetManager assetManager;
	private static String path;
	private static String assetPath;
	
	private static ZariaObjectDefinitionLibrary library;
	
	private static ParticleEffectDefinition particleEffectDefinition;
	private static ParticleEffect particleEffect;
	
	private static ArrayList<ParticleEmitterDefinition> emitterDefinitions = new ArrayList<>();
	private static ArrayList<ParticleEmitter> emitters = new ArrayList<>();
	private static ArrayList<TextureArea> textureAreas = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	public static void init(String assetPath)
	{
		ParticleEffectZoneContainer.assetPath = assetPath;
		assetManager = new AssetManager(new AssetResolver(assetPath));
	}
	
	//--------------------------------------------------------------------------

	public static String getAssetPath()
	{
		return assetPath;
	}
	
	//--------------------------------------------------------------------------

	public static AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	//--------------------------------------------------------------------------
	
	private static <Def extends ZariaObjectDefinition> Def createNewDefinition(Class<Def> cls, String id)
	{
		try
		{
			Field id_field = ZariaObjectDefinition.class.getDeclaredField("id");
			id_field.setAccessible(true);
			
			Field uuid_field = ZariaObjectDefinition.class.getDeclaredField("uuid");
			uuid_field.setAccessible(true);
			
			Def obj = cls.getConstructor().newInstance();
			id_field.set(obj, id);
			uuid_field.set(obj, UUID.randomUUID());
			
			return obj;
		}
		catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException ex)
		{
			ex.printStackTrace();
		}
			
		return null;
	}
	
	//--------------------------------------------------------------------------
				
	private static String setIndentation(String text)
	{
		String[] split = text.split("\"");
		text = "";
		
		for(int i = 0 ; i < split.length ; i++)
		{
			if(i != 0)
				text += "\"";
			
			if(i % 2 == 0)
			{
				text +=	split[i].replace(",", ",\n")
								.replace(":", " : ")
								.replace("{", "\n{\n")
								.replace("}", "\n}\n")
								.replace("[", "\n[\n")
								.replace("]", "\n]\n")
								.replace("\n]\n,\n", "\n],\n")
								.replace("\n}\n,\n", "\n},\n");
			}
			else
				text += split[i];
		}
		
		String indent = "";		
		String ret = "";
		boolean eol = false;
		
		for(int i = 0 ; i < text.length() ; i++)
		{						
			char c = text.charAt(i);
			
			if(c == '{' || c == '[')
				indent += "\t";
			else if(c == '}'  || c == ']')
				indent = indent.substring(0, indent.length()-1);
			
			if(eol)
			{			
				if(!indent.isEmpty())
				{
					if(c == '{' || c == '[')
						ret += indent.substring(0, indent.length()-1);
					else
						ret += indent;
				}
				eol = false;
			}
			
			if(c == '\n')
				eol = true;
			
			ret += c;
		}

		return ret.trim() + "\n";
	}
	
	//--------------------------------------------------------------------------
	
	public static String getNewHeader(String name)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.UK);
		
		String text = "// " + name + "\n";
		
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(System.currentTimeMillis());
						
		text += "// Generated on: " + dateFormat.format(cal.getTime()) + "\n";
		text += "\n";
		
		return text;
	}
	
	//--------------------------------------------------------------------------
	
	public static void save(JFrame editor)
	{
		File file;
		
		if (path == null)
		{
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setCurrentDirectory(new File(assetPath));
			fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());

			fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileFilter()
			{
				@Override
				public boolean accept(File f)
				{
					return f.isDirectory() || f.getAbsolutePath().toLowerCase().endsWith(".zone");
				}

				@Override
				public String getDescription()
				{
					return "(*.zone) zone file";
				}
			});

			if (fileChooser.showSaveDialog(editor) == JFileChooser.APPROVE_OPTION)
			{
				path = fileChooser.getSelectedFile().getAbsolutePath();
				
				if (!path.toLowerCase().endsWith(".zone"))
					path += ".zone";
			}
			else
			{
				return;
			}
		}

		file = new File(path);
		
		assert file != null;
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
		{
			ZariaObjectDefinition[] definitions = new ZariaObjectDefinition[emitterDefinitions.size() + 1];
			
			for (int i = 0 ; i < emitterDefinitions.size() ; i++)
				definitions[i] = emitterDefinitions.get(i);
			
			definitions[definitions.length - 1] = particleEffectDefinition;
			
			String text = EditorApplication.getSerializer().writeValueAsString(definitions);
			writer.write(getNewHeader(file.getName()) + setIndentation(text));
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void createNew()
	{
		try
		{
			path = null;
			
			// Create a brand new library to hold our definitions.
			library = new ZariaObjectDefinitionLibrary();
			
			// Clear arrays.
			emitterDefinitions.clear();
			emitters.clear();
			
			textureAreas.clear();
			
			ArrayList<ZariaObjectDefinition> new_defs = new ArrayList<>();
			
			// Create a new particle effect definition with default data.
			particleEffectDefinition = createNewDefinition(ParticleEffectDefinition.class, "ParticleEffect");
			new_defs.add(particleEffectDefinition);
			
			ParticleEmitterDefinition emitter_def = createNewDefinition(ParticleEmitterDefinition.class, "Emitter");
			emitterDefinitions.add(emitter_def);
			new_defs.add(emitter_def);
			
			addEmitterToEffect(emitter_def);
			
			library.addDefinitions(new_defs);
			
			particleEffect = particleEffectDefinition.create();
			emitters.addAll(particleEffect.getEmitters());
		}
		catch (ZariaObjectDefinitionLibrary.CantAddDefinitionException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex)
		{
			ex.printStackTrace();
		}
	}
	
	//--------------------------------------------------------------------------
	
	private static void findTextures(File file, FileFilter filter, ArrayList<TextureArea> textures, HashSet<UUID> uuids)
	{
		File[] files = file.listFiles(filter);
		
		for (File f : files)
		{
			// We're done.
			if (textures.size() == uuids.size())
				return;
					
			if (f.isDirectory())
			{
				findTextures(f, filter, textures, uuids);
			}
			else
			{
				try
				{
					ZariaObjectDefinition[] definitions = EditorApplication.getSerializer().readValue(new FileInputStream(f), ZariaObjectDefinition[].class);
					
					for (ZariaObjectDefinition def : definitions)
					{
						if (uuids.contains(def.getUUID()))
							textures.add((TextureArea) def);
					}
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void openEffect(JFrame editor)
	{
		try
		{
			GetDefinitionDialog<ParticleEffectDefinition> effect_dialog = new GetDefinitionDialog<>(editor, ParticleEffectDefinition.class);
			effect_dialog.setVisible(true);

			ParticleEffectDefinition effect_def = effect_dialog.getDefinition();
			
			if (effect_def == null)
			{
				// Throw some kind of an error here.
				return;
			}
			
			File file = effect_dialog.getFile();
			path = file.getAbsolutePath();
			
			assetManager.clear();
			
			// Create a brand new library to hold our definitions.
			library = new ZariaObjectDefinitionLibrary();
			
			// Clear arrays.
			emitterDefinitions.clear();
			emitters.clear();
			
			textureAreas.clear();
			
			System.gc();
			
			ArrayList<ZariaObjectDefinition> new_defs = new ArrayList<>();
			ArrayList<ParticleEmitterDefinition> emitter_defs = new ArrayList<>();
			
			ZariaObjectDefinition[] definitions = EditorApplication.getSerializer().readValue(new FileInputStream(file), ZariaObjectDefinition[].class);
			
			for (ZariaObjectDefinition def : definitions)
			{
				if (ParticleEffectDefinition.class.isAssignableFrom(def.getClass()))
					particleEffectDefinition = (ParticleEffectDefinition) def;
				else if (ParticleEmitterDefinition.class.isAssignableFrom(def.getClass()))
					emitter_defs.add((ParticleEmitterDefinition) def);
			}

			new_defs.add(particleEffectDefinition);
			
			HashSet<UUID> texture_uuids = new HashSet<>();
			
			for (ParticleEmitterDefinition emitter_def : emitter_defs)
			{
				emitterDefinitions.add(emitter_def);
				new_defs.add(emitter_def);
				
				Field tex_field = ParticleEmitterDefinition.class.getDeclaredField("texture");
				tex_field.setAccessible(true);
				
				Link<TextureArea> tex_link = (Link<TextureArea>) tex_field.get(emitter_def);
				
				if (tex_link.getUUID() != null)
					texture_uuids.add(tex_link.getUUID());
			}
			
			ArrayList<TextureArea> texture_areas = new ArrayList<>();
			findTextures(new File(assetPath), new ZoneFileFilter(), texture_areas, texture_uuids);
			
			new_defs.addAll(texture_areas);
			
			library.addDefinitions(new_defs);
			
			particleEffect = particleEffectDefinition.create();
			emitters.addAll(particleEffect.getEmitters());

			for (TextureArea area : texture_areas)
				assetManager.load(area.getTexture(), Texture.class);
			
			assetManager.finishLoading();
			
			particleEffect.loadAssets(assetManager);
		}
		catch (ZariaObjectDefinitionLibrary.CantAddDefinitionException | SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | IOException ex)
		{
			ex.printStackTrace();
		}	
	}
	
	//--------------------------------------------------------------------------
	
	private static void addEmitterToEffect(ParticleEmitterDefinition emitterDefinition) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
	{
		// Creating new array of uuids.
		UUID[] uuids = new UUID[particleEffectDefinition.getEmitters().size() + 1];
		
		for (int i = 0 ; i < particleEffectDefinition.getEmitters().size() ; i++)
			uuids[i] = particleEffectDefinition.getEmitters().getUUID(i);
		
		uuids[uuids.length-1] = emitterDefinition.getUUID();
		
		// Creating new link array with those uuids.
		LinkArray link_array = new LinkArray();
		Field id_field = LinkArray.class.getDeclaredField("uuids");
		id_field.setAccessible(true);
		id_field.set(link_array, uuids);
		
		// Overwriting emitter link array.
		Field emitters_field = ParticleEffectDefinition.class.getDeclaredField("emitters");
		emitters_field.setAccessible(true);
		
		emitters_field.set(particleEffectDefinition, link_array);
	}
	
	//--------------------------------------------------------------------------
	
	public static void removeEmitter(int index)
	{
		
	}
	
	//--------------------------------------------------------------------------
	
	public static ParticleEmitter addParticleEmitter()
	{
		try
		{
			ArrayList<ZariaObjectDefinition> new_defs = new ArrayList<>();
			
			ParticleEmitterDefinition emitter_def = createNewDefinition(ParticleEmitterDefinition.class, "Emitter");
			new_defs.add(emitter_def);
			
			addEmitterToEffect(emitter_def);
			
			library.addDefinitions(new_defs);
			
			particleEffectDefinition.preLoad(library);
			particleEffect = particleEffectDefinition.create();
			particleEffect.loadAssets(assetManager);
			
			emitterDefinitions.add(emitter_def);
			
			ParticleEmitter emitter = particleEffect.getEmitters().get(particleEffect.getEmitters().size()-1);
			
			emitters.add(emitter);
			
			return emitter;
		}
		catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException | ZariaObjectDefinitionLibrary.CantAddDefinitionException ex)
		{
			ex.printStackTrace();
		}
		
		return null;
	}
	
	//--------------------------------------------------------------------------

	public static ParticleEffect getEffect()
	{
		return particleEffect;
	}
	
	//--------------------------------------------------------------------------
	
	
	public static ParticleEmitter getEmitter(int index)
	{
		return emitters.get(index);
	}
	
	//--------------------------------------------------------------------------
	
	public static boolean setTexture(ParticleEmitter emitter, TextureArea texture)
	{
		assert texture != null;

		try
		{
			library.addDefinition(texture);
		}
		catch (ZariaObjectDefinitionLibrary.CantAddDefinitionException ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		// Imediately load the texture.
		assetManager.load(texture.getTexture(), Texture.class);
		assetManager.finishLoading();

		try
		{
			Field textures_field = ParticleEmitterDefinition.class.getDeclaredField("texture");	
			textures_field.setAccessible(true);

			Link link = new Link();
			Field id_field = Link.class.getDeclaredField("uuid");
			id_field.setAccessible(true);
			id_field.set(link, texture.getUUID());

			textures_field.set(emitter.getDefinition(), link);

		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex)
		{
			ex.printStackTrace();
			return false;
		}
		
		emitter.getDefinition().preLoad(library);

		ParticleEffectZoneContainer.getEffect().loadAssets(ParticleEffectZoneContainer.getAssetManager());
		
		return true;
	}
	
	//--------------------------------------------------------------------------
}
