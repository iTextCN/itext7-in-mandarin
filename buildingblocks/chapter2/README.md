&nbsp;&nbsp;&nbsp;&nbsp;在这一章中，我们通过添加``BlockElement``和``Image``对象添加到``RootElement``实例的方式来创建PDF文档。``RootElement``是拥有两个子类的抽象类：``Document``和``Canvas``：
- ``Document``是创建自定义PDF的时候默认的根元素。由它来管理很多高等级(high-level)的操作例如设置页面大小和旋转角度，添加元素和添加文本到特定坐标。当然，``Document``不知道实际的PDF里面的术语和语法。一个``Document``的渲染行为可以通过实现一个``DocumentRenderer``类并且调用``setRenderer()``方法设置这个``Document``的渲染器的方式来改变。
- ``Canvas``是用来添加``BlockElement``和``Image``内容进一个特定的矩形(``rectangle``)，这个矩形在一个``PdfCanvas``上使用绝对坐标来定义，``Canvas``不知道一个页面的概念，以及内容超出矩形大小的部分会被丢失。该类充当高级布局API和低级内核API之间的桥梁。  

> 这章开始每章的内容会很多，请耐心观看

&nbsp;&nbsp;&nbsp;&nbsp;在前面一章中，我们已经使用过``Document``类了，所以我们先从``Canvas``的一些例子开始。

# 1.使用Canvas来添加内容到Rectangle

&nbsp;&nbsp;&nbsp;&nbsp;在下图中，我们使用低级API来画了一个``Rectangle``，然后我们往里面添加了文本，这些文本使用``Canvas``对象的方式来添加。如图2.1：  

![itext-h-2-1](http://oss.cuteke.cn/itext-h-2-1.png)  

<center>图2.1：在一个矩形中添加文本</center>

&nbsp;&nbsp;&nbsp;&nbsp;让我们来看一下代码：
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfPage page = pdf.addNewPage();
PdfCanvas pdfCanvas = new PdfCanvas(page);
Rectangle rectangle = new Rectangle(36, 650, 100, 100);
pdfCanvas.rectangle(rectangle);
pdfCanvas.stroke();
Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle);
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
Text title =
    new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
Text author = new Text("Robert Louis Stevenson").setFont(font);
Paragraph p = new Paragraph().add(title).add(" by ").add(author);
canvas.add(p);
canvas.close();
pdf.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们来一行一行地解读：
- 行1：我们定义了一个``PdfDocument``
- 行2：我们没有使用一个``Document``对象，所以我们必须自己创建每一个``PdfPage``对象
- 行3：我们使用这个``PdfPage``对象来创建一个``PdfCanvas``
- 行4：我们定义了一个矩形
- 行5-6：使用低层次API来创建整个矩形
- 行7：使用``PdfPage``，``PdfDocument``和这个矩形来创建一个``Canvas``
- 行8-13： 创建了一个``Paragraph``，这段代码和上一章的代码一样
- 行14：添加这个``Pargraph``到这个``Canvas``
- 行15：关闭``Canvas``
- 行16：关闭``PdfDocument``

&nbsp;&nbsp;&nbsp;&nbsp;仔细查看这个例子，我们可以发现并不是很难理解。如果你需要把内容添加到**特定页面的特定的矩形位置**，你可以通过传递这个页面和矩形两个参数来创建一个``Canvas``。当你往这个``Canvas``添加内容时，这些内容会被渲染在这个矩形之内。  

&nbsp;&nbsp;&nbsp;&nbsp;我们要牢记那些超出矩形大小的内容将会被裁剪，见下图2.2：  

![itext-h-2-2](http://oss.cuteke.cn/itext-h-2-2.png)  

<center>图2.2：添加的文本超出矩形大小</center>

&nbsp;&nbsp;&nbsp;&nbsp;我们来看一下代码：
```
Rectangle rectangle = new Rectangle(36, 750, 100, 50);
Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle);
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
Text title =
    new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
Text author = new Text("Robert Louis Stevenson").setFont(font);
Paragraph p = new Paragraph().add(title).add(" by ").add(author);
canvas.add(p);
canvas.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;在这个代码片段，我们添加了之前的内容，但是我们不同于之前的``new Rectangle(36,650,100,100)``，我们把高度从100到50：``new Rectangle(36,750,100,50)``。这样做的结果就是文本不再完全容纳进矩形中：原本的文本“Mr. Hyde by Robert Louis Stevenson”将会丢失。没有异常抛出，因为这是正规操作。  

&nbsp;&nbsp;&nbsp;&nbsp;文本被裁剪了但是没有警告是不尽如人意的。在有些情况下，你需要知道内容是否适应矩形大小。例如，下面这个例子，我们定义了一个更大的矩形，然后尽可能多次往里面添加``Paragraph``，如下图2.3：  

![itext-h-2-3](http://oss.cuteke.cn/itext-h-2-3.png)  

<center>图2.3：长矩形中多次添加文本</center>

&nbsp;&nbsp;&nbsp;&nbsp;我们3次添加``Paragraph``，因为最多只能往里面添加2次半就能完全适应这个矩形。我们怎么知道添加内容时，矩形已经满了呢？可以看一下下面一段代码：
```
class MyCanvasRenderer extends CanvasRenderer {
    protected boolean full = false;
 
    private MyCanvasRenderer(Canvas canvas) {
        super(canvas);
    }
 
    @Override
    public void addChild(IRenderer renderer) {
        super.addChild(renderer);
        full = Boolean.TRUE.equals(getPropertyAsBoolean(Property.FULL));
    }
 
    public boolean isFull() {
        return full;
    }
}
```
&nbsp;&nbsp;&nbsp;&nbsp;在这里，我们引入了一个成员变量``full``，这个变量表明矩形是否被完整填充。每次我们往里面添加元素时，我们都会检查``FULL``属性的状态，状态可以为``null``,``false``或者``true``。如果状态为``true``，意味着没有剩余的空间来添加内容。为了方便，我们使用``ifFull()``方法来获取属性。接着我们看添加内容的代码：  
```
Rectangle rectangle = new Rectangle(36, 500, 100, 250);
Canvas canvas = new Canvas(pdfCanvas, pdf, rectangle);
MyCanvasRenderer renderer = new MyCanvasRenderer(canvas);
canvas.setRenderer(renderer);
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
Text title =
    new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
Text author = new Text("Robert Louis Stevenson").setFont(font);
Paragraph p = new Paragraph().add(title).add(" by ").add(author);
while (!renderer.isFull())
    canvas.add(p);
canvas.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;行1我们和之前一样定义``Rectangle``。行3-4是新添加的内容，创建了我们自定义的渲染器并把它加入到``Canvas``对象中。在行11-12，我们一直尝试尽可能添加``Paragraph``，知道``Canvas``元素满为止。  

> 你可能疑惑我们设置矩形边界的时候会使用低级/底层(low-level)对象``rectangle``。抽象类``RootElement``继承抽象类``ElementPropertyContainer``。``ElementPropertyContainer``类定义了类似``setBorder()``和``setBackgroudColor()``类似的方法，但是这些方法不能被使用因为对于``Canvas``来说设置一个边框或者背景是不可行的，``Document``也是不行的。在``ElementPropertyContainer``定义的每一个方法对于它的子类来说并不是都有意义的。例如：为一个``Image``设置字体方法``setFont()``是没有意义的。你可以在[附录C](https://developers.itextpdf.com/content/itext-7-building-blocks/c-rootelement-methods)里面查看对于``Canvas``和``Document``是有意义的。

&nbsp;&nbsp;&nbsp;&nbsp;在下图中，我们创建了带有两个页面的文档，但是这个文档有些特别：当我们往第二页添加内容以后，我们在存在的第一页的底下添加了内容，如图2.4所示：  

![itext-h-2-4](http://oss.cuteke.cn/itext-h-2-4.png)  

<center>图2.4：在前面一页中添加内容</center>

&nbsp;&nbsp;&nbsp;&nbsp;第一部分的代码和我们之前的例子中的代码是一样的：定义了第一个页面和一个``rectangle``,使用这个页面和矩形来创建``Canvas``实例，然后我们定义一个``Paragraph``对象然后添加这个对象到画布``Canvas``。接着我们来看一下添加第二页内容的代码实现：
```
PdfPage page2 = pdf.addNewPage();
PdfCanvas pdfCanvas2 = new PdfCanvas(page2);
Canvas canvas2 = new Canvas(pdfCanvas2, pdf, rectangle);
canvas2.add(new Paragraph("Dr. Jekyll and Mr. Hyde"));
canvas2.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们一行一行来看：
- 行1：使用``addNewPage()``方法来向文档添加新的一页
- 行2：使用这个页面来创建一个新的``PdfCanvas``
- 行3：使用新的``PdfCanvas``，``PdfDocument``和``Rectangle``来创建新的``Canvas``
- 行4：添加这个``Paragraph``到``Canvas``

&nbsp;&nbsp;&nbsp;&nbsp;这应该看起来很直接了当，但是我们来看接下来的代码：
```
PdfPage page1 = pdf.getFirstPage();
PdfCanvas pdfCanvas1 = new PdfCanvas(
    page1.newContentStreamBefore(), page1.getResources(), pdf);
rectangle = new Rectangle(100, 700, 100, 100);
pdfCanvas1.saveState()
        .setFillColor(Color.CYAN)
        .rectangle(rectangle)
        .fill()
        .restoreState();
Canvas canvas = new Canvas(pdfCanvas1, pdf, rectangle);
canvas.add(new Paragraph("Dr. Jekyll and Mr. Hyde"));
canvas.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;行1，使用``getFirstPage()``方法来获取``PdfPage``实例。

> ``getFirstPage()``是``getPage()``方法的定制版方法。只要``PdfDocument``实例没有关闭你可以获取任何页面

&nbsp;&nbsp;&nbsp;&nbsp;行2和行3，我们使用了如下参数创建了一个``PdfCanvas``对象：

- 一个``PdfStream``实例：一个页面包含一个或者多个内容流。在这个例子中，我们想在已经存在的内容**下**添加内容，因此我们使用``newContentStreamBefore()``方法。如果你想要在已经存在的内容**上**添加内容，你应该使用``newContentStreamAfter()``方法。这些方法会创建一个新的内容流，并且把它添加到页面中。同样的，你也可以获取这些已经存在的内容流。``getContentStreamCount()``会告诉你当前页面的内容流有多少。``getContentStream()``方法允许你通过索引(index)来获取特定的内容流。类似的，同样也有``getFirstContentStream()``和``getLastContenStream()``方法、
- 一个``PdfResources``实例：内容流自己不足以渲染一个页面。每一个页面都会指向资源文件，例如字体和图片。当我们向页面添加内容的时候，我们需要使用和更新资源
- ``PdfDocument``实例：我们一直在使用的底层/低级对象

&nbsp;&nbsp;&nbsp;&nbsp;行4，我们定义了一个矩形。行5-行9把矩形画成蓝青色。行10-11，我们创建了一个``Canvas``对象并把``Paragraph``添加进入。 

> 能够回退到之前的页面并且添加内容到那页面是iText7中新的并且强大的特性。而iText5的架构不允许我们改变已经“完成”页面的内容。这也是iText官方想抛弃iText5架构重新写iText的众多因素之一

> 至今，我们都是一直用``Canvas``类来添加内容到``PdfCanvas``。在章节7，我们会发现另一个用例：你同样可以使用``Canvas``类来添加内容到``PdfFormXobject``。*form XObject*是任何页面内容流的外部对象。它表示可以从同一页面或不同页面多次引用的PDF内容流。这是一个可重复使用的PDF语法流。``Canvas``对象允许你没有任何困难创建PDF语法。

&nbsp;&nbsp;&nbsp;&nbsp;是时候我们已经创建一个PDF包含所有故事内容，而不是只有标题和作者的一个页面。我们将使用``Document``类来完成我们的任务。 

# 2.使用Document类来转换文本成PDF
&nbsp;&nbsp;&nbsp;&nbsp;下图2.5展示了整个故事的内容：  

![itext-h-2-5](http://oss.cuteke.cn/itext-h-2-5.png)  

<center>图2.5：Jekyll and Hyde故事的文本文件</center>

&nbsp;&nbsp;&nbsp;&nbsp;接下来我们会在接下来的一系列例子中一步一步地转换成PDF，我们首先创建如图2.6所示的文件：  

![itext-h-2-6](http://oss.cuteke.cn/itext-h-2-6.png)  

<center>图2.6：文本转PDF的第一个例子</center>

&emsp;&emsp;这个例子很简单，在下面代码中没有新的函数：
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
Document document = new Document(pdf);
BufferedReader br = new BufferedReader(new FileReader(SRC));
String line;
while ((line = br.readLine()) != null) {
    document.add(new Paragraph(line));
}
document.close();
```
&emsp;&emsp;在行1，我们创建了低等级的``PdfDocument``对象。在行2，我们创建了高等级的``Document``实例。在行3中我们创建了一个``BufferedReader``来读文本文件。在行4和行7之间我们在循环之间读取每一行。在行6，我们把每一行天剑到``Paragraph``对象中，然后添加到``Document``对象中。在行8中，我们关闭了文档。这个结果是一个有全部"The Strange Case of Dr. Jekyll and Mr. Hyde."故事的42页的PDF。  

&emsp;&emsp;虽然这样的PDF效果还算可以，但是我们可以做的更好。在下图2.7中我们能很快看出我们改变了对齐方式。我们采用了双端对齐来替代左对齐。如果你再仔细看一点，你会发现我们引进了连接符，就是一个单词出现在另一行会用连接符来连接。   

![itext-h-2-7](http://oss.cuteke.cn/itext-h-2-7.png)  

<center>图2.7：文本转PDF的第二个例子</center>

&emsp;&emsp;对于这个例子，我们复制了第一个例子的代码，并且添加了如下的代码：
```
document.setTextAlignment(TextAlignment.JUSTIFIED)
    .setHyphenation(new HyphenationConfig("en", "uk", 3, 3));
```
&emsp;&emsp;我们使用``setTextAlignment()``方法在``Document``级别来改变对齐方式。我们使用``setHyphenation()``方法来定义连字符规则。在这个样例中，我们创建了一个``HyphenationConfig``对象来把文本当成英式英语。当分割一个单词的时候，我们指出我们在分割点之前至少需要3个字母，在分割点之后至少需要3个字母。举2个例子，例如"elephant"这个单词不能被分割成"e-lephant"，因为"e"是少于三个字母；正确的分法应该是像"ele-phant"这种分割法。“attitude"不能被分割成"attitude-de”因为"de"少于3个字母。正确的分法应该像"atti-tude"这种分法。

> 在``Document``级别修改默认设置，例如默认的对齐方式，默认的断字方式，或者默认的字体，这是在iText5中不可能的。你需要在单独的基础构建块中定义这些属性。而在iText7中，我们引入了属性继承，默认的字体还是Helvtica，但是我们现在可以在``Document``级别定义一种不同字体。

> 如果你不能看到文本被正确的分割，请确认hyph包被正确的引入，请在pom.xml引入或者下载jar文件，如下：

```
<dependency>
     <groupId>com.itextpdf</groupId>
     <artifactId>hyph</artifactId>
     <version>${iText_Version}</version>
 </dependency>  
```

&emsp;&emsp;图2.8展示了我们第三次把文本文件转换成一个PDF文件。我们把字体从Helvetica 12pt转换成TimeRoman 11pt。结果就是页数从42页缩小到34页。  

![itext-h-2-8](http://oss.cuteke.cn/itext-h-2-8.png)  

<center>图2.8：文本转PDF的第三个例子</center>

&emsp;&emsp;这是相应的代码，我们可以看到两种不同的字体被使用：
```
Document document = new Document(pdf);
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
document.setTextAlignment(TextAlignment.JUSTIFIED)
    .setHyphenation(new HyphenationConfig("en", "uk", 3, 3))
    .setFont(font)
    .setFontSize(11);
```
&emsp;&emsp;Time-Roman被使用为默认字体，但是我们也定义了标题的字体为Helvetica-Bold。txt文件的构成方式是第一行是书的标题和作者。故事中的每个其他标题之前面都有一个空行。每个不是标题的行都是一个完整的段落。知道这一点，我们可以逐行调整读取文本文件的循环。  
```
BufferedReader br = new BufferedReader(new FileReader(SRC));
String line;
Paragraph p;
boolean title = true;
while ((line = br.readLine()) != null) {
    p = new Paragraph(line);
    p.setKeepTogether(true);
    if (title) {
        p.setFont(bold).setFontSize(12);
        title = false;
    }
    else {
        p.setFirstLineIndent(36);
    }
    if (line.isEmpty()) {
        p.setMarginBottom(12);
        title = true;
    }
    else {
        p.setMarginBottom(0);
    }
    document.add(p);
}
```
&emsp;&emsp;这个代码片段比之前的要稍微复杂一点，但是我们一步一步来解读：
- 我们在行4中创建了一个boolean类型的变量``title``并把值设置成``true``，因为我们知道txt文件的第一行为标题。在行6中我们为每一行创建了一个``Paragraph``并且调用了``setKeepTogether()``方法，因为我们不希望iText把段落分部在不同的页面上(行7)。如果一个``Paragraph``不能适应在当前页，它会被放在下一页，除非下一页也适应不了，如果发生下一页也适应不了的情况，这个段落会被分为分裂成两块，一块在当前页，另一块在下一页。
- 如果``title``的值为``true``，我们会修改字体，一开始为在``Document``里面定义的11pt大小的Times-Roman字，变成12 pt带下的Helvetica-Bold字体。然后我们知道在txt文件中下一行的文本内容是正常的文本内容，所以在行9-11中把``title``的值设置为``false``。对于正常文本内容，我们改变首行的缩进以此来区分不同的段落(行12-14)。
- 如果当前行为一个空的``String``，我们定义一个大小为12的下边距，并且把``title``的值改回``true``(行17)，这是因为我们知道下一行内容为一个标题；对于其余情况，也就是其他行，我们把``Paragraph``的下边距改为0(行20)。
- 一旦所有``Paragraph``的所有属性被设置，我们把它添加到``Document``中(行22)。

&emsp;&emsp;图2.8向我们展示了iText可以很好地把文本渲染成PDF页面。现在我们想要把文本渲染成两列，在一页上并排组织，如果这样的话，我们需要引入一个``DocumentRenderer``实例。

# 3.改变Document渲染器

在图2.8的例子中使用了默认``Document``和前面例子拥有同样属性的``Paragraph``。接下来的例子主要有一个主要的不同点：文本在每一页渲染成两列。

![itext-h-2-9](http://oss.cuteke.cn/itext-h-2-9.png)  

<center>图2.9 把文本渲染成两列</center>

&emsp;&emsp;为了能达到整个效果，我们使用``ColumnDocumentRender``类。这个类通常默认使用即可，是``DocumentRenderer``的子类。下面这个例子代码解释了``ColumnDocumentRender``被创建和使用。
```
float offSet = 36;
float gutter = 23;
float columnWidth = (PageSize.A4.getWidth() - offSet * 2) / 2 - gutter;
loat columnHeight = PageSize.A4.getHeight() - offSet * 2;
Rectangle[] columns = {
   new Rectangle(offSet, offSet, columnWidth, columnHeight),
   new Rectangle(
      offSet + columnWidth + gutter, offSet, columnWidth, columnHeight)};
document.setRenderer(new ColumnDocumentRenderer(document, columns));
```
&emsp;&emsp;我们定义了``Rectangle``类型的一维数组，然后使用了这个数组来创建一个``ColumnDocumentRenderer``对象。我们使用``setRenderer()``方法告诉``Document``不使用默认``DocumentRenderer``实例而使用这个渲染器。

&emsp;&emsp;如果在iText 5中我们想要组织内容以列的形式呈现，那就需要使用``ColumnText``对象。在iText 2中有一个``MultiColumnText``对象可以减少写分布列的代码数，但是在iText 5中因为缺少健壮性被移出。有了``ColumnDocumentRenderer``类，开发者现在可以有一种可靠的方式来创造列，而不需要在像iText 5那样写很多代码。

&emsp;&emsp;接着我们在解析文本的时候有了一丁点改变：
```
BufferedReader br = new BufferedReader(new FileReader(SRC));
String line;
Paragraph p;
boolean title = true;
AreaBreak nextArea = new AreaBreak(AreaBreakType.NEXT_AREA);
while ((line = br.readLine()) != null) {
    p = new Paragraph(line);
    if (title) {
        p.setFont(bold).setFontSize(12);
        title = false;
    }
    else {
       p.setFirstLineIndent(36);
   }
   if (line.isEmpty()) {
      document.add(nextArea);
       title = true;
   }
   document.add(p);
}
```
&emsp;&emsp;在行5，我创建了一个``AreaBreak``对象。这是一个布局对象，它的作用是终结当前域的内容并且创建新的域。在这个例子中，我们创建了一个``NEXT_AREA``类型的``AreaBreak``，而且在每一章之前加入这个对象。有了这个的引入会产生图2.10一样的效果。

![itext-h-2-10](http://oss.cuteke.cn/itext-h-2-10.png)  

<center>图2.10 NEXT_AREA类型的AreaBreak的效果</center>

&emsp;&emsp;没有了``AreaBreak``，章节"INCIDENT AT THE WINDOW"会在19页的左边出现，也就是紧接着前一章内容之后。引入了``AreaBreak``之后，新的章节在新的一列开始。如果我们使用``NEXT_PAGE``类型的``AreaBreak``，新的章节会在新的一页上开始，如图2.11所示。

![itext-h-2-11](http://oss.cuteke.cn/itext-h-2-11.png)  

<center>图2.11 NEXT_PAGE类型的AreaBreak的效果</center>

&emsp;&emsp;而在代码上，我们只改变了一行：
```
AreaBreak nextPage = new AreaBreak(AreaBreakType.NEXT_PAGE);
```
&emsp;&emsp;使用了这个以后，iText现在不是调到下一列而是下一页。

> 默认的情况是新创建的页面和当前页有着同样的大小。如果你想要iText创建一个页面是另外一个大小的，你可以使用带``PageSize``类型的构造函数。例如：``new AreaBreak(PageSize.A3)``。

&emsp;&emsp;还有一个类型为``LAST_PAGE``的``AreaBreak``。这种类型是用来在不同渲染器之间切换。

# 4.在不同渲染器之间切换

图2.12向我们展示在第一页我们使用了默认的``DocumentRenderer``，而在第二页使用了两列的``ColumnDocumentRenderer``渲染器。

![itext-h-2-12](http://oss.cuteke.cn/itext-h-2-12.png)  

<center>图2.12 NEXT_PAGE类型的AreaBreak的效果</center>

&emsp;&emsp;如果我们查看这个例子的代码，我们可以看到有两次切换选软器。
```
public void createPdf(String dest) throws IOException {
   PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
   Document document = new Document(pdf);
   Paragraph p = new Paragraph()
       .add("Be prepared to read a story about a London lawyer "
       + "named Gabriel John Utterson who investigates strange "
       + "occurrences between his old friend, Dr. Henry Jekyll, "
       + "and the evil Edward Hyde.");
   document.add(p);
   document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
   ... // Define column areas
   document.setRenderer(new ColumnDocumentRenderer(document, columns));
   document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
   ... // Add novel in two columns
   document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
   document.setRenderer(new DocumentRenderer(document));
   document.add(new AreaBreak(AreaBreakType.LAST_PAGE));
   p = new Paragraph()
       .add("This was the story about the London lawyer "
       + "named Gabriel John Utterson who investigates strange "
       + "occurrences between his old friend, Dr. Henry Jekyll, "
       + "and the evil Edward Hyde. THE END!");
   document.add(p);
   document.close();
}
```
&emsp;&emsp;在行4-9我们在第一页中添加了一个长的``Paragraph``。因为我们没有定义任何的渲染器，默认的``DocumentRenderer``将会被使用。然后我们在行10引入了一个断页，并且改变渲染器到一个有两列的``ColumnDocumentRenderer``。紧接着，我们引入了调到最后一页的``AreaBreak``，这么做有必要吗？原因是？

> 不管在任何时候你创建了一个新的``DocumentRenderer``，iText都会跳转到第一页。这就你能在相同的文档的同一页上使用相邻的选软器，如果你需要这样做，我们需要通知iText不要吧内容刷新到``OutputStream``；否则我们对前面的页面不能访问，所以我们在前面的页面上不需要改变任何东西，我们只需要在下一页切换到另一个渲染器即可。引入一个可以到最后页面的断页可以避免新的内容覆盖旧的内容。

&emsp;&emsp;如果我们遗漏``document.add(new AreaBreak(AreaBreakType.LAST_PAGE));``，你们新的以列组织的内容会添加到第一页，并且覆盖掉长的``Paragraph``。

&emsp;&emsp;在添加完小说的内容后在行15行引入了另一个断页，改变渲染器回到默认的标准``DocumentRenderer``(行17)。同样我们在添加新的段落之前要添加一个类型为``LAST_PAGE``的断页。

&emsp;&emsp;这个例子阐释了``NEXT_AREA``，``NEXT_PAGE``和``LAST_PAGE``之间不同点，并且有了抽象的认识。但是我们忽视了一个在渲染PDF时的一个重要的问题：在何时我们需要刷新内容到``OutputStream``中？

# 5.刷新文档渲染器

&emsp;&emsp;如果你去观察``Canvas``，``Document``，``CanvasRenderer``，``DocumentRenderer``和``ColumnDocumentRenderer``的API文档，你会主要到这些对象都至少有一个能接受叫``immediateFlus``，类型为Boolean的构造函数。至今，我们都没有任何这样的构造函数，所以iText会使用默认值：``true``。所有我们添加的内容会立刻刷新。

&emsp;&emsp;在接下来的三个例子中，我们将会把这个值设置为``false``。在这三个例子中，我们会推迟内容的刷新，原因有三点：1.改变添加内容后的布局，2.改变对象添加后的内容，3.往前面的页面中添加内容。

&emsp;&emsp;在iText 5中，添加到``Document``的内容会在一页内容满以后刷新到``OutputStream``中。一旦内容被添加到一个页面中，我们没法改（包括布局）内容。而在iText 7中，有一种方式可以推迟内容的实际渲染，允许我们改变哪些添加到``Canvas``或者`Document``之中的内容。  
&emsp;&emsp;让我们回到之前把文档转换为有两列的PDF，并且在每章引入断页例子中。这些断页导致有一些页面只有一列的内容，由图2.11可以看到，这个列在页面的右边。

&emsp;&emsp;现在我们想要把这些单独的移到页面的中间，如图2.13所示。

![itext-h-2-13](http://oss.cuteke.cn/itext-h-2-13.png)  

<center>图2.13 把列移到页面的中间</center>

&emsp;&emsp;我们不会提前预支何时这种情况会发生，当我们一行一行的解析文本的时候，我们不会知道下一行会给我们带来什么，这可以是另一个``Paragraph``，或者是一个``LineBreak``。这意味着我们不应该立刻渲染内容。如果我们这么做了，当一个章节在左边列结束的时候不能做到把它移动到中间。我们需要推迟刷新，也就是下面例子所做到的那样。

&emsp;&emsp;在这个例子中，我们使用了``ColumnDocumentRenderer``，并把它应用到我们的特殊需求中。
```
class MyColumnRenderer extends DocumentRenderer {
   protected int nextAreaNumber;
   protected final Rectangle[] columns;
   protected int currentAreaNumber;
   protected Set moveColumn = new HashSet();

   public MyColumnRenderer(Document document, Rectangle[] columns) {
       super(document, false);
       this.columns = columns;
   }

   @Override
   protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
       if (overflowResult != null
           && overflowResult.getAreaBreak() != null
           && overflowResult.getAreaBreak().getType()
               != AreaBreakType.NEXT_AREA) {
           nextAreaNumber = 0;
       }
       if (nextAreaNumber % columns.length == 0) {
           super.updateCurrentArea(overflowResult);
       }
       currentAreaNumber = nextAreaNumber + 1;
       return (currentArea = new LayoutArea(currentPageNumber,
           columns[nextAreaNumber++ % columns.length].clone()));
   }

   @Override
   protected PageSize addNewPage(PageSize customPageSize) {
       if (currentAreaNumber != nextAreaNumber
           && currentAreaNumber % columns.length != 0)
           moveColumn.add(currentPageNumber - 1);
       return super.addNewPage(customPageSize);
   }

   @Override
   protected void flushSingleRenderer(IRenderer resultRenderer) {
       int pageNum = resultRenderer.getOccupiedArea().getPageNumber();
       if (moveColumn.contains(pageNum)) {
          resultRenderer.move(columns[0].getWidth() / 2, 0);
       }
       super.flushSingleRenderer(resultRenderer);
  }
}
```
让我们看一下这个自定义的``DocumentRenderer``：
- 行2-5：我们重新使用了``DocumentRenderer``的两个成员变量：``nextAreaNumber``整数跟踪记录列的数量；``columns``数组存储了每一列的位置和尺寸。然后我们添加了一个额外的证书``currentAreaNumber``来记录当前的列的数量和一个容器变量``moveColumn``记录那些只有单个列的页码。
- 行7-9：构建一个``MyColumnRenderer``实例。首先调用``DocumentRenderer``父类的够赞函数，然后设置``immediateFlush``变量为``false``：内容不会被立刻刷新。
- 行12-26：``updateCurrentArea``方法和在``ColumnDocumentRenderer``类中同名方法的作用大致相同，除了一点细微的不同：我们会设置``currentAreaNumber``的值为``nextAreaNumber + 1``。这个方法会在新起一列的时候被调用。注意当一个断页被引入的时候，``currentAreaNumber``的值会重置为``0``。
- 行28-32：覆写了``newPage()``方法。这个放啊会在展开新的一页的时候触发调用。内容是否渲染到前面的页面取决于``immediateFlush``变量的值。我们可以使用这个方法来检查前面的页面是否只有一个列。这种情况只有当``currentAreaNumber``的值和``nextAreaNumber``的值不相等，并且``currentAreaNumber``的值为奇数（这里面列的个数为2）。如果在前面的页面中只有一个列，那么我们把那一页的页码(``currentPageNumber - 1``)添加到``moveColumns``容器中。
- 行36-43：：覆写了``flushSingleRenderer()``方法。这个方法用来控制渲染内容。如果``immediateFlush``的值为``true``，那么这个方法会自动调用。如果值为``false``，我们需要手动触发。我们覆写这个方法的原因是因为我们想要在``newPage()``方法对那些只有一个列的页面把``IRender``的坐标系往右移动半列的大小。

&emsp;&emsp;然后我们可以看一下如何使用这个自定义的列渲染器。
```
Rectangle[] columns = {
   new Rectangle(offSet, offSet, columnWidth, columnHeight),
   new Rectangle(
       offSet + columnWidth + gutter, offSet, columnWidth, columnHeight)};
DocumentRenderer renderer = new MyColumnRenderer(document, columns);
document.setRenderer(renderer);
```
&emsp;&emsp;首先定义了一个有两个``Rectangle``类型的对象数组。利用这个数组我们创建了一个自定义``MyColumnRenderer``的实例，然后使用这个实例来当做``Document``的渲染器。接下来的代码和我们之前的一样，设置``Document``的默认值，然后解析文本文件并且添加内容。

&emsp;&emsp;如果我们在添加所有内容后关闭``document``，那结果会得到一个内容为空的文档。在我们的渲染器，我们一个一个创建，并且一页一页添加，但是我们并没有渲染任何东西，因为``flushSingleRenderer() ``方法从未被调用。我们需要手动调用这个方法，并且我们需要像这样干：
```
renderer.flush();
document.close();
```
&emsp;&emsp;当renderer调用``flush()``方法，那我们没有刷新的添加的所有内容将会被渲染。``flushSingleRenderer()``将会被多次调用，因为很多对象被添加到``Document``。哪些只有单列的页面就会移动到页面中间。

> 这是本教程中复杂例子中的一个，编码自己的``RootRender``实现并不容易，但是它能很大程度上帮助你创建你想要的PDF文档，这也是iText的方式。

&emsp;&emsp;让我们继续当创建一个``Document``实例是需要使用``immediateFlush``参数的例子。

# 6.改变之前添加的内容

&emsp;&emsp;先观察一下图2.14，第一眼看上去和我们之前例子没有什么不同，但是在第一行有着明显的不同。

![itext-h-2-14](http://oss.cuteke.cn/itext-h-2-14.png)  

<center>图2.14 一开始页面实现整个文档的页面数</center>

&emsp;&emsp;文档的第一行有着This document has 34 pages"。从之前的例子我们知道我们是一步一步地，一行一行的构建文本的。当我们解析文本文件的一行内容的时候，是无法预知整个文档需要多少页。我们是怎么知道这个文档总共是34页的呢？

&emsp;&emsp;实话实说，我们不要猜，这里是有小技巧的，下面的例子代码解释了这个小技巧。  
&emsp;&emsp;首先创建了一个``immediateFlush``参数为flase的``Document``。
```
 Document document = new Document(pdf, PageSize.A4, false);
```
&emsp;&emsp;我们首先添加到``document``中第一个对象是一个文本This document has {totalpages} pages."。
```
Text totalPages = new Text("This document has {totalpages} pages.");
IRenderer renderer = new TextRenderer(totalPages);
totalPages.setNextRenderer(renderer);
document.add(new Paragraph(totalPages));
```
&emsp;&emsp;显而易见的是我们使用了一个占位符``{totalpages}``来表明所有页面的数量。我们创建了一个``TextRenderer``实例，并把这个实例当做``Text``对象的下一个渲染器。然后把这个``Text``传参到一个``Paragraph``之中并把这个段落添加到``document``，紧接着我们添加Dr. Jekyll and Mr. Hyde的故事，因为我们设置了``immediateFlush``的值为``false``，在最后时刻刷新之前都不会有任何文本被渲染，这个最后时刻可能是当我们关闭这个文档，第一行还是This document has {totalpages} pages."。

&emsp;&emsp;现在一切都按照我们所计划地进行，现在想要在渲染文本之前把``{totalpages}``修改成真实的页数。我们可以使用``TextRenderer``对象来实现：
```
String total = renderer.toString().replace("{totalpages}",
   String.valueOf(pdf.getNumberOfPages()));
((TextRenderer)renderer).setText(total);
((Text)renderer.getModelElement()).setNextRenderer(renderer);
document.relayout();
document.close();
```
&emsp;&emsp;在行1-2，我们把``String``"This document has {totalpages} pages."修改成了"This document has 34 pages."。我们可以看到，我们可以从渲染器中重新获得原始的``Text``，并且被占位符替换为``pdf.getNumberOfPages()``。在行3-4，我们修改了文本的``TextRenderer``并且把这个已改变的文本渲染器添加到``Text``对象。

&emsp;&emsp;如果我们在行4之后就立即关闭文档，生成的PDF文档仍旧会显示"This document has {totalpages} pages."，为了使改变生效，我们需要重新布局文档。在行5中使用``relayout()``方法来完成，只有当重新布局完成后，我们可以关闭文档(行6)。

&emsp;&emsp;在iText 5中，我们通过添加一个固定维度的的占位符的方式或多或少都可以实现这个功能。一旦整个文档被渲染，我们可以把整个页面数填充到占位符上。在itext 6的教程第7章也是用的和iText 7一样的方法。但是iText 7现在可以提供一种可替代的方法， 这种方法可以改变``Text``的内容并且重新创建布局。  
&emsp;&emsp;改变``Text``的内容还是有一点复杂的，还是有很多不需要重新创建布局的情况，正如在下一个示例中所演示的那样，可以大大降低复杂性。

# 7.添加Page X of Y 页脚

&emsp;&emsp;图2.15所示，每一页的页脚阐明了当前页码和总页数。

![itext-h-2-15](http://oss.cuteke.cn/itext-h-2-15.png)  

<center>图2.15 Page X of Y 页脚</center>

&emsp;&emsp;为了能做到这样，我们使用了一种比之前样例都要简单的方法，让我们来详细看一下这个例子。

&emsp;&emsp;同样的，我们需要让``Document``不需要离开刷新内容。
```
Document document = new Document(pdf, PageSize.A4, false);
```
&emsp;&emsp;当我们添加完这个故事的所有内容后，我们循环文档的每一页并且添加一个``Paragraph``到每一页中。
```
int n = pdf.getNumberOfPages();
Paragraph footer;
for (int page = 1; page <= n; page++) {
   footer = new Paragraph(String.format("Page %s of %s", page, n));
   document.showTextAligned(footer, 297.5f, 20, page,
       TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0);
}
document.close();
```
&emsp;&emsp;``showTextAligned()``方法是用来在任意一个页面的绝对位置上添加文本，使用一个特定的相对于所选坐标系的垂直和水平对齐，并且使用一个特定的角度。

&emsp;&emsp;在这种情况下，我们循环所有页面(从1到34)，然后在在坐标``(x = 297.5f,y = 20)``的水平和垂直居中的位置上添加一行文本。我们不需要改变已经添加内容布局，所以我们不需要使用``relayout()``方法。当我们``close()``文档的时候所有内容都会被渲染。

&emsp;&emsp;这个例子只会当你设置``immediateFlush``为``flase``的时候生效，如果你忘记设置了这个值，那么你会得到以下的异常：
```
Exception in thread "main" java.lang.NullPointerException at com.itextpdf.kernel.pdf.PdfDictionary.get(PdfDictionary.java)
```
&emsp;&emsp;这个异常会发生是因为你尝试改变已经被刷新到``OutputStram``的一个页面字典上内容。iText还是拥有那个页面字典的引用，但是这个字典已经不再存在，所以会抛出``NullPointerException``异常。

> 为什么我们在第4个例子中不会的到NullPointerException？  
> 在第4个例子中，我们创建了``PdfPage``对象。当我们使用这些底层/低级函数API的时候吗，我们有责任来管理所有资源。我们可以在一个完成的页面``PagePage``对象上使用``flush()``来把内容刷新到``OutputStream``。一旦这么做了，我们不能再往这个页面添加任何东西。如果我们尝试获取那个页面的(一个或多个)内容流，那么我会得到一个``NullPointerException``异常。

# 8.使用showTextAligned方法添加文本

&emsp;&emsp;在``RootElement``类中可以使用不同的``showTextAligned()``方法。这些方法可以在``Canvas``和``Document``对象中使用来把单行的文本添加在一个特定的位置上。如果这行文本在``Canvas``或者``Document``的当前页不能适应，它会被分割为两行，它甚至可以抛出页面外，也就是可视的页面外。

![itext-h-2-16](http://oss.cuteke.cn/itext-h-2-16.png)  

<center>图2.16 在绝对位置上添加文本</center>

&emsp;&emsp;图2.16所展示的PDF使用如下的代码：
```
Paragraph title = new Paragraph("The Strange Case of Dr. Jekyll and Mr. Hyde");
document.showTextAligned(title, 36, 806, TextAlignment.LEFT);
Paragraph author = new Paragraph("by Robert Louis Stevenson");
document.showTextAligned(author, 36, 806,
    TextAlignment.LEFT, VerticalAlignment.TOP);
document.showTextAligned("Jekyll", 300, 800,
    TextAlignment.CENTER, 0.5f * (float)Math.PI);
document.showTextAligned("Hyde", 300, 800,
   TextAlignment.CENTER, -0.5f * (float)Math.PI);
document.showTextAligned("Jekyll", 350, 800,
   TextAlignment.CENTER, VerticalAlignment.TOP, 0.5f * (float)Math.PI);
document.showTextAligned("Hyde", 350, 800,
   TextAlignment.CENTER, VerticalAlignment.TOP, -0.5f * (float)Math.PI);
document.showTextAligned("Jekyll", 400, 800,
   TextAlignment.CENTER, VerticalAlignment.MIDDLE, 0.5f * (float)Math.PI);
document.showTextAligned("Hyde", 400, 800,
   TextAlignment.CENTER, VerticalAlignment.MIDDLE, -0.5f * (float)Math.PI);
```
&emsp;&emsp;在行1和行3中，我们创建了两个``Paragraph``对象。我们使用``showTextAligned()``方法添加这些对象到当前页。
- 在行2，我们在坐标坐标``(x = 36,y = 806)``上添加了``Paragraph``，并且设置水平左端对齐，我们没有设置垂直对齐，默认值``VerticalAlignment.BOTTOM``将会被使用，意味着垂直底部对齐，坐标系为内容的底部。
- 在行4-5，我们在相同的坐标上添加了内容，但是我们使用一种不同的垂直对齐方式：``VerticalAlignment.TOP``，也就是垂直顶端对齐，坐标系为内容顶部。

&emsp;&emsp;在行6到17，我们使用``String``而不是``Paragraph``来添加文本。我们也引入了旋转角度90度(``0.5f * (float)Math.PI``)和-90度。
- 在行6-9，我们在同一个坐标上添加了两个名字，但是使用了不同的旋转角度，同样在行10-13也是同样地做法，"Jekyll"和"Hyde"的呈现方式不同点在于``VerticalAlignment``的值(因为我们旋转了90度，垂直变成了水平，反之亦然)
- 在行14-17，同样我们在相同的坐标和不同的角度下添加两个名字，但是使用了相同的``VerticalAlignment.MIDDLE``。这些名字被写在一起，几乎无法分辨出。

&emsp;&emsp;这个例子向我们展示了``showTextAligned()``方法的变种方法，其实还有一种方法叫``showTextAlignedKerned()``，但是在使用这个方法之前有必要了解一下在使用iText add-ons之前的注意事项。

# 9.使用iText 7 add-ons

&emsp;&emsp;iText 7的核心代码是当做许可证为APGL的开源项目，这叫意味着你可以在软件中免费使用它而不需要付任何费用，*只要你同样在APGL下发布软件*。我们简单来说：只要你让自己的源代码免费获取，那么你也可以免费使用iText。只要你发布的软件在另一种协议下：例如你为一名顾客服务，这名顾客在非开源的情况下使用代码，那么你或者你的顾客需要购买iText 7的商用许可证。

&emsp;&emsp;关于APGL还有更多的内容，但是这将导致我们深入讨论AGPL;这是一本技术教程，不是一本法律书。

> 许多开发人员没有意识到使用AGPL软件的含义。由于许多不同的原因，这可能非常烦人。以下是一些令人烦恼的例子:
> - 即将获得资金或被收购的公司未能通过尽职调查程序，因为他们没有使用iText的商业许可。
> - 即将获得资金或被收购的公司未能通过尽职调查程序，因为他们没有使用iText的商业许可。iText集团成功起诉一家公司，公然滥用我们的知识产权，这个作为证明AGPL可以执行的一个例子。该案件在大约一个半月内获胜。这很快，但是在iText集团，我们都同意有更好的方式来花时间而不是去法院，因为有些公司错误地认为开源软件是免费且免费的软件。
> - 有些公司故意忽略了AGPL许可证的含义。这导致购买商业许可证的客户之间的不公平竞争，允许我们投资于进一步开发，以及从进一步开发中受益的用户，拒绝以任何方式做出贡献。

&emsp;&emsp;pdfCalligraph模块(又称typography jar)是这样一个闭源的插件。我们花费了很多时间和经历来改进排版。有了pdfCalligraph模块，iText最终支持印度语写作系统，如梵文和泰米尔语。iText现在还支持特殊功能，例如阿拉伯语元音的可视化。所有这些功能都可以在单独的typography jar中使用。

&emsp;&emsp;你可以通过引入如下依赖来使用pdfCalligraph插件：
```
<dependency>
   <groupId>com.itextpdf</groupId>
   <artifactId>typography</artifactId>
   <version>1.0.0</version>
   <scope>compile</scope>
</dependency>
```
&emsp;&emsp;这个插件是闭源的，所有不能在Maven Central RepositoryZ中获取到。但是你可以在iText repository源中获取，你可以在POM文件中添加这个源：
```
<repositories>
   <repository>
       <id>central</id>
       <name>iText Repository-releases</name>
       <url>https://repo.itextsupport.com/releases</url>
   </repository>
</repositories>
```
&emsp;&emsp;当添加一个闭源插件的时候，你需要许可证密钥才能加载该项。你需要使用 itext-licensekey jar来将改密钥导入代码中，这是itext-licensekey jar的依赖关系：
```
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-licensekey</artifactId>
    <version>2.0.0</version>
   <scope>compile</scope>
</dependency>
```
&emsp;&emsp;可以想这样添加许可证密钥到你的代码中：
```
LicenseKey.loadLicenseFile(new FileInputStream(KEY));
```
&emsp;&emsp;在这个例子中，``KEY``的值指向自己的使用typography jar的许可证密钥文件。

&emsp;&emsp;如果你引用一个插件，但是添加``loadLicenseFile()``方法，那么你会获得如下异常：
```
Exception in thread "main" java.lang.RuntimeException: java.lang.reflect.InvocationTargetException ... Caused by: com.itextpdf.licensekey.LicenseKeyException: License file not loaded.
```
&emsp;&emsp;如果你尝试加载许可证密钥文件，但是它丢失了，那么下面的异常就会抛出：
```
Exception in thread "main" java.io.FileNotFoundException:itextkey.xml (The system cannot find the path specified)
```
&emsp;&emsp;如果许可证文件存在，但是已被破坏，那么你会的到这个``LicenseKeyException``异常：
```
Exception in thread "main" com.itextpdf.licensekey.LicenseKeyException: Signature was corrupted.
```
&emsp;&emsp;如果你正在使用过期的许可证密钥，那么你会获得另一个消息：
```
Exception in thread "main" com.itextpdf.licensekey.LicenseKeyException: License expired.
```
&emsp;&emsp;还有更多常见的异常会抛出，通常，抛出的消息会告诉你出现了什么问题。在下一个例子中，我们会使用typography jar来进行字距调整。

# 10.改进排版

&emsp;&emsp;图2.17展示调整了字距的文本和没有调整的文本之间的差异。

![itext-h-2-17](http://oss.cuteke.cn/itext-h-2-17.png)  

<center>图2.17 字距调整的文本</center>

&emsp;&emsp;在史蒂文森的短篇小说的标题中，字距调整机制并不那么明显。差异在细节中：在``Dr``和``Mr``之后的``.``在调整的行中轻微的移动。当字距调整被激活时，将查询字体程序以获取字距调整信息。在这种情况下，字体程序知道当``r``和``.``相遇时，``.``应该靠近于``r``。

&emsp;&emsp;这个机制更容易在``AWAY``这个单词中发现。在调整的版本中，字母``A``在``W``的两边都更加靠近，，字母``A``在``Y``的距离也更加近。下面代码展示了如何使用``showTextAlignedKerned()``来做到这种情况。
```
document.showTextAligned(
   "The Strange Case of Dr. Jekyll and Mr. Hyde", 36, 806, TextAlignment.LEFT);
document.showTextAlignedKerned(
   "The Strange Case of Dr. Jekyll and Mr. Hyde", 36, 790,
   TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
document.showTextAligned("AWAY AGAIN", 36, 774, TextAlignment.LEFT);
document.showTextAlignedKerned("AWAY AGAIN", 36, 758,
    TextAlignment.LEFT, VerticalAlignment.BOTTOM, 0);
```
&emsp;&emsp;pdfCalligraph插件是可选的，因为改进的排版需要更多的计算来检查字符组合并查看字体程序是否包含这些组合的字距调整或连字信息。

&emsp;&emsp;在iText 5中，R2L脚本是可以支持的，但是仅限在``ColumnText``和``PdfPcell``的内容中使用。但是你必须明确地改变书写系统。支持连字，但是仅支持阿拉伯语文本。没有任何支持印地语或其他印度语书写系统。使用iText 7的话则更加简单，只要把typograph jar添加到CLASSPATH。一旦iText 7检测到pdfCalligraph插件，如果检测到希伯来语或阿拉伯语，写入系统将自动从左向右（L2R）改为从右到左（R2L）。 当检测到梵文或泰米尔语内容时，将自动进行连字。  
&emsp;&emsp;对于简单的英文文本，所有这些额外的工作可能都是不需要的，在这种情况下，您并不真正需要pdfCalligraph插件。

> 我曾尝试使用字母/支持阿拉伯语，印度语/连字，但它不起作用。 为什么?  
> ``showTextAlignedKerned()``方法在当CLASSPATH中不存在typography jar的时候会没有任何效果。如果typography jar丢失，普通的文本和调整后的文本将会没有任何区别。如果想要渲染印地语或阿拉伯语，文本在没有typography jar的情况下不会正确呈现，除非你将typography jar添加到CLASSPATH，否则也不会进行连字。 
> 目前并非所有的书写系统都受支持。 我们从阿拉伯语，梵文和泰米尔语开始。 根据iText客户的要求，将支持其他书写系统。

&emsp;&emsp;有了pdfCalligraph and typography以后可以开始更多样例，但是我们把这些遗留在别的教程中。本章介绍的是关于``RootElement``对象``Canvas``和``Document``,我们至今为止已经涵盖了相当多的基础内容。

# 总结

&emsp;&emsp;在本章，我们讨论了``Canvas``和``Document``对象，这两者都是抽象类``RootElement``的子类。我们同样介绍了``RootRenderer``类的例子，``CanvasRenderer``和``DocumentRenderer``。在这样做的同时，我们发现我们可以使用``ColumnDocumentRendere``r轻松地在列中呈现内容。 列示例允许我们更多地了解``AreaBreak``对象，它是抽象``AbstractElement``类的子类。

&emsp;&emsp;我们多次调整了``Document``对象的不同属性，简短地讲述了“The Strange Case of Dr. Jekyll and Mr. Hyde”。我们了解到默认情况下内容会尽快刷新到``OutputStream``，但我们可以要求iText推迟元素的呈现，以便我们可以在之后更改其内容或布局。

&emsp;&emsp;最后，我们讨论了iText 7的闭源插件的运行机制。这些插件需要需要从iText软件购买许可证密钥。我们已经尝试了pdfCalligraph插件，也称为typography jar。在下一章中，我们将深入研究``ILeafElement``实现。我们已经多次使用``Text``对象，但在下一章中，我们还将看一下``Link``，``Tab``和``Image``对象。