import React from 'react';

class EditingBy extends React.Component {

    constructor(props)
    {
        super(props);

        this.isModifing = this.isModifing.bind(this);
    }

    isModifing(editedBy)
    {
        if(editedBy == undefined)
        {
            return '';
        }
        if(editedBy.length  > 0)
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
            <i id = {this.props.Id + "State"} className={this.isModifing(this.props.EditedBy)} aria-hidden="true">  {this.props.EditedBy}</i>
        );
    }

}

export default EditingBy;