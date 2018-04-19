/*
 * This example is part of the iText 7 tutorial.
 */
package tutorial.chapter04;
 
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfLineAnnotation;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.IOException;
 
/**
 * Simple line annotation example.
 */
@WrapToTest
public class C04E01_03_LineAnnotation {
 
    public static final String DEST = "results/chapter04/line_annotation.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C04E01_03_LineAnnotation().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException {
 
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PdfPage page = pdf.addNewPage();
 
        PdfArray lineEndings = new PdfArray();
        lineEndings.add(new PdfName("Diamond"));
        lineEndings.add(new PdfName("Diamond"));
 
        //Create line annotation with inside caption
        PdfAnnotation annotation = new PdfLineAnnotation(
            new Rectangle(0, 0),
            new float[]{20, 790, page.getPageSize().getWidth() - 20, 790})
                .setLineEndingStyles((lineEndings))
                .setContentsAsCaption(true)
                .setTitle(new PdfString("iText"))
                .setContents("The example of line annotation")
                .setColor(Color.BLUE);
        page.addAnnotation(annotation);
 
        //Close document
        pdf.close();
 
    }
}