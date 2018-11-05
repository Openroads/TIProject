import React from 'react';
import Messages from './Messages';
import ChatInput from './ChatInput';
import './Chat.css';

class Chat extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            messages: []
        };

        this.sendHandler = this.sendHandler.bind(this);

        // Create socketConnection
        const documentId = this.props.documentId;
        this.socket = new WebSocket(`ws://localhost:8000/ws/chat/${documentId}/`);

       this.socket.onmessage = e => {
            console.log('Message received: ', e.data);
            var data = JSON.parse(e.data);
            console.log(data);
            var userName = data['username'];
            var message = data['text'];

            const msObject = {
                username: userName,
                message: message
            };

            this.addMessage(msObject);
        };

        this.socket.onclose = function(e) {
            console.log('Chat socket closed unexpectedly');
        }; 
    }

    sendHandler(message){
        const messageObject = {
            username: this.props.username,
            message

        };

        // Send message via sockets
       this.socket.send(messageObject);
        
        messageObject.fromMe = true;
        this.addMessage(messageObject);
    }

    addMessage(message){
        // Append the message to the component state
        const messages = this.state.messages;
        messages.push(message);
        this.setState({messages});
    }

    render(){
        return(
            <div className='cont'>
                <h3>Chat</h3>
                <Messages messages = {this.state.messages}/>
                <ChatInput onSend = {this.sendHandler}/>
            </div>
        );
    }
}

export default Chat;