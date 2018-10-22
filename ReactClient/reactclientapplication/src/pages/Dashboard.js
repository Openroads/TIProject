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
            editingBy: '',
            editingDocument: ''
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
        this.handleDocumentClick = this.handleDocumentClick.bind(this);
        this.getDocuments();
    }

    async getDocuments()
    {
        const response = await axios.get('http://localhost:8000/online-docs/documents/');
        this.setState({documents: response.data})
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
        var domain = "http://localhost:8000/online-docs/document/";
        var id = this.state.idEditedDocument.toString();
        var endOf = "/stop-editing/";
        var address = domain.concat(id, endOf);
        const response = await axios.post(address, {
            headers: {'Content-Type': 'application/x-www-form-urlencoded' }
                });
        console.log("End editing: ", response);

        this.setState({isEditedByMe: false});
    }

    resetInput(){
        document.getElementById("fileTitle").value = "";
        document.getElementById("fileContent").value = "";
        this.setState({fileContent: '', fileTitle: ''});
    }

    async deleteDocument(event){
        event.preventDefault();
        const response = await axios.delete(`http://localhost:8000/online-docs/document/${this.state.idEditedDocument}/`, {
                
                    title: this.state.fileTitle,
                    content: this.state.fileContent,
                    version: 1,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded' }
                
            });
        console.log("Deleted: ", response);

        

        // removing from the list
        const newState = this.state;
        const deletedIndex = newState.documents.findIndex(x => x.id == newState.idEditedDocument);
        if(deletedIndex == -1) return;
        newState.documents.splice(deletedIndex, 1);
        this.setState(newState);

        this.toggle();
    }

    async handleSave(event){

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
           const response = await axios.put(`http://localhost:8000/online-docs/document/${this.state.idEditedDocument}/`, {
                
                    title: this.state.fileTitle,
                    content: this.state.fileContent,
                    version: 1,
            });

           console.log("Updated: ", response);
           
           const newState = this.state;
           axios.get('http://localhost:8000/online-docs/documents/')
            .then(response => {newState.documents = response.data})

            
            this.setState(newState);
        }

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
                    headers: {'Content-Type': 'application/x-www-form-urlencoded' }
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

    async handleDocumentClick(event) {

        event.preventDefault();
        var respondedData;
        const response = await axios.get(`http://localhost:8000/online-docs/document/${event.target.id}/`);
        this.setState({editingDocument: response.data});
        
        let title = this.state.editingDocument.title;
        let content = this.state.editingDocument.content;
        let id = this.state.editingDocument.id;
        let editingby = this.state.editingDocument.editingBy;

        // Callback when clickin position on the list
        if(title !== "" && content !== "" && title !== undefined && content !== undefined && id != "")
        {
            if(editingby == undefined || editingby == ""){
                const editResponse = await axios.post(`http://localhost:8000/online-docs/document/${id}`+ '/editing-by/1/', {
                    headers: {'Content-Type': 'application/x-www-form-urlencoded' }
                });

                console.log("Editing: ", editResponse.data)

                this.setState({fileTitle: title, fileContent: content, idEditedDocument: id, isEditedByMe: true});
                this.toggle();
            }
            else{
                this.toggle();
                this.setState({fileTitle: title, fileContent: content, editingBy: editingby});
                
                this.disableViewWhenEditing();
            }
            
            
            
        }
    }

    isModifing(doc)
    {
        if(doc.editingBy == undefined)
        {
            return '';
        }
        if(doc.editingBy.length > 0)
        {
            return 'fa fa-lock lockList';
        }
        else{
            return '';
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
                                {
                                    this.state.documents.map(doc => {
                                        return(
                                            <li id={doc.id} onClick={this.handleDocumentClick} className="list-group-item list-group-item-action" key={doc.id}>
                                        {doc.title}
                                        <i className={this.isModifing(doc)} aria-hidden="true">  {doc.editingBy}</i>
                                        </li>
                                        );
                                    })
                                }
                                 
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
                    </Modal>
                    </div>
        );
    }
}

export default Dashboard;