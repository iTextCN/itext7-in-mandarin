ready to translate:[https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-4-making-pdf-interactive](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-4-making-pdf-interactive)
## PDF交互
>标签：[Java](https://developers.itextpdf.com/tags/java)
[annotations](https://developers.itextpdf.com/tags/annotations)
[forms](https://developers.itextpdf.com/tags/forms)
[AcroForm](https://developers.itextpdf.com/tags/acroform)

![](https://developers.itextpdf.com/sites/default/files/C04F01.png "图4.1:一个文本注解
")

在前面的章节中，我们通过向页面添加内容来创建PDF文档。如果我们添加高级对象（如段落）或低级指令（例如lineTo()，moveTo()，stroke()），iText将所有内容转换为写入到其中的PDF语法，更多的内容流。在本章中，我们将添加不同性质的内容。我们将添加交互功能，称为注解。注解不是内容流的一部分。他们通常被添加在现有的内容之上。有许多不同类型的注解，其中许多允许用户交互。
### 添加注解
我们将从一系列简单的例子开始。图4.1显示了一个带有文本段落的PDF。在文本的顶部，我们添加了一个绿色文本注解。
![](https://developers.itextpdf.com/sites/default/files/C04F01.png "图4.1：文本注解
")

<p align="center">图4.1：文本注解</p>

TextAnnotation示例的大部分代码与Hello World示例相同。唯一的区别是我们创建并添加一个注解：
```
PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(20, 800, 0, 0))
    .setColor(Color.GREEN)
    .setTitle(new PdfString("iText"))
    .setContents("With iText, "
        + "you can truly take your documentation needs to the next level.")
    .setOpen(true);
pdf.getFirstPage().addAnnotation(ann);
```
我们使用Rectangle定义文本注解的位置。我们设置颜色，标题（一个PdfString），内容（一个字符串）和注解的打开状态。我们问PdfDocument的第一页，并添加注解。

在图4.2中，我们创建了一个不可见的注解，但是如果将鼠标悬停在其位置上，则会显示一个URL。你可以通过单击注解来打开该URL。这是一个链接注解。

![](https://developers.itextpdf.com/sites/default/files/C04F02.png "图4.2：一个链接注解")

<p align="center">图4.2：一个链接注解</p>

由于注解是一个句子的一部分，如果我们必须计算单词“here”的位置，这很不方便。幸运的是，可以将链接注解包装在一个Link对象中，iText会自动计算Rectangle。LinkAnnotation示例显示了如何完成。
```
PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0))
        .setAction(PdfAction.createURI("http://itextpdf.com/"));
Link link = new Link("here", annotation);
Paragraph p = new Paragraph("The example of link annotation. Click ")
        .add(link.setUnderline())
        .add(" to learn more...");
document.add(p);
```
在第2行中，我们创建了一个打开iText网站的URI操作。我们将这个动作用于链接注解，然后创建一个Link对象。这是接受链接注解作为参数的基本构建块。此链接注解将不会添加到内容流中，因为注解不是内容流的一部分。相反，它将被添加到相应坐标的相应页面。使文字可点击不会改变内容流中文字的外观。在例子中，我们强调了“here”这个词，以便我们知道在哪里点击。

每种类型的注解都需要自己的参数类型。图4.3显示了一个带有线条注解的页面。

![](https://developers.itextpdf.com/sites/default/files/C04F03.png "图4.3：一个线条注解")

<p align="center">图4.3：一个线条注解</p>

[LineAnnotation](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-4#1758-c04e01_03_lineannotation.java)显示创建此外观所需的内容。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PdfPage page = pdf.addNewPage();
PdfArray lineEndings = new PdfArray();
lineEndings.add(new PdfName("Diamond"));
lineEndings.add(new PdfName("Diamond"));
PdfAnnotation annotation = new PdfLineAnnotation(
    new Rectangle(0, 0),
    new float[]{20, 790, page.getPageSize().getWidth() - 20, 790})
        .setLineEndingStyles((lineEndings))
        .setContentsAsCaption(true)
        .setTitle(new PdfString("iText"))
        .setContents("The example of line annotation")
        .setColor(Color.BLUE);
page.addAnnotation(annotation);
pdf.close();
```
在这个例子中，我们将注解添加到新创建的页面。这个例子中没有Document实例。

ISO-32000-2定义了28种不同的注解类型，其中两种在PDF 2.0中不推荐使用。使用iText，你可以将所有这些注解类型添加到PDF文档中，但在本教程的上下文中，我们只会看一个示例，然后再转到交互式表单。见图4.4：

![](https://developers.itextpdf.com/sites/default/files/C04F04.png "图4.4：标记注解")

<p align="center">图4.4：标记注解</p>

看看[TextMarkupAnnotation](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-4#1759-c04e01_04_textmarkupannotation.java)的例子，能看到我们确实需要一个单独的教程来理解这个代码片段中使用的所有细节。
```
PdfAnnotation ann = PdfTextMarkupAnnotation.createHighLight(
        new Rectangle(105, 790, 64, 10),
        new float[]{169, 790, 105, 790, 169, 800, 105, 800})
    .setColor(Color.YELLOW)
    .setTitle(new PdfString("Hello!"))
    .setContents(new PdfString("I'm a popup."))
    .setTitle(new PdfString("iText"))
    .setOpen(true)
    .setRectangle(new PdfArray(new float[]{100, 600, 200, 100}));
pdf.getFirstPage().addAnnotation(ann);
```
在下一节中，我们将创建一个由不同表单域组成的交互式表单。表单中的每个表单字段都将与一个窗口小部件注解对应，但这些注解将被隐式创建。

### 创建一个交互式表单
在下一个例子中，我们将创建一个基于AcroForm技术的交互式表单。该技术是在PDF 1.2（1996）中引入的，允许你使用表单字段填充PDF文档，例如文本字段，选项（组合框或列表字段），按钮（按钮，复选框和单选按钮）以及签名字段。

>将PDF表单与HTML中的表单进行比较是很有吸引力的，但这是错误的。当文本不适合HTML表单的可用文本区域时，可以调整该字段的大小。列表字段的内容可以基于对服务器的查询而实时更新。总之，一个HTML表单可以是非常动态的。

这并不意味着AcroForm技术已经变得毫无用处。交互式PDF格式在两个特定的用例中非常常见：
* 当表格就相当于数字纸时，在某些情况下，对表格有严格的形式要求。数字文档是相应表单的精确副本，这一点很重要。填写的每一份表格都需要遵守完全相同的形式要求。如果是这种情况，那么使用PDF表单比HTML表单更好。
* 当表单不用于数据收集时，而是用作模板。例如：你有一个表单，代表一个事件的凭证或入场券。在这个表格中，你有不同的领域，包括买票的人的姓名，事件的日期和时间，行和座位号等等。当人们买票时，你不需要重新生成完整的凭证，可以采取适当的形式并填写适当的数据。

在这两种使用情况下，都将手动创建表单，例如使用Adobe软件，LibreOffice或任何其他具有图形用户界面的工具。

你也可以用编程方式创建这样的一个表单，但是很少有用例可以证明使用软件库来创建表单或模板，而不是使用带有GUI的工具。不过，我们要试一试。

![](https://developers.itextpdf.com/sites/default/files/C04F05.png "图4.5：一个交互式表单")

<p align="center">图4.5：一个交互式表单</p>

在图4.5中，可以看到文本字段，单选按钮，复选框，组合框，多行文本字段和按钮。我们看到这些字段是因为它们由一个小部件注解表示。当创建一个字段时，这个小部件注解是隐式创建的。在JobApplication示例中，我们使用从Document对象获取的PdfDocument实例创建一个PdfAcroForm对象。第二个参数是布尔值，表示如果没有现有表单，是否需要创建新表单。由于我们刚刚创建了文档，目前还没有任何表单，所以参数应该是正确的：
```
PdfAcroForm form = PdfAcroForm.getAcroForm(doc.getPdfDocument(), true);
```
现在可以开始添加字段。我们将使用Rectangle来定义每个小部件注解的维度及其在页面上的位置。
### 文本域
这部分内容将从用于全名的文本字段开始。
```
PdfTextFormField nameField = PdfTextFormField.createText(
    doc.getPdfDocument(), new Rectangle(99, 753, 425, 15), "name", "");
form.addField(nameField);
```
createText()方法需要一个PdfDocument实例，一个Rectangle，该字段的名称和一个默认值（在这种情况下，默认值是一个空字符串）。请注意，字段的标签和小部件注解是两个不同的东西。我们已经使用段落添加了“Full name:”。该段落是内容流的一部分，而该字段本身不属于内容流。它使用小部件注解表示。
### 单选按钮
我们创建一个无线电领域来选择一种语言。请注意，有一个名为language的radio组有五个未命名的按钮字段，每个语言可以选择一个：
```
PdfButtonFormField group = PdfFormField.createRadioGroup(
    doc.getPdfDocument(), "language", "");
PdfFormField.createRadioButton(doc.getPdfDocument(),
    new Rectangle(130, 728, 15, 15), group, "English");
PdfFormField.createRadioButton(doc.getPdfDocument(),
    new Rectangle(200, 728, 15, 15), group, "French");
PdfFormField.createRadioButton(doc.getPdfDocument(),
    new Rectangle(260, 728, 15, 15), group, "German");
PdfFormField.createRadioButton(doc.getPdfDocument(),
    new Rectangle(330, 728, 15, 15), group, "Russian");
PdfFormField.createRadioButton(doc.getPdfDocument(),
    new Rectangle(400, 728, 15, 15), group, "Spanish");
form.addField(group);
```
一次只能选择一种语言。如果可以应用多个选项，我们应该使用复选框。
### 复选框
在下一个片段中，我们将介绍三个复选框，名为experience0，experience1，experience2：
```
for (int i = 0; i < 3; i++) {
    PdfButtonFormField checkField = PdfFormField.createCheckBox(
        doc.getPdfDocument(), new Rectangle(119 + i * 69, 701, 15, 15),
        "experience".concat(String.valueOf(i+1)), "Off",
        PdfFormField.TYPE_CHECK);
    form.addField(checkField);
}
```
如你所见，我们使用带有以下参数的createCheckBox()方法：PdfDocument对象，Rectangle，字段名称，字段的当前值以及复选标记的外观。

>复选框有两个可能的值：关闭状态的值必须是“Off”;    on状态的值通常是“Yes”（这是iText默认使用的值），但是这里允许一些自由度。

人们也可以从列表或组合框中选择一个或多个选项。在PDF术语中，我们称之为选择领域。

### 选择领域
选项字段的配置方式可以让用户只能选择其中一个选项或多个选项。在我们的例子中，我们创建了一个组合框。
```
String[] options = {"Any", "6.30 am - 2.30 pm", "1.30 pm - 9.30 pm"};
PdfChoiceFormField choiceField = PdfFormField.createComboBox(
    doc.getPdfDocument(), new Rectangle(163, 676, 115, 15),
    "shift", "Any", options);
form.addField(choiceField);
```
我们的选择字段被命名为“shift”，并提供三个选项，其中默认选择“Any”。
### 多行字段
我们也看到一个多行字段的形式。与常规文本字段相反，只能在单行中添加文本的情况下，如果该字段不适合单行，则该字段中的文本将被封装。
```
PdfTextFormField infoField = PdfTextFormField.createMultilineText(
    doc.getPdfDocument(), new Rectangle(158, 625, 366, 40), "info", "");
form.addField(infoField);
```
我们将用一个按钮来结束我们的表格。
### 按钮
在现实世界的例子中，我们使用一个提交按钮，允许用户将他们输入的数据提交给服务器。这种PDF格式已经很少见，因为HTML演变成了HTML5和相关技术，引入了更多用户友好的功能来填写表单。我们通过添加一个重置按钮来结束这个例子，当按钮被点击时，重置按钮将把选择的字段重置为它们的初始值。
```
PdfButtonFormField button = PdfFormField.createPushButton(doc.getPdfDocument(),
        new Rectangle(479, 594, 45, 15), "reset", "RESET");
button.setAction(PdfAction.createResetForm(
    new String[] {"name", "language", "experience1", "experience2",
        "experience3", "shift", "info"}, 0));
form.addField(button);
```
如果你想使用iText来创建一个PDF表单，那么现在你已经明白了如何完成了。在许多情况下，使用具有图形用户界面的工具手动创建表单是一个更好的主意。然后你将使用iText自动填写此表单，例如使用数据库中的数据。
### 填写表格
当我们创建表单时，可以定义默认值，以便填写表单，如图4.6所示。
![](https://developers.itextpdf.com/sites/default/files/C04F06_0.png "图4.6：一个填写完整的交互式表单")

<p align="center">图4.6：一个填写完整的交互式表单</p>

我们仍然可以在创建表单后添加这些值。[CreateAndFill](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-4#1761-c04e03_createandfill.java)示例向我们展示了如何添加这些值。
```
Map<String, PdfFormField> fields = form.getFormFields();
fields.get("name").setValue("James Bond");
fields.get("language").setValue("English");
fields.get("experience1").setValue("Off");
fields.get("experience2").setValue("Yes");
fields.get("experience3").setValue("Yes");
fields.get("shift").setValue("Any");
fields.get("info").setValue("I was 38 years old when I became an MI6 agent.");
```
我们问过为其字段添加了所有表单字段的PdfAcroForm，并且得到一个由键值对组成的映射，每个字段的名称和PdfFormField对象。我们可以逐个获取PdfFormField实例，并设置它们的值。当然，这没有什么意义。在创建每个字段的那一刻，设置正确的值可能会更聪明。更常见的用例是预先填写现有的表单。

### 预先填写现有的表格
在下一个示例中，我们将采用现有的表单[job_application.pdf](http://gitlab.itextsupport.com/itext7/samples/raw/develop/publications/jumpstart/cmpfiles/chapter04/cmp_job_application.pdf)，从该表单获取一个PdfAcroForm对象，并使用完全相同的代码填写现有文档。请参阅[FillForm](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-4#1762-c04e04_fillform.java)示例。
```
PdfDocument pdf = new PdfDocument(
    new PdfReader(src), new PdfWriter(dest));
PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
Map<String, PdfFormField> fields = form.getFormFields();
fields.get("name").setValue("James Bond");
fields.get("language").setValue("English");
fields.get("experience1").setValue("Off");
fields.get("experience2").setValue("Yes");
fields.get("experience3").setValue("Yes");
fields.get("shift").setValue("Any");
fields.get("info").setValue("I was 38 years old when I became an MI6 agent.");
pdf.close();
```
我们在第2行介绍了一个新的对象。PdfReader是一个允许iText访问PDF文件并读取存储在PDF文件中的不同PDF对象的类。在这种情况下，src保存现有表单的路径。
>I / O由iText中的两个类来处理。
>* PdfReader是输入类;
>* PdfWriter是输出类。

在第2行中，我们创建了一个PdfWriter，它将写入一个新版本的源文件。第1行和第2行与我们之前所做的不同。现在使用reader和writer对象作为参数创建一个PdfDocument对象，可以使用与以前相同的getAcroForm()方法获得一个PdfAcroForm实例。第4到第11行与用来填写从头开始创建的字段的值的行完全相同。当关闭PdfDocument（第12行）时，将会有一个与图4.6所示相同的PDF。

>表单仍然是交互式的：人们仍然可以改变值。iText已被用于许多应用程序预填表单。例如，当用户登录在线服务时，服务器端已经知道了很多信息（如姓名，地址，电话号码）。当他们需要在线填写表格时，给他们一个空白的文件，他们必须重新填写他们的姓名，地址和电话号码是没有什么意义的。如果这些值已经存在于表格中，则可以节省大量的时间。这可以通过iText预填充表单来实现。人们可以检查信息是否正确，如果不正确（例如，因为他们的电话号码改变了），他们仍然可以改变字段的内容。

有时你不希望最终用户更改PDF上的信息。例如：如果表单是具有特定日期和时间的凭证，则不希望最终用户更改该日期和时间。在这种情况下，你会把表格弄平。
### 平整表单
当我们添加一行到前面的代码片段时，将得到一个不再交互的PDF。图4.7中的消息“此文件包含可填写的表单域”已消失。当你点击名字“詹姆斯·邦德”时，你不能再手动改变它。
![](https://developers.itextpdf.com/sites/default/files/C04F07.png "图4.7：扁平的形式")

<p align="center">图4.7：扁平的形式</p>

这个额外的行是在[FlattenForm](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-4#1763-c04e05_flattenform.java)示例中添加的。
```
PdfDocument pdf =
    new PdfDocument(new PdfReader(src), new PdfWriter(dest));
PdfAcroForm form = PdfAcroForm.getAcroForm(pdf, true);
Map<String, PdfFormField> fields = form.getFormFields();
fields.get("name").setValue("James Bond");
fields.get("language").setValue("English");
fields.get("experience1").setValue("Off");
fields.get("experience2").setValue("Yes");
fields.get("experience3").setValue("Yes");
fields.get("shift").setValue("Any");
fields.get("info").setValue("I was 38 years old when I became an MI6 agent.");
form.flattenFields();
pdf.close();
```
在我们设置了表单字段的所有值后，我们添加第13行：form.flattenFields()，所有的字段将被删除; 相应的小部件注解将被其内容替换。
###  总结
我们通过寻找少量的注解类型来开始本章：
* 一个文本注解，
* 链接注解，
* 一个线条注解，
* 和一个文本标记注解。
我们还提到了小部件注解。这导致我们到互动形式的主题。我们学会了如何创建一个表单，但更重要的是如何填写和拼合表单。

在填充和压扁的例子中，我们遇到了一个新的类，PdfReader。在下一章中，我们将看看更多使用这个类的例子。