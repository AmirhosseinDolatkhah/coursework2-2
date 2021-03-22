package model;

import java.io.FileOutputStream;
import java.io.IOException;

public final class JSONWriter {
    public static String dataFrameToJSON(DataHandler dataHandler) {
        var cols = dataHandler.getColumnNames();
        var sb = new StringBuilder("[\n");
        var cells = dataHandler.getAllCells();
        for (var row : cells) {
            sb.append("\t{\n");
            for (int i = 0; i < row.length; i++)
                sb.append("\t  \"").append(cols.get(i)).append("\": \"").append(row[i]).append("\"").append(i == row.length - 1 ? "" : ",").append("\n");
            sb.append("\t}").append(row == cells[cells.length-1] ? "" : ",").append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void dataFrameToJSONFile(DataHandler dataHandler, String resultPath) throws IOException {
        var fos = new FileOutputStream(resultPath + (resultPath.endsWith(".json") ? "" : ".json"));
        fos.write(dataFrameToJSON(dataHandler).getBytes());
        fos.close();
    }
}
