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
import DocumentList from './DocumentList';
  const NavLink = require("react-router-dom").NavLink;

class Dashboard extends React.Component 
{
    constructor(props)
    {
        super(props);
        this.state = 
        {
            fileName: '',
            modal: false,
            documents: [],
            filecontent: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.toggle = this.toggle.bind(this);
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    toggle() {
        this.setState({
          modal: !this.state.modal
        });
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
                        
                        <Row>

                        <div>
                        <h2>
                            <strong>My documents</strong>
                        </h2>
                                <div class="form-inline">
                                <input type="text" id="fileName" class="form-control mr-1" placeholder="File name..." value={this.state.documentName} onChange={this.handleChange} />
                                
                                <a
                                    className="border nav-link border-light rounded mr-1"
                                    onClick={this.toggle}
                                    target="_blank"
                                    rel="noopener noreferrer">
                                        <Fa icon="pencil" className="mr-2" />
                                        Create new
                                </a>
                                </div>
                                <div className="documentList">
                                    <DocumentList />
                                </div>
                        </div>
                        </Row>
                        </CardBody>
                    </Col>  
                    </Row>
                </FreeBird>
                <Modal
                    isOpen={this.state.modal}
                    toggle={this.toggle}
                    className="cascading-modal">
                    <div className="modal-header primary-color white-text">
                        <h4 className="title">
                        {this.state.fileName}
                        </h4>
                        <button type="button" className="close" onClick={this.toggle}>
                        <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <ModalBody className="grey-text">
                    <div class="form-group shadow-textarea">
                        <textarea class="form-control z-depth-1" id="exampleFormControlTextarea6" rows="10" placeholder="Write your content..."></textarea>
                    </div>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="secondary" onClick={this.toggle}>
                        Close
                        </Button>{" "}
                        <Button color="primary" onClick={(event) => this.handleClick(event)}>Save</Button>
                    </ModalFooter>
                    </Modal>
                    </div>
        );
    }
}

export default Dashboard;