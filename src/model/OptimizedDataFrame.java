package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class OptimizedDataFrame implements DataHandler {
    private final ArrayList<Column> columns;
    private final File file;
    private final boolean isJson;

    public OptimizedDataFrame(File file) {
        columns = new ArrayList<>();
        this.file = file;
        isJson = file.getName().toLowerCase().endsWith(".json");
        try {
            loadPartially(1, 100);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found");
        }
    }

    public void loadPartially(int from, int to) throws FileNotFoundException {
        boolean isColSet = false;
        int counter = 0;
        var scanner = new Scanner(file);
        columns.clear();
        String[] columnNames = new String[0];
        if (!isJson) {
            columnNames = scanner.nextLine().split(",");
            for (var name : columnNames)
                addColumn(new Column(name));
        }
        while (scanner.hasNext() && counter <= to) {
            if (counter < from) {
                counter++;
                continue;
            }
            if (isJson) {
                var line = scanner.nextLine();
                if (line.trim().startsWith("[") || line.trim().startsWith("{") || line.trim().startsWith("]"))
                    continue;
                if (line.trim().startsWith("}") || line.trim().startsWith("},")) {
                    isColSet = true;
                    counter++;
                    continue;
                }
                var kv = line.trim().replace("\"", "").split(": ");
                if (!isColSet)
                    addColumn(new Column(kv[0]));
                try {
                    addValue(kv[0], kv[1].endsWith(",") ? kv[1].substring(0, kv[1].length() - 1) : kv[1]);
                } catch (Exception ignore) {
                }
            } else {
                var tuple = scanner.nextLine().split(",");
                int counter2 = 0;
                for (var cell : tuple)
                    addValue(columnNames[counter2++], cell == null || cell.isEmpty() ? "null" : cell);
                counter++;
            }
        }
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
