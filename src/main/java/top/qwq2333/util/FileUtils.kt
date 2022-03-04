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

object FileUtils {

    @JvmStatic
    fun createFolder(path: String): Boolean = File(path).mkdir()

    @JvmStatic
    fun isExist(path: String): Boolean = File(path).isFile or File(path).isDirectory

    @JvmStatic
    fun write(path: String, msg: String) {
        val target = File(path)
        if (target.isDirectory) {
            throw IOException("Target is a folder.")
        }
        target.writeText(msg, Charsets.UTF_8)
    }

    @JvmStatic
    fun read(path: String) = File(path).readText(Charsets.UTF_8)

    @JvmStatic
    fun delete(path: String) {
        if (isExist(path))
            return
        val target = File(path)
        if (target.isDirectory)
            target.deleteRecursively()
        target.delete()
    }
}
