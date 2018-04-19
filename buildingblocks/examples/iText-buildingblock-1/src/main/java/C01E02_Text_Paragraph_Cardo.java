/**
 * Created by ASUS on 2018/4/11.
 */
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.File;
import java.io.IOException;


public class C01E02_Text_Paragraph_Cardo {

    public static final String DEST = "results/chapter01/text_paragraph_cardo.pdf";

    public static final String REGULAR = "src/main/resources/fonts/Cardo-Regular.ttf";
    public static final String BOLD = "src/main/resources/fonts/Cardo-Bold.ttf";
    public static final String ITALIC = "src/main/resources/fonts/Cardo-Italic.ttf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E02_Text_Paragraph_Cardo().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        // Initialize document
        Document document = new Document(pdf);

        // Add content
        FontProgram fontProgram = FontProgramFactory.createFont(REGULAR);
        PdfFont font = PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, true);
        PdfFont bold = PdfFontFactory.createFont(BOLD, true);
        PdfFont italic = PdfFontFactory.createFont(ITALIC, true);
        Text title = new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
        Text author = new Text("Robert Louis Stevenson").setFont(font);
        Paragraph p = new Paragraph().setFont(italic).add(title).add(" by ").add(author);
        document.add(p);

        //Close document
        document.close();
    }
}
