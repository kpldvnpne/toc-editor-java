
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
        var outline = document.getOutlines(true);
        outline.removeOutline();
        document.initializeOutlines();
        // for (var child: outline.getAllChildren()) {
        //   child.removeOutline();
        // }
        // var page = document.getPage(12);
        // var destination = PdfExplicitDestination.createFit(page);
        // outline.addDestination(destination);
        // System.out.println("Added some destination to outline");
      }
    } catch (Exception exception) {
      System.out.println("Could not open the file");
      exception.printStackTrace();
    }
  }
}
