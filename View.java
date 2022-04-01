import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class View extends JPanel {
    private Controller c;
    private static final Color[] colorsArray = { Color.blue, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.RED,
            new Color(102, 51, 0), Color.BLACK, new Color(102, 0, 153), Color.PINK, new Color(51, 204, 255),
            new Color(0, 102, 0) };

    public View(Model m) {
        c = new Controller(m, this);
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        JLabel iterateLabel = new JLabel("Iterasyon: ");
        JTextField iterations = new JTextField(10);
        iterateLabel.add(iterations);
        buttons.add(iterateLabel);
        buttons.add(iterations);

        JLabel kCountLabel = new JLabel("K sayisi:(Center)");
        Integer[] kCounts = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        JComboBox<Integer> kCountDropDown = new JComboBox<>(kCounts);
        kCountLabel.add(kCountDropDown);
        buttons.add(kCountLabel);
        buttons.add(kCountDropDown);

        JButton fileSelect = new JButton("Dosyadan sec");
        fileSelect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.fileSelection(buttons);
            }
        });

        JButton clusterButton = new JButton("K-Means Clustering");
        clusterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                c.kMeansCluster(buttons, iterations.getText());
            }
        });
        buttons.add(fileSelect);
        buttons.add(clusterButton);
        setLayout(new BorderLayout());
        add(buttons, BorderLayout.SOUTH);
    }

    public Dimension getDimension() {
        // Pencere boyutuna gore rastgele merkez yaratmak icin yazilmisti. Simdilik
        // sadece preffered size donuyor.
        // return new Dimension(windowSize)
        return getPreferredSize();
    }

    public Dimension getPreferredSize() {
        return new Dimension(1280, 720);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        ArrayList<Controller.CenterPoint> centerPoints = c.getCenterPoints();

        for (int i = 0; i < centerPoints.size(); i++) {
            Controller.CenterPoint cp = centerPoints.get(i);
            g.setColor(colorsArray[10]);
            int xCenterPoint = (int)cp.getX();
            int yCenterPoint = (int)cp.getY();
            g.fillOval(xCenterPoint, yCenterPoint, 12, 12);
            ArrayList<Controller.Point> currentPointsList = centerPoints.get(i).getPointsList();
            for (int j = 0; j < currentPointsList.size(); j++) {
                Controller.Point p = currentPointsList.get(j);
                g.setColor(p.getColor());
                int xPoint = (int)p.getX();
                int yPoint = (int)p.getY();
                g.fillOval(xPoint, yPoint, 7, 7);
            }
        }
    }
}
