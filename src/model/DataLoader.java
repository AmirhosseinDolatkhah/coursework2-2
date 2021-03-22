package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class DataLoader {
    public static DataHandler loadCSV(String path, boolean optimized) throws FileNotFoundException {
        if (path == null || path.isEmpty())
            return new DataFrame();
        var file = new File(path);
        var dataFrame = optimized ? new OptimizedDataFrame(file) : new DataFrame();
        if (optimized)
            return dataFrame;
        var scanner = new Scanner(file);
        var columnNames = scanner.nextLine().split(",");
        for (var name : columnNames)
            dataFrame.addColumn(new Column(name));
        while (scanner.hasNext()) {
            var tuple = scanner.nextLine().split(",");
            int counter = 0;
            for (var cell : tuple)
                dataFrame.addValue(columnNames[counter++], cell == null || cell.isEmpty() ? "null" : cell);
        }
        return dataFrame;
    }
}
