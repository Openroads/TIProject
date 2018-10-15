import React from "react";

import "./HomePage.css";
import axios from 'axios';
import {
  Input,
  Button,
  EdgeHeader,
  FreeBird,
  Container,
  Col,
  Row,
  CardBody,
  Card,
  Modal,
  ModalBody,
  ModalFooter,
  Fa
} from "mdbreact";
const NavLink = require("react-router-dom").NavLink;

class HomePage extends React.Component {
  constructor(props) 
  {
    super(props);
    this.state = {
      modal: false,
      username: "",
      password: "",
      name: "",
      surname: "",
      user: []
    };

    this.toggle = this.toggle.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
  }

  toggle() {
    this.setState({
      modal: !this.state.modal
    });
  }

  handleChange = event => {
    this.setState({
        [event.target.id] : event.target.value
    });
}

  handleClick(event){

    var self = this;
    

    //event.preventDefault();
    axios.get(`https://localhost:44322/api/Login/${this.state.username}/${this.state.password}`)
    .then(response => 
      {
        self.setState({user: response.data, name: response.data.name, surname: response.data.surname});
      })

    alert(this.state.user);
    
    if(this.state.name != "" && this.state.surname != "")
    {
      this.props.history.push(
        {
          pathname: '/Dashboard',
          state: {user: this.state.username}
        }
      )
    }
    




    /*
    var apiBaseUrl = "http://localhost:4000/api/";
    var self = this;
    var payload={
      "email":this.state.username,
      "password":this.state.password
    }
    axios.post(apiBaseUrl+'login', payload)
    .then(function (response) {
      console.log(response);
      if(response.data.code == 200){
        console.log("Login successfull");
      // redirect to another page
    }
    else if(response.data.code == 204){
      console.log("Username password do not match");
      alert("username password do not match")
    }
    else{
      console.log("Username does not exists");
      alert("Username does not exist");
    }
    })
      .catch(function (error) {
      console.log(error);
    }); */
    }

  render() {
    return (
      <div>
        <EdgeHeader color="indigo darken-3" />
        <FreeBird>
          <Row>
            <Col
              md="10"
              className="mx-auto float-none white z-depth-1 py-2 px-2"
            >
              <CardBody>
                <h2 className="h2-responsive mb-4">
                  <strong>Documents Online</strong>
                </h2>
                <p>Create your online documents and stay always up to date</p>
                <Row className="d-flex flex-row justify-content-center row">
                  <a
                    className="border nav-link border-light rounded mr-1"
                    onClick={this.toggle}
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    <Fa icon="user" className="mr-2" />
                    Sign in
                  </a>
                </Row>
              </CardBody>
            </Col>
          </Row>
        </FreeBird>
        <Modal
              isOpen={this.state.modal}
              toggle={this.toggle}
              className="cascading-modal"
            >
              <div className="modal-header primary-color white-text">
                <h4 className="title">
                  <Fa className="fa fa-pencil" /> Sign in
                </h4>
                <button type="button" className="close" onClick={this.toggle}>
                  <span aria-hidden="true">×</span>
                </button>
              </div>
              <ModalBody className="grey-text">
                <Input
                  size="sm"
                  label="Login"
                  icon="user"
                  group
                  type="text"
                  validate
                  error="wrong"
                  success="right"
                  id="username"
                  onChange = {this.handleChange}
                />
                <Input
                  size="sm"
                  type="password"
                  label="Password"
                  icon="key"
                  id="password"
                  onChange = {this.handleChange}
                />
              </ModalBody>
              <ModalFooter>
                <Button color="secondary" onClick={this.toggle}>
                  Close
                </Button>{" "}
                <Button color="primary" onClick={(event) => this.handleClick(event)}>Sign in</Button>
              </ModalFooter>
            </Modal>
      </div>
    );
  }
}

export default HomePage;
