/****************************************/
/* RecentManager.java					*/
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor;

import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import java.util.ArrayList;
import javax.swing.JFrame;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class RecentManager implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private JFrame frame;
	private JMenu menu;
	private ArrayList<JMenuItem> menuItems = new ArrayList<>();
	private ArrayList<File> files = new ArrayList<>();
	
	private int listLength;
	
	private DocumentManager documentManager;

	//--------------------------------------------------------------------------
	
	public RecentManager(JMenu menu, JFrame frame, DocumentManager documentManager, int numberOfItems)
	{
		this.menu = menu;
		this.documentManager = documentManager;
		this.frame = frame;
		
		listLength = numberOfItems;
		
		for(int i = 0 ; i < listLength ; i++)
		{
			String path = Settings.getRecentFile(i);			
			
			if(!path.isEmpty())
				addDocument(new File(path));
			else
				break;
		}
	}
	
	//--------------------------------------------------------------------------
	
	public void save()
	{
		for(int i = 0 ; i < listLength ; i++)
			Settings.removeRecentFile(i);
		
		for(int i = 0 ; i < files.size() ; i++)
			Settings.setRecentFile(i, files.get(i).getAbsolutePath());			
	}
	
	//--------------------------------------------------------------------------
	
	private void addDocument(File file)
	{
		if(file.exists())
		{
			JMenuItem item = getNextMenuItem();

			item.setText(file.getName());

			menuItems.add(item);
			menu.add(item,0);
			files.add(file);
		}
	}
	
	//--------------------------------------------------------------------------
	
	private JMenuItem getNextMenuItem()
	{
		JMenuItem item;
		
		if(menuItems.size() == listLength)
		{
			item = menuItems.get(0);
			
			menuItems.remove(0);
			files.remove(0);
			
			menu.remove(item);
		}
		else
		{
			item = new JMenuItem();
			item.addActionListener(this);
		}
		
		return item;
	}
	
	//--------------------------------------------------------------------------
	
	public void addDocument(ZoneDocument document)
	{
		// Checking if the file is not already on the list.
		for(int i = 0 ; i < files.size() ; i++)
		{
			File file = files.get(i);
			
			if(file.getAbsolutePath().equals(document.getPath()))
			{
				if(i < files.size()-1)
				{
					JMenuItem item = menuItems.get(i);

					menuItems.remove(i);
					files.remove(i);

					menu.remove(item);

					addDocument(file);										
				}
				
				return;
			}
		}
		
		JMenuItem item = getNextMenuItem();

		item.setText(document.getTitle());
		
		menuItems.add(item);
		menu.add(item,0);
		files.add(new File(document.getPath()));	
	}
	
	//--------------------------------------------------------------------------
	
	public void documentUpdated(ZoneDocument document, String oldPath)
	{
		for(int i = 0 ; i < files.size() ; i++)
		{
			if(files.get(i).getAbsolutePath().equals(oldPath))
			{
				removeItem(i);
				addDocument(document);
				return;
			}
		}
		
		throw new NullPointerException("No document " + oldPath + " in recent documents.");
	}
	
	//--------------------------------------------------------------------------
	
	private void removeItem(int index)
	{
		JMenuItem item = menuItems.get(index);
		
		menuItems.remove(index);
		files.remove(index);
		menu.remove(item);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		for(int i = 0 ; i < menuItems.size() ; i++)
		{
			if(event.getSource() == menuItems.get(i))
			{				
				File file = files.get(i);
				
				if(file.exists())
					documentManager.openDocument(file);
				else
				{					
					JOptionPane.showMessageDialog(frame, "File " + file.getName() + " no longer exists!", "File open error!", JOptionPane.ERROR_MESSAGE);					
					removeItem(i);
				}
				
				return;
			}
		}
		
		throw new NullPointerException("Wrong recent item event.");
	}
	
	//--------------------------------------------------------------------------
}
