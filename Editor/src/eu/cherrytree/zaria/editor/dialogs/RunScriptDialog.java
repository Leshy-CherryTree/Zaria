/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.EditorApplication;
import eu.cherrytree.zaria.editor.ScriptInterface;
import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.TextEditorState;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.editor.document.parsers.ScriptDocument;
import eu.cherrytree.zaria.editor.document.parsers.ScriptParser;
import eu.cherrytree.zaria.scripting.preprocessor.ScriptPreprocessor;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import javassist.Modifier;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrapFactory;

/**
 *
 * @author leshy
 */
public class RunScriptDialog extends JDialog implements ActionListener, ScriptDocument
{
	//--------------------------------------------------------------------------
	
	public class ScriptFileWrapper
	{
		private File file;

		public ScriptFileWrapper(File file)
		{
			this.file = file;
		}

		public File getFile()
		{
			return file;
		}				
		@Override
		public String toString()
		{
			if (file == null)
				return "untitled";
			
			String path = file.getAbsolutePath();
			return path.substring(EditorApplication.getEditorScriptsLocation().length());
		}				
	}
	
	//--------------------------------------------------------------------------
	
	private static class EditorContextFactory extends ContextFactory
	{
		@Override
		protected Context makeContext()
		{
			Context c = super.makeContext();
			c.setWrapFactory(new EditorWrapFactory());

			return c;
		}
	}
	
	//--------------------------------------------------------------------------

	private static class EditorWrapFactory extends WrapFactory
	{				
		@Override
		public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType)
		{
			return new ScriptInterface.NativeObject(scope, javaObject, staticType);
		}
	}
	
	//--------------------------------------------------------------------------

	private static final String [] functions;
	
	static
	{
		ContextFactory.initGlobal(new EditorContextFactory());
		
		Method[] methods = ScriptInterface.class.getMethods();
		ArrayList<String> names = new ArrayList<>();
		
		for (Method method : methods)
		{
			if (Modifier.isStatic(method.getModifiers()) && Modifier.isPublic(method.getModifiers()) && !names.contains(method.getName()))
				names.add(method.getName());
		}
		
		functions = new String[names.size()];		
		names.toArray(functions);
	}
	
	//--------------------------------------------------------------------------
	
	private JButton closeButton = new JButton();
	private JButton newButton = new JButton();
	private JButton deleteButton = new JButton();
	private JButton saveAsButtton = new JButton();
	private JButton runButton = new JButton();
	private JComboBox<String> scriptsComboBox = new JComboBox<>();
	private JPanel buttonPanel = new JPanel();
	private RSyntaxTextArea textArea = new RSyntaxTextArea();
	private RTextScrollPane pane = new RTextScrollPane();	
	
	private File currentScript = null;
	
	private JFrame parentFrame;		
	
	//--------------------------------------------------------------------------

	public RunScriptDialog(JFrame parent, boolean modal)
	{
		super(parent, modal);
		
		this.parentFrame = parent;
	
		textArea.setSyntaxEditingStyle("script_syntax");
		textArea.setAutoIndentEnabled(true);		
		textArea.setCloseCurlyBraces(true);
		textArea.setCodeFoldingEnabled(true);		
		textArea.addParser(new ScriptParser(this));
		
		textArea.setAntiAliasingEnabled(TextEditorState.isAntialiasingEnabled());
		textArea.setFractionalFontMetricsEnabled(TextEditorState.isFractionalFontMetricsEnabled());
		
		TextEditorState.getTheme().apply(textArea);
		
		applyFontSize(TextEditorState.getFontSize());
	
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		getAllScripts();
		
		scriptsComboBox.addActionListener(this);

		setTitle("Run Script - untitled");

		runButton.setText("Run");
		newButton.setText("New");		
		deleteButton.setText("Delete");
		saveAsButtton.setText("Save As");
		closeButton.setText("Close");
		
		runButton.addActionListener(this);
		newButton.addActionListener(this);
		deleteButton.addActionListener(this);
		saveAsButtton.addActionListener(this);
		closeButton.addActionListener(this);

		GroupLayout jPanel2Layout = new GroupLayout(buttonPanel);
		buttonPanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
			jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(closeButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 419, Short.MAX_VALUE)
				.addComponent(newButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(saveAsButtton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(deleteButton)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(scriptsComboBox, GroupLayout.PREFERRED_SIZE, 469, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(runButton)
				.addContainerGap())
		);
		jPanel2Layout.setVerticalGroup(
			jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(jPanel2Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				.addComponent(newButton)
				.addComponent(saveAsButtton)
				.addComponent(deleteButton)
				.addComponent(scriptsComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addComponent(runButton)
				.addComponent(closeButton))
		);

		pane.setViewportView(textArea);
		pane.setLineNumbersEnabled(true);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addComponent(buttonPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(pane)
				.addContainerGap())
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(pane, GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap())
		);

		pack();
	}
	
	//--------------------------------------------------------------------------
	
	private void getAllScripts()
	{
		ArrayList<File> files = new ArrayList();
		
		getAllFilesRecursive(new File(EditorApplication.getEditorScriptsLocation()), files);
								
		ScriptFileWrapper[] file_array = new ScriptFileWrapper[files.size()+1];
		
		file_array[0] = new ScriptFileWrapper(null);
		
		int selected_index = 0;
		
		for (int i = 1 ; i < file_array.length ; i++)
		{
			if (currentScript != null && currentScript.getAbsolutePath().equals(files.get(i-1).getAbsolutePath()))
				selected_index = i;
			
			file_array[i] = new ScriptFileWrapper(files.get(i-1));
		}

		scriptsComboBox.setModel(new DefaultComboBoxModel(file_array));
		scriptsComboBox.setSelectedIndex(selected_index);
	}
	
	//--------------------------------------------------------------------------
	
	private void getAllFilesRecursive(File f, ArrayList<File> files)
	{
		if (f.isDirectory())
		{
			File[] contents = f.listFiles();
			
			for (File file : contents)
				getAllFilesRecursive(file, files);
		}
		else if (f.getName().toLowerCase().endsWith(".zonescript"))
		{
			files.add(f);
		}
	}
	
	//--------------------------------------------------------------------------
		
	private void applyFontSize(float size)
	{
		SyntaxScheme scheme = (SyntaxScheme) textArea.getSyntaxScheme().clone();

		int count = scheme.getStyleCount();
		
		for (int i = 0; i < count; i++)
		{
			Style ss = scheme.getStyle(i);
			if (ss != null)
			{
				Font font = ss.font;
							
				if (font != null)
					ss.font = font.deriveFont(size);				
			}
		}

		Font font = textArea.getFont();
		textArea.setFont(font.deriveFont(size));
		
		textArea.setSyntaxScheme(scheme);
		
		Component parent = textArea.getParent();
		
		if (parent instanceof JViewport)
		{
			parent = parent.getParent();
			
			if (parent instanceof JScrollPane)
				parent.repaint();
		}
		
		textArea.setSyntaxScheme(scheme);
	}

	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent ae)
	{
		if (ae.getSource() == runButton)
		{			
			if (currentScript == null)
				currentScript = DocumentManager.showSaveDialog(ZoneDocument.DocumentType.ZONE_SCRIPT, EditorApplication.getEditorScriptsLocation(), false);
		
			if (currentScript == null)
			{
				setVisible(false);
				return;
			}
			

			try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentScript)))
			{
				writer.write(textArea.getText());
			}
			catch (IOException ex)
			{
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
			}
		
			getAllScripts();
			
			String error = DocumentManager.showWaitDialog("Please wait...", new WaitDialog.ResultRunnable<String>()
			{
				private String error;

				@Override
				public String get()
				{
					return error;
				}

				@Override
				public void run()
				{
					try
					{
						error = RunScriptDialog.this.run();
					}
					catch (IOException ex)
					{
						error = ex.getMessage();
					}

					finish();
				}
			});	
			
			if (error != null)
				ExtendedInformationDialog.show(parentFrame, "Runing of script failed", error);
			else
				setVisible(false);
		}
		else if(ae.getSource() == closeButton)
		{
			setVisible(false);
		}
		else if (ae.getSource() == newButton)
		{
			currentScript = null;
			textArea.setText("");
			setTitle("Run Script - untitled");
			
			getAllScripts();
			scriptsComboBox.setSelectedIndex(0);
		}
		else if (ae.getSource() == saveAsButtton)
		{
			if (currentScript == null)
				currentScript = DocumentManager.showSaveDialog(ZoneDocument.DocumentType.ZONE_SCRIPT, EditorApplication.getEditorScriptsLocation(), false);
		
			if (currentScript != null)
			{
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentScript)))
				{
					writer.write(textArea.getText());			
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
			}			
			
			getAllScripts();			
		}
		else if (ae.getSource() == scriptsComboBox)
		{
			currentScript = ((ScriptFileWrapper) scriptsComboBox.getSelectedItem()).getFile();
			
			if (currentScript == null)
			{
				textArea.setText("");
				setTitle("Run Script - untitled");	
			}
			else
			{
				try
				{
					textArea.setText(ZoneDocument.loadFileAsString(currentScript));
					setTitle("Run Scipt - " + currentScript.getName());
				}
				catch (IOException ex)
				{
					JOptionPane.showMessageDialog(this, ex.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	//--------------------------------------------------------------------------

	@Override
	public String getText()
	{
		return textArea.getText();
	}
		
	//--------------------------------------------------------------------------
	
	public String run() throws IOException
	{						
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();				
		
		try
		{
			ScriptPreprocessor.setScriptLocation(EditorApplication.getEditorScriptsLocation());
			String script = ScriptPreprocessor.preProcess(currentScript);
			
			scope.defineFunctionProperties(functions, ScriptInterface.class, ScriptableObject.DONTENUM);
			cx.evaluateString(scope, script, "script", 0, null);
		}
		catch(RhinoException ex)
		{
			String error = "Compilation of script " + currentScript.getName() + " failed.\n\n";
			
			error += ex.details() + "\n";
			
			if (ex.lineSource() != null)
				error += "[" + Integer.toString(ex.lineNumber()) + "," + Integer.toString(ex.columnNumber()) + "] " + ex.lineSource();
			
			return error;
		}
		
		Context.exit();
		
		return null;
	}
	
	//--------------------------------------------------------------------------
}
