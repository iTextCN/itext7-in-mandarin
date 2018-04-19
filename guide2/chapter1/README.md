ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-1-introducing-basic-building-blocks](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-1-introducing-basic-building-blocks)

> # **Hello World**

&nbsp;&nbsp;&nbsp;&nbsp;和别的语言的代码库一样，我们又从HelloWorld开始入手，代码如下所示：
```java
PdfWriter writer = new PdfWriter(dest);
PdfDocument pdf = new PdfDocument(writer);
Document document = new Document(pdf);
document.add(new Paragraph("Hello World!"));
document.close();
```
* 创建`PdfWriter`实例，`PdfWriter`是一个可以写PDF文件的对象，它不需要了解它要写的pdf的实际内容是什么，`PdfWriter`不需要知道文档是什么，一旦文件结构完成，它就写不同的文件部分,不同的对象，构成一个有效的文档。`PdfWriter`的初始化参数可以是**文件名**或者**Stream**。
* `PdfWriter`了解它需要写什么内容，因为它监听`PdfDocument`的动态。`PdfWriter`负责管理添加的内容，并把内容分布到不同的页面上，并跟踪有关页面内容的所有信息。在第7张，我们可以发现`PdfWriter`可以有多重监听`PdfDocument`的方式。
* `PdfDocument`和`PdfWriter`创建以后，我们把`PdfDocument`传入`Docment`，并对`Document`对象操作
* 创建`Paragraph`，包含`"Hello World"`字符串，并把这个短语加入`Document`独享中
* 关闭`Document`。PDF文档创建完成  

我们在pdf中创建一个Hello World的字符如下图所示：
![Hello World pdf](http://obkwqzjnq.bkt.clouddn.com/C01F01.png)

---
> # **添加缩进无序段落**

 下面的例子是添加更加复杂的文字的情况
```java
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
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产生的效果如下图所示：
![文字缩进](http://obkwqzjnq.bkt.clouddn.com/C01F02.png)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;从第1行到22行内容是与Hello World这个例子是相同的，不过我们不仅仅增加了一个`Paragraph`。iText默认使用Helvetica字体，如果想使用其他字体，就要通过`PdfFontFactory`的`createFont`方法创获得一个`PdfFont`对象。然后，我们可以使用这个`PdfFont`对象改变`Paragraph`和`List`里面的字体，`List`是一个标题段落(11行)的集合,并且每一列表项都缩进12个字符。我们添加6个`ListItem`对象到`list`，最终写入`document`。

---
> # **添加图片**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;如果我们使用如下代码添加图片和文字：
```
Image fox = new Image(ImageDataFactory.create(fox.bmp));
Image dog = new Image(ImageDataFactory.create(dog.bmp));
Paragraph p = new Paragraph("The quick brown ")
            .add(fox)
            .add(" jumps over the lazy ")
            .add(dog);
document.add(p);
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产生的效果如下图所示：
![图片](http://obkwqzjnq.bkt.clouddn.com/C01F03_0.png)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一开始我们把图片的路径传给`ImageDataFactory`,并会返回一个`Image`对象。`ImageDataFactory`会根据路径自动分析文件的类型(包括jpg,png,gif,bmp等)并进行处理保存在pdf中，在本例中我们插入的图片同时也是`Paragraph`的一本部分，他们替代了单词"fox"和"dog"。

---
> # **展示数据库**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;许多用户都会使用iText来创建pdf展示数据库的查询结果。下图是一个保存美国各大州的信息的数据库的展示：
![数据库](http://obkwqzjnq.bkt.clouddn.com/C01F04_0.png)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;当然，如果我们使用数据库的话会增加代码的复杂度，所以我们使用csv个格式的文件，如下图：
![CSV](http://obkwqzjnq.bkt.clouddn.com/C01F05.png)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;一般pdf的纸张默认是A4型号的，在本例中如果想要展示如上上图所示的效果的话，先要通过`PageSize.A4.rotate()`来使A4纸旋转。在第5含改变边缘大小，从默认的36字符到20字符。代码如下所示：
```java
PdfWriter writer = new PdfWriter(dest);
PdfDocument pdf = new PdfDocument(writer);    
Document document = new Document(pdf, PageSize.A4.rotate());
document.setMargins(20, 20, 20, 20);
PdfFont font = PdfFontFactory.createFont(FontConstants.HELVETICA);
PdfFont bold = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
Table table = new Table(new float[]{4, 1, 3, 4, 3, 3, 3, 3, 1});
table.setWidthPercent(100);
BufferedReader br = new BufferedReader(new FileReader(data.csv));
String line = br.readLine();
process(table, line, bold, true);
while ((line = br.readLine()) != null) {
    process(table, line, font, false);
}
br.close();
document.add(table);
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;在本例中，我们一行一行读取CSV文件，并把CSV文件每行的数据放在`Table`对象中，首先，我们创建两个相同字体家族的字体：Helvetica regular和Helvetica bold。接着，我们传递9个`float`类型的数据来创建`Table`对象，其中每个`float`类型的数据定义了表格列的宽度，例如第一、三列的长度是第二列的2倍（4/1），然后设置表格的相对于纸张的大小。在本例中我们设置表格大小为为纸张大小的100%(100%也会减去边缘大小的)。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;最后，我们根据路径(data.csv)来读取CSV文件，读取第一行并使用`process()`来处理这一行数据，`process()`这个函数是用来在`Table`中添加` line`，这个`line`的样式为`font`字体，并且检查这个`line`是否为header(表头)。其中，我们使用`StringTokenizer`来分割CSV文件的每一行，根据是否为表头内容来添加`Paragraph`到`Cell`对象中。以下是process函数：
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
---
> # **总结**

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;以上的例子都在官网上，通过以上几个例子，我们可以见识到itext的基本功能，通过itext我们可以用程序创建文档。`Paragraph`,`List`,`Image`,`Table`和`Cell`,这些都是基础的高层次对象，在我们创建基础文档的时候有着重要作用。  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;但是，我们有时候也需要低层次语法（高层次用来建立块内容，低层次的方式影响布局和设计），在第二章，我们开始介绍这些低层次方法