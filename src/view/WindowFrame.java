package view;

import view.state.NoTableState;
import view.state.StatisticsState;
import view.state.TableState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class WindowFrame extends MainFrame {
    private TableState tableState;

    public WindowFrame() {
        init();
    }

    private void init() {
        if (isVisible())
            return;
        handleTablesTab();
        handleNoTablePanel();
        setState("noTable");
    }

    private void handleTablesTab() {
        // table = key
        tableState = new TableState() {
            @Override
            protected void noTabAction() {
                setState("noTable");
            }

            @Override
            protected void addTableAction() {
                var f = chooseFileHandler();
                addModel(f, f.length() > 1_000_000);
                addTable(f.getName());
                repaint();
                revalidate();
            }

            @Override
            protected String[] getColumnModel(String name) {
                return WindowFrame.this.getColumnModel(name);
            }

            @Override
            protected void viewChangeAction(String name, boolean[] visibility) {
                var tabbedPane = tableState.getTabbedPane();
                tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
                setColVisibility(name, visibility);
                addTable(name);
                tabbedPane.repaint();
                tabbedPane.revalidate();
            }

            @Override
            protected boolean[] getColVisibility(String name) {
                return WindowFrame.this.getColVisibility(name);
            }

            @Override
            protected String[] getColumns(String name) {
                return WindowFrame.this.getColumns(name);
            }

            @Override
            protected void closeTabAction(String name) {
                removeModel(name);
            }

            @Override
            protected void statisticAction(String name) {
                setContentPane(new StatisticsState(name) {
                    @Override
                    protected String[] getColumns(String name) {
                        return WindowFrame.this.getColumns(name);
                    }

                    @Override
                    protected Map<String, Integer> getStatisticsOf(String tableName, String col) {
                        return WindowFrame.this.getStatisticsOf(tableName, col);
                    }

                    @Override
                    protected void backAction() {
                        setState("table");
                    }
                });

                repaint();
                revalidate();
            }

            @Override
            protected void toJsonState() {
                var panel = new JPanel(new BorderLayout());
                var txtArea = new JTextArea();
                txtArea.setEditable(false);
                txtArea.setLineWrap(true);
                txtArea.setText(getJSON(tableState.getSelectedTitle()));
                var back = new JButton("Back");
                back.addActionListener(e -> setState("table"));
                var save = new JButton("Save to File");
                save.addActionListener(e -> saveJSON(JOptionPane.showInputDialog("Please enter name of file: "), tableState.getSelectedTitle()));
                var wrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                wrapper.add(back);
                wrapper.add(save);
                panel.add(new JScrollPane(txtArea), BorderLayout.CENTER);
                panel.add(wrapper, BorderLayout.SOUTH);
                setContentPane(panel);
                repaint();
                revalidate();
                panel.repaint();
                panel.revalidate();
                repaint();
            }
        };
        addState("table", tableState);
    }

    private void handleNoTablePanel() {
        // noTable = key
        var noTableState = new NoTableState() {
            @Override
            protected void addTableAction() {
                var f = chooseFileHandler();
                addModel(f, f.length() > 1_000_000);
                if (getRowModel(f.getName()).length == 0)
                    return;
                addTable(f.getName());
                setState("table");
                repaint();
                revalidate();
            }
        };
        addState("noTable", noTableState);
    }

    private File chooseFileHandler() {
        var fileChooser = new JFileChooser(".\\");
        fileChooser.setDialogTitle("Choose a .csv or .json file");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.showDialog(this, "Select");
        return fileChooser.getSelectedFile();
    }

    private void addTable(String name) {
        if (getRowModel(name).length == 0)
            return;
        var majorCM = getColumnModel(name);
        var majorRM = getRowModel(name);
        var cm = new String[majorCM.length + 1];
        cm[0] = "No.";
        System.arraycopy(majorCM, 0, cm, 1, majorCM.length);
        var rm = new String[majorRM.length][majorRM[0].length + 1];
        int startIndex = getStartIndex(name);
        for (int i = 0; i < majorRM.length; i++) {
            rm[i][0] = String.valueOf(startIndex + i);
            System.arraycopy(majorRM[i], 0, rm[i], 1, majorCM.length);
        }
        JTable dataTable = new JTable(new DefaultTableModel(rm, cm));
        dataTable.setRowHeight(40);
        var txtField = new JTextField();
        var columns = new JComboBox<String>();
        for (var col : getColumnModel(name))
            columns.addItem(col);
        var wrapper = new JPanel(new BorderLayout());
        wrapper.add(columns, BorderLayout.WEST);
        wrapper.add(txtField, BorderLayout.CENTER);
        var indexResult = new JComboBox<Integer>();
        var label = new JLabel("========> Search Result Indexes: ", JLabel.RIGHT);
        txtField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                indexResult.removeAllItems();
                if (txtField.getText().isEmpty())
                    return;
                var sr = getSearchResultIndexes(name, (String) columns.getSelectedItem(), txtField.getText());
                for (var i : sr)
                    indexResult.addItem(i - 1 + startIndex);
                label.setText("========> Search Result Indexes (" + sr.size() + " Items are found): ");
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (txtField.getText().trim().isEmpty())
                    label.setText("========> Search Result Indexes: ");
            }
        });
        var innerWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerWrapper.add(label);
        innerWrapper.add(indexResult);
        wrapper.add(innerWrapper, BorderLayout.EAST);
        var outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        outerWrapper.add(wrapper, BorderLayout.NORTH);
        tableState.getTabbedPane().add(name, outerWrapper);
        tableState.getTabbedPane().
                getTabComponentAt(tableState.getTabbedPane().getTabCount() - 1).
                setForeground(isOptimized(name) ? Color.GREEN : Color.BLUE);
        tableState.getTabbedPane().
                getTabComponentAt(tableState.getTabbedPane().getTabCount() - 1).
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        try {
                            var res = JOptionPane.showInputDialog(
                                    "Enter row index in (from, to) format. Both inclusive(Start from 1).")
                                    .replace(" ", "").split(",");
                            setRangeInOptimizedDataFrame(name, Integer.parseInt(res[0]), Integer.parseInt(res[1]));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Something went wrong (Maybe input format or range).",
                                    "Sorry!", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        var tabbedPane = tableState.getTabbedPane();
                        tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
                        addTable(name);
                        tabbedPane.repaint();
                        tabbedPane.revalidate();
                    }
                });
    }

    protected abstract String[] getColumnModel(String name);
    protected abstract String[][] getRowModel(String name);
    protected abstract void addModel(File file, boolean optimized);
    protected abstract void removeModel(String name);
    protected abstract boolean[] getColVisibility(String name);
    protected abstract void setColVisibility(String name, boolean[] visibility);
    protected abstract String[] getColumns(String name);
    protected abstract List<Integer> getSearchResultIndexes(String tableName, String column, String key);
    protected abstract Map<String, Integer> getStatisticsOf(String tableName, String col);
    protected abstract String getJSON(String tableName);
    protected abstract void saveJSON(String path, String tableName);
    protected abstract boolean isOptimized(String name);
    protected abstract void setRangeInOptimizedDataFrame(String name, int from, int to);
    protected abstract int getStartIndex(String name);
}
