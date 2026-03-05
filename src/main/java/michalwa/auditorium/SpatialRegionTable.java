package michalwa.auditorium;

import java.awt.Color;
import java.util.List;
import javax.swing.JTable;
import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.SpatialAudio;

class SpatialRegionTable extends JTable {
    SpatialRegionTable(List<SpatialRegion<SpatialAudio>> regions) {
        setModel(new SimpleTableModel<>(regions) {
            {
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
                addColumn("Volume", Double.class, r -> r.getData().getEffectiveVolume());
            }
        });

        ColorCellEditor colorCellEditor = new ColorCellEditor();

        setDefaultEditor(Color.class, colorCellEditor);
        setDefaultRenderer(Color.class, colorCellEditor);

        getColumnModel().getColumn(2).setPreferredWidth(300);
    }
}
