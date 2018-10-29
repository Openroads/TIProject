import React from "react";

import "./HomePage.css";
import axios from 'axios';
import {
  Input,
  Button,
  EdgeHeader,
  FreeBird,
  Col,
  Row,
  CardBody,
  Modal,
  ModalBody,
  ModalFooter,
  Fa
} from "mdbreact";

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
      user: [],
      errorMessage: ''
    };

    this.toggle = this.toggle.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.handleClick = this.handleClick.bind(this);
    this.showError = this.showError.bind(this);
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

setStateAsync(state) {
  return new Promise((resolve) => {
    this.setState(state, resolve)
  });
}

  async handleClick(event){
  
    event.preventDefault();
    var response = await axios.post("http://localhost:8000/online-docs/users/login/", {
      username: this.state.username,
      password: this.state.password
    })
    
    var userName = response.data.username;
    var id = response.data.id;
    if(userName != undefined && userName != "")
    {
      sessionStorage.setItem('userId', id);
      sessionStorage.setItem('username', userName);
      this.props.history.push(
        {
          pathname: '/Dashboard'
        }
      )
    }
    else
    {
      this.showError();
    }
  }

  showError()
  {
    document.getElementById('errorMessage').textContent = 'Incorrect username or password!';
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
                <label id="errorMessage" className="text-danger"></label>
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
