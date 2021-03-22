package controller;

import model.JSONWriter;
import model.Model;
import model.OptimizedDataFrame;
import view.WindowFrame;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Controller {
    private final static WindowFrame windowFrame;
    private final static Map<String, Model> models;

    static {
        models = new HashMap<>();
        windowFrame = new WindowFrame() {

            @Override
            protected String[] getColumnModel(String name) {
                return models.get(name).getColumnModel();
            }

            @Override
            protected String[][] getRowModel(String name) {
                return models.get(name).getRowModel();
            }

            @Override
            protected void addModel(File file, boolean optimized) {
                models.put(file.getName(), new Model(file, optimized));
            }

            @Override
            protected void removeModel(String name) {
                models.remove(name);
            }

            @Override
            protected boolean[] getColVisibility(String name) {
                return models.get(name).getColumnVisibility();
            }

            @Override
            protected void setColVisibility(String name, boolean[] visibility) {
                models.get(name).setColumnVisibility(visibility);
            }

            @Override
            protected String[] getColumns(String name) {
                return models.get(name).getColumns();
            }

            @Override
            protected List<Integer> getSearchResultIndexes(String tableName, String column, String key) {
                return models.get(tableName).search(column, key);
            }

            @Override
            protected Map<String, Integer> getStatisticsOf(String tableName, String col) {
                return models.get(tableName).getStatisticOf(col);
            }

            @Override
            protected String getJSON(String tableName) {
                return JSONWriter.dataFrameToJSON(models.get(tableName).getDataHandler());
            }

            @Override
            protected void saveJSON(String path, String tableName) {
                try {
                    JSONWriter.dataFrameToJSONFile(models.get(tableName).getDataHandler(), path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected boolean isOptimized(String name) {
                return models.get(name).isOptimized();
            }

            @Override
            protected void setRangeInOptimizedDataFrame(String name, int from, int to) {
                models.get(name).loadPartially(from, to);
            }

            @Override
            protected int getStartIndex(String name) {
                return models.get(name).getStartIndex();
            }
        };
    }

    public static void run() {
        SwingUtilities.invokeLater(windowFrame);
    }
}
