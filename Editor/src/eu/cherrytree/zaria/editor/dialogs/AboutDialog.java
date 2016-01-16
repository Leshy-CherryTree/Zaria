/****************************************/
/* AboutDialog.java						*/
/* Created on: 05-May-2013				*/
/* Copyright Cherry Tree Studio 2013	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.EditorApplication;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AboutDialog extends JDialog
{
	//--------------------------------------------------------------------------
	
	private static final long serialVersionUID = 1L;
	
	//--------------------------------------------------------------------------
	
	public AboutDialog(JFrame parent)
	{
		super(parent, true);
		
		setResizable(false);

        JLabel logoLabel = new JLabel();
        JLabel titleLabel = new JLabel();
        JLabel versionLabel = new JLabel();
        JLabel copyrightLabel = new JLabel();
        JLabel licenseLabel = new JLabel();
        JButton okButton = new JButton();

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setTitle("About");

        logoLabel.setIcon(new ImageIcon(getClass().getResource("/eu/cherrytree/zaria/editor/res/logo.png")));

        titleLabel.setFont(new Font("Cantarell", 1, 36));
        titleLabel.setText("Zone Editor");

        versionLabel.setFont(new Font("Cantarell", 0, 20));
        versionLabel.setText("v" + EditorApplication.getVersion());

        copyrightLabel.setText("Copyright © Cherry Tree Studio 2014");

        licenseLabel.setText("Released under EUPL v1.1");

        okButton.setText("Ok");
        okButton.addActionListener(new ActionListener()
        {
			@Override
            public void actionPerformed(ActionEvent evt)
            {
                setVisible(false);
            }
        });

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(okButton, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logoLabel)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(titleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(versionLabel))
                            .addComponent(copyrightLabel)
                            .addComponent(licenseLabel))))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
		
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addComponent(logoLabel)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(titleLabel)
                            .addComponent(versionLabel))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(copyrightLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(licenseLabel))
                            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(okButton)))))
                .addGap(10, 10, 10))
        );

        pack();
    }
	
	//--------------------------------------------------------------------------
}