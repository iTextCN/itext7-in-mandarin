ready to translate : [https://developers.itextpdf.com/content/itext-7-building-blocks/chapter-1](https://developers.itextpdf.com/content/itext-7-building-blocks/chapter-1)

&nbsp;&nbsp;&nbsp;&nbsp;本章我们开始讲述一些使用不同字体展示标题和作者的例子，在这里会引入一些类，例如``FontProgram``和``PdfFont``。  

> 本章内容偏长，请耐心观看，中文字体相关，请参考的我的[博客](https://blog.csdn.net/u012397189/article/details/78471319)和[个人网站](http://www.cuteke.cn/u/CuteKe/blogs/22)

> 官网上的例子的编码、字体、操作系统可能与我们本机的环境不一样，所以结果可能不一样，请以实际为准

# 1.创建一个PdfFont对象  

&nbsp;&nbsp;&nbsp;&nbsp;如下图所示，我们可以看见使用了三种不同的字体来创建带有标题和作者的PDF文档，三种字体是：Helvetica、Times-Bold和Times-Roman，在Adobe Acrobat Pro阅读器中，这些字体替换为：ArialMT、TimesNewRomanPS-BoldMT和TimesNewRomanPSMT。  

![itext7-h-1-1](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;在实际字体中会包含MT，MT是字体供应商the Monotype Imaging Holdings, IncD的简称，这些实际字体是随Windows一起打包带走的，如果你在Linux机器上打开相同的文件，那么实际字体会用其他字体。这种情况尤其会在**不使用嵌入字体**的时候回发生，阅读器会在操作系统中搜索展示文档所必须的字体。如果可以找到特定的替代品，那么就会使用这种字体。  

> 传统意义上，每个阅读器应该识这14种字体：四种Helvetica字体(normal,bold,oblique和bold-oblique，也就是普通，加粗，斜体和加粗斜体),四种Times-Roman字体(normal,bold,italic和bold-italic,italic也是斜体，和oblique的区别就是：italic是斜体字，对于没有斜体的字体应该使用oblique属性来实现倾斜的文字效果)，四种Courier字体(normal,bold,oblique和bold-oblique)，Symbol符号以及Zapfdingbats(这个暂时不知道怎么翻译，先放着，估计是专门的术语吧)。这14种字体也是标准的Type 1字体。每个阅读器不能使用与声明字体一样的字体，但是会使用和声明字体看起来完全一样的字体  

&nbsp;&nbsp;&nbsp;&nbsp;为了创建上图的PDF，我们需要使用三种字体：其中两种字体显式声明，一种字体隐式声明，代码如下：  
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
Document document = new Document(pdf);
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
PdfFont bold = PdfFontFactory.createFont(FontConstants.TIMES_BOLD);
Text title =
    new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
Text author = new Text("Robert Louis Stevenson").setFont(font);
Paragraph p = new Paragraph().add(title).add(" by ").add(author);
document.add(p);
document.close();
```  
&nbsp;&nbsp;&nbsp;&nbsp;在行1，我们使用了``PdfWriter``创建了``PdfDocument``。j这些对象都是**低级对象**，会根据你的内容来创建PDF。在行2，我们创建了一个``Document``实例，这是一个**高级对象**，允许你无需担心PDF的语法来创建一个文档。  

&nbsp;&nbsp;&nbsp;&nbsp;行5和行6，我们使用``PdfFontFactory``来创建一个``PdfFont``，在FontConstants对象中，你可以找到14种标准Type 1字体。在行7，我们创建了一个``Text``，内容是故事标题，并且设置字体为``TIME_BOLD``。在行8中，创建了一个``Text``，内容是作者名称，并且设置字体为``TIMES_ROMAN``。我们不能直接添加这些``Text``对象到``document``中，但是我们可以把他们添加到一个``BlockElement``，在这里是行9的``Paragraph``。  

> 在标题和作者之间，我们添加了一个``String``对象。既然我们没有为这个``String``定义一种字体，``Paragraph``的默认字体将会被使用。在iText中，默认的字体是Helvetica。这也是我们在上图李看到字体列表有Helvetica字体了  

&nbsp;&nbsp;&nbsp;&nbsp;在行10，我们添加``Paragraph``到``document``对象中；在行11中我们关闭了这个``Document``。  

&nbsp;&nbsp;&nbsp;&nbsp;我们已经完成了不使用嵌入字体来创建PDF，结果就是只有几种字体在渲染文件的时候能使用，不过我们可以使用嵌入的字体来使用更多的字体。  

# 2.创建字体程序  

&nbsp;&nbsp;&nbsp;&nbsp;iText支持标准的Type 1字体，因为io-jar里面已经包含了14种字体的Adobe Font Metrics(AFM)文件。这些文件包含了必要的度量(metrics)，这些度量用来计算单词和线的宽度和高度，这在创建文件的布局的时候是必须的。  

&nbsp;&nbsp;&nbsp;&nbsp;如果我们想要创建一个字体，我们需要一个字体程序。如果是标准Type 1字体，这些字体程序是存储在PostScript Font Binary(PFB)文件中。再进一步如果是14中标准Type 1字体，这些PFB是有版权的，他们不会在iText7中包含，因为iText7没有许可证，iText中只能包含那些度量文件(metrics files)。  

&nbsp;&nbsp;&nbsp;&nbsp;因为版权的原因，iText不能嵌入这些14中字体，但是并不意味着iText不能嵌入字体。在下面这个例子中，我们在PDF文件中嵌入了三种Cardo字体家族的字体的子集，Cardo字体程序是在Summer Institute of Logistics (SIL) Open Font License (OFL)旗下发布的。  

&nbsp;&nbsp;&nbsp;&nbsp;结果如下图所示：  

![itext7-h-1-2](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;首先，我们需要指明这三个字体的路径，``Cardo-Regular.ttf``、``Cardo-Bold.ttf``和``Cardo-Italic.ttf``,如下所示：  
```
public static final String REGULAR =
    "src/main/resources/fonts/Cardo-Regular.ttf";
public static final String BOLD =
    "src/main/resources/fonts/Cardo-Bold.ttf";
public static final String ITALIC =
    "src/main/resources/fonts/Cardo-Italic.ttf";
```  
&nbsp;&nbsp;&nbsp;&nbsp;紧接着，我们从``FontProgramFactory``来获得一个``FontProgram``对象：  
```
FontProgram fontProgram =
    FontProgramFactory.createFont(REGULAR);
```  
&nbsp;&nbsp;&nbsp;&nbsp;利用这个``FontProgram``实例，我们可以创建一个``PdfFont``对象：  
```
PdfFont font = PdfFontFactory.createFont(
    fontProgram, PdfEncodings.WINANSI, true);
```  
&nbsp;&nbsp;&nbsp;&nbsp;这里我们传递了一个编码(``PdfEncodings.WINANSI``)，并且我们表明了字体需要被嵌入(``true``)。当然，我们可以直接传递给``PdfFontFactory``字体路径来创建字体，如下所示：  
```
PdfFont bold = PdfFontFactory.createFont(BOLD, true);
PdfFont italic = PdfFontFactory.createFont(ITALIC, true);
```  
&nbsp;&nbsp;&nbsp;&nbsp;现在我们可以使用者三种字体来填充我们的``Paragraph``对象：  
```
Text title =
    new Text("The Strange Case of Dr. Jekyll and Mr. Hyde").setFont(bold);
Text author = new Text("Robert Louis Stevenson").setFont(font);
Paragraph p = new Paragraph().setFont(italic)
    .add(title).add(" by ").add(author);
document.add(p);
```  
&nbsp;&nbsp;&nbsp;&nbsp;Helvetica字体在上图没有出现因为我们改变了``Paragraph``的默认字体。  

# 3.FontProgram和PdfFont的区别  

&nbsp;&nbsp;&nbsp;&nbsp;在接下来的例子中，我们将会使用一直使用``PdfFontFactory``来创建``PdfFont``对象。``PdfFontFactory``会内部使用``FontProgram``实例，不过我们要清楚``PdfFont``和``FontProgram``之间的一个重要的区别：  

- 一个``FontProgram``对象可以针对不同PDF文档来创建不同的``PdfFont``对象
- 一个``PdfFont``对象只能被用于一个``PdfDocument``

&nbsp;&nbsp;&nbsp;&nbsp;你只能使用一次``PdfFont``对象，因它会跟踪计算在这个文档中所需要的所有字形(glyphs)。通过这种方式，整个字体程序不需要都加入PDF文件，只需要加入字体的子集就行了。这样做得好处就是能减少PDF文件的大小。  
&nbsp;&nbsp;&nbsp;&nbsp;我们来看一下代码：  
```
protected PdfFont font;
protected PdfFont bold;
protected PdfFont italic;
public static void main(String args[]) throws IOException {
    File file = new File(DEST);
    file.getParentFile().mkdirs();
    C01E02_Text_Paragraph_Cardo2 app =
        new C01E02_Text_Paragraph_Cardo2();
    FontProgram fontProgram =
        FontProgramFactory.createFont(REGULAR);
    FontProgram boldProgram =
        FontProgramFactory.createFont(BOLD);
    FontProgram italicProgram =
        FontProgramFactory.createFont(ITALIC);
    for (int i = 0; i < 3; ) {
        app.font = PdfFontFactory.createFont(
            fontProgram, PdfEncodings.WINANSI, true);
        app.bold = PdfFontFactory.createFont(
            boldProgram, PdfEncodings.WINANSI, true);
        app.italic = PdfFontFactory.createFont(
            italicProgram, PdfEncodings.WINANSI, true);
        app.createPdf(String.format(DEST, ++i));
    }
}
public void createPdf(String dest) throws IOException {
    PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
    Document document = new Document(pdf);
    Text title =
        new Text("The Strange Case of Dr. Jekyll and Mr. Hyde")
            .setFont(bold);
    Text author = new Text("Robert Louis Stevenson")
        .setFont(font);
    Paragraph p = new Paragraph()
        .setFont(italic).add(title).add(" by ").add(author);
    document.add(p);
    document.close();
}
```  
&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们创建了``FontProgram``实例：``fontProgram``，``boldProgram``和``italicProgram``。我们三次重用这些实例来创建三个PDF文档、对于每个PDF文档，我们常见新的``PdfFont``实例。  

&nbsp;&nbsp;&nbsp;&nbsp;下面的代码就是错的，因为我们尝试重用``PdfFont``实例来创建不同的PDF文档：  
```
public static void main(String args[]) throws IOException {
    File file = new File(DEST);
    file.getParentFile().mkdirs();
    C01E02_Text_Paragraph_Cardo2 app =
        new C01E02_Text_Paragraph_Cardo2();
    app.font = PdfFontFactory.createFont(REGULAR, true);
    app.bold = PdfFontFactory.createFont(BOLD, true);
    app.italic = PdfFontFactory.createFont(ITALIC, true);
    for (int i = 0; i < 3; ) {
        app.createPdf(String.format(DEST, ++i));
    }
}
```  
&nbsp;&nbsp;&nbsp;&nbsp;如果你尝试这个代码，会抛出下面的错误：  
> com.itextpdf.kernel.PdfException: Pdf indirect object belongs to other PDF document. Copy object to current pdf document.  

&nbsp;&nbsp;&nbsp;&nbsp;这个异常会在第二次调用``createPdf()``的时候抛出。因为我们在尝试用第一次调用``createPdf()``的``PdfFont``实例。  

# 4.嵌入字体的重要性  

&nbsp;&nbsp;&nbsp;&nbsp;如果你尝试使用不同语言来创建PDF，那么PDF的显示效果会更差。如下图，我们尝试用捷克语/俄语/韩文添加一些文字，捷克语的文字看起来还行。

&nbsp;&nbsp;&nbsp;&nbsp;下图展示了如果我们不嵌入字体的情况：  

![itext7-h-1-3](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-3.png)  

&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们定义了三种字体：  
```
PdfFont font = PdfFontFactory.createFont(REGULAR);
PdfFont bold = PdfFontFactory.createFont(BOLD);
PdfFont italic = PdfFontFactory.createFont(ITALIC);
```  
&nbsp;&nbsp;&nbsp;&nbsp;其中``REGULAR``，``BOLD``和``ITALIC``常量都指向正确的Cardo字体的ttf文件，但是这里没有让iText来嵌入字体。顺带一提，在阅读PDF的机子上没有安装Cardo字体。Adobe Reder使用Adobe Sans MM字体来替换他们。如我们可以看见，这些结果看起来并不很好看。如果你不使用任何标准的Type 1字体，则应始终嵌入字体。  

&nbsp;&nbsp;&nbsp;&nbsp;如果你尝试使用不同语言来创建PDF，那么PDF的显示效果会更差。如下图，我们尝试用捷克语/俄语/韩文添加一些文字，捷克语的文字看起来还行，但是还是有一个字符丢失了（不懂捷克语，看不出来2333）。至于俄语和韩文则完全没有显示出来。  

![itext7-h-1-4](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;当然嵌入字体是必须的，还有我们应该主要要定义正确的编码。  

# 5.选择正确的编码  

&nbsp;&nbsp;&nbsp;&nbsp;在上图，正确的要渲染的字体如下：  

> Podivný případ Dr. Jekylla a pana Hyda by Robert Louis Stevenson  
> Странная история доктора Джекила и мистера Хайда by Robert Louis Stevenson  
> 하이드, 지킬, 나 by Robert Louis Stevenson  

&nbsp;&nbsp;&nbsp;&nbsp;第一行是"The Strange Case of Dr.Jekyll and Mr.Hyde."的捷克语翻译，如果你看的更仔细一点，可以看出字母ř消失了，因为ř没有Winansi编码中，Winansi编码，在西式操作系统里面默认的代码页是1253(CP-1252，我们中文的默认代码页就不是1253了，我们一般是936，GBK)，Windows 1252，是ISO 8859-1d的子集，也叫做Latin-1。  

&nbsp;&nbsp;&nbsp;&nbsp;对于捷克语，我们需要用另外一种编码。一种选择是使用1250代码页，一种使用拉丁脚本的用来表示中欧和东欧的编码。第二行是S*trannaya istoriya doktora Dzhekila i mistera Khayda*。对于本文，我们可以使用代码页1251，这是一种用于涵盖使用西里尔脚本的语言的编码。Cp1250和Cp1251都是8位字符编码。第三行是*Hyde, Jekyll, Me*，这是一部基于Jekyll和海德故事松散的韩国电视剧。为了显示韩文，我们不能使用8为编码。为了呈现这个文本，我们需要使用Unicode。 Unicode是一种计算行业标准，用于对大多数世界写作系统中表达的文本进行一致的编码，表示和处理。  

>  当你使用8位编码创建字体时，iText将为PDF创建一个简单的字体。一个简单的字体由至多256个字符组成，映射到至多256个字形(glyphs)。当你使用Unicode创建字体（PDF概念：**横向书写系统的Identity-H或垂直书写系统的Identity-V**）时，iText将创建一个复合字体。复合字体可以包含65,536个字符。这少于Unicode中可用代码点的总数（1,114,112）。这意味着没有一个字体可以包含所有可能的语言中的所有可能的字符。  

&nbsp;&nbsp;&nbsp;&nbsp;除了Cp1250和Cp1251，我们可以使用Unicode来表示捷克问和俄文。实际上，当我们在源代码中存储硬编码文本时，最好存储Unicode值。如下所示：  
```
public static final String CZECH =
        "Podivn\u00fd p\u0159\u00edpad Dr. Jekylla a pana Hyda";
public static final String RUSSIAN =
        "\u0421\u0442\u0440\u0430\u043d\u043d\u0430\u044f "
        + "\u0438\u0441\u0442\u043e\u0440\u0438\u044f "
        + "\u0434\u043e\u043a\u0442\u043e\u0440\u0430 "
        + "\u0414\u0436\u0435\u043a\u0438\u043b\u0430 \u0438 "
        + "\u043c\u0438\u0441\u0442\u0435\u0440\u0430 "
        + "\u0425\u0430\u0439\u0434\u0430";
public static final String KOREAN =
        "\ud558\uc774\ub4dc, \uc9c0\ud0ac, \ub098";
```  
&nbsp;&nbsp;&nbsp;&nbsp;接下来的例子中我们也会使用``CZECH``，``RUSSIAN``和``KOREAN``。  

> ### 为什么我们应该使用Unicode表示特别的字符？  
> &nbsp;&nbsp;&nbsp;&nbsp;当源代码文件存储在磁盘上，提交给版本控制系统或以任何方式传输时，总会有编码丢失的风险。如果一个Unicode文件以纯文本形式存储，则两个字节的字符会变成两个单字节字符。例如，Unicode值为``\ ud0ac``的字符``킬``会变成ASCII码为``d0``和``ac``的两个字符。当发生这种情况时，音节``킬``（发音为“kil”）变成``Ð¬``，文字变得难以辨认。在上面的代码片段中使用Unicode表示法是一种很好的做法;这将帮助你避免使用源代码编码问题。  

&nbsp;&nbsp;&nbsp;&nbsp;使用正确的编码并不能有效地解决你遇到的所有的字体问题，例如如下代码：  
```
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
document.add(new Paragraph().setFont(font)
        .add(CZECH).add(" by Robert Louis Stevenson"));
document.add(new Paragraph().setFont(font)
        .add(RUSSIAN).add(" by Robert Louis Stevenson"));
document.add(new Paragraph().setFont(font)
        .add(KOREAN).add(" by Robert Louis Stevenson"));
```  
&nbsp;&nbsp;&nbsp;&nbsp;这代码所生成的Pdf会字体显式不正确，不仅因为没有使用正确的编码，而且我们没有定义了一种支持俄文和韩文的字体。为此，我们为捷克语和俄文嵌入``FreeSans``字体，为韩文使用``HCR Batang``字体，我们先用Cp1250和Cp1251来表示捷克问和俄文。  
```
public static final String FONT = "src/main/resources/fonts/FreeSans.ttf";
public static final String HCRBATANG = "src/main/resources/fonts/HANBatang.ttf";
.......
PdfFont font1250 = PdfFontFactory.createFont(FONT, PdfEncodings.CP1250, true);
document.add(new Paragraph().setFont(font1250)
        .add(CZECH).add(" by Robert Louis Stevenson"));
PdfFont font1251 = PdfFontFactory.createFont(FONT, "Cp1251", true);
document.add(new Paragraph().setFont(font1251)
        .add(RUSSIAN).add(" by Robert Louis Stevenson"));
PdfFont fontUnicode =
    PdfFontFactory.createFont(HCRBATANG, PdfEncodings.IDENTITY_H, true);
document.add(new Paragraph().setFont(fontUnicode)
        .add(KOREAN).add(" by Robert Louis Stevenson"));
```   
&nbsp;&nbsp;&nbsp;&nbsp;最终显示的结果如下图：  

![itext7-h-1-5](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-5.png)  

&nbsp;&nbsp;&nbsp;&nbsp;当我们查看文档的字体属性的时候，我们可以看见使用了``FreeSans``字体两次。这是正确的：我们第一次使用Cp1250编码添加了字体，第二次使用了Cp1251编码。我们试试捷克文和俄文都是用``freeUnicode``来表示，也就是``FreeSanS``字体，如下代码所示：  
```
PdfFont freeUnicode =
    PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H, true);
document.add(new Paragraph().setFont(freeUnicode)
    .add(CZECH).add(" by Robert Louis Stevenson"));
document.add(new Paragraph().setFont(freeUnicode)
    .add(RUSSIAN).add(" by Robert Louis Stevenson"));
PdfFont fontUnicode =
    PdfFontFactory.createFont(HCRBATANG, PdfEncodings.IDENTITY_H, true);
document.add(new Paragraph().setFont(fontUnicode)
    .add(KOREAN).add(" by Robert Louis Stevenson"));
```  
&nbsp;&nbsp;&nbsp;&nbsp;下图展示了结果，页面展示可能和上图一样，但是字体属性里面捷文和俄文都是使用Identity-H编码的``FreeSans``字体。  

![itext7-h-1-6](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-6.png)  

&nbsp;&nbsp;&nbsp;&nbsp;出于可访问性(我在第七章里面翻译为感知性，accessible)的原因，使用Unicode是PDF/UA和某些PDF/A标准的要求之一。使用自定义编码，并不总是可以知道每个字符代表哪些字形。  
&nbsp;&nbsp;&nbsp;&nbsp;在接下来的例子中，我们会尝试改变字体的属性，例如字体大小，字体颜色和渲染模式。  

# 6.字体属性  

&nbsp;&nbsp;&nbsp;&nbsp;下图是一个PDF的截图，这个PDF使用了默认字体Helvetica，但是我们定义了不同的字体大小。  

![itext7-h-1-7](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-7.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们可以使用``setFontSize()``来设置字体大小。这个方法是定义在抽象类``ElementPropertyContainer``中的，这意味着我们在不同的对象中使用它。在下面的代码中，``Text``和``Paragraph``中都使用这个方法。  
```
Text title1 = new Text("The Strange Case of ").setFontSize(12);
Text title2 = new Text("Dr. Jekyll and Mr. Hyde").setFontSize(16);
Text author = new Text("Robert Louis Stevenson");
Paragraph p = new Paragraph().setFontSize(8)
        .add(title1).add(title2).add(" by ").add(author);
document.add(p);
```  
&nbsp;&nbsp;&nbsp;&nbsp;我们把新创建的``Paragaph``的字体大小设置为8pt。这个字体会被所有加入这个``Paragraph``的对象继承，除了对象重写了默认的字体大小，例如我们添加的``title1``和``title2``改变了字体大小，而我们添加的``"by"``则会继承字体大小，为8pt。  

> 在iText5中，当我们想要一个字体有不同的大小和颜色，我们必须要创建不同的``Font``对象。在Text7中则不需要，只要创建一个``PdfFont``对象即可。字体的大小和颜色是在基础块里面的定义的，同时字体，字体大小和其他属性也可以从父对象中继承  

&nbsp;&nbsp;&nbsp;&nbsp;在前面的例子中，我们使用了同一系列的不同字体。例如，我们创建了一个包含Cardo家族三种不同字体的文档：Cardo-Regular，Cardo-Bold和Cardo-Italic。对于大多数西方字体，您至少可以找到常规字体，粗体字体，斜体字体和粗斜体字体。为东方语言和闪语语言找到粗体，斜体和粗斜体字体将会更加困难。在这种情况下，你可以使用下图的方式。如果仔细观察，会发现使用了不同的样式，但我们只在PDF中定义了单个字体。  

![itext7-h-1-8](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-8.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下：  
```
Text title1 = new Text("The Strange Case of ").setItalic();
Text title2 = new Text("Dr. Jekyll and Mr. Hyde").setBold();
Text author = new Text("Robert Louis Stevenson").setItalic().setBold();
Paragraph p = new Paragraph()
        .add(title1).add(title2).add(" by ").add(author);
document.add(p);
```  
&nbsp;&nbsp;&nbsp;&nbsp;行1-3，我们使用了``setItalic()``和``setBold()``方法。``setItalic()``方法不会重新选择一个斜体的字体，它会倾斜字体的字形(glyphs)让它看起来是斜体的。``setBold()``字体则会改变字体的渲染模式并且增加画笔的宽度。接下来，我们来改变文本的颜色和渲染模式：  

![itext7-h-1-9](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-9.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下：  
```
Text title1 = new Text("The Strange Case of ").setFontColor(Color.BLUE);
Text title2 = new Text("Dr. Jekyll")
        .setStrokeColor(Color.GREEN)
        .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.FILL_STROKE);
Text title3 = new Text(" and ");
Text title4 = new Text("Mr. Hyde")
        .setStrokeColor(Color.RED).setStrokeWidth(0.5f)
        .setTextRenderingMode(PdfCanvasConstants.TextRenderingMode.STROKE);
Paragraph p = new Paragraph().setFontSize(24)
        .add(title1).add(title2).add(title3).add(title4);
document.add(p);
```  
&nbsp;&nbsp;&nbsp;&nbsp;字体程序包含构造每个字形路径的语法。默认情况下，路径使用填充操作符绘制，而不是使用笔划操作绘制，但我们可以更改此默认值。  

- 行1：我们使用``setFontColor()``方法来设置字体颜色为蓝色。这将改变绘画路径的画笔的*填充颜色*
- 行2-3：我们不定义一个字体颜色，这意味文本会用黑色绘制。取而代之我们使用``setStrokeColor()``方法来定义绘画颜色，并且我们使用``setTextRenderingMode()``方法改变渲染模式为``FILL_STROKE``。结果就是每个字形的轮廓为绿色，在轮廓里面，我们可以看见默认的填充颜色——黑色。
- 行5：一切都使用默认的值。``Text``对象会简单地继承``Paragraph``的字体大小。
- -行6-8：改变填充颜色为红色并且使用``setStrokeWidth()``方法来设置画笔宽度为0.5用户单位。默认的画笔宽度为1用户单位，1英寸有72个用户单位。然后我们改变了文本的渲染模式为``STROKE``，着意味着文字不会用默认颜色画笔填充，所以结果我们只能看见轮廓。  

&nbsp;&nbsp;&nbsp;&nbsp;模仿粗体字形是通过将文本渲染模式为``FILL_STROKE``并增加画笔宽度来实现的，模仿斜体是通过使用将在第三章中讨论的``setSkew()``方法来完成的。虽然这种方式能显示的很好，但是使用``setBold()``和``setItalic()``不是很好的选择，只有我们找不到相应样式的文字时，才会使用这种方式。这种方式带来的弊端就是，在PDF中提取文本时不能发现文本的渲染方式。  

# 7.重用样式

&nbsp;&nbsp;&nbsp;&nbsp;如果你要构造很多不同的基础块，为不同的对象一次一次定义相同的样式是很笨重的。例如。下图中部分文本-故事的标题使用13pt大小的TimeRoman字体。但是其他部分-主要任务的名字使用的12pt大小的Courier书写，字体颜色为红色，浅灰色的背景。  

![itext7-h-1-10](http://obkwqzjnq.bkt.clouddn.com/itext-h-1-10.png)  

&nbsp;&nbsp;&nbsp;&nbsp;下面的例子是使用``Style``对象来一次性定义不同的样式：  
```
Style normal = new Style();
PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
normal.setFont(font).setFontSize(14);
Style code = new Style();
PdfFont monospace = PdfFontFactory.createFont(FontConstants.COURIER);
code.setFont(monospace).setFontColor(Color.RED)
        .setBackgroundColor(Color.LIGHT_GRAY);
Paragraph p = new Paragraph();
p.add(new Text("The Strange Case of ").addStyle(normal));
p.add(new Text("Dr. Jekyll").addStyle(code));
p.add(new Text(" and ").addStyle(normal));
p.add(new Text("Mr. Hyde").addStyle(code));
p.add(new Text(".").addStyle(normal));
document.add(p);
```  
&nbsp;&nbsp;&nbsp;&nbsp;在行1-3，我们定义了一个``normal``样式;在行4-7，我们定义了``code``样式-Courier字体经常在表示代码块的时候使用。在行8-13，我向一个``Paragraph``添加了不同的``Text``对象。我们设置这些``Text``对象为``normal``或者``code``。  

&nbsp;&nbsp;&nbsp;&nbsp;``Style``对象是``ElementPropertyContainer``类是的子类，``ElementProperttContainer``是所有构建基础块的基类，它包含一系列的属性的setters和getters，例如字体，颜色，边界，尺寸和位置。你可以在每个``AbstractElement``子类上使用``addStyle()``方法来一次性设置这些属性。  

> 在一个类里面联合多个属性是iText7里面的新特性，相比iText5而言可以少写很多代码  

&nbsp;&nbsp;&nbsp;&nbsp;在``Style``类里面不止可以设置字体，你甚至可以设置``BlockElement``d的内边距和外边距，``BlockElement``将会在第4和第5章讨论。 

# 8.总结  

&nbsp;&nbsp;&nbsp;&nbsp;在这个章节，我们介绍了``PdfFont``类并且讨论了字体程序，嵌入字体和使用不同编码。我们使用了英文，捷克文，俄文和韩文展示了同一个标题。然后我们设置了字体的属性，例如字体大小，字体颜色和渲染模式。最后我们模仿了粗体和斜体样式。  

&nbsp;&nbsp;&nbsp;&nbsp;当然关于字体能说还有很多，我们会在接下来的教程里面提到。下一章我们会全面讨论如果创建一个PDF，讨论``RootElement``的实现类``Document``和``Canvas``。