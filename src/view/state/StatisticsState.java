package view.state;

import view.GraphPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public abstract class StatisticsState extends JPanel {
    private GraphPanel gp;

    public StatisticsState(String tableName) {
        super(new BorderLayout());
        init(tableName);
    }

    private void init(String tableName) {
        var backButton = new JButton("Back");
        backButton.addActionListener(e -> backAction());
        var wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        wrapper.add(backButton);
        add(wrapper, BorderLayout.SOUTH);

        var columns = new JComboBox<String>();
        var cols = getColumns(tableName);
        gp = new GraphPanel(tableName + ": " + cols[0], getStatisticsOf(tableName, cols[0]));
        for (var s : cols)
            columns.addItem(s);
        add(new JScrollPane(gp), BorderLayout.CENTER);

        var wrapper2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper2.add(columns);
        add(wrapper2, BorderLayout.NORTH);

        var txtArea = new JTextArea();
        add(new JScrollPane(txtArea), BorderLayout.EAST);
        int counter = 0;
        for (var i : getStatisticsOf(tableName, cols[0]).values())
            counter += i;
        for (var kv : getStatisticsOf(tableName, cols[0]).entrySet())
            txtArea.append(kv.getKey() + " = " + kv.getValue() + "  (" + Math.round((double) kv.getValue() / counter * 100) + " %)\n");
        columns.addActionListener(e -> {
            gp.setName(tableName + ": " + columns.getSelectedItem());
            var values = getStatisticsOf(tableName, (String) columns.getSelectedItem());
            gp.setValues(values);
            txtArea.setText("");
            int all = 0;
            for (var i : values.values())
                all += i;
            for (var kv : values.entrySet())
                txtArea.append(kv.getKey() + " = " + kv.getValue() + "  (" + Math.round((double) kv.getValue() / all * 100) + " %)\n");
        });
    }

    protected abstract String[] getColumns(String name);
    protected abstract Map<String, Integer> getStatisticsOf(String tableName, String col);
    protected abstract void backAction();
}
