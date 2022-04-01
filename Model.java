import java.io.File;
import javax.swing.*;

public class Model extends JFrame {
    private File csvFile;
    private int iterationCount;
    private int kCountValue;
    private View view;

    public Model() {
        View functionalPanel = new View(this);
        view = functionalPanel;
        setTitle("K-Means Clustering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        add(functionalPanel);
    }    
    
    public void setCSVFile(File f) {
        csvFile = f;
    }

    public void setIterationCount(int i) {
        iterationCount = i;
    }

    public void setKCountValue(int i) {
        kCountValue = i;
    }

    public File getCSVFile() {
        return csvFile;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    public int getKCountValue() {
        return kCountValue;
    }
}
