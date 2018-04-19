ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-5-manipulating-existing-pdf-document](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-5-manipulating-existing-pdf-document)

# **前言**

&nbsp;&nbsp;&nbsp;&nbsp;在之前的第1到第3章，我们总是从头开始用iText创建一个新的PDF文档。在第4章的最后几个例子中，我们使用了一个现有的PDF文档，利用现有的PDF来读取表单并填写了自己的表单或者预填充定义的表单。在本章，我们会使用``PdfReader``读取一个存在的PDF文件，或者使用``PdfWriter``对象来创建一个新的``PdfDocument``。

# **添加注释和内容**
&nbsp;&nbsp;&nbsp;&nbsp;在前面的章节中，我们读取了一个带有表单的PDF文档，然后填充了里面的内容。在这个章节中，我们会进行深入的操作。我们首先添加一个文档注释，一些文本和一个新的复选框，如下图：   

![itext5-1](http://obkwqzjnq.bkt.clouddn.com/itext5-1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;接下来就是我们在第四章里面添加注释和内容里面的代码一样了：
```
PdfDocument pdfDoc =
    new PdfDocument(new PdfReader(src), new PdfWriter(dest));
// add content
pdfDoc.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们在//add content部分添加相应的内容，首先，我们通过``PdfDocument``实例来向一个页面里面添加注释：
```
PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(400, 795, 0, 0))
    .setTitle(new PdfString("iText"))
    .setContents("Please, fill out the form.")
    .setOpen(true);
pdfDoc.getFirstPage().addAnnotation(ann);
```
&nbsp;&nbsp;&nbsp;&nbsp;如果我们想添加内容到内容流里面，我们需要创建一个``PdfCancas``对象。我们可以传入一个``PdfPage``对象传入``PdfCanvas``的构造函数：
```
PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
canvas.beginText().setFontAndSize(
        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
        .moveText(265, 597)
        .showText("I agree to the terms and conditions.")
        .endText();
```
&nbsp;&nbsp;&nbsp;&nbsp;添加文本的代码和我们在第2章写的代码差不多。无论您是从头开始创建文档还是将内容添加到现有文档，都可以使用我们写的代码。当然，向``PdfAcroForm``实例添加字段也是跟之前的例子一样的：
```
PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
PdfButtonFormField checkField = PdfFormField.createCheckBox(
        pdfDoc, new Rectangle(245, 594, 15, 15),
        "agreement", "Off", PdfFormField.TYPE_CHECK);
checkField.setRequired(true);
form.addField(checkField);
```
&nbsp;&nbsp;&nbsp;&nbsp;最后，既然我们已经添加了一个多余的字段，我们想要改变reset的动作：
```
form.getField("reset").setAction(PdfAction.createResetForm(
    new String[]{"name", "language", "experience1", "experience2",
    "experience3", "shift", "info", "agreement"}, 0))
```

# **改变表单字段的属性**
&nbsp;&nbsp;&nbsp;&nbsp;之前的第四章的例子中，我们是填充了表单的内容。在这里，我们可以尝试改变字段的属性，添加或者删除。
```
PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
Map<String, PdfFormField> fields = form.getFormFields();
fields.get("name").setValue("James Bond").setBackgroundColor(Color.ORANGE);
fields.get("language").setValue("English");
fields.get("experience1").setValue("Yes");
fields.get("experience2").setValue("Yes");
fields.get("experience3").setValue("Yes");
List<PdfString> options = new ArrayList<PdfString>();
options.add(new PdfString("Any"));
options.add(new PdfString("8.30 am - 12.30 pm"));
options.add(new PdfString("12.30 pm - 4.30 pm"));
options.add(new PdfString("4.30 pm - 8.30 pm"));
options.add(new PdfString("8.30 pm - 12.30 am"));
options.add(new PdfString("12.30 am - 4.30 am"));
options.add(new PdfString("4.30 am - 8.30 am"));
PdfArray arr = new PdfArray(options);
fields.get("shift").setOptions(arr);
fields.get("shift").setValue("Any");
PdfFont courier = PdfFontFactory.createFont(FontConstants.COURIER);
fields.get("info")
    .setValue("I was 38 years old when I became a 007 agent.", courier, 7);
```
&nbsp;&nbsp;&nbsp;&nbsp;在这里我们关注下面的几行：  
- 行3：我们设置``name``字段的值为``James Bond``,同时我们设置这个字段的背景色为``Color.ORANGE``
- 行9-17：创建了一个Java ``List``来添加更多的选项(行8-15),然后把这个``List``转换成``PdfArray``(行16),最后使用这个array来更新``shift``字段
- 行19-21：我们创建了一个新的``PdfFont``，然后使用这个新的字体和新的字体大小作为``info``字段的设置值方法的参数。

&nbsp;&nbsp;&nbsp;&nbsp;让我们看看加入这些代码以后是否有所改变，如图：  

![itext5-2](http://obkwqzjnq.bkt.clouddn.com/itext5-2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们可以看到``shift``字段有了更多的选项，但是我们没有看见``name``字段的背景，我们是不能清楚``info``字段是否改变。是否发生错误了呢？其实是没有错误的，所有字段当前是被突出显示的而且蓝色的突出色覆盖了背景颜色，我们可以点击``Highlight Existing Fields``来看看还会发生什么，如图：  

![itext5-3](http://obkwqzjnq.bkt.clouddn.com/itext5-3.png)  

&nbsp;&nbsp;&nbsp;&nbsp;一切跟我们期望的一样。但是我们没有遇到这样一种情况：在关闭``PdfDocument``之前，我们加入了``form.flattenFields();``语句，这时候表单就锁定，我们就没有了表单，针对这种情况，我们会在下一章探讨。现在，我们来看看我们是不是还能对没有表单的pdf文档进行一些其他的操作。


# **添加页眉、页脚和水印**
&nbsp;&nbsp;&nbsp;&nbsp;我们回忆在一下第三章里面的UFO信息的PDF，我们会创建一个相似的ufo.pdf文件：  

![itext5-4](http://obkwqzjnq.bkt.clouddn.com/itext5-4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;可以看出我们创建的ufo.pdf并没有第三章创建的那样炫酷，如果我们往里面添加一个页眉和一个页脚（显示第几页/总页数）还有水印，该如何做呢，如下图：  

![itext5-5](http://obkwqzjnq.bkt.clouddn.com/itext5-5.png) 

&nbsp;&nbsp;&nbsp;&nbsp;在上图中，我们不像第三章那样在添加页脚的时候不知道总的页数，现在我们知道了总的页数，可以添加"1" of "4"，"2" of "4"等

> 从头创建文档时，可以为总页数创建一个占位符（placeholder）。一旦创建了所有的页面，我们就可以将该页面的总数添加到该占位符，但这不在本介绍性教程的范围之内。

&nbsp;&nbsp;&nbsp;&nbsp;如下代码展示了如何在已知的文档的每一页中添加内容：
```
PdfDocument pdfDoc =
    new PdfDocument(new PdfReader(src), new PdfWriter(dest));
Document document = new Document(pdfDoc);
Rectangle pageSize;
PdfCanvas canvas;
int n = pdfDoc.getNumberOfPages();
for (int i = 1; i <= n; i++) {
    PdfPage page = pdfDoc.getPage(i);
    pageSize = page.getPageSize();
    canvas = new PdfCanvas(page);
    // add new content
}
pdfDoc.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们使用``pdfDoc``对象来创建一个``Document``实例。我们将要使用这个``document``对象来添加内容。我们同时也会使用``pdfDoc``对象来找到原始PDF的总页数。我们遍历所有所有的页数，获得每一页的``PdfPage``对象。以下代码是``//add new content``所做得东西：
```
//Draw header text
canvas.beginText().setFontAndSize(
        PdfFontFactory.createFont(FontConstants.HELVETICA), 7)
        .moveText(pageSize.getWidth() / 2 - 24, pageSize.getHeight() - 10)
        .showText("I want to believe")
        .endText();
//Draw footer line
canvas.setStrokeColor(Color.BLACK)
        .setLineWidth(.2f)
        .moveTo(pageSize.getWidth() / 2 - 30, 20)
        .lineTo(pageSize.getWidth() / 2 + 30, 20).stroke();
//Draw page number
canvas.beginText().setFontAndSize(
        PdfFontFactory.createFont(FontConstants.HELVETICA), 7)
        .moveText(pageSize.getWidth() / 2 - 7, 10)
        .showText(String.valueOf(i))
        .showText(" of ")
        .showText(String.valueOf(n))
        .endText();
//Draw watermark
Paragraph p = new Paragraph("CONFIDENTIAL").setFontSize(60);
canvas.saveState();
PdfExtGState gs1 = new PdfExtGState().setFillOpacity(0.2f);
canvas.setExtGState(gs1);
document.showTextAligned(p,
        pageSize.getWidth() / 2, pageSize.getHeight() / 2,
        pdfDoc.getPageNumber(page),
        TextAlignment.CENTER, VerticalAlignment.MIDDLE, 45);
canvas.restoreState();
```
&nbsp;&nbsp;&nbsp;&nbsp;总共的步骤分为四部：  
- 页眉(行2-6)：我们使用第二章谈及的低等级的文本api来在每一页的最上方添加``"I want to believe"``
- 页脚线(行8-11)：我们使用低等级的画图api来在每一页的底部画线
- 包含页码的页脚(行13-19)：我们使用低等级的文本api在每一页底部添加当前页号，``"of"``,以及总的页数。
- 水印(行21-28)：我们创建一个带有文本的``Paragrah``来当做水印。然后我们改变画布的透明性。最后我们添加这个``Paragrah``到文档中，使用``showTextAligned()``方法使水印定位在每一页的中间，并45度倾斜。

&nbsp;&nbsp;&nbsp;&nbsp;在这里，我们添加水印的时候做了一些特殊的工作。我们改变了``canvas``对象的图像状态，然后我们在``document``的每一页中添加了文本，在内部，iText会发现我们已经使用了当前页的``PafCanvas``实例，同时``showTextAligned``会想同一个``canvas``写入内容，这种方式，我们可以使用低级api和一些简单方法(如高级api)的结合(可以和第三章的[添加水印](http://blog.csdn.net/u012397189/article/details/77540464#t3)相比较，确实是方便了不少)。在本章最后的例子中，我们会改变页面的大小和upf.pdf的每一页的方向。

# **改变页面大小和方向**
&nbsp;&nbsp;&nbsp;&nbsp;新的pdf文档效果如下：  

![itext5-6](http://obkwqzjnq.bkt.clouddn.com/itext5-6.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们可以发现每一页面变得更大了，而且第二页的颠倒了，向下了。让我们来看看代码：
```
PdfDocument pdfDoc =
    new PdfDocument(new PdfReader(src), new PdfWriter(dest));
float margin = 72;
for (int i = 1; i <= pdfDoc.getNumberOfPages(); i++) {
    PdfPage page = pdfDoc.getPage(i);
    // change page size
    Rectangle mediaBox = page.getMediaBox();
    Rectangle newMediaBox = new Rectangle(
            mediaBox.getLeft() - margin, mediaBox.getBottom() - margin,
            mediaBox.getWidth() + margin * 2, mediaBox.getHeight() + margin * 2);
    page.setMediaBox(newMediaBox);
    // add border
    PdfCanvas over = new PdfCanvas(page);
    over.setStrokeColor(Color.GRAY);
    over.rectangle(mediaBox.getLeft(), mediaBox.getBottom(),
            mediaBox.getWidth(), mediaBox.getHeight());
    over.stroke();
    // change rotation of the even pages
    if (i % 2 == 0) {
        page.setRotation(180);
    }
}
pdfDoc.close();
```
&nbsp;&nbsp;&nbsp;&nbsp;可以发现，这里我们不需要``Document``实例，我们只需要``PdfDocument``实例就够啦。我们遍历所有的页面(行4)和获取每一页的``PdfPage``实例(行5)：  
-  一个页面可以有不同的边界，其中``/MediaBox``基本上就是页面大小了(详情了可以百度PDF MediaBox)，然后我们获取一个``Rectangle``作为原页面的边界(行7)，根据这个边界来创建一英尺更大的边``Rectangle``来当做新的页面边界大小，最后我们使用``setMediaBox()``来改变页面大小
-  我们创了页面的``PdfCanvas``对象，然后使用灰色画笔画出了``mediaBox``的边界(行14-17)
-  偶数页，我们把页面旋转180度(行19)

&nbsp;&nbsp;&nbsp;&nbsp;改变已知存在的PDF文档需要关于PDF的很多知识，比如``/MediaBox``的概念。我们为了使举的例子看起来简单，我们会规避一些其他的细节，例如在上面一个例子中，我们没有检查文档中有``CropBox``对象，如果有这个对象的话，使``/MediaBox``变大不会带来任何视觉上的变换。所以将会花上更多篇章来深入讨论这些问题。

# **总结**
&nbsp;&nbsp;&nbsp;&nbsp;在之前的篇章中，我们学习了交互式的PDF表单。在本章中，我们继续操作这些表单：添加注释，一些文本和额外的字段，我们在填充表单的同时还改变了字段的一些属性。  
&nbsp;&nbsp;&nbsp;&nbsp;然后我们讨论了那些没有交互的PDF文档，首先，我们添加了页眉、页脚和水印，然后我们改变了已知文档的页面大小和方向。  
&nbsp;&nbsp;&nbsp;&nbsp;在下一个章节中，我们会缩放和平铺已知文档，然后我们会学会如何将多个文档组合成一个PDF。