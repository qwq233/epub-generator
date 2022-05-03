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

package top.qwq2333.config.data

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class Metadata(
    val title: String,
    val author: String,
    val creator: String = author,
    val description: String,
    val language: String,
    val date: String = SimpleDateFormat("yyyy-M-d").format(Date()),
    val rights: String = "epub by $creator using EPUB Generator created by James Clef",
    val cover: Cover,
    val customDeliverLine: CustomDeliverLine

) {
    @Serializable
    data class Cover(
        val hasCover: Boolean = false,
        val image: String = ""
    )

    @Serializable
    data class CustomDeliverLine(
        val enable: Boolean = false,
        val type: String = "null",
        val content: String = "null"
    )
}
