package michalwa.auditorium;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.SpatialAudio;

class SpatialRegionTable extends JTable {
    SpatialRegionTable(List<SpatialRegion<SpatialAudio>> regions, PopupFactory popupFactory) {
        setModel(new SimpleTableModel<>(regions) {
            {
                addColumn("👁", Boolean.class, SpatialRegion::isVisible, SpatialRegion::setVisible);
                addColumn("Type", String.class, r -> r.getData().getTypeName());
                addColumn("Color", Color.class, SpatialRegion::getColor, SpatialRegion::setColor);
                addColumn(
                    "Name",
                    String.class,
                    SpatialAudio::getRegionName,
                    SpatialAudio::setRegionName
                );
                addColumn("X", Double.class, SpatialRegion::getCenterX, SpatialRegion::setCenterX);
                addColumn("Y", Double.class, SpatialRegion::getCenterY, SpatialRegion::setCenterY);
                addColumn("R", Double.class, SpatialRegion::getRadius, SpatialRegion::setRadius);
                addColumn(
                    "Min delay (s)",
                    Double.class,
                    AudioChirp::getRegionMinDelaySeconds,
                    AudioChirp::setRegionMinDelaySeconds
                );
                addColumn(
                    "Max delay (s)",
                    Double.class,
                    AudioChirp::getRegionMaxDelaySeconds,
                    AudioChirp::setRegionMaxDelaySeconds
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

        ColorCellEditor colorCellEditor = new ColorCellEditor();

        setDefaultEditor(Color.class, colorCellEditor);
        setDefaultRenderer(Color.class, colorCellEditor);

        getColumnModel().getColumn(0).setPreferredWidth(24);
        getColumnModel().getColumn(0).setResizable(false);

        getColumnModel().getColumn(2).setPreferredWidth(300);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    var rowIndex = rowAtPoint(e.getPoint());
                    popupFactory.createPopup(rowIndex)
                        .show(SpatialRegionTable.this, e.getX(), e.getY());
                }
            }
        });
    }

    interface PopupFactory {
        JPopupMenu createPopup(int rowIndex);
    }
}
