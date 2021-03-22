package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class Model {
    private final DataHandler dataHandler;
    private boolean[] columnVisibility;
    private final boolean isOptimized;

    public Model(File file, boolean optimized) {
        if (file.getName().endsWith(".csv")) {
            try {
                dataHandler = DataLoader.loadCSV(file.getPath(), optimized);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Not Found csv File");
            }
        } else if (file.getName().endsWith(".json")) {
            try {
                dataHandler = JSONReader.readFromJSON(file.getPath(), optimized);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Not Found json File");
            }
        } else {
            throw new RuntimeException("Not appropriate format");
        }
        columnVisibility = new boolean[dataHandler.getColumnCount()];
        isOptimized = optimized;
        for (int i = 0; i < dataHandler.getColumnCount(); i++)
            columnVisibility[i] = true;
    }

    public String[] getColumns() {
        return dataHandler.getColumnNames().toArray(new String[0]);
    }

    public String[] getColumnModel() {
        var res = new ArrayList<String>();
        for (int i = 0; i < dataHandler.getColumnCount(); i++)
            if (columnVisibility[i])
                res.add(dataHandler.getColumnNames().get(i));
        return res.toArray(new String[0]);
    }

    public String[][] getRowModel() {
        var res = new String[dataHandler.getRowCount()][];
        for (int i = 0; i < dataHandler.getRowCount(); i++) {
            var row = new ArrayList<String>();
            for (int j = 0; j < dataHandler.getColumnCount(); j++)
                if (columnVisibility[j])
                    row.add(dataHandler.getValueAt(i, j));
            res[i] = row.toArray(new String[0]);
        }
        return res;
    }

    public boolean[] getColumnVisibility() {
        return columnVisibility;
    }

    public void setColumnVisibility(boolean[] columnVisibility) {
        this.columnVisibility = columnVisibility;
    }

    public List<Integer> search(String column, String key) {
        var col = dataHandler.getColumnByName(column).values();
        key = key.toLowerCase();
        var res = new ArrayList<Integer>();
        for (int i = 0; i < col.length; i++)
            if (col[i].toLowerCase().contains(key))
                res.add(i+1);
        return res;
    }

    public HashMap<String, Integer> getStatisticOf(String columnName) {
        return dataHandler.getStatistical(columnName);
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }

    public boolean isOptimized() {
        return isOptimized;
    }
}
