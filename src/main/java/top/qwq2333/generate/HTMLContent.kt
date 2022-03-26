/*
 * EPUB Generator
 * Copyright (C) 2022 qwq233 qwq233@qwq2333.top
 * https://github.com/qwq233/epub-generator
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by qwq233.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/qwq233/qwq233/blob/master/eula.md>.
 */

package top.qwq2333.generate

import com.vladsch.flexmark.ext.footnotes.FootnoteExtension
import com.vladsch.flexmark.ext.tables.TablesExtension
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.parser.ParserEmulationProfile
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.DataHolder
import com.vladsch.flexmark.util.data.MutableDataSet
import org.dom4j.Document
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultDocumentType
import org.jetbrains.kotlin.konan.file.File
import top.qwq2333.config.data.Config
import top.qwq2333.config.data.Content
import top.qwq2333.util.Defines
import top.qwq2333.util.FileUtils
import top.qwq2333.util.Utils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException


object HTMLContent {

    fun process(
        cfg: Config,
        contents: List<Content>,
        targetPath: String,
        sourcePath: String,
        document: Document
    ) {
        val metadata = cfg.metadata
        for (content in contents) {
            var output: String
            when (content.type) {
                Defines.subContent ->
                    process(cfg, content.content!!, targetPath, sourcePath, document)
                Defines.image -> {
                    val fileExtension = content.path!!.split(File.pathSeparator).last().split(".").last()
                    output = FileUtils.convertInputStreamToString(
                        this::class.java.classLoader.getResource("Text/image.xhtml")!!.openStream()
                    ).replace("LANGUAGE", metadata.language).replace("TITLE", content.title!!).replace(
                        "PATH", "${content.id}.${fileExtension}"
                    )

                    FileUtils.write(
                        "$targetPath/${Defines.mainFolder}/Images/${content.id}.$fileExtension",
                        FileInputStream("$sourcePath/${content.path}")
                    )
                    FileUtils.write("$targetPath/${Defines.mainFolder}/Text/${content.id}.xhtml", output)
                }
                Defines.text -> {
                    output =
                        convertMdToHtml("$sourcePath/${content.path!!}", content.title!!, targetPath, document, cfg)
                    FileUtils.write("$targetPath/${Defines.mainFolder}/Text/${content.id}.xhtml", output)
                }
            }
        }

    }

    fun genCover(targetPath: String, sourcePath: String, metadata: top.qwq2333.config.data.Metadata) {
        val fileExtension = metadata.cover.image.split(File.pathSeparator).last().split(".").last()
        FileUtils.write(
            "$targetPath/${Defines.mainFolder}/Images/cover.$fileExtension",
            FileInputStream("${sourcePath}/${metadata.cover.image}")
        )


        val output = FileUtils.convertInputStreamToString(
            this::class.java.classLoader.getResource("Text/image.xhtml")!!.openStream()
        ).replace("LANGUAGE", metadata.language).replace("TITLE", "Cover").replace(
            "PATH", "cover.${fileExtension}"
        )


        FileUtils.write("$targetPath/${Defines.mainFolder}/Text/cover.xhtml", output)
    }

    fun genToC(string: String) = Defines.contentFull(string)

    fun genToCElement(list: List<Content>): String {
        val sb = StringBuilder()
        for (content in list) {
            if (content.hiddenInContent) {
                continue
            }
            if (content.type == Defines.subContent) {
                sb.append(genToCElement(content.content!!))
            }
            if (content.type != Defines.subContent) {
                sb.append(Defines.contentElement("${content.id}.xhtml", content.title!!))
            }
        }
        return sb.toString()
    }

    private fun convertMdToHtml(
        path: String, title: String, targetPath: String, document: Document, cfg: Config
    ): String {
        val content = FileUtils.read(path)


        var target = String()
        this::class.java.classLoader.getResource("Text/template.xhtml")!!.openStream().let {
            if (it != null) {
                target = FileUtils.convertInputStreamToString(it)
            }
        }

        val options: DataHolder = MutableDataSet().setFrom(ParserEmulationProfile.MULTI_MARKDOWN).set(
            Parser.EXTENSIONS, listOf(
                FootnoteExtension.create(), TablesExtension.create()
            )
        ).toImmutable()

        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val node: Node = parser.parse(content)
        val output = renderer.render(node)

        target = target.replace("TITLE", title)
        target = target.replace("TARGET", output)

        val reader = SAXReader()
        val html: Document = reader.read(ByteArrayInputStream(target.toByteArray()))
        val type = DefaultDocumentType(
            html.rootElement.name, "-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"
        )
        html.docType = type
        html.rootElement.addNamespace("epub", "http://www.idpf.org/2007/ops")
        html.rootElement.addNamespace("xml", "http://www.w3.org/XML/1998/namespace")
        html.rootElement.element("body").element("div").elements().forEach { it ->
            it.elements().forEach { element ->
                if (element.name == "img") {
                    if (Utils.isURL(element.attribute("src").value)) {
                        throw UnsupportedOperationException("It don't support the image from internet.")
                    }
                    val value = element.attribute("src").value
                    val sourceFile = File("${File(path).parent}/${value}")
                    if (!sourceFile.isFile) {
                        throw IOException("Image not found. Please check markdown file.")
                    }
                    val fileName = sourceFile.path.split(File.separator).last()
                    FileUtils.write(
                        "$targetPath/OEBPS/Images/$fileName", FileInputStream(sourceFile.path)
                    )
                    element.attribute("src").value = "../Images/${sourceFile.path.split(File.separator).last()}"

                    val item = document.rootElement.element("manifest").addElement("item")
                    item.addAttribute("id", "image-${fileName.split(".")[0]}")
                    item.addAttribute("href", "Images/${sourceFile.path.split(File.separator).last()}")

                    when (val extension = fileName.split(".")[1]) {
                        "jpg" -> item.addAttribute("media-type", "image/jpeg")
                        "png" -> item.addAttribute("media-type", "image/png")
                        else -> item.addAttribute("media-type", "image/$extension")
                    }
                }
                if (element.name == "a") {
                    val link = element.attribute("href").value
                    if (!Utils.isURL(link)) {
                        val fileName = link.split(File.separator).last()
                        var isFound = false
                        cfg.content.forEach { content ->
                            if (content.type == Defines.text) {
                                if (content.path!!.contains(fileName)) {
                                    element.attribute("href").value = "./${content.id}.xhtml"
                                    isFound = true
                                }
                            }
                        }
                        if (!isFound) {
                            println(
                                "WARN: file $fileName is not defined in config.yml or it's a url,\n" +
                                    "so it will not link to any file."
                            )
                        }
                    }
                }
            }
        }

        val result = ByteArrayOutputStream()
        val writer = XMLWriter(result, OutputFormat.createPrettyPrint())
        writer.write(html)

        target = String(result.toByteArray())

        if (cfg.metadata.customDeliverLine.enable) {
            if (cfg.metadata.customDeliverLine.type == Defines.image) {
                target = target.replace(
                    "<hr/>",
                    Defines.deliverLineImage(Utils.fileExtension(cfg.metadata.customDeliverLine.content))
                )
            } else {
                target = target.replace("<hr/>", Defines.textHTML(cfg.metadata.customDeliverLine.content))
            }
        }

        return target
    }
}
