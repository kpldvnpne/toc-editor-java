import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
// import java.io.File;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
public class Main {
  public static JFileChooser fileChooser;

  private static JPanel panel = null;
  private static JComponent editor = null;

  private static void showTocFromFile(JLabel inputFileText) {
    var filepath = inputFileText.getText();
    var editor = TocEditor.fromInputFile(filepath);

    if (editor == null) {
      Dialog.showError("Could not get table of contents from the file");
    } else {
      updateEditor(editor);
    }
  }

  private static void updateEditor(JComponent newEditor) {
    updateChild(Main.panel, Main.editor, newEditor);
    Main.editor = newEditor;
  }

  private static void updateChild(JComponent parent, JComponent oldChild, JComponent newChild) {
    var indexOfOldChild = Arrays.asList(parent.getComponents()).indexOf(oldChild);
    parent.remove(oldChild);
    parent.add(newChild, indexOfOldChild);
  }

  private static void createAndShowGUI() {
    // Create and setup the window
    JFrame frame = new JFrame("TOC Editor");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // File chooser
    fileChooser = new JFileChooser();
    var filter = new FileNameExtensionFilter("Only PDF Files", "pdf");
    fileChooser.setFileFilter(filter);

    // Add content to the window
    panel = new JPanel();
    panel.setBounds(0, 0, 400, 200);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    frame.getContentPane().add(panel);

    // Padding
    Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 20);
    panel.setBorder(padding);

    // Input Panel
    JPanel inputContainer = new JPanel();
    inputContainer.setLayout(new GridLayout(0, 1)); // Grid helps it fill the width

    JPanel inputContainerInner = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0)); // Flow Layout helps keep everything flexible
    inputContainer.add(inputContainerInner);

    JButton inputButton = new JButton("Select input file:");
    inputContainerInner.add(inputButton);
    JLabel inputFileText = new JLabel();
    inputContainerInner.add(inputFileText);

    panel.add(inputContainer);

    // Tree
    Main.editor = TocEditor.getNoEditor();
    panel.add(editor);

    inputButton.addActionListener((ActionEvent _) -> {
      var result = fileChooser.showOpenDialog(frame);

      if (result == JFileChooser.APPROVE_OPTION) {
        String filepath = fileChooser.getSelectedFile().getAbsolutePath();
        inputFileText.setText(filepath);

        showTocFromFile(inputFileText);
      }
    });

    // Display the window
    frame.setSize(800, 400);

    frame.setLocationRelativeTo(null);
    frame.setVisible(true);


    // TODO: Delete once done with Rendering
    // var filepath = "/Users/kapildev/Downloads/pdf_without_toc.pdf";
    // inputFileText.setText(filepath);
    // fileChooser.setSelectedFile(new File(filepath));
    // Main.showTocFromFile(inputFileText);
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
