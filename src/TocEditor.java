import javax.swing.*;

public class TocEditor extends JPanel {
  TocItem tocItem;

  public TocEditor(TocItem tocItem) {
    super();
    this.tocItem = tocItem;

    this.setup();
  }

  private void setup() {
    var topRowPanel = new JPanel();
    topRowPanel.setLayout(new BoxLayout(topRowPanel, BoxLayout.LINE_AXIS));

    var label = new JLabel("TOC Editor");

    var buttonPanel = new JPanel();
    var addChildButton = new JButton("Add Child");
    var removeButton = new JButton("Remove");
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
  }
}
