package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class JSONReader {
    public static DataHandler readFromJSON(String filePath, boolean optimized) throws FileNotFoundException {
        var file = new File(filePath);
        var res = optimized ? new OptimizedDataFrame(file) : new DataFrame();
        if (optimized)
            return res;
        var scanner = new Scanner(file);
        boolean isColSet = false;
        while (scanner.hasNext()) {
            var line = scanner.nextLine();
            if (line.trim().startsWith("[") || line.trim().startsWith("{") || line.trim().startsWith("]"))
                continue;
            if (line.trim().startsWith("}") || line.trim().startsWith("},")) {
                isColSet = true;
                continue;
            }
            var kv = line.trim().replace("\"", "").split(": ");
            if (!isColSet)
                res.addColumn(new Column(kv[0]));
            try {
                res.addValue(kv[0], kv[1].endsWith(",") ? kv[1].substring(0, kv[1].length() - 1) : kv[1]);
            } catch (Exception ignore) {}
        }
        scanner.close();
        return res;
    }
}
