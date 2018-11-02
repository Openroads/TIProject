package pl.documenteditor.documenteditor.utils

import okhttp3.MediaType

class Constants {

    companion object {
        const val REST_SERVERS_ADDRESS = "http://192.168.1.6:8000/"
        const val WEB_SOCKET_ADDRESS = "ws://10.0.2.2:8000/ws/"
        val JSON = MediaType.parse("application/json; charset=utf-8")
    }

}