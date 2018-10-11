import React from 'react';

class HelloWorld extends React.Component{
    constructor(props){
        super(props);
        this.state = 
        {
            inputUserName: "",
            message: ""
        };

        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    handleSubmit()
    {
        fetch('https://jsonplaceholder.typicode.com/todos/1')
        .then(response => response.json())
        .then(json => alert(json.title))
    }

    render(){
        return(
        <div>
        <h1>Hello world with REST API</h1>
          <form onSubmit={this.handleSubmit}>
            <div className="form-group">
              <input type="text" id="inputUserName" className="form-control" placeholder="Enter your name..." value={this.state.value} onChange={this.handleChange} />
            </div>
            <input type="submit" className="btn btn-primary" value="Submit" />
          </form>
        </div>
        );
    }
}

export default HelloWorld;