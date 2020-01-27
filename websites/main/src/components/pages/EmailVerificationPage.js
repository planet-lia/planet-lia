import React, { Component } from 'react';
import queryString from 'query-string';
import { Redirect } from "react-router-dom"

import { authActions } from '../../utils/actions/authActions'

import { connect } from 'react-redux';
import {Button} from "react-bootstrap";

class EmailVerificationPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isCheckingForCode: true,
      codeExists: false,
      confirmationCode: ""
    }
  }

  componentDidMount = () => {
    const parms = queryString.parse(this.props.location.search)
    if(parms.code){
      this.setState({
        codeExists: true,
        isCheckingForCode: false,
        confirmationCode: parms.code
      });
      // this.confirmEmailFromCode(parms.code);
    } else {
      this.setState({
        codeExists: false,
        isCheckingForCode: false
      });
    }
  }

  confirmEmailFromCode = async (code) => {
    await this.props.dispatch(authActions.confirmEmail(code));
  }

  getMessage = () => {
    const { isCheckingForCode } = this.state;
    const { isVerifing, isAuthenticated, isVerified, error } = this.props;
    let msg = "";

    if(isVerifing || isCheckingForCode){
      msg = "Verifing...";
    } else {
      if(error){
        msg = "Verification failed, with error: " + error;

      } else if(isAuthenticated && isVerified){
        msg = "Your email was successfully verified!";
      } else {
        msg = <div style={{paddingTop: "15px"}}>
          <h3>I hereby confirm my email.</h3>
          <Button bsClass="btn custom-btn custom-btn-lg btn-invite-lead"
                  onClick={() => this.confirmEmailFromCode(this.state.confirmationCode)}>Confirm</Button>
        </div>
      }
    }
    return msg;

  }

  render(){
    const { isCheckingForCode, codeExists } = this.state;
    return (
        <div className="container">
          {(codeExists || isCheckingForCode)
              ? (<div className="text-center">{this.getMessage()}</div>)
              : (<Redirect to="/" />)
          }
        </div>
    );
  }
}

function mapStateToProps(state) {
  const { isVerifing, isAuthenticated, isVerified, error } = state.authentication;
  return {
    isVerifing,
    isAuthenticated,
    isVerified,
    error
  };
}

export default connect(mapStateToProps)(EmailVerificationPage);
