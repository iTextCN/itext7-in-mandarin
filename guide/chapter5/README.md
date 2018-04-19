ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-5-manipulating-existing-pdf-document](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-5-manipulating-existing-pdf-document)

## 第5章：操纵一个现有的PDF文档

第1章到第3章的例子中，我们总是从头开始用iText创建一个新的PDF文档。第四章的最后几个例子中用的是一个现有的PDF文档，通过对这个PDF表格进行数据的填写，让它不再具有交互性或者带了一些预设值。接下来的第五章里，我们将继续使用现有的PDF。首先通过PdfReader加载一个现有的文件，然后用reader对象来创建一个新的PdfDocument。

### 添加注解及内容

在前面的章节中，我们使用了一个现有的PDF表单job_application.pdf，并填写了其中的相关字段。在这一章中，我们会更进一步。我们将开始添加一个文本注解，一些文本和一个新的复选框。如图5.1所示。

![Figure 5.1: an updated form](https://developers.itextpdf.com/sites/default/files/C05F01.png)
<p align="center">图5.1:可修改的表单</p>

我们将重复之前AddAnnotationsAndContent例子中的代码。

```
PdfDocument pdfDoc =
    new PdfDocument(new PdfReader(src), new PdfWriter(dest));
// add content
pdfDoc.close();
```

在上面的代码中，有一段"add content"的注释，我们将从这里开始添加注解，包括额外的文本和复选框。

就像第4章中的例子一样，我们将注解添加到从PdfDocument实例中获取的页面：

```
PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(400, 795, 0, 0))
    .setTitle(new PdfString("iText"))
    .setContents("Please, fill out the form.")
    .setOpen(true);
pdfDoc.getFirstPage().addAnnotation(ann);
```

如果我们想要把内容添加到内容流中，则需要创建一个PdfCanvas对象，之后可以使用PdfPage对象作为PdfCanvas构造函数的参数：

```
PdfCanvas canvas = new PdfCanvas(pdfDoc.getFirstPage());
canvas.beginText().setFontAndSize(
        PdfFontFactory.createFont(FontConstants.HELVETICA), 12)
        .moveText(265, 597)
        .showText("I agree to the terms and conditions.")
        .endText();
```

添加文本的代码与我们在第2章中所做的相似。无论您是从头开始创建文档，还是将内容添加到现有文档，都不会影响我们使用的说明。将字段添加到PdfAcroForm实例也是如此：

```
PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
PdfButtonFormField checkField = PdfFormField.createCheckBox(
        pdfDoc, new Rectangle(245, 594, 15, 15),
        "agreement", "Off", PdfFormField.TYPE_CHECK);
checkField.setRequired(true);
form.addField(checkField);
```

现在我们已经添加了一个额外的字段，接下来我们要更改reset字段的操作：

```
form.getField("reset").setAction(PdfAction.createResetForm(
    new String[]{"name", "language", "experience1", "experience2",
    "experience3", "shift", "info", "agreement"}, 0));
```

让我们看看我们是否也可以改变表单字段的一些视觉效果。

### 修改表单字段属性

在FillAndModifyForm例子中，我们返回到第4章中的FillForm示例，不是仅仅填写表单，还要更改字段的属性：

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

请仔细看下面几行：

* 第3行：将“name”字段的值设置为“James Bond”，同时将背景颜色更改为Color.ORANGE。

* 第8-17行：创建一个Java List，其中包含比最初表单更多的选项（第8-15行）。我们将这个List转换为一个PdfArray（第16行），使用这个数组来更新“shift”字段的选项（第17行）。

* 第19-21行：创建一个新的PdfFont，当设置“info”字段的值时，我们使用这个字体和一个新的字体大小作为额外的参数。

来看看图5.2，看看我们的变化是否被应用。

![Figure 5.2: updated form with highlighted fields](https://developers.itextpdf.com/sites/default/files/C05F02.png)
<p align="center">图5.2:用高亮字段修改表单</p>

我们看到“shift”字段现在有更多的选项，但是没有看到“name”字段的背景颜色。目前还不清楚“info”字段的字体是否已经改变。但这又怎么了？没有什么是错的，这些字段当前是高亮显示的，蓝色高亮显示的是背景颜色。让我们点击“突出显示的字段”，看看会发生什么。

![Figure 5.3: updated form, no highlighting](https://developers.itextpdf.com/sites/default/files/C05F03.png
<p align="center">图5.3:不用高亮字段修改表单</p>

现在图5.3看起来和我们预期的一样。如果我们添加了form.flattenFields()，我们就不会有这个问题了。因为在这种情况下，我们在关闭PdfDocument之前将不再有一个表单。下一章中我们会来做一些更多的表单示例，但现在让我们看看可以对现有不包含表单的文档做些什么。

### 添加页眉、页脚及水印

你还记得我们在第三章中创造的20世纪UFO目击报告吗？我们将在下面的几个例子中使用类似的报告：ufo.pdf，见图5.4。

![Figure 5.4: UFO sightings report](https://developers.itextpdf.com/sites/default/files/C05F04.png)
<p align="center">图5.4:UFO目击报告</p>

正如你所看到的那样，它不像我们在第三章中所做的报告那么华丽。但如果我们想在这个现有的报告中添加一个标题、一个水印和一个页脚，说明“Y的第X页”怎么办？图5.5就显示了这样一个报告的样子。

![Figure 5.5: UFO sightings report with header, footer, and watermark](https://developers.itextpdf.com/sites/default/files/C05F05_0.png)
<p align="center">图5.5:有页眉、页脚及水印的UFO目击报告</p>

在图5.5中，我们放大了第3章中添加页码时没有的优势。第3章中，在添加页脚时我们不知道总页数，因此我们只添加了当前的页码。现在我们用的是现有的文档，我们可以添加“4页中的第1页”，“4页中的第2页”，等等。

> 从头创建文档时，可以为总页数创建一个占位符。一旦创建了所有的页面，我们就可以将该页面的总数添加到该占位符，但这不在本介绍性教程的范围之内。

AddContent示例显示了如何将内容添加到现有文档中的每个页面。

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

我们使用pdfDoc对象来创建一个Document实例。我们将给该文档对象添加一些内容。另外还使用了pdfDoc对象来查找原始PDF中的页数。通过遍历所有的页面，获得每个页面的PdfPage对象。接下来看看之前省略的 "// add new content"这一部分。

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

我们正在添加四个部分的内容：

1. 标题（第2-6行）：我们使用低级文本功能在页面顶部添加“我想相信”。

2. 页脚行（第8-11行）：我们使用低级图形功能在页面底部画一条线。

3. 带页码（13-19）的页脚：我们使用低级文本功能添加页码，中间用“of”分开，然后是页面底部的总页数。

4. 一个水印（lin 21-28）：我们用我们想要添加的文本作为水印创建一个段落。然后我们改变画布的不透明度。最后，我们使用showTextAligned()方法将段落添加到文档，集中在页面中间，角度为45度。

在添加水印时，我们做了一些特殊的事情：我们正在改变从页面获得的画布对象的图形状态，然后将文本添加到文档中的相应页面。在内部，iText会检测到我们已经在使用该页面的PdfCanvas实例，而showTextAligned()方法将写入同一个画布。这样，我们就可以混合使用低级和便捷的方法。

在本章的最后一个例子中，我们将更改UFO目击报告页面的页面大小和方向。

### 修改页面大小和方向

如果我们看看图5.6，我们会看到它很像图5.4的原始报告，但页面更大，同时第二页被翻转了。

![Figure 5.6: changed page size and orientation](https://developers.itextpdf.com/sites/default/files/C05F06.png)
<p align="center">图5.6:修改页面大小和方向</p>

ChangePage示例显示了这是如何完成的。

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

这里不需要一个Document实例，我们只需要PdfDocument实例。通过遍历所有页面（第4行）并获取每个页面的PdfPage实例（第5行）。

* 一个页面可以有不同的页面边界，其中一个不是可选的：/MediaBox。我们将这个页面边界的值作为矩形（第7行），并且在每一边创建一个新的矩形（第8-10行）。我们使用setMediaBox()方法来改变页面大小。

* 我们为这个页面创建了一个PdfCanvas对象(第13行)，我们将使用原始mediaBox的尺寸（第14-17行）来描一行灰色的边。

* 对于每个偶数页（第19行），我们将页面旋转设置为180度。

操作现有的PDF文档需要一些PDF的知识。例如：你需要知道/MediaBox的概念。我们尽量保持例子简单，但这也意味着我们已经削减了一些知识点。例如：在我们的最后一个例子中，我们没有检查麻烦的/ CropBox是否被定义。如果原始的PDF有一个/CropBox，放大/ MediaBox不会有任何视觉效果。为此我们需要更深入的教程来涵盖这些主题。

### 总结

在前一章，我们学习了有关交互式PDF格式的知识。在本章，我们继续使用这些表单。我们在一个现有的表单中添加了一个注解，一些文本和一个额外的字段。在填写表格时，我们也改变了一些属性。

然后，我们没有任何交互性地转战PDF主体。首先，我们给PDF添加了一个页眉，一个页脚和一个水印。然后，我们随意更改了现有文档页面的大小和方向。

在下一章中，我们将对现有文档进行缩放和平铺，并且我们将介绍如何将多个文档组合到一个PDF中。
