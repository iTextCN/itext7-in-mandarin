ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-7-creating-pdfua-and-pdfa-documents](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-7-creating-pdfua-and-pdfa-documents)

# **前言**

&nbsp;&nbsp;&nbsp;&nbsp;在1-4章中，我们使用了iText7来创建PDF文档。在5-6章中，我们操作和重用了现有的PDF文档。在这些章节中我们操作的PDF文档都是在ISO 32000规范下的，是PDF文件的核心标准。ISO 32000并不是PDF的唯一ISO标准，还有很多为了特定原因创建的子标准。在本章中，我们着重关注两个：  

- ISO 14289，也叫做PDF/UA。UA的意思就是通用访问设计(Universal Accessibility)，使用PDF/UA标准的PDF的文档，每个人都可以查看，包括那些有视觉障碍的人甚至瞎子(我天，真有这么神奇吗)
- ISO 19005，也叫做PDF/A。A的意思是归档化(Archiving)。目标是文档数字化的长期存储。


&nbsp;&nbsp;&nbsp;&nbsp;在本章中，我们通过创建一系列的PDF/A和PDF//UA文件会学习PDF/A和PDF/UA相关的知识。

# **创建PDF/UA文档**
&nbsp;&nbsp;&nbsp;&nbsp;在我们开始PDF/UA例子之前，我们来看一下我们要解决的问题。在第1章，我们已经创建了带有图片的文档，在句子"Quick brown fox jumps over the lazy dog"中，我们把"dag"和"fox"替换为相应的图片，当这个文件被读入的时候，一个机器不能知道第一张图片代码一个fox，第二张图片代表dog，因此这个文件会被认为："Quick brown jumps over the lazy"。

> 在一个普通的PDF中，内容会被画入画布(canvas)中。我们可能会使用高级的对象，例如``List``和``Table``,但是一旦PDF被创建，这些对象不会保存。一个``List``是一系列行组成的，但是在list元素中一个文本片段并不知道它是list的一部分。一个``Table``由一群先和特定位置的文本组成，同样的，一个文本片段并不知道它属于特定行和列。

&nbsp;&nbsp;&nbsp;&nbsp;除非我们让一个PDF变成加带标签的PDF，否则这个文档不会包含任何的语义上的结构。当一个文档没有语义结构存储的时候，我们就说这个PDF无法感知/理解(isn't accessible)。为了可感知/理解，这个文档需要能够能够区分一个页面上哪些部分是真实的内容，哪些部分不是真实的内容(例如页眉，页码)，一行文本如果不是``paragraph``的一部分的话，需要知道自己是否是一个``title``,当然还有其他一些要求。我们可以通过一种方式来添加所有的信息到一个页面，这个方式就是创建``结构树(structure)``和把内容定义为``带标签的内容``。这个可能听起来比较复杂，但是如果我们使用iText7的高级对象，我们可以高效的使用``setTagged()``来达到这一目标。  
&nbsp;&nbsp;&nbsp;&nbsp;通过定义``PdfDocument``为带标签的文档，``List``、``Table``和``Paragraph``等带结构的对象被引入后，会反映在带标签的PDF中。  
&nbsp;&nbsp;&nbsp;&nbsp;当然这只为了PDF感知(accessible，是实在不知道翻译成啥比较好，就暂且翻译成感知吧)的其中一个要求，下面的代码可以帮我们理解其他的要求：
```
PdfDocument pdf =  new PdfDocument(new PdfWriter(dest, new WriterProperties().addXmpMetadata()));
Document document = new Document(pdf);
//Setting some required parameters
pdf.setTagged();
pdf.getCatalog().setLang(new PdfString("en-US"));
pdf.getCatalog().setViewerPreferences(
        new PdfViewerPreferences().setDisplayDocTitle(true));
PdfDocumentInfo info = pdf.getDocumentInfo();
info.setTitle("iText7 PDF/UA example");
//Fonts need to be embedded
PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
Paragraph p = new Paragraph();
p.setFont(font);
p.add(new Text("The quick brown "));
Image foxImage = new Image(ImageFactory.getImage(FOX));
//PDF/UA: Set alt text
foxImage.getAccessibilityProperties().setAlternateDescription("Fox");
p.add(foxImage);
p.add(" jumps over the lazy ");
Image dogImage = new Image(ImageFactory.getImage(DOG));
//PDF/UA: Set alt text
dogImage.getAccessibilityProperties().setAlternateDescription("Dog");
p.add(dogImage);
document.add(p);
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;创建一个``PdfDocument``和``Document``,但是这次我们使用``WriterProperties``的``addXmpMetadata()``来自动添加XMP元数据。在PDF/UA中，必须在PDF中以XML格式存储相同的元数据。XML可能不是压缩的。不熟悉PDF内容格式的处理者/处理程序必须能够探测这个XMP元数据并能正确处理它。一个XMP数据流会在Info字典(Info dictionary)条目中自动创建。这个Info字典是一个PDF对象，它包含诸如文档标题之类的数据。除了添加XMP数据流以后，我们还需求进行以下操作来使之符合PDF/UA标准：  

- 把这个``PdfDocument``设置为带标签的(行4)
- 我们添加一个语言说明符。在这个例子中，文件知道在这个文件中使用的主要语言是美国英语(行5)
- 更改查看器首选项，以便文档的标题始终显示在PDF查看器的顶部栏中
(行6-7)。然后我们把标题放入了文档的元数据中(行8-9)
- 所有的字体需要被嵌入(行11)。对于字体其实还有一些其他的要求，但是我们现在讨论还为时过早。
- 所有的内容需要带标签。遇到图片时，我们需要使用替代图片文字提供该图片的描述(行17和行22)

&nbsp;&nbsp;&nbsp;&nbsp;现在我们已经完成了创建PDF/UA的工作。结果如下两图所示，可能与之前的差别并不是很明显，但是如果我们打开Tags版面(一定要用Adobe Acrobat Pro，用Adobe Acrobat Reader DC不行的哟)：  

![itext7-1](http://obkwqzjnq.bkt.clouddn.com/itext7-1.png)

![itext7-2](http://obkwqzjnq.bkt.clouddn.com/itext7-2.png) 


&nbsp;&nbsp;&nbsp;&nbsp;我们可以看到``<Document>``标签里面有``<P>``标签，``<P>``标签由两个``<Span>``和两个``<Figures>``组成。我们会在这章的后面创建更加复杂的PDF/UA文档，现在我们先来看看PDF/A怎么创建。



# **创建PDF/A文档 PDF/A-1**                                 
&nbsp;&nbsp;&nbsp;&nbsp;ISO 19005的Part 1是在2005年发布的。它在Adobe PDF 1.4声明官方中被定义(那时候这份声明并不是ISO标准)。SO 19005-1引入了一系列的义务和限制：  

- 文档的所有资源和信息必须自己存储：所有的字体需要被嵌入;扩展的动画、视频、声音和其他二进制文件是不被允许的。
- 文档必须把元数据保存在XMP(eXensible Metadata Platform)格式中：ISO 16684(XMP)描述了如何把XML格式的元数据保存在一个二进制文件中，以便不知道怎么读取和解释二进制文件的软件仍然可以提取文件的元数据。
- 不允许一些未来(先进的，不在PDF里面声明的或未来添加的)功能：PDF不能包含JavaScript而且也不能被加密

&nbsp;&nbsp;&nbsp;&nbsp;SO 19005-1:2005 (PDF/A-1)定义了两种符合性级别：  

- Level B("basic")：确保长期保存文件的视觉外观。
- Level A("accessible")：不仅确保长期保存文件的视觉外观，而且引入了结构和语义特性，这个PDF需要是带标签的PDF。(注意和PDF/UA比较类似，但是不同，原因后面例子会提及)

&nbsp;&nbsp;&nbsp;&nbsp;下面的代码展示了如何把我们之前创建的"Quick brown fox"的PDF变成符合PDF/A-1b标准：
```
//Initialize PDFA document with output intent
PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
    PdfAConformanceLevel.PDF_A_1B,
    new PdfOutputIntent("Custom", "", "http://www.color.org",
            "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
Document document = new Document(pdf);
//Fonts need to be embedded
PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
Paragraph p = new Paragraph();
p.setFont(font);
p.add(new Text("The quick brown "));
Image foxImage = new Image(ImageFactory.getImage(FOX));
p.add(foxImage);
p.add(" jumps over the lazy ");
Image dogImage = new Image(ImageFactory.getImage(DOG));
p.add(dogImage);
document.add(p);
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们可以看到，我们不再使用``PdfDocument``实例，相反，我们使用的是``PdfADocument``实例。首先我们创建了一个``PdfADocument``实例，``PdfADocument``实例构造函数第一个参数是一个``PdfWriter``，第二个参数是符合性级别(在这里就是``PdfAConformanceLevel.PDF_A_1B``)，第三个参数是一个``PdfOutpuyIntext``，这个输出意图告诉文档如何解读这个文档里面存储的颜色。在第10行，我们确保字体被嵌入。  

&nbsp;&nbsp;&nbsp;&nbsp;产生的PDF的样子如下图：  

![itext7-3](http://obkwqzjnq.bkt.clouddn.com/itext7-3.png)  


&nbsp;&nbsp;&nbsp;&nbsp;由上图我们可以看见一个带有"这个文件符合PDF/A标准规范，且已在只读模式下打开以防被修改"的小蓝条。对此我们从两个方法来解读这句话：  

- 这句话并不意味着这个PDF实际上是符合PDF/A标准的，它只是声明它有可能是，为了确认是否符合标准，我们需要在Adobe Acrobat中打开"标准"面板，然后点击"验证符合性``链接，Acrobat会验证这个文档是否和它声明的一样，在这个例子中，结果是“验证成功";这样，我们会最终创建PDF/A-1B标准的文档。
-  文档已经以只读方式打开，并不是因为不允许修改（PDF/A不能保护PDF不被修改），而是Adobe Acrobat以只读的方式显示，因为任何修改都可能会改变PDF转换为不再符合PDF/A标准的PDF。在不破坏PDF/A状态的情况下更新PDF/A是被允许的。  

&nbsp;&nbsp;&nbsp;&nbsp;然后我们来看看怎么创建PDF/A-1a，代码如下：
```
//Initialize PDFA document with output intent
PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
    PdfAConformanceLevel.PDF_A_1A,
    new PdfOutputIntent("Custom", "", "http://www.color.org",
            "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
Document document = new Document(pdf);
//Setting some required parameters
pdf.setTagged();
//Fonts need to be embedded
PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, true);
Paragraph p = new Paragraph();
p.setFont(font);
p.add(new Text("The quick brown "));
Image foxImage = new Image(ImageFactory.getImage(FOX));
//Set alt text
foxImage.getAccessibilityProperties().setAlternateDescription("Fox");
p.add(foxImage);
p.add(" jumps over the lazy ");
Image dogImage = new Image(ImageFactory.getImage(DOG));
//Set alt text
dogImage.getAccessibilityProperties().setAlternateDescription("Dog");
p.add(dogImage);
document.add(p);
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;让我们来解读代码，在第3行中，我们把``PdfConformanceLevel.PDF_A1B``变为了``PdfConformanceLevel.PDF_A1A``。在第8行中，把这个``PdfADocument``变成了带标签的PDF，然后加入了图片的文字描述信息，最后结果如下图所示：  

![itext7-4](http://obkwqzjnq.bkt.clouddn.com/itext7-4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们打开标准面板，可以看出Adobe Acrobat Pro把这个文件认为是PDF/A-1A和PDF/UA-1，但是这次并没有验证符合性链接，所以我需要借助于印前检查工具(英文版的是Preflight，晕，中文版的找了半天才找到，我在这里就分享给大家吧，估计大家都是用的中文，具体步骤为：**打开工具里面的PDF标准→印前检查(或者直接左边点击打开印前检查)→找到PDF/A规范下面的PDF/A-1b规范→分析**)，如下图：  

![itext7-8](http://obkwqzjnq.bkt.clouddn.com/itext7-8.png)


&nbsp;&nbsp;&nbsp;&nbsp;我们继续看英文版的那张图，可以看出来结果是没有发现任何错误。我们无法验证PDF/UA符合性，因为PDF/UA涉及一些无法通过本地计算机验证的要求。例如：如果我们将狐狸形象的描述与狗的形象描述交换，机器就不会注意到。这将使文件无法访问，因为文件会根据屏幕阅读器向人们传播虚假信息。无论如何，只需知道我们创建文档不符合PDF/UA标准，因为我们省略了一些基本要素（如语言，第一个例子里面就同时设置了语言）。  

&nbsp;&nbsp;&nbsp;&nbsp;从一开始就确定ISO 19005的认可部分永远不会失效。新的，后续的部分只会定义新的有用的功能。这些后续定义的就是我们即将介绍的PDF/A-2和PDF/A-3。

# **创建PDF/A文档 PDF/A-2和PDF/A-3**

&nbsp;&nbsp;&nbsp;&nbsp;**ISO 19005-2:2011**(PDF/A-2)是根据ISO标准（而不是Adobe的PDF官方文档）被添加到PDF/A标准中的。PDF/A-2在PDF1.5，1.6.1.7中很多特性和提升：  

- 有用的添加功能有：JPEG2000的支持，容器，对象级XMP和可选内容
- 有用的提升改进有：对透明、类型注释、注释和数字签名有更好的支持。

&nbsp;&nbsp;&nbsp;&nbsp;PDF/A-2在符合性方面，除了原有的Level A和Level B以外，还定义了额外的level:  

- Level U("Unicode")：确保文档的视觉外贸能长久保存，并且所有的文本的存储格式为UNICODE

&nbsp;&nbsp;&nbsp;&nbsp;**ISO 19005-3**:2012 (PDF/A-3)几乎与PDF/A-2一毛一样。唯一的区别就是：在PDF/A-3中，附件不需要一定是PDF/A格式的。你可以把任何格式的文件当前是PFA/A-3的附件，例如可以把一个excel格式的文件当作是这个文档用到的结果，一个word格式文件用来创建一个PDF文档，等等。文档本身需要符合PDF/A规范的所有义务和限制，但这些义务和限制不适用于其附件。  

&nbsp;&nbsp;&nbsp;&nbsp;在下面的例子中，我们会创建同时符合PDF/UA和PDF/A-3A标准，我们之所以会选择PDF/A-3，是因为要用到CSV文件来创建PDF，代码如下：
```
 PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
    PdfAConformanceLevel.PDF_A_3A,
    new PdfOutputIntent("Custom", "", "http://www.color.org",
            "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
Document document = new Document(pdf, PageSize.A4.rotate());
//Setting some required parameters
pdf.setTagged();
pdf.getCatalog().setLang(new PdfString("en-US"));
pdf.getCatalog().setViewerPreferences(
        new PdfViewerPreferences().setDisplayDocTitle(true));
PdfDocumentInfo info = pdf.getDocumentInfo();
info.setTitle("iText7 PDF/A-3 example");
//Add attachment
PdfDictionary parameters = new PdfDictionary();
parameters.put(PdfName.ModDate, new PdfDate().getPdfObject());
PdfFileSpec fileSpec = PdfFileSpec.createEmbeddedFileSpec(
    pdf, Files.readAllBytes(Paths.get(DATA)), "united_states.csv",
    "united_states.csv", new PdfName("text/csv"), parameters,
    PdfName.Data, false);
fileSpec.put(new PdfName("AFRelationship"), new PdfName("Data"));
pdf.addFileAttachment("united_states.csv", fileSpec);
PdfArray array = new PdfArray();
array.add(fileSpec.getPdfObject().getIndirectReference());
pdf.getCatalog().put(new PdfName("AF"), array);
//Embed fonts
PdfFont font = PdfFontFactory.createFont(FONT, true);
PdfFont bold = PdfFontFactory.createFont(BOLD_FONT, true);
// Create content
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
//Close document
document.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;让我们逐行解释代码：  
 
 - 行1-5：我们创建了``PdfADocument``(类型为``PdfAConformanceLevel.PDF_A_3A)``)和``Document``
 - 行7：让PDF变成带标签的PDF——PDF/UA和PDF/A-3A标准。
 - 行8-12：设置语言，文档标题和查看器首选项——PDF/UA标准。
 - 行14-20：使用特定的参数来添加一个附件——PDF/A-3A标准。
 - 行26-27：嵌入图片和字体——PDF/UA和PDF/A-3A标准。
 - 行28-38：提取内容和我们之前第1章的代码一样的。
 - 行30：关闭文档，保存内容
 

 &nbsp;&nbsp;&nbsp;&nbsp;如下图，我们可以看到我们用``Table``和``Cell``对象添加到文档里在标签面板里面，被保存了Table数据结构了，有点像HTML：  
 
 ![itext7-5](http://obkwqzjnq.bkt.clouddn.com/itext7-5.png)  
 
 &nbsp;&nbsp;&nbsp;&nbsp;同时，我们打开附件面板，我们可以看见CSV源文件，并且可以轻松提取出来，如下图：  
  
 ![itext7-5](http://obkwqzjnq.bkt.clouddn.com/itext7-5.png)  
 
&nbsp;&nbsp;&nbsp;&nbsp;通过上述的例子，与一般的PDF文件相比，我们创建符合PDF/UA或者PDF/A文档的时候需要添加另外的信息，*"我们是否能用iText改把现有的普通的PDF文档转换成符合PDF/UA或者PDF/A标准的文档呢？"*是在论坛和咨询里面问得最多的问题。我们希望通过这一章让大家明白iText是不能自动转换的，原因如下：  
 
 - 如果和之前一样有一个文档有一张fox和一张dog图片，iText不能自动给图片添加缺失的替换描述信息，因为iText不能准备识别这些图片的含义(说白了就是没有机器学习、人工智能模块，不能识别内容) 
 - 如果字体没有被嵌入，而且并没有提供相应的字体程序的的话，iText并不会知道字体长成什么样，也不能把字体嵌入到文档中。

&nbsp;&nbsp;&nbsp;&nbsp;当然这只是不能自动转换的两个小原因。让一个PDF展示小蓝条说这个文档貌似符合PDF/A标准是很容易，但是并不是所有的声明都是正确的。  
&nbsp;&nbsp;&nbsp;&nbsp;最后，我们来看看PDF/A文档的拼接。
 
# **拼接PDF/A文档** 
 
&nbsp;&nbsp;&nbsp;&nbsp;当拼接PDF/A文件的时候，最值得我们注意的是，我们拼接的各个文档必须都是PDF/A文件，不能一个是PDF/A文件，一个是普通文件，而且PDF/A的Level也要一样，不能一个是A，一个是B，因为一个有结构树，一个没有，拼接在一起会导致结果错误。  

&nbsp;&nbsp;&nbsp;&nbsp;我们把之前两个PDF/A A级的文档拼接起来，生成的文件如下图所示：  

![itext7-5](http://obkwqzjnq.bkt.clouddn.com/itext7-5.png)  

&nbsp;&nbsp;&nbsp;&nbsp;通过标签面板我们看到一个``<P>``，紧接着是``<Table>``，如下代码展示了如何创建这个文档：  
```
PdfADocument pdf = new PdfADocument(new PdfWriter(dest),
    PdfAConformanceLevel.PDF_A_1A,
    new PdfOutputIntent("Custom", "", "http://www.color.org",
            "sRGB IEC61966-2.1", new FileInputStream(INTENT)));
//Setting some required parameters
pdf.setTagged();
pdf.getCatalog().setLang(new PdfString("en-US"));
pdf.getCatalog().setViewerPreferences(
        new PdfViewerPreferences().setDisplayDocTitle(true));
PdfDocumentInfo info = pdf.getDocumentInfo();
info.setTitle("iText7 PDF/A-1a example");
//Create PdfMerger instance
PdfMerger merger = new PdfMerger(pdf);
//Add pages from the first document
PdfDocument firstSourcePdf = new PdfDocument(new PdfReader(SRC1));
merger.addPages(firstSourcePdf, 1, firstSourcePdf.getNumberOfPages());
//Add pages from the second pdf document
PdfDocument secondSourcePdf = new PdfDocument(new PdfReader(SRC2));
merger.addPages(secondSourcePdf, 1, secondSourcePdf.getNumberOfPages());
//Merge
merger.merge();
//Close the documents
firstSourcePdf.close();
secondSourcePdf.
```
&nbsp;&nbsp;&nbsp;&nbsp;整体上，这段代码可以说和之前的例子很像:  

- 行1-11就不说了，和之前的代码没什么区别。
- 行12-25的话，在上一章奥斯卡奖项拼接的例子中有提及，创建``PdfMerger``的时候，我们传入的是``PdfADocument``对象，之后往这个``PdfMerger``对象添加的是``PdfDocument``类型的，如果是``PdfADocument``类型的话，会检查文档的合法性。

&nbsp;&nbsp;&nbsp;&nbsp;关于PDF/IA和PDF/A标准还有很多讨论，当然还有其他子标准，例如在PDF/A-3中有一个德语发音的ZUGFeRD的标准，会在别的系列里面讲述(这个是官方文档里面说的，个人的话看需求喽，如果有时间我就开这个坑)  

# **总结**
 
 &nbsp;&nbsp;&nbsp;&nbsp;在本章，我们探讨了符合其他PDF标准的文档的创建和拼接，学会了创建PDF/UA和PDF/A的文档，本系列也就在此结束了，当然我们还需要一些其他的系列来深入的了解iText7。
 