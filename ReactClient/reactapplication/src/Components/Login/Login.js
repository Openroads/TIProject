import React from 'react';

class Login extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            login: "",
            password: ""
        };
    }

    validateForm(){
        return this.state.login > 0 && this.state.password.length > 5;
    }

    handleChange = event => {
        this.setState({
            [event.target.id] : event.target.value
        });
    }

    render(){
        return(
            <form>
                <div className="form-group">
                    <label>Login</label>
                    <input type="text"className="form-control" id="login" placeholder="Enter your login" value={this.state.login} onChange={this.handleChange}/>
                </div>
                <div className="form-group">
                    <label>Password</label>
                    <input type="password" className="form-control" id="password" placeholder="Enter your password" value={this.state.password} onChange={this.handleChange}/>
                </div>
                <button disables={!this.validateForm()} type="submit" className="btn btn-primary">Submit</button>
            </form>
        );
    }
}

export default Login;