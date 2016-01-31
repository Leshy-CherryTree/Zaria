/****************************************/
/* TextureAtlasDialog.java 				*/
/* Created on: 31-Jan-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.dialogs.textureatlas.AtlasGenerator;
import eu.cherrytree.zaria.editor.document.AssetFileFilter;
import static eu.cherrytree.zaria.editor.document.DocumentManager.showSaveDialog;
import java.awt.Graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javax.swing.AbstractListModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class TextureAtlasDialog extends JDialog implements ActionListener, TreeSelectionListener, ListSelectionListener
{	
	//--------------------------------------------------------------------------

	private JButton addButton = new JButton();
	private JButton cancelButton = new JButton();
	private JTree fileView = new JTree();
	private JPanel fileViewPanel = new JPanel();
	private JScrollPane fileViewScrollPane = new JScrollPane();
	private JList<String> imageList = new JList<>();
	private JPanel imageListPanel = new JPanel();
	private JScrollPane imageListScrollPane = new JScrollPane();
	private ImagePanel previewPanel = new ImagePanel();
	private JButton okButton = new JButton();
	private JButton removeButton = new JButton();
	
	private AtlasGenerator generator = new AtlasGenerator();
	private FileListModel fileListModel = new FileListModel();
	private DirectoryTreeModel directoryTreeModel = new DirectoryTreeModel();
	
	//--------------------------------------------------------------------------

	public TextureAtlasDialog(JFrame parent, boolean modal)
	{
		super(parent, modal);

		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		setResizable(false);

		fileViewScrollPane.setViewportView(fileView);
		fileView.setModel(directoryTreeModel);

		fileView.addTreeSelectionListener(this);

		addButton.setText("Add");

		cancelButton.setText("Cancel");

		GroupLayout fileViewPanelLayout = new GroupLayout(fileViewPanel);
		fileViewPanel.setLayout(fileViewPanelLayout);
		fileViewPanelLayout.setHorizontalGroup(
			fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(fileViewPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(fileViewScrollPane)
					.addGroup(fileViewPanelLayout.createSequentialGroup()
						.addComponent(cancelButton)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 354, Short.MAX_VALUE)
						.addComponent(addButton))))
		);
		fileViewPanelLayout.setVerticalGroup(
			fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(fileViewPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addGap(1, 1, 1)
				.addComponent(fileViewScrollPane, GroupLayout.DEFAULT_SIZE, 871, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(fileViewPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(addButton)
					.addComponent(cancelButton))
				.addContainerGap())
		);

		imageList.setModel(fileListModel);
		imageList.addListSelectionListener(this);
		
		imageListScrollPane.setViewportView(imageList);

		removeButton.setText("Remove");

		okButton.setText("Ok");

		GroupLayout imageListPanelLayout = new GroupLayout(imageListPanel);
		imageListPanel.setLayout(imageListPanelLayout);
		imageListPanelLayout.setHorizontalGroup(
			imageListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, imageListPanelLayout.createSequentialGroup()
				.addGroup(imageListPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
					.addComponent(imageListScrollPane)
					.addGroup(imageListPanelLayout.createSequentialGroup()
						.addComponent(removeButton)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 332, Short.MAX_VALUE)
						.addComponent(okButton)))
				.addContainerGap())
		);
		imageListPanelLayout.setVerticalGroup(
			imageListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(imageListPanelLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(imageListScrollPane, GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(imageListPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
					.addComponent(removeButton)
					.addComponent(okButton))
				.addContainerGap())
		);

		GroupLayout previewPanelLayout = new GroupLayout(previewPanel);
		previewPanel.setLayout(previewPanelLayout);
		previewPanelLayout.setHorizontalGroup(
			previewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 0, Short.MAX_VALUE)
		);
		previewPanelLayout.setVerticalGroup(
			previewPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGap(0, 0, Short.MAX_VALUE)
		);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addComponent(fileViewPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
					.addComponent(imageListPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(layout.createSequentialGroup()
						.addComponent(previewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addContainerGap())))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(fileViewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(previewPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(imageListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
		);

		addButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okButton.addActionListener(this);
		removeButton.addActionListener(this);
		
		
		pack();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void setVisible(boolean bln)
	{
		fileView.clearSelection();
		fileListModel.clear();
			
		super.setVisible(bln);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == addButton)
		{						
			for (TreePath path : fileView.getSelectionPaths())
			{
				File file = ((FileWrapper) path.getLastPathComponent()).getFile();
				
				if (file.isFile())
					fileListModel.add(file);
			}						
			
			fileView.clearSelection();
			imageList.updateUI();
		}
		else if (event.getSource() == removeButton)
		{
			fileListModel.remove(imageList.getSelectedIndices());
			
			imageList.clearSelection();
			imageList.updateUI();
		}
		else if (event.getSource() == cancelButton)
		{
			setVisible(false);
		}
		else if (event.getSource() == okButton)
		{
			String path = getAtlasPath();
			
			if (path != null)
			{
				String error = generator.run(path, fileListModel.getFiles());
				
				if (!error.isEmpty())
				{
					JOptionPane.showMessageDialog(this, error, "Error!", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					setVisible(false);
				}
			}
		}
	}
	
	//--------------------------------------------------------------------------
	
	private String getAtlasPath()
	{
	
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(EditorApplication.getAssetsLocation()));
		fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
		
		fileChooser.addChoosableFileFilter(new AssetFileFilter("Texture Atlas", new String[]{"png"}));
		
		if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			File file = fileChooser.getSelectedFile();
			
			if (!file.getAbsolutePath().startsWith(EditorApplication.getAssetsLocation()))
			{
				if (JOptionPane.showConfirmDialog(this, "File " + file.getName() + " is not in the assets directory. Pick another location?", "Wrong path!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.NO_OPTION)	
					return getAtlasPath();
			}
			else			
			{
				if (file.exists())
				{
					if (JOptionPane.showConfirmDialog(this, "File " + file.getName() + " already exits. Overwrite file?", "File exists!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.NO_OPTION)	
						return getAtlasPath();
				}
					
				return file.getAbsolutePath().substring(EditorApplication.getAssetsLocation().length());				
			}
		}		
		
		return null;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(TreeSelectionEvent event)
	{
		if (event.isAddedPath())
			showThumbnail(((FileWrapper)event.getPath().getLastPathComponent()).getFile());
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent event)
	{
		if (!event.getValueIsAdjusting())
			showThumbnail(fileListModel.getFiles().get(imageList.getSelectedIndex()));
	}
	
	//--------------------------------------------------------------------------
	
	private void showThumbnail(File imageFile)
	{
		if (imageFile.isDirectory())
			return;
		
		try
		{
			BufferedImage img = ImageIO.read(imageFile);
			
			previewPanel.setImage(img);
			previewPanel.updateUI();
		}
		catch (IOException ex)
		{
			DebugConsole.logger.log(Level.SEVERE, null, ex);
		}
	}	
	
	//--------------------------------------------------------------------------
}


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class ImagePanel extends JPanel
{
	//--------------------------------------------------------------------------
	
	BufferedImage image;
	
	//--------------------------------------------------------------------------

	public void setImage(BufferedImage image)
	{
		this.image = image;
	}
	
	//--------------------------------------------------------------------------

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (image != null)
			g.drawImage(image, 0, 0, this);
	}		
	
	//--------------------------------------------------------------------------
}


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class FileListModel extends AbstractListModel
{
	//--------------------------------------------------------------------------
	
	ArrayList<File> files = new ArrayList<>();
	
	//--------------------------------------------------------------------------

	@Override
	public int getSize()
	{
		return files.size();
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String getElementAt(int i)
	{
		return files.isEmpty() ? "" : files.get(i).getName();
	}

	//--------------------------------------------------------------------------

	public void add(File file)
	{
		if (!files.contains(file))
			files.add(file);
	}
	
	//--------------------------------------------------------------------------
	
	public void remove(int[] indices)
	{
		File[] to_remove = new File[indices.length];
		
		for (int i = 0 ; i < indices.length ; i++)
			to_remove[i] = files.get(indices[i]);
		
		for (File rem : to_remove)
			files.remove(rem);
	}
	
	//--------------------------------------------------------------------------

	public ArrayList<File> getFiles()
	{
		return files;
	}
	
	//--------------------------------------------------------------------------
	
	public void clear()
	{
		files.clear();
	}
	
	//--------------------------------------------------------------------------
}


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class ImageFilter implements FileFilter
{
	//--------------------------------------------------------------------------
	
	@Override
	public boolean accept(File file)
	{
		if (file.isDirectory())
			return true;
		
		for (String suffix : ImageIO.getReaderFileSuffixes())
		{
			if (file.getPath().endsWith(suffix))
				return true;
		}
		
		return false;	
	}	
	
	//--------------------------------------------------------------------------
}


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class FileWrapper implements Comparable<FileWrapper>
{
	//--------------------------------------------------------------------------
	
	private File file;
	private FileWrapper[] children;
	
	//--------------------------------------------------------------------------

	public FileWrapper(File file, ImageFilter filter)
	{
		this.file = file;		
		
		File[] files = file.listFiles(filter);
		
		if (files != null)
		{
			children = new FileWrapper[files.length];
		
			for (int i = 0 ; i < files.length ; i++)
				children[i] = new FileWrapper(files[i], filter);
		
			Arrays.sort(children);
		}
		else
		{
			children = new FileWrapper[0];
		}
	}
	
	//--------------------------------------------------------------------------

	public File getFile()
	{
		return file;
	}
	
	//--------------------------------------------------------------------------

	@Override
	public String toString()
	{
		return file.getName();
	}		
	
	//--------------------------------------------------------------------------

	@Override
	public int compareTo(FileWrapper t)
	{
		if (this.file == t.file)
			return 0;
		
		if (this.file.isDirectory() == t.file.isDirectory())
			return this.file.getName().compareTo(t.file.getName());
		
		return this.file.isDirectory() ? -1 : 1;
	}
	
	//--------------------------------------------------------------------------

	public FileWrapper[] getChildren()
	{
		return children;
	}
	
	//--------------------------------------------------------------------------
}


/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
class DirectoryTreeModel implements TreeModel
{	
	//--------------------------------------------------------------------------
	
	private FileWrapper root = new FileWrapper(new File(EditorApplication.getAssetsLocation()), new ImageFilter());	

	//--------------------------------------------------------------------------

	@Override
	public Object getRoot()
	{
		return root;
	}

	//--------------------------------------------------------------------------

	@Override
	public boolean isLeaf(Object node)
	{		
		if (node == root)
			return false;
		else
			return ((FileWrapper) node).getFile().isFile();		
	}

	//--------------------------------------------------------------------------

	@Override
	public int getChildCount(Object parent)
	{	
		return ((FileWrapper) parent).getChildren().length;
	}

	//--------------------------------------------------------------------------

	@Override
	public Object getChild(Object parent, int index)
	{		
		return ((FileWrapper) parent).getChildren()[index];
	}

	//--------------------------------------------------------------------------

	@Override
	public int getIndexOfChild(Object parent, Object child)
	{	
		FileWrapper parent_wrapper = (FileWrapper) parent;
		FileWrapper child_wrapper = (FileWrapper) child;

		for (int i = 0 ; i < child_wrapper.getChildren().length ; i++)
		{
			if (parent_wrapper == child_wrapper.getChildren()[i])
				return i;
		}

		return -1;
	}

	//--------------------------------------------------------------------------

	@Override
	public void valueForPathChanged(TreePath path, Object newvalue)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void addTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}

	//--------------------------------------------------------------------------

	@Override
	public void removeTreeModelListener(TreeModelListener l)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}