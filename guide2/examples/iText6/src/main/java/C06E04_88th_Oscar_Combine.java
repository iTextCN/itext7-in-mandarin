/**
 * Created by ASUS on 2017/12/7.
 */
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.utils.PdfMerger;

import java.io.File;
import java.io.IOException;

public class C06E04_88th_Oscar_Combine {
    public static final String SRC1 = "src/main/resources/pdf/88th_reminder_list.pdf";
    public static final String SRC2 = "src/main/resources/pdf/88th_noms_announcement.pdf";
    public static final String DEST = "results/chapter06/88th_oscar_combined_documents.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E04_88th_Oscar_Combine().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDF document with output intent
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PdfMerger merger = new PdfMerger(pdf);

        //Add pages from the first document
        PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
        merger.merge(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());

        //Add pages from the second pdf document
        PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
        merger.merge(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());

        firstSourcePdf.close();
        secondSourcePdf.close();
        pdf.close();
    }
}