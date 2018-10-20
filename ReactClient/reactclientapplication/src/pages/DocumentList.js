import React from 'react';

class DocumentList extends React.Component{

    constructor(props)
    {
        super(props);

        this.state = {
            user: '',
            documents: [],
        }

        this.handleClick = this.handleClick.bind(this);

    }

    handleClick(event){
        event.preventDefault();
        this.props.callbackFromParent(event.target.id);
    }

    render()
    {
        const documentItems = this.props.Documents.map(doc =>
            <li id={doc.id} onClick={this.handleClick} class="list-group-item list-group-item-action" key={doc.id}>{doc.title}</li>)
        return(
            <div class="list-group">
                {documentItems}
            </div>
        );
    }
}

export default DocumentList;