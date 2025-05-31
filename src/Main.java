
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.navigation.PdfExplicitDestination;

public class Main {
  public static void main(String[] args) {
    String inputFilename = "/Users/kapildev/Downloads/ielts 15 academic/Cambridge 15 - Full Version.pdf";
    String outputFilename = "/Users/kapildev/Downloads/ielts 15 academic/Cambridge 15 - Full Version - JAVA TOC.pdf";
    try {
      PdfReader reader = new PdfReader(inputFilename);
      PdfWriter writer = new PdfWriter(outputFilename);

      try(PdfDocument document = new PdfDocument(reader, writer)) {
        var root = document.getOutlines(true);

        // Remove every child
        while (!root.getAllChildren().isEmpty()) {
          root.getAllChildren().get(0).removeOutline();
        }

        // root.removeOutline();
        // document.initializeOutlines();
        // for (var child: root.getAllChildren()) {
        //   child.removeOutline();
        // }
        // var childOutline = root.addOutline("Go to page 12");
        // var page = document.getPage(12);
        // var destination = PdfExplicitDestination.createFit(page);
        // childOutline.addDestination(destination);
        // System.out.println("Added some destination to outline");
      }
    } catch (Exception exception) {
      System.out.println("Could not open the file");
      exception.printStackTrace();
    }
  }
}
