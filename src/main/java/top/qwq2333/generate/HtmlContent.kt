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

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.ast.Node
import com.vladsch.flexmark.util.data.MutableDataSet
import org.jetbrains.kotlin.konan.file.File
import top.qwq2333.config.data.Content
import top.qwq2333.util.Defines
import top.qwq2333.util.FileUtils
import java.io.FileInputStream

object HtmlContent {

    fun process(
        metadata: top.qwq2333.config.data.Metadata,
        contents: List<Content>,
        targetPath: String,
        sourcePath: String
    ) {
        for (content in contents) {
            var output: String = "null"
            if (content.type == Defines.subcontent) {
                process(metadata, content.content!!, targetPath, sourcePath)
            } else if (content.type == Defines.text) {
                output = convertMdToHtml(FileUtils.read("$sourcePath/${content.path!!}"), content.title!!)
                FileUtils.write("$targetPath/${Defines.mainfolder}/Text/${content.id}.xhtml", output)
            } else if (content.type == Defines.image) {
                val fileExtension = content.path!!.split(File.pathSeparator).last().split(".").last()
                output = FileUtils.convertInputStreamToString(
                    this::class.java.classLoader.getResource("Text/image.xhtml")!!.openStream()
                )
                    .replace("LANGUAGE", metadata.language)
                    .replace("TITLE", content.title!!)
                    .replace(
                        "PATH",
                        "${content.id}.${fileExtension}"
                    )

                FileUtils.write(
                    "$targetPath/${Defines.mainfolder}/Images/${content.id}.$fileExtension",
                    FileInputStream("$sourcePath/${content.path!!}")
                )
                FileUtils.write("$targetPath/${Defines.mainfolder}/Text/${content.id}.xhtml", output)
            }
        }

    }

    fun genCover(targetPath: String, sourcePath: String, metadata: top.qwq2333.config.data.Metadata) {
        val fileExtension = metadata.cover.image.split(File.pathSeparator).last().split(".").last()
        FileUtils.write(
            "$targetPath/${Defines.mainfolder}/Images/cover.$fileExtension",
            FileInputStream("${sourcePath}/${metadata.cover.image}")
        )


        val output = FileUtils.convertInputStreamToString(
            this::class.java.classLoader.getResource("Text/image.xhtml")!!.openStream()
        )
            .replace("LANGUAGE", metadata.language)
            .replace("TITLE", "Cover")
            .replace(
                "PATH",
                "cover.${fileExtension}"
            )


        FileUtils.write("$targetPath/${Defines.mainfolder}/Text/cover.xhtml", output)
    }

    fun genToC(string: String) =
        Defines.contentFull(string)

    fun genToCElement(list: List<Content>): String {
        val sb = StringBuilder()
        for (content in list) {
            if (content.type == Defines.subcontent) {
                sb.append(genToCElement(content.content!!))
            }
            if (content.type != Defines.subcontent) {
                sb.append(Defines.contentElement("${content.id}.xhtml", content.title!!))
            }
        }
        return sb.toString()
    }

    fun convertMdToHtml(content: String, title: String): String {
        var target = String()
        this::class.java.classLoader.getResource("Text/template.xhtml")!!.openStream().let {
            if (it != null) {
                target = FileUtils.convertInputStreamToString(it)
            }
        }

        val options = MutableDataSet()
        val parser = Parser.builder(options).build()
        val renderer = HtmlRenderer.builder(options).build()

        val document: Node = parser.parse(content)
        val output = renderer.render(document)

        target = target.replace("TITLE", title)
        target = target.replace("TARGET", output)

        return target
    }
}
