package michalwa.auditorium;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.AudioLoop;
import michalwa.auditorium.playback.SpatialAudio;

class App extends JFrame implements Runnable {
    private static final SmoothingPreset[] SMOOTHING_PRESETS = new SmoothingPreset[] {
        new SmoothingPreset("Subtle", 0.3),
        new SmoothingPreset("Moderate", 0.2),
        new SmoothingPreset("Heavy", 0.1),
        new SmoothingPreset("Sluggish", 0.02) };

    List<SpatialRegion<SpatialAudio>> regions = new ArrayList<>();
    SpatialSlider slider;
    SpatialRegionTable table;

    record SmoothingPreset(String name, double speed) {}

    private void addRegion(SpatialRegion<SpatialAudio> region) {
        regions.add(region);
        region.getData().initialize();
        updateAudioLevels();
        table.revalidate();
    }

    private void clearRegions() {
        for (var region : regions) region.getData().kill();
        regions.clear();
        table.revalidate();
    }

    private void moveRegion(int i, int j) {
        if (i < 0 || j < 0 || i >= regions.size() || j >= regions.size()) return;

        regions.add(j, regions.remove(i));

        table.revalidate();
        table.repaint();
    }

    private void removeRegion(int index) {
        regions.get(index).getData().kill();
        regions.remove(index);
        table.revalidate();
    }

    @Override
    public void run() {
        slider = new SpatialSlider(regions, SliderPopupMenu::new);
        slider.setPreferredSize(new Dimension(400, 400));
        slider.addPropertyChangeListener("value", e -> updateAudioLevels());

        table = new SpatialRegionTable(regions, TablePopupMenu::new);
        table.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) updateAudioLevels();
            }
        });
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                for (var i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
                    regions.get(i).setSelected(table.isRowSelected(i));
                }
            }
        });

        var tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(400, 100));
        tableScrollPane.setComponentPopupMenu(new TablePopupMenu(-1));

        add(slider, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        pack();

        setTitle("auditorium " + getClass().getPackage().getImplementationVersion());
        setLocationRelativeTo(null);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (
                    JOptionPane.showConfirmDialog(
                        App.this,
                        "Are you sure you want to exit? Unsaved changes will be lost",
                        "Exit",
                        JOptionPane.OK_CANCEL_OPTION
                    ) == JOptionPane.OK_OPTION
                ) {
                    System.exit(0);
                }
            }
        });

        setVisible(true);
    }

    private void setAllRegionsVisible(boolean visible) {
        for (var region : regions) region.setVisible(visible);

        table.revalidate();
        table.repaint();
    }

    private void updateAudioLevels() {
        for (SpatialRegion<SpatialAudio> region : regions) {
            var squareDist = squareDistance(slider.getValue(), region.getCenter());
            var squareRadius = region.getSquareRadius();

            region.getData().setVolume(1.0f - (float)(squareDist / squareRadius));
        }

        table.repaint();
    }

    public static void main(String[] args) throws IOException {
        var logConfig = App.class.getClassLoader().getResourceAsStream("logging.properties");
        LogManager.getLogManager().readConfiguration(logConfig);

        SwingUtilities.invokeLater(new App());
    }

    private static double squareDistance(Point2D a, Point2D b) {
        var dx = a.getX() - b.getX();
        var dy = a.getY() - b.getY();
        return dx * dx + dy * dy;
    }

    class SliderPopupMenu extends JPopupMenu {
        SliderPopupMenu(Point2D value) {
            add(new JMenuItem("Add loop")).addActionListener(e -> {
                var data = FilePicker.loadAudio(AudioLoop::new);
                if (data != null) {
                    addRegion(new SpatialRegion<>(value, data));
                } else {
                    JOptionPane.showMessageDialog(
                        getInvoker(),
                        "Invalid file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            add(new JMenuItem("Add chirp")).addActionListener(e -> {
                var data = FilePicker.loadAudio(AudioChirp::new);
                if (data != null) {
                    addRegion(new SpatialRegion<>(value, data));
                } else {
                    JOptionPane.showMessageDialog(
                        getInvoker(),
                        "Invalid file(s)",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            add(new JMenuItem("Export"))
                .addActionListener(e -> { FilePicker.exportData(regions); });

            add(new JMenuItem("Import")).addActionListener(e -> {
                var data = FilePicker.importData();
                if (data != null) {
                    @SuppressWarnings("unchecked")
                    var importedRegions = (List<SpatialRegion<SpatialAudio>>)regions.getClass()
                        .cast(data);
                    for (var region : importedRegions) addRegion(region);
                } else {
                    JOptionPane.showMessageDialog(
                        getInvoker(),
                        "Invalid file",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            });

            addCheckbox(
                "Show all gizmos",
                slider.isShowAllGizmosEnabled(),
                slider::setShowAllGizmosEnabled
            );
            addCheckbox("Show guides", slider.isShowGuidesEnabled(), slider::setShowGuidesEnabled);
            addCheckbox(
                "Dynamic visualization",
                slider.isDynamicVisualizationEnabled(),
                slider::setDynamicVisualizationEnabled
            );

            var smoothingMenu = new JMenu("Smoothing");
            smoothingMenu.add(new JRadioButtonMenuItem("None", !slider.isSmoothingEnabled()))
                .addActionListener(e -> slider.setSmoothingEnabled(false));

            for (var preset : SMOOTHING_PRESETS) {
                smoothingMenu
                    .add(
                        new JRadioButtonMenuItem(
                            preset.name,
                            slider.isSmoothingEnabled()
                                && slider.getSmoothingSpeed() == preset.speed
                        )
                    )
                    .addActionListener(e -> {
                        slider.setSmoothingEnabled(true);
                        slider.setSmoothingSpeed(preset.speed);
                    });
            }

            add(smoothingMenu);
        }

        private void addCheckbox(String text, boolean state, Consumer<Boolean> setter) {
            add(new JCheckBoxMenuItem(text, state)).addActionListener(e -> setter.accept(!state));
        }
    }

    class TablePopupMenu extends JPopupMenu {
        TablePopupMenu(int rowIndex) {
            if (rowIndex >= 0) {
                add(new JMenuItem("Move up"))
                    .addActionListener(e -> { moveRegion(rowIndex, rowIndex - 1); });
                add(new JMenuItem("Move down"))
                    .addActionListener(e -> { moveRegion(rowIndex, rowIndex + 1); });
                add(new JMenuItem("Move to top"))
                    .addActionListener(e -> { moveRegion(rowIndex, 0); });
                add(new JMenuItem("Move to bottom")).addActionListener(e -> {
                    moveRegion(rowIndex, regions.size() - 1);
                });
            }

            add(new JMenuItem("Show all")).addActionListener(e -> setAllRegionsVisible(true));
            add(new JMenuItem("Hide all")).addActionListener(e -> setAllRegionsVisible(false));
            add(new JMenuItem("Clear selection")).addActionListener(e -> table.clearSelection());

            if (rowIndex >= 0) {
                add(new JMenuItem("Delete")).addActionListener(e -> { removeRegion(rowIndex); });
            }

            add(new JMenuItem("Delete all")).addActionListener(e -> {
                var title = ((JMenuItem)e.getSource()).getText();
                if (
                    JOptionPane.showConfirmDialog(
                        getInvoker(),
                        "Delete all regions?",
                        title,
                        JOptionPane.OK_CANCEL_OPTION
                    ) == JOptionPane.OK_OPTION
                ) {
                    clearRegions();
                }
            });
        }
    }
}
