import java.io.IOException;
import java.util.Arrays;

import javax.swing.*;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

public class TocEditor extends JPanel {
  public static JComponent getNoEditor() {
    var noEditorPanel = new JPanel();
    noEditorPanel.add(new JLabel("Please select a file"));

    return noEditorPanel;
  }

  public static TocEditor fromInputFile(String inputFilePath) {
    try (var reader = new PdfReader(inputFilePath); var document = new PdfDocument(reader)) {
      var tocItem = TocItem.fromPdfDocument(document);

      return new TocEditor(tocItem, inputFilePath);
    } catch (IOException exception) {
      System.out.println("Can't read the file");
      return null;
    }
  }

  private TocItem tocItem;
  private String inputFilePath;
  private JButton saveAsButton;

  private TocEditor(TocItem tocItem, String inputFilePath) {
    super();

    // The root should be named Root for easier understanding
    tocItem.label = "Root";

    this.tocItem = tocItem;
    this.inputFilePath = inputFilePath;

    this.setup();
  }

  private void setup() {
    var topRowPanel = new JPanel();
    topRowPanel.setLayout(new BoxLayout(topRowPanel, BoxLayout.LINE_AXIS));

    var label = new JLabel("Table of content");

    var buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
    var editButton = new JButton("Edit");
    var addChildButton = new JButton("Add Child");
    var removeButton = new JButton("Remove");

    var buttons = Arrays.asList(new JButton[] { editButton, addChildButton, removeButton });

    // Add each button and disable
    buttons.stream().forEach(buttonPanel::add);
    buttons.stream().forEach((button) -> button.setEnabled(false));

    topRowPanel.add(label);
    topRowPanel.add(Box.createHorizontalGlue());
    topRowPanel.add(buttonPanel);

    // Table of Contents Scroll View
    var tree = this.tocItem == null ? null : new TocTree(this.tocItem);
    tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    var treeView = new JScrollPane(tree);

    this.add(topRowPanel);
    this.add(treeView);

    // Add TOC button
    this.saveAsButton = new JButton("Save As");
    this.disableSaveAsButton();
    this.add(saveAsButton);

    // Make the main panel column
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    // Add selection listener
    tree.addSelectionListener((TocItem tocItem) -> {
      if (tocItem == null) {
        buttons.stream().forEach((button) -> button.setEnabled(false));
      } else {
        buttons.stream().forEach((button) -> button.setEnabled(true));
      }
    });

    // Add edited listener
    tree.addEditListener((edited) -> {
      if (edited) {
        this.enableSaveAsButton();
      } else {
        this.disableSaveAsButton();
      }
    });

    // Add actions to buttons
    editButton.addActionListener((_) -> {
      if (tree.editSelectedItem())
        this.enableSaveAsButton();
    });

    addChildButton.addActionListener((_) -> {
      if (tree.addChildToSelectedItem())
        this.enableSaveAsButton();
    });

    removeButton.addActionListener((_) -> {
      if (tree.removeSelectedItem())
        this.enableSaveAsButton();
    });

    saveAsButton.addActionListener((_) -> {
      if (this.tocItem == null) {
        Dialog.showError("No TOC Found");
        return;
      }

      var fileChooser = Main.fileChooser;

      if (inputFilePath.isBlank()) {
        Dialog.showError("You don't have input file selected");
        return;
      }

      var result = fileChooser.showSaveDialog(this);

      if (result == JFileChooser.APPROVE_OPTION) {
        String outputFilePath = fileChooser.getSelectedFile().getAbsolutePath();

        if (outputFilePath.equals(inputFilePath)) {
          Dialog.showError("You can't save to the same file. Please select different file");
          return;
        }

        if (addTOC(inputFilePath, outputFilePath, this.tocItem)) {
          Dialog.showInfo("Successful");

          this.disableSaveAsButton();
        } else {
          Dialog.showError("Could not edit TOC");
        }
      }
    });
  }

  private void enableSaveAsButton() {
    this.saveAsButton.setEnabled(true);
  }

  private void disableSaveAsButton() {
    this.saveAsButton.setEnabled(false);
  }

  private static boolean addTOC(String inputFilename, String outputFilename, TocItem tocItem) {
    if (inputFilename.equals(outputFilename)) {
      Dialog.showError("Input and output should not be same file. Exiting");
      return true;
    }

    try {
      PdfReader reader = new PdfReader(inputFilename);
      PdfWriter writer = new PdfWriter(outputFilename);

      try(PdfDocument document = new PdfDocument(reader, writer)) {
        var root = document.getOutlines(true);

        // Remove every child
        // TODO: Removing every outline does not work, so remove all except 1
        while (root.getAllChildren().size() > 1) {
          root.getAllChildren().get(0).removeOutline();
        }

        var needToRemoveFirst = root.getAllChildren().size() == 1;

        tocItem.addChildrenTo(root, document);

        // TODO: Remove this
        // Remove the first element that we possibly didn't remove
        if (needToRemoveFirst)
          root.getAllChildren().get(0).removeOutline();
      }

      return true;
    } catch (Exception exception) {
      System.out.println("Could not open the file");
      exception.printStackTrace();

      return false;
    }
  }
}
