/****************************************/
/* CurveEditingDialog.java 				*/
/* Created on: 15-Jul-2016				*/
/* Copyright Cherry Tree Studio 2016	*/
/* Released under EUPL v1.1				*/
/****************************************/

package eu.cherrytree.zaria.editor.dialogs;

import eu.cherrytree.zaria.editor.debug.DebugConsole;
import eu.cherrytree.zaria.editor.dialogs.curves.GraphPanel;
import eu.cherrytree.zaria.editor.dialogs.curves.XYInputDialog;
import eu.cherrytree.zaria.editor.document.ZoneDocument;
import eu.cherrytree.zaria.serialization.ZariaObjectDefinition;
import eu.cherrytree.zaria.math.Curve;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Leszek Szczepański <leszek.gamedev@gmail.com>
 */
public class CurveEditingDialog extends EditorDialog implements ActionListener, ListSelectionListener, ItemListener, GraphPanel.GraphPanelSeclectionListener, MouseListener
{
	//--------------------------------------------------------------------------
	
	private JPanel buttonPanel = new JPanel();
    private JButton cancelButton = new JButton();
    private JComboBox<Curve.CurveType> curveTypeComboBox = new JComboBox<>();
    private GraphPanel graphPanel = new GraphPanel();
    private JButton okButton = new JButton();
    private JList<String> pointList = new JList<>();
    private JPanel pointListPanel = new JPanel();
    private JButton removeButton = new JButton();
    private JScrollPane scrollPane = new JScrollPane();
	
	//--------------------------------------------------------------------------

	public CurveEditingDialog(JFrame parent, ZoneDocument document, ZariaObjectDefinition definition)
	{
		super(parent, document, definition);

		setTitle("Curve Editor - " + definition.getID());
		
		graphPanel.setListener(this);
		graphPanel.init((Curve) definition);
		graphPanel.setFocusable(true);
		graphPanel.setFocusCycleRoot(true);
		
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        curveTypeComboBox.setModel(new DefaultComboBoxModel<>(Curve.CurveType.values()));
		curveTypeComboBox.addItemListener(this);

		curveTypeComboBox.setSelectedIndex(graphPanel.getType().ordinal());
		
		pointList.addListSelectionListener(this);
		pointList.addMouseListener(this);
        pointList.setModel(graphPanel.getListModel());
		
        scrollPane.setViewportView(pointList);

        removeButton.setText("Remove Point");
		removeButton.addActionListener(this);

        GroupLayout pointListPanelLayout = new GroupLayout(pointListPanel);
        pointListPanel.setLayout(pointListPanelLayout);
        pointListPanelLayout.setHorizontalGroup(
            pointListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pointListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pointListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(curveTypeComboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(GroupLayout.Alignment.TRAILING, pointListPanelLayout.createSequentialGroup()
                        .addGroup(pointListPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(removeButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
                            .addComponent(scrollPane))
                        .addContainerGap())))
        );
        pointListPanelLayout.setVerticalGroup(
            pointListPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(pointListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(curveTypeComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 463, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton))
        );

        okButton.setText("OK");
		okButton.addActionListener(this);

        cancelButton.setText("Cancel");
		cancelButton.addActionListener(this);

        GroupLayout buttonPanelLayout = new GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(cancelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton)
                .addContainerGap())
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(buttonPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap())
        );

        graphPanel.setBackground(new Color(255, 204, 153));

        GroupLayout graphPanelLayout = new GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 916, Short.MAX_VALUE)
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pointListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(buttonPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(pointListPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(graphPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == removeButton)
		{
			graphPanel.removePointAtIndex(pointList.getSelectedIndex());
			pointList.setSelectedIndex(Integer.MAX_VALUE);
		}
		else if (e.getSource() == okButton)
		{					
			try
			{
				graphPanel.save((Curve) definition);
				document.setModified();
			}
			catch (SecurityException | IllegalArgumentException | NoSuchFieldException ex)
			{
				DebugConsole.logger.log(Level.SEVERE, null, ex);
			}
			setVisible(false);
		}
		else if (e.getSource() == cancelButton)
		{
			setVisible(false);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getSource() == pointList)
			graphPanel.highlightPoint(pointList.getSelectedIndex());
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void onPointSelected(int index)
	{
		if (index >= 0)
			pointList.setSelectedIndex(index);
		else
			pointList.setSelectedIndex(Integer.MAX_VALUE);
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void itemStateChanged(ItemEvent ie)
	{
		if(ie.getSource() == curveTypeComboBox)
		{
			graphPanel.setType(Curve.CurveType.values()[curveTypeComboBox.getSelectedIndex()]);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseClicked(java.awt.event.MouseEvent evt)
	{
		JList list = (JList) evt.getSource();
		
		if (evt.getClickCount() == 2)
		{
			int index = list.locationToIndex(evt.getPoint());
			XYInputDialog dialog = new XYInputDialog(this, graphPanel, graphPanel.getPoint(index));
			dialog.setVisible(true);
		}
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mousePressed(java.awt.event.MouseEvent me)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseReleased(java.awt.event.MouseEvent me)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseEntered(java.awt.event.MouseEvent me)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------

	@Override
	public void mouseExited(java.awt.event.MouseEvent me)
	{
		// Intentionally empty.
	}
	
	//--------------------------------------------------------------------------
}