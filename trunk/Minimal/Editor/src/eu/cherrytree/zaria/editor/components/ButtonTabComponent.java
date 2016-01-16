/****************************************/
/* ButtonTabComponent.java              */
/* Created on: 04-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.components;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.ZoneDocument;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class ButtonTabComponent extends JPanel implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private JTabbedPane pane;
	private DocumentManager documentManager;
	private ZoneDocument document;
	
	//--------------------------------------------------------------------------

	public ButtonTabComponent(final JTabbedPane pane, ZoneDocument document, DocumentManager documentManager)
	{
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		this.pane = pane;
		this.documentManager = documentManager;
		this.document = document;
		
		setOpaque(false);

		JLabel icon = new JLabel()
		{
			@Override
			public Icon getIcon()
			{
				switch(ButtonTabComponent.this.document.getDocumentType().getLocationType())
				{
					case ASSETS:
						if(ButtonTabComponent.this.document.isSaved())
							return EditorApplication.icons[0];
						else
							return EditorApplication.icons[1];
						
					case SCRIPTS:
						if(ButtonTabComponent.this.document.isSaved())
							return EditorApplication.icons[4];
						else
							return EditorApplication.icons[5];
						
					default:
						return EditorApplication.icons[3];
				}
			}	
			
			@Override
			public String getText()
			{				
				return " ";
			}
		};
		
		JLabel label = new JLabel()
		{						
			@Override
			public String getText()
			{				
				return ButtonTabComponent.this.document.getTitle();
			}															
		};
		
		add(icon);
		add(label);
		
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		
		JButton button = new TabCloseButton(pane);
		add(button);

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
		
		button.addActionListener(this);
	}
	
	//--------------------------------------------------------------------------			

	@Override
	public void actionPerformed(ActionEvent e)
	{
		documentManager.closeDocument(pane.indexOfTabComponent(this));
	}

	//--------------------------------------------------------------------------
}
