import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.awt.Component;
import java.util.EventListener;

// TODO: Make the toc items draggable
public class TocTree extends JTree {
    TocItem root;
    private TocItem selectedItem;
    private SelectionListener selectionListener;

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

            this.selectionListener.valueChanged(this.selectedItem);
        });
    }

    private void populate() {
        var rootNode = this.root.toTreeNode();
        var thisTree = (JTree) this;

        thisTree.setModel(new DefaultTreeModel(rootNode));
    }

    private void update() {
        this.populate();
    }

    public void editSelectedItem() {
        if (this.selectedItem == null) {
            return;
        }

        EditTocItem.showFor(this.selectedItem);

        this.update();
    }

    public void addChildToSelectedItem() {
        if (this.selectedItem == null) {
            return;
        }

        var child = new TocItem("New Label", this.selectedItem.pageNum, this.selectedItem, null);

        // Edit using a dialog, add only when "OK" is pressed
        if (EditTocItem.showFor(child)) {
            this.selectedItem.addChild(child);
        }

        this.update();
    }

    public void removeSelectedItem() {
        this.selectedItem.removeFromParent();
        this.selectedItem = null;

        this.update();
    }

    public void addSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
    }

    interface SelectionListener extends EventListener {
        void valueChanged(TocItem tocItem);
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
}
