import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';

import LoadingButton from '../elems/LoadingButton';
import SubmitCodeText from '../elems/SubmitCodeText';
import NoAuthModal from '../elems/NoAuthModal';

import api from '../../utils/api';

import { connect } from 'react-redux';

class PopupChallenge extends Component {
  constructor(props){
    super(props);
    this.state = {
      isSent: false,
      loading: false,
      isBotProcessing: true,
      error: null
    };
  }

  submitCode = async () => {
    const {code, language} = this.props;

    this.setState({
      loading: true,
      error: null
    })

    try {
      await api.game.submitBasicSource(btoa(code), language);
      this.setState({
        isSent: true,
        loading: false
      })
    } catch (err) {
      if(err.response) {
        let errorMsg = "";
        if(err.response.data.errors.length>0){
          const errorData = err.response.data.errors[0]
          errorMsg = errorData.field + ": " + errorData.msg;
        } else if(err.response.data.error) {
          errorMsg = err.response.data.error
        }
        console.log(err.response.data);
        this.setState({
          error: errorMsg,
          loading: false
        });
      } else {
        this.setState({
          error: "Network Error",
          loading: false
        });
        console.log(err.message);
      }
    }
  }

  popupSubmitButton = () => {
    const { loading, isSent, isBotProcessing } = this.state;
    if(loading){
      return <LoadingButton bsClass="btn custom-btn custom-btn-lg">Submit</LoadingButton>
    } else if(isSent){
      return <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.closePopup}>OK</Button>
    } else if(isBotProcessing) {
      return <Button bsClass="btn custom-btn custom-btn-lg" disabled>Submit</Button>
    } else {
      return <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.submitCode}>Submit</Button>
    }
  }

  closePopup = () => {
    this.setState({
      isSent: false,
      loading: false,
      isBotProcessing: true,
      error: null
    });

    this.props.onHide();
  }

  render() {
    const { show, isAuthenticated, username } = this.props;
    const { isSent, error } = this.state;
    const heading = "Submit Your Code"
    if(isAuthenticated){
      return(
        <Modal dialogClassName="custom-popup pop-editor-sm pop-text" show={show} onHide={this.closePopup}>
          <Modal.Header className="custom-modal-header" closeButton>
            <Modal.Title>{heading}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <SubmitCodeText
              setIsBotProcessing={(isProcessing) => this.setState({isBotProcessing: isProcessing})}
            />
            {isSent
              ? <p className="clr-em resp-msg">
                  <span>Your code was submitted! Follow the progress on your </span>
                  <a href={"/user/" + username} target="_blank" rel="noopener noreferrer">profile page</a>
                  .
                </p>
              : null
            }
            {error!==null
              ? <p className="text-danger resp-msg">{error}</p>
              : null}
          </Modal.Body>
          <Modal.Footer>
            <div className="text-center">
              {this.popupSubmitButton()}
            </div>
          </Modal.Footer>
        </Modal>
      )
    } else {
      return(
        <NoAuthModal {...this.props} heading={heading} onHide={this.closePopup}>
          <p>You need to sign in to submit your code! If you do not have an account yet, you need to sign up.</p>
        </NoAuthModal>
      )
    }

  }

}

function mapStateToProps(state) {
    const { isAuthenticated, username } = state.authentication;
    return {
        isAuthenticated,
        username
    };
}

export default connect(mapStateToProps)(PopupChallenge);
