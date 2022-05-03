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

object Defines {
    const val text = "text"
    const val image = "image"
    const val subContent = "subcontent"
    const val mainFolder = "OEBPS"
    const val manifest = "META-INF"
    const val style = "Styles"
    const val textFolder = "Text"
    const val imageFolder = "Images"
    const val currentConfigVersion = 2

    fun contentFull(element: String) =
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"zh-CN\" xmlns:epub=\"http://www.idpf.org/2007/ops\">" +
            "<head>  <link href=\"../Styles/style.css\" rel=\"stylesheet\" type=\"text/css\"/>" +
            "<title>Table of Contents</title>" +
            "</head><body><div> <p class=\"contents em12\">Table of Contents</p>$element</div></body></html>"

    fun contentElement(href: String, title: String): String =
        "<p> </p><p class=\"content\"><a class=\"no-d co20 bold\" href=\"$href\">$title</a></p><p> </p>"

    fun textHTML(str: String): String =
        "<p> </p><p>$str</p><p> </p>"

    fun deliverLineImage(extension: String) = "<p><img src=\"../deliverLine.${extension}\" alt=\"\"/></p> "
}
