/****************************************/
/* Capsule.java							*/
/* Created on: 18-Mar-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.serialization;

import eu.cherrytree.zaria.base.ApplicationInstance;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public final class Capsule implements SaveCapsule, LoadCapsule
{
	//--------------------------------------------------------------------------
	
	private HashMap<String, Byte> bytes = new HashMap<>();
	private HashMap<String, Boolean> booleans = new HashMap<>();
	private HashMap<String, Short> shorts = new HashMap<>();	
	private HashMap<String, Integer> integers = new HashMap<>();	
	private HashMap<String, Long> longs = new HashMap<>();
	private HashMap<String, Float> floats = new HashMap<>();
	private HashMap<String, Double> doubles = new HashMap<>();	
	private HashMap<String, Character> characters = new HashMap<>();	
	private HashMap<String, String> strings = new HashMap<>();
	private HashMap<String, UUID> uuids = new HashMap<>();
	private HashMap<String, Serializable> objects = new HashMap<>();
	private HashMap<String, Capsule> capsules = new HashMap<>();
	
	//--------------------------------------------------------------------------
	
	public static LoadCapsule loadCapsule(String path) throws FileNotFoundException, IOException, ClassNotFoundException
	{		
		path = ApplicationInstance.getSavePath() + path;
		
		try (FileInputStream filein = new FileInputStream(path) ; ObjectInputStream in = new ObjectInputStream(filein))
		{
			return (LoadCapsule) in.readObject();
		}
	}
	
	//--------------------------------------------------------------------------
	
	public static void saveCapsule(String path, SaveCapsule capsule) throws FileNotFoundException, IOException
	{
		path = ApplicationInstance.getSavePath() + path;
		
		File saveFile = new File(path);
		
		if(!saveFile.exists())
		{						
			File dirs = new File(path.substring(0, path.indexOf(Paths.get(path).getFileName().toString())));
			dirs.mkdirs();
			
			saveFile.createNewFile();
		}
		
		try (FileOutputStream fileout = new FileOutputStream(path) ; ObjectOutputStream out = new ObjectOutputStream(fileout))
		{
			out.writeObject(capsule);
		}	
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void write(String name, UUID value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(uuids.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		uuids.put(name, value);	
	}
	
	//--------------------------------------------------------------------------
	

	@Override
	public void write(String name, byte value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(bytes.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		bytes.put(name, value);				
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void write(String name, boolean value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(booleans.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		booleans.put(name, value);
	}

	//--------------------------------------------------------------------------

	@Override
	public void write(String name, short value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(shorts.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		shorts.put(name, value);	
	}

	//--------------------------------------------------------------------------

	@Override
	public void write(String name, int value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(integers.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		integers.put(name, value);
	}

	//--------------------------------------------------------------------------

	@Override
	public void write(String name, long value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(longs.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		longs.put(name, value);
	}

	//--------------------------------------------------------------------------

	@Override
	public void write(String name, float value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(floats.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		floats.put(name, value);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void write(String name, double value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(doubles.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		doubles.put(name, value);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void write(String name, char value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(characters.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		characters.put(name, value);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void write(String name, String value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		
		if(strings.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		strings.put(name, value);
	}

	//--------------------------------------------------------------------------

	@Override
	public void writeObject(String name, Serializable value) throws ValueAlreadySetException
	{
		assert name != null && !name.isEmpty();
		assert !(value instanceof Capsule) : "To save capsules use the writeCapsule method.";
		
		if(objects.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		objects.put(name, value);
	}

	//--------------------------------------------------------------------------
	
	@Override
	public void writeCapsule(String name, SaveCapsule value) throws ValueAlreadySetException
	{
		assert value != this;
		
		if(capsules.containsKey(name))
			throw new ValueAlreadySetException(name);
		
		capsules.put(name, (Capsule) value);
	}
	
	//--------------------------------------------------------------------------		
	
	@Override
	public SaveCapsule createCapsule(String name) throws ValueAlreadySetException
	{
		Capsule capsule = new Capsule();
		writeCapsule(name, capsule);
		return capsule;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public UUID readUUID(String name, UUID defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(uuids.containsKey(name))
			return uuids.get(name);
		else
			return defaultValue;
	}
	
	//--------------------------------------------------------------------------	
	@Override
	public byte readByte(String name, byte defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(bytes.containsKey(name))
			return bytes.get(name);
		else
			return defaultValue;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean readBoolean(String name, boolean defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(booleans.containsKey(name))					
			return booleans.get(name);
		else
			return defaultValue;
	}

	//--------------------------------------------------------------------------

	@Override
	public short readShort(String name, short defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(shorts.containsKey(name))					
			return shorts.get(name);
		else
			return defaultValue;	
	}

	//--------------------------------------------------------------------------

	@Override
	public int readInt(String name, int defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(integers.containsKey(name))					
			return integers.get(name);
		else
			return defaultValue;
	}

	//--------------------------------------------------------------------------

	@Override
	public long readLong(String name, long defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(longs.containsKey(name))					
			return longs.get(name);
		else
			return defaultValue;
	}

	//--------------------------------------------------------------------------

	@Override
	public float readFloat(String name, float defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(floats.containsKey(name))					
			return floats.get(name);
		else
			return defaultValue;	
	}

	//--------------------------------------------------------------------------

	@Override
	public double readDouble(String name, double defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(doubles.containsKey(name))					
			return doubles.get(name);
		else
			return defaultValue;
	}

	//--------------------------------------------------------------------------
	
	@Override
	public char readChar(String name, char defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(characters.containsKey(name))					
			return characters.get(name);
		else
			return defaultValue;	
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String readString(String name, String defaultValue)
	{
		assert name != null && !name.isEmpty();
		
		if(strings.containsKey(name))					
			return strings.get(name);
		else
			return defaultValue;	
	}

	//--------------------------------------------------------------------------

	@Override
	public Serializable readObject(String name)
	{
		assert name != null && !name.isEmpty();
		
		return objects.get(name);	
	}

	//--------------------------------------------------------------------------
	
	@Override
	public LoadCapsule readCapsule(String name)
	{
		assert name != null && !name.isEmpty();
		
		if(capsules.containsKey(name))
			return capsules.get(name);
		else
			return new Capsule();
	}
	
	//--------------------------------------------------------------------------
		
	@Override
	public boolean isEmpty()
	{
		return	bytes.isEmpty() && 
				booleans.isEmpty() && 
				shorts.isEmpty() && 
				integers.isEmpty() && 
				longs.isEmpty() && 
				floats.isEmpty() && 
				doubles.isEmpty() && 
				strings.isEmpty() && 
				objects.isEmpty() &&
				capsules.isEmpty();
	}
	
	//--------------------------------------------------------------------------
	
	private String getString(String indent)
	{
		StringBuilder builder = new StringBuilder();
		
		if(!bytes.isEmpty())
		{
			builder.append(indent).append("Bytes:").append(System.lineSeparator());
		
			for(Map.Entry<String,Byte> b : bytes.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!booleans.isEmpty())
		{
			builder.append(indent).append("Booleans:").append(System.lineSeparator());
		
			for(Map.Entry<String,Boolean> b : booleans.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!shorts.isEmpty())
		{
			builder.append(indent).append("Shorts:").append(System.lineSeparator());
		
			for(Map.Entry<String,Short> b : shorts.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
	
		if(!integers.isEmpty())
		{		
			builder.append(indent).append("Integers:").append(System.lineSeparator());
		
			for(Map.Entry<String,Integer> b : integers.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!longs.isEmpty())
		{
			builder.append(indent).append("Longs:").append(System.lineSeparator());
		
			for(Map.Entry<String,Long> b : longs.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!floats.isEmpty())
		{
			builder.append(indent).append("Floats:").append(System.lineSeparator());
		
			for(Map.Entry<String,Float> b : floats.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}

		if(!doubles.isEmpty())
		{
			builder.append(indent).append("Double:").append(System.lineSeparator());
		
			for(Map.Entry<String,Double> b : doubles.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!strings.isEmpty())
		{
			builder.append(indent).append("Strings:").append(System.lineSeparator());
		
			for(Map.Entry<String,String> b : strings.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!objects.isEmpty())
		{
			builder.append(indent).append("Objects:").append(System.lineSeparator());
		
			for(Map.Entry<String,Serializable> b : objects.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(": ").append(b.getValue()).append(System.lineSeparator());
		}
		
		if(!capsules.isEmpty())
		{
			builder.append(indent).append("Capsules:").append(System.lineSeparator());
		
			for(Map.Entry<String,Capsule> b : capsules.entrySet())
				builder.append(indent).append("\t").append(b.getKey()).append(":").append(System.lineSeparator()).append(b.getValue().getString(indent+"\t\t")).append(System.lineSeparator());
		}
						
		return builder.append(indent).toString();	
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String toString()
	{
		return getString("");
	}		
	
	//--------------------------------------------------------------------------
}
