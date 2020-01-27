import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';

import LoadingButton from '../elems/LoadingButton';
import ChallengeText from '../elems/ChallengeText';
import NoAuthModal from '../elems/NoAuthModal';

import api from '../../utils/api';

import { connect } from 'react-redux';

class PopupChallenge extends Component {
  constructor(props){
    super(props);
    this.state = {
      cLeft: 0,
      isSent: false,
      textIsReady: false,
      loadingData: false,
      error: null
    };
  }

  postChallenge = async () => {
    this.setState({loadingData: true});
    try {
      await api.game.challengeUser(this.props.opponentId);
      this.setState({
        isSent: true,
        loadingData: false,
        error: null
      });
    } catch(err) {
      if(err.response){
        this.setState({
          loadingData: false,
          error: err.response.data.error
        });
      } else {
        this.setState({
          loadingData: false,
          error: "Network Error"
        });
        console.log(err.message);
      }
    }
  }

  popupChallengeButton = () => {
    const { cLeft, loadingData, isSent, textIsReady } = this.state;
    if(loadingData || !textIsReady){
      return <LoadingButton bsClass="btn custom-btn custom-btn-lg">Send Challenge</LoadingButton>
    } else if(isSent){
      return <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.props.onHide}>OK</Button>
    } else if(cLeft>0){
      return <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.postChallenge}>Send Challenge</Button>
    } else {
      return <Button bsClass="btn custom-btn custom-btn-lg" disabled>Send Challenge</Button>
    }
  }

  handleReady = (isReady, success = false, data = null) => {
    if(isReady){
      if(success){
        this.setState({
          cLeft: data,
          textIsReady: true,
          error: null
        })
      } else {
        this.setState({
          textIsReady: true,
          error: "Network Error"
        })
      }
    } else {
      this.setState({
        cLeft: 0,
        isSent: false,
        textIsReady: false,
        loadingData: false,
        error: null
      })
    }
  }

  render() {
    const { show, onHide, isAuthenticated } = this.props;
    const { isSent, error } = this.state;
    const heading = "Challenge"
    if(isAuthenticated){
      return(
        <Modal dialogClassName="custom-popup pop-challenge pop-text" show={show} onHide={onHide}>
          <Modal.Header className="custom-modal-header" closeButton>
            <Modal.Title>{heading}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <ChallengeText isSent={isSent} ready={(success, data) => this.handleReady( true, success, data)} setNotReady={() => this.handleReady(false)} />
            {isSent
              ? <p className="clr-em resp-msg">The challenge was sent! Follow the progress of the challange on your profile page under challanges.</p>
              : null
            }
            {error!==null
              ? <p className="text-danger resp-msg">{error}</p>
              : null}
          </Modal.Body>
          <Modal.Footer>
            <div className="text-center">
              {this.popupChallengeButton()}
            </div>
          </Modal.Footer>
        </Modal>
      )
    } else {
      return(
        <NoAuthModal {...this.props} heading={heading}>
          <p>You need to sign in to send a challenge! If you do not have an account yet, you need to sign up and upload a bot.</p>
        </NoAuthModal>
      )
    }

  }

}

function mapStateToProps(state) {
    const { isAuthenticated } = state.authentication;
    const { opponentId } = state.popups;
    return {
        isAuthenticated,
        opponentId
    };
}

export default connect(mapStateToProps)(PopupChallenge);
