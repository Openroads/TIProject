import React from 'react';
import Messages from './Messages';
import ChatInput from './ChatInput';
import './Chat.css';
import Constants from './../Constants';

class Chat extends React.Component{
    constructor(props){
        super(props);
        this.state = {
            messages: []
        };

        this.sendHandler = this.sendHandler.bind(this);

        // Create socketConnection
        const documentId = this.props.documentId;
        this.socket = new WebSocket(`${Constants.WebSocket.Chat}/${documentId}`);

        this.socket.onmessage = e => {
            console.log('Message received: ', e.data);
            var data = JSON.parse(e.data);
            var userName = data['username'];
            var message = data['text'];

            
            var msObject = {
                username: userName,
                message
            };

            if(userName == this.props.username)
            {
                msObject.fromMe = true;
            }
            else{
                msObject.fromMe = false;
            }

            this.addMessage(msObject);
        };

        this.socket.onclose = function(e) {
            console.log('Chat socket closed unexpectedly');
        }; 
    }

    sendHandler(message){
        const messageObject = {
            username: this.props.username,
            text: message

        };

        var stringified = JSON.stringify(messageObject);
        console.log(stringified);
        this.socket.send(stringified);
    }

    addMessage(message){
        // Append the message to the component state
        const messages = this.state.messages;
        messages.push(message);
        this.setState({messages});
    }

    componentWillUnmount(){
        this.socket.close();
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