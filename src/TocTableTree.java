import java.awt.Color;
import java.util.EventListener;
import java.util.List;

import javax.swing.Icon;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.*;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

public class TocTableTree extends Outline {
    private TocItem root;
    private TocItem selectedItem;
    private SelectionListener selectionListener;

    interface SelectionListener extends EventListener {
        void valueChanged(TocItem tocItem);
    }

    public TocTableTree(TocItem tocItem) {
        this.root = tocItem;

        this.setup();
        this.update();
    }

    private void setup() {
        // Set action listener
        // TODO: Fill  this out

        // Allow single selection only
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Use custom render
        this.setRenderDataProvider(new RenderData());
    }

    private void update() {
        var treeModel = new TocTableTreeModel(this.root);
        var rowModel = new TocTableTreeRowModel();
        OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(treeModel, rowModel);

        this.setModel(outlineModel);
    }

    // TODO: Make these work
    public void addSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
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

        var child = new TocItem("New Label", this.selectedItem.pageNum, this.selectedItem, null);

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

    class TocTableTreeModel implements TreeModel {
        private TocItem root;

        public TocTableTreeModel(TocItem root) {
            this.root = root;
        }

        @Override
        public Object getRoot() {
            return this.root;
        }

        private List<TocItem> getChildren(Object node) {
            return ((TocItem) node).children;
        }

        @Override
        public Object getChild(Object parent, int index) {
            return getChildren(parent).get(index);
        }

        @Override
        public int getChildCount(Object parent) {
            return getChildren(parent).size();
        }

        @Override
        public boolean isLeaf(Object node) {
            var children = getChildren(node);
            return children == null || children.size() == 0;
        }

        @Override
        public void valueForPathChanged(TreePath path, Object newValue) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'valueForPathChanged'");
        }

        @Override
        public int getIndexOfChild(Object parent, Object child) {
            return getChildren(parent).indexOf(child);
        }

        @Override
        public void addTreeModelListener(TreeModelListener l) {
            // TODO Auto-generated method stub
        }

        @Override
        public void removeTreeModelListener(TreeModelListener l) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'removeTreeModelListener'");
        }

    }

    class TocTableTreeRowModel implements RowModel {
        @Override
        public int getColumnCount() {
            return 1;
        }

        @Override
        public Object getValueFor(Object node, int column) {
            var typedNode = (TocItem) node;
            switch (column) {
                case 0:
                    return typedNode.pageNum;
                default:
                    assert false;
            }

            return null;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Integer.class;
                default:
                    assert false;
            }

            return null;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return "Page Number";
                default:
                    assert false;
            }

            return null;
        }

        @Override
        public boolean isCellEditable(Object node, int column) {
            return false;
        }

        @Override
        public void setValueFor(Object node, int column, Object value) {
            var typedNode = (TocItem) node;
            switch (column) {
                case 0:
                    typedNode.pageNum = (int) value;
                default:
                    assert false;
            }
        }
    }

    class RenderData implements RenderDataProvider {

        @Override
        public String getDisplayName(Object o) {
            return ((TocItem) o).label;
        }

        @Override
        public boolean isHtmlDisplayName(Object o) {
            return false;
        }

        @Override
        public Color getBackground(Object o) {
            return null;
        }

        @Override
        public Color getForeground(Object o) {
            return null;
        }

        @Override
        public String getTooltipText(Object o) {
            return "" + ((TocItem) o).children.size() + " Children";
        }

        @Override
        public Icon getIcon(Object o) {
            return null;
        }
    }
}
