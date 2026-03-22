package michalwa.auditorium;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import michalwa.auditorium.playback.AudioChirp;
import michalwa.auditorium.playback.AudioLoop;
import michalwa.auditorium.playback.SpatialAudio;

class App extends JFrame implements Runnable {
    List<SpatialRegion<SpatialAudio>> regions = new ArrayList<>();
    SpatialSlider slider;
    SpatialRegionTable table;

    private void addRegion(SpatialRegion<SpatialAudio> region) {
        regions.add(region);
        region.getData().initialize();
        updateAudioLevels();
        slider.repaint();
        table.revalidate();
    }

    private void clearRegions() {
        for (var region : regions) region.getData().kill();
        regions.clear();
        slider.repaint();
        table.revalidate();
    }

    private void removeRegion(int index) {
        regions.get(index).getData().kill();
        regions.remove(index);
        slider.repaint();
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
                if (e.getType() == TableModelEvent.UPDATE) {
                    updateAudioLevels();
                    slider.repaint();
                }
            }
        });

        var tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(400, 100));

        add(slider, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);

        pack();

        setTitle("auditorium " + getClass().getPackage().getImplementationVersion());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
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
        }
    }

    class TablePopupMenu extends JPopupMenu {
        TablePopupMenu(int rowIndex) {
            add(new JMenuItem("Delete")).addActionListener(e -> { removeRegion(rowIndex); });

            add(new JMenuItem("Delete all")).addActionListener(e -> {
                var title = ((JMenuItem)e.getSource()).getText();
                var choice = JOptionPane.showConfirmDialog(
                    getInvoker(),
                    "Delete all regions?",
                    title,
                    JOptionPane.YES_NO_OPTION
                );

                if (choice == 0) clearRegions();
            });
        }
    }
}
