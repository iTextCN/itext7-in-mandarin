ready to translate : [https://developers.itextpdf.com/content/itext-7-building-blocks/we-start-overview-classes-and-interfaces](https://developers.itextpdf.com/content/itext-7-building-blocks/we-start-overview-classes-and-interfaces)

&nbsp;&nbsp;&nbsp;&nbsp;这系列开始全面讲述iText7里面的构建块（blocks）以及一些其他应用。

> 本系列也是翻译自iText的官网，同时也会添加自己个人的见解，如果大家有什么不明白的，可以去官网查看 

# 总的接口

&nbsp;&nbsp;&nbsp;&nbsp;当我们谈及iText7里面的基础的构建块（就是一些页面元素），首先会提到那些实现``IElement``接口的所有的类，iText7首先是用Java写的，然后移植到了C#。iText7官方为了开发者的方便，尤其针对C#开发人员，每个类接口的名字都以字母``I``开头。  

&nbsp;&nbsp;&nbsp;&nbsp;下图宏观展示了``IElemeent``和其他接口的关系：  

![itext-h-1](http://obkwqzjnq.bkt.clouddn.com/itext-h-1.png)  

&nbsp;&nbsp;&nbsp;&nbsp;在整个层次的最高层，我们可以看到``IPropertyContainer``接口。这个接口定义了方法来set,get和delete属性。这个接口有两个直接子接口：``IElement``和``IRenderr``。``IElement``接口最终是被一些类实现的，例如``Text``，``Paragraph``和``Table``。这些类的对象会被直接或者间接地被加入一个documnt。而``IRenderer``接口最终会被另外一些类实现，例如``TextRender``，``ParagraphRenderer``和``TableRender``，当我们想调整一个对象的呈现方式，这些renderers会被iText内部使用。  

&nbsp;&nbsp;&nbsp;&nbsp;``IElement``接口两个直接子接口。``ILeafElement``接口会被那些不能包含其他元素的构造块元素实现，例如，你可以添加一个``Text``和一个``Image``元素添加到``Paragraph``对象，但是你不可以添加任何元素到一个``Text``或者``Image``元素中。所以``Text``和``Image``元素是实现``ILeafElement``接口的。最后，这里的``ILeafElement``接口是允许你在加入所有内容之前渲染这个对象，这么做得好处就是能减少内存使用：在加入所有内容之前，那些已经加入表格的内容可以被渲染并且从内存中清除。  

# 实现接口的抽象类

&nbsp;&nbsp;&nbsp;&nbsp;``IPropertyContainer``接口被抽象类``ElementPropertyContainer``实现。这个类有三个直接子类，如下图：  

![itext-h-2](http://obkwqzjnq.bkt.clouddn.com/itext-h-2.png)  

&nbsp;&nbsp;&nbsp;&nbsp;``Style``类是一个有很多样式属性的容器，例如外边距、内边距和旋转属性。它从``ElementPropertyContainer``抽象类继承一些样式属性，例如：宽度、高度、颜色、边框和对齐方式。  

&nbsp;&nbsp;&nbsp;&nbsp;``RootElement``类定义了方法来添加添加内容，要么使用``add()``方法，要么使用``showTextAligned()``方法。``Document``对象会把内容添加到一个页面中。而``Canvas``对象并不会知道页面Page的概念。``RootElement``相当于充当了高级布局API（``Document``）和低级布局API(``Canvas``)之间的桥梁和过渡。  

&nbsp;&nbsp;&nbsp;&nbsp;下图宏观展示了``AbstractElement``接口的实现类：  

![itext-h-3](http://obkwqzjnq.bkt.clouddn.com/itext-h-3.png)  

> 这张图和下面一张图都比较模糊，官网上给的图都很模糊，没办法，等以后有空重新画一下吧>_<，可以看看文字解释

&nbsp;&nbsp;&nbsp;&nbsp;所有继承``AbstractElement``类都要实现``IElement``接口。``Text``,``Image``,``Tab``和``Link``实现``ILeafElement``接口。``ILargeElement``接口只会被``Table``类实现。上述的这些类的使用能让可以更加容易创建带有标签的PDF。  

> 还记得我在第七章提到的PDF/A level A吗，有标签的PDF的PDF在那一章有提及，大家忘记了可以去看看

&nbsp;&nbsp;&nbsp;&nbsp;当我们使用``setTAgged()``方法来创建一个``PdfDocument``是，所有实现``IAcesssibleElement``接口的元素的语义信息都会被加入到省城的PDF文件中。iText会创建一个结构树，这个结构树里面``Table``被标记为一个table,``List``被标记为一个list,其余元素都会正确的带有标签，这些元素包括：``Text``、``Link``、``Image``、``Paragraph``、``Div``、``List``、``ListItem``、``Cell``和``LineSeparator``。``Tab``和``AreaBreak``对象不会实现``IAcesssibleElement``接口，因为他们没有实际的内容并且空格也没有任何语义。  

# IRender的实现类

&nbsp;&nbsp;&nbsp;&nbsp;iText会使用正确的s``IRender``来渲染文档里面的内容。下图给出了``IRender``接口的实现的类：  

![itext-h-4](http://obkwqzjnq.bkt.clouddn.com/itext-h-4.png)  

&nbsp;&nbsp;&nbsp;&nbsp;我们来比较这张图和上张图，我们会发现每个``AbstractElement``和每个``RootElement``都有对应的渲染器，在这里我们不会深入讨论每一个渲染器，在以后的例子中，我们涉及到这些渲染器，到时候再提