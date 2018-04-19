/*
 * This example is part of the iText 7 tutorial.
 */

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import java.io.File;
import java.io.IOException;

/**
 * Simple Hello World example.
 */
public class C01E01_HelloWorld {

    public static final String DEST = "results/chapter01/hello_world.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();             //创建目录
        new C01E01_HelloWorld().createPdf(DEST);        //生成文件
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDF writer
        PdfWriter writer = new PdfWriter(dest);

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(writer);

        // Initialize document
        Document document = new Document(pdf);

        //Add paragraph to the document
        document.add(new Paragraph("Hello World!"));

        //Close document
        document.close();
    }
}