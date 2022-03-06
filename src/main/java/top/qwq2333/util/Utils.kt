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

package top.qwq2333.util

import top.qwq2333.config.data.Config
import top.qwq2333.generate.XmlContent

object Utils {
    @JvmStatic
    fun validateConfig(config: Config) {
        val id: MutableList<String> = mutableListOf()
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
            } else if (type == Defines.subcontent) {
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
        FileUtils.createFolder("$path/${Defines.mainfolder}")

        // minetype
        classloader.getResource("minetype")?.readText(Charsets.UTF_8)?.let {
            FileUtils.write("$path/minetype", it)
        }

        // container.xml
        classloader.getResource("container.xml")?.readText(Charsets.UTF_8)?.let {
            FileUtils.write("$path/${Defines.manifest}/container.xml", it)
        }

        // fonts
        val fontPath = "$path/${Defines.mainfolder}/Fonts"
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

        val stylesPath = "$path/${Defines.mainfolder}/${Defines.style}"
        FileUtils.createFolder(stylesPath)
        classloader.getResource("Styles/style.css")!!.openStream().let {
            if (it != null) {
                FileUtils.write("$stylesPath/style.css", it)
            }
        }

        FileUtils.write("$path/${Defines.mainfolder}/content.opf", XmlContent.genContent(cfg))
        FileUtils.write("$path/${Defines.mainfolder}/toc.ncx", XmlContent.genTableOfContent(cfg))

        FileUtils.createFolder("$path/${Defines.mainfolder}/${Defines.textFolder}")
        FileUtils.createFolder("$path/${Defines.mainfolder}/${Defines.imageFolder}")


    }

}

