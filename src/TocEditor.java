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

  private TocItem tocItem;
  private JLabel inputFileText;
  private JButton saveAsButton;

  public TocEditor(TocItem tocItem, JLabel inputFileText) {
    super();
    this.tocItem = tocItem;
    this.inputFileText = inputFileText;

    // TODO: Handle when toc is null
    this.setup();
  }

  private void setup() {
    var topRowPanel = new JPanel();
    topRowPanel.setLayout(new BoxLayout(topRowPanel, BoxLayout.LINE_AXIS));

    var label = new JLabel("Table of content");

    var buttonPanel = new JPanel();
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

    var tree = this.tocItem == null ? null : new TocTree(this.tocItem);
    var treeView = new JScrollPane(tree);

    this.add(topRowPanel);
    this.add(treeView);

    // Add TOC button
    this.saveAsButton = new JButton("Save As");
    this.disableSaveAsButton();
    this.add(saveAsButton);

    // Make the main panel column
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    // Add selection listenger
    tree.addSelectionListener((TocItem tocItem) -> {
      if (tocItem == null) {
        buttons.stream().forEach((button) -> button.setEnabled(false));
      } else {
        buttons.stream().forEach((button) -> button.setEnabled(true));
      }
    });

    // Add actions to buttons
    editButton.addActionListener((e) -> {
      if (tree.editSelectedItem())
        this.enableSaveAsButton();
    });

    addChildButton.addActionListener((e) -> {
      if (tree.addChildToSelectedItem())
        this.enableSaveAsButton();
    });

    removeButton.addActionListener((e) -> {
      if (tree.removeSelectedItem())
        this.enableSaveAsButton();
    });

    saveAsButton.addActionListener((e) -> {
      if (this.tocItem == null) {
        Dialog.showError("No TOC Found");
        return;
      }

      var fileChooser = Main.fileChooser;

      if (inputFileText.getText().isBlank()) {
        Dialog.showError("You don't have input file selected");
        return;
      }

      var result = fileChooser.showSaveDialog(this);

      if (result == JFileChooser.APPROVE_OPTION) {
        String outputFilePath = fileChooser.getSelectedFile().getAbsolutePath();
        String inputFilePath = inputFileText.getText();

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
}
