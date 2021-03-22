package model;

import java.util.ArrayList;
import java.util.HashMap;

public class Column {
    private final String name;
    private final ArrayList<String> rows;

    public Column(String name) {
        this.name = name;
        rows = new ArrayList<>();
    }

    public int getSize() {
        return rows.size();
    }

    public String getRowValue(int row) {
        if (row >= rows.size())
            return null;
        return rows.get(row);
    }

    public void setRowValue(int row, String newValue) {
        rows.set(row, newValue);
    }

    public void addRowValue(String newRowValue) {
        rows.add(newRowValue);
    }

    public String getName() {
        return name;
    }

    public String[] values() {
        return rows.toArray(new String[0]);
    }

    public HashMap<String, Integer> getStatisticalMap() {
        var res = new HashMap<String, Integer>();
        for (var r : rows)
            res.put(r, res.getOrDefault(r, 0) + 1);
        return res;
    }
}
