package michalwa.auditorium;

import java.awt.Color;
import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

class ColorCellEditor extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
    ColorControl renderer = new ColorControl(Color.BLACK);
    ColorControl editor = new ColorControl(Color.BLACK);

    ColorCellEditor() {
        editor.addPropertyChangeListener("value", e -> stopCellEditing());
        editor.addCancelListener(() -> cancelCellEditing());
    }

    @Override
    public Object getCellEditorValue() {
        return editor.getValue();
    }

    @Override
    public Component getTableCellEditorComponent(
        JTable table,
        Object value,
        boolean isSelected,
        int row,
        int column
    ) {
        editor.setValue((Color)value);
        return editor;
    }

    @Override
    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        renderer.setValue((Color)value);
        return renderer;
    }
}
