ready to translate:[https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-4-making-pdf-interactive](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-4-making-pdf-interactive)
# **前言**
![itext4-1](http://obkwqzjnq.bkt.clouddn.com/itext4-1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;在之前的章节中，我们创建了PDF文件，并往里面添加了内容。不管我们使用的是高级api（例如`Paragraph`）或者低级api（例如``lineTO()``,``MoveTo``,``stroke()``），iText会把这些api转换成pdf的语法，这些pdf语法会被写入内容流(content stream)。在本章，我们会介绍一种不同特性的内容————注解（如上图）。注解并不属于内容流(content stream)，他们通常被放在已经存在内容的上面。注解的种类有很多，大多数的注解可以允许用户交互。


# **添加注解**

&nbsp;&nbsp;&nbsp;&nbsp;我们从简单的例子开始讲起，如下图，我们首先添加了一个``Paragraph``类型的文本，然后在这文本之前添加了绿色的注释。  

![itext4-2](http://obkwqzjnq.bkt.clouddn.com/itext4-2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这个例子的大部分代码都和第一章的HelloWorld的例子完全一样，多了的是创建和添加注释：
```
PdfAnnotation ann = new PdfTextAnnotation(new Rectangle(20, 800, 0, 0))
    .setColor(Color.GREEN)
    .setTitle(new PdfString("iText"))
    .setContents("With iText, "
        + "you can truly take your documentation needs to the next level.")
    .setOpen(true);
pdf.getFirstPage().addAnnotation(ann);
```
&nbsp;&nbsp;&nbsp;&nbsp;我们通过定义一个``Rectangle``的方式来定义文本注释的位置，然后设置颜色、注释标题(``PdfString``对象)、内容(``String``对象)和注释打开选项，最后通过``PdfDocument``对象来获得第一页对象然后添加注释。  
&nbsp;&nbsp;&nbsp;&nbsp;这个例子过后，我们来看看下面这个例子，如下图，我们创建了一个可视的注释，如果你鼠标停留在附近的话就会显示一个原始网站，我们可以通过点击这个文本来打开这个链接，这就是链接注释。  

![itext4-3](http://obkwqzjnq.bkt.clouddn.com/itext4-3.png)  

&nbsp;&nbsp;&nbsp;&nbsp;因为注释是所在位置是句子的一部分，如果我们需要计算here在句子中的位置的话，这是极其不方便。幸运的是，我们可以把链接注释包裹在一个``Link``对象中，iText会自动计算注释的``Rectangle``，代码如下：
```
PdfLinkAnnotation annotation = new PdfLinkAnnotation(new Rectangle(0, 0))
        .setAction(PdfAction.createURI("http://itextpdf.com/"));
Link link = new Link("here", annotation);
Paragraph p = new Paragraph("The example of link annotation. Click ")
        .add(link.setUnderline())
        .add(" to learn more...");
document.add(p);
```
&nbsp;&nbsp;&nbsp;&nbsp;在第2行，我们创建了一个URI，这个URI可以打开iText的官网。我们把这个action当做是链接注释的参数，然后创建了一个``link``对象：这个一个接受一个链接注释对象为参数的基础绘画对象。这个链接注释不会被加入内容流中，因为**注释不属于内容流！** 相反的是，链接注释会被放在特定页的特定位置上。使文本可以点击不会改变内容流里面的文本的外表，我们在here下面加下划线能让我们知道在哪点击链接。  
&nbsp;&nbsp;&nbsp;&nbsp;不同种类的注释接受它自己定义的参数，如下图是线注释：  

![itext4-4](http://obkwqzjnq.bkt.clouddn.com/itext4-4.png)  


&nbsp;&nbsp;&nbsp;&nbsp;下面代码展示了如何变成的过程：
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
&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们把注释添加到了新创的页面中，在这边我们没有创建一个``Document``对象。  
&nbsp;&nbsp;&nbsp;&nbsp;ISO-32000-2定义了28中不同的注释，有2种在PDF2.0中被弃用，你可以使用剩余26种注释，限于篇幅限制，剩下不同种类的注释就不再一一给出了，我们等等把注意力移到交互型的注释，先看一下如下图：  

![itext4-5](http://obkwqzjnq.bkt.clouddn.com/itext4-5.png)  

&nbsp;&nbsp;&nbsp;&nbsp;这个TextMarkupAnnotation例子，代码如下，我们可能需要专门的章节来讲一下代码（外国人埋坑能力超强）
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
&nbsp;&nbsp;&nbsp;&nbsp;在下一节中，我们将创建一个包含不同表单字段的交互式表单。该表单中的每个表单字段将与窗口小部件注释（widget annotation）对应，但这些注释将被隐式创建。

# **创建交互式表单**

&nbsp;&nbsp;&nbsp;&nbsp;下一个例子中，我们使用AcroForm技术来创建交互式表单。AcroForm这个技术是在PDF1.2(1992)中首次提及的技术，这个技术可以在PDF文档中填充各种表单字段，例如文本域、选择框（组合框或者列表等）、按钮（下压按钮、复选框和单选按钮等）和签名域

> 将PDF表单与HTML中的表单进行比较是很诱人的，但这是错误的。当文本长度超过HTML表单的可用文本区域时，这个文本区域可以调整大小。可以基于对服务器的查询，即时更新列表字段的内容。简而言之，HTML表单可以非常动态。  
但是对于AcroForm技术的交互式表单，这是不能实现的。这种形式的表单最好与纸张形式进行比较，每种字段都有其固定位置和固定尺寸。多年来已经放弃了使用PDF表单在网络浏览器中收集用户数据的想法。HTML表单对于在线数据收集更加用户友好。

&nbsp;&nbsp;&nbsp;&nbsp;但是这并不意味着AcroForm技术毫无用处，AcroForm技术应用于以下两种应用场景：
1. *当表单相当于数字纸（digital paper）*：在某些情况下，对表单有严格的形式要求。重要的是数字文档是相应表单的精确副本。填写的每个表单都需要符合相同的正式要求。如果是这种情况，那么使用PDF格式比HTML表格更好。
2. *当表单不用于数据收集，但作为模板*：例如：您有一个表单代表一个优惠券或一个活动的入场券。在这个表单上，你有不同的字段，例如谁买的票，事件的日期和时间，和座位号等等。当人们买票时，您不需要重新生成完整的凭证，您可以使用表单，只需填写适当的数据。 

&nbsp;&nbsp;&nbsp;&nbsp;在这两种应用场景中，我们可以通过Abode软件、LibreOffice和其他一些其他攻击的图形界面来手动创建表单。  
&nbsp;&nbsp;&nbsp;&nbsp;您也可以以编程方式创建一个这样的表单，但是很少有用例可以使用软件库来创建表单或模板，而不是使用带GUI的工具。不过，我们要试一试。先看下图：  

![itext4-6](http://obkwqzjnq.bkt.clouddn.com/itext4-6.png)  


&nbsp;&nbsp;&nbsp;&nbsp;上图中，我们可以看见文本域、单选按钮、复选框、下拉列表框、多行文本域和一个下压按钮。我们看到这些字段，因为它们由窗口小部件注释表示。当我们创建一个字段时，这个小部件注释是隐式创建的。在下面的代码中，我们先创建另一个``PdfAcroForm``对象，第一个参数是``PdfDocument``类型的参数，从``Document``对象中获取，第二个参数一个布尔值，表明这个新的表单是否创建，如果没有已知表单存在的话。因为我们刚刚创建了``Document``的对象，里面没有表单，所以我们设置为True。代码如下：
```
PdfAcroForm form = PdfAcroForm.getAcroForm(doc.getPdfDocument(), true);
```
&nbsp;&nbsp;&nbsp;&nbsp;现在我们可以往里面添加字段，我们将会使用一个``Rectangle``窗口小部件注释的位置和尺寸。

## **文本域**

&nbsp;&nbsp;&nbsp;&nbsp;我们将从将用于全名(Full Name)的文本字段开始。以下代码：
```
PdfTextFormField nameField = PdfTextFormField.createText(
    doc.getPdfDocument(), new Rectangle(99, 753, 425, 15), "name", "");
form.addField(nameField);
```
&nbsp;&nbsp;&nbsp;&nbsp;``createText()``方法需要一个``PdfDocument``实例、一个``Rectangle``、域的名称、一个默认的值（在这个例子中，默认的值为一个空的``String``）。值得注意的是，文本域的标签和窗口小部件注释是不同的。我们使用一个``Paragraph``来添加"Full Name"。这个``Paragraph``是内容流的一部分。文本域不属于内容流，它可以用小窗口部件注释来表示。

## **单选按钮**

&nbsp;&nbsp;&nbsp;&nbsp;我们创建单选按钮来选择语言，值得注意的是，这里有一个名称为``language``的radio group，还有五个没有名称的按钮，只有其中一个按钮会被选中。
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

## **复选框**

&nbsp;&nbsp;&nbsp;&nbsp;在下面的代码段中，我们会引入三个复选按钮，名称为``experience0``、``experience1``和``experience2``:
```
for (int i = 0; i < 3; i++) {
    PdfButtonFormField checkField = PdfFormField.createCheckBox(
        doc.getPdfDocument(), new Rectangle(119 + i * 69, 701, 15, 15),
        "experience".concat(String.valueOf(i+1)), "Off",
        PdfFormField.TYPE_CHECK);
    form.addField(checkField);
}
```
&nbsp;&nbsp;&nbsp;&nbsp;正如大家所见，我们使用``createCheckBox()``方法，函数的参数为：``PdfDocument``对象，``Rectangle``，check box的名称，当前的值，选中标记的外观。

> 一个check box的值有两种可能的值：未选中状态的值必须是"off";选中的值通常是"Yes"(这个值得iText使用的默认值)，但是这里可以使用其他值，看自己的选择了。

&nbsp;&nbsp;&nbsp;&nbsp;我们可以从列表框或者下拉列表框选择一个或者多个选项，在PDF术语中，我们称之为选择字段（choice field）。

## **选择字段（choice field）**

&nbsp;&nbsp;&nbsp;&nbsp;在这里，我们创建下拉列表框，选择字段为的名称为``"shift"``，并且提供三个选中，其中``Any``选项被默认被选中。
```
String[] options = {"Any", "6.30 am - 2.30 pm", "1.30 pm - 9.30 pm"};
PdfChoiceFormField choiceField = PdfFormField.createComboBox(
    doc.getPdfDocument(), new Rectangle(163, 676, 115, 15),
    "shift", "Any", options);
form.addField(choiceField);
```

## **多行文本框**

&nbsp;&nbsp;&nbsp;&nbsp;多行文本框与通常的文本框相比是翔安的，普通文本框
如果添加的内容超出单行能显示的内容，则此字段中的文本将会只显示一部分，其余部分被包裹。

```
PdfTextFormField infoField = PdfTextFormField.createMultilineText(
    doc.getPdfDocument(), new Rectangle(158, 625, 366, 40), "info", "");
form.addField(infoField);
```

## **下压按钮**

&nbsp;&nbsp;&nbsp;&nbsp;在现实的例子中，我们将使用一个提交按钮，允许人们将他们以表单输入的数据提交到服务器。这种PDF表格已经变得罕见，因为HTML演变为HTML 5和相关技术，引入更的用户友好的功能来填写表单。我们通过添加重置按钮来结束示例，该按钮将在点击按钮时将选定的字段重置为其初始值。
```
PdfButtonFormField button = PdfFormField.createPushButton(doc.getPdfDocument(),
        new Rectangle(479, 594, 45, 15), "reset", "RESET");
button.setAction(PdfAction.createResetForm(
    new String[] {"name", "language", "experience1", "experience2",
        "experience3", "shift", "info"}, 0));
form.addField(button);
```
&nbsp;&nbsp;&nbsp;&nbsp;如果您想使用iText创建一个PDF表单，那么您现在可以对它的完成情况有一个很好的了解。在许多情况下，使用具有图形用户界面的工具来手动创建表单是一个更好的主意。然后，您将使用iText自动填写此表单，例如使用数据库中的数据。

# **填充交互式表单**

&nbsp;&nbsp;&nbsp;&nbsp;当我们创建完表单，我们可以设置他们的默认的值，如下图：  

![itext4-7](http://obkwqzjnq.bkt.clouddn.com/itext4-7.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们一旦创建完表单，我们就可以设置这些字段的值，代码如下：
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
&nbsp;&nbsp;&nbsp;&nbsp;我们之前都是想``PdfAcroForm``对象(form变量)添加了各种各样的字段，我们通过这个对象来获得各个字段的``Map``，然后我们可以一个一个设置值，当然还有其他更有效的方式，我们这种填充的技术通常应用于预填充一个存在的表单中。

# **预填充已存在的表单**

&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们从一个存在表单的pdf中获取一个``PdfAcroForm``的表单，然后像之前的代码一样来进行操作：
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
&nbsp;&nbsp;&nbsp;&nbsp;在第二行里面，``PdfReader``一个可以让iText读取pdf中不同种类的对象，在这里``src``指向存在的pdf文件路径。

> 在iText中，I/O由两个类来处理：1.``PdfReader``负责输入 2.``PdfWriter``负责输出

&nbsp;&nbsp;&nbsp;&nbsp;第一行第二行和我们之前创建``PdfDocument``的方式不太一样，在这里我们同时接收``reader``和``writer``对象为参数，然后我们使用``getAcroForm()``获得``PdfAcroForm``对象，其余操作就和上面一样了。

> 在这里表单仍然是互动的：人们仍然根据需要改变相应的值。iText已被用于许多应用程序中以预填写表单。例如：当用户登录登录在线服务器是，服务器端已经知道很多信息（例如姓名，地址，电话号码）。当他们需要在线填写表单时，向他们提供一个空白的文件没有多大意义，他们必须再次填写他们的姓名，地址和电话号码。如果这些值已经存在于表单中，则可以节省大量时间。这可以通过用iText预填写表单来实现。人们可以检查信息是否正确，如果不是（例如因为他们的电话号码被改变），他们现场仍然可以改变其内容

&nbsp;&nbsp;&nbsp;&nbsp;有的时候我们不想让终端用户改变PDF里面的内容，如果表单是具有特定日期和时间的凭单，则不希望最终用户更改该日期和时间。在这种情况下，表单就会锁定

# **锁定表单**

&nbsp;&nbsp;&nbsp;&nbsp;我们在之前的代码里面，添加一个语句，之前的"This file includes fillable form fields"消失了，当你点击Full name后就不能手动改变里面的值，如下图：  

![itext4-8](http://obkwqzjnq.bkt.clouddn.com/itext4-8.png)  

&nbsp;&nbsp;&nbsp;&nbsp;代码如下，在第12行加入``form.flattenFields()``，所有字段会被锁定，对应的小窗口部件注释将会被内容所替代。  

# **总结**

&nbsp;&nbsp;&nbsp;&nbsp;在本章我们介绍了很多种类的注释
- 文本注释
- 线注释
- 标记注释（markup annoation）
- 小窗口部件注释（widget annoation）

&nbsp;&nbsp;&nbsp;&nbsp;在填充和锁定表单的例子中，我们引入了``PdfReader``这个类，在后面的章节中，我们会继续讨论这个类。