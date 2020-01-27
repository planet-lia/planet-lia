import React, {Component} from 'react';
import {FormGroup, FormControl, ControlLabel, Button, HelpBlock} from 'react-bootstrap';
import isEmpty from 'lodash/isEmpty';

import { validators } from '../../utils/helpers/validators';

import api from '../../utils/api';

import { connect } from 'react-redux';
import { authActions } from '../../utils/actions/authActions'

class SignInForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      username: "",
      usernameError: null,
      password: "",
      passwordError: null,
      isForgotPw: false,
      email: "",
      emailError: null,
      responseError: null,
      message: null,
      emailIsSent: false

    }
  }

  formSubmit = async (event) => {
    event.preventDefault();

    const {username, password} = this.state;
    const {dispatch, closePopup} = this.props;

    this.setState({
      usernameError: null,
      passwordError: null,
      responseError: null
    });

    if(this.validateForm()){
      const respLogin = await dispatch(authActions.login(username, password));
      if(respLogin.username){
        closePopup();
        this.setState({
          username: "",
          usernameError: null,
          password: "",
          passwordError: null,
          responseError: null
        });

      } else if(respLogin.error){
        this.setState({responseError: respLogin.error});
      } else {
        this.setState({responseError: "Error"});
      }

    }
  }

  validateForm = () => {
    const { username, password } = this.state;
    let errors = {};

    if(username){
      if( !validators.username(username) ){
        errors.usernameError = "Invalid username"
      }
    } else {
      errors.usernameError = "Username required"
    }

    if(password){
      if(!validators.passwordLength(password)){
        errors.passwordError = "Invalid password"
      }
    } else {
      errors.passwordError = "Password required"
    }

    this.setState(errors);
    return isEmpty(errors);
  }

  forgotPwSubmit = async (event) => {
    event.preventDefault();

    const { email } = this.state;

    this.setState({
      emailError: null,
      responseError: null,
      message: null
    });

    if(this.validateForgetPw()){
      try {
        await api.user.resetPasswordEmail(email);
        this.setState({
          email: "",
          emailError: null,
          responseError: null,
          message: "A link was sent to your email. Please follow the link to reset your password. If you don't see the email, check your spam and promotions.",
          emailIsSent: true
        });
        this.props.disableButton();
      } catch(err) {
        if(err.response){
          let msg = JSON.stringify(err.response.data);
          try {
            msg = err.response.data.errors[0].msg;
          } catch (exception) {}

          this.setState({
            emailError: msg,
            isLoading: false
          });
        } else {
          this.setState({
            responseError: "Network Error: ",
            isLoading: false
          });
        }
      }
    }
  }

  validateForgetPw = () => {
    const { email } = this.state;
    let errors = {};

    if(email){
      if( !validators.email(email) ){
        errors.emailError = "Invalid email"
      }
    } else {
      errors.emailError = "Email required"
    }

    this.setState(errors);
    return isEmpty(errors);
  }

  switchForm = () => {
    this.props.setHeading("Forgot Password");
    this.props.setButtonText("Send");
    this.setState({
      isForgotPw: true,
      username: "",
      usernameError: null,
      password: "",
      passwordError: null,
      email: "",
      emailError: null,
      responseError: null,
      message: null,
      emailIsSent: false
    })
  }

  onChange = (event) => {
    this.setState({ [event.target.name]: event.target.value });
  }

  render(){
    const {username, usernameError, password, passwordError, responseError, isForgotPw, email, emailError, message, emailIsSent} = this.state;

    if(isForgotPw) {
      return (
        <form onSubmit={this.forgotPwSubmit} noValidate>
          <FormGroup validationState={emailError ? "error" : null}>
            <ControlLabel>Email</ControlLabel>
            <FormControl
              type="text"
              name="email"
              placeholder="Enter your email"
              value={email}
              onChange={this.onChange}
            />
            {emailError && <HelpBlock>{emailError}</HelpBlock> }
          </FormGroup>
          <Button id={this.props.submitButtonId} type="submit" bsClass="hidden" disabled={emailIsSent}></Button>
          {message
            ? <span className="clr-em">{message}</span>
            : null
          }
          {responseError
            ? (<FormGroup validationState="error">
                <HelpBlock>{responseError}</HelpBlock>
              </FormGroup>)
            : null
          }
        </form>
      )
    } else {
      return (
        <div>
          <form onSubmit={this.formSubmit} noValidate>
            <FormGroup validationState={usernameError ? "error" : null}>
              <ControlLabel>Username</ControlLabel>
              <FormControl
                type="text"
                name="username"
                placeholder="Enter your username"
                value={username}
                onChange={this.onChange}
              />
              {usernameError && <HelpBlock>{usernameError}</HelpBlock> }
            </FormGroup>
            <FormGroup validationState={passwordError ? "error" : null}>
              <ControlLabel>Password</ControlLabel>
              <FormControl
                type="password"
                name="password"
                placeholder="Enter your password"
                value={password}
                onChange={this.onChange}
              />
              {passwordError && <HelpBlock>{passwordError}</HelpBlock>}
            </FormGroup>
            <Button id={this.props.submitButtonId} type="submit" bsClass="hidden"></Button>
            {responseError
              ? (<FormGroup validationState="error">
                  <HelpBlock>{responseError}</HelpBlock>
                </FormGroup>)
              : null
            }
          </form>
          <div className="text-right">
            <a role="button" onClick={this.switchForm}>Forgot password?</a>
          </div>
        </div>
      );
    }

  }

}

function mapStateToProps(state) {
    const { isLoggingIn } = state.authentication;
    return {
        isLoggingIn
    };
}

export default connect(mapStateToProps)(SignInForm);
