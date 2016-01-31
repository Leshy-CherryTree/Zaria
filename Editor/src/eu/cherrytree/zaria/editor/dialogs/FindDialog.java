/****************************************/
/* FindDialog.java						*/
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.document.DocumentManager;
import eu.cherrytree.zaria.editor.document.FindOptions;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class FindDialog extends JDialog implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	// Input text fields
	private JTextField findTextField = new JTextField();
	private JTextField replaceWithTextField = new JTextField();
	
	// Scope radio buttons
	private ButtonGroup scopeRadioButtonGroup = new ButtonGroup();	
	private JRadioButton allRadioButton = new JRadioButton();
	private JRadioButton selectionRadioButton = new JRadioButton();
	
	// Direction radio buttons
	private ButtonGroup directionRadioButtonGroup = new ButtonGroup();	
    private JRadioButton backwardRadioButton = new JRadioButton();
    private JRadioButton forwardRadioButton = new JRadioButton();
           
	// Options check boxes
    private JCheckBox markFoundCheckBox = new JCheckBox();
	private JCheckBox caseSensitiveCheckBox = new JCheckBox();
    private JCheckBox wholeWordCheckBox = new JCheckBox();
    private JCheckBox wrapSearchCheckBox = new JCheckBox();
	
	// Buttons
	private JButton findButton = new JButton();
	private JButton replaceAllButton = new JButton();
    private JButton replaceButton = new JButton();
    private JButton replaceFindButton = new JButton();
	private JButton closeButton = new JButton();
	
	// Document manager
	private DocumentManager documentManager;
	
	//--------------------------------------------------------------------------

	public FindDialog(JFrame parent, DocumentManager manager)
	{
		super(parent, false);
		
		setTitle("Find/Replace");
		
		this.documentManager = manager;

        JPanel inputPanel = new JPanel();
        JLabel findLabel = new JLabel();
        JLabel replaceWithLabel = new JLabel();
        JPanel directionPanel = new JPanel();
        JPanel scopePanel = new JPanel();
        JPanel optionsPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();

        setAlwaysOnTop(true);
        setType(Window.Type.UTILITY);
		setResizable(false);

        findLabel.setText("Find:");

        replaceWithLabel.setText("Replace with:");

        GroupLayout inputPanelLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(replaceWithLabel)
                    .addComponent(findLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(findTextField)
                    .addComponent(replaceWithTextField)))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(findTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(findLabel))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(replaceWithTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(replaceWithLabel)))
        );

        directionPanel.setBorder(BorderFactory.createTitledBorder("Direction"));

        forwardRadioButton.setText("Forward");
		forwardRadioButton.setSelected(true);
        backwardRadioButton.setText("Backward");
		
		directionRadioButtonGroup.add(forwardRadioButton);
		directionRadioButtonGroup.add(backwardRadioButton);

        GroupLayout directionPanelLayout = new GroupLayout(directionPanel);
        directionPanel.setLayout(directionPanelLayout);
        directionPanelLayout.setHorizontalGroup(
            directionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(directionPanelLayout.createSequentialGroup()
                .addGroup(directionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(forwardRadioButton)
                    .addComponent(backwardRadioButton))
                .addGap(0, 110, Short.MAX_VALUE))
        );
        directionPanelLayout.setVerticalGroup(
            directionPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(directionPanelLayout.createSequentialGroup()
                .addComponent(forwardRadioButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(backwardRadioButton))
        );

        scopePanel.setBorder(BorderFactory.createTitledBorder("Scope"));

        allRadioButton.setText("All");
		allRadioButton.setSelected(true);
        selectionRadioButton.setText("Selection");
		
		scopeRadioButtonGroup.add(allRadioButton);
		scopeRadioButtonGroup.add(selectionRadioButton);
		
		// For now RSyntaxTextArea does not support this.
		selectionRadioButton.setEnabled(false);
		selectionRadioButton.setToolTipText("Currently not supported");

        GroupLayout scopePanelLayout = new GroupLayout(scopePanel);
        scopePanel.setLayout(scopePanelLayout);
        scopePanelLayout.setHorizontalGroup(
            scopePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(scopePanelLayout.createSequentialGroup()
                .addGroup(scopePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(allRadioButton)
                    .addComponent(selectionRadioButton))
                .addGap(0, 43, Short.MAX_VALUE))
        );
        scopePanelLayout.setVerticalGroup(
            scopePanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(scopePanelLayout.createSequentialGroup()
                .addComponent(allRadioButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(selectionRadioButton))
        );

        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

        caseSensitiveCheckBox.setText("Case sensitive");
        wholeWordCheckBox.setText("Whole word");
        wrapSearchCheckBox.setText("Wrap search");
        markFoundCheckBox.setText("Mark found");

        GroupLayout optionsPanelLayout = new GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
            optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(caseSensitiveCheckBox)
                    .addComponent(wholeWordCheckBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(wrapSearchCheckBox)
                    .addComponent(markFoundCheckBox))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        optionsPanelLayout.setVerticalGroup(
            optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(optionsPanelLayout.createSequentialGroup()
                .addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(caseSensitiveCheckBox)
                    .addComponent(wrapSearchCheckBox))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(wholeWordCheckBox)
                    .addComponent(markFoundCheckBox)))
        );

        findButton.setText("Find");		
        replaceFindButton.setText("Replace/Find");
        replaceButton.setText("Replace");
        replaceAllButton.setText("Replace All");
        closeButton.setText("Close");
		
		findButton.addActionListener(this);
		replaceFindButton.addActionListener(this);
		replaceButton.addActionListener(this);
		replaceAllButton.addActionListener(this);
		closeButton.addActionListener(this);
		
        GroupLayout buttonsPanelLayout = new GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addComponent(findButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceFindButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(replaceAllButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(findButton)
                .addComponent(replaceFindButton)
                .addComponent(replaceButton)
                .addComponent(replaceAllButton)
                .addComponent(closeButton))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(inputPanel, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(optionsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(directionPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scopePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(buttonsPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(directionPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scopePanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(optionsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

		findTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				super.keyPressed(e);
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					documentManager.find(findTextField.getText(), getFindOptions());
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					setVisible(false);
				}
			}
							
		});	
		
		replaceWithTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				super.keyPressed(e);
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					documentManager.replace(findTextField.getText(), replaceWithTextField.getText(),  getFindOptions());
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					setVisible(false);
				}
			}
							
		});	
		
        pack();
    }
	
	//--------------------------------------------------------------------------
	
	private FindOptions getFindOptions()
	{
		return new FindOptions(	forwardRadioButton.isSelected() ? FindOptions.Direction.FORWARD : FindOptions.Direction.BACKWARD, 
													allRadioButton.isSelected() ? FindOptions.Scope.ALL : FindOptions.Scope.SELECTION, 
													caseSensitiveCheckBox.isSelected(), 
													wholeWordCheckBox.isSelected(), 
													wrapSearchCheckBox.isSelected(), 
													markFoundCheckBox.isSelected(),
													selectionRadioButton.isSelected());
	}

	//--------------------------------------------------------------------------
	
	public void setSearched(Object obj)
	{
		if(obj != null)
			findTextField.setText(obj.toString());
		else
			findTextField.setText("");
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{	
		if(event.getSource() == closeButton)
		{
			setVisible(false);
		}
		else if(event.getSource() == findButton)
		{
			documentManager.find(findTextField.getText(), getFindOptions());
		}
		else if(event.getSource() == replaceFindButton)
		{
			documentManager.replaceFind(findTextField.getText(), replaceWithTextField.getText(),  getFindOptions());
		}
		else if(event.getSource() == replaceButton)
		{
			documentManager.replace(findTextField.getText(), replaceWithTextField.getText(),  getFindOptions());
		}
		else if(event.getSource() == replaceAllButton)
		{
			documentManager.replaceAll(findTextField.getText(), replaceWithTextField.getText(),  getFindOptions());
		}		
	}
	
	//--------------------------------------------------------------------------
}