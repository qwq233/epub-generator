/*
 * EPUB Generator
 * Copyright (C) 2022 qwq233 qwq233@qwq2333.top
 * https://github.com/qwq233/epub-generator
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of our Licenses
 * as published by James Clef; either
 * version 2 of the License, or any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the our
 * licenses for more details.
 *
 * You should have received a copy of the our License
 * and eula along with this software.  If not, see
 * <https://github.com/qwq233/License/blob/master/v2/LICENSE.md>.
 */

package top.qwq2333.util

import org.jetbrains.kotlin.konan.file.File
import top.qwq2333.config.data.Config
import top.qwq2333.generate.XMLContent
import java.io.IOException
import java.util.*

object Utils {
    @JvmStatic
    fun validateConfig(config: Config) {
        if (config.version < Defines.currentConfigVersion)
            throw IllegalStateException("Config version is too new!\nPlease check updates")
        val id: MutableList<String> = mutableListOf()

        val deliverLine = config.metadata.customDeliverLine
        if (deliverLine.enable) {
            if (deliverLine.type == Defines.image) {
                if (!FileUtils.isExist(deliverLine.content))
                    throw IOException("deliver image is not found!")
            } else if (deliverLine.type != Defines.text) {
                throw IllegalStateException("Type of deliver line can be only image or text!")
            }
        }

        for (content in config.content) {
            if (!id.contains(content.id)) {
                id.add(content.id)
            } else {
                throw IllegalStateException("One or more occurrences of the same Id")
            }
            val type = content.type
            if (type == Defines.text) {
                if (content.path == null)
                    throw IllegalStateException("Path for type text should not be empty")
                if ((content.title == null) and !content.hiddenInContent)
                    throw IllegalStateException("Title for type text should not be empty when noTitle is false")
            } else if (type == Defines.image) {
                if (content.path == null)
                    throw IllegalStateException("Path for type text should not be empty")
            } else if (type == Defines.subContent) {
                if (content.content == null)
                    throw IllegalStateException("Content for type subcontent should not be empty")
            } else {
                throw Exception("Unknown type \"$type\"")
            }

        }
    }

    /**
     * Prepare files for next step.
     * This will copy some const file from built-in resource
     * @param path tmp path
     */
    @JvmStatic
    fun prepareFile(path: String, cfg: Config) {
        val classloader = this::class.java.classLoader

        // main folders
        FileUtils.createFolder("$path/${Defines.manifest}")
        FileUtils.createFolder("$path/${Defines.mainFolder}")

        // mimetype
        classloader.getResource("mimetype")?.readText(Charsets.UTF_8)?.let {
            FileUtils.write("$path/mimetype", it)
        }

        // container.xml
        classloader.getResource("container.xml")?.readText(Charsets.UTF_8)?.let {
            FileUtils.write("$path/${Defines.manifest}/container.xml", it)
        }

        // fonts
        val fontPath = "$path/${Defines.mainFolder}/Fonts"
        FileUtils.createFolder(fontPath)
        classloader.getResource("Fonts/author.ttf")!!.openStream().let {
            if (it != null) {
                FileUtils.write("$fontPath/author.ttf", it)
            }
        }
        classloader.getResource("Fonts/title.ttf")!!.openStream().let {
            if (it != null) {
                FileUtils.write("$fontPath/title.ttf", it)
            }
        }
        classloader.getResource("Fonts/KaiGenGothicTC-Heavy.ttf")!!.openStream().let {
            if (it != null) {
                FileUtils.write("$fontPath/KaiGenGothicTC-Heavy.ttf", it)
            }
        }

        val stylesPath = "$path/${Defines.mainFolder}/${Defines.style}"
        FileUtils.createFolder(stylesPath)
        classloader.getResource("Styles/style.css")!!.openStream().let {
            if (it != null) {
                FileUtils.write("$stylesPath/style.css", it)
            }
        }


        FileUtils.write("$path/${Defines.mainFolder}/toc.ncx", XMLContent.genTableOfContent(cfg))

        FileUtils.createFolder("$path/${Defines.mainFolder}/${Defines.textFolder}")
        FileUtils.createFolder("$path/${Defines.mainFolder}/${Defines.imageFolder}")

        val deliverLine = cfg.metadata.customDeliverLine
        if (deliverLine.enable) {
            if (deliverLine.type == Defines.image) {
                File(deliverLine.content).copyTo(
                    File(
                        "$path/${Defines.mainFolder}/${Defines.imageFolder}/deliverLine.${fileExtension(deliverLine.content)}"
                    )
                )
            }
        }


    }

    /**
     * @param input String
     * @return boolean is this string a url
     */
    fun isURL(input: String): Boolean {
        // lower case
        val str = input.lowercase(Locale.getDefault())
        val regex = ("^((https|http|ftp)?://)" //https、http、ftp、rtsp、mms
            + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 例如：199.194.52.184
            + "|" // 允许IP和DOMAIN（域名）
            + "([0-9a-z_!~*'()-]+\\.)*" // 域名- www.
            + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // 二级域名
            + "[a-z]{2,6})" // first level domain- .com or .museum
            + "(:[0-9]{1,5})?" // 端口号最大为65535,5位数
            + "((/?)|" // a slash isn't required if there is no file name
            + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$")
        return str.matches(Regex(regex))
    }

    fun fileExtension(path: String) =
        path.split("/").last().split(".").last()

}

