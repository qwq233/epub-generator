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

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


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
            Console.printMsg("Target file $path is already exist.")
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
    @JvmStatic
    fun write(path: String, inputStream: InputStream) {
        val target = File(path)
        if (target.isDirectory) {
            throw IOException("Target is a folder.")
        }
        if (target.isFile) {
            Console.printMsg("WARN: Target file $path is already exist.")
        }
        val fos = target.outputStream()
        fos.write(inputStream.readAllBytes())
        fos.flush()
        fos.close()
    }

    /**
     * read text from file
     *
     * path cannot point to a folder
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
     * convert InputStream to String
     * @param inputStream target
     * @return result
     */
    fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        var line: String? = ""
        var result: String? = ""
        while (bufferedReader.readLine().also { line = it } != null) {
            result += line
        }
        inputStream.close()
        return result!!
    }


    fun pack(sourceDirPath: String, zipFilePath: String) {
        val p: Path = Files.createFile(Paths.get(zipFilePath))
        ZipOutputStream(Files.newOutputStream(p)).use { zs ->
            val pp: Path = Paths.get(sourceDirPath)
            Files.walk(pp)
                .filter { path -> !Files.isDirectory(path) }
                .forEach { path ->
                    val zipEntry = ZipEntry(pp.relativize(path).toString())
                    zs.putNextEntry(zipEntry)
                    Files.copy(path, zs)
                    zs.closeEntry()
                }
        }
    }


}
