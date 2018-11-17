package pl.documenteditor.documenteditor.utils

import okhttp3.MediaType

class JsonUtils {

    companion object {

        val JSON = MediaType.parse("application/json; charset=utf-8")

        const val OPERATION_PROPERTY = "operation"

        const val DATA_PROPERTY = "data"

        const val DOCUMENT_ID_PROPERTY = "documentId"

    }
}