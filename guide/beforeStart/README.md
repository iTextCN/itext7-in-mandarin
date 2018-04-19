ready to translate : [https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/installing-itext-7](https://developers.itextpdf.com/content/itext-7-jump-start-tutorial/installing-itext-7)

## 准备工作：安装iText 7
本 教 程 中 介 绍 的 所有 示 例 均 可 在 我 们 的 网 站 上 通 过以下链接访问：
[http://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples](http://developers.itextpdf.com/content/itext-7-jump-start-tutorial/examples)

在我们开始使用iText 7之前，我们需要先安装关键的iText  jars。实现这一点最好的方法是从中央Maven仓库导入iText jars。我们已经做了一些简单的视频解释如何使用不同的IDE来做到这一点：
* [如何在Eclipse中导入iText 7来创建Hello World PDF？](https://www.youtube.com/watch?v=sxArv-GskLc&)
* [如何在Netbeans中导入iText 7来创建HelloWorld PDF？](https://www.youtube.com/watch?v=VcOi99zW7O4)
* [如何在IntelliJ IDEA中导入iText 7来创建Hello World PDF？](https://www.youtube.com/watch?v=6WxITuCgpHQ)

在这些教程中，我们只将内核和布局项目定义为依赖关系。Maven也会自动导入io jar，因为内核包依赖于io包。

如果要运行本教程中的所有示例，则需要定义完整的依赖关系列表：

```
<dependencies>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>kernel</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>io</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>layout</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>forms</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>pdfa</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>com.itextpdf</groupId>
        <artifactId>pdftest</artifactId>
        <version>7.0.4</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.18</version>
    </dependency>
</dependencies>
```
每个依赖对应于Java中的jar和C＃中的DLL。

* 内核和io：包含低级功能。
* 布局：包含高级功能。
* 形式：所有AcroForm示例都需要。
* pdfa：PDF / A特定功能所需的。
* pdftest：也是一个测试的例子所需要的。

在本教程中，我们不会使用以下可用的模块：
* 条码：如果你想创建条码，使用这个。
* hyph：如果你想让文本被连字符号连接，可以使用这个。
* font-asian：使用你需要的CJK功能（中文/日文/韩文）
* 签名：如果你需要数字签名支持，请使用此选项。

上面列出的所有jar包都可以根据AGPL许可证获得。额外的iText 7功能可以通过附加组件获得，这些附加组件通过商业授权以jar包形式交付。如果你想要使用这些附件中的任何一个，或者如果你要使用iText 7和你的专有代码，则需要获取iText 7的商业许可证密钥（请参阅[我们网站的法律部分](https://itextpdf.com/legal)）。

你可以使用许可证密钥模块导入这类许可证密钥。为了能够下载这个JAR包，你需要添加一个仓库到你的仓库文件中的仓库节点。这是必要的，因为许可证密钥存储库是封闭源，因此在Maven Central上不可用。
```
<repositories>
    <repository>
        <id>itext</id>
        <name>iText Repository - releases</name>
        <url>https://repo.itextsupport.com/releases</url>
    </repository>
</repositories>
```
你可以通过添加如下所示的依赖项来获取许可证密钥jar：
```
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-licensekey</artifactId>
    <version>2.0.4</version>
</dependency>
```
iText中的一些功能是封闭的源代码。例如，如果你想使用**PdfCalligraph**，你需要排版模块。没有官方许可证密钥，此模块将无法使用。

