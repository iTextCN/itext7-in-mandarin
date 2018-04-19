/**
 * Created by ASUS on 2017/12/7.
 */
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

import java.io.File;
import java.io.IOException;

public class C06E03_TheGoldenGateBridge_N_up {
    public static final String SRC = "src/main/resources/pdf/the_golden_gate_bridge.pdf";
    public static final String DEST = "results/chapter06/the_golden_gate_bridge_nup.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E03_TheGoldenGateBridge_N_up().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PdfDocument sourcePdf = new PdfDocument(new PdfReader(SRC));

        //Original page
        PdfPage origPage = sourcePdf.getPage(1);

        //Original page size
        Rectangle orig = origPage.getPageSize();
        PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);

        //N-up page
        PageSize nUpPageSize = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(nUpPageSize);
        PdfCanvas canvas = new PdfCanvas(page);

        //Scale page
        AffineTransform transformationMatrix = AffineTransform.getScaleInstance(nUpPageSize.getWidth() / orig.getWidth() / 2f, nUpPageSize.getHeight() / orig.getHeight() / 2f);
        canvas.concatMatrix(transformationMatrix);

        //Add pages to N-up page
        canvas.addXObject(pageCopy, 0, orig.getHeight());
        canvas.addXObject(pageCopy, orig.getWidth(), orig.getHeight());
        canvas.addXObject(pageCopy, 0, 0);
        canvas.addXObject(pageCopy, orig.getWidth(), 0);

        pdf.close();
        sourcePdf.close();
    }
}