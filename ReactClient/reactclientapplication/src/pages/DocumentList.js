import React from 'react';
import axios from 'axios';

class DocumentList extends React.Component{

    constructor(props)
    {
        super(props);

        this.state = {
            user: '',
            documents: [],
        }

        this.handleClick = this.handleClick.bind(this);
        this.resetState = this.resetState.bind(this);
        this.isModifing = this.isModifing.bind(this);
        this.isDisabled = this.isDisabled.bind(this);

    }

    handleClick(event){
        event.preventDefault();

        axios.get(`http://localhost:8000/online-docs/document/${event.target.id}/`)
        .then(response => this.props.callbackFromParent(response.data))
    }

    resetState()
    {
        this.setState({documents: []});
    }

    isModifing(doc)
    {
        if(doc.name == undefined)
        {
            return '';
        }
        if(doc.name.length > 0)
        {
            return 'a fa-lock lockList';
        }
        else{
            return '';
        }
    }

    isDisabled(doc)
    {
        if(doc.name == undefined)
        {
            return 'list-group-item list-group-item-action';
        }
        if(doc.name.length > 0)
        {
            return 'list-group-item list-group-item-action disabled';
        }
        else{
            return 'list-group-item list-group-item-action';
        }
    }

    render()
    {
        const documentItems = this.props.Documents.map(doc =>
            <li id={doc.id} onClick={this.handleClick} className={this.isDisabled(doc)} key={doc.id}>{doc.title}<i className={this.isModifing(doc)} aria-hidden="true">{doc.name}</i></li>)
        return(
            <div className="list-group">
                {documentItems}
            </div>
        );
    }
}

export default DocumentList;