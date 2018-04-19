ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-3-using-renderers-and-event-handlers](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-3-using-renderers-and-event-handlers)

## 第3章：渲染器和事件处理程序

在本教程的第1章中，我们创建了一个具有指定页面大小和页边距的文档（明确或者含蓄的进行了定义），并且在该文档对象中添加了段落和列表等基本的构建块。iText能够确保在页面上很好的组织内容。我们还创建了一个表格对象来导出CSV文件的内容，而且导出的表格样式也相当好看。但如果这些都还不够用呢？如果我们还想要更好的控制内容在页面上的布局呢？如果您对Table类绘制的矩形边框不满意怎么办？如果您想在所有生成页面的指定位置上添加内容怎么办？

在第二章中，为了满足一些特定的需要，你必须写明绝对位置的坐标来绘制内容，但这是否真的有必要呢？通过“星球大战”的例子，我们很清楚这样写的结果就是得到一段难以维护的复杂代码。答案是没有必要的，我们可以通过基本构建模块的高级方法和低级方法混合使用的方式来操作更复杂的页面布局。而如何去操作就是第三章要说明的内容。

### 文档渲染器

假设我们要将一段文本和几张图片添加到一个文档上，但不希望文本横跨整个页面，而是像图3.1那样被分成三列。

![Figure 3.1: Text and images organized in columns](https://developers.itextpdf.com/sites/default/files/C03F01.png)
<p align="center">图3.1: 文本和图片按列排放</p>

具体代码请看[NewYorkTimes](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-3#1742-c03e01_newyorktimes.java)这个案例：

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
Image inst = new Image(ImageDataFactory.create(INST_IMG)).setWidth(columnWidth);
String articleInstagram = new String(
    Files.readAllBytes(Paths.get(INST_TXT)), StandardCharsets.UTF_8);
 
 // The method addArticle is defined in the full  NewYorkTimes sample
NewYorkTimes.addArticle(document,
    "Instagram May Change Your Feed, Personalizing It With an Algorithm",
    "By MIKE ISAAC MARCH 15, 2016", inst, articleInstagram);
 
document.close();
```

前面五行代码是非常标准的样板代码，在第一章的时候就已经介绍过了。从第五行开始，我们定义了几个新的参数：

* 代码中的offSet这个变量，被用于定义顶部和底部的边距，以及右侧和左侧的边距。

* columnWidth这个变量代表每一列的宽度，这个值是通过将可用页面宽度除以3计算得出（因为我们只要3列）。这句话的“可用页面宽度”是整个页面宽度减去左右边距，再从边距中减去5个用户单位，以便列与列之间可以又一个小的间距。

* columnHeight这个变量代表每一列的高度，通过整个页面的高度减去顶部和底部的边距计算得出。

我们接下来使用这些变量又定义了三个Rectangle矩形对象：

* 第一个矩形的左下角坐标为（X = offSet - 5 , Y = offSet），宽为columnWidth，高为columnHeight。

* 第二个矩形的左下角坐标为（X = offSet + columnWidth , Y = offSet），宽为columnWidth，高为columnHeight。

* 第三个矩形的左下角坐标为（X = offSet + columnWidth * 2 + 5 , Y = offSet），宽为columnWidth，高为heightColumn。

我们把这三个Rectangle矩形对象放置在一个名为columns的数组中，并把它作为构造函数的参数来实例化一个ColumnDocumentRenderer。新实例化出来的ColumnDocumentRenderer又被作为DocumentRenderer的类传入我们的文档实例中，接下来，我们添加到文档的所有内容都将被放置在刚定义的三个Rectangle所对应的列中。

在第19行，我们创建了一个Image对象并把它进行了适当的缩放以适应列的宽度。在第20和21行中，我们读取了一个文本文件的内容并放入一个字符串变量中。这一系列的对象将被作为我们自定义方法addArticle()的参数。

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

上述代码并没有引入新的概念。timesNewRoman和timesNewRomanBold这两个PdfFont对象都是NewYorkTimes类的静态成员变量，这样的形式去引用字体比我们上一章的做法要更加容易。接下来，看一个稍微复杂一点的例子。

### 方块渲染器的使用

之前的案例中，有一个包含美国各州信息的CSV文件被我们读取内容发布到PDF上来，为此，我们在表格对象中创建了一系列的单元格对象，但我们没有定义背景颜色，也没有定义边框的样式，全部都是使用的默认值。

> 一般而言，一个表单的单元格没有背景颜色，边框也是由0.5个用户单位的黑色矩形组成。

我们现在把另外一个[premier_league.csv](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/data/premier_league.csv)数据源放入一个表格中，但是这次稍微做了一些调整，请参考图3.2.

![Figure 3.2: a table with colored cells and rounded borders](https://developers.itextpdf.com/sites/default/files/C03F02_0.png)
<p align="center">图3.2:五颜六色的表格</p>

我们不会重复样板代码，因为它与之前例子中的代码相同，除了这一行：

```
PageSize ps = new PageSize(842, 680);
```

我们在之前的案例都是用的A4纸大小，这次用自定义的842 * 680 pt（17.7 ＊ 9.4 in）。写起来就跟[PremierLeague](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-3#1743-c03e02_premierleague.java)这个案例所示范的那样简单。

```
PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
Table table = new Table(new float[]{1.5f, 7, 2, 2, 2, 2, 3, 4, 4, 2});
table.setWidthPercent(100)
        .setTextAlignment(TextAlignment.CENTER)
        .setHorizontalAlignment(HorizontalAlignment.CENTER);
BufferedReader br = new BufferedReader(new FileReader(DATA));
String line = br.readLine();
process(table, line, bold, true);
while ((line = br.readLine()) != null) {
    process(table, line, font, false);
}
br.close();
document.add(table);
```

上面的代码跟这个[UnitedStates](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-1#1726-c01e04_unitedstates.java)例子比起来，只有细微的差别。在这个例子中，我们把表格的文本内容设置为了居中对齐，并修改了表格本身的水平对齐方式，当然，这并不重要，因为表格本来就占用了可用宽度的100%。下面要说的这个process()方法相对来说要更有趣一点。

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

先从最普通的单元格开始说起，在第16，19，22以及25行中，我们根据列号改变了背景颜色。

在第13行，设置了单元格内容的字体，并使用setBorder()方法替换了默认的边框，这个方法将边框重新定义为一个0.5pt线宽的黑色实边框。

> SolidBorder是Border类的一个子类，它也有像DashedBorder、DottedBorder、DoubleBorder等等这样的兄弟类。如果您在这些类中找不到想要的边界类，您可以自行对它们进行扩展，这些现有的实现应该可以为您提供一些灵感，比如，您可以通过创建自己的CellRenderer来实现。

我们在第7行使用了一个自定义的RoundedCornersCellRenderer()，在第8行，给单元格的内容定义了填充色，然后把边框设置为null。如果setBorder(null)这个方法不存在，则会绘制两个边框：一个是由iText自己绘制的，一个是由我们下面将要说的单元格渲染器来绘制。

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

CellRenderer类是BlockRenderer类的特殊实现。

> BlockRenderer类可以用在段落、列表这样的块元素上。继承自它的渲染器类也允许您通过重写draw()方法来自定义功能，比如：创建一个段落的的自定义背景。ps:CellRenderer也有一个drawBorder()方法噢。

我们通过重写drawBorder()方法来绘制一个顶部更丰满的矩形（第6到21行）。getOccupiedAreaBBox()这个方法返回了一个Rectangle矩形对象，可以使用它来找到块元素的边界框(第8行)。像getX()，getY()，getWidth()以及getHeight()这样的方法就是用来定义单元格的左下角和右上角的坐标(第9-12行)。

drawContext这个对象的getCanvas()方法允许我们获取一个PdfCanvas的实例（第13行）。我们用一系列直线和曲线把边界绘制了出来（14-20行）。上面写的例子就很好的演示了：在绘制由单元格组成的表的过程中，如何把相关对象的高级方法和我们之前几乎手动创建PDF的低级方法相结合，以便精确地绘制出符合需求的边界。

> 虽然绘制曲线的代码涉及到一些数学知识，但也不是什么很深奥的东西。大部分常见的边框的种类也都被iText包含在内了，所以您也不必担心引擎里面包含的数学知识。

关于BlockRenderer还有很多的内容需要介绍，但我们就此打住，把那些知识留在下一篇教程里去详细叙述。接下来，让我们用一在每一个页面上自动添加背景、页眉、页脚、水印和页码的例子来结束这一章节。

### 事件处理

当我们向一个文档添加一个包含多行的表格时，这个表格很可能会分布在不同的页面上。在图3.3中，我们列出了一个包含不明飞行物目击的清单[fo.csv](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/data/ufo.csv)。每个奇数页的背景彩石灰色，每个偶数页的背景是蓝色，每个页面的顶部都有一个标题“THE TRUTH IS OUT OUTERE”，底部都有页码，中间都带有一段"CONFIDENTIAL"的水印。=

![Figure 3.3: repeating background color and watermark](https://developers.itextpdf.com/sites/default/files/C03F03_1.png)
<p align="center">图3.3: 重复的背景颜色和水印</p>

 这个[UFO](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-3#1744-c03e03_ufo.java)的例子，里面创建表格的代码相信您已经看到过很多遍了。

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

在这段代码中，我们通过往setTextAlignment()这个方法中设置Property.TextAlignment.CENTER这个值，来使添加的段落居中。然后循环处理一个CSV文件每一行的内容，就像之前处理其他行一样。

这个例子里面第二行是重点，要考的。我们给PdfDocument的实例pdf添加了一个MyEventHandler类型的事件处理器。这个MyEventHandler实现了IEventHandler接口，虽然这个接口只有一个方法“handleEvent()”的说，每当PdfDocument.Event.END_PAGE类型的事件发生时都会触发这个方法。也就是说，无论是因为新页面被创建，或者已经到了最后一页，只要iText完成向页面添加内容，都会触发上述方法。

接下来看看IEventHandler的具体实现。

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
        canvas.setFontColor(Color.WHITE);
        canvas.setProperty(Property.FONT_SIZE, 60);
        canvas.setProperty(Property.FONT, helveticaBold);
        canvas.showTextAligned(new Paragraph("CONFIDENTIAL"),
            298, 421, pdfDoc.getPageNumber(page),
            TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
 
        pdfCanvas.release();
    }
}
```

在我们实现的handleEvent()方法中，有一个PdfDocument的实例，它是从参数event中的getDocument()方法获取到的（第3和第4行），包括PdfPage也是（第5行）。这些对象可以提供页码（第6行）、页面大小（第7行）和一个PdfCanvas的实例（第8-9行）。

> 在页面上绘制不同的路径和形状会有重叠的可能，一般来说，内容流中的第一个内容首先被绘制。之后绘制的内容可以覆盖之前的，但对于背景来说，却是在每次页面内容完全渲染后再被添加。每个PdfPage对象都会去跟踪一个内容流的数组，所以您可以使用以索引为参数的getContentStream()方法来获取每个单独的内容流，还可以使用getFirstContentStream()和getLastContentStream()来获取第一个和最后一个内容流。当然了，也可以使用newContentStreamBefore()或者newContentStreamAfter()创建一个新的内容流。

在handleEvent()中，您会看到由以下参数创建的PdfCanvas构造函数：

* page.newContentStreamBefore()：如果在页面渲染之后绘制一个不透明的矩形，那么这个矩形将覆盖现有内容，为此，需要访问之前添加的内容流，以便背景和水印不会覆盖原来表格中的内容。

* page.getResources()：每个内容流引用的外部资源，比如字体或者图像。如果我们要添加新的内容到一个页面中，那么请确保iText有足够的权限访问这个页面的资源句柄。

* pdfDoc： 我们需要访问一个PdfDocument对象，因为它可以代表添加了新内容后的新PDF对象。

那我们到底把什么东西添加到画布对象了？

* 第11－18行：定义了limeColor和blueColor两种颜色，保存了当前的图形状态后，根据页码将填充颜色更改为上述两种颜色中的一种，之后由创建了一个矩形并用选定的颜色填充它。这样做就会让石灰或者蓝色铺满整个页面，之后恢复图形状态并返回到原来填充的颜色上，因为我们不希望其他内容受到颜色变化的影响。

* 第20-26行：我们创建了一个文本对象，设置好字体和字体大小之后，把它移动到靠近页面顶部的中间位置，然后写了一段“THE TRUTH IS OUT THERE”，之后把光标移动到页面底部，在那里写上了页码，最后关闭这个文本对象。通过这些步骤，页眉和页脚就添加到了页面之中。

* 第28-31行：我们创建了一个名为canvas的Canvas对象，这样就可以通过setProperty()来更改字体、字体大小等属性，而不是用PDF的语法。

* 第32-34行：我们使用showTextAligned()创建了一个段落，以X = 298，Y ＝ 421为中心并倾斜45度。

最后我们把背景，页眉和页脚以及水印添加上去之后就关闭了PdfCanvas对象。

在这个例子中，我们使用了两种不同的方法在绝对位置添加文本。在上一章中讨论页眉和页脚的文本状态时，我们使用了一些低级的方法。虽然也可以使用类似的方法来添加水印，但是我们想要旋转文本并将其居中放置在页面中间，这需要相当一些数学知识。为了避免必须计算将文本置于所需坐标的变换矩阵，我们使用了一种更方便的方法。通过iText的showTextAligned()来在需要的地方完成所有繁重的工作。

### 总结

在本章中，您应该明白了为什么对上一章讨论的底层功能有一些了解是很重要的。我们可以将此功能与基本构建块结合使用来创建自定义功能。同时，我们还为Cell对象创建了自定义的边框、为页面添加了背景颜色，并且引入了页眉和页脚。在添加水印的时候，我们发现我们并不需要知道PDF语法的所有内容，而是通过使用一种方便的方法来处理矩阵的转换来旋转和居中文本。

在下一个例子中，我们将学习不同类型的内容。我们将通过放大一个特定类型的注解来了解注解的概念，这将允许我们可以创建交互式表单。

