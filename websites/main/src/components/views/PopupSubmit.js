import React, { Component } from 'react';
import { Modal, Button } from 'react-bootstrap';

import SignInForm from '../forms/SignInForm';
import SignUpForm from '../forms/SignUpForm';
import LoadingButton from '../elems/LoadingButton';

class PopupSubmit extends Component {
  constructor(props) {
    super(props);
    this.state = {
      formSubmitButtonId: "btn-submit-" + this.props.formType,
      newHeading: "",
      newButtonText: "",
      disableButton: false,
      isSuccess: false,
      isSending: false
    }
  }

  getForm = () => {
    if(this.props.formType==="sign-in"){
      return (
        <SignInForm
          submitButtonId={this.state.formSubmitButtonId}
          closePopup={this.props.onHide}
          setHeading={(heading) => this.setState({newHeading: heading})}
          setButtonText={(buttonText) => this.setState({newButtonText: buttonText})}
          disableButton={() => this.setState({disableButton: true})}
        />
      );
    } else if (this.props.formType==="sign-up"){
      return (
        <SignUpForm
          submitButtonId={this.state.formSubmitButtonId}
          closePopup={this.props.onHide}
          setSuccess={() => this.setState({isSuccess: true})}
          setIsSending={(isSending) => this.setState({isSending: isSending})}
        />
      )
    }
  }

  popupSubmitButton = () => {
    const { formSubmitButtonId, newButtonText, isSending, isSuccess, disableButton } = this.state;
    const { buttonText, onHide } = this.props;

    if(isSending){
      return <LoadingButton bsClass="btn custom-btn custom-btn-lg">{newButtonText ? newButtonText : buttonText}</LoadingButton>
    } else if(isSuccess){
      return <Button bsClass="btn custom-btn custom-btn-lg" onClick={onHide}>OK</Button>
    } else {
      return (
        <label
          className="btn custom-btn custom-btn-lg"
          htmlFor={formSubmitButtonId}
          disabled={disableButton}
        >
          {newButtonText ? newButtonText : buttonText}
        </label>
      )
    }
  }


  render(){
    const { newHeading } = this.state;
    const { dialogClassName, show, onHide, heading } = this.props;

    return(
      <Modal dialogClassName={dialogClassName} show={show} onHide={onHide}>
        <Modal.Header className="custom-modal-header" closeButton>
          <Modal.Title>{newHeading ? newHeading : heading}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          {this.getForm()}
        </Modal.Body>
        <Modal.Footer>
          {this.popupSubmitButton()}
        </Modal.Footer>
      </Modal>
    )
  }

}

export default PopupSubmit;
