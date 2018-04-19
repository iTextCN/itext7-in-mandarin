/**
 * Created by ASUS on 2017/12/7.
 */
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;

import java.io.*;
import java.util.Map;
import java.util.StringTokenizer;

public class C06E08_FillOutAndMergeForms {
    public static final String DEST = "results/chapter06/fill_out_and_merge_forms.pdf";
    public static final String SRC = "src/main/resources/pdf/state.pdf";
    public static final String DATA = "src/main/resources/data/united_states.csv";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E08_FillOutAndMergeForms().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest));
        PdfPageFormCopier formCopier = new PdfPageFormCopier();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(DATA));
        String line;
        boolean headerLine = true;
        int i = 1;
        while ((line = bufferedReader.readLine()) != null) {
            if (headerLine) {
                headerLine = false;
                continue;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfDocument sourcePdfDocument = new PdfDocument(new PdfReader(SRC), new PdfWriter(baos));

            //Rename fields
            i++;
            PdfAcroForm form = PdfAcroForm.getAcroForm(sourcePdfDocument, true);
            form.renameField("name", "name_" + i);
            form.renameField("abbr", "abbr_" + i);
            form.renameField("capital", "capital_" + i);
            form.renameField("city", "city_" + i);
            form.renameField("population", "population_" + i);
            form.renameField("surface", "surface_" + i);
            form.renameField("timezone1", "timezone1_" + i);
            form.renameField("timezone2", "timezone2_" + i);
            form.renameField("dst", "dst_" + i);

            //Fill out fields
            StringTokenizer tokenizer = new StringTokenizer(line, ";");
            Map<String, PdfFormField> fields = form.getFormFields();
            fields.get("name_" + i).setValue(tokenizer.nextToken());
            fields.get("abbr_" + i).setValue(tokenizer.nextToken());
            fields.get("capital_" + i).setValue(tokenizer.nextToken());
            fields.get("city_" + i).setValue(tokenizer.nextToken());
            fields.get("population_" + i).setValue(tokenizer.nextToken());
            fields.get("surface_" + i).setValue(tokenizer.nextToken());
            fields.get("timezone1_" + i).setValue(tokenizer.nextToken());
            fields.get("timezone2_" + i).setValue(tokenizer.nextToken());
            fields.get("dst_" + i).setValue(tokenizer.nextToken());

            sourcePdfDocument.close();
            sourcePdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(baos.toByteArray())));

            //Copy pages
            sourcePdfDocument.copyPagesTo(1, sourcePdfDocument.getNumberOfPages(), pdfDocument, formCopier);
            sourcePdfDocument.close();
        }

        bufferedReader.close();
        pdfDocument.close();
    }
}
