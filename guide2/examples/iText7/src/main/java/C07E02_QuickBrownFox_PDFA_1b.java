/**
 * Created by ASUS on 2017/12/24.
 */
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.pdfa.PdfADocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


public class C07E02_QuickBrownFox_PDFA_1b {
    public static final String DOG = "src/main/resources/img/dog.bmp";
    public static final String FOX = "src/main/resources/img/fox.bmp";
    public static final String FONT = "src/main/resources/font/FreeSans.ttf";
    public static final String INTENT = "src/main/resources/color/sRGB_CS_profile.icm";

    public static final String DEST = "results/chapter07/quick_brown_fox_PDFA-1b.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C07E02_QuickBrownFox_PDFA_1b().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDFA document with output intent
        PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
                PdfAConformanceLevel.PDF_A_1B,
                new PdfOutputIntent("Custom", "", "http://www.color.org",
                        "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
        Document document = new Document(pdf);

        //Fonts need to be embedded
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
        Paragraph p = new Paragraph();
        p.setFont(font);
        p.add(new Text("The quick brown "));
        Image foxImage = new Image(ImageDataFactory.create(FOX));
        p.add(foxImage);
        p.add(" jumps over the lazy ");
        Image dogImage = new Image(ImageDataFactory.create(DOG));
        p.add(dogImage);

        document.add(p);
        document.close();
    }
}