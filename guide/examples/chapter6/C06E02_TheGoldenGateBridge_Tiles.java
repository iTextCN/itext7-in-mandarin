/*
 * This example is part of the iText 7 tutorial.
 */
package tutorial.chapter06;
 
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.test.annotations.WrapToTest;
 
import java.io.File;
import java.io.IOException;
 
@WrapToTest
public class C06E02_TheGoldenGateBridge_Tiles {
    public static final String SRC = "src/main/resources/pdf/the_golden_gate_bridge.pdf";
    public static final String DEST = "results/chapter06/the_golden_gate_bridge_tiles.pdf";
 
    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new C06E02_TheGoldenGateBridge_Tiles().createPdf(SRC, DEST);
    }
 
    public void createPdf(String src, String dest) throws IOException {
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PdfDocument sourcePdf = new PdfDocument(new PdfReader(src));
 
        //Original page
        PdfPage origPage = sourcePdf.getPage(1);
        PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);
 
        //Original page size
        Rectangle orig = origPage.getPageSize();
        //Tile size
        Rectangle tileSize = PageSize.A4.rotate();
        // Transformation matrix
        AffineTransform transformationMatrix = AffineTransform.getScaleInstance(tileSize.getWidth() / orig.getWidth() * 2f, tileSize.getHeight() / orig.getHeight() * 2f);
 
 
        //The first tile
        PdfPage page = pdf.addNewPage(PageSize.A4.rotate());
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.concatMatrix(transformationMatrix);
        canvas.addXObject(pageCopy, 0, -orig.getHeight() / 2f);
 
        //The second tile
        page = pdf.addNewPage(PageSize.A4.rotate());
        canvas = new PdfCanvas(page);
        canvas.concatMatrix(transformationMatrix);
        canvas.addXObject(pageCopy, -orig.getWidth() / 2f, -orig.getHeight() / 2f);
 
        //The third tile
        page = pdf.addNewPage(PageSize.A4.rotate());
        canvas = new PdfCanvas(page);
        canvas.concatMatrix(transformationMatrix);
        canvas.addXObject(pageCopy, 0, 0);
 
        //The fourth tile
        page = pdf.addNewPage(PageSize.A4.rotate());
        canvas = new PdfCanvas(page);
        canvas.concatMatrix(transformationMatrix);
        canvas.addXObject(pageCopy, -orig.getWidth() / 2f, 0);
 
        pdf.close();
        sourcePdf.close();
    }
}