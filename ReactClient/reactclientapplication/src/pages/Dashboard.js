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
import avatar from '../Images/img_avatar.png';
import Chat from './Chat';
import Constants from './../Constants';

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
            editingBy: '',
            editingDocument: '',
            userName: sessionStorage.getItem('username'),
            userId: sessionStorage.getItem('userId')
        }

        

        this.handleChange = this.handleChange.bind(this);
        this.toggle = this.toggle.bind(this);
        this.handleSave = this.handleSave.bind(this);
        this.getDocuments = this.getDocuments.bind(this);
        this.resetInput = this.resetInput.bind(this);
        this.endEditing = this.endEditing.bind(this);
        this.disableViewWhenEditing = this.disableViewWhenEditing.bind(this);
        this.deleteDocument = this.deleteDocument.bind(this);
        this.canDelete = this.canDelete.bind(this);
        this.isNewDocument = this.isNewDocument.bind(this);
        this.onDocumentClicked = this.onDocumentClicked.bind(this);
        this.addDocument = this.addDocument.bind(this);
        this.getDocuments();

        this.socketBroadcast = new WebSocket(Constants.WebSocket.Broadcast);
        this.socketBroadcast.onmessage = e => {}
    }

    async getDocuments()
    {
        const response = await axios.get(`${Constants.Rest.Documents}`);
        this.setState({documents: response.data});

        return response.data;
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

    canDelete()
    {
        if(this.state.isEditedByMe == true){
            return 'btn btn-danger btn-rounded'
        }

        return 'btn btn-danger btn-rounded disabled'
    }

    async endEditing()
    {
        var domain = `${Constants.Rest.Document}`;
        var id = this.state.idEditedDocument.toString();
        var endOf = "/stop-editing/";
        var address = domain.concat(id, endOf);
        const response = await axios.post(address, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded' }
                });
        console.log("End editing: ", response);

        this.setState({isEditedByMe: false});

        var unlockObject = {
            operation: "UNLOCK",
            data: {
                documentId: id
            }
        }

        var stringUnlockObject = JSON.stringify(unlockObject);
        this.socketBroadcast.send(stringUnlockObject);
    }

    resetInput(){
        document.getElementById("fileTitle").value = "";
        document.getElementById("fileContent").value = "";
        this.setState({fileContent: '', fileTitle: ''});
    }

    async deleteDocument(event){
        event.preventDefault();
        const response = await axios.delete(`${Constants.Rest.Document}${this.state.idEditedDocument}/`, {
                
                    title: this.state.fileTitle,
                    content: this.state.fileContent,
                    version: 1,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded' }
                
            });
        console.log("Deleted: ", response);
        
        var deleteObject = {
            operation: "DELETE",
            data: {
                documentId: this.state.idEditedDocument
            }
        }

        var stringDeleteObject = JSON.stringify(deleteObject);
        this.socketBroadcast.send(stringDeleteObject);

        this.toggle();
    }

    async handleSave(event){

        event.preventDefault();
        
        if(this.isNewDocument())
        {
            axios.post(`${Constants.Rest.Documents}`, {
                title: this.state.fileTitle,
                content: this.state.fileContent,
            })
            .then(response => this.addDocument(response))
            .catch(function (error) {
                console.log(error);
            });     
        }
        else{
           const response = await axios.put(`${Constants.Rest.Document}${this.state.idEditedDocument}/`, {
                
                    title: this.state.fileTitle,
                    content: this.state.fileContent,
                    version: 1,
            });

           console.log("Updated: ", response);
           this.getDocuments();
        }

        this.toggle();
        
    }

    addDocument(response)
    {
        var addedObject = {
            operation: "ADD",
            data: {
                document: response.data
            }
        }

        var stringAddedObject = JSON.stringify(addedObject);
        this.socketBroadcast.send(stringAddedObject);
    }

    isNewDocument(){
        if(this.state.isEditedByMe == undefined || this.state.editingBy == undefined || this.state.isEditedByMe == false)
        {
            return true;
        }

        return false;
    }

    disableViewWhenEditing(){
        var textArea = document.getElementById("fileContent");
        var saveButton = document.getElementById("saveButton");

        if(this.state.isEditedByMe == false){
            textArea.disabled = true;
            saveButton.disabled = true;

        }
        else{
            textArea.disabled = false;
            saveButton.disabled = false;
        }
    }

    onDocumentClicked(documentData) {
        var data = documentData.data;

        var editedBy = data.editingBy;

        if(editedBy == undefined || editedBy == "")
        {
            this.setState({fileTitle: data.title, fileContent: data.content, idEditedDocument: data.id, isEditedByMe: true});
            this.toggle();
        }
        else {
            this.setState({fileTitle: data.title, fileContent: data.content, idEditedDocument: data.id, isEditedByMe: false});
            this.toggle();
            this.disableViewWhenEditing();
            
        }
    }

    render()
    {
        return(
            <div>
                <EdgeHeader color="indigo darken-3" />
                <FreeBird>
                    <Row>
                    <Col md="10" className="mx-auto float-none white z-depth-1 py-2 px-2">
                        <CardBody>
                        <Row>
                            <div className="container">
                                <div className="row">
                                    <div className="float-right">
                                        <img className="avatar rounded-circle" src={avatar} />
                                        <div className="text-center" ><h4>{this.state.userName}</h4></div>
                                    </div>

                                </div>
                                <div className="row">
                                    <h2>
                                        <strong>My documents</strong>
                                    </h2>
                                </div>
                                <div className="row">
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
                                    </div>
                                    <div className="row">
                                    
                                        <DocumentList Documents = {this.state.documents} onElementClicked = {this.onDocumentClicked} UserId = {this.state.userId} UserName = {this.state.userName} EditedDocument = {this.state.idEditedDocument}/>
                                        
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
                        <Button className={this.canDelete()} onClick={this.deleteDocument}>
                        Delete
                        </Button>{" "}
                        <Button id="saveButton" color="primary" onClick={(event) => this.handleSave(event)}>Save</Button>
                    </ModalFooter>
                     <Chat username = {this.state.userName} documentId = {this.state.idEditedDocument}/>
                    </Modal>
                </div>
        );
    }
}

export default Dashboard;