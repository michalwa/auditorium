package michalwa.auditorium;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import michalwa.auditorium.playback.ChirpEmitter;
import michalwa.auditorium.playback.Emitter;

class EmitterTable extends JTable {
    /**
     * Amount to change values by when scrolling over selected numeric cells
     */
    private static final double NUMBER_SCROLL_STEP = 0.02f;

    EmitterTable(List<Region2D<Emitter>> regions, PopupFactory popupFactory) {
        setModel(new SimpleTableModel<>(regions) {
            {
                addColumn("👁", Boolean.class, Region2D::isVisible, Region2D::setVisible);
                addColumn("🎨", Color.class, Region2D::getColor, Region2D::setColor);
                addColumn("Type", String.class, r -> r.getData().getTypeName());
                addColumn("Name", String.class, Emitter::getRegionName, Emitter::setRegionName);
                addColumn("X", Double.class, Region2D::getCenterX, Region2D::setCenterX);
                addColumn("Y", Double.class, Region2D::getCenterY, Region2D::setCenterY);
                addColumn("R", Double.class, Region2D::getRadius, Region2D::setRadius);
                addColumn(
                    "Min delay (s)",
                    Double.class,
                    ChirpEmitter::getRegionMinDelaySeconds,
                    ChirpEmitter::setRegionMinDelaySeconds
                );
                addColumn(
                    "Max delay (s)",
                    Double.class,
                    ChirpEmitter::getRegionMaxDelaySeconds,
                    ChirpEmitter::setRegionMaxDelaySeconds
                );
                addColumn(
                    "Base volume",
                    Float.class,
                    r -> r.getData().getBaseVolume(),
                    (r, v) -> r.getData().setBaseVolume(v)
                );
                addColumn("Volume", Float.class, r -> r.getData().getVolume());
            }
        });

        var colorCellEditor = new ColorCellEditor();
        var numberCellRenderer = new NumberCellRenderer();

        setDefaultEditor(Color.class, colorCellEditor);
        setDefaultRenderer(Color.class, colorCellEditor);
        setDefaultRenderer(Double.class, numberCellRenderer);
        setDefaultRenderer(Float.class, numberCellRenderer);

        getColumnModel().getColumn(0).setPreferredWidth(32);
        getColumnModel().getColumn(0).setResizable(false);

        getColumnModel().getColumn(1).setPreferredWidth(32);
        getColumnModel().getColumn(1).setResizable(false);

        getColumnModel().getColumn(3).setPreferredWidth(300);

        var mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    var rowIndex = rowAtPoint(e.getPoint());
                    popupFactory.createPopup(rowIndex).show(EmitterTable.this, e.getX(), e.getY());
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                var rowIndex = rowAtPoint(e.getPoint());
                var columnIndex = columnAtPoint(e.getPoint());

                var isCellSelected = rowIndex == getSelectedRow()
                    && columnIndex == getSelectedColumn();
                var isCellEditable = getModel().isCellEditable(rowIndex, columnIndex);
                var columnClass = getModel().getColumnClass(columnIndex);

                if (
                    isCellSelected
                        && isCellEditable
                        && (columnClass == Double.class || columnClass == Float.class)
                ) {
                    var currentValue = (Number)getModel().getValueAt(rowIndex, columnIndex);
                    var newValue = currentValue.doubleValue()
                        - e.getPreciseWheelRotation() * NUMBER_SCROLL_STEP;

                    if (columnClass == Double.class) {
                        getModel().setValueAt(Double.valueOf(newValue), rowIndex, columnIndex);
                    } else if (columnClass == Float.class) {
                        getModel()
                            .setValueAt(Float.valueOf((float)newValue), rowIndex, columnIndex);
                    }
                } else {
                    getParent().dispatchEvent(e);
                }
            }
        };

        addMouseListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);
    }

    interface PopupFactory {
        JPopupMenu createPopup(int rowIndex);
    }
}
