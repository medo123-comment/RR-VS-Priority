package util;

import java.io.*;
import javax.swing.*;

public class FileSaver {

    public static void save(String content) {
        JFileChooser chooser = new JFileChooser();

        if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                FileWriter fw = new FileWriter(chooser.getSelectedFile() + ".txt");
                fw.write(content);
                fw.close();
                JOptionPane.showMessageDialog(null, "Saved Successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error Saving File!");
            }
        }
    }
}