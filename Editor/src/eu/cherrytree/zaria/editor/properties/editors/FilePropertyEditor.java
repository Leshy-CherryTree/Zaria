/****************************************/
/* FilePropertyEditor.java				*/
/* Created on: 05-Jan-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/


package eu.cherrytree.zaria.editor.properties.editors;

import eu.cherrytree.zaria.editor.document.AssetFileFilter;
import eu.cherrytree.zaria.editor.properties.propertysheet.UIUtils;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public abstract class FilePropertyEditor extends DialogPropertyEditor
{
	//--------------------------------------------------------------------------
	
	private String path = "";
	private String[] allowedPaths;
	
	private JTextField textField;
	
	//--------------------------------------------------------------------------

	public FilePropertyEditor(String ... allowedPaths)
	{
		super(new JTextField());	
		
		textField = (JTextField) getComponent();
		
		textField.setBorder(UIUtils.EMPTY_BORDER);
		textField.addActionListener(this);
		
		this.allowedPaths = allowedPaths.clone();
	}

	//--------------------------------------------------------------------------

	@Override
	public void setValue(Object value)
	{		
		path = (String) value;
		textField.setText(path);
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getValue()
	{
		return path;
	}

	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == button)
		{
			JFileChooser chooser = new JFileChooser();
			AssetFileFilter filter = new AssetFileFilter(getFileTypeString(), getFileTypeExtensions());
			chooser.setFileFilter(filter);
			chooser.setCurrentDirectory(new File(path == null ? allowedPaths[0] : allowedPaths[0] + path));

			int returnVal = chooser.showOpenDialog(getCustomEditor());

			if(returnVal == JFileChooser.APPROVE_OPTION)
			{			
				String old = path;
				String newpath = chooser.getSelectedFile().getPath();

				boolean ok = false;

				for(String assetPath : allowedPaths)
				{
					if(newpath.startsWith(assetPath))
					{									
						path = newpath.substring(assetPath.length());

						textField.setText(path);

						firePropertyChange(old, path);

						ok = true;

						break;
					}				
				}

				if(!ok)
					JOptionPane.showMessageDialog(getCustomEditor(), "Path:\n" + newpath + "\nis not valid!\nPath must be inside the Assets directory.", "Wrong path!", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(event.getSource() == textField)
		{
			String old = path;
			path = textField.getText();
			
			firePropertyChange(old, path);
		}
	}

	//--------------------------------------------------------------------------
	
	public abstract String getFileTypeString();
	public abstract String[] getFileTypeExtensions();
	
	//--------------------------------------------------------------------------
}