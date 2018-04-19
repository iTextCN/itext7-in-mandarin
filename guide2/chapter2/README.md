ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-2-adding-low-level-content](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/chapter-2-adding-low-level-content)

# **一些基本操作**

&nbsp;&nbsp;&nbsp;&nbsp;第一章的内容是介绍了一些基础的内容，本章介绍的内容则是一些更底层的东西。到后面的章节会涉及*操作现有pdf*的内容，希望大家耐心等待。

&nbsp;&nbsp;&nbsp;&nbsp;当我们谈论iText文档里面底层（low-level）的内容的时候，我们会参考被写入PDF官方文档里面的那些PDF语法。PDF定义的一系列的操作在iText中都有对应，例如``m``操作对应``moveTo()``方法，``l``操作对应``lineTo()``方法，``S``操作对应``stroke()``方法等等。通过这些方法，我们可以画出路径和形状。  

&nbsp;&nbsp;&nbsp;&nbsp;我们来看下面这个简单的例子：
```
-406 0 m
406 0 l
S
```
&nbsp;&nbsp;&nbsp;&nbsp;这段在pdf中的语法的含义就是：
1. 移动到坐标(-406,0)
2. 然后从(-406,0)画一条线到(406,0)，构成一条路径
3. 最后画出这一条线，stroke()对应的就是画出路径  

&nbsp;&nbsp;&nbsp;&nbsp;这段对应iText里面的操作就是这样的：
```
canvas.moveTo(-406, 0)
            .lineTo(406, 0)
            .stroke();
```
&nbsp;&nbsp;&nbsp;&nbsp;乍一看很简单，但是``canvas``这个对象是什么呢？让我们通过几个例子来找寻答案。

# **在pdf画坐标系**
## **在画布(canvas)上画线**

&nbsp;&nbsp;&nbsp;&nbsp;假如我们要创建如下图的pdf:  

![itext-2-1](http://obkwqzjnq.bkt.clouddn.com/itext-2-1.png)

&nbsp;&nbsp;&nbsp;&nbsp;这个例子在pdf上面画出了X和Y坐标系。让我们一步一步地解释这其中的过程：
```
PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
PageSize ps = PageSize.A4.rotate();
PdfPage page = pdf.addNewPage(ps);
PdfCanvas canvas = new PdfCanvas(page);
// Draw the axes画出坐标系
pdf.close();
```
- 我们不再使用``Document``这个对象
- 就像之前第一章所讲得一样，，我们创建了``PdfWriter``和``PdfDocument``对象。
- 我们没有像之前一样创建页面默认大小的``Document``对象，而是创建了特定``PageSize``的``PdfPage``
- 页面是A4大小，旋转过后变成了横向
- 创建完``PdfPage``以后，我们使用它创建了``PdfCanvas``
- 在``PdfCanvas``进行了一系列的操作，完成我们的坐标系的绘画
- 最后我们，我们想要在页面（page）中添加我们所绘画的内容，只需关闭``PdfDocument``对象即可

> 在之前的章节中，我们使用``document.close()``关闭``Document``对象，这个操作其实暗地里也关闭了``PdfDocument``对象。现在这里不再有``Document``对象，所以我们必须手动关闭``PdfDocument``

&nbsp;&nbsp;&nbsp;&nbsp;在PDF中，所有测量都以用户单位(user unit)完成。 默认情况下，一个用户单位对应一个点。 这意味着在一英寸(one inch)内有72个用户单位。 在PDF中，X轴指向右侧，Y轴指向上。 如果您使用```PageSize```对象创建页面大小，则坐标系的原点位于页面的左下角。 我们用作操作符（如m或l操作）的操作数的所有坐标都使用此坐标系。 我们可以通过改变当前的变换矩阵来改变坐标系。

## **坐标系统和变化矩阵**
&nbsp;&nbsp;&nbsp;&nbsp;如果你上过几何学的相关课程，那你应该就会知道我们可以通过一个变化矩阵来作用于对象，可以使之进行平移、旋转、缩放等操作。 假设我们要移动坐标系，使坐标系的原点位于页面正中间。在这种情况下，我们需要使用``concatMatrix()``方法的参数是：
```
canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
```
&nbsp;&nbsp;&nbsp;&nbsp;``concatMatrix()``方法的参数是一个3*3的变换矩阵：
```
a   b   0
c   d   0
e   f   1
```
&nbsp;&nbsp;&nbsp;&nbsp;在这里，这个矩阵的第三行的是固定值：（0,0,1），之所以是这样是因为我们是在二维里面操作。而``a``,``b``,``c``和``d``的值在这里可以用的值为来旋转、缩放和平移坐标系。当然我们没必要规定x轴必须水平，y轴垂直垂直，不过我们为了简单起见，我们就这么规定，所以规定``a``,``b``,``c``和``d``的值为``1``,``0``,``0``和``1``。``e``和``f``定义平移（translation）距离，在这里我们平移大小分笔试``ps``高度和宽度的1/2

## **图像状态(The graphics state)**

&nbsp;&nbsp;&nbsp;&nbsp;上一节提到变化矩阵是``Page``的图像状态之一，还有诸如线条宽度、画线颜色、填充颜色等状态。在接下来的章节中，我们会更加深入探讨其他一些图像状态。在这里我们只需知道：默认的线条宽度是1个用户单位并且默认的线条颜色是黑色。下面代码是画上图的过程：
```
//1.Draw X axis
canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
        .lineTo(ps.getWidth() / 2 - 15, 0)
        .stroke();
//2.Draw X axis arrow
canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
        .moveTo(ps.getWidth() / 2 - 25, -10)
        .lineTo(ps.getWidth() / 2 - 15, 0)
        .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
        .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
//3.Draw Y axis
canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
        .lineTo(0, ps.getHeight() / 2 - 15)
        .stroke();
//4.Draw Y axis arrow
canvas.saveState()
        .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
        .moveTo(-10, ps.getHeight() / 2 - 25)
        .lineTo(0, ps.getHeight() / 2 - 15)
        .lineTo(10, ps.getHeight() / 2 - 25).stroke()
        .restoreState();
//5.Draw X serif
for (int i = -((int) ps.getWidth() / 2 - 61);
    i < ((int) ps.getWidth() / 2 - 60); i += 40) {
    canvas.moveTo(i, 5).lineTo(i, -5);
}
//6.Draw Y serif
for (int j = -((int) ps.getHeight() / 2 - 57);
    j < ((int) ps.getHeight() / 2 - 56); j += 40) {
    canvas.moveTo(5, j).lineTo(-5, j);
}
canvas.stroke();
```

&nbsp;&nbsp;&nbsp;&nbsp;这段代码可以划分如下几部分：
* 1和3段代码大家应该很熟悉啦，就是画出x和y轴
* 第2段代码是画了箭头，也就是两条线连接在一起。交汇点的样式有很多：1）斜接，两线交接于一点 2）斜切，角是斜切的 3）圆形，角是圆的 我们通过调用一次``moveTo``和两次``lineTo``函数来构建路径，最后我们把交汇点的样式重置为默认值，这样不会影响之后的一些操作了，但是这并不是还原之前图像状态的最好的方法
* 第4段代码向我们展示了如果我们要改变图像状态更好的操作方式：1）我们用``saveState()``方法来保存当前的图像状态 2）然后改变图像状态，画线或者其他任意形状 3）最后我们使用``restoreState()``方法来还原原始的图像状态，所有在``saveState()``之后的改变图像状态的操作都会撤销，当你进行很多改变图像状态的操作时，这将会很有效
* 第5、6段代码我们每个40个用户单元就划一个分隔符，值得注意的是，我们并没有立马就调用``stroke()``函数，而是等所有所有路径都画好以后，我们再调用函数来画。

&nbsp;&nbsp;&nbsp;&nbsp;当然不止这么一种方式在画布上画线和形状，考虑到pdf生成的速度、生成文件的大小和在视图里面渲染的速度，我们很难讨论上述这种方法是好还是坏的，这在我们以后章节会讨论。

> 在这里注意``saveState()``和``restoreState()``必须成对出现，而且``restoreState()``不能先于``savaState()``

## **添加网格线**
&nbsp;&nbsp;&nbsp;&nbsp;现在我们在之前的图片的基础上，更改线的宽度，增加一个虚线，添加不同颜色的网格线，形成如下图所示：
![](http://obkwqzjnq.bkt.clouddn.com/itext-2-2.png)
&nbsp;&nbsp;&nbsp;&nbsp;在这个例子中，我们首先定义一系列的``Color``对象：
```
Color grayColor = new DeviceCmyk(0.f, 0.f, 0.f, 0.875f);
Color greenColor = new DeviceCmyk(1.f, 0.f, 1.f, 0.176f);
Color blueColor = new DeviceCmyk(1.f, 0.156f, 0.f, 0.118f);
```
&nbsp;&nbsp;&nbsp;&nbsp;在PDF官方文档(ISO-32000) 中，定义了很多的颜色空间，不同的颜色空间在iText中对应着不同的``class``,最常用的颜色空间是``DeviceGray``（灰度空间，只需一个亮度参数），``DeviceRgb``（RGB空间，有红色、绿色和蓝色决定）和``DeviceCmyk``（印刷四色空间，由青色、品红、黄色和黑色），在这个例子中，我们使用的是``DeviceCmyk``空间。

> 注意，我们使用的不是来自``java.awt.Color``定义的颜色，而是来自iText的``Color``的类，可以在``com.itextpdf.kernel.color``包中找到。

&nbsp;&nbsp;&nbsp;&nbsp;如果我们想要画蓝色的网格线，可以使用如下代码：
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
&nbsp;&nbsp;&nbsp;&nbsp;在一开始，我们设置线条的宽度为0.5用户单位，接下来就是画路线，最后调用``stroke()``函数。  
&nbsp;&nbsp;&nbsp;&nbsp;画坐标的话，我们只用之前的方法来画，不过在画之前，我们改变线条宽度和画笔颜色。
```
canvas.setLineWidth(3).setStrokeColor(grayColor);
```
&nbsp;&nbsp;&nbsp;&nbsp;画完坐标系后，我们用2个用户宽度来画虚线：
```
canvas.setLineWidth(2).setStrokeColor(greenColor)
        .setLineDash(10, 10, 8)
        .moveTo(-(ps.getWidth() / 2 - 15), -(ps.getHeight() / 2 - 15))
        .lineTo(ps.getWidth() / 2 - 15, ps.getHeight() / 2 - 15).stroke();
```
&nbsp;&nbsp;&nbsp;&nbsp;可以有很多变量来定义一个虚线(line dash)，但是在本例中，我们只需三个变量，实线(dash)的长度为10个用户单位，实线之间的间隙为10个用户单位，相位(phase)为8个用户单位（相位是在实线模块中定义实线的开始？这段翻译不是特别清楚，原文：the phase is 8 user units —the phase defines the distance in the dash pattern to start the dash.）

> 我们可以试试在``PdfCanvas``中的其他方法，例如``curveTo()``函数是用来画曲线的，``rectangle()``方法是用来画矩形的，以及还有一些其他的方法。除了用``stroke``方法来画线以后，我们还可以用``fill()``方法填充路径。``PdfCanvas``类中提供的方法远远多于java版本的PDF操作器，并且也提供了在PDF中没有的构造路径的方法，例如椭圆和圆形

&nbsp;&nbsp;&nbsp;&nbsp;下一节我们会讨论一下图像状态中可以改变文字的绝对位置的那一部分

# **文字状态**

&nbsp;&nbsp;&nbsp;&nbsp;下图展示了星球大战5帝国反击战的开头的文字： 

![itext2-3](http://obkwqzjnq.bkt.clouddn.com/itext2-3.png)  


&nbsp;&nbsp;&nbsp;&nbsp;如果想要创建这样一个pdf，最好的方式是创建不同对齐方式的``Paragraph ``对象，主题居中，内容左对齐，然后把``Paragraph ``对象添加到``Document``中。在高级的api(high-level approach)中会把连续的文字分成很多段，当文字长度超过页面宽度会引入换行符，当文字内容超出页面高度时会换页。  
&nbsp;&nbsp;&nbsp;&nbsp;当然我们有更简单方式，使用低级的方式(low-level approach)，我们只要把文字分解成几块就行了：
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
&nbsp;&nbsp;&nbsp;&nbsp;出于简便的原因，我们把原来在左下角的坐标系放到左上角来，然后我们使用``beginText()``方法来创建文本对象，并改变文本的状态：
```
canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
canvas.beginText()
    .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 14)
    .setLeading(14 * 1.2f)
    .moveText(70, -40);
```
* 我们改变文本状态为等宽粗体和改变字体大小为14，这样以后的字体都为这个格式
* 间距为字体大小的1.2倍
* 最后，我们向右移动70个用户单位，向下移动40个用户单位，也就是(70,-40)开始显示文字


&nbsp;&nbsp;&nbsp;&nbsp;紧接着，我们一个一个遍历text数组里面的string，每个string另一起行，保持上述的间距，最后使用``endText()``方法结束。
```
for (String s : text) {
    //Add text and move to the next line
    canvas.newlineShowText(s);
}
canvas.endText();
```

> 注意：我们使用``newlineShowText()``必须在``beginText()``和``endText()``之间，并且``beginText()``---》``endText()``之间顺序不能打乱

# **炫酷的文字**
&nbsp;&nbsp;&nbsp;&nbsp;先看看如下的效果： 

![itext-2-4](http://obkwqzjnq.bkt.clouddn.com/itext2-4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;是不是很酷炫？其实很简单的，我们一步一步来走：
```
canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
        .setColor(Color.BLACK, true)
        .fill();
```
&nbsp;&nbsp;&nbsp;&nbsp;我们首先创建一个矩形，矩形的左下角的坐标为(0,0)，宽度和高度为页面的宽度和高度，然后我们设置填充颜色为黑色，当然我们可以使用``setFillColor(Color.BLACK)``来设置填充颜色，但这里我们使用更通用的``setColor()``方法。``setColor()``的第二个参数为是否改变填充颜色，最后我们填充这个矩形就OK 了，然后就是文字部分了：
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
&nbsp;&nbsp;&nbsp;&nbsp;像之前一样，我们再次把坐标系放在左上角，并使用Cmyk空间定义了黄色，定义了行距和y轴一开始的位置,然后开始添加文字，我们使用等宽粗体并定义了字体的大小为1用户单位，虽然只有1用户单位，但是我们会在后面通过文本矩阵的方式来放大文本，在里面我们不会用``setLeading()``来设置行距，因为我们没有使用``newlineShowText()``方法。我们设置好文字颜色，然后一个一个文字显示，而不是之前整行画。

> 每个字符的图像在字体里面定义为是画好的路径，默认情况下，这些字符的路径都是被填充的，这就是为什么我们设置fill的color能改变字体颜色的原因


&nbsp;&nbsp;&nbsp;&nbsp;我们开始循环文本，将每行读入一个String， 我们需要一系列数学变量来定义将用于定位每个字形的文本矩阵的不同元素： 我们为每一行定义一个``xOffset``变量来决定当前行的文字的起始位置，字体大小被定义为1个用户单位，但是我们将它与一个``fontSizeCoeff``相乘，这取决于文本数组中行的索引。同事我们还将定义yOffset来决定每一行的起始位置。  
&nbsp;&nbsp;&nbsp;&nbsp;计算每行中的字符数，然后循环所有字符， 我们根据字符在行中的位置定义一个angle变量,charOffset变量取决于行的索引和字符的位置。  
&nbsp;&nbsp;&nbsp;&nbsp;最后，设置文本的变换矩阵，``a``和``d``定义了缩放比例，``c``参数定义了倾斜程度，然后计算字符的坐标来定义``e``和``f``参数，每个字符的位置确定以后，使用``showText()``函数来显示字符。这个方法不会另起一行来显示字符，我们通过循环的方式来另起一行，最后使用``endext()``来关闭text对象

> 这个例子很复杂，但是通过这个例子我们可以看出，我们可以创建任意内容，只要在PDF可以做到，那么iText也可以做到。但请放心,以后的例子将更容易理解

# **总结**
&nbsp;&nbsp;&nbsp;&nbsp;在本章，我们一直在尝试着在PDF里面的各种操作，并在iText里面的进行相应的操作。我们已经学到了一种称为图形状态的概念，它拥有当前的变换矩阵，线宽，颜色等属性。文本状态是涵盖与文本相关的所有属性的图形状态的子集，例如文本矩阵，文本的字体和大小以及我们尚未讨论的许多其他属性。 我们将在另一个教程中详细介绍。你可能会想知道为什么开发人员需要访问低级API，而不是使用iText的很多高级功能， 这个问题将在下一章回答。