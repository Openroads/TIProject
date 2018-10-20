import React from 'react';
import {
    Button,
    EdgeHeader,
    FreeBird,
    Col,
    Row,
    CardBody,
    Modal,
    ModalBody,
    ModalFooter,
    Fa
  } from "mdbreact";
import DocumentList from './DocumentList';
import axios from 'axios';

class Dashboard extends React.Component 
{
    constructor(props)
    {
        super(props);
        this.state = 
        {
            fileTitle: '',
            modal: false,
            documents: [],
            fileContent: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.toggle = this.toggle.bind(this);
        this.handleSave = this.handleSave.bind(this);
        this.getDocuments = this.getDocuments.bind(this);
        this.resetInput = this.resetInput.bind(this);

        this.getDocuments();
    }

    onChangeText

    getDocuments()
    {
        //event.preventDefault();
        axios.get('http://localhost:8000/online-docs/documents/')
        .then(response => this.setState({documents: response.data}))
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    toggle() {

        if(this.state.modal)
        {
            this.resetInput();
        }

        this.setState({
          modal: !this.state.modal
        });
    }

    resetInput(){
        document.getElementById("fileTitle").value = "";
        document.getElementById("fileContent").value = "";
    }

    handleSave(){
        this.state.documents.push({"id": "2", "title": this.state.fileTitle.toString()});
        // Todo: Push to the server
        //this.getDocuments();
        this.toggle();
    }

    documentCallback = (dataFromCallback) => {
        let title = dataFromCallback.title;
        let content = dataFromCallback.content;

        if(title !== "" && content !== "" && title != undefined && content != undefined)
        {
            this.setState({fileTitle: title, fileContent: content});
            this.toggle();
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
                        
                        <Row>

                        <div>
                        <h2>
                            <strong>My documents</strong>
                        </h2>
                                <div className="form-inline">
                                <input type="text" id="fileTitle" className="form-control mr-1" placeholder="File name..." value={this.state.documentName} onChange={this.handleChange} />
                                
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
                                    <DocumentList Documents={this.state.documents} callbackFromParent={this.documentCallback}/>
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
                        {this.state.fileTitle}
                        </h4>
                        <button type="button" className="close" onClick={this.toggle}>
                        <span aria-hidden="true">×</span>
                        </button>
                    </div>
                    <ModalBody className="grey-text">
                    <div className="form-group shadow-textarea">
                        <textarea className="form-control z-depth-1" id="fileContent" rows="10" placeholder="Write your content..." value={this.state.fileContent} onChange={this.handleChange}></textarea>
                    </div>
                    </ModalBody>
                    <ModalFooter>
                        <Button color="secondary" onClick={this.toggle}>
                        Close
                        </Button>{" "}
                        <Button color="primary" onClick={(event) => this.handleSave(event)}>Save</Button>
                    </ModalFooter>
                    </Modal>
                    </div>
        );
    }
}

export default Dashboard;