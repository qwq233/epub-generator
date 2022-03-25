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

package top.qwq2333

import com.charleskorn.kaml.Yaml
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import top.qwq2333.config.data.Config
import top.qwq2333.generate.HTMLContent
import top.qwq2333.generate.XMLContent
import top.qwq2333.util.Defines
import top.qwq2333.util.FileUtils
import top.qwq2333.util.Utils
import java.io.ByteArrayOutputStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size < 2) {
        println("Usage: [Source Folder] [Target Folder]")
        exitProcess(1)
    }

    val source = args[0]
    val target = args[1]

    val tmp = "$target/tmp"


    if ((FileUtils.isExist(target)) and (FileUtils.isExist(tmp))) {
        FileUtils.delete(tmp)
        FileUtils.createFolder(tmp)
    } else {
        if (FileUtils.isExist(target)) {
            FileUtils.createFolder(tmp)
        } else {
            FileUtils.createFolder(target)
            FileUtils.createFolder(tmp)
        }
    }

    println("Program arguments: ${args.joinToString()}")

    println("Validating Config")
    val cfg = Yaml.default.decodeFromString(Config.serializer(), FileUtils.read("$source/config.yml"))
    Utils.validateConfig(cfg)
    println("Config File is valid")

    println("Generating base files.")

    Utils.prepareFile(tmp, cfg)

    if (FileUtils.isExist("$target/${cfg.metadata.title}.epub")) {
        FileUtils.delete("$target/${cfg.metadata.title}.epub")
    }
    val content = XMLContent.genContent(cfg)
    HTMLContent.process(cfg.metadata, cfg.content, tmp, source, content)

    val output = ByteArrayOutputStream()
    val writer = XMLWriter(output, OutputFormat.createPrettyPrint())
    writer.write(content)

    FileUtils.write("$tmp/${Defines.mainFolder}/content.opf", String(output.toByteArray()))


    FileUtils.write(
        "$tmp/${Defines.mainFolder}/Text/contents.xhtml",
        HTMLContent.genToC(HTMLContent.genToCElement(cfg.content))
    )
    if (cfg.metadata.cover.hasCover) {
        HTMLContent.genCover(tmp, source, cfg.metadata)
    }

    FileUtils.pack(tmp, "$target/${cfg.metadata.title}.epub")
    println("Complete")

    exitProcess(0)


}
