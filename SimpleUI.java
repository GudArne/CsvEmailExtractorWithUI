import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class SimpleUI {
    private JFrame frame;
    private JTextField fileField;
    private JTextField delimiterField;
    private JButton runButton;

    public SimpleUI() {
        frame = new JFrame("CSV Email Extractor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS)); // Vertical layout

        // File field
        JLabel fileLabel = new JLabel("File:");
        frame.add(fileLabel);
        fileField = new JTextField(20);
        frame.add(fileField);

        // File button
        JButton fileButton = new JButton("Browse");
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
                fileChooser.setCurrentDirectory(new File(".")); // Sets the default directory to the current directory
                int choice = fileChooser.showOpenDialog(frame);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    String selectedFilePath = fileChooser.getSelectedFile().getAbsolutePath();
                    fileField.setText(selectedFilePath);
                }
            }
        });
        frame.add(fileButton);

        // Delimiter field
        JLabel delimiterLabel = new JLabel("Delimiter:");
        frame.add(delimiterLabel);
        delimiterField = new JTextField();
        delimiterField.setFont(new Font(delimiterField.getFont().getName(), Font.PLAIN, 32)); // Increase font size
        delimiterField.setText(",");

        // Limit the delimiter field to one character only
        ((AbstractDocument) delimiterField.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (offset == 0 && length == 1 && text.isEmpty()) {
                    super.replace(fb, offset, length, text, attrs);
                } else if (offset == 0 && length == 0 && text.length() == 1) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        frame.add(delimiterField);

        // Status label
        var statusLabel = new JLabel("Choose a CSV file and click Run");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        frame.add(statusLabel);

        // Run button
        runButton = new JButton("Run");
        frame.add(runButton);

        // Add action listener to the run button
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Loading...");
                runButton.setEnabled(false);
                String file = fileField.getText();
                String delimiter = delimiterField.getText();

                // Run the processFile method in a separate thread to avoid blocking the UI
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        var ee = new EmailExtractor();
                        boolean success = ee.processFile(file, delimiter);
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                if (success) {
                                    statusLabel.setText("Success!");
                                    openResultFile();
                                } else {
                                    statusLabel.setText("Failed!");
                                }
                                runButton.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        });

        frame.pack();
        frame.setVisible(true);
    }

    private void openResultFile() {
        try {
            File resultFile = new File("result.txt");
            String absolutePath = resultFile.getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "\"\"", absolutePath);
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimpleUI();
            }
        });
    }
}
