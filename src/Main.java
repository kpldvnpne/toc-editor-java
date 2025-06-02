// TODO: 1. Add child button, add cross button

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Main {

  private static TocItem tocItem = null;
  private static JPanel panel = null;
  private static TocEditor editor = null;

  private static void showTocFromFile(String filepath) {
    // TODO: Update existing editor
    try (var reader = new PdfReader(filepath); var document = new PdfDocument(reader)) {
      Main.panel.remove(editor);

      Main.tocItem = TocItem.fromPdfDocument(document);
      var editor = new TocEditor(Main.tocItem);

      Main.panel.add(editor, 2); // TODO: Don't use index
    } catch (IOException exception) {
      System.out.println("Can't read the file");
    }
  }

  private static void createAndShowGUI() {
    // Create and setup the window
    JFrame frame = new JFrame("TOC Creator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // File chooser
    JFileChooser fileChooser = new JFileChooser();
    var filter = new FileNameExtensionFilter("Only PDF Files", "pdf");
    fileChooser.setFileFilter(filter);

    // Add content to the window
    panel = new JPanel();
    panel.setBounds(0, 0, 400, 200);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    frame.getContentPane().add(panel);

    // Input Panel
    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    JButton inputButton = new JButton("Select Input Destination:");
    inputPanel.add(inputButton);
    JLabel inputFileText = new JLabel();
    inputPanel.add(inputFileText);

    panel.add(inputPanel);

    // Output Panel
    JPanel outputPanel = new JPanel();
    outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
    JButton outputButton = new JButton("Select Output Destination:");
    outputPanel.add(outputButton);
    JLabel outputFileText = new JLabel();
    outputPanel.add(outputFileText);

    panel.add(outputPanel);

    // Tree
    Main.editor = new TocEditor(null);
    panel.add(editor);

    // Add TOC button
    JButton addTocButton = new JButton("Update TOC");
    panel.add(addTocButton);

    inputButton.addActionListener((ActionEvent e) -> {
      var result = fileChooser.showOpenDialog(frame);

      if (result == JFileChooser.APPROVE_OPTION) {
        String filepath = fileChooser.getSelectedFile().getAbsolutePath();
        inputFileText.setText(filepath);

        showTocFromFile(filepath);
      }
    });

    outputButton.addActionListener((ActionEvent e) -> {
      var result = fileChooser.showSaveDialog(frame);

      if (result == JFileChooser.APPROVE_OPTION) {
        String filepath = fileChooser.getSelectedFile().getAbsolutePath();
        outputFileText.setText(filepath);
      }
    });

    addTocButton.addActionListener((ActionEvent e) -> {
      if (Main.tocItem == null) {
        showError("No TOC Found");
      }

      if (inputFileText.getText().isBlank()) {
        showError("You don't have input file selected");
      }

      if (outputFileText.getText().isBlank()) {
        showError("You don't have output file selected");
      }

      if (addTOC(inputFileText.getText(), outputFileText.getText(), Main.tocItem)) {
        showInfo("Successful");
      } else {
        showError("Could not edit TOC");
      }

    });

    // Display the window
    frame.setSize(new Dimension(800, 400));
    frame.setVisible(true);

    // TODO: Delete once done with Rendering
    Main.showTocFromFile("/Users/kapildev/Downloads/toc_example_2.pdf");
  }

  private static void showError(String message) {
    JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  private static void showInfo(String message) {
    JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
  }

  private static boolean addTOC(String inputFilename, String outputFilename, TocItem tocItem) {
    if (inputFilename.equals(outputFilename)) {
      showError("Input and output should not be same file. Exiting");
      return true;
    }

    try {
      PdfReader reader = new PdfReader(inputFilename);
      PdfWriter writer = new PdfWriter(outputFilename);

      try(PdfDocument document = new PdfDocument(reader, writer)) {
        var root = document.getOutlines(true);

        // Remove every child
        // TODO: Removing every outline does not work
        // TODO: Check if this works with PDFs without an existing outline
        while (root.getAllChildren().size() > 1) {
          root.getAllChildren().get(0).removeOutline();
        }

        tocItem.addChildrenTo(root, document);

        // TODO: Remove this
        // Remove the first element, that we didn't remove because it does not work without it
        root.getAllChildren().get(0).removeOutline();
      }

      return true;
    } catch (Exception exception) {
      System.out.println("Could not open the file");
      exception.printStackTrace();

      return false;
    }
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
