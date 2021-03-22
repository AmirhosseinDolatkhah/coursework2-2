package view.state;

import javax.swing.*;
import java.awt.*;

public abstract class NoTableState extends JPanel {

    public NoTableState() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        var addTable = new JButton("Add a Table");
        addTable.addActionListener(e -> addTableAction());
        var p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(addTable);
        add(p, BorderLayout.SOUTH);
        add(new JLabel("Please add a .csv or .json file", SwingConstants.CENTER));
    }

    protected abstract void addTableAction();
}
