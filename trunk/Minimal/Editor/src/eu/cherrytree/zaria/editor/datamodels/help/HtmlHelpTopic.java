/****************************************/
/* HtmlHelpTopic.java					*/
/* Created on: 14-Jul-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.datamodels.help;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class HtmlHelpTopic extends HelpTopic
{
	//--------------------------------------------------------------------------
	
	public URL url;
	
	//--------------------------------------------------------------------------

	public HtmlHelpTopic(String fullPath)
	{
		try
		{
			Document doc = Jsoup.parse(HelpTopic.class.getResourceAsStream(fullPath), "UTF-8", "");
			Element head = doc.select("title").first();
			name = head.ownText();
		}
		catch (IOException ex)
		{
			Logger.getLogger(HelpTopic.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		this.url = HelpTopic.class.getResource(fullPath);
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public URL getUrl()
	{
		return url;
	}
	
	//--------------------------------------------------------------------------
	
	@Override
	public String getContent()
	{
		return null;
	}
	
	//--------------------------------------------------------------------------
}
