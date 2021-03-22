package model;

import java.util.HashMap;
import java.util.List;

public interface DataHandler {
    void addColumn(Column column);
    List<String> getColumnNames();
    int getRowCount();
    int getColumnCount();
    String getValue(String columnName, int row);
    void putValue(String columnName, int row, String value);
    void addValue(String columnName, String newValue);
    String getValueAt(int row, int col);
    String[][] getAllCells();
    HashMap<String, Integer> getStatistical(String columnName);
    Column getColumnByName(String columnName);
}
