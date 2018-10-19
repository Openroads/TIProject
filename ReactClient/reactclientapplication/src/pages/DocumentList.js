import React from 'react';

class DocumentList extends React.Component{
    render()
    {
        return(
            <div class="list-group">
                <a href="#!" class="list-group-item list-group-item-action">
                    Document 1
                </a>
                <a href="#!" class="list-group-item list-group-item-action">Document 2</a>
                <a href="#!" class="list-group-item list-group-item-action">Document 3</a>
                <a href="#!" class="list-group-item list-group-item-action">Document 4</a>
                <a href="#!" class="list-group-item list-group-item-action">Document 5</a>
            </div>
        );
    }
}

export default DocumentList;