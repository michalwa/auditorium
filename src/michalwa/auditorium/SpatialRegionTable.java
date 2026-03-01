package michalwa.auditorium;

import java.awt.Color;

import javax.swing.JTable;

import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.SpatialAudio;

class SpatialRegionTable extends JTable {
    private SimpleTableModel<SpatialRegion<SpatialAudio>> model = new SimpleTableModel<>() {{
        addColumn("Type", String.class, r -> r.getData().getTypeName());
        addColumn("Color", Color.class, r -> r.color, (r, v) -> r.color = v);
        addColumn("Name", String.class,
            r -> r.getData().getName(),
            (r, v) -> r.getData().setName(v));
        addColumn("X", Float.class,
            r -> r.centerX,
            (r, v) -> r.centerX = v);
        addColumn("Y", Float.class,
            r -> r.centerY,
            (r, v) -> r.centerY = v);
        addColumn("R", Float.class,
            r -> r.radius,
            (r, v) -> r.radius = v);
        addColumn("Min delay (s)", Float.class,
            r -> (r.getData() instanceof AudioChirp) ? ((AudioChirp)r.getData()).minDelaySeconds : null,
            (r, v) -> {
                if (r.getData() instanceof AudioChirp)
                    ((AudioChirp)r.getData()).minDelaySeconds = v;
            });
        addColumn("Max delay (s)", Float.class,
            r -> (r.getData() instanceof AudioChirp) ? ((AudioChirp)r.getData()).maxDelaySeconds : null,
            (r, v) -> {
                if (r.getData() instanceof AudioChirp)
                    ((AudioChirp)r.getData()).maxDelaySeconds = v;
            });
        addColumn("Volume", Float.class,
            r -> r.getData().getEffectiveVolume());
    }};

    SpatialRegionTable() {
        setModel(model);

        ColorCellEditor colorCellEditor = new ColorCellEditor();

        setDefaultEditor(Color.class, colorCellEditor);
        setDefaultRenderer(Color.class, colorCellEditor);

        getColumnModel().getColumn(2).setPreferredWidth(300);
    }

    public void addRegion(SpatialRegion<SpatialAudio> region) {
        model.addRow(region);
        revalidate();
        repaint();
    }
}
