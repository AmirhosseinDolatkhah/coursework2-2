package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DataFrame implements DataHandler {
    private final ArrayList<Column> columns;

    public DataFrame() {
        columns = new ArrayList<>();
    }

    @Override
    public void addColumn(Column column) {
        columns.add(column);
    }

    @Override
    public List<String> getColumnNames() {
        return columns.stream().map(Column::getName).collect(Collectors.toList());
    }

    @Override
    public int getRowCount() {
        return columns.isEmpty() ? 0 : columns.get(0).getSize();
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getValue(String columnName, int row) {
        return getColumnByName(columnName).getRowValue(row);
    }

    @Override
    public void putValue(String columnName, int row, String value) {
        getColumnByName(columnName).setRowValue(row, value);
    }

    @Override
    public void addValue(String columnName, String newValue) {
        getColumnByName(columnName).addRowValue(newValue);
    }

    @Override
    public String getValueAt(int row, int col) {
        return columns.get(col).getRowValue(row);
    }

    @Override
    public String[][] getAllCells() {
        var rowCount = getRowCount();
        var colCount = getColumnCount();
        var cells = new String[rowCount][colCount];
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < colCount; j++)
                cells[i][j] = getValueAt(i, j);
        return cells;
    }

    @Override
    public HashMap<String, Integer> getStatistical(String columnName) {
        return getColumnByName(columnName).getStatisticalMap();
    }

    @Override
    public Column getColumnByName(String columnName) {
        for (var column : columns)
            if (column.getName().equalsIgnoreCase(columnName))
                return column;
        throw new RuntimeException("Invalid Column Name.");
    }
}
