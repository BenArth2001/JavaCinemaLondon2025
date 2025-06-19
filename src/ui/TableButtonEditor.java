package ui;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.BiConsumer;

public class TableButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel;
    private JButton editButton;
    private JButton deleteButton;
    private BiConsumer<ActionEvent, Integer> actionHandler;
    private JTable table;
    private int row;

    public TableButtonEditor(JCheckBox checkBox, BiConsumer<ActionEvent, Integer> actionHandler) {
        this.actionHandler = actionHandler;

        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));

        editButton = new JButton("Edit");
        editButton.setPreferredSize(new Dimension(60, 25));
        editButton.addActionListener(e -> {
            fireEditingStopped();
            actionHandler.accept(e, row);
        });

        deleteButton = new JButton("Delete");
        deleteButton.setPreferredSize(new Dimension(80, 25));
        deleteButton.addActionListener(e -> {
            fireEditingStopped();
            actionHandler.accept(e, row);
        });

        panel.add(editButton);
        panel.add(deleteButton);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;

        if (isSelected) {
            panel.setBackground(table.getSelectionBackground());
        } else {
            panel.setBackground(table.getBackground());
        }

        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "Edit/Delete";
    }
}
