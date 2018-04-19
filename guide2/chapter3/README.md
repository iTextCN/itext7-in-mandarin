ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-3-using-renderers-and-event-handlers](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-3-using-renderers-and-event-handlers)

# **前言**

&nbsp;&nbsp;&nbsp;&nbsp;大家是否还记得在本系列的第一章中，我们创建了特定页面大小的、特定页面边距的（明确或隐式定义的）``Document``,并且当我们向``Document``对象里面添加基础的绘画块，例如``Paragraph``s和``List``s,iText会确保内容会在页面中组织得很好。同时我们也创建了``Table``对象来显示一个CSV文件的内容并且结果已经显示的很好了。但是如果上述的这一切执行起来都不是很有效率呢？如果我们想要更好地控制内容在网页上的布局，该怎么办？如果您对Table类绘制的矩形边框不满意，该怎么办？如果在每一页特定位置添加内容，无论创建多少页面，该怎么办？  

&nbsp;&nbsp;&nbsp;&nbsp;我们是否可以用第二章提到的在绝对位置上画所有内容的方法来解决这个问题？通过第二章的画星球大战开头文字的例子，我们体会到这可能导致代码非常复杂（代码很难维护）。当然，这里肯定有办法来吧基本构件的高层次api与更低层次的api相结合，使我们能对布局更进一步掌控，这就是这一章————第三章讨论的内容。

# **引入文档渲染器(document renderer)**

&nbsp;&nbsp;&nbsp;&nbsp;假如我们要向一个``Document``里面添加文字和图片，但是我们不想要文字占满整个文档的宽度，相反，我们想要把内容组织成三列，如下图所示：  

![iText3-1](http://obkwqzjnq.bkt.clouddn.com/itext3-1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这个例子可以用下述代码（以下代码都是``NewYorkTimes``类的一部分）：
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PageSize ps = PageSize.A5;
Document document = new Document(pdf, ps);
//Set column parameters
float offSet = 36;
float columnWidth = (ps.getWidth() - offSet * 2 + 10) / 3;
float columnHeight = ps.getHeight() - offSet * 2;
//Define column areas
Rectangle[] columns = {
    new Rectangle(offSet - 5, offSet, columnWidth, columnHeight),
    new Rectangle(offSet + columnWidth, offSet, columnWidth, columnHeight),
    new Rectangle(
        offSet + columnWidth * 2 + 5, offSet, columnWidth, columnHeight)};
document.setRenderer(new ColumnDocumentRenderer(document, columns));
// adding content
Image inst = new Image(ImageDataFactory.getImage(INST_IMG)).setWidth(columnWidth);
String articleInstagram = new String(
    Files.readAllBytes(Paths.get(INST_TXT)), StandardCharsets.UTF_8);
NewYorkTimes.addArticle(document,
    "Instagram May Change Your Feed, Personalizing It With an Algorithm",
    "By MIKE ISAAC MARCH 15, 2016", inst, articleInstagram);
doc.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;前面三行大家应该很熟悉了，在第1章里面介绍过了，第5、6、7行定义了一系列的参数：
- ``offset``变量，使用这个变量来定义上下左右边界的宽度
- 每一列的宽度，``columnWidth``,这个通过划分可计算页面成3份（我们的目标是3列）的方式来计算，可计算页面的大小是整个页面宽度-2*``offset``+10，其中+10的作用是确保每一列之间有空隙
- ``columnHeight``的大小，就是简单的整个页面高度-2*``offset``

&nbsp;&nbsp;&nbsp;&nbsp;我们使用``columns``这个数组变量来存储三个``Rectangle``对象（我们回顾之前，牢记默认坐标系是在左下角，向右为x轴，向上为y轴）：
-  第一个``Rectangle``：左下角坐标为(``offset-5``,``offset``)，宽度为``columnWidth``,高度为``columnHeight``
-  第二个``Rectangle``：左下角坐标为(``offset+columnWidth``,``offset``)，宽度为``columnWidth``,高度为``columnHeight``
-  第三个``Rectangle``：左下角坐标为(``offset+2*columnWidth+5``,``offset``)，宽度为``columnWidth``,高度为``columnHeight``

&nbsp;&nbsp;&nbsp;&nbsp;然后我们使用``columns``这个对象来创建``ColumnDocumentRenderer``，一旦声明这个``ColumnDocumentRenderer``作为``Document``的``DocumentRenderer``,我们向``Document``添加的所有内容都会按照我们定义的三个``Rectangle``的布局来显示。  

&nbsp;&nbsp;&nbsp;&nbsp;在第16行，我们创建了一个``Image``对象，我们按比例缩放这个图像来让它适应矩形的宽度。在第17、18行，我们读取一个文本文件并保存在``String``中，我们利用这些变量作为``addArticle()``的参数，如下所示：
```
public static void addArticle(
    Document doc, String title, String author, Image img, String text)
    throws IOException {
    Paragraph p1 = new Paragraph(title)
            .setFont(timesNewRomanBold)
            .setFontSize(14);
    doc.add(p1);
    doc.add(img);
    Paragraph p2 = new Paragraph()
            .setFont(timesNewRoman)
            .setFontSize(7)
            .setFontColor(Color.GRAY)
            .add(author);
    doc.add(p2);
    Paragraph p3 = new Paragraph()
            .setFont(timesNewRoman)
            .setFontSize(10)
            .add(text);
    doc.add(p3);
}
```
&nbsp;&nbsp;&nbsp;&nbsp;``timesNewRoman``和``timesNewRomanBold``对象是``NewYorkTimes``类的静态成员变量，类型为``PdfFont``，总的来说，这个例子比上一章的例子更简单。接下来我们来看稍微复杂点的例子：

# **使用块渲染器(block renderer)**

&nbsp;&nbsp;&nbsp;&nbsp;在第一章的时候，当我们把美国各洲的信息的csv文件内容显示到PDF中，我们会创建一系列的``Cell``对象，然后添加到``Table``对象中，我们没有定义``Cell``对象的背景颜色还有边框大小，我们使用的是默认的值。

> 默认的配置：一个Cell对象没有背景颜色，边框大小为0.5用户单位

&nbsp;&nbsp;&nbsp;&nbsp;现在我们使用另一个数据源，并把它放进``Table``里面，如下图所示：   

![itext3-2](http://obkwqzjnq.bkt.clouddn.com/itext3-2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;现在讲解怎么来写代码：一开始的代码和之前的代码类似，唯一值得注意的是下面一句代码（整个类为``PremierLeague``类）：
```
PageSize ps = new PageSize(842, 680);
```
&nbsp;&nbsp;&nbsp;&nbsp;在之前，我们是使用的标准的纸张，例如``PageSize.A4``大小。在这个例子中，我们使用的是自己定义的纸张大小：842x680用户单位（1英寸等于72用户单位，也就是11.7x9.4英寸），``PremierLeague``类中的主体代码如下：
```
PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
Table table = new Table(new float[]{1.5f, 7, 2, 2, 2, 2, 3, 4, 4, 2});
table.setWidthPercent(100)
        .setTextAlignment(Property.TextAlignment.CENTER)
        .setHorizontalAlignment(Property.HorizontalAlignment.CENTER);
BufferedReader br = new BufferedReader(new FileReader(DATA));
String line = br.readLine();
process(table, line, bold, true);
while ((line = br.readLine()) != null) {
    process(table, line, font, false);
}
br.close();
document.add(table);
```
&nbsp;&nbsp;&nbsp;&nbsp;与之前第1章显示美国各洲的例子只有一些不同，在这个例子中，我们设置使用``setTextAlignment``和``setHorizontalAlignment``方法来使表格里面内容为居中对齐和表格自身居中对齐（这与表格占用可用宽度的100％无关）。紧接着，我们来看一下``process()``这个更有趣的函数：
```
public void process(Table table, String line, PdfFont font, boolean isHeader) {
    StringTokenizer tokenizer = new StringTokenizer(line, ";");
    int columnNumber = 0;
    while (tokenizer.hasMoreTokens()) {
        if (isHeader) {
            Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
            cell.setNextRenderer(new RoundedCornersCellRenderer(cell));
            cell.setPadding(5).setBorder(null);
            table.addHeaderCell(cell);
        } else {
            columnNumber++;
            Cell cell = new Cell().add(new Paragraph(tokenizer.nextToken()));
            cell.setFont(font).setBorder(new SolidBorder(Color.BLACK, 0.5f));
            switch (columnNumber) {
                case 4:
                    cell.setBackgroundColor(greenColor);
                    break;
                case 5:
                    cell.setBackgroundColor(yellowColor);
                    break;
                case 6:
                    cell.setBackgroundColor(redColor);
                    break;
                default:
                    cell.setBackgroundColor(blueColor);
                    break;
            }
            table.addCell(cell);
        }
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;我们先来看一下普通的语句。在行16、19、22和25，我们根据列号来改变背景颜色。在行13，我们设置``Cell``的字体，并通过``setBorder()``函数覆盖默认的边框，我们将边框定义为黑色实体边框，其宽度为0.5个用户单位。

> ``SolidBorder``继承自``Border``类，它有很多相似的兄弟类，例如``DashedBorder``、``DottedBorder``和``DoubleBorder``等等。如果iText不提供您选择的边框，您可以扩展``Border``类, 您可以使用现有的实现进行灵感，也可以创建自己的``CellRenderer``实现。

&nbsp;&nbsp;&nbsp;&nbsp;我们在行7,8使用了自定义的``RoundedCornersCellRenderer()``，我们规定了内边距(padding)大小，并设置边框为``nul``。如果``setBorder(null)``没有的话，两个边框将会画出来：一个是iText自己画出的，另一个就是我们将要写的内容渲染器画出的边框。我们来看看我们定义的内容渲染器：
```
private class RoundedCornersCellRenderer extends CellRenderer {
    public RoundedCornersCellRenderer(Cell modelElement) {
        super(modelElement);
    }
 
    @Override
    public void drawBorder(DrawContext drawContext) {
        Rectangle rectangle = getOccupiedAreaBBox();
        float llx = rectangle.getX() + 1;
        float lly = rectangle.getY() + 1;
        float urx = rectangle.getX() + getOccupiedAreaBBox().getWidth() - 1;
        float ury = rectangle.getY() + getOccupiedAreaBBox().getHeight() - 1;
        PdfCanvas canvas = drawContext.getCanvas();
        float r = 4;
        float b = 0.4477f;
        canvas.moveTo(llx, lly).lineTo(urx, lly).lineTo(urx, ury - r)
                .curveTo(urx, ury - r * b, urx - r * b, ury, urx - r, ury)
                .lineTo(llx + r, ury)
                .curveTo(llx + r * b, ury, llx, ury - r * b, llx, ury - r)
                .lineTo(llx, lly).stroke();
        super.drawBorder(drawContext);
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;``CellRenderer``类是``BlockRenderer``类的一个特殊实现。

> ``BlockRenderer``类可以在``BlockElements``上使用，如``Paragraph``和List。这些渲染器类允许您通过覆盖``draw()``方法来创建自定义功能。例如：您可以为``Paragraph``创建自定义背景。``CellRenderer``还具有一个``drawBorder()``方法。

&nbsp;&nbsp;&nbsp;&nbsp;我们覆盖``drawBorder()``方法来绘制一个在顶部圆角的矩形（第6-21行）。``getOccupiedAreaBBox()``方法返回一个``Rectangle``对象，我们可以使用它来找到``BlockElement``（第8行）的边界框。我们使用``getX()``，``getY()``，``getWidth()``和``getHeight()``方法来定义单元格的左下角和右上角的坐标（第9-12行）。  
&nbsp;&nbsp;&nbsp;&nbsp;``drawContext``这个参数可以让我们访问``PdfCanvas``实例（第13行）。我们通过一系列线和曲线的形式来画出边框（第14-20行）。此示例演示了如何使用高级api（由``Cell``组成的``Table``）与低级api（我们几乎手动创建PDF语法来绘制符合我们需求的边框。）紧密结合。

> 绘制曲线的代码需要一些关于数学的知识，但它不是像火箭科学那么难。大多数常见类型的边界都在iText中有，所以你不需要担心在iText引擎下数学公式是如何计算的。

&nbsp;&nbsp;&nbsp;&nbsp;关于``BlockRenderer``和它的实现类的知识还有很多，我们将会在另一个教程里详细解释。最后我们将一个示例来结束本章，演示如何自动为创建的每个页面添加背景，页眉（或页脚），水印和页码。

# **处理事件(Handling events,添加背景、页面页脚和水印)**

&nbsp;&nbsp;&nbsp;&nbsp;当我们向文档添加拥有许多行的``Table``时，这个表很可能会分布在不同的页面上。在下图中，我们看到存储在``ufo.csv``中的UFO目录列表。每个奇数页面的背景都是绿黄色，每个偶数页的背景都是蓝色的。每一页有页眉``“THE TRUTH IS OUT THERE”``，在实际的页面内容下有一个水印``“CONFIDENTIAL”``,在每页的底部为中心，有一个**页码**。

![itext3-3](http://obkwqzjnq.bkt.clouddn.com/itext3-3.png)  

&nbsp;&nbsp;&nbsp;&nbsp;以下是产生UFO目录表格的代码，这与第一章的表格显示代码很相似：
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new MyEventHandler());
Document document = new Document(pdf);
Paragraph p = new Paragraph("List of reported UFO sightings in 20th century")
        .setTextAlignment(Property.TextAlignment.CENTER)
        .setFont(helveticaBold).setFontSize(14);
document.add(p);
Table table = new Table(new float[]{3, 5, 7, 4});
table.setWidthPercent(100);
BufferedReader br = new BufferedReader(new FileReader(DATA));
String line = br.readLine();
process(table, line, helveticaBold, true);
while ((line = br.readLine()) != null) {
    process(table, line, helvetica, false);
}
br.close();
document.add(table);
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;在代码中，我们通过设置文本对齐属性为``Property.TextAlignment.CENTER``的方式，来添加居中的``Paragraph``，然后我们循环ufo.csv的方式来显示内容，如下：
```
public void process(Table table, String line, PdfFont font, boolean isHeader) {
        StringTokenizer tokenizer = new StringTokenizer(line, ";");
        while (tokenizer.hasMoreTokens()) {
            if (isHeader) {
                table.addHeaderCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(font)).setFontSize(9).setBorder(new SolidBorder(Color.BLACK, 0.5f)));
            } else {
                table.addCell(new Cell().add(new Paragraph(tokenizer.nextToken()).setFont(font)).setFontSize(9).setBorder(new SolidBorder(Color.BLACK, 0.5f)));
            }
        }
    }
```
&nbsp;&nbsp;&nbsp;&nbsp;``pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new MyEventHandler());``，是不是之前没见过？在这里我们是向``PdfDocument``添加一个事件处理器``MyEventHandler``，这个``MyEventHandler``实现(implement)了``IEventHandler``接口，这个接口只有一个方法：``handleEvent()``。这个方法每当``PdfDocumentEvent.END_PAGE``这类事件出现时就会触发，这类事件指的是：每当iText已经完成向页面添加内容，无论是因为创建了新页面，还是因为最后一页已经到达和完成。  
&nbsp;&nbsp;&nbsp;&nbsp;然后我们来看看``IEventHandler``的实现：
```
protected class MyEventHandler implements IEventHandler {
    public void handleEvent(Event event) {
        PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
        PdfDocument pdfDoc = docEvent.getDocument();
        PdfPage page = docEvent.getPage();
        int pageNumber = pdfDoc.getPageNumber(page);
        Rectangle pageSize = page.getPageSize();
        PdfCanvas pdfCanvas = new PdfCanvas(
            page.newContentStreamBefore(), page.getResources(), pdfDoc);
 
        //Set background
        Color limeColor = new DeviceCmyk(0.208f, 0, 0.584f, 0);
        Color blueColor = new DeviceCmyk(0.445f, 0.0546f, 0, 0.0667f);
        pdfCanvas.saveState()
                .setFillColor(pageNumber % 2 == 1 ? limeColor : blueColor)
                .rectangle(pageSize.getLeft(), pageSize.getBottom(),
                    pageSize.getWidth(), pageSize.getHeight())
                .fill().restoreState();
        //Add header and footer
        pdfCanvas.beginText()
                .setFontAndSize(helvetica, 9)
                .moveText(pageSize.getWidth() / 2 - 60, pageSize.getTop() - 20)
                .showText("THE TRUTH IS OUT THERE")
                .moveText(60, -pageSize.getTop() + 30)
                .showText(String.valueOf(pageNumber))
                .endText();
        //Add watermark
        Canvas canvas = new Canvas(pdfCanvas, pdfDoc, page.getPageSize());
        canvas.setProperty(Property.FONT_COLOR, Color.WHITE);
        canvas.setProperty(Property.FONT_SIZE, 60);
        canvas.setProperty(Property.FONT, helveticaBold);
        canvas.showTextAligned(new Paragraph("CONFIDENTIAL"),
            298, 421, pdfDoc.getPageNumber(page),
            TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
 
        pdfCanvas.release();
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;在行3中，我们先把``event``这个函数参数转换成``PdfDocumentEvent``,然后调用``getDocument()``来获得``PdfDocument``,我们通过这些变量来获得当前页的页码，页面大小还有一个``PdfCanvas``的一个实例。

> 不同的路径或者形状可以重叠，先画的路径或者形状（会保存在内容流content  stream里面）会先画到画布上，后画的图形会覆盖之前的内容(如果有重叠的部分)。每次页面内容完全呈现时，我们要添加一个背景，每个``PdfPage``跟踪内容流数组。您可以使用索引为参数的``getContentStream()``方法来获取每个单独的内容流。您可以使用``getFirstContentStream()``和``getLastContentStream()``获取第一个和最后一个内容流。您还可以使用``newContentStreamBefore()``和``newContentStreamAfter()``方法创建新的内容流。  

&nbsp;&nbsp;&nbsp;&nbsp;在行8，我们通过以下三个参数来构造一个``PdfCanvas``：
- ``page.newContentStreamBefore()``:如果我们在页面呈现之后绘制一个不透明的矩形，那么该矩形将覆盖所有现有的内容。我们需要访问将在页面内容之前添加的内容流，以便我们的背景和我们的水印不覆盖我们的表中的内容。
- ``page.getResources()``:每个内容流都是需要外部资源，如字体和图像。当我们要向页面添加新内容时，iText可以访问该页面的资源目录很重要。
- ``pdfDoc``:我们需要能获取``PdfDocument``对象，这样我们新添加的内容流能添加到``PdfDocument``中。 

&nbsp;&nbsp;&nbsp;&nbsp;然后是我们向``canvas``对象添加的内容：
- 行11-18：定义``limeColor``和``blueColor``两种颜色。首先保存当前**图像状态**（详见第二章有详细解释），然后根据奇偶页数来设置填充笔的颜色，构造一个整个页面大小的矩形，奇数页绿黄色，偶数页蓝色。最后恢复之前的图像状态，不影响之后的内容的颜色。
- 行20-26：开始写文字，设置一种字体样式和字体大小，然后移动到页面最上方的中间，开始写``"THE TRUTH IS OUT THERE"``,然后把光标移动到最底部，写下页码，页眉和页脚就OK。
- 行28-31：这里我们创建了``Canvas``类型的实例``canvas``。``Canvas``是``PdfCanvas``的高级别表示，就跟``Document``是``PdfDocument``的高级别表示一样。在这里我们不适用pdf的语法（第二章里面的语法）来改变字体、字体大小、字体颜色和其他属性，我们使用的是``setProperty()``方法，同样的，在``Document``里可以使用``setProperty()``方法，例如改变默认字体。它可用于同样目的的对象，如``Paragraph``，``List``,``Table``。
- 行32-34：使用``showTextAligned()``方法来添加一个``Paragraph``,居中显示，坐标为(298,421)，45度倾斜。

&nbsp;&nbsp;&nbsp;&nbsp;一旦我们添加了背景、页眉、页脚和水印，我们就释放``PdfCanvas``对象。  
&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们使用两种不同的方法在绝对位置添加文本。关于页眉和页脚的绘制，使用了上一章我们遇见的的低级api——包括text 状态，我们可以使用类似的方法来添加水印。但是，我们要旋转文本并将其置于页面的中间，这需要很多的数学计算。为了避免计算将文本置于所需坐标的转换矩阵，我们使用了一种方便的方法，使用``showTextAligned()``，iText在这边免去了很多繁多的操作。

# **总结**
&nbsp;&nbsp;&nbsp;&nbsp;结合本章样例，我们可以了解到上一章讨论的那些低级api是多么的重要！我们可以将此功能与基本构建块结合使用来创建自定义功能。由此创建了单元格对象的自定义边框；向页面添加了背景颜色、页眉和页脚；最后当我们添加水印时，我们发现并不需要知道PDF语法的所有的input和output，这里可以使用一种方便的方法来处理定义转换矩阵来使文本旋转和居中。  
&nbsp;&nbsp;&nbsp;&nbsp;在下一章，我们将了解一种不同形式的内容————注解，我们将专注于一些特定类型的注释，这些注释能够创建交互式表单。希望大家继续关注

