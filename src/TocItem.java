import java.awt.Component;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.tree.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;

class TocItem {
    public String label;
    public int pageNum;
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

        PdfArray pdfArray = (PdfArray) destination.getPdfObject();
        var dictionary = pdfArray.getAsDictionary(0);
        int pageNum = document.getPageNumber(dictionary);
        return pageNum;
    }

    private static TocItem fromOutline(PdfOutline outline, PdfDocument document) {
        var label = outline.getTitle();
        var pageNum = getPageNumber(outline, document);
        // System.out.println("Label: " + outline.getTitle() + "; " + "Page Num: "  + pageNum);

        var destination = outline.getDestination();

        // If destination is null, it is the root
        if (destination != null) {
            // PdfDictionary content = outline.getContent();
            // for (var key: content.keySet()) {
            //     var value = content.get(key);
            //     System.out.print(key + ": " + value + " [" + value.getClass().getSimpleName() + "]");
            //     System.out.print(", ");
            // }
            // System.out.println();

            // var dictionary = (PdfPage) destination.getDestinationPage(null);

            // System.out.println(outline.getTitle() + ": " + document.getPageNumber(dictionary));

            // if (destination instanceof PdfExplicitDestination) {
            //     var explicitDestination = (PdfExplicitDestination) destination;
            //     explicitDestination
            //     System.out.println("It is explicit");
            // }
            // System.out.println(destination.getPdfObject().getClass().getSimpleName());
            // PdfArray pdfArray = (PdfArray) destination.getPdfObject();
            // System.out.println(pdfArray);

            // var first = pdfArray.get(0);
            // System.out.println(first);
            // System.out.println(first.getClass().getSimpleName());

            // var dictionary = pdfArray.getAsDictionary(0);
            // System.out.println(dictionary);

            // int pageNum = document.getPageNumber(dictionary);
            // System.out.println("Page Number " + pageNum);


            // var pageObjNum = pdfArray.getAsNumber(0);
            // System.out.println(pageObjNum.getValue());
        }

        TocItem[] children = outline.getAllChildren()
            .stream()
            .map((PdfOutline child) -> TocItem.fromOutline(child, document))
            .collect(Collectors.toList())
            .toArray(new TocItem[0]);

        return new TocItem(
            label,
            pageNum,
            children
        );
    }

    public TocItem(String label, int pageNum, TocItem[] children) {
        this.label = label;
        this.pageNum = pageNum;
        this.children = children;
    }

    public TocItem copyWithLabel(String newLabel) {
        return new TocItem(
            newLabel,
            this.pageNum,
            this.children
        );
    }

    public TocItem copyWithPageNum(int newPageNum) {
        return new TocItem(
            this.label,
            newPageNum,
            children
        );
    }

    public TocItem copyWith(String newLabel, int newPageNum) {
        return this.copyWithLabel(newLabel).copyWithPageNum(newPageNum);
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
        public LabelAndPageNumRenderer() {}

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasFocus) {
            var panel = new JPanel();
            var node = (DefaultMutableTreeNode) value;
            var tocItem = (TocItem) node.getUserObject();
            var label = new JLabel(tocItem.label);
            var pageNum = new JLabel("" + tocItem.pageNum);
            panel.add(label);
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
            panel.add(pageNumField);

            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            String label = this.labelField.getText();
            int pageNum = Integer.parseInt(this.pageNumField.getText());

            this.tocItem.label = label;
            this.tocItem.pageNum = pageNum;

            // TODO: Then, when the "ADD TOC" button is clicked, use it to create the TOC

            return this.tocItem;
        }
    }

    // TODO: Get this from the file itself
    static TocItem outline = new TocItem("", 0, new TocItem[] {
        new TocItem(
            "Cover",
            1,
            null
        ),
        new TocItem(
            "Contents",
            5,
            null
        ),
        new TocItem(
            "Introduction",
            6,
            null
        ),
        new TocItem(
            "Test 1",
            12,
            new TocItem[] {
                new TocItem(
                    "Listening",
                    12,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            12,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            13,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            15,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            17,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Reading",
                    18,
                    new TocItem[] {
                        new TocItem(
                            "Passage 1",
                            18,
                            null
                        ),
                        new TocItem(
                            "Passage 2",
                            22,
                            null
                        ),
                        new TocItem(
                            "Passage 3",
                            26,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Writing",
                    30,
                    new TocItem[] {
                        new TocItem(
                            "Task 1",
                            30,
                            null
                        ),
                        new TocItem(
                            "Task 2",
                            31,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Speaking",
                    32,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            32,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            32,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            32,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Test 2",
            33,
            new TocItem[] {
                new TocItem(
                    "Listening",
                    33,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            33,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            34,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            36,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            38,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Reading",
                    39,
                    new TocItem[] {
                        new TocItem(
                            "Passage 1",
                            39,
                            null
                        ),
                        new TocItem(
                            "Passage 2",
                            42,
                            null
                        ),
                        new TocItem(
                            "Passage 3",
                            46,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Writing",
                    51,
                    new TocItem[] {
                        new TocItem(
                            "Task 1",
                            51,
                            null
                        ),
                        new TocItem(
                            "Task 2",
                            52,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Speaking",
                    53,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            53,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            53,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            53,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Test 3",
            54,
            new TocItem[] {
                new TocItem(
                    "Listening",
                    54,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            54,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            56,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            58,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            59,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Reading",
                    60,
                    new TocItem[] {
                        new TocItem(
                            "Passage 1",
                            60,
                            null
                        ),
                        new TocItem(
                            "Passage 2",
                            64,
                            null
                        ),
                        new TocItem(
                            "Passage 3",
                            68,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Writing",
                    73,
                    new TocItem[] {
                        new TocItem(
                            "Task 1",
                            73,
                            null
                        ),
                        new TocItem(
                            "Task 2",
                            74,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Speaking",
                    75,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            75,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            75,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            75,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Test 4",
            76,
            new TocItem[] {
                new TocItem(
                    "Listening",
                    76,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            76,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            77,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            79,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            81,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Reading",
                    82,
                    new TocItem[] {
                        new TocItem(
                            "Passage 1",
                            82,
                            null
                        ),
                        new TocItem(
                            "Passage 2",
                            86,
                            null
                        ),
                        new TocItem(
                            "Passage 3",
                            89,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Writing",
                    95,
                    new TocItem[] {
                        new TocItem(
                            "Task 1",
                            95,
                            null
                        ),
                        new TocItem(
                            "Task 2",
                            96,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Speaking",
                    97,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            97,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            97,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            97,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Audioscripts",
            98,
            new TocItem[] {
                new TocItem(
                    "Test 1",
                    98,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            98,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            99,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            100,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            101,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 2",
                    103,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            103,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            104,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            105,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            107,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 3",
                    109,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            109,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            110,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            111,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            113,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 4",
                    115,
                    new TocItem[] {
                        new TocItem(
                            "Part 1",
                            115,
                            null
                        ),
                        new TocItem(
                            "Part 2",
                            116,
                            null
                        ),
                        new TocItem(
                            "Part 3",
                            117,
                            null
                        ),
                        new TocItem(
                            "Part 4",
                            119,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Listening and Reading answer keys",
            121,
            new TocItem[] {
                new TocItem(
                    "Test 1",
                    121,
                    new TocItem[] {
                        new TocItem(
                            "Listening",
                            121,
                            null
                        ),
                        new TocItem(
                            "Reading",
                            122,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 2",
                    123,
                    new TocItem[] {
                        new TocItem(
                            "Listening",
                            123,
                            null
                        ),
                        new TocItem(
                            "Reading",
                            124,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 3",
                    125,
                    new TocItem[] {
                        new TocItem(
                            "Listening",
                            125,
                            null
                        ),
                        new TocItem(
                            "Reading",
                            126,
                            null
                        ),
                    }
                ),
                new TocItem(
                    "Test 4",
                    127,
                    new TocItem[] {
                        new TocItem(
                            "Listening",
                            127,
                            null
                        ),
                        new TocItem(
                            "Reading",
                            128,
                            null
                        ),
                    }
                ),
            }
        ),
        new TocItem(
            "Sample Writing answers",
            129,
            new TocItem[] {
                new TocItem(
                    "Test 1, Writing Task 1",
                    129,
                    null
                ),
                new TocItem(
                    "Test 1, Writing Task 2",
                    130,
                    null
                ),
                new TocItem(
                    "Test 2, Writing Task 1",
                    131,
                    null
                ),
                new TocItem(
                    "Test 2, Writing Task 2",
                    133,
                    null
                ),
                new TocItem(
                    "Test 3, Writing Task 1",
                    135,
                    null
                ),
                new TocItem(
                    "Test 3, Writing Task 2",
                    136,
                    null
                ),
                new TocItem(
                    "Test 4, Writing Task 1",
                    138,
                    null
                ),
                new TocItem(
                    "Test 4, Writing Task 2",
                    139,
                    null
                ),
            }
        ),
        new TocItem(
            "Sample answer sheets",
            140,
            null
        ),
        new TocItem(
            "Acknowledgements",
            144,
            null
        ),
        new TocItem(
            "Cover",
            147,
            null
        ),
    });
}

