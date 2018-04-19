/**
 * Created by ASUS on 2018/4/11.
 */
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.IOException;


public class C01E06_Czech_Russian_Korean_Unicode {

    public static final String DEST = "results/chapter01/czech_russian_korean_unicode.pdf";

    public static final String FONT = "src/main/resources/fonts/FreeSans.ttf";
    public static final String HCRBATANG = "src/main/resources/fonts/HANBatang.ttf";

    public static final String CZECH =
            "Podivn\u00fd p\u0159\u00edpad Dr. Jekylla a pana Hyda";
    public static final String RUSSIAN =
            "\u0421\u0442\u0440\u0430\u043d\u043d\u0430\u044f "
                    + "\u0438\u0441\u0442\u043e\u0440\u0438\u044f "
                    + "\u0434\u043e\u043a\u0442\u043e\u0440\u0430 "
                    + "\u0414\u0436\u0435\u043a\u0438\u043b\u0430 \u0438 "
                    + "\u043c\u0438\u0441\u0442\u0435\u0440\u0430 "
                    + "\u0425\u0430\u0439\u0434\u0430";
    public static final String KOREAN =
            "\ud558\uc774\ub4dc, \uc9c0\ud0ac, \ub098";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E06_Czech_Russian_Korean_Unicode().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        // Initialize document
        Document document = new Document(pdf);

        // Add content
        PdfFont freeUnicode = PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);
        document.add(new Paragraph().setFont(freeUnicode)
                .add(CZECH).add(" by Robert Louis Stevenson"));
        document.add(new Paragraph().setFont(freeUnicode)
                .add(RUSSIAN).add(" by Robert Louis Stevenson"));
        PdfFont fontUnicode = PdfFontFactory.createFont(HCRBATANG, PdfEncodings.IDENTITY_H, true);
        document.add(new Paragraph().setFont(fontUnicode)
                .add(KOREAN).add(" by Robert Louis Stevenson"));

        //Close document
        document.close();
    }
}
