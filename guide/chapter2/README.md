ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-2-adding-low-level-content](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-2-adding-low-level-content)
## 章节2：添加底层内容
当谈论iText文档中的底层内容时，我们总是会引用写入PDF内容流的PDF语法。PDF定义了一系列运算符，比如在iText中为其创建moveTo()方法的m，lineTo()方法的l，以及stroke()方法的S。通过在PDF中组合这些操作数—或者在iText中组合这些方法—您可以绘制路径和形状。

现在来看一个小例子：
```
-406 0 m
406 0 l
S
```
这是一个PDF语法：移动到位置（X = -406; Y = 0），然后构造一个路径到位置（X = 406; Y = 0）；最后笔画这条线—在这个上下文中，“抚摸”是指绘画。如果想用iText创建这个PDF语法片段，就像这样：
```
canvas.moveTo(-406, 0)
.lineTo(406, 0)
.stroke();
```
这看起来很简单，不是吗？但是，我们使用的画布对象是什么？现在来看看几个例子来找出答案。
### 在画布上画直线
假设想创建一个如图2.1所示的PDF。

![](https://developers.itextpdf.com/sites/default/files/C02F01.png "图2.1：绘制X和Y轴")

<p align="center">图2.1：绘制X和Y轴</p>

显示X和Y轴的PDF是使用[Axes](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-2#1734-c02e01_axes.java)示例创建的。现在开始一步一步检查这个例子。
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PageSize ps = PageSize.A4.rotate();
PdfPage page = pdf.addNewPage(ps);
PdfCanvas canvas = new PdfCanvas(page);
// Draw the axes
pdf.close();
```
跳出来的第一件事就是我们不再使用Document对象。就像在前一章中一样，一开始创建了一个PdfWriter（第2行）和一个PdfDocument对象，而不是创建一个具有默认或特定页面大小的Document，我们创建的是一个带有特定PageSize（第2行）的PdfPage（第3行）。在这种情况下，我们使用横向的A4页面。一旦有一个PdfPage实例，我们就用它来创建一个PdfCanvas（第4行）。之后将使用这个画布对象来创建一个PDF操作符和操作数序列。只要我们完成画图和绘制要添加到页面的路径和形状，之后就会关闭PdfDocument（第6行）。
>在前一章中，我们用document.close()关闭了Document对象。这隐式关闭了PdfDocument对象。现在没有Document对象，我们必须关闭PdfDocument对象。
在PDF中，所有测量均以pt完成。默认情况下，一个pt对应一个点。这意味着一英寸有72个pt。在PDF中，X轴指向右侧，Y轴指向上。如果使用PageSize对象创建页面大小，则坐标系的原点应该位于页面的左下角。所有我们用作操作数的坐标，例如m或l，都使用这个坐标系。我们可以通过改变*当前的变换矩阵*来改变坐标系。
### 坐标系和变换矩阵
如果你已经在解析几何中跟随了一个类，那么就可以知道通过应用一个变换矩阵来在空间中移动物体。在PDF中，我们不移动对象，但我们移动坐标系，并在新坐标系中绘制对象。假设我们要移动坐标系统，坐标系统的原点位于页面的中间位置。在这种情况下，我们需要使用concatMatrix()方法：
```
canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
```
concatMatrix()方法的参数是变换矩阵的元素。这个矩阵由三列和三行组成：
```
a   b   0
c   d   0
e   f   1
```
因为我们在二维空间中工作，因此第三列元素的值总是固定的（0，0和1）。值a，b，c和d可用于缩放、旋转和倾斜坐标系。我们没有理由将其限制在轴正交的坐标系中，或者X方向的进度需要与Y方向的进度相同。但让我们保持简单，并使用1，0，0和1作为a，b，c和d的值。元素e和f定义了翻译。我们采用页面大小ps，把它的宽度和高度除以2来得到e和f的值。
### 图形状态
当前的转换矩阵是页面图形状态的一部分。在图形状态下定义的其他值是线宽，笔划颜色（线条），填充颜色（形状）等等。在另一个教程中，我们将更深入地描述图形状态的每个值。现在知道默认线宽是1个pt并且默认笔划颜色是黑色就足够了。让我们画出之前在图2.1中看到的那些轴：
```
//Draw X axis
canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
        .lineTo(ps.getWidth() / 2 - 15, 0)
        .stroke();
//Draw X axis arrow
canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
        .moveTo(ps.getWidth() / 2 - 25, -10)
        .lineTo(ps.getWidth() / 2 - 15, 0)
        .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
        .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
//Draw Y axis
canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
        .lineTo(0, ps.getHeight() / 2 - 15)
        .stroke();
//Draw Y axis arrow
canvas.saveState()
        .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
        .moveTo(-10, ps.getHeight() / 2 - 25)
        .lineTo(0, ps.getHeight() / 2 - 15)
        .lineTo(10, ps.getHeight() / 2 - 25).stroke()
        .restoreState();
//Draw X serif
for (int i = -((int) ps.getWidth() / 2 - 61);
    i < ((int) ps.getWidth() / 2 - 60); i += 40) {
    canvas.moveTo(i, 5).lineTo(i, -5);
}
//Draw Y serif
for (int j = -((int) ps.getHeight() / 2 - 57);
    j < ((int) ps.getHeight() / 2 - 56); j += 40) {
    canvas.moveTo(5, j).lineTo(-5, j);
}
canvas.stroke();
```
这个代码片段由不同的部分组成：
* 2-4行和12-14行不应该有你的秘密了。我们移动到一个坐标，构建一条线到另一个坐标，开始划线。
* 第6-10行绘制两条相互连接的线。这里有一些可行的方法来绘制该连接：尖角（线条加入尖锐的点），斜角（角落斜切）和圆形（角落圆角）。我们希望角落被四舍五入，所以我们将默认的行连接值（这是MITRE）更改为ROUND。我们用一个moveTo()和两个lineTwo()调用来构造箭头的路径，并且将行连接值更改回默认值。虽然图形状态现在已经回到原始值，但这不是返回到先前图形状态的最佳方式。
* 第16-21行显示了一个更好的练习，每当改变图形状态时，我们应该使用这几行。首先我们用saveState()方法保存当前的图形状态，然后改变状态并绘制我们想绘制的任何线条或形状，最后，我们使用restoreState()方法返回到原始图形状态。在saveState()之后应用的所有更改都将被撤消。如果更改多个值（线条宽度，颜色...）或难以计算反转更改（返回到原始坐标系），这一点尤其有趣。
* 在第23-31行中，我们构造了每40个pt在两个轴上绘制的小衬线。请注意，我们不会立即绘制。只有当我们构建完整的路径时，才会调用stroke()方法。

通常有不止一种方法可以在画布上绘制线条和形状。这将导致我们对PDF文件的生产速度，对文件大小的影响，以及在PDF阅读器中呈现文档的速度等方面的不同方法的优缺点进行说明。这是另一个教程中需要进一步讨论的问题。
>还有一些具体的规则需要考虑。例如：saveState()和restoreState()的序列需要平衡。每个saveState()都需要一个restoreState(); 禁止使用saveState()之前的restoreState()。

现在让我们通过改变线条宽度，引入短划线模式，以及应用不同的笔触颜色来修改本章的第一个例子，从而得到如图2.2所示的PDF。
![](https://developers.itextpdf.com/sites/default/files/C02F02.png "图2.2：绘制一个网格")
<p align="center">图2.2：绘制一个网格</p>

在[GridLines](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-2#1735-c02e02_gridlines.java)示例中，我们首先定义一系列Color对象：
```
Color grayColor = new DeviceCmyk(0.f, 0.f, 0.f, 0.875f);
Color greenColor = new DeviceCmyk(1.f, 0.f, 1.f, 0.176f);
Color blueColor = new DeviceCmyk(1.f, 0.156f, 0.f, 0.118f);
```
PDF规范（ISO-32000）定义了许多不同的颜色空间，每个颜色空间都在iText的一个单独的类中实现。最常用的颜色空间是DeviceGray（由单个强度参数定义的颜色），DeviceRgb（由三个参数：红色，绿色和蓝色定义）和DeviceCmyk（由四个参数：青色，品红色，黄色和黑色定义）。在例子中，我们使用三种CMYK颜色。

>请注意，我们没有使用java.awt.Color类。我们正在使用iText的Color类，它可以在com.itextpdf.kernel.color包中找到。
我们要创建一个由细蓝线组成的网格：
```
canvas.setLineWidth(0.5f).setStrokeColor(blueColor);
for (int i = -((int) ps.getHeight() / 2 - 57);
    i < ((int) ps.getHeight() / 2 - 56); i += 40) {
    canvas.moveTo(-(ps.getWidth() / 2 - 15), i)
            .lineTo(ps.getWidth() / 2 - 15, i);
}
for (int j = -((int) ps.getWidth() / 2 - 61);
    j < ((int) ps.getWidth() / 2 - 60); j += 40) {
    canvas.moveTo(j, -(ps.getHeight() / 2 - 15))
            .lineTo(j, ps.getHeight() / 2 - 15);
}
canvas.stroke();
```
在第1行中，我们将行宽设置为pt的一半，颜色为蓝色。在第2-10行，我们构造了网格线的路径，并且我们在第11行中对它们进行了描边。

我们重复使用代码来绘制前面例子中的坐标轴，但是我们让它们在一行之前改变线宽和笔划颜色。
```
canvas.setLineWidth(3).setStrokeColor(grayColor);
```
在绘制轴之后，绘制一个2个pt的绿色虚线：
```
canvas.setLineWidth(2).setStrokeColor(greenColor)
        .setLineDash(10, 10, 8)
        .moveTo(-(ps.getWidth() / 2 - 15), -(ps.getHeight() / 2 - 15))
        .lineTo(ps.getWidth() / 2 - 15, ps.getHeight() / 2 - 15).stroke();
```
定义一条直线短划线有许多可能的变化，但是在这种情况下，我们使用三个参数来定义直线短划线。短划线的长度是10个pt; 差距的长度是10个pt; 阶段是8个pt-相位定义在虚线模式中开始破折号的距离。
>随意尝试一些在PdfCanvas类中可用的其他方法。你可以使用curveTo()方法构造曲线，使用rectangle()方法构建矩形等等。你也可以使用填充颜色使用fill()方法填充路径，而不是使用笔触颜色的stroke()方法来绘制路径。PdfCanvas类提供的不仅仅是Java版本的PDF运算符。它还引入了许多便利类来构建特定的路径，PDF中没有可用的操作符，如椭圆或圆形。
在下一个例子中，我们将看看图形状态的一个子集，它允许我们在绝对位置添加文本。
### 文本状态
在图2.3中，我们看到了“星球大战：帝国反击战”第五集的开头部分。
![](https://developers.itextpdf.com/sites/default/files/C02F03.png "图2.3：在绝对位置添加文本")

<p align="center">图2.3：在绝对位置添加文本</p>

创建这种PDF的最好方法是使用具有不同对齐的段落对象序列-标题中心; 左对齐正文文本），并将这些段落添加到一个Document对象。使用高级方法将文本分布在多行，如果内容不符合页面宽度，则自动引入换行符;如果剩余内容不符合页面高度，则使用分页符。

当我们使用低级方法添加文本时，所有这些都不会发生。我们需要将内容分解成小块文本，正如[StarWars](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-2#1736-c02e03_starwars.java)示例中所做的那样：
```
List<String> text = new ArrayList();
text.add("         Episode V         ");
text.add("  THE EMPIRE STRIKES BACK  ");
text.add("It is a dark time for the");
text.add("Rebellion. Although the Death");
text.add("Star has been destroyed,");
text.add("Imperial troops have driven the");
text.add("Rebel forces from their hidden");
text.add("base and pursued them across");
text.add("the galaxy.");
text.add("Evading the dreaded Imperial");
text.add("Starfleet, a group of freedom");
text.add("fighters led by Luke Skywalker");
text.add("has established a new secret");
text.add("base on the remote ice world");
text.add("of Hoth...");
```
为了方便起见，我们改变坐标系，使其原点位于左上角而不是左下角。然后用beginText()方法创建一个文本对象，之后再改变文本状态：
```
canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
canvas.beginText()
    .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 14)
    .setLeading(14 * 1.2f)
    .moveText(70, -40);
```
我们创建一个PdfFont来显示Courier Bold中的文本，和改变文本状态，以便所有绘制的文本都将使用字体大小为14的字体。我们还定义了这种字体大小的1.2倍的前导。领先的是后面两行文字的基线之间的距离。最后，我们更改文本矩阵，使光标向右移动70个pt，向下移动40个pt。

接下来，我们在文本列表中循环显示不同的字符串值，在一个新行上显示每个字符串—将光标向下移动16.2个pt（这是领先的）—，然后用endText()方法关闭文本对象。
```
for (String s : text) {
    //Add text and move to the next line
    canvas.newlineShowText(s);
}
canvas.endText();
```
不要在文本对象之外显示任何文本（由beginText()/ endText()方法分隔）。它也被禁止嵌套beginText()/ endText()序列。

如果我们假设这个例子，并且改变它以产生如图2.4所示的PDF，那又该怎么办呢？
![](https://developers.itextpdf.com/sites/default/files/C02F04.png "图2.4：在绝对位置添加倾斜的和彩色的文本")

<p align="center">图2.4：在绝对位置添加倾斜的和彩色的文本</p>

更改背景的颜色是[StarWarsCrawl](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples/chapter-2#1737-c02e04_starwarscrawl.java)示例中的简单部分：
```
canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
        .setColor(Color.BLACK, true)
        .fill();
```
我们创建一个矩形，其左下角的坐标为X = 0，Y = 0，其宽度和高度与页面大小的宽度和高度相对应，并将填充颜色设置为黑色。我们可以使用setFillColor（Color.BLACK），但最好还是使用更通用的setColor()方法。布尔值表示是否要更改笔触颜色（false）或填充颜色（true）。最后，我们使用填充颜色来填充矩形的路径。

现在来讨论代码中相对重要的部分：我们如何添加文本？
```
canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
Color yellowColor = new DeviceCmyk(0.f, 0.0537f, 0.769f, 0.051f);
float lineHeight = 5;
float yOffset = -40;
canvas.beginText()
    .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 1)
    .setColor(yellowColor, true);
for (int j = 0; j < text.size(); j++) {
    String line = text.get(j);
    float xOffset = ps.getWidth() / 2 - 45 - 8 * j;
    float fontSizeCoeff = 6 + j;
    float lineSpacing = (lineHeight + j) * j / 1.5f;
    int stringWidth = line.length();
    for (int i = 0; i < stringWidth; i++) {
        float angle = (maxStringWidth / 2 - i) / 2f;
        float charXOffset = (4 + (float) j / 2) * i;
        canvas.setTextMatrix(fontSizeCoeff, 0,
                angle, fontSizeCoeff / 1.5f,
                xOffset + charXOffset, yOffset - lineSpacing)
            .showText(String.valueOf(line.charAt(i)));
    }
}
canvas.endText();
```
再次，我们将坐标系统的原点更改为页面顶部（第1行），为文本定义一个CMYK颜色（第2行）。我们初始化线高（线3）和Y方向（线4）的偏移值，之后开始写一个文本对象。我们将使用Courier Bold作为字体，并定义一个pt的字体大小（第6行）。字体大小只有1，但将通过更改文本矩阵将文本缩放到可读大小。我们没有定义领先的; 我们不需要领先，因为不会使用newlineShowText()。相反，我们将计算每个单独的字符的起始位置，并逐个字符地绘制文本。接下来还介绍了填充颜色（第7行）。
>字体中的每个字形都被定义为一个路径。默认情况下，填充组成文本的字形的路径。这就是为什么我们设置填充颜色来改变文本的颜色。
我们开始遍历文本（第8行），并且每行读入一个字符串（第9行）。我们需要大量的数字来定义将用于定位每个字形的文本矩阵的不同元素。我们为每一行定义一个xOffset（第10行）。字体大小被定义为1个pt，但是我们将它乘以一个fontSizeCoeff，这取决于文本数组（第11行）中的行索引。在这里还将定义线将从yOffset（12）开始的位置。

我们计算每行（第13行）中的字符数，然后循环所有字符（第14行），根据字符在行中的位置定义一个角度（第15行）。charOffset取决于行的索引和字符的位置（第16行）。

现在我们准备设置文本矩阵（第17-19行）。参数a和d定义比例因子，我们将使用它们来更改字体大小。使用参数c，我们引入一个偏移因子。最后，我们计算字符的坐标来确定参数e和f。现在确定了字符的确切位置，就使用showText()方法（第20行）显示字符。这种方法不会引入任何新的行。一旦我们完成了所有行中的所有字符的循环，我们用endText()方法关闭文本对象（第23行）。

如果你认为这个例子相当复杂，你是绝对正确的。我用它只是为了表明iText允许你以任何你想要的方式创建内容。如果在PDF中有可能，iText也可以。但是请放心，下面的例子会更容易理解。
### 总结
在本章中，我们一直在试验PDF操作符和操作数以及相应的iText方法。我们已经学习了一种称为图形状态的概念，用于跟踪诸如当前转换矩阵，线宽，颜色等属性。文本状态是图形状态的一个子集，涵盖了与文本相关的所有属性，如文本矩阵，文本的字体和大小，以及其他许多我们尚未讨论过的属性。我们将在另一个教程中详细介绍。

有人可能会问，为什么开发人员需要访问低级API，因为他知道iText中有很多高级功能。这个问题将在下一章回答。
