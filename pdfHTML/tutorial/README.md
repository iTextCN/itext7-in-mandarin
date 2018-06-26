&emsp;&emsp;Hello，everyone！好久不见，我又准备开新坑啦！旧坑基础块的介绍也同时更新，请大家放心，废话不多说，让我们赶紧开始吧。

# 整体介绍

&emsp;&emsp;在本次教程中，我们将会学习如何使用``pdfHTML``来把HTML转换成PDF，这是iText 7的一个插件，如果你刚开始接触iText，那么可以直接跳跃到第一章。如果你之前使用过iText，那你可能记得旧的HTML转PDF的函数，那么你可以使用过废弃的``HTMLWorker``(iText 2)或者老旧的XML Worker插件(iText 5)。  

&emsp;&emsp;``HTMLWorker``类在很多年之前就被弃用了，``HTMLWorker``的目标就是把少量简单的HTML代码段转换成iText对象。它设计的初衷就不是用来把完整的HTML页面转换成PDF，虽然很多的开发者尝试用来这么干，但是这将导致大量的错误，因为``HTMLWorker``并不支持每个HTML标签，不能解析CSS文件，还有其他一些不足。为了避免这些错误，iText的近期的发布版本中把``HTMLWorker``移除了。  

&emsp;&emsp;在2011年，iText Group在iText5的基础上发布了XML Worker当做一种通用的XML转成PDF的工具。它提供了一种默认实现，它能把XHTML(数据)和CSS(样式)转换成PDF，把HTML标签例如``<p>``，``<img>``和``<li>``转换成iText5里面的对象例如``Paragraph``，``Image``和``ListItem``。我们并不知道其他用XML Worker来解析其他XML格式的文件，但是很多开发者使用XML Worker和[jsoup](https://jsoup.org/)一起来当做HTML2PDF的转换器。  

&emsp;&emsp;但是XML Worker并不是一个URL2PDF的工具。XML Worke希望的文件的是那种专门为转换成PDF的单一HTML文件。一种常见的用法是发票的创建。为了方便，开发者们不会使用Java或者C#编译语言来设计发票，他们更喜欢创建一个HTML模板定义文档的结构，和一些CSS文件来定义样式，然后使用数据来填充HTML文件，并且使用XML Worker来创建发票的PDF文档，最后抛弃原始的HTML文件。我们会在第4章进一步关注这个用户用例，使用XSLT在内存中转换XML成HTML，然后使用``pdfTHML``插件来转换HTML成PDF。  

&emsp;&emsp;当iText 5一开始被创建的时候，它设计的目标是当做一款能尽快产生PDF的工具，当页面完成的时候刷新页面元素到``OutputStream``。在iText第一次发布的时候，有几种的设计选择表现得很不错，这些设计理念在16年之后还存在iText 5中。但是，不幸的是，这些设计选择中的一部分会让XML Worker扩展它的功能来让开发者满意变得很困难。如果我们想要把一个功能强大的HTML转换PDF的转换器，那我们必须从头到尾重写iText，这也是我们所做的。  

&emsp;&emsp;在2016年，我们发布了iText 7，这是一个全新的iText版本，它不再与之前的版本兼容，但是它考虑到了pdfHTML。许多的工作被花费到新的``Renderer``框架。当一个文档使用iText7创建，它的渲染器和子渲染器的树形结构会被构建。通过遍历这课树来创建页面布局，这种方式在处理HTML转换成PDF的时候更加合适。同时iText对象重新设计来更好匹配HTML标签并且可以使用CSS的方式来定义样式。  

&emsp;&emsp;几个例子，在iText 5，你有一个``PdfTable``和一个``PdfCell``对象来创建一个表格和它的单元格。如果你想要每个单元格的字体不同于默认字体，你需要为每个单元格设置字体。在Text 7中，你有一个``Table``和``Cell``对象，当你设置区别于默认字体的字体到整个表格的时候，每个单元格将会继承整个字体。这是在架构设计方面重要的一步，尤其是目标是转换HTML到PDF。  

&emsp;&emsp;总之，让我们不要停留在过去，让我们看看pdfHTML能为我们做些什么。在第一章节，我们会关注``converToPdf()``方法一些变更题，然后我们会发现转换器如果被配置。总体章节架构如下：  

- 章节1：Hello HTML To PF
- 章节2：使用CSS定义样式
- 章节3：使用媒体查询创建PDF
- 章节4：使用pdfHTML创建报表
- 章节5：自定义标签工作体和CSS填充器
- 章节6：在pdfHTML中使用字体
- 章节7：关于pdfHTML常见的问题