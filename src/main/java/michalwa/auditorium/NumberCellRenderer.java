package michalwa.auditorium;

import java.util.Locale;
import javax.swing.table.DefaultTableCellRenderer;

class NumberCellRenderer extends DefaultTableCellRenderer {
    private String formatNumber(Number n) {
        if (n == null) return "";

        return String.format(Locale.US, "%.02f", n.floatValue());
    }

    @Override
    protected void setValue(Object value) {
        super.setValue(formatNumber((Number)value));
    }
}
