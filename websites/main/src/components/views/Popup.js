import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';

class Popup extends Component {

  render(){
    return(
      <Modal dialogClassName={this.props.dialogClassName} show={this.props.show} onHide={this.props.onHide}>
        <Modal.Header className="custom-modal-header" closeButton>
          <Modal.Title>{this.props.heading}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {this.props.children}
        </Modal.Body>
        <Modal.Footer>
          <Button bsClass={this.props.center ? "btn custom-btn custom-btn-lg center-block" : "btn custom-btn custom-btn-lg"} onClick={this.props.onButtonClick}>{this.props.buttonText}</Button>
        </Modal.Footer>
      </Modal>
    )
  }

}

export default Popup;
