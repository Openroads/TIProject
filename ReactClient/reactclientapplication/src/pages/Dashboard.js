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
            fileContent: '',
            idEditedDocument: '',
            editingBy: ''
        }

        this.handleChange = this.handleChange.bind(this);
        this.toggle = this.toggle.bind(this);
        this.handleSave = this.handleSave.bind(this);
        this.getDocuments = this.getDocuments.bind(this);
        this.resetInput = this.resetInput.bind(this);
        this.endEditing = this.endEditing.bind(this);
        this.disableViewWhenEditing = this.disableViewWhenEditing.bind(this);

        this.getDocuments();
    }

    getDocuments()
    {
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
            if(this.state.isEditedByMe == true){
                this.endEditing();
            }
            
            this.resetInput();
            
        }

        this.setState({
          modal: !this.state.modal
        });
    }

    endEditing()
    {
        var domain = "http://localhost:8000/online-docs/document/";
        var id = this.state.idEditedDocument.toString();
        var endOf = "/stop-editing/";
        var address = domain.concat(id, endOf);
        axios.post(address, {
                })
                .then(response => console.log("End editing: ", response))
                .catch(function (error) {
                    console.log(error);
                });

        this.setState({isEditedByMe: false});
    }

    handleCreation(event){
        
        
    }

    resetInput(){
        document.getElementById("fileTitle").value = "";
        document.getElementById("fileContent").value = "";
        this.setState({fileContent: '', fileTitle: ''});
    }

    handleSave(event){

        event.preventDefault();

        if(this.state.isEditedByMe == undefined || this.state.editingBy == undefined || this.state.isEditedByMe == false)
        {
            axios.post('http://localhost:8000/online-docs/documents/', {
                title: this.state.fileTitle,
                content: this.state.fileContent,
            })
            .then(response => this.setState({documents: [...this.state.documents, response.data]}))
            .catch(function (error) {
                console.log(error);
            });   
            
            
        }
        else{
            axios.put(`http://localhost:8000/online-docs/document/${this.state.idEditedDocument}/`, {
                
                    title: this.state.fileTitle,
                    content: this.state.fileContent,
                    version: 1
                
            })
            .then(response => console.log("Updated: ", response))
            .catch(function (error) {
                console.log(error);
            });

/*
            const formData = new FormData();
            formData.append('title', this.state.fileTitle);
            formData.append('content', this.state.fileContent);

            fetch(`http://localhost:8000/online-docs/document/${this.state.idEditedDocument}/`, {
                method: 'PUT',
                body: formData,
                headers: {
                    'Content-Type': 'application/json'
                }
                }).then(res => {
                    console.log("Put: ", res.json())
                }).catch(err => err);
            }
            */
        }

        //this.getDocuments();

        this.toggle();
        
    }

    disableViewWhenEditing(){
        var textArea = document.getElementById("fileContent");
        var saveButton = document.getElementById("saveButton");

        if(this.state.editingBy != ""){
            textArea.disabled = true;
            saveButton.disabled = true;

        }
        else{
            textArea.disabled = false;
            saveButton.disabled = false;
        }
    }

    documentCallback = (dataFromCallback) => {
        let title = dataFromCallback.title;
        let content = dataFromCallback.content;
        let id = dataFromCallback.id;
        let editingby = dataFromCallback.editingBy;

        // Callback when clickin position on the list
        if(title !== "" && content !== "" && title !== undefined && content !== undefined && id != "")
        {
            if(editingby == undefined || editingby == ""){
                axios.post(`http://localhost:8000/online-docs/document/${id}`+ '/editing-by/1/', {
                })
                .then(response => console.log("Editing: ", response))
                .catch(function (error) {
                    console.log(error);
                });

                this.setState({fileTitle: title, fileContent: content, idEditedDocument: id, isEditedByMe: true});
                this.toggle();
            }
            else{
                this.setState({fileTitle: title, fileContent: content, editingBy: editingby});
                this.toggle();
                this.disableViewWhenEditing();
            }
            
            
            
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
                                    onClick={this.toggle} //Todo : handle creation
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
                        <Button id="saveButton" color="primary" onClick={(event) => this.handleSave(event)}>Save</Button>
                    </ModalFooter>
                    </Modal>
                    </div>
        );
    }
}

export default Dashboard;