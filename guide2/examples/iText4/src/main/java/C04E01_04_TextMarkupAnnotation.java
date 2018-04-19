/**
 * Created by ASUS on 2017/9/12.
 */
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextMarkupAnnotation;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;

import java.io.File;
import java.io.IOException;

/**
 * Simple text markup annotation example.
 */
public class C04E01_04_TextMarkupAnnotation {

    public static final String DEST = "results/chapter04/textmarkup_annotation.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C04E01_04_TextMarkupAnnotation().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        //Initialize document
        Document document = new Document(pdf);

        Paragraph p = new Paragraph("The example of text markup annotation.");
        document.showTextAligned(p, 20, 795, 1, TextAlignment.LEFT,
                VerticalAlignment.MIDDLE, 0);

        //Create text markup annotation
        PdfAnnotation ann = PdfTextMarkupAnnotation.createHighLight(new Rectangle(105, 790, 64, 10),
                new float[]{169, 790, 105, 790, 169, 800, 105, 800})
                .setColor(Color.YELLOW)
                .setTitle(new PdfString("Hello!"))
                .setContents(new PdfString("I'm a popup."))
                .setTitle(new PdfString("iText"))
                .setOpen(true)
                .setRectangle(new PdfArray(new float[]{100, 600, 200, 100}));
        pdf.getFirstPage().addAnnotation(ann);

        //Close document
        document.close();

    }
}