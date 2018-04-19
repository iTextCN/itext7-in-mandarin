/**
 * Merge PDF/A documents example
 */
package tutorial.chapter07;
 
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.PdfMerger;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
 
@WrapToTest
public class C07E04_MergePDFADocuments {
    public static final String INTENT = "src/main/resources/color/sRGB_CS_profile.icm";
 
    public static final String SRC1 = "src/main/resources/pdf/quick_brown_fox_PDFA-1a.pdf";
    public static final String SRC2 = "src/main/resources/pdf/united_states_PDFA-1a.pdf";
    public static final String DEST = "results/chapter07/merged_PDFA-1a_documents.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C07E04_MergePDFADocuments().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException {
        //Initialize PDFA document with output intent
        PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
            PdfAConformanceLevel.PDF_A_1A,
            new PdfOutputIntent("Custom", "", "http://www.color.org",
                    "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
 
        //Setting some required parameters
        pdf.setTagged();
        pdf.getCatalog().setLang(new PdfString("en-US"));
        pdf.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle("iText7 PDF/A-1a example");
 
        //Create PdfMerger instance
        PdfMerger merger = new PdfMerger(pdf);
        //Add pages from the first document
        PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
        merger.merge(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());
        //Add pages from the second pdf document
        PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
        merger.merge(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());
 
        //Close the documents
        firstSourcePdf.close();
        secondSourcePdf.close();
        pdf.close();
    }
}