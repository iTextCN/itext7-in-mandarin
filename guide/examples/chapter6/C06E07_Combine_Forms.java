/*
 * This example is part of the iText 7 tutorial.
 */
package tutorial.chapter06;
 
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.IOException;
 
@WrapToTest
public class C06E07_Combine_Forms {
    public static final String DEST = "results/chapter06/combined_forms.pdf";
    public static final String SRC1 = "src/main/resources/pdf/subscribe.pdf";
    public static final String SRC2 = "src/main/resources/pdf/state.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E07_Combine_Forms().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException {
        PdfDocument destPdfDocument = new PdfDocument(new PdfWriter(dest));
        PdfDocument[] sources = new PdfDocument[] {
                new PdfDocument(new PdfReader(SRC1)),
                new PdfDocument(new PdfReader(SRC2))
        };
        PdfPageFormCopier formCopier = new PdfPageFormCopier();
        for (PdfDocument sourcePdfDocument : sources) {
            sourcePdfDocument.copyPagesTo(
                    1, sourcePdfDocument.getNumberOfPages(),
                    destPdfDocument, formCopier);
            sourcePdfDocument.close();
        }
        destPdfDocument.close();
    }
}