import React from 'react';

const Constants = {
    WebSocket:{
        Chat: "ws://localhost:8000/ws/chat",
        HelloWorld: "ws://localhost:8000/ws/say-hello/",
        Broadcast: "ws://localhost:8000/ws/broadcast/",

    },
    Rest:{
        HelloWorld: "http://localhost:8000/hello-api/by-name",
        Documents: "http://localhost:8000/online-docs/documents/",
        Document: "http://localhost:8000/online-docs/document/",
        EditingBy: "/editing-by/",

    }
}

export default Constants;