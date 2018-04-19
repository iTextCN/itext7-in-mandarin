/**
 * Created by ASUS on 2018/4/11.
 */
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.File;
import java.io.IOException;


public class C01E10_ReusingStyles {

    public static final String DEST = "results/chapter01/style_example.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E10_ReusingStyles().createPdf(DEST);
    }
    public void createPdf(String dest) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Document document = new Document(pdf);
        Style normal = new Style();
        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        normal.setFont(font).setFontSize(14);
        Style code = new Style();
        PdfFont monospace = PdfFontFactory.createFont(FontConstants.COURIER);
        code.setFont(monospace).setFontColor(Color.RED)
                .setBackgroundColor(Color.LIGHT_GRAY);
        Paragraph p = new Paragraph();
        p.add(new Text("The Strange Case of ").addStyle(normal));
        p.add(new Text("Dr. Jekyll").addStyle(code));
        p.add(new Text(" and ").addStyle(normal));
        p.add(new Text("Mr. Hyde").addStyle(code));
        p.add(new Text(".").addStyle(normal));
        document.add(p);
        document.close();
    }
}
