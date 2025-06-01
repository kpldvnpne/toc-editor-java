
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Main {

  private static void createAndShowGUI() {
    // Create and setup the window
    JFrame frame = new JFrame("TOC Creator");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // File chooser
    JFileChooser fileChooser = new JFileChooser();
    var filter = new FileNameExtensionFilter("Only PDF Files", "pdf");
    fileChooser.setFileFilter(filter);

    // Add content to the window
    JPanel panel = new JPanel();
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
    JTree tree = TocItem.outline.toJTree();
    JScrollPane treeView = new JScrollPane(tree);
    panel.add(treeView);

    // Add TOC button
    JButton addTocButton = new JButton("Add TOC");
    panel.add(addTocButton);

    inputButton.addActionListener((ActionEvent e) -> {
      var result = fileChooser.showOpenDialog(frame);

      if (result == JFileChooser.APPROVE_OPTION) {
        String filepath = fileChooser.getSelectedFile().getAbsolutePath();
        inputFileText.setText(filepath);
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
      printTOC();
      // if (!inputFileText.getText().isBlank() && !outputFileText.getText().isBlank()) {
      //   addTOC(inputFileText.getText(), outputFileText.getText());
      // }
    });

    // Display the window
    frame.setSize(new Dimension(800, 400));
    frame.setVisible(true);
  }

  private static void printTOC() {
    TocItem.outline.print();
  }

  private static void addTOC(String inputFilename, String outputFilename) {
    try {
      PdfReader reader = new PdfReader(inputFilename);
      PdfWriter writer = new PdfWriter(outputFilename);

      try(PdfDocument document = new PdfDocument(reader, writer)) {
        var root = document.getOutlines(true);

        // Remove every child
        // TODO: Removing every outline does not work
        while (root.getAllChildren().size() > 1) {
          root.getAllChildren().get(0).removeOutline();
        }

        TocItem.outline.addChildrenTo(root, document);

        // TODO: Remove this
        // Remove the first element, that we didn't remove because it does not work without it
        root.getAllChildren().get(0).removeOutline();
      }
    } catch (Exception exception) {
      System.out.println("Could not open the file");
      exception.printStackTrace();
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
