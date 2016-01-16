/****************************************/
/* TextManager.java						*/
/* Created on: Feb 2, 2012				*/
/* Copyright Cherry Tree Studio 2012	*/
/* Released under EUPL v1.1				*/
/* Ported from the jPAJ project.		*/
/****************************************/

package eu.cherrytree.zaria.utilities;

import eu.cherrytree.zaria.debug.DebugManager;
import java.io.IOException;
import java.io.ObjectInputStream;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 *
 * @param <TextIndex>
 * @param <LangIndex>
 */
public class TextManager <TextIndex extends Enum<TextIndex>, LangIndex extends Enum<LangIndex>>
{
    //--------------------------------------------------------------------------

    private String [][] textArray;

    private LangIndex currentLang;

    private boolean loaded = false;

	//--------------------------------------------------------------------------

	public TextManager(String textfile, LangIndex defaultLang)
	{
		if(textfile.charAt(0) != '/')
			textfile = "/" + textfile;

		loaded = loadTexts(textfile);
		currentLang = defaultLang;
	}

	//--------------------------------------------------------------------------

	public boolean isLoaded()
	{
		return loaded;
	}

	//--------------------------------------------------------------------------

	public void setLanguage(LangIndex lang)
	{
		currentLang = lang;
	}

	//--------------------------------------------------------------------------

	public LangIndex getLanguage()
	{
		return currentLang;
	}

	//--------------------------------------------------------------------------

	public String getText(TextIndex text)
	{
		if(currentLang == null || !loaded)
			return "TEXT ERROR";
		else
			return textArray[currentLang.ordinal()][text.ordinal()];
	}

	//--------------------------------------------------------------------------

	private boolean loadTexts(String textfile)
	{
		try
		{
			ObjectInputStream in = new ObjectInputStream(TextManager.class.getResourceAsStream(textfile));
			textArray = (String[][]) in.readObject();
			in.close();
			return true;
		}
		catch(IOException | ClassNotFoundException e)
		{
			DebugManager.trace(e);
			return false;
		}
	}

	//--------------------------------------------------------------------------
}
