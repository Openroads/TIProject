import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import axios from 'axios';

class App extends Component {

  constructor(props){
    super(props);
    this.state = 
    {
      value: '',
      userName: '',
      persons: []
    };
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event){
    this.setState({value: event.target.value});
  }

  handleSubmit(event){
    
    const formData = new FormData(event.target);

    //axios
        //.get(`localhost:8000/hello-api/by-name/${this.state.value}`)
        //.then(
          //response => {
            //const per = response.data;
            //this.setState({userName: per});
          //})

          fetch(`http://192.168.1.191:8000/hello-api/by-name/${this.state.value}`)
          .then(response => 
            {
              this.setState({userName: response});
            })

    alert(this.state.userName);
    //event.preventDefault();
  }


  render() {
    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p>
            Urszula Lis, Dariusz Szyszlak, Piotr Makowiec
          </p>

          <div>
          <form onSubmit={this.handleSubmit}>
            <div className="form-group">
              <input type="text" id="name" name="name" className="form-control" placeholder="Enter your name..." value={this.state.value} onChange={this.handleChange} />
              <input type="submit" className="btn btn-primary" value="Submit" />
            </div>
          </form>
        </div>
        </header>
      </div>
    );
  }
}

export default App;
