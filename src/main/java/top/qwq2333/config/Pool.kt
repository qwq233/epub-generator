/*
 *  EPUB Generator
 *  Copyright (C) 2022 qwq233 qwq233@qwq2333.top
 *  https://github.com/qwq233/epub-generator
 *  *
 *  This software is non-free but opensource software: you can redistribute it
 *  and/or modify it under the terms of our Licenses
 *  as published by James Clef; either
 *  version 2 of the License, or any later version.
 *
 *  This software is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See our
 *  license
 *
 *  You should have received a copy of the our License
 *  and eula along with this software.  If not, see
 *  <https://github.com/qwq233/License/blob/master/v2/LICENSE.md>.
 */

package top.qwq2333.config

import org.dom4j.Document
import top.qwq2333.config.data.Config
import top.qwq2333.config.data.Content

object Pool {
    lateinit var cfg: Config
    lateinit var metadata: top.qwq2333.config.data.Metadata
    lateinit var globalContents: List<Content>

    lateinit var doc: Document
}
