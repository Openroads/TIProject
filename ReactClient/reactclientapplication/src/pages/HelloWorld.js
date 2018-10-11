import React from 'react';
import {
    EdgeHeader,
    FreeBird,
    Col,
    Row,
    CardBody,
  } from "mdbreact";


class HelloWorld extends React.Component {
    constructor(props){
        super(props);
        this.state = 
        {
            inputUserName: "",
            message: ""
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    handleSubmit()
    {
        fetch('https://jsonplaceholder.typicode.com/todos/1')
        .then(response => response.json())
        .then(json => alert(json.title))
    }

    render(){
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
                </FreeBird>
            </div>
            
        
        );
    }
}

export default HelloWorld;