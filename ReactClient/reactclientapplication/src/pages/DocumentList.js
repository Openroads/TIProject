import React from 'react';
import axios from 'axios';
import Constants from './../Constants';
import EditingBy from './EditingBy';

class DocumentList extends React.Component{

    static documents;
    constructor(props)
    {
        super(props);
        this.state = {
            documents: props.Documents,
            isEditorShown: false
        };

        this.documents = props.Documents;
        this.handleClick = this.handleClick.bind(this);
        this.resetState = this.resetState.bind(this);
        this.setEditingByToDocument = this.setEditingByToDocument.bind(this);
        this.unlockDocument = this.unlockDocument.bind(this);
        this.deleteDocument = this.deleteDocument.bind(this);
        this.addDocument = this.addDocument.bind(this);

        this.socketBroadcast = new WebSocket(Constants.WebSocket.Broadcast);

        this.socketBroadcast.onmessage = e => {
            var data = JSON.parse(e.data);
            var operation = data.operation;

            if(operation == "LOCK")
            {
                var editingBy = data.data.editingBy;
                var editingDocument = data.data.documentId;
                document.getElementById(editingDocument+"State").className = 'fa fa-lock lockList';
                this.setEditingByToDocument(editingDocument, editingBy);
                console.log(data);
            }

            if(operation == "UNLOCK")
            {
                var documentId = data.data.documentId;
                this.unlockDocument(documentId);
            }

            if(operation == "DELETE")
            {
                var documentId = data.data.documentId;
                this.deleteDocument(documentId);
            }

            if(operation == "ADD")
            {
                this.addDocument(data.data.document);
            }
            
        }

        this.socketBroadcast.onclose = function(e){
            console.log("Broadcast closed");
        }
    }

    addDocument(document) {
        var newDocuments = this.state.documents;
        newDocuments.push(document);

        this.setState({documents: newDocuments});

    }

    deleteDocument(documentId) {
        var newDocuments = this.state.documents;
        for(var i = 0; i < newDocuments.length; i++)
        {
            if(newDocuments[i].id == documentId)
            {
                newDocuments.splice(i, 1);
            }
        }

        this.setState({documents: newDocuments});
    }

    unlockDocument(documentId) {
        var newDocuments = this.state.documents;
        for(var i = 0; i < newDocuments.length; i++)
        {
            if(newDocuments[i].id == documentId)
            {
                newDocuments[i].editingBy = "";
            }
        }

        this.setState({documents: newDocuments});
    }

    async handleClick(event){
        event.preventDefault();

        const documentId = event.target.id.replace("State", "");
        // Get edited document
        const response = await axios.get(`${Constants.Rest.Document}${documentId}/`);

        var edited = response.data.editingBy;

        if(edited == undefined || edited == "")
        {
                // Set document as edited
            const editResponse = await axios.post(`${Constants.Rest.Document}${documentId}`+ `${Constants.Rest.EditingBy}` + `${this.props.UserId}/`, {
                headers: {'Content-Type': 'application/x-www-form-urlencoded' }
            });

            this.props.onElementClicked(response);

            var messageObject = {
                operation: "LOCK",
                data: {
                    documentId: documentId,
                    editingBy: this.props.UserName
                }
            }

            var messageString = JSON.stringify(messageObject);
            console.log('{"operation":"LOCK","data":{"documentId":19,"editingBy":"admin"}}');
            console.log(messageString);
            this.socketBroadcast.send(messageString);
        }
        else {
            this.props.onElementClicked(response);
        }

        

        
    }

    componentWillReceiveProps(props) {
        this.setState({documents: props.Documents});
    }

    setEditingByToDocument(id, editingBy) {

        var newDocuments = this.state.documents;
        for(var i = 0; i < newDocuments.length; i++)
        {
            if(newDocuments[i].id == id)
            {
                newDocuments[i].editingBy = editingBy;
            }
        }

        this.setState({documents: newDocuments});
    }

    resetState()
    {
        this.setState({documents: []});
    }

    render()
    {
        // this.documents = this.props.Documents;
        const documentItems = this.state.documents.map(doc =>
            <li id={doc.id} onClick={this.handleClick} className="list-group-item list-group-item-action listWidth" key={doc.id}>
            {doc.title}
            <EditingBy Id = {doc.id} EditedBy = {doc.editingBy} ClassStyle={doc.style}/>
            </li>)
        return(
            <div className="documentList">
                {documentItems}
            </div>
        );
    }
}

export default DocumentList;