package view;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphPanel extends JPanel {
    private Map<String, Integer> values;
    private String name;
    private Color[] colors;

    public GraphPanel(String name, Map<String, Integer> values) {
        super(new BorderLayout());
        this.values = values;
        this.name = name;
        colors = new Color[values.size()];
        for (int i = 0; i < values.size(); i++)
            colors[i] = new Color((int) (Math.random() * Integer.MAX_VALUE));
    }

    @Override
    public void setName(String name) {
        this.name = name;
        repaint();
    }

    public void setValues(Map<String, Integer> values) {
        this.values = values;
        colors = new Color[values.size()];
        for (int i = 0; i < values.size(); i++)
            colors[i] = new Color((int) (Math.random() * Integer.MAX_VALUE));
        repaint();
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        if (values.size() > 200) {
//            g.setColor(Color.RED);
//            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 14));
//            g.drawString("Sorry, Graphing is not possible for this column due to variations", 20, getHeight() / 2);
//            g.dispose();
//            return;
//        }
//        g.setColor(Color.DARK_GRAY.darker());
//        var width = getWidth();
//        var height = getHeight();
//        g.fillRect(0, 0, width, height);
//        g.setColor(Color.GRAY);
//        g.drawLine(10, 40, 10, height - 30);
//        g.drawLine(10, height - 30, width - 10, height - 30);
//        g.setColor(Color.GREEN);
//        g.drawString(name, 20, 20);
//        var w = (width - 30) / values.size() - 7;
//        int offset = 20;
//        for (var kv : values.entrySet()) {
//            g.setColor(new Color((int) (Math.random() * Integer.MAX_VALUE)));
//            var h = height(kv.getKey());
//            g.fillRect(offset, height - h - 30, w, h);
//            g.setColor(Color.WHITE);
//            g.drawString(kv.getValue().toString(), offset + w / 2 - g.getFontMetrics().stringWidth(kv.getValue().toString()) / 2, height - h - 40);
//            g.drawString(kv.getKey(), offset + w / 2 - g.getFontMetrics().stringWidth(kv.getKey()) / 2, height - 15);
//            offset += w + 7;
//        }
//        g.dispose();
//    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.DARK_GRAY.darker());
        var width = getWidth();
        var height = getHeight();
        setPreferredSize(new Dimension(SwingUtilities.getWindowAncestor(this).getWidth() - 250, height));
        g.fillRect(0, 0, width, height);
        g.setColor(Color.GRAY);
        g.drawLine(10, 40, 10, height - 30);
        g.drawLine(10, height - 30, width - 10, height - 30);
        g.setColor(Color.GREEN);
        g.drawString(name, 20, 20);
        var w = Math.max(90, Math.min((width - 30) / values.size() - 7, 200));
        setPreferredSize(new Dimension((w + 7) * values.size() + 30, SwingUtilities.getWindowAncestor(this).getHeight() - 250));
        int offset = 20;
        int counter = 0;
        for (var kv : values.entrySet()) {
            g.setColor(colors[counter++]);
            var h = height(kv.getKey());
            g.fillRect(offset, height - h - 30, w, h);
            g.setColor(Color.WHITE);
            g.drawString(kv.getValue().toString(), offset + w / 2 - g.getFontMetrics().stringWidth(kv.getValue().toString()) / 2, height - h - 40);
            String tmp;
            g.drawString(tmp = kv.getKey().length() > 12 ? kv.getKey().substring(0, 12) + "..." : kv.getKey(), offset + w / 2 - g.getFontMetrics().stringWidth(tmp) / 2, height - 15);
            offset += w + 7;
        }
        var sp = (JScrollPane) getParent().getParent();
        sp.getVerticalScrollBar().repaint();
        sp.getHorizontalScrollBar().repaint();
        sp.getVerticalScrollBar().revalidate();
        sp.getHorizontalScrollBar().revalidate();
        g.dispose();
    }

    private int height(String key) {
        int num = 0;
        for (var v : values.values())
            num += v;
        return (getHeight() - 80) * values.get(key) / num;
    }
}
