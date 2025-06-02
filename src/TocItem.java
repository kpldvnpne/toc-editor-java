import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.tree.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;

class TocItem {
    public String label;
    public int pageNum;
    public TocItem parent;
    public TocItem[] children;

    public static TocItem fromPdfDocument(PdfDocument document) {
        var outline = document.getOutlines(true);
        return TocItem.fromOutline(outline, document);
    }

    private static int getPageNumber(PdfOutline outline, PdfDocument document) {
        var destination = outline.getDestination();
        if (destination == null) {
            return 0;
        }

        var pdfObject = destination.getPdfObject();
        if (!(pdfObject instanceof PdfArray)) {
            // Can't find the page number
            return 0;
        }

        PdfArray pdfArray = (PdfArray) destination.getPdfObject();
        var dictionary = pdfArray.getAsDictionary(0);

        if (dictionary == null) {
            // Can't find the dictionary
            return 0;
        }

        int pageNum = document.getPageNumber(dictionary);
        return pageNum;
    }

    private static TocItem fromOutline(PdfOutline outline, PdfDocument document) {
        return TocItem.fromOutline(outline, document, null);
    }

    private static TocItem fromOutline(PdfOutline outline, PdfDocument document, TocItem parent) {
        var label = outline.getTitle();
        var pageNum = getPageNumber(outline, document);

        var current = new TocItem(
            label,
            pageNum,
            parent,
            null
        );

        current.children = outline.getAllChildren()
            .stream()
            .map((PdfOutline child) -> TocItem.fromOutline(child, document, current))
            .collect(Collectors.toList())
            .toArray(new TocItem[0]);

        return current;
    }

    public TocItem(String label, int pageNum, TocItem parent, TocItem[] children) {
        this.label = label;
        this.pageNum = pageNum;
        this.parent = parent;
        this.children = children;
    }

    public TocItem copyWithLabel(String newLabel) {
        return new TocItem(
            newLabel,
            this.pageNum,
            this.parent,
            this.children
        );
    }

    public TocItem copyWithPageNum(int newPageNum) {
        return new TocItem(
            this.label,
            newPageNum,
            this.parent,
            children
        );
    }

    public TocItem copyWith(String newLabel, int newPageNum) {
        return this.copyWithLabel(newLabel).copyWithPageNum(newPageNum);
    }

    public void removeFromParent() {
        if (this.parent != null) this.parent.removeChild(this);
    }

    public void removeChild(TocItem child) {
        Arrays.asList(this.children).remove(child);
    }

    public void addChildrenTo(PdfOutline root, PdfDocument document) {
        for (var child: this.children) {
            var page = document.getPage(child.pageNum);
            var destination = PdfExplicitDestination.createFit(page);
            var childOutline = root.addOutline(child.label);
            childOutline.addDestination(destination);

            if (child.children != null) {
                child.addChildrenTo(childOutline, document);
            }
        }
    }

    private MutableTreeNode toNode() {
        var top = new DefaultMutableTreeNode(this);

        if (this.children != null) {
            for (var child: this.children) {
                var childNode = child.toNode();
                top.add(childNode);
            }
        }
        return top;
    }

    public JTree toJTree() {
        var top = this.toNode();
        var tree = new JTree(top);
        tree.setRowHeight(30);
        var renderer = new LabelAndPageNumRenderer();
        tree.setCellRenderer(renderer);
        tree.setCellEditor(new LabelAndPageNumEditor(tree, renderer));
        tree.setEditable(true);
        return tree;
    }

    public void print() {
        this.print("");
    }

    private void print(String tab) {
        System.out.println(tab + this.label + " " + this.pageNum);
        if (this.children != null) {
            for (var child: this.children) {
                child.print(tab + "\t");
            }
        }
    }

    public class LabelAndPageNumRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            var panel = new JPanel();
            var node = (DefaultMutableTreeNode) value;
            var tocItem = (TocItem) node.getUserObject();
            var label = new JLabel(tocItem.label);
            var pageNum = new JLabel("" + tocItem.pageNum);
            var removeButton = new JButton("Remove");

            panel.add(label);
            panel.add(Box.createHorizontalStrut(50));
            panel.add(pageNum);
            panel.add(Box.createHorizontalGlue());
            panel.add(removeButton);

            removeButton.addActionListener((ActionEvent e) -> {
                tocItem.removeFromParent();
                // TODO: Regenerate based on tocItem
            });

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

        // TODO: Make it fully visible when editing
        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
                boolean leaf, int row) {
            var panel = new JPanel();
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

