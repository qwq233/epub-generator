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

import org.dom4j.DocumentHelper
import org.dom4j.Element
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultDocumentType
import org.jetbrains.kotlin.konan.file.File
import top.qwq2333.config.data.Config
import top.qwq2333.config.data.Content
import top.qwq2333.util.Defines
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.UUID

object XmlContent {
    private var depth = 1
    private var currentDepth = 1
    private var order = 1
    private val uuid = UUID.randomUUID()

    /**
     * Generate content.opf
     * @param cfg config
     * @return xml content to write to file
     */
    fun genContent(cfg: Config): String {
        val target = DocumentHelper.createDocument()
        val rootElement = target.addElement("package", "http://www.idpf.org/2007/opf")
        rootElement.addAttribute("version", "2.0")
            .addAttribute("unique-identifier", "BookId")

        val metadata = rootElement.addElement("metadata")
            .addNamespace("opf", "http://www.idpf.org/2007/opf")
            .addNamespace("dc", "http://purl.org/dc/elements/1.1/")
        metadata.addElement("dc:title").text = cfg.metadata.title
        metadata.addElement("dc:creator")
            .addAttribute("opf:role", "aut")
            .addAttribute("opf:file-as", cfg.metadata.author)
            .text = cfg.metadata.author
        metadata.addElement("dc:date")
            .text =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+08:00'").format(
                SimpleDateFormat("yyyy-M-d").parse(cfg.metadata.date)
            )
        metadata.addElement("dc:rights").text = cfg.metadata.rights
        metadata.addElement("dc:identifier")
            .addAttribute("id", "BookId")
            .addAttribute("opf:scheme", "UUID")
            .text = "urn:uuid:${uuid}"
        metadata.addElement("dc:identifier")
            .addAttribute("opf:scheme", "calibre")
            .text = "$uuid"
        metadata.addElement("language")
            .text = cfg.metadata.language
        metadata.addElement("meta")
            .addAttribute("name", "Sigil version")
            .addAttribute("content", "1.7.0")
        metadata.addElement("meta")
            .addAttribute("name", "calibre:title_sort")
            .addAttribute("content", cfg.metadata.title)
        metadata.addElement("meta")
            .addAttribute("name", "calibre:author_link_map")
            .addAttribute("content", "{&quot;${URLEncoder.encode(cfg.metadata.author, "UTF-8")}&quot;: &quot;&quot;}")
        if (cfg.metadata.cover.hasCover) {
            metadata.addElement("meta")
                .addAttribute("name", "cover")
                .addAttribute(
                    "content",
                    "cover"
                )
        }

        val manifest = rootElement.addElement("manifest")
        manifest.addElement("item")
            .addAttribute("id", "ncx")
            .addAttribute("media-type", "application/x-dtbncx+xml")
            .addAttribute("href", "toc.ncx")
        manifest.addElement("item")
            .addAttribute("id", "minetype")
            .addAttribute("href", "../minetype")
            .addAttribute("media-type", "application/octet-stream")
        manifest.addElement("item")
            .addAttribute("id", "css")
            .addAttribute("href", "Styles/style.css")
            .addAttribute("media-type", "text/css")


        val itemList: MutableList<String> = mutableListOf()

        if (cfg.metadata.cover.hasCover) {
            val extension = cfg.metadata.cover.image.split(File.pathSeparator).last().split(".").last()

            val item = manifest.addElement("item")
            item.addAttribute("id", "cover")
                .addAttribute("href", "Images/cover.$extension")

            val item2 = manifest.addElement("item")
            item2.addAttribute("id", "cover-xhtml")
                .addAttribute("href", "Text/cover.xhtml")
                .addAttribute("media-type", "application/xhtml+xml")
            itemList.add("cover-xhtml")

            when (extension) {
                "jpg" -> item.addAttribute("media-type", "image/jpeg")
                "png" -> item.addAttribute("media-type", "image/png")
                else -> item.addAttribute("media-type", "image/$extension")
            }
        }

        listOf("author.ttf", "KaiGenGothicTC-Heavy.ttf", "title.ttf").forEach {
            manifest.addElement("item")
                .addAttribute("id", it)
                .addAttribute("media-type", "application/x-font-truetype")
                .addAttribute("href", "Fonts/$it")
        }
        manifest.addElement("item")
            .addAttribute("id", "contents")
            .addAttribute("href", "Text/contents.xhtml")
            .addAttribute("media-type", "application/xhtml+xml")

        genManifestElement(manifest, cfg.content, itemList)

        val spine = rootElement.addElement("spine").addAttribute("toc", "ncx")
        itemList.forEach {
            spine.addElement("itemref")
                .addAttribute("idref", it)
        }
        spine.addElement("itemref")
            .addAttribute("idref", "contents")

        val guide = rootElement.addElement("guide")
        guide.addElement("reference")
            .addAttribute("type", "toc")
            .addAttribute("title", "Table of Contents")
            .addAttribute("href", "Text/contents.xhtml")
        if (cfg.metadata.cover.hasCover) {
            guide.addElement("reference")
                .addAttribute("type", "cover")
                .addAttribute("title", "Cover")
                .addAttribute("href", "Text/cover.xhtml")
        }

        val output = ByteArrayOutputStream()
        val writer = XMLWriter(output, OutputFormat.createPrettyPrint())
        writer.write(target)

        return String(output.toByteArray())
    }

    /**
     * Generate toc.ncx
     * @param cfg config
     * @return xml content to write to file
     */
    fun genTableOfContent(cfg: Config): String {
        val target = DocumentHelper.createDocument()
        val rootElement = target.addElement("ncx")
        target.docType = DefaultDocumentType(
            "ncx",
            "-//NISO//DTD ncx 2005-1//EN",
            "http://www.daisy.org/z3986/2005/ncx-2005-1.dtd"
        )

        rootElement.addAttribute("xmlns", "http://www.daisy.org/z3986/2005/ncx/")
        rootElement.addAttribute("version", "2005-1")
        rootElement.addAttribute("xml:lang", cfg.metadata.language)

        val metadata = rootElement.addElement("head")
        checkDepth(cfg.content)
        metadata.addElement("meta")
            .addAttribute("name", "dtb:uid")
            .addAttribute("content", "urn:uuid:${uuid}")
        metadata.addElement("meta")
            .addAttribute("name", "dtb:depth")
            .addAttribute("content", "$depth")
        metadata.addElement("meta")
            .addAttribute("name", "dtb:generator")
            .addAttribute("content", "EPUB Generator by James Clef")
        metadata.addElement("meta")
            .addAttribute("name", "dtb:totalPageCount")
            .addAttribute("content", "0")
        metadata.addElement("meta")
            .addAttribute("name", "dtb:maxPageNumber")
            .addAttribute("content", "0")

        rootElement.addElement("docTitle").addElement("text").text = cfg.metadata.title

        val navMap = rootElement.addElement("navMap")
        genNavPoint(cfg.content, navMap)

        val output = ByteArrayOutputStream()
        val writer = XMLWriter(output, OutputFormat.createPrettyPrint())
        writer.write(target)

        return String(output.toByteArray())
    }

    /**
     * Check Max Content Depth.
     * @param contents [Config][contents]
     */
    private fun checkDepth(contents: List<Content>) {
        contents.forEach {
            if (it.type == Defines.subcontent) {
                currentDepth++
                if (currentDepth > depth)
                    depth = currentDepth
                checkDepth(it.content!!)
            } else {
                currentDepth = 1
            }
        }
    }

    private fun genNavPoint(contents: List<Content>, parentElement: Element) {
        for (content in contents) {
            if (content.hiddenInContent) {
                continue
            }
            val target = parentElement.addElement("navPoint")
                .addAttribute("id", "num_${order}")
                .addAttribute("playOrder", "${order++}")
            if (content.type == Defines.subcontent) {
                target.addElement("navLabel").addElement("text").text = content.title!!
                target.addElement("content").addAttribute("src", "Text/${content.content!![0].id}.xhtml")
                genNavPoint(content.content, target)
            } else {
                target.addElement("navLabel").addElement("text").text = content.title!!
                target.addElement("content").addAttribute("src", "Text/${content.id}.xhtml")
            }
        }
    }

    fun genManifestElement(manifest: Element, contents: List<Content>, itemList: MutableList<String>) {
        contents.forEach {
            if (it.type != Defines.subcontent) {
                val item = manifest.addElement("item")
                item.addAttribute("id", it.id)
                if (it.type == Defines.text) {
                    itemList.add(it.id)
                    item.addAttribute("media-type", "application/xhtml+xml")
                    item.addAttribute("href", "Text/${it.id}.xhtml")
                } else if (it.type == Defines.image) {
                    itemList.add(it.id)
                    item.addAttribute("media-type", "application/xhtml+xml")
                    item.addAttribute("href", "Text/${it.id}.xhtml")

                    val extension = it.path!!.split(File.pathSeparator).last().split(".").last()
                    val item2 = manifest.addElement("item")
                    item2.addAttribute("id", "${it.id}-image")
                        .addAttribute("href", "Images/${it.id}.$extension")

                    when (extension) {
                        "jpg" -> item2.addAttribute("media-type", "image/jpeg")
                        "png" -> item2.addAttribute("media-type", "image/png")
                        else -> item2.addAttribute("media-type", "image/$extension")
                    }
                }
            } else {
                genManifestElement(manifest, it.content!!, itemList)
            }
        }

    }

}
