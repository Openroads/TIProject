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
            <div class="form-group">
              <input type="text" name="name" class="form-control" placeholder="Enter your name..." value={this.state.value} onChange={this.handleChange} />
              <input type="submit" class="btn btn-primary" value="Submit" />
            </div>
          </form>
        </div>
        </header>
      </div>
    );
  }
}

export default App;
