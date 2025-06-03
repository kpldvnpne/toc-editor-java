import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.EventListener;

public class TocTree extends JTree {
    TocItem root;
    private TocItem selectedItem;
    private SelectionListener selectionListener;
    private EditListener editListener;

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

        // Allow single selection only
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Allow drag
        this.setDragEnabled(true);
        this.setDropMode(DropMode.ON_OR_INSERT);
        this.setTransferHandler(new TocTreeTransferHandler());
    }

    private void populate() {
        var rootNode = this.root.toTreeNode();
        var thisTree = (JTree) this;

        thisTree.setModel(new DefaultTreeModel(rootNode));
    }

    private void update() {
        this.populate();
    }

    public boolean editSelectedItem() {
        if (this.selectedItem == null) {
            return false;
        }

        // Update only when OK is pressed
        if (EditTocItem.showFor(this.selectedItem)) {
            this.update();

            return true;
        }

        return false;
    }

    public boolean addChildToSelectedItem() {
        if (this.selectedItem == null) {
            return false;
        }

        var child = new TocItem("New Label", Math.max(this.selectedItem.pageNum, 1), this.selectedItem, null);

        // Edit using a dialog, add only when "OK" is pressed
        if (EditTocItem.showFor(child)) {
            this.selectedItem.addChild(child);
            this.update();

            return true;
        }

        return false;
    }

    public boolean removeSelectedItem() {
        this.selectedItem.removeFromParent();
        this.selectedItem = null;

        this.update();

        return true;
    }

    public void addSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
    }

    interface SelectionListener extends EventListener {
        void valueChanged(TocItem tocItem);
    }

    public void addEditListener(EditListener listener) {
        this.editListener = listener;
    }

    interface EditListener extends EventListener {
        void edited(boolean edited);
    }

    private class LabelAndPageNumRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            // TODO: Make all of equal width
            var panel = new JPanel();

            var color = selected ? Color.LIGHT_GRAY : Color.WHITE;
            panel.setBackground(color);

            var node = (DefaultMutableTreeNode) value;
            var tocItem = (TocItem) node.getUserObject();
            var label = new JLabel(tocItem.label);
            var pageNum = new JLabel("" + tocItem.pageNum);

            panel.add(label);

            // Don't show pageNum for the root
            if (row != 0) {
                panel.add(Box.createHorizontalStrut(50));
                panel.add(pageNum);
            }


            return panel;
        }
    }

    private class TocTreeTransferHandler extends TransferHandler {
        DataFlavor nodeFlavor;

        public TocTreeTransferHandler() {
            var mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + DefaultMutableTreeNode.class.getName() + "\"";

            try {
                this.nodeFlavor = new DataFlavor(mimeType);
            } catch (ClassNotFoundException e) {
                System.out.println("Invalid mime type");
                e.printStackTrace();
            }
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return true;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            var tree = (JTree) c;
            var path = tree.getSelectionPath();
            var selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
            return new NodeTransferable(selectedNode);
        }

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        public boolean importData(TransferSupport support) {
            var tree = (TocTree) support.getComponent();

            DefaultMutableTreeNode nodeToTransfer = null;
            try {
                var transferData = support.getTransferable().getTransferData(this.nodeFlavor);
                nodeToTransfer = (DefaultMutableTreeNode) transferData;
            } catch (UnsupportedFlavorException | IOException e) {
                System.out.println("Something wrong while getting transferrable node");
                return false;
            }

            var dropLocation = (JTree.DropLocation) support.getDropLocation();
            var childIndex = dropLocation.getChildIndex();
            var destinationPath = dropLocation.getPath();
            var destinationNode = (DefaultMutableTreeNode) destinationPath.getLastPathComponent();
            var destinationTocItem = (TocItem) destinationNode.getUserObject();

            var insertIndex = childIndex; // DropMode.INSERT
            if (childIndex == -1) { // DropMode.ON
                insertIndex = destinationTocItem.children.size();
            }

            var itemToTransfer = (TocItem) nodeToTransfer.getUserObject();
            itemToTransfer.removeFromParent(); // Needed, because could be re-added to the same parent
            destinationTocItem.addChild(itemToTransfer, insertIndex);

            // Update tree precisely to avoid errors
            destinationNode.insert(nodeToTransfer, insertIndex);
            tree.updateUI();
            tree.editListener.edited(true);

            return true;
        }

        private class NodeTransferable implements Transferable {
            DefaultMutableTreeNode node;

            public NodeTransferable(DefaultMutableTreeNode node) {
                this.node = node;
            }

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[] {};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return true;
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return node;
            }
        }
    }
}
