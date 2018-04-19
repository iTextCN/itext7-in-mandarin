/**
 * Created by ASUS on 2017/12/24.
 */
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.File;
import java.io.IOException;

public class C07E01_QuickBrownFox_PDFUA {
    public static final String DOG = "src/main/resources/img/dog.bmp";
    public static final String FOX = "src/main/resources/img/fox.bmp";
    public static final String FONT = "src/main/resources/font/FreeSans.ttf";

    public static final String DEST = "results/chapter07/quick_brown_fox_PDFUA.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C07E01_QuickBrownFox_PDFUA().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest, new WriterProperties().addXmpMetadata()));
        Document document = new Document(pdf);

        //Setting some required parameters
        pdf.setTagged();
        pdf.getCatalog().setLang(new PdfString("en-US"));
        pdf.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle("iText7 PDF/UA example");

        //Fonts need to be embedded
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
        Paragraph p = new Paragraph();
        p.setFont(font);
        p.add(new Text("The quick brown "));
        Image foxImage = new Image(ImageDataFactory.create(FOX));
        //PDF/UA: Set alt text
        foxImage.getAccessibilityProperties().setAlternateDescription("Fox");
        p.add(foxImage);
        p.add(" jumps over the lazy ");
        Image dogImage = new Image(ImageDataFactory.create(DOG));
        //PDF/UA: Set alt text
        dogImage.getAccessibilityProperties().setAlternateDescription("Dog");
        p.add(dogImage);

        document.add(p);
        document.close();
    }
}