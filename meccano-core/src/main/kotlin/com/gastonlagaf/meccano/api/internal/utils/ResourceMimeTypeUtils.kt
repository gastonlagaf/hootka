package com.gastonlagaf.meccano.api.internal.utils

import com.gastonlagaf.meccano.api.model.MimeType

object ResourceMimeTypeUtils {

    fun resolveMimeType(fileName: String): MimeType {
        return when (val extension = fileName.substring(fileName.lastIndexOf('.') + 1)) {
            "jpg" -> MimeType.IMAGE_JPEG
            "svg" -> MimeType.IMAGE_SVG
            "png" -> MimeType.IMAGE_PNG
            "gif" -> MimeType.IMAGE_GIF
            "css" -> MimeType.TEXT_CSS
            "js" -> MimeType.TEXT_JAVASCRIPT
            "json" -> MimeType.APPLICATION_JSON
            "ttf" -> MimeType.FONT_TTF
            "woff2" -> MimeType.FONT_WOFF2
            "ico" -> MimeType.IMAGE_ICO
            "html" -> MimeType.TEXT_HTML
            else -> throw IllegalArgumentException("Unprocessed file extension `$extension`")
        }
    }

}