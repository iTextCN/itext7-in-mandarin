/**
 * Created by ASUS on 2018/4/11.
 */
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;

import java.io.File;
import java.io.IOException;


public class C01E08_BoldItalic {

    public static final String DEST = "results/chapter01/bold_italic.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C01E08_BoldItalic().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        // Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        // Initialize document
        Document document = new Document(pdf);

        // Add content
        Text title1 = new Text("The Strange Case of ").setItalic();
        Text title2 = new Text("Dr. Jekyll and Mr. Hyde").setBold();
        Text author = new Text("Robert Louis Stevenson").setItalic().setBold();
        Paragraph p = new Paragraph()
                .add(title1).add(title2).add(" by ").add(author);
        document.add(p);

        //Close document
        document.close();
    }
}
