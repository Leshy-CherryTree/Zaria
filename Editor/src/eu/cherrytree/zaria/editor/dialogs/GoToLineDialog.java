/****************************************/
/* GoToLineDialog.java					*/
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.document.DocumentManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class GoToLineDialog extends JDialog
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	private JFormattedTextField inputTextField = new JFormattedTextField();
	
	private DocumentManager documentManager;
	
	//--------------------------------------------------------------------------
    
	public GoToLineDialog(JFrame parent, DocumentManager manager)
	{
		super(parent, true);
		
		setResizable(false);
		
		this.documentManager = manager;

        JLabel label = new JLabel();
        JButton goButton = new JButton();
        JButton cancelButton = new JButton();

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("Go to Line");

        label.setText("Go to Line:");

        goButton.setText("Go");
        goButton.addActionListener(new ActionListener()
        {
			@Override
            public void actionPerformed(ActionEvent evt)
            {
				confirm();
            }
        });

        cancelButton.setText("Cancel");
		
		cancelButton.addActionListener(new ActionListener()
        {
			@Override
            public void actionPerformed(ActionEvent evt)
            {
				cancel();
            }
        });
		
		inputTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e)
			{
				super.keyPressed(e);
				
				if(e.getKeyCode() == KeyEvent.VK_ENTER)
				{
					confirm();
				}
				else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					cancel();
				}
			}
							
		});	

        inputTextField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(new java.text.DecimalFormat("#0"))));

        GroupLayout layout = new GroupLayout(getContentPane());
		
        getContentPane().setLayout(layout);
		
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(label)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inputTextField, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(goButton)))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
		
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(label)
                    .addComponent(inputTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(cancelButton)
                    .addComponent(goButton))
                .addGap(10, 10, 10))
        );

        pack();
    }
	
	//--------------------------------------------------------------------------
	
	private void confirm()
	{
		String input = inputTextField.getText();

		if(!input.isEmpty())
		{
			documentManager.goToLine(Integer.parseInt(input));

			inputTextField.setText("");
			setVisible(false);
		}
	}

	//--------------------------------------------------------------------------
	
	private void cancel()
	{
		inputTextField.setText("");
		setVisible(false);
	}
	
	//--------------------------------------------------------------------------
}