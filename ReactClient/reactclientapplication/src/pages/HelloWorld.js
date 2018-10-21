import React from 'react';
import {
    EdgeHeader,
    FreeBird,
    Col,
    Row,
    CardBody,
  } from "mdbreact";
  import axios from 'axios';
  import SockJS from 'sockjs-client';

class HelloWorld extends React.Component {
    constructor(props){
        super(props);
        this.state = 
        {
            inputUserName: '',
            message: '',
            messages: []
        };

        // Create socket connection
        // this.sock = new SockJS('https://chat-server.azurewebsites.net/chat');
        this.socket = new WebSocket('ws://localhost:8000/ws/say-hello/');

 /*       this.sock.onopen = () => {
            console.log('Socket connection opened');
        };

        this.sock.onmessage = e => {
            console.log('Message received: ', e.data);
            this.setState({messages: [...this.state.messages, e.data]});
        };

        this.sock.onclose = () => {
            console.log('Connection closed');
        };
*/

        this.socket.onmessage = function (e) {
            console.log('Message received: ', e.data);
            var data = JSON.parse(e.data);
            console.log(data)
            var message = data['message'];
            console.log("Message: " + message + " received")
            console.log("Stop receiving..")

            alert(message)
        };

        this.socket.onclose = function (e) {
            console.error('Chat socket closed unexpectedly');
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleWebSockSubmit = this.handleWebSockSubmit.bind(this);
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    handleSubmit(event)
    {
        event.preventDefault();
        axios.get(`http://localhost:8000/hello-api/by-name/${this.state.inputUserName}`)
        .then(response => alert(response.data))
    }

    handleWebSockSubmit(e){
        e.preventDefault();
        let text = this.refs.messageText.value;
        this.socket.send(text);
    }

    render(){
        let i = 0;
        return(
            <div>
                <EdgeHeader color="indigo darken-3" />
                <FreeBird>
                    <Row>
                    <Col md="10" className="mx-auto float-none white z-depth-1 py-2 px-2">
                        <CardBody>
                        <h2 className="h2-responsive mb-4">
                            <strong>Hello world with REST API</strong>
                        </h2>
                        <Row className="d-flex flex-row justify-content-center row">
                        <div>
                            <form onSubmit={this.handleSubmit}>
                                <div className="form-group">
                                <input type="text" id="inputUserName" className="form-control" placeholder="Enter your name..." value={this.state.value} onChange={this.handleChange} />
                                </div>
                                <input type="submit" className="btn btn-primary" value="Submit" />
                            </form>
                        </div>
                        </Row>
                        </CardBody>
                    </Col>  
                    </Row>
                    <Row>
                    <Col md="10" className="mx-auto float-none white z-depth-1 py-2 px-2">
                        <CardBody>
                        <h2 className="h2-responsive mb-4">
                            <strong>Hello world with WebSockets</strong>
                        </h2>
                        <Row className="d-flex flex-row justify-content-center row">
                        <div>
                            <form onSubmit={this.handleWebSockSubmit}>
                                <div className="form-group">
                                <input type="text" ref="messageText" className="form-control" placeholder="Type your message..." />
                                </div>
                                <input type="submit" className="btn btn-primary" value="Submit" />
                            </form>
                        </div>
                        <div>
                            <h3>Messages</h3>
                            <hr />
                            <ul>
                                {this.state.messages.map(msg => {
                                    return <li key={i++}>{msg}</li>
                                })}
                            </ul>
                        </div>

                        </Row>
                        </CardBody>
                    </Col>  
                    </Row>
                </FreeBird>
            </div>
            
        
        );
    }
}

export default HelloWorld;