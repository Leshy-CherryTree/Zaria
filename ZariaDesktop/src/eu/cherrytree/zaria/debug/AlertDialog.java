/****************************************/
/* AlertDialog.java						*/
/* Created on: 09-10-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.debug;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class AlertDialog extends JDialog implements ActionListener
{
	
	//--------------------------------------------------------------------------
	
	public enum Type
	{
		Error		("/eu/cherrytree/zaria/debug/res/error.png"),
		Warning		("/eu/cherrytree/zaria/debug/res/warning.png");
		
		private Image icon;

		private Type(String path)
		{
			try
			{
				icon = ImageIO.read(getClass().getResource(path));
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			}
		}

		public Image getIcon()
		{
			return icon;
		}				
	}
	
	//--------------------------------------------------------------------------
	
	public static void show(String title, String text, Type type)
	{
		new AlertDialog(null, title, text, type.getIcon());
	}
	
	//--------------------------------------------------------------------------

    private JScrollPane scrollPane = new JScrollPane();
    private JButton okButton = new JButton();
    private JTextArea textArea = new JTextArea();    
	
	//--------------------------------------------------------------------------	

    private AlertDialog(JFrame parent, String title, String text, Image icon)
	{
        super(parent, true);
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(title);
		setIconImage(icon);
        setType(Window.Type.UTILITY);

        textArea.setEditable(false);
        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setTabSize(4);
        scrollPane.setViewportView(textArea);
		
		textArea.setText(text);

        okButton.setText("Ok");
		okButton.addActionListener(this);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
                    .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addContainerGap())
        );

        pack();
		
		Rectangle framebounds = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Rectangle dialogbounds = getBounds();

		Rectangle newbounds = new Rectangle(framebounds.x + framebounds.width/2 - dialogbounds.width/2, 
											framebounds.y + framebounds.height/2 - dialogbounds.height/2, 
											dialogbounds.width, dialogbounds.height);
		
		setBounds(newbounds);	
		
		setVisible(true);
    }
	
	//--------------------------------------------------------------------------

	@Override
	public void actionPerformed(ActionEvent evt)
	{
		if(evt.getSource() == okButton)
			setVisible(false);
	}
	
	//--------------------------------------------------------------------------
}
