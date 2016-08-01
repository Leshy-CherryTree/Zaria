/****************************************/
/* ZoneContainer.java					*/
/* Created on: 01-08-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.gdx.particles.editor;

import eu.cherrytree.gdx.particles.ParticleEffect;
import eu.cherrytree.gdx.particles.ParticleEffectDefinition;
import eu.cherrytree.gdx.particles.ParticleEmitter;
import eu.cherrytree.gdx.particles.ParticleEmitterDefinition;

import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinitionLibrary;
import eu.cherrytree.zaria.texture.TextureArea;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ParticleEffectZoneContainer
{
	//--------------------------------------------------------------------------
	
	private static String path;
	
	private static ZariaObjectDefinitionLibrary library;
	
	private static ParticleEffectDefinition particleEffectDefinition;
	private static ParticleEffect particleEffect;
	
	private static ArrayList<ParticleEmitterDefinition> emitterDefinitions = new ArrayList<>();
	private static ArrayList<ParticleEmitter> emitters = new ArrayList<>();
	
	private static ArrayList<TextureArea> textureAreas = new ArrayList<>();
	
	//--------------------------------------------------------------------------
	
	private static <Def extends ZariaObjectDefinition> Def createNewDefinition(Class<Def> cls, String id)
	{
		try
		{
			Field id_field = ZariaObjectDefinition.class.getField("id");
			id_field.setAccessible(true);
			
			Field uuid_field = ZariaObjectDefinition.class.getField("uuid");
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

	public static void load()
	{
		// Open the load dialog.
	}
	
	//--------------------------------------------------------------------------
	
	public static void save()
	{
		
	}
	
	//--------------------------------------------------------------------------
	
	public static void createNew()
	{
		library = new ZariaObjectDefinitionLibrary();
		
		particleEffectDefinition = createNewDefinition(ParticleEffectDefinition.class, "ParticleEffect");
		
		
		
		emitterDefinitions.clear();
		emitters.clear();
		
		textureAreas.clear();
	}
	
	//--------------------------------------------------------------------------
	
	public static void removeEmitter(int index)
	{
		
	}
	
	//--------------------------------------------------------------------------
	
	public static ParticleEmitter addParticleEmitter()
	{
		
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
	
	public static void setTexture(ParticleEmitter emitter, String path, UUID uuid)
	{
		
	}
	
	//--------------------------------------------------------------------------
}
