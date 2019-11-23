import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';

import SubscriptionForm from '../forms/SubscriptionForm';

class SubscriptionPopup extends Component {
  constructor(props) {
    super(props);
    this.state = {
        allowEmailsChecked: false
    };
  }


  handleCheckbox = () => {
    let allow;

    if(this.state.allowEmailsChecked===true){
      allow = false;
    } else {
      allow = true;
    }

    this.setState({
      allowEmailsChecked: allow
    });
  }

  render(){
    return(
      <Modal dialogClassName={this.props.dialogClassName} show={this.props.show} onHide={this.props.onHide}>
        <Modal.Header className="custom-modal-header" closeButton>
          <Modal.Title>{this.props.heading}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <SubscriptionForm onCheckboxChange={this.handleCheckbox} allowEmailsChecked={this.state.allowEmailsChecked}/>
        </Modal.Body>
        <Modal.Footer>
          {this.state.allowEmailsChecked ? (
            <label className="btn custom-btn custom-btn-lg" htmlFor="mc-embedded-subscribe" onClick={this.props.onButtonClick}>{this.props.buttonText}</label>
          ) : (
            <Button bsClass="btn custom-btn custom-btn-lg" title="You have to check the box agreeing you would like to subscribe" disabled>{this.props.buttonText}</Button>
          )}
        </Modal.Footer>
      </Modal>
    )
  }

}

export default SubscriptionPopup;
