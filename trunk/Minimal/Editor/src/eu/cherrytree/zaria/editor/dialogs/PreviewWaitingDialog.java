/****************************************/
/* PreviewWaitingDialog.java			*/
/* Created on: 11-Apr-2014				*/
/* Copyright Cherry Tree Studio 2014	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class PreviewWaitingDialog extends JDialog implements ActionListener
{
	//--------------------------------------------------------------------------
	
	private JButton endButton = new JButton();
	private JLabel mapNameLabel = new JLabel();
	private Process process;
	
	//--------------------------------------------------------------------------

	public PreviewWaitingDialog(Frame parent, Process process)
	{
		super(parent, false);
		
		setResizable(false);

		this.process = process;		

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Map preview in progress");
		setType(Window.Type.UTILITY);

		mapNameLabel.setText("Previewing map");

		endButton.setText("End preview");
		
		endButton.addActionListener(this);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(endButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(mapNameLabel, GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(mapNameLabel)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(endButton)
				.addGap(10, 10, 10)));

		pack();
		
		Rectangle framebounds = parent.getBounds();
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);
		
		setBounds(newbounds);		
	}
	
	//--------------------------------------------------------------------------
	
	public void start()
	{
		setVisible(true);
		getParent().setEnabled(false);
		
		new Thread(new Runnable() {

			@Override
			public void run()
			{
				try
				{
					process.waitFor();
					process.exitValue();
				}
				catch (InterruptedException ex)
				{
					DebugConsole.logger.log(Level.SEVERE, null, ex);
				}				
												
				onFinished();				
			}
			
		}).start();
	}
	
	//--------------------------------------------------------------------------
	
	private synchronized void onFinished()
	{
		getParent().setEnabled(true);
		setVisible(false);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == endButton)
		{
			process.destroy();
			onFinished();
		}
	}
	
	//--------------------------------------------------------------------------
}
