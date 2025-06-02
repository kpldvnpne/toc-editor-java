import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.awt.Component;

// TODO: Use a top add child (+) and remove (x) button

public class TocTree extends JTree {
    TocItem root;
    private TocItem selectedItem;

    public TocTree(TocItem root) {
        this.root = root;

        this.setup();
        this.populate();
    }

    private void setup() {
        var thisTree = (JTree) this;
        thisTree.setRowHeight(30);

        // Set renderer
        var renderer = new LabelAndPageNumRenderer();
        thisTree.setCellRenderer(renderer);
        thisTree.setCellEditor(new LabelAndPageNumEditor(thisTree, renderer));
        thisTree.setEditable(true);

        // Set action listener
        thisTree.addTreeSelectionListener((TreeSelectionEvent e) -> {
            TreePath newSelectionPath = e.getNewLeadSelectionPath();

            if (newSelectionPath == null) {
                this.selectedItem = null;
            } else {
                var node = (DefaultMutableTreeNode) newSelectionPath.getLastPathComponent();
                var tocItem = (TocItem) node.getUserObject();

                this.selectedItem = tocItem;
            }
        });
    }

    private void populate() {
        var rootNode = this.root.toTreeNode();
        var thisTree = (JTree) this;

        thisTree.setModel(new DefaultTreeModel(rootNode));
    }

    private void update() {
        // Update
        this.populate();
    }

    public void addChildToSelectedItem() {
        this.selectedItem.addChild();

        this.update();

        // TODO: Expand on doing this
    }

    public void removeSelectedItem() {
        this.selectedItem.removeFromParent();
        this.selectedItem = null;

        this.update();
    }

    public class LabelAndPageNumRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            var panel = new JPanel();

            var color = selected ? Color.LIGHT_GRAY : Color.WHITE;
            panel.setBackground(color);

            var node = (DefaultMutableTreeNode) value;
            var tocItem = (TocItem) node.getUserObject();
            var label = new JLabel(tocItem.label);
            var pageNum = new JLabel("" + tocItem.pageNum);

            panel.add(label);
            panel.add(Box.createHorizontalStrut(50));
            panel.add(pageNum);

            return panel;
        }
    }

    public class LabelAndPageNumEditor extends DefaultTreeCellEditor {
        private TocItem tocItem;
        private JTextField labelField;
        private JTextField pageNumField;
        private DefaultMutableTreeNode node;

        public LabelAndPageNumEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer);
            this.tocItem = null;
        }

        // TODO: Make it not jitter when converting from Renderer to Editor
        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
                boolean leaf, int row) {
            var panel = new JPanel();
            panel.setBackground(Color.WHITE);
            this.node = (DefaultMutableTreeNode) value;
            var tocItem = (TocItem) this.node.getUserObject();
            this.tocItem = tocItem;
            this.labelField = new JTextField(this.tocItem.label); // TODO: Make one editable at a time

            this.pageNumField = new JTextField("" + this.tocItem.pageNum);

            panel.add(labelField);
            panel.add(Box.createHorizontalStrut(50));
            panel.add(pageNumField);

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            String label = this.labelField.getText();
            int pageNum = Integer.parseInt(this.pageNumField.getText());

            this.tocItem.label = label;
            this.tocItem.pageNum = pageNum;

            return this.tocItem;
        }
    }

}
