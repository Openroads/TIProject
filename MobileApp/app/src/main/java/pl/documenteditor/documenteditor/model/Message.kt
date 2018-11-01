package pl.documenteditor.documenteditor.model

data class Message(
    val text: String // message body
    //, val data: MemberData // data of the user that sent this message
    , val belongsToCurrentUser: Boolean // is this message sent by us?
)