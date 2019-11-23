import React, { Component } from 'react';
import { Col, FormGroup, FormControl, ControlLabel, Button, HelpBlock } from 'react-bootstrap';
import queryString from 'query-string';
import { Redirect } from 'react-router-dom';
import Loader from 'react-loader-spinner';
import isEmpty from 'lodash/isEmpty';

import { validators } from '../../utils/helpers/validators';

import api from '../../utils/api';

class ResetPasswordPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      code: "",
      isCheckingForCode: true,
      codeExists: false,
      password: "",
      passwordError: null,
      repeat: "",
      repeatError:null,
      isLoading: false,
      isSent: false,
      message: null,
      reserror: null
    }
  }

  componentDidMount = () => {
    const parms = queryString.parse(this.props.location.search)
    if(parms.code){
      this.setState({
        code: parms.code,
        codeExists: true,
        isCheckingForCode: false
      });
    } else {
      this.setState({
        codeExists: false,
        isCheckingForCode: false
      });
    }
  }

  formSubmit = async (event) => {
    event.preventDefault();

    const {password, code} = this.state;

    this.setState({
      passwordError: null,
      repeatError:null,
      error: null
    });

    if(this.validateForm()){
      try {
        await api.user.resetPassword(code, password);

        this.setState({
          password: "",
          passwordError: null,
          repeat: "",
          repeatError:null,
          isSent: true,
          message: "Your pasword was successfully reset. You can now login with your new password.",
          error: null
        });

      } catch(err) {
        if(err.response){
          let field = "";
          let msg = JSON.stringify(err.response.data);
          try {
            field = err.response.data.errors[0].field;
            msg = err.response.data.errors[0].msg;
          } catch (exception) {}

          this.setState({
            error: field + ": " + msg,
            isLoading: false
          });
          //set up errors for each field
        } else {
          this.setState({
            error: "Network Error",
            isLoading: false
          });
        }
      }
    }
  }

  validateForm = () => {
    const { password, repeat } = this.state;
    let errors = {};

    if(password){
      if(!validators.passwordLength(password)){
        errors.passwordError = "Invalid password"
      } else if(repeat) {
        if(!validators.passwordWithRepeat(password, repeat)) {
          errors.passwordError = "Fields do not match"
          errors.repeatError = "Fields do not match"
        }
      } else {
        errors.repeatError = "Field required"
      }
    } else {
      errors.passwordError = "Field required"
    }

    this.setState(errors);
    return isEmpty(errors);
  }

  getContent = () => {
    const { isLoading, isCheckingForCode, passwordError, repeatError, error, isSent} = this.state;

    if (isLoading || isCheckingForCode) {
      return (
        <Loader
          type="Triangle"
          color="#019170"
          height="100"
          width="100"
        />)
    } else {
      return (
        <Col md={4} mdOffset={4} sm={6} smOffset={3} xs={10} xsOffset={1}>
          <div className="form-page">
            <Col>
              <h3 className="title no-top">Reset Password</h3>
            </Col>
            <form onSubmit={this.formSubmit} noValidate>
              <Col componentClass={FormGroup}>
                <ControlLabel>Password</ControlLabel>
                <FormControl
                  type="password"
                  name="password"
                  placeholder="Create a password"
                  value={this.state.password}
                  onChange={this.onChange}
                />
                {passwordError && <HelpBlock>{passwordError}</HelpBlock> }
              </Col>
              <Col componentClass={FormGroup}>
                <ControlLabel>Repeat Password</ControlLabel>
                <FormControl
                  type="password"
                  name="repeat"
                  placeholder="Repeat password"
                  value={this.state.repeat}
                  onChange={this.onChange}
                />
                {repeatError && <HelpBlock>{repeatError}</HelpBlock> }
              </Col>
              <Col>
                <span className="clr-em">{this.state.message}</span>
                <span className="text-danger">{error}</span>
                <div className="text-right">
                  <Button type="submit" className="btn custom-btn custom-btn-lg" disabled={isLoading && isSent}>Reset</Button>
                </div>
              </Col>
            </form>
          </div>
        </Col>
      )
    }
  }

  onChange = (event) => {
    this.setState({ [event.target.name]: event.target.value });
  }

  render(){
    const { isCheckingForCode, codeExists } = this.state;
    return (
      <div className="container">
        {(codeExists || isCheckingForCode)
          ? (<div>{this.getContent()}</div>)
          : (<Redirect to="/" />)
        }
      </div>
    );
  }
}

export default ResetPasswordPage;
