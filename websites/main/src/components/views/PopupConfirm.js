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
        <div className="text-center">
          <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.props.onButtonClick}>OK</Button>
          <Button bsClass="btn custom-btn custom-btn-lg" onClick={this.props.onHide}>Cancle</Button>
        </div>
        </Modal.Footer>
      </Modal>
    )
  }

}

export default Popup;
