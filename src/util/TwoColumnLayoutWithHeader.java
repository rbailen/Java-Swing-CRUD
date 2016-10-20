package util;

import java.awt.*;
import javax.swing.*;

public class TwoColumnLayoutWithHeader {

    public JComponent getTwoColumnLayout(JLabel[] labels, JComponent[] fields, boolean addMnemonics) {
        JComponent panel = new JPanel();

        GroupLayout layout = new GroupLayout(panel);

        panel.setLayout(layout);

        // Turn on automatically adding gaps between components
        layout.setAutoCreateGaps(true);

        // Create a sequential group for the horizontal axis
        GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
        GroupLayout.Group yLabelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING);
        hGroup.addGroup(yLabelGroup);
        GroupLayout.Group yFieldGroup = layout.createParallelGroup();
        hGroup.addGroup(yFieldGroup);
        layout.setHorizontalGroup(hGroup);

        // Create a sequential group for the vertical axis
        GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
        layout.setVerticalGroup(vGroup);

        int p = GroupLayout.PREFERRED_SIZE;

        // Add the components to the groups
        for (JLabel label : labels) {
            yLabelGroup.addComponent(label);
        }
        for (Component field : fields) {
            yFieldGroup.addComponent(field, p, p, p);
        }
        for (int i = 0; i < labels.length; i++) {
            vGroup.addGroup(layout.createParallelGroup().
                    addComponent(labels[i]).
                    addComponent(fields[i], p, p, p));
        }

        return panel;
    }

    public JComponent getTwoColumnLayout(String[] labelStrings, JComponent[] fields) {
        JLabel[] labels = new JLabel[labelStrings.length];

        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel(labelStrings[i]);
        }

        return getTwoColumnLayout(labels, fields);
    }

    public JComponent getTwoColumnLayout(JLabel[] labels, JComponent[] fields) {
        return getTwoColumnLayout(labels, fields, true);
    }
}
