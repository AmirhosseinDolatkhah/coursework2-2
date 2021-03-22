package view.state;

import view.CloseableTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class TableState extends JPanel {
    private CloseableTabbedPane tabbedPane;

    public TableState() {
        super(new BorderLayout());
        init();
    }

    private void init() {
        tabbedPane = new CloseableTabbedPane(() -> {
//            closeTabAction(getSelectedTitle());
            if (tabbedPane.getTabCount() == 0)
                noTabAction();
        });
        add(tabbedPane.get(), BorderLayout.CENTER);
        var addTable = new JButton("Add New Table");
        addTable.addActionListener(e -> addTableAction());
        var wrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        wrapper.add(addTable);
        add(wrapper, BorderLayout.SOUTH);

        var changeView = new JButton("Change Table View");
        wrapper.add(changeView);
        changeView.addActionListener(e -> {
            var content = new JPanel(new BorderLayout());
            var list = new JList<JCheckBox>();
            var model = new DefaultListModel<JCheckBox>();
            list.setModel(model);
            var name = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
            var visibility = getColVisibility(name);
            int counter = 0;
            for (var col : getColumns(name))
                model.addElement(new JCheckBox(col, visibility[counter++]));
            list.setCellRenderer(
                    (list1, value, index, isSelected, cellHasFocus) -> list1.getModel().getElementAt(index));
            list.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (list.getSelectedIndex() >= 0) {
                        var cb = model.getElementAt(list.getSelectedIndex());
                        cb.setSelected(!cb.isSelected());
                        visibility[list.getSelectedIndex()] = cb.isSelected();
                        list.repaint();
                        list.revalidate();
                    }
                }
            });
            content.add(list);
            JOptionPane.showMessageDialog(null, content, "Please choose which column should be visible", JOptionPane.PLAIN_MESSAGE);
            viewChangeAction(name, visibility);
        });

        var statistics = new JButton("Statistics and Graphs");
        wrapper.add(statistics);
        statistics.addActionListener(e -> statisticAction(getSelectedTitle()));

        var toJson = new JButton("To JSON Converter");
        wrapper.add(toJson);
        toJson.addActionListener(e -> toJsonState());
    }

    public CloseableTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public String getSelectedTitle() {
        if (tabbedPane.getSelectedIndex() < 0)
            return "Nothing is Selected";
        return tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    }

    protected abstract void noTabAction();
    protected abstract void addTableAction();
    protected abstract String[] getColumnModel(String name);
    protected abstract void viewChangeAction(String name, boolean[] visibility);
    protected abstract boolean[] getColVisibility(String name);
    protected abstract String[] getColumns(String name);
    protected abstract void closeTabAction(String name);
    protected abstract void statisticAction(String name);
    protected abstract void toJsonState();
}
