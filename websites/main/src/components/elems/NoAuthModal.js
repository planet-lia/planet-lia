import React from 'react';
import { Modal, Button } from 'react-bootstrap';

import { connect } from 'react-redux';
import { popupsActions } from '../../utils/actions/popupsActions';

const NoAuthModal = (props) => {
  const { show, onHide, heading, children, dispatch } = props;

  const onClick = (dispatchAction) => {
    dispatch(dispatchAction);
    onHide();
  }

  return(
    <Modal dialogClassName="custom-popup pop-noauth pop-text" show={show} onHide={onHide}>
      <Modal.Header className="custom-modal-header" closeButton>
        <Modal.Title>{heading}</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        {children}
      </Modal.Body>
      <Modal.Footer>
        <div className="text-center">
          <Button bsClass="btn custom-btn custom-btn-lg" onClick={() => onClick(popupsActions.showSignIn())}>Sign In</Button>
          <Button bsClass="btn custom-btn custom-btn-lg" onClick={() => onClick(popupsActions.showRegistration())}>Sign Up</Button>
        </div>
      </Modal.Footer>
    </Modal>
  )
}

export default connect()(NoAuthModal);
