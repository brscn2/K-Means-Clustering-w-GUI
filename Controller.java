import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class Controller {
    private static final Color[] colorsArray = { Color.blue, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.RED,
            new Color(102, 51, 0), Color.BLACK, new Color(102, 0, 153), Color.PINK, new Color(51, 204, 255),
            new Color(0, 102, 0) };
    private ArrayList<CenterPoint> centerPoints = new ArrayList<CenterPoint>();
    private boolean isValidFile = true;
    private boolean isValidIteration = true;
    private Model model;
    private View view;

    public Controller(Model m, View v) {
        model = m;
        view = v;
    }

    public int getCenterPointsSize() {
        return centerPoints.size();
    }

    public ArrayList<CenterPoint> getCenterPoints() {
        return centerPoints;
    }

    public void fileSelection(JPanel buttons) {
        JFileChooser fileSelector = new JFileChooser("./");
        int approveCheck = fileSelector.showOpenDialog(view);

        if (JFileChooser.APPROVE_OPTION == approveCheck) {
            File chosenFile = fileSelector.getSelectedFile();
            String fileExtension = chosenFile.toString();

            int indexOfLastDot = 0;
            for (int i = 0; i < fileExtension.length(); i++) {
                if (fileExtension.charAt(i) == '.') {
                    indexOfLastDot = i;
                }
            }
            fileExtension = fileExtension.substring(indexOfLastDot + 1, fileExtension.length());

            if (fileExtension.equals("csv")) {
                isValidFile = true;
                model.setCSVFile(chosenFile);
            } else {
                isValidFile = false;
                JOptionPane.showMessageDialog(view, "Yalnizca CSV dosyasi secilmelidir!",
                        "Hatali Dosya Secimi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void kMeansCluster(JPanel buttons, String iterations) {
        if(model.getCSVFile() == null) {
            isValidFile = false;
        }
        
        try {
            Integer.valueOf(iterations);
            isValidIteration = true;
        } catch (NumberFormatException ne) {
            isValidIteration = false;
        }

        if (isValidFile && isValidIteration) {
            centerPoints.clear();
            model.setIterationCount(Integer.valueOf(iterations));
            model.setKCountValue(
                    Integer.valueOf(((JComboBox<Integer>) buttons.getComponent(3)).getSelectedItem().toString()));

            initializeWindow();

            for (int i = 1; i < model.getIterationCount(); i++) {
                clusterMethod();
                if (i == model.getIterationCount() - 1) {
                    view.repaint();
                }
            }
        } else {
            if (!isValidFile && !isValidIteration) {
                JOptionPane.showMessageDialog(view,
                        "Iterasyon bolumune bir tamsayi girmeli ve dosya olarak bir csv dosyasi secmelisiniz!",
                        "Hata", JOptionPane.ERROR_MESSAGE);
            } else if (!isValidFile) {
                JOptionPane.showMessageDialog(view, "CSV dosyasi secmediginiz halde islem yapamazsiniz!",
                        "Hatali Dosya Secimi", JOptionPane.ERROR_MESSAGE);
            } else if (!isValidIteration) {
                JOptionPane.showMessageDialog(view, "Iterasyon bolumune bir tamsayi girilmelidir!",
                        "Hatali Iterasyon Girisi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void initializeWindow() {
        Scanner fileInput = null;
        for (int i = 0; i < model.getKCountValue(); i++) {
            CenterPoint currentPoint = new CenterPoint();
            boolean overlapping = false;
            for (int j = 0; j < centerPoints.size(); j++) {
                if (currentPoint.getX() == centerPoints.get(j).getX()
                        && currentPoint.getY() == centerPoints.get(j).getY()) {
                    overlapping = true;
                }
            }
            if (!overlapping) {
                centerPoints.add(currentPoint);
            }
        }

        try {
            fileInput = new Scanner(new FileInputStream(model.getCSVFile()));
        } catch (FileNotFoundException e) {
            System.out.println("Problem occured while opening file for input stream.");
            System.exit(0);
        }

        while (fileInput.hasNextLine()) {
            String currentCoords = fileInput.nextLine();

            String xCoordinate = "";
            String yCoordinate = "";
            int i = 0;
            while (currentCoords.charAt(i) != ',') {
                xCoordinate += currentCoords.charAt(i);
                i++;
            }
            i++;
            while (i < currentCoords.length()) {
                yCoordinate += currentCoords.charAt(i);
                i++;
            }

            Point currentPoint = new Point(Double.valueOf(xCoordinate), Double.valueOf(yCoordinate));

            CenterPoint closest = null;
            double minDistance = Double.MAX_VALUE;
            for (int j = 0; j < centerPoints.size(); j++) {
                if (currentPoint.getDistance(centerPoints.get(j)) < minDistance) {
                    closest = centerPoints.get(j);
                    minDistance = currentPoint.getDistance(closest);
                }
            }

            closest.addToPoints(currentPoint);
        }

        fileInput.close();

        for (int i = 0; i < centerPoints.size(); i++) {
            centerPoints.get(i).setColorOfPoints(colorsArray[i]);
        }

        view.repaint();
    }

    private void clusterMethod() {
        for (int i = 0; i < model.getKCountValue(); i++) {
            CenterPoint currentPoint = generateCenter(centerPoints.get(i));
            centerPoints.set(i, currentPoint);
        }
    }

    private CenterPoint generateCenter(CenterPoint cp) {
        ArrayList<Point> pointsTempList = cp.getPointsList();
        double sumOfXCoords = 0;
        double sumOfYCoords = 0;

        for (int i = 0; i < pointsTempList.size(); i++) {
            sumOfXCoords += pointsTempList.get(i).getX();
            sumOfYCoords += pointsTempList.get(i).getY();
        }

        double newCenterPointX;
        double newCenterPointY;

        if (pointsTempList.size() != 0) {
            newCenterPointX = sumOfXCoords / pointsTempList.size();
        } else {
            newCenterPointX = cp.getX();
        }

        if (pointsTempList.size() != 0) {
            newCenterPointY = sumOfYCoords / pointsTempList.size();
        } else {
            newCenterPointY = cp.getY();
        }

        CenterPoint toReturn = new CenterPoint(newCenterPointX, newCenterPointY, pointsTempList);

        return toReturn;
    }

    static class CenterPoint {
        private double x, y;
        private Color colorOfPoints;
        private ArrayList<Point> points;

        public CenterPoint() {
            x = (int)(Math.random() * 1280);
            y = (int)(Math.random() * 720);
            points = new ArrayList<Point>();
        }

        public CenterPoint(double xc, double yc, ArrayList<Point> list) {
            x = xc;
            y = yc;
            points = list;
        }

        public void setColorOfPoints(Color c) {
            colorOfPoints = c;
            for (int i = 0; i < points.size(); i++) {
                if (points.get(i) != null) {
                    points.get(i).setColor(colorOfPoints);
                }
            }
        }

        public ArrayList<Point> getPointsList() {
            return points;
        }

        public void addToPoints(Point p) {
            points.add(p);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    static class Point {
        private double x, y;
        private Color color;

        public Point(double xCoord, double yCoord) {
            x = xCoord;
            y = yCoord;
            color = Color.BLACK;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public void setColor(Color c) {
            color = c;
        }

        public Color getColor() {
            return color;
        }

        public double getDistance(CenterPoint cp) {
            double x2 = cp.getX();
            double y2 = cp.getY();

            return Math.sqrt(Math.pow(x - x2, 2.0) + Math.pow(y - y2, 2));
        }
    }
}
