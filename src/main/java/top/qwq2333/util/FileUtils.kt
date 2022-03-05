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

import java.io.File
import java.io.IOException
import java.io.InputStream

object FileUtils {

    /**
     * Create a folder
     * @param path path to target
     */
    @JvmStatic
    fun createFolder(path: String): Boolean {
        val target = File(path)
        if (target.isFile)
            throw IOException("Target has existed and it's a file")
        if (target.isDirectory)
            return true
        target.mkdir()
        return true
    }

    /**
     * Is a file or a folder existed
     * @param path path to target file or folder
     * @return is this file or folder existed
     */
    @JvmStatic
    fun isExist(path: String): Boolean = File(path).exists()

    /**
     * write text to file
     *
     * @param path path to target
     * @param msg content to write
     * @throws IOException If target has already existed or is a folder, then throw IOException
     */
    @JvmStatic
    fun write(path: String, msg: String) {
        val target = File(path)
        if (target.isDirectory) {
            throw IOException("Target is a folder.")
        }
        if (target.isFile) {
            throw IOException("Target has already existed")
        }
        val fos = target.outputStream()
        fos.write(msg.toByteArray())
        fos.flush()
        fos.close()
    }

    /**
     * write binary to file
     *
     * @param path path to target
     * @param inputStream inputStream of source
     * @throws IOException If target has already existed or is a folder, then throw IOException
     */
    fun write(path: String, inputStream: InputStream) {
        val target = File(path)
        if (target.isDirectory) {
            throw IOException("Target is a folder.")
        }
        if (target.isFile) {
            throw IOException("Target has already existed")
        }
        val fos = target.outputStream()
        fos.write(inputStream.readAllBytes())
        fos.flush()
        fos.close()
    }

    /**
     * read text from file
     * pat cannot point to a folder
     * @param path path to target file
     */
    @JvmStatic
    fun read(path: String) = File(path).readText(Charsets.UTF_8)

    /**
     * Delete file or folder
     * @param path Path to target file or folder
     */
    @JvmStatic
    fun delete(path: String) {
        if (!isExist(path))
            return
        val target = File(path)
        if (target.isDirectory)
            target.deleteRecursively()
        target.delete()
    }

    /**
     * Prepare files for next step
     * This will copy some const file from built-in resource
     * @param path tmp path
     */
    @JvmStatic
    fun prepareFile(path: String) {
        val classloader = this::class.java.classLoader

        // main folders
        createFolder("$path/${Defines.manifest}")
        createFolder("$path/${Defines.mainfolder}")

        // minetype
        classloader.getResource("minetype")?.readText(Charsets.UTF_8)?.let {
            write("$path/minetype", it)
        }

        // container.xml
        classloader.getResource("container.xml")?.readText(Charsets.UTF_8)?.let {
            write("$path/${Defines.manifest}/container.xml", it)
        }

        // fonts
        val fontPath = "$path/${Defines.mainfolder}/fonts"
        createFolder(fontPath)
        classloader.getResource("Fonts/author.ttf")!!.openStream().let {
            if (it != null) {
                write("$fontPath/author.ttf", it)
            }
        }
        classloader.getResource("Fonts/title.ttf")!!.openStream().let {
            if (it != null) {
                write("$fontPath/title.ttf", it)
            }
        }
        classloader.getResource("Fonts/KaiGenGothicTC-Heavy.ttf")!!.openStream().let {
            if (it != null) {
                write("$fontPath/KaiGenGothicTC-Heavy.ttf", it)
            }
        }


    }
}
