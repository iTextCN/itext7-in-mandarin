ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-6-reusing-existing-pdf-documents](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-6-reusing-existing-pdf-documents)
## 章节六：重用现有的PDF文档
>标签：[Java](https://developers.itextpdf.com/tags/java)
[Scaling](https://developers.itextpdf.com/tags/scaling)
[tiling](https://developers.itextpdf.com/tags/tiling)
[Merging](https://developers.itextpdf.com/tags/merging)
[iText7](https://developers.itextpdf.com/tags/itext-7)
[jump start tutorial](https://developers.itextpdf.com/tags/jump-start-tutorial)

这一章中，我们将做更多的文档操作，但是在方法上会有细微的差别。在上一章的例子中，我们创建了一个PdfDocument实例，它将一个PdfReader链接到一个PdfWriter，因此我们可以操纵一个单一的文件。

在本章中，我们将始终创建至少两个PdfDocument实例：一个或多个源文档，一个用于目标文档。

我们从一些缩放和平铺文档的例子开始。

### 缩放PDF页面
假设我们有一个单页的PDF文件，大小为16.54英寸 x 11.69英寸，见图6.1
![](https://developers.itextpdf.com/sites/default/files/C06F01_1.png "图6.1：金门大桥，原尺寸为16.54 x 11.69英寸")
<p align="center">图6.1：金门大桥，原尺寸为16.54 x 11.69英寸</p>

现在我们要创建一个三页PDF文件。在第一页中，原始页面缩小到11.69 x 8.26英寸，如图6.2所示。在页2上，原始页面大小被保留。在第3页，原始页面被放大到23.39 x 16.53 in，如图6.3所示。
![](https://developers.itextpdf.com/sites/default/files/C06F02_1.png "图6.2：金门大桥，缩小到11.69 x 8.26英寸")
<p align="center">图6.2：金门大桥，缩小到11.69 x 8.26英寸</p>

![](https://developers.itextpdf.com/sites/default/files/C06F03_1.png "图6.3：金门大桥，按比例缩小至23.39 x 16.53英寸")
<p align="center">图6.3：金门大桥，按比例缩小至23.39 x 16.53英寸</p>

[GoldenGateBridge_Scale_Shrink](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1782-c06e01_thegoldengatebridge_scale_shrink.java)示例显示了如何完成。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfDocument origPdf = new PdfDocument(new PdfReader(src));
PdfPage origPage = origPdf.getPage(1);
Rectangle orig = origPage.getPageSizeWithRotation();
 
//Add A4 page
PdfPage page = pdf.addNewPage(PageSize.A4.rotate());
//Shrink original page content using transformation matrix
PdfCanvas canvas = new PdfCanvas(page);
AffineTransform transformationMatrix = AffineTransform.getScaleInstance(
    page.getPageSize().getWidth() / orig.getWidth(),
    page.getPageSize().getHeight() / orig.getHeight());
canvas.concatMatrix(transformationMatrix);
PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);
canvas.addXObject(pageCopy, 0, 0);
 
//Add page with original size
pdf.addPage(origPage.copyTo(pdf));
 
//Add A2 page
page = pdf.addNewPage(PageSize.A2.rotate());
//Scale original page content using transformation matrix
canvas = new PdfCanvas(page);
transformationMatrix = AffineTransform.getScaleInstance(
    page.getPageSize().getWidth() / orig.getWidth(),
    page.getPageSize().getHeight() / orig.getHeight());
canvas.concatMatrix(transformationMatrix);
canvas.addXObject(pageCopy, 0, 0);
 
pdf.close();
origPdf.close();
```
在这个代码片段中，我们创建一个PdfDocument实例，它将创建一个新的PDF文档（第1行）; 我们创建一个PdfDocument实例来读取现有的PDF文档（第2行）。为现有的PDF的第一页（第3行）获得一个PdfPage实例，并且得到它的尺寸（第4行）。然后，添加三个页面到新的PDF文档：

>1、使用横向（第7行）添加A4页面，并为该页面创建一个PdfCanvas对象。我们使用getScaleInstance（）方法（第9-12行）使用AffineTransform实例，而不是计算缩放坐标系的变换矩阵的a，b，c，d，e和f值。我们应用该转换（第13行），创建了一个包含原始页面（第14行）的表单XObject，并将该XObject添加到新页面（第15行）。

>2、以原始尺寸添加原始页面要容易得多。我们通过将origPage复制到新的PdfDocument实例来创建一个新页面，并使用addPage（）方法（第18行）将它添加到pdf中。

>3、放大和缩小的方式完全相同。这一次，我们使用横向方向（第21行）添加一个新的A2页面，我们使用与以前相同的代码来缩放坐标系（第23-27行），重用pageCopy对象并将其添加到画布（第29行）。

我们关闭pdf以完成新文档（第30行），并关闭origPdf以释放原始文档的资源。

我们可以使用相同的功能来平铺PDF页面。

### 平铺PDF页面
平铺PDF页面意味着您可以将不同页面的内容分配到不同的页面上。例如：如果您的PDF只包含一个尺寸为A3的单页，则可以创建一个包含四个不同尺寸（甚至相同尺寸）的页面的PDF，每个页面显示原始A3页面的四分之一。这就是我们在图6.4中所做的。
![](https://developers.itextpdf.com/sites/default/files/C06F04.png "图6.4：金门大桥，平铺页面")
<p align="center">图6.4：金门大桥，平铺页面</p>

接下来来看一下[TheGoldenGateBridge_Tiles](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1783-c06e02_thegoldengatebridge_tiles.java)示例。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfDocument sourcePdf = new PdfDocument(new PdfReader(src));
PdfPage origPage = sourcePdf.getPage(1);
PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);
Rectangle orig = origPage.getPageSize();
//Tile size
Rectangle tileSize = PageSize.A4.rotate();
AffineTransform transformationMatrix = AffineTransform.getScaleInstance(
    tileSize.getWidth() / orig.getWidth() * 2f,
    tileSize.getHeight() / orig.getHeight() * 2f);
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
// closing the documents
pdf.close();
sourcePdf.close();
```
我们以前看过1-5行; 已经在前面的例子中使用了它们。在第7行中定义了一个图块大小，我们创建了一个transformationMatrix来根据原始大小和图块大小来缩放坐标系。然后，逐个添加拼贴：第12-15行，第17-20行，第22-25行和第27-30行是相同的，除了一个细节：addXObject（）方法中使用的偏移量。

现在再次使用带有金门大桥的PDF作为例子，来做一个平铺的对立面：让我们N-up PDF。
### N-加一个PDF
图6.5显示了N-upping的含义。在下一个例子中，我们将把N个页面放在一个页面上。
![](https://developers.itextpdf.com/sites/default/files/C06F05.png "图6.5：金门大桥，一页四页")
<p align="center">图6.5：金门大桥，一页四页</p>

在[TheGoldenGateBridge_N_up](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1784-c06e03_thegoldengatebridge_n_up.java)示例中，N等于4.我们将在一个页面上放置4个页面。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfDocument sourcePdf = new PdfDocument(new PdfReader(SRC));
//Original page
PdfPage origPage = sourcePdf.getPage(1);
Rectangle orig = origPage.getPageSize();
PdfFormXObject pageCopy = origPage.copyAsFormXObject(pdf);
//N-up page
PageSize nUpPageSize = PageSize.A4.rotate();
PdfPage page = pdf.addNewPage(nUpPageSize);
PdfCanvas canvas = new PdfCanvas(page);
//Scale page
AffineTransform transformationMatrix = AffineTransform.getScaleInstance(
    nUpPageSize.getWidth() / orig.getWidth() / 2f,
    nUpPageSize.getHeight() / orig.getHeight() / 2f);
canvas.concatMatrix(transformationMatrix);
//Add pages to N-up page
canvas.addXObject(pageCopy, 0, orig.getHeight());
canvas.addXObject(pageCopy, orig.getWidth(), orig.getHeight());
canvas.addXObject(pageCopy, 0, 0);
canvas.addXObject(pageCopy, orig.getWidth(), 0);
// close the documents
pdf.close();
sourcePdf.close();
```
到目前为止，本章只重复使用单个PDF中的单个页面。在接下来的一系列例子中，我们将把不同的PDF文件组合成一个。

### 汇编文件
在图6.6中，我们从旧金山到洛杉矶，可以找到关于奥斯卡的三个文件。
![](https://developers.itextpdf.com/sites/default/files/C0606.png "图6.6：奥斯卡，源文件")
<p align="center">图6.6：奥斯卡，源文件</p>

这些文件是：
* [88th_reminder_list.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/88th_reminder_list.pdf)：一个32页的文件，标题为“第88届奥斯卡奖有资格的提示名单”，
* [88th_noms_announcement.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/88th_noms_announcement.pdf)：一个15页的文件，标题为“奥斯卡”
* [oscars_movies_checklist_2016.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/oscars_movies_checklist_2016.pdf)：一页1页文件，标题为“奥斯卡电影清单2016”在接下来的几个例子中，我们将合并这些文档。

### 将文档与PdfMerger合并
图6.7显示了通过合并第一个32页文档和第二个15页文档创建的PDF，生成一个47页的文档。
![](https://developers.itextpdf.com/sites/default/files/C0607.png "图6.7：合并两个文档")
<p align="center">图6.7：合并两个文档</p>

[88th_Oscar_Combine](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1785-c06e04_88th_oscar_combine.java)例子的代码几乎是自我解释的。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfMerger merger = new PdfMerger(pdf);
//Add pages from the first document
PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
merger.merge(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());
 //Add pages from the second pdf document
PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
merger.merge(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());
// merge and close
merger.close();
firstSourcePdf.close();
secondSourcePdf.close();
pdf.close();
```
我们创建一个PdfDocument来创建一个新的PDF（第1行）。PdfMerger类是新的。这是一个让我们更容易重用现有文档页面的类（第2行）。就像以前一样，我们为源文件创建一个PdfDocument（第4行，第7行）。然后将所有页面添加到合并实例（第5行，第8行）。一旦我们完成添加页面，将合并（）（第10行）和close（）（第11-13行）。

如果我们不想要，不需要添加所有的页面。我们可以轻松地添加一个有限的选择页面。例如见[88th_Oscar_CombineXofY](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1786-c06e05_88th_oscar_combinexofy.java)例子。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfMerger merger = new PdfMerger(pdf);
PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
merger.merge(firstSourcePdf, Arrays.asList(1, 5, 7, 1));
PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
merger.merge(secondSourcePdf, Arrays.asList(1, 15));
merger.close();
firstSourcePdf.close();
secondSourcePdf.close();
pdf.close();
```
现在的文件只有六页。第一个文档（第一个页面重复）中的页面1,5,7,1，第二个文档中的页面1和15。PdfMerger是一个便利的类，用它来合并文件将不费吹灰之力。但是，在某些情况下，需要逐个添加页面。
### 将页面添加到PdfDocument
图6.8显示了根据我们即将创建的目录（TOC）合并特定页面的结果。该TOC包含链接注释，如果单击TOC条目，则可以跳转到特定页面。
![](https://developers.itextpdf.com/sites/default/files/C0608.png "图6.8：基于TOC合并文档")
<p align="center">图6.8：基于TOC合并文档</p>

[88th_Oscar_Combine_AddTOC](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1787-c06e06_88th_oscar_combine_addtoc.java)例子比前两个例子更复杂。接下来开始一步一步检查。假设我们有一个所有类别的树状图，“Revenant”被提名，其中关键是提名，值是提到提名的文件的页码。
```
public static final Map<String, Integer> TheRevenantNominations =
    new TreeMap<String, Integer>();
static {
    TheRevenantNominations.put("Performance by an actor in a leading role", 4);
    TheRevenantNominations.put(
        "Performance by an actor in a supporting role", 4);
    TheRevenantNominations.put("Achievement in cinematography", 4);
    TheRevenantNominations.put("Achievement in costume design", 5);
    TheRevenantNominations.put("Achievement in directing", 5);
    TheRevenantNominations.put("Achievement in film editing", 6);
    TheRevenantNominations.put("Achievement in makeup and hairstyling", 7);
    TheRevenantNominations.put("Best motion picture of the year", 8);
    TheRevenantNominations.put("Achievement in production design", 8);
    TheRevenantNominations.put("Achievement in sound editing", 9);
    TheRevenantNominations.put("Achievement in sound mixing", 9);
    TheRevenantNominations.put("Achievement in visual effects", 10);
}
```
创建PDF代码的第一行非常简单。
```
PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
Document document = new Document(pdfDoc);
document.add(new Paragraph(new Text("The Revenant nominations list"))
    .setTextAlignment(TextAlignment.CENTER));
```
但是，一旦开始循环TreeMap中的条目，我们需要仔细观察。
```
PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
for (Map.Entry<String, Integer> entry : TheRevenantNominations.entrySet()) {
    //Copy page
    PdfPage page  = firstSourcePdf.getPage(entry.getValue()).copyTo(pdfDoc);
    pdfDoc.addPage(page);
    //Overwrite page number
    Text text = new Text(String.format(
        "Page %d", pdfDoc.getNumberOfPages() - 1));
    text.setBackgroundColor(Color.WHITE);
    document.add(new Paragraph(text).setFixedPosition(
            pdfDoc.getNumberOfPages(), 549, 742, 100));
    //Add destination
    String destinationKey = "p" + (pdfDoc.getNumberOfPages() - 1);
    PdfArray destinationArray = new PdfArray();
    destinationArray.add(page.getPdfObject());
    destinationArray.add(PdfName.XYZ);
    destinationArray.add(new PdfNumber(0));
    destinationArray.add(new PdfNumber(page.getMediaBox().getHeight()));
    destinationArray.add(new PdfNumber(1));
    pdfDoc.addNameDestination(destinationKey, destinationArray);
    //Add TOC line with bookmark
    Paragraph p = new Paragraph();
    p.addTabStops(
        new TabStop(540, TabAlignment.RIGHT, new DottedLine()));
    p.add(entry.getKey());
    p.add(new Tab());
    p.add(String.valueOf(pdfDoc.getNumberOfPages() - 1));
    p.setProperty(Property.ACTION, PdfAction.createGoTo(destinationKey));
    document.add(p);
}
firstSourcePdf.close();
```
开始以下步骤：
* 第1行：创建一个PdfDocument，其中包含所有关于提名的信息。
* 第2行：循环列出“亡灵”提名的字母表。
* 第3-4行：得到与提名相对应的页面，并且将一个副本添加到PdfDocument中。
* 第7-8行：创建一个包含页码的iText文本元素。我们从该页码中减去1，因为文档中的第一页是包含TOC的无编号页面。
* 第9行：将背景颜色设置为Color.WHITE。这将导致一个不透明的白色矩形被绘制成与文本的大小相同。我们这样做是为了覆盖原始页码。
* 第10-11行：将这个文本添加到PdfDocument中当前页面上的一个固定位置。固定的位置是：X = 549，Y = 742，文本的宽度是100个用户单位。
* 第13行：创建一个将用来命名目的地的密钥。
* 14-19行：创建一个包含目的地信息的PdfArray。我们将引用刚刚添加的页面（第15行），使用X，Y坐标和缩放因子（第16行）来定义目标，以及X（第17行），Y （第18行）和缩放因子（第19行）。
* 第20行：将指定的目标添加到PdfDocument。
* 第22行：创建一个空的段落。
* 第23-24行：在位置X = 540添加一个制表位，我们定义制表符需要右对齐，制表符之前的空格需要是一个DottedLine。
* 第25行：在段落中加上提名。
* 第26行：介绍一个Tab。
* 第27行：添加页码减1（因为TOC的页面是页面0）。
* 第28行：添加一个操作，当有人点击段落时会触发。
* 第29行：将段落添加到文档中。
* 第31行：关闭源文件。

我们已经介绍了许多新的功能，这些功能的确需要更深入的教程，但是我们看一下这个例子的一个主要原因：显示PdfDocument对象与新页面是通过循环和Document对象添加的，我们在第一页上添加了Paragraph对象。

让我们再次通过一些步骤来添加清单。
```
//Add the last page
PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
PdfPage page  = secondSourcePdf.getPage(1).copyTo(pdfDoc);
pdfDoc.addPage(page);
//Add destination
PdfArray destinationArray = new PdfArray();
destinationArray.add(page.getPdfObject());
destinationArray.add(PdfName.XYZ);
destinationArray.add(new PdfNumber(0));
destinationArray.add(new PdfNumber(page.getMediaBox().getHeight()));
destinationArray.add(new PdfNumber(1));
pdfDoc.addNameDestination("checklist", destinationArray);
//Add TOC line with bookmark
Paragraph p = new Paragraph();
p.addTabStops(new TabStop(540, TabAlignment.RIGHT, new DottedLine()));
p.add("Oscars\u00ae 2016 Movie Checklist");
p.add(new Tab());
p.add(String.valueOf(pdfDoc.getNumberOfPages() - 1));
p.setProperty(Property.ACTION, PdfAction.createGoTo("checklist"));
document.add(p);
secondSourcePdf.close();
// close the document
document.close();
```
此代码片段添加了所有提名概览的检查列表。在TOC中增加一行说“奥斯卡®2016电影核对清单”。
>这个例子介绍了一些用于教育目的的新概念。它不应该用于真实世界的应用程序，因为包含一个主要的缺陷。我们假定TOC只包含一个页面。假设在文档对象中添加了更多的行，那么会看到一个奇怪的现象：不适合第一页的文本会被添加到第二页。这第二页不会是一个新的页面，这将是我们在循环中添加的第一页。换句话说，第一个导入的页面的内容将被覆盖。这是一个可以解决的问题，但它不在这个简短的入门教程的范围之内。

我们将通过一些合并表单的例子来完成本章。
### 合并表单
合并表格是特殊的。在HTML中，可以在一个HTML文件中包含多个表单。PDF的情况并非如此。在PDF文件中，只能有一种形式。如果你想要合并两个表单并且希望保留表单，则需要使用特殊的方法和特殊的IPdfPageExtraCopier实现。

图6.9显示了两种不同形式的组合，[subscribe.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/subscribe.pdf)和[state.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/state.pdf):
![](https://developers.itextpdf.com/sites/default/files/C0609.png "图6.9：合并两种不同的形式")
<p align="center">图6.9：合并两种不同的形式</p>

[Combine_Forms](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1788-c06e07_combine_forms.java)示例与之前的示例不同。
```
PdfDocument destPdfDocument = new PdfDocument(new PdfWriter(dest));
PdfDocument[] sources = new PdfDocument[] {
        new PdfDocument(new PdfReader(SRC1)),
        new PdfDocument(new PdfReader(SRC2))
};
for (PdfDocument sourcePdfDocument : sources) {
    sourcePdfDocument.copyPagesTo(
            1, sourcePdfDocument.getNumberOfPages(),
            destPdfDocument, new PdfPageFormCopier());
    sourcePdfDocument.close();
}
destPdfDocument.close();
```
在这个代码片段中，使用copyPageTo（）方法。前两个参数定义源文档的页面的起始/到范围。第三个参数定义目标文档。第四个参数表示我们正在复制表单，两个不同文档中的两种不同表单应该合并成一个表单。PdfPageFormCopier是IPdfPageExtraCopier接口的实现，它确保将两种不同的表单合并成一个表单。
>合并两种形式并不总是微不足道的，因为每个领域的名称必须是唯一的。假设我们将两次合并相同的表单。然后，为每个字段提供两个小部件注释。具有特定名称的字段（例如“名称”）可以使用不同的小部件注释来可视化，但是它只能具有一个值。假设您将在第一页上为字段“名称”创建一个窗口小部件注释，并在第二页上为同一个字段创建窗口小部件注释，然后更改一个页面上窗口小部件注释中显示的值将自动更改在另一页上的小部件注释。

在下一个示例中，我们将填写并合并与CSV文件[united_states.csv](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/data/united_states.csv)中的条目相同的表单[state.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/state.pdf)。见图6.10。
![](https://developers.itextpdf.com/sites/default/files/C0610.png "图6.10：合并相同的表单")
<p align="center">图6.10：合并相同的表单</p>

如果我们将原始形式的字段名称保持原样，那么将状态“ALABAMA”的值更改为“CALIFORNIA”，也会在第二页上更改名称“ALASKA”，而其他页面的所有国家的名称。我们确保在合并表单之前重命名所有字段不会发生这种情况。

我们来看看[FillOutAndMergeForms](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1789-c06e08_filloutandmergeforms.java)的例子。
```
PdfDocument pdfDocument = new PdfDocument(new PdfWriter(dest));
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
    PdfDocument sourcePdfDocument = new PdfDocument(
            new PdfReader(SRC), new PdfWriter(baos));
    //Rename fields
    i++;
    PdfAcroForm form = PdfAcroForm.getAcroForm(sourcePdfDocument, true);
    form.renameField("name", "name_" + i);
    form.renameField("abbr", "abbr_" + i);
    // ... (removed repetitive lines)
    form.renameField("dst", "dst_" + i);
    //Fill out fields
    StringTokenizer tokenizer = new StringTokenizer(line, ";");
    Map<String, PdfFormField> fields = form.getFormFields();
    fields.get("name_" + i).setValue(tokenizer.nextToken());
    fields.get("abbr_" + i).setValue(tokenizer.nextToken());
    // ... (removed repetitive lines)
    fields.get("dst_" + i).setValue(tokenizer.nextToken());
    // close the source document and use it to create a new PdfDocument
    sourcePdfDocument.close();
    sourcePdfDocument = new PdfDocument(
        new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
    //Copy pages
    sourcePdfDocument.copyPagesTo(
        1, sourcePdfDocument.getNumberOfPages(),
        pdfDocument, new PdfPageFormCopier());
    sourcePdfDocument.close();
}
bufferedReader.close();
pdfDocument.close();
```
我们先看看while循环内的代码。我们正在循环存储在CSV文件中的美国的不同州（第6行）。跳过包含列标题信息的第一行（第7-10行），接下来的几行很有趣。到目前为止，我们一直在写PDF文件到磁盘。在这个例子中，我们使用ByteArrayOutputStream（11-13行）在内存中创建PDF文件。

如前所述，我们从重命名所有的字段开始，得到了PdfAcroForm实例（第16行），并使用renameField（）方法将字段（如“name”）重命名为“name_1”，“name_2”等。请注意，为了简洁起见，我们在代码段中跳过了一些行。一旦重新命名了所有的字段，我们设置它们的值（23-27行）。

当我们关闭sourcePdfDocument（第29行）时，在内存中有一个完整的PDF文件。我们在内存中使用该文件创建的ByteArrayInputStream创建一个新的sourcePdfDocument（第30-31行）。我们现在可以将新的sourcePdfDocument的页面复制到我们的目标pdfDocument中。

这是一个相当人为的例子，但是这是一个很好的例子来解释合并表单时常见的一些陷阱：
* 没有PdfPageFormCopier，表单将不会正确合并。
* 一个字段只能有一个值，不管该字段使用小部件标注可视化多少次。

更常见的用例是在内存中多次填写和拼合相同的表单，同时将所有生成的文档合并到一个PDF中。

### 合并扁平形式
图6.11显示了两个PDF文件，这些文件是同样程序的结果：我们在内存中填写了一个与美国州相同的表格。把这些填好的表格弄平了，将它们合并成一个单一的文件。
![](https://developers.itextpdf.com/sites/default/files/C0611.png "图6.11：填充，拼合和合并表格")
<p align="center">图6.11：填充，拼合和合并表格</p>

从外部看，这些文件看起来是一样的，但是如果我们看看图12中的文件大小，我们看到了巨大的差异。
![](https://developers.itextpdf.com/sites/default/files/C0612.png "图6.12：文件大小的差异取决于文档如何合并")
<p align="center">图6.12：文件大小的差异取决于文档如何合并</p>

是什么造成这种文件大小的差异？我们需要看看[FillOutFlattenAndMergeForms](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-6#1790-c06e09_filloutflattenandmergeforms.java)的例子来找出答案。
```
PdfDocument destPdfDocument =
    new PdfDocument(new PdfWriter(dest1));
PdfDocument destPdfDocumentSmartMode =
    new PdfDocument(new PdfWriter(dest2).setSmartMode(true));
BufferedReader bufferedReader = new BufferedReader(new FileReader(DATA));
String line;
boolean headerLine = true;
while ((line = bufferedReader.readLine()) != null) {
    if (headerLine) {
        headerLine = false;
        continue;
    }
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PdfDocument sourcePdfDocument =
        new PdfDocument(new PdfReader(SRC), new PdfWriter(baos));
    //Fill out fields
    PdfAcroForm form = PdfAcroForm.getAcroForm(sourcePdfDocument, true);
    StringTokenizer tokenizer = new StringTokenizer(line, ";");
    Map<String, PdfFormField> fields = form.getFormFields();
    fields.get("name").setValue(tokenizer.nextToken());
    fields.get("abbr").setValue(tokenizer.nextToken());
    // ... (removed repetitive lines)
    fields.get("dst").setValue(tokenizer.nextToken());
    //Flatten fields
    form.flattenFields();
    sourcePdfDocument.close();
    sourcePdfDocument = new PdfDocument(
        new PdfReader(new ByteArrayInputStream(baos.toByteArray())));
    //Copy pages
    sourcePdfDocument.copyPagesTo(
        1, sourcePdfDocument.getNumberOfPages(), destPdfDocument, null);
    sourcePdfDocument.copyPagesTo(
        1, sourcePdfDocument.getNumberOfPages(), destPdfDocumentSmartMode, null);
    sourcePdfDocument.close();
}
bufferedReader.close();
destPdfDocument.close();
destPdfDocumentSmartMode.close();
```
在这段代码中，同时创建了两个文档：
* destPdfDocument实例（第1-2行）的创建方式与我们一直创建PdfDocument实例的方式相同。
* destPdfDocumentSmartMode实例（第3-4行）也是以这种方式创建的，但是我们已经打开了智能模式。

我们像之前一样循环CSV文件的行（第8行），但是由于我们要将表单变平，我们不必再重新命名这些字段。无论如何，由于扁平化过程，田地将会丢失。我们在内存中创建一个新的PDF文档（第12-15行），并填写字段（第17-23行）。我们将字段弄平（第25行）并关闭内存中创建的文档（第26行），使用在内存中创建的文件来创建一个新的源文件。我们将这个源文件的所有页面添加到两个PdfDocument实例中，一个在正常模式下工作，另一个在智能模式下工作。我们不再需要使用PdfPageFormCopier实例，因为表单已经变平了; 他们不再是形式。

这些正常和智能模式有什么区别？
* 当我们将填写好的表单页面复制到在正常模式下工作的PdfDocument时，PdfDocument将处理每个文档，就好像它与其他正在添加的文档完全无关。在这种情况下，生成的文档会变得臃肿，因为文档是相关的：它们都共享相同的模板。该模板被添加到PDF文档中的次数与美国的州数量相同。在这种情况下，结果是一个大约12 MB的文件。
* 当我们将填写的表单页面复制到以智能模式工作的PdfDocument时，PdfDocument将花费时间比较每个文档的资源。如果两个单独的文档共享相同的资源（例如模板），则该资源仅被复制到新文件一次。在这种情况下，结果可能被限制为365 KB。在PDF查看器或打印时，12兆字节和365千字节文件看起来完全一样，但不言而喻，365千字节文件优于12兆字节文件。

### 总结
在本章中，我们一直在扩展，平铺，用一个不同的文件作为结果N-upping一个文件。我们还以许多不同的方式组装文件，发现合并交互式表单时存在相当多的缺陷。关于重复使用现有PDF文档的内容还有很多要说的。

在下一章中，我们将讨论符合PDF / UA和PDF / A等特殊PDF标准的PDF文档。将会发现合并PDF / A文档也需要特别注意。