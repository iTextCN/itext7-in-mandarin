/**
 * Created by ASUS on 2017/11/17.
 */
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfTextAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import java.io.File;
import java.io.IOException;

/**
 * Simple adding annotations example.
 */
public class C05E01_AddAnnotationsAndContent {

    public static final String SRC = "src/main/resources/pdf/job_application.pdf";
    public static final String DEST = "results/chapter05/edited_job_application.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C05E01_AddAnnotationsAndContent().manipulatePdf(SRC, DEST);
    }

    public void manipulatePdf(String src, String dest) throws IOException {

        //Initialize PDF document
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(src), new PdfWriter(dest));

        //Add text annotation
        PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(400, 795, 0, 0))
                .setTitle(new PdfString("iText"))
                .setContents("Please, fill out the form.")
                .setOpen(true);
        pdfDoc.getFirstPage().addAnnotation(ann);

        PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
        canvas.beginText().setFontAndSize(PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
                .moveText(265, 597)
                .showText("I agree to the terms and conditions.")
                .endText();

        //Add form field
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        PdfButtonFormField checkField = PdfFormField.createCheckBox(pdfDoc, new Rectangle(245, 594, 15, 15),
                "agreement", "Off", PdfFormField.TYPE_CHECK);
        checkField.setRequired(true);
        form.addField(checkField);

        //Update reset button
        form.getField("reset").setAction(PdfAction.createResetForm(new String[]{"name", "language",
                "experience1", "experience2", "experience3", "shift", "info", "agreement"}, 0));

        pdfDoc.close();

    }
}
