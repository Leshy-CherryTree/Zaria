/****************************************/
/* HelpTopic.java						*/
/* Created on: 07-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.help;

import java.net.URL;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class HelpTopic implements Comparable<HelpTopic>
{
	//--------------------------------------------------------------------------
	
	protected String name;

	//--------------------------------------------------------------------------
	
	public abstract URL getUrl();
	public abstract String getContent();
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return name;
	}

	//--------------------------------------------------------------------------

	@Override
	public int compareTo(HelpTopic topic)
	{
		return name.compareTo(topic.name);
	}
	
	//--------------------------------------------------------------------------
}
