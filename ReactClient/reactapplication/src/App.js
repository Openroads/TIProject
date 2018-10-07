import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props){
    super(props);
    this.state = {value: ''};
    this.handleChange = this.handleChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleChange(event){
    this.setState({value: event.target.value});
  }

  handleSubmit(event){
    alert('Your name is: ' + this.state.value);
    event.preventDefault();
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
            <input type="text" name="name" placeholder="Enter your name..." value={this.state.value} onChange={this.handleChange} />
            <input type="submit" value="Submit" />
          </form>
        </div>
        </header>
      </div>
    );
  }
}

export default App;
