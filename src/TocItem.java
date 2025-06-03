import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.tree.*;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;

class TocItem {
    public String label;
    public int pageNum;
    public TocItem parent;
    public List<TocItem> children;

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
            .collect(Collectors.toList());

        return current;
    }

    public TocItem(String label, int pageNum, TocItem parent, List<TocItem> children) {
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

    public void addChild(TocItem child) {
        if (this.children == null) {
            this.children = new ArrayList<TocItem>();
        }
        this.children.add(child);
    }

    public void removeFromParent() {
        if (this.parent != null) this.parent.removeChild(this);
    }

    private void removeChild(TocItem child) {
        this.children.remove(child);
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

    public MutableTreeNode toTreeNode() {
        var top = new DefaultMutableTreeNode(this);

        if (this.children != null) {
            for (var child: this.children) {
                var childNode = child.toTreeNode();
                top.add(childNode);
            }
        }
        return top;
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
}

