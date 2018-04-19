ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-6-reusing-existing-pdf-documents](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-6-reusing-existing-pdf-documents)
# **前言**

&nbsp;&nbsp;&nbsp;&nbsp;在本章中，我们做进行更多的文档操作功能介绍,但是采用的方法会和之前不一样。在之前的篇章中，我们会创建一个关联``PdfReader``和``PdfWriter``的``PdfDocument``实例，我们操作单一的文档。  
&nbsp;&nbsp;&nbsp;&nbsp;在本章中，我们会至少创建两个``PdfDocument``实例：至少一个源文件对象和一个目的文档对象。  
&nbsp;&nbsp;&nbsp;&nbsp;PS:这章的内容有点多，请耐心观看~

# **缩放、分裂(tiling)、聚集(N-upping)**
&nbsp;&nbsp;&nbsp;&nbsp;我们先从缩放和分裂一个文档的例子开始：

## **缩放PDF**
&nbsp;&nbsp;&nbsp;&nbsp;首先我们看一下下图的单个文档，初始大小16.54*11.69(**单位为1用户单位，用户单位即pt,1点(pt)=1/72(英寸)inch 1英寸=25.4毫米mm**): 

![itext6_1](http://obkwqzjnq.bkt.clouddn.com/itext6_1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;现在我们想要创建3页的PDF文件。在第一页，原始的页面缩小到11.69*8.26pt，如下图：

![itext6_2](http://obkwqzjnq.bkt.clouddn.com/itext6_2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;在第二页，保留原始页面大小，在第三页，原始页面被放大到23.39*16.53pt，如下图：

![itext6_3](http://obkwqzjnq.bkt.clouddn.com/itext6_3.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下所示：
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
&nbsp;&nbsp;&nbsp;&nbsp;在此代码片段，我们创建了一个目标文件的``PdfDucument``实例(行1);随后我们创建了读取一个现有PDF文档的``PdfdDocument``实例(行2);然后我们读取现有文件的第一页的``PdfPage``实例(行3)，最后我们获取它的大小(行4，``Rectangle``实例)，添加新的三页到新PDF文档中：  

- 我们添加横向的A4页面(行7)，创建了那一页的``PdfCanvas``对象。在这里，我们不需要计算缩放坐标系的变换矩阵的``a``,``b``,``c``,``d``,``e``,``f``的值，我们通过``getScaleInstance()``方法来获取``AffineTransform``实例(行9-12)。然后我们应用这个变换(行13),创建一个包含原始的``Form Xobject``(行14),最后添加这个Xobject到新的页面中(行15)。
- 添加同样大小的原始页面会简单很多：只需复制``origPage``到新的``PdfDcoument``实例，然后我们使用``addPage()``方法来添加到``pdf``中(行18)。
- 我们按照同样的方式来进行放大。在这里，我们添加横向的A2页面(行21),然后使用之前同样的代码来放大坐标系(行23-27)，重新使用``pageCopy``对象并且添加到``canvas``中(行29)。

> 关于Xobject大家可以参阅PDF页面描述指令部分，是PDF文件格式的一部分，在这里，我们只需知道xobject是外部对象，可以有自己的资源。

&nbsp;&nbsp;&nbsp;&nbsp;在最后，我们关闭``pdf``来完成新文档的创建(行30)，关闭``origPdf``来释放原始文档。同样的，我们可以使用相同的函数功能来完成分裂一个pdf页面。


## **分裂PDF**
&nbsp;&nbsp;&nbsp;&nbsp;分裂一个pdf页面意味着你在多个页面上显示一个页面的内容。例如：如果你有一个A3大小的单一页面，你可以创建包含4个页面的PDF文档，每一页显示四分之一的原始A3页面，如下图所示：  

![itext6_4](http://obkwqzjnq.bkt.clouddn.com/itext6_4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下所示：
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
&nbsp;&nbsp;&nbsp;&nbsp;行1-5之前我们已经见过了，在之前一个例子中已经使用了。在行7中，我们了一个分片的大小，然后我们创建了``transformationMatrix``来放大原始的坐标系。然后我们一个一个添加分片:行12-15，行17-20，行22-25，行27-30，这四个代码片段是相同的，除了在``addXObject()``方法中的偏移量。然后我们看一下与之相反的例子，聚集一个PDF文件：

## **聚集PDF**
&nbsp;&nbsp;&nbsp;&nbsp;下图展示了聚集PDF的结果，即把N个页面的内容放在一个页面上：  

![itext6_5](http://obkwqzjnq.bkt.clouddn.com/itext6_5.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下：
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
&nbsp;&nbsp;&nbsp;&nbsp;至今，我们已经学习了如何重复使用一页的内来构建文档。在接下来的例子中，我们组合不同的PDF文件来生成一个文件。

# **组合文档**
&nbsp;&nbsp;&nbsp;&nbsp;我们的视野从旧金山到洛杉矶，如下图，三个文档都是关于奥斯卡的：  

![itext6_6](http://obkwqzjnq.bkt.clouddn.com/itext6_6.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这些文档分别是：  

- [88th\_reminder\_list.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/88th_reminder_list.pdf):一个32页文档，标题为"Reminder List of Productions Eligible for the 88th Academy Awards"
- [88th\_noms\_announcement.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/88th_reminder_list.pdf):一个15页文档，标题为"Oscars"
- [oscars\_movies\_checklist_2016.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/88th_reminder_list.pdf):一个一页的文档，标题为"Oscars Movie Checklist 2016"

&nbsp;&nbsp;&nbsp;&nbsp;在接下来的例子中，我们会拼接这些文档。

## **使用PdfMerger拼接文档**
&nbsp;&nbsp;&nbsp;&nbsp;如下图，展示了我们拼接了32页的文档和15页的文档，产生了一个47页的文档：  

![itext6_7](http://obkwqzjnq.bkt.clouddn.com/itext6_7.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这个例子的代码如下：
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
&nbsp;&nbsp;&nbsp;&nbsp;这段代码很好理解，我们首先创建了一个新的PDF对象``PdfDocument``(行1)，然后引入了新的类``PdfMerger``，这个类能让利用现有文档的页面变得更简单(行2)。就跟之前的一样，我们创建了源文件的``PdfDocument``(行4，行7)；然后添加所有的页面到``merger``实例中(行5，行8)。一旦我们加完全部的页面后，我们通过``close()``方法来关闭``merger``。  
&nbsp;&nbsp;&nbsp;&nbsp;当然了，我们不必添加所有的页面，完全可以省略不想添加的页面。如下代码：
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
&nbsp;&nbsp;&nbsp;&nbsp;现在的结果就是生成的文档只有6页。从第一个源文件里面的第1,5,7,1页中选取(其中第1页重复选取了)，以及从第二个源文件里面的第1和15页选取。``PdfMerger``是一个能拼接文档的方便无脑的类，但是在某些情况下，我们需要一个一个页面添加。

## **向PdfDocument添加页面**
&nbsp;&nbsp;&nbsp;&nbsp;基于目录的拼接文档结果如下图所示，在目录中可以通过链接注释跳转到特定的位置：  

![itext6_8](http://obkwqzjnq.bkt.clouddn.com/itext6_8.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这个例子稍微复杂一点，我们一步步地解释：  
&nbsp;&nbsp;&nbsp;&nbsp;假设我们有一个``TreeMap``类型的变量，里面存储的是荒野猎人的提名项内容（一共有12项提名），其中key的内容是提名的具体内容，value值是提名对应页码，*也就是我们从oscars\_movies\_checklist_2016.pdf中选取有对应提名的页面组成新的文档，当然新的文档里面页面会有重复*。代码如下：
```
//提名出现的页码，如果两个提名项出现在同一页面上，也需要重复记录
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
&nbsp;&nbsp;&nbsp;&nbsp;在这之前创建新的PDF文档代码很简单，如下所示：
```
PdfDocument pdfDoc = new PdfDocument(new PdfWriter(dest));
Document document = new Document(pdfDoc);
document.add(new Paragraph(new Text("The Revenant nominations list"))
    .setTextAlignment(TextAlignment.CENTER));
```
&nbsp;&nbsp;&nbsp;&nbsp;当循环``TreeMap``里面的元素的时候，我们就要慢慢来了：
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
&nbsp;&nbsp;&nbsp;&nbsp;OK,我们逐行来解释一下(**阅读之前请查看一下对应的两个pdf文件：88th\_noms\_announcement.pdf和oscars\_movies\_checklist_2016.pdf**):  

- 行1：我们利用带有所有提名信息的源文件创建了一个``PdfDocument``对象
- 行2：荒野猎人的提名项是按照字母排序的，我们开始遍历提名项。
- 行3-4：获取提名项对应的页码然后，复制源文件对应的页码的页面
- 行7-8：创建一个iText中``Text``对象来存储页码，用来替换源文件左上角的页码，同时注意要减去1，因为第1页是用来存储目录的
- 行9：设置背景颜色为``Color.WHITE``，这将会产生一个不透明的矩形，大小为``Text``的大小，我们用来覆盖源文件的左上角的页码。
- 行10-11:向``PdfDocument``确定页面固定的位置上添加这个``text``坐标为x=548,y=742，这个文本的宽度为100用户单位,pt
- 行13：创建了跳转目标命名的一个key，，以后会用
- 行14-19：创建了``PdfArray``，里面包含了目标的信息。首先添加跳转页面的对象，然后确定跳转页面的位置，xyz的信息，最后确定x=0,y=页面高度(我们要牢记初始坐标系左下角为原点),z是放大系数，设置为1，表示不放大。
- 行20：向``PdfDocument``中添加命名的跳转目标。
- 行22：创建了空的``Paragraph``
- 行23-24：在x=540的位置上添加制表符，表的对齐方式为右对齐，空格的部分为虚线。
- 行25：添加提名项到``Paragraph``中
- 行26：添加一个``Tab``到``Paragraph``中
- 行27：表格右边为页码，当前页数减去1
- 行28：添加点击``Paragh``到的事件
- 行29：添加``Paragh``到``document``中去(**还记得之前的文章中doc添加的内容和PdfDocument添加的内容所在页面并不是一样的,位置不一样**)
- 行31：关闭源文件

&nbsp;&nbsp;&nbsp;&nbsp;在这里我们引入了很多新的方法和类，详细介绍的话会需要更多的章节，所以我们着重关注的是``PdfDocument``和``Document``对象之间的区别，前者我们是通过循环添加页面，后者是在第一页上已知添加``Paragraph``对象。  
&nbsp;&nbsp;&nbsp;&nbsp;让我们再次看一下添加目录的代码：
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
&nbsp;&nbsp;&nbsp;&nbsp;这个代码片段添加了提名项的目录，最后一行是"Oscars® 2016 Movie Checklist" 。

> 注意，这个例子引入了很多新的概念，当然是出于教育的目的。它不能应用到现实生活中，因为它的假设是目录只有一页。假如我们向``document``对象中添加更多的行，然后你会发现一个奇怪的现象：第一页放不下的内容会被放入第二页，但是这里的第二页并不是会新的一页，而是我们循环添加循环的第一页（在我之前写的杂谈里面有提及，这里也是类似的情况，反正就是注意向``document``添加的内容和通过``PdfDocument``添加的内容是不兼容的，会出现很多错误），当然了，我们还是有方法来防止这个现象的发生，但是不在本次教程范围内

&nbsp;&nbsp;&nbsp;&nbsp;我们最后看一下拼接表单的例子。

# **拼接表单**
&nbsp;&nbsp;&nbsp;&nbsp;拼接表单比较特殊(这里的拼接表单并不是指视觉上组成一个表单，而是页面的拼接)。在HTML中，在单一的HTML文件中可能存在多个表单。但是PDF文件不存在这种情况，PDF文件只有一张表单。如果你想拼接两个表单并且想要维持这个表单。可以只用特殊的方法和``IPdfPageExtraCopier``实现。  
&nbsp;&nbsp;&nbsp;&nbsp;如下图展示了两种不同的表单的拼接，[subscribe.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/subscribe.pdf)和[state.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/state.pdf)  

![itext6_9](http://obkwqzjnq.bkt.clouddn.com/itext6_9.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下：
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
&nbsp;&nbsp;&nbsp;&nbsp;在这个代码片段，我们使用了``copyPageTo()``方法。这个方法的前面两个两个参数是要拷贝源文件的开始页面和拷贝的页面数，第三个参数是目的文档，第四个参数暗示了当我们拷贝不同源文件的不同表单的时候，应该要拼接成一张表单。  
&nbsp;&nbsp;&nbsp;&nbsp;``PdfPageFormCopier``是``IPdfPageExtraCopier``接口的实现，确保了两个不同的表单拼接成一张表单。

> 拼接表单的时候也要注意特殊情况，因为需要每个字段唯一。如果我们拼接两张相同的表单，那么每个字段会对应两个窗口小部件。假如我们有字段为``"name"``，可以被不同的窗口小部件展示，但是它的值只能有1个，在页1上有``"name"``对应的窗口小部件，在页2上也有``"name"``对应的窗口小部件，那么任意的``"name"``对应的窗口小部件的值改变都会改变在另一页上的窗口小部件的值。


&nbsp;&nbsp;&nbsp;&nbsp;接下来的例子中，我们会填充并拼接相同的表单，[state.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/pdf/state.pdf) ,表单里面的数据为[united_states.csv](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/data/united_states.csv)里面的数据，结果为下图：

![itext6_10](http://obkwqzjnq.bkt.clouddn.com/itext6_10.png)  

&nbsp;&nbsp;&nbsp;&nbsp;如果我们保持原始表单的字段的名称不变的话，州名Name从"ALABAMA"编程"CALIFORNIA"的话，那么会改变第二页的"ALASKA"，以及其他页的name都会改变。所以我们需要改变各个字段的名称  
&nbsp;&nbsp;&nbsp;&nbsp;现在我们看一下代码：
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
&nbsp;&nbsp;&nbsp;&nbsp;我们注重看一下while循环里面的代码，从CSV文件中循环读取美国各个州的信息(行6)，跳过了第一行的列标题(行7-10)。接下来的代码片段就很有意思啦。至今，我们都是把PDF文档写入磁盘，在这个例子中，我们使用``ByteArrayOutputStream``来在内存中创建PDF文件(行11-13)。  
&nbsp;&nbsp;&nbsp;&nbsp;在之前提及过，我们要改变字段的名称。首先获取``PdfAcroForm``实例(行16)，然后使用``renameField()``方法来重命名字段，例如``"name"``字段重命名为``"name_1"``、``"name_1"``等。值的注意的是，在上述代码中省略了一些代码(行19、行26)。一旦所有字段都重命名完毕，我们就可以设置他们的值(行23-27)。  
&nbsp;&nbsp;&nbsp;&nbsp;当我们关闭``sourcePdfDocument``对象(行29)时,就会在内存有一个完成的PDF文档。然后我们用于之关联的``ByteArrayInputStream``来创建一个新的``sourcePdfDocument``(行30-32)。现在我们可以使用这个新的``sourcePdfDocument``来拷贝页到我的目标文件``pdfDocument``中。  
&nbsp;&nbsp;&nbsp;&nbsp;当然这是比较牵强的例子，都是人为的。但是他解释了拼接表单的时候经常会出现的错误：

- 没有``PdfPageFormCopier``，表单不能被正确地拼接
- 无论一个字段被多个窗口小部件表示，它都只能有一个值

&nbsp;&nbsp;&nbsp;&nbsp;一个更常用的做法就是在内存中多次填充并锁定相同的表格，最终所有的表格拼接成目标文件。

# **拼接锁定的表单**
&nbsp;&nbsp;&nbsp;&nbsp;拼接锁定的表单的效果图如下：

![itext6_11](http://obkwqzjnq.bkt.clouddn.com/itext6_11.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这两个文件的内容是一样，但是文件的大小却是不一样的，如下图：

![itext6_12](http://obkwqzjnq.bkt.clouddn.com/itext6_12.png)  

&nbsp;&nbsp;&nbsp;&nbsp;为什么会产生如此的不同呢？让我们来看一下代码：
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
&nbsp;&nbsp;&nbsp;&nbsp;在代码一开始，我们同时创建了两个文档：

- ``destPdfDocument``实例，和平时我们创建``PdfDocument``实例一样
- ``destPdfDocumentSmartMode``实例，也是同样的方法创建，但是我们设置成了smart mode。

&nbsp;&nbsp;&nbsp;&nbsp;像之前一样，我们循环遍历CSV文件里面的行(行8)，但是因为我们要锁定表单，所以我们不需要再重命名表单字段。当表单锁定使，表单字段将会丢失，所以会和后续读入的表单字段不冲突。然后我们在内存中创建了一个新的PDF文档(行12-15)，填充了各个字段(行17-23)，接着锁定表单(行25)并关闭了内存中的文档(行26)。最后我们使用内存中的文档来创建新的文档，添加所有页面到两个``PdfDocument``实例中，一个工作在normal mode，另一个工作在smart mode中，其中我们不再使用``PdfPageFormCopier``实例，因为表单都被锁定了，不再有表单了。  
&nbsp;&nbsp;&nbsp;&nbsp;那么这两种模式有什么区别呢？  

- 在normal模式下我们拷贝页面到``PdfDocument``中，不会比较加入的页面是否先关联，是否采用同一个模板
- 在smart模式下会检查关联性，如果多个页面资源一样，那么那个资源只会复制一次。

# **总结**
&nbsp;&nbsp;&nbsp;&nbsp;在本章，我们缩放、分裂和聚集了PDF文档，同时我们采用了多种方式来组合文档。当拼接表单的时候，我们会遇到很多困难，关于重复使用现有PDF文档的内容还有很多要说的。  
&nbsp;&nbsp;&nbsp;&nbsp;在下一章中，我们将讨论符合PDF / UA和PDF / A等特殊PDF标准的PDF文档。我们会发现合并PDF / A文档也需要特别注意一些特殊情况。