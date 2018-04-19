/*
 * This example is part of the iText 7 tutorial.
 */
package tutorial.chapter04;
 
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.IOException;
 
/**
 * Simple text annotation example.
 */
@WrapToTest
public class C04E01_01_TextAnnotation {
 
    public static final String DEST = "results/chapter04/text_annotation.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C04E01_01_TextAnnotation().createPdf(DEST);
    }
 
    public void createPdf(String dest) throws IOException {
 
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
 
        //Initialize document
        Document document = new Document(pdf);
        document.add(new Paragraph("The example of text annotation."));
 
        //Create text annotation
        PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(20, 800, 0, 0))
                .setColor(Color.GREEN)
                .setTitle(new PdfString("iText"))
                .setContents("With iText, you can truly take your documentation needs to the next level.")
                .setOpen(true);
        pdf.getFirstPage().addAnnotation(ann);
 
        //Close document
        document.close();
 
    }
}