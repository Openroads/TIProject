import React from 'react';
import {
    Input,
    Button,
    EdgeHeader,
    FreeBird,
    Container,
    Col,
    Row,
    CardBody,
    Card,
    Modal,
    ModalBody,
    ModalFooter,
    Fa
  } from "mdbreact";
import { deflate } from 'zlib';
  const NavLink = require("react-router-dom").NavLink;

class Dashboard extends React.Component 
{
    constructor(props)
    {
        super(props);
        this.state = 
        {
            userName: '',
            documents: [],
        }
    }


    render()
    {
        return(
            <div>
                <EdgeHeader color="indigo darken-3" />
                <div>
                    <h3>{this.state.userName}</h3>
                </div>
                <FreeBird>
                    <Row>
                    <Col md="10" className="mx-auto float-none white z-depth-1 py-2 px-2">
                        <CardBody>
                        <h2 className="h2-responsive mb-4">
                            <strong>Username</strong>
                        </h2>
                        <Row className="d-flex flex-row justify-content-center row">
                        <div>
                            <ul>
                                <li>Pierwszy plik</li>
                                <li>Drugi plik</li>
                                <li>Pierwszy plik</li>
                                <li>Drugi plik</li>
                                <li>Pierwszy plik</li>
                                <li>Drugi plik</li>
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

export default Dashboard;