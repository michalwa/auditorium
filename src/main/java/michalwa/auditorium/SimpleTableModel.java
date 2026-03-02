package michalwa.auditorium;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.swing.table.AbstractTableModel;

class SimpleTableModel<TRow> extends AbstractTableModel {
    private List<Column<?>> columns = new ArrayList<>();
    private List<TRow> rows = new ArrayList<>();

    public <TValue> void addColumn(
        String name,
        Class<TValue> klass,
        Function<TRow, TValue> getter
    ) {
        columns.add(new Column<>(name, klass, getter, null));
    }

    public <TValue> void addColumn(
        String name,
        Class<TValue> klass,
        Function<TRow, TValue> getter,
        BiConsumer<TRow, TValue> setter
    ) {
        columns.add(new Column<>(name, klass, getter, setter));
    }

    public void addRow(TRow row) {
        rows.add(row);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns.get(columnIndex).getName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns.get(columnIndex).getKlass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return getValueAt(rowIndex, columnIndex) != null && columns.get(columnIndex).isEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        TRow row = rows.get(rowIndex);
        return columns.get(columnIndex).getValue(row);
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        TRow row = rows.get(rowIndex);
        columns.get(columnIndex).setValue(row, value);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    class Column<TValue> {
        private String name;
        private Class<TValue> klass;
        private Function<TRow, TValue> getter;
        private BiConsumer<TRow, TValue> setter;

        Column(
            String name,
            Class<TValue> klass,
            Function<TRow, TValue> getter,
            BiConsumer<TRow, TValue> setter
        ) {
            this.name = name;
            this.klass = klass;
            this.getter = getter;
            this.setter = setter;
        }

        public String getName() {
            return name;
        }

        public Class<TValue> getKlass() {
            return klass;
        }

        public boolean isEditable() {
            return setter != null;
        }

        public TValue getValue(TRow row) {
            return getter.apply(row);
        }

        public void setValue(TRow row, Object value) {
            if (setter == null)
                throw new IllegalStateException("Column " + name + " is not editable");

            setter.accept(row, klass.cast(value));
        }
    }
}
