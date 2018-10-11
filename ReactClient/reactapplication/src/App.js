import React, { Component } from 'react';
import './App.css';
import Navigation from './Components/Navigation/Navigation';
import Footer from './Components/Footer';
import Home from './Components/Home';
import {
  BrowserRouter as Router,
  Route
} from 'react-router-dom';

import Login from './Components/Login/Login';
import HelloWorld from './Components/HelloWorld';

class App extends Component {
  render() {
    return (
      <Router>
        <div>
          <Navigation />
          <div className="container">
              <Route exact path="/" component={Home} />
              <Route exact path="/Login" component={Login} />
              <Route exact path="/HelloWorld" component={HelloWorld} />
              <Footer />
          </div>
        </div>
      </Router>
     
    );
  }
}

export default App;
