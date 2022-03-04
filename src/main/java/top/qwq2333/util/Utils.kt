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

object Utils {
    @JvmStatic
    fun validateConfig(config: Config) {
        for (content in config.content) {
            val type = content.type
            if (type == Defines.text) {
                if (content.path == null)
                    throw IllegalStateException("Path for type text should not be empty")
                if ((content.title == null) and !content.noTitle)
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

}

