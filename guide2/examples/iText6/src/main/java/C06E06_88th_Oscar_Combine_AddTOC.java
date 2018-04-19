/**
 * Created by ASUS on 2017/12/7.
 */
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.draw.DottedLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class C06E06_88th_Oscar_Combine_AddTOC {
    public static final String SRC1 = "src/main/resources/pdf/88th_noms_announcement.pdf";
    public static final String SRC2 = "src/main/resources/pdf/oscars_movies_checklist_2016.pdf";
    public static final String DEST = "results/chapter06/88th_oscar_the_revenant_nominations_TOC.pdf";

    public static final Map<String, Integer> TheRevenantNominations = new TreeMap<String, Integer>();
    static {
        TheRevenantNominations.put("Performance by an actor in a leading role", 4);
        TheRevenantNominations.put("Performance by an actor in a supporting role", 4);
        TheRevenantNominations.put("Achievement in cinematography", 4);
        TheRevenantNominations.put("Achievement in costume design", 5);
        TheRevenantNominations.put("Achievement in directing", 5);
        TheRevenantNominations.put("Achievement in film editing", 6);
        TheRevenantNominations.put("Achievement in makeup and hairstyling", 7);
        TheRevenantNominations.put("Best motion picture of the year", 8);
        TheRevenantNominations.put("Achievement in production design", 8);
        TheRevenantNominations.put("Achievement in sound editing", 9);
        TheRevenantNominations.put("Achievement in sound mixing", 9);
        TheRevenantNominations.put("Achievement in visual effects", 10);
    }

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E06_88th_Oscar_Combine_AddTOC().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdfDoc);
        document.add(new Paragraph(new Text("The Revenant nominations list"))
                .setTextAlignment(TextAlignment.CENTER));

        PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
        for (Map.Entry<String, Integer> entry : TheRevenantNominations.entrySet()) {
            //Copy page
            PdfPage page  = firstSourcePdf.getPage(entry.getValue()).copyTo(pdfDoc);
            pdfDoc.addPage(page);

            //Overwrite page number
            Text text = new Text(String.format("Page %d", pdfDoc.getNumberOfPages() - 1));
            text.setBackgroundColor(Color.WHITE);
            document.add(new Paragraph(text).setFixedPosition(
                    pdfDoc.getNumberOfPages(), 549, 742, 100));

            //Add destination
            String destinationKey = "p" + (pdfDoc.getNumberOfPages() - 1);
            PdfArray destinationArray = new PdfArray();
            destinationArray.add(page.getPdfObject());
            destinationArray.add(PdfName.XYZ);
            destinationArray.add(new PdfNumber(0));
            destinationArray.add(new PdfNumber(page.getMediaBox().getHeight()));
            destinationArray.add(new PdfNumber(1));
            pdfDoc.addNamedDestination(destinationKey, destinationArray);

            //Add TOC line with bookmark
            Paragraph p = new Paragraph();
            p.addTabStops(new TabStop(540, TabAlignment.RIGHT, new DottedLine()));
            p.add(entry.getKey());
            p.add(new Tab());
            p.add(String.valueOf(pdfDoc.getNumberOfPages() - 1));
            p.setProperty(Property.ACTION, PdfAction.createGoTo(destinationKey));
            document.add(p);
        }
        firstSourcePdf.close();

        //Add the last page
        PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
        PdfPage page  = secondSourcePdf.getPage(1).copyTo(pdfDoc);
        pdfDoc.addPage(page);

        //Add destination
        PdfArray destinationArray = new PdfArray();
        destinationArray.add(page.getPdfObject());
        destinationArray.add(PdfName.XYZ);
        destinationArray.add(new PdfNumber(0));
        destinationArray.add(new PdfNumber(page.getMediaBox().getHeight()));
        destinationArray.add(new PdfNumber(1));
        pdfDoc.addNamedDestination("checklist", destinationArray);

        //Add TOC line with bookmark
        Paragraph p = new Paragraph();
        p.addTabStops(new TabStop(540, TabAlignment.RIGHT, new DottedLine()));
        p.add("Oscars\u00ae 2016 Movie Checklist");
        p.add(new Tab());
        p.add(String.valueOf(pdfDoc.getNumberOfPages() - 1));
        p.setProperty(Property.ACTION, PdfAction.createGoTo("checklist"));
        document.add(p);
        secondSourcePdf.close();

        // close the document
        document.close();
    }
}
