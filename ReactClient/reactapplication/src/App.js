import React, { Component } from 'react';
import './App.css';
import Navigation from './Components/Navigation/Navigation';
import Footer from './Components/Footer';
import Home from './Components/Home';
import {
  BrowserRouter as Router,
  Route,
  Link
} from 'react-router-dom';

import Login from './Components/Login/Login';

class App extends Component {
  render() {
    return (
      <Router>
        <div>
          <Navigation />
          <div className="container">
              <Route exact path="/" component={Home} />
              <Route exact path="/Login" component={Login} />
              <Footer />
          </div>
        </div>
      </Router>
     
    );
  }
}

export default App;
