/****************************************/
/* WaitDialog.java 						*/
/* Created on: 07-Sep-2014				*/
/* Copyright Cherry Tree Studio 2014		*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import java.awt.Rectangle;
import java.awt.Window;
import javax.swing.GroupLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class WaitDialog<T> extends JDialog
{
	//--------------------------------------------------------------------------
	
	public static abstract class ResultRunnable<T> implements Runnable
	{
		private WaitDialog dialog;

		public final void setDialog(WaitDialog dialog)
		{
			this.dialog = dialog;
		}
		
		protected final void finish()
		{			
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run()
				{
					dialog.setVisible(false);
				}
			});
		}
		
		public abstract T get();
	}
	
	//--------------------------------------------------------------------------
	
	private JLabel label = new JLabel();
	private JProgressBar progressBar = new JProgressBar();
	private ResultRunnable<T> runnable;
	
	//--------------------------------------------------------------------------
	
	public WaitDialog(JFrame parent, ResultRunnable<T> runnable, String message)
	{
		super(parent, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setType(Window.Type.POPUP);

		label.setText(message);
		progressBar.setIndeterminate(true);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addComponent(label)
				.addGap(0, 0, Short.MAX_VALUE))
				.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))
				.addContainerGap()));
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
				.addContainerGap()
				.addComponent(label)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

		pack();
		
		Rectangle framebounds = parent.getBounds();
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);
		
		setBounds(newbounds);	
		
		runnable.setDialog(this);
		this.runnable = runnable;
	}
	
	//--------------------------------------------------------------------------
	
	public void run()
	{						
		new Thread(runnable).start();
		setVisible(true);
	}

	//--------------------------------------------------------------------------
	
	public T getResult()
	{
		return runnable.get();
	}
	
	//--------------------------------------------------------------------------
}
