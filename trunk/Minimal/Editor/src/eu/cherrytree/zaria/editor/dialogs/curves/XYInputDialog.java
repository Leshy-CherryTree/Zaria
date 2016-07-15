/****************************************/
/* XYInputDialog.java					*/
/* Created on: 15-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs.curves;

import eu.cherrytree.zaria.editor.debug.DebugConsole;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class XYInputDialog extends JDialog implements ActionListener
{
	//--------------------------------------------------------------------------
	
    private JButton cancelButton = new JButton();
    private JButton okButton = new JButton();
    private JLabel xLabel = new JLabel();
    private JLabel yLabel = new JLabel();
    private JTextField xTextField = new JTextField();
    private JTextField yTextField = new JTextField();
	
	private GraphPanel.Point point;
	private GraphPanel panel;
	
	//--------------------------------------------------------------------------
	
	public XYInputDialog(JDialog parent, GraphPanel panel, GraphPanel.Point point)
	{
		super(parent, true);
		
		this.panel = panel;
		this.point = point;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setType(java.awt.Window.Type.POPUP);

        cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);
		
        okButton.setText("Ok");
		okButton.addActionListener(this);

        xLabel.setText("X:");
        yLabel.setText("Y:");
		
		xTextField.setText(Float.toString(point.getX()));
		yTextField.setText(Float.toString(point.getY()));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(cancelButton)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(xLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(xTextField, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(yTextField, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(xTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(xLabel)
                    .addComponent(yLabel)
                    .addComponent(yTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addGap(15, 15, 15))
        );
		
		Rectangle framebounds = parent.getBounds();
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);
		
		setBounds(newbounds);

        pack();
    }
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == okButton)
		{
			try
			{
				float f_x = Float.parseFloat(xTextField.getText());
				float f_y = Float.parseFloat(yTextField.getText());
				
				point.set(f_x, f_y);
				panel.update();
			}
			catch(NumberFormatException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
			
			setVisible(false);
		}
		else if (event.getSource() == cancelButton)			
		{
			setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------
}
