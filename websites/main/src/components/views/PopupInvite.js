import React from 'react';
import { Modal, Button } from 'react-bootstrap';

import InviteText from '../elems/InviteText';
import NoAuthModal from '../elems/NoAuthModal';

import { connect } from 'react-redux';

const PopupInvite = (props) => {
    const { show, onHide, isAuthenticated, username } = props;
    const heading = "Invite";

    if(isAuthenticated){
      return(
        <Modal dialogClassName="custom-popup pop-invite pop-text" show={show} onHide={onHide}>
          <Modal.Header className="custom-modal-header" closeButton>
            <Modal.Title>{heading}</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <InviteText username={username}/>
          </Modal.Body>
          <Modal.Footer>
            <div className="text-center">
              <Button bsClass="btn custom-btn custom-btn-lg" onClick={onHide}>OK</Button>
            </div>
          </Modal.Footer>
        </Modal>
      )
    } else {
      return(
        <NoAuthModal {...props} heading={heading}>
          <p>You need to sign in to send an invite!</p>
        </NoAuthModal>
      )
    }
}

function mapStateToProps(state) {
    const { isAuthenticated, username } = state.authentication;
    return {
        isAuthenticated,
        username
    };
}

export default connect(mapStateToProps)(PopupInvite);
