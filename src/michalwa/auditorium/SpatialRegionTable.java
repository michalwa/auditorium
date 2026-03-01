package michalwa.auditorium;

import javax.swing.JTable;

class SpatialRegionTable extends JTable {
    private SimpleTableModel<SpatialRegion<Audio>> model = new SimpleTableModel<>() {{
        addColumn("Type", String.class, r -> r.getData().getTypeName());
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
        getColumnModel().getColumn(1).setPreferredWidth(300);
    }

    public void addRegion(SpatialRegion<Audio> region) {
        model.addRow(region);
        revalidate();
        repaint();
    }
}
