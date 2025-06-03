import javax.swing.*;

public class TocEditor extends JPanel {
  public static JComponent getNoEditor() {
    var noEditorPanel = new JPanel();
    noEditorPanel.add(new JLabel("Please select a file"));

    return noEditorPanel;
  }

  TocItem tocItem;

  public TocEditor(TocItem tocItem) {
    super();
    this.tocItem = tocItem;

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

    buttonPanel.add(editButton);
    buttonPanel.add(addChildButton);
    buttonPanel.add(removeButton);

    topRowPanel.add(label);
    topRowPanel.add(Box.createHorizontalGlue());
    topRowPanel.add(buttonPanel);

    var tree = this.tocItem == null ? null : new TocTree(this.tocItem);
    var treeView = new JScrollPane(tree);

    this.add(topRowPanel);
    this.add(treeView);

    // Make the main panel column
    this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

    // Add actions to buttons
    editButton.addActionListener((e) -> {
      tree.editSelectedItem();
    });

    addChildButton.addActionListener((e) -> {
      tree.addChildToSelectedItem();
    });


    removeButton.addActionListener((e) -> {
      tree.removeSelectedItem();
    });
  }
}
