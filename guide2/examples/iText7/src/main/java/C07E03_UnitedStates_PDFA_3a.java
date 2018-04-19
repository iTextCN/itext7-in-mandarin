/**
 * Created by ASUS on 2017/12/24.
 */
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.pdfa.PdfADocument;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

public class C07E03_UnitedStates_PDFA_3a {
    public static final String DATA = "src/main/resources/data/united_states.csv";
    public static final String FONT = "src/main/resources/font/FreeSans.ttf";
    public static final String BOLD_FONT = "src/main/resources/font/FreeSansBold.ttf";
    public static final String INTENT = "src/main/resources/color/sRGB_CS_profile.icm";


    public static final String DEST = "results/chapter07/united_states_PDFA-3a.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C07E03_UnitedStates_PDFA_3a().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
                PdfAConformanceLevel.PDF_A_3A,
                new PdfOutputIntent("Custom", "", "http://www.color.org",
                        "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(20, 20, 20, 20);

        //Setting some required parameters
        pdf.setTagged();
        pdf.getCatalog().setLang(new PdfString("en-US"));
        pdf.getCatalog().setViewerPreferences(
                new PdfViewerPreferences().setDisplayDocTitle(true));
        PdfDocumentInfo info = pdf.getDocumentInfo();
        info.setTitle("iText7 PDF/A-3 example");

        //Add attachment
        PdfDictionary parameters = new PdfDictionary();
        parameters.put(PdfName.ModDate, new PdfDate().getPdfObject());
        PdfFileSpec fileSpec = PdfFileSpec.createEmbeddedFileSpec(
                pdf, Files.readAllBytes(Paths.get(DATA)), "united_states.csv",
                "united_states.csv", new PdfName("text/csv"), parameters,
                PdfName.Data, false);
        fileSpec.put(new PdfName("AFRelationship"), new PdfName("Data"));
        pdf.addFileAttachment("united_states.csv", fileSpec);
        PdfArray array = new PdfArray();
        array.add(fileSpec.getPdfObject().getIndirectReference());
        pdf.getCatalog().put(new PdfName("AF"), array);

        //Embed fonts
        PdfFont font = PdfFontFactory.createFont(FONT, true);
        PdfFont bold = PdfFontFactory.createFont(BOLD_FONT, true);

        // Create content
        Table table = new Table(new float[]{4, 1, 3, 4, 3, 3, 3, 3, 1});
        table.setWidthPercent(100);
        BufferedReader br = new BufferedReader(new FileReader(DATA));
        String line = br.readLine();
        process(table, line, bold, true);
        while ((line = br.readLine()) != null) {
            process(table, line, font, false);
        }
        br.close();
        document.add(table);

        //Close document
        document.close();
    }

    public void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(new Cell().setHorizontalAlignment(HorizontalAlignment.CENTER).add(new Paragraph(tokenizer.nextToken()).setHorizontalAlignment(HorizontalAlignment.CENTER).setFont(font)));
            } else {
                table.addCell(new Cell().setHorizontalAlignment(HorizontalAlignment.CENTER).add(new Paragraph(tokenizer.nextToken()).setHorizontalAlignment(HorizontalAlignment.CENTER).setFont(font)));
            }
        }
    }
}