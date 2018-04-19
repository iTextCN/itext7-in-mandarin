/**
 * Created by ASUS on 2017/8/12.
 */
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.io.File;
import java.io.IOException;

public class C02E01_Axes {

    public static final String DEST = "results/chapter02/axes.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C02E01_Axes().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);
        //Replace the origin of the coordinate system to the center of the page
        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);

        C02E01_Axes.drawAxes(canvas, ps);

        //Close document
        pdf.close();

    }

    public static void drawAxes(PdfCanvas canvas, PageSize ps) {
        //Draw X axis
        canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .stroke();

        //Draw X axis arrow
        canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(ps.getWidth() / 2 - 25, -10)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);

        //Draw Y axis
        canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
                .lineTo(0, ps.getHeight() / 2 - 15)
                .stroke();

        //Draw Y axis arrow
        canvas.saveState()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(-10, ps.getHeight() / 2 - 25)
                .lineTo(0, ps.getHeight() / 2 - 15)
                .lineTo(10, ps.getHeight() / 2 - 25).stroke()
                .restoreState();

        //Draw X serif
        for (int i = -((int) ps.getWidth() / 2 - 61); i < ((int) ps.getWidth() / 2 - 60); i += 40) {
            canvas.moveTo(i, 5).lineTo(i, -5);
        }
        //Draw Y serif
        for (int j = -((int) ps.getHeight() / 2 - 57); j < ((int) ps.getHeight() / 2 - 56); j += 40) {
            canvas.moveTo(5, j).lineTo(-5, j);
        }
        canvas.stroke();
    }
}
