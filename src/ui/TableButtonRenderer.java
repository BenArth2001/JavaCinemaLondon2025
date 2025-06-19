package ui;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableButtonRenderer implements TableCellRenderer {
    private JPanel panel;
    private JButton editButton;
    private JButton deleteButton;

    public TableButtonRenderer() {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");

        editButton.setPreferredSize(new Dimension(60, 25));
        deleteButton.setPreferredSize(new Dimension(80, 25));

        panel.add(editButton);
        panel.add(deleteButton);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }

        return panel;
    }
}
