ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-1-introducing-basic-building-blocks](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-1-introducing-basic-building-blocks)

## 章节1：介绍基本的构建块

假若时光倒流到2000年，那个时候为了解决两个非常明确的问题，我创建了iText:

1. 在90年代，大多数的PDF文档都是Adobe Illustrator或者Acrobat Distiller这样的桌面应用程序来手动创建的。当时，我需要在一个供互联网用户访问的web应用程序中批量处理PDF文件，以一种无人值守的模式，或者一个批处理模式。这些PDF文件不能通过用户手动操作来生成，因为它们的内容一般都比较庞大，且是不可预知的（需要通过基于用户的输入和数据库查询到的结算计算得出）。在1998年，我写的第一个PDF库解决了这个问题。随后我将这个库部署在一台web服务器上，并且一个部署在这台服务器的Java应用程序通过调用这个库生成了数千份PDF文档。

2. 很快我就碰到了第二个问题：开发人员在没有了解清楚PDF格式规范的情况下，是没有办法使用我第一个PDF库的。事实证明，我是唯一一个在我的团队中能够理解我代码的人。这也意味着如果出了问题，我是唯一一个能够修复代码的人。在一个软件项目中，这可不是一个好的情况。我通过从头到尾重写了一遍那个PDF库来解决这个问题，确保开发人员没有必要一定要了解PDF的格式规范。我是通过引入文档的概念来实现的，所谓文档，就是一个允许开发者添加段落、列表、图片、块和其他高级对象的一个类。通过结合这些直观的构建，一个开发者可以很容易地通过编程的方式来创建一个PDF文档。并且创建PDF文档的代码也变得更加容易阅读和理解，尤其是对于那些没有写过这类代码的人来说。

这只是一个iText7的启动教程，所以我们不会讲太多的细节，但我们可以先从一些包含了这些基本构建块的例子开始讲起。

### 引入iText的基本构建块

很多的编程教程都是以一个Hello World案例来开始，这篇教程也不例外。

以下就是iText7的一个Hello World例子：

```
PdfWriter writer = new PdfWriter(dest);
PdfDocument pdf = new PdfDocument(writer);
Document document = new Document(pdf);
document.add(new Paragraph("Hello World!"));
document.close();
```

接下来我们一行一行分析这个例子：

1. 首先我们实例化了一个PdfWriter的对象writer。writer这个对象可以编写PDF文件，但它不一定很清楚它所创建PDF文档的内容是什么。当文件的结构被确定的时候，writer将编写不同的文件部分和不同的对象，来构成一个合法的文档文件。在这种情况下，我们通过传递一个包含了文件路径，名为dest的字符串参数，例如：results/chapter01/hello_world.pdf。构造函数也会接受一个输出流OutPutStream作为参数。例如：如果我们想写一个web应用程序，我们可以创建一个ServletOutPutStream，如果我们想在内存中创建一个PDF文档，我们可以使用一个ByteArrayOutputStream等等。

2. 一个PdfWriter通过监听PdfDocument来获取应该编写的内容。PdfDocument将会管理那些被添加的，分配在不同页面中的内容，并且去关联跟这些内容相关的信息。在第七章中，我们将会发现一个PdfWriter可以监听一系列不同的PdfDocument类。

3. 一旦我们创建了一个PdfWriter和一个PdfDocument，我们就完成了所有具有PDF特征的低层次代码。在第三行代码中，我们创建了一个以PdfDocument为参数的文档对象，有了它之后，我们就可以不用去管创建PDF什么的了。

4. 第四行代码中，我们创建了一个包含“Hello World”这段文本信息的Paragraph对象，并且将这个对象添加到了文档对象中。

5. 第五行代码我们关闭了文档对象，然后我们的PDF就已经创建好了。

图1.1展示了最终的结果：

![Figure 1.1: Hello World example](https://developers.itextpdf.com/sites/default/files/C01F01.png)
<p align="center">图1.1: Hello World案例</p>

接下来让我们添加一些复杂的内容，比如改改字体，组织一些列表之类的文本，参考图1.2。

![Figure 1.2: List example](https://developers.itextpdf.com/sites/default/files/C01F02.png)
<p align="center">图1.2: List案例</p>

这个例子[RickaStley](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-1#1724-c01e02_rickastley.java)写明了是如何做到的：

```
PdfWriter writer = new PdfWriter(dest);
PdfDocument pdf = new PdfDocument(writer);
Document document = new Document(pdf);
// Create a PdfFont
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
// Add a Paragraph
document.add(new Paragraph("iText is:").setFont(font));
// Create a List
List list = new List()
    .setSymbolIndent(12)
    .setListSymbol("\u2022")
    .setFont(font);
// Add ListItem objects
list.add(new ListItem("Never gonna give you up"))
    .add(new ListItem("Never gonna let you down"))
    .add(new ListItem("Never gonna run around and desert you"))
    .add(new ListItem("Never gonna make you cry"))
    .add(new ListItem("Never gonna say goodbye"))
    .add(new ListItem("Never gonna tell a lie and hurt you"));
// Add the list
document.add(list);
document.close();
```

1到第3行，以及第22行的代码跟Hello World的示例是相同的，但现在我们已经不是仅仅添加一个段落了。iText通常会使用Helvetica作为默认的文本字体。如果你想换一个字体的话，那么你需要先创建一个PdfFont的实例。你可以通过PdfFontFactory这个类来获得字体（第5行）。我们使用这个字体对象来改变一个段落（第7行）和一个列表（第9行）的字体。这是一个无序列表（第11行），列表项的列表缩进用了12个用户单位（第10行）。然后我们添加了6个列对象（第14－19行）到文档列表中。

这难道不是一件很有意思的事情吗？接下来，让我们介绍一些图片。图1.3介绍了如何添加一张狐狸和一张狗的图像到一个段落中。

![图1.3:Image案例](https://developers.itextpdf.com/sites/default/files/C01F03_0.png)
<p align="center">图1.3:Image案例</p>

如果我们从[QuickBrownFox](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-1#1725-c01e03_quickbrownfox.java)的案例中删除样板式代码，剩下的代码为：

```
Image fox = new Image(ImageDataFactory.create(FOX));
Image dog = new Image(ImageDataFactory.create(DOG));
Paragraph p = new Paragraph("The quick brown ")
            .add(fox)
            .add(" jumps over the lazy ")
            .add(dog);
document.add(p);
```
我们传递了一个图片的路径到ImageDataFactory类中，然后它会返回一个可以用于创建iText图像的对象。ImageDataFactory的任务就是检测并处理一系列类型的图像(jpg、png、git、bmp等等)，确保它可以用在一个PDF中。在这种情况下，我们添加的图像都是段落的一部分。上面的例子中，两个图像分别对应了“fox”和“dog”。

### 导出数据库

许多开发人员使用iText来发布数据库查询结果到一个PDF文档中。假设我们有一个数据库，其中包含美利坚合众国所有的州，我们想创建一个PDF列出这些州及其相关信息到一个表格中，例如图1.4所示。

![Figure 1.4: Table example](https://developers.itextpdf.com/sites/default/files/C01F04_0.png)
<p align="center">图1.4: Table案例</p>

使用一个真正的数据库可能会增加这个简单案例的复杂性，所以我们仅仅用了一个CSV文件: [united_states.csv](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/src/main/resources/data/united_states.csv)。（请看图1.5）

![Figure 1.5: United States CSV file](https://developers.itextpdf.com/sites/default/files/C01F05.png)
<p align="center">图1.5: 美国各州CSV文件</p>

如果你仔细看这个[UnitedStates](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-1#1726-c01e04_unitedstates.java)案例的样板代码，你会发现我们在创建一个文档的时候，做了一点小小的改动（第4行）。我们添加了一个用于定义文档页面大小的额外参数。默认的页面大小是一张A4纸大小，并且印刷的时候也是用的它。所以在这个例子中，我们也在使用A4，但是我们旋转了一下页面（PageSize.A4.rotate()）来让它看起来像图1.4所示的那样。我们也在第五行中改变了一下页边距。默认的情况下，iText使用36个用户单位作为页边距（半英寸）。我们把所有的页边距都改为20个用户单位（这将在下文中作详细的解释）。

```
PdfWriter writer = new PdfWriter(dest);
PdfDocument pdf = new PdfDocument(writer);    
Document document = new Document(pdf, PageSize.A4.rotate());
document.setMargins(20, 20, 20, 20);
PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
Table table = new Table(new float[]{4, 1, 3, 4, 3, 3, 3, 3, 1});
table.setWidthPercent(100);
BufferedReader br = new BufferedReader(new FileReader(DATA));
String line = br.readLine();
process(table, line, bold, true);
while ((line = br.readLine()) != null) {
    process(table, line, font, false);
}
br.close();
document.add(table);
document.close();
```
在这个案例中，我们逐行读取CSV文件数据，并把所有的数据放入一个表格对象中。

我们首先创建了两个同类的PdfFont对象：Helvetica regular(第5行)和Helvetica bold(第6行)。我们创建了一个包含9列的表格对象来定义一个有着9个元素的浮动数组（第7行）。每个浮动定义了一个列的相对宽度。第1列是第2列的4倍宽。第3列是第2列的3倍宽等等。我们也定义了表的宽度是相对于页面的宽度而言的（第8行）。在当前情况下，表将100%使用页面的宽度，最小化页边距。

然后我们从数据常数里取出CSV文件路径并开始读取它的文件内容（第9行）。我们读的第一行将作为列的标题（第10和11行）被处理，我们写了一个process()方法，它使用一个特殊的字体，把每一行无论是否为标题行都添加到表格中来。

```
public void process(Table table, String line, PdfFont font, boolean isHeader) {
    StringTokenizer tokenizer = new StringTokenizer(line, ";");
    while (tokenizer.hasMoreTokens()) {
        if (isHeader) {
            table.addHeaderCell(
                new Cell().add(
                    new Paragraph(tokenizer.nextToken()).setFont(font)));
        } else {
            table.addCell(
                new Cell().add(
                    new Paragraph(tokenizer.nextToken()).setFont(font)));
        }
    }
}
```

我们使用StringTokenizer这个类来循环存储在CSV文件的每一行中的所有字段。并且用指定的字体创建了一个段落。再将该段落添加到新的Cell对象中。并根据是否为标题行，来决定是将这个Cell作为标题单元或者普通单元添加到表中。

在处理好标题行（第11行）之后，代码继续循环其余的行（第12行），并处理剩余的行数（第13行）。如图1.4所示，表格不适合做为单个页面出现。但也没有必要担心这一点：iText会根据需要创建更多新的页面，直到表格完整呈现。iText也将重复标题行，因为我们是通过addHeaderCell()而不是addCell()来添加单元格的。

当我们读完数据之后（第14行），我们将把该表添加到文档对象中（第16行）并关闭它（第17行）。至此，我们就已经成功的导出CSV文件的数据到一个PDF中。

而且这也不难，用少量的几行代码，我们就在一个PDF中成功创建了一个相当不错的表格。

### 总结

通过这几个列子，我们已经看到了iText强大的功能。我们也发现，用编程的方式来创建文档是一件非常简单的事情。在第一章中，我们讨论了的高级对象，如段落、列表、图片、表格和单元格，都是一些iText中最基本的构建块。

然而，很多时候也需要使用一些较为低级的语法来创建PDF。iText通过它的底层API实现了这一点。我们将在第二章中来看一看这些底层方法的例子。
