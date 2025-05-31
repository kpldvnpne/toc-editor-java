
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;

public class Main {
  public static void main(String[] args) {
    String filename = "/Users/kapildev/Downloads/ielts 15 academic/Cambridge 15 - Full Version.pdf";
    try {
      PdfReader reader = new PdfReader(filename);
      PdfDocument document = new PdfDocument(reader);
    } catch (Exception exception) {
      System.out.println("Could not open the file");
    }
  }
}
