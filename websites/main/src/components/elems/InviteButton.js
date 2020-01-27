import React from 'react';
import { Button } from 'react-bootstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { connect } from 'react-redux';
import { popupsActions } from '../../utils/actions/popupsActions';

const InviteButton = (props) => {
  return (
      <Button
        className={"btn custom-btn btn-invite " + props.className}
        onClick={() => showPopup()}
      >
        <span><FontAwesomeIcon icon="bullhorn" /></span>
        <span> Invite Friends</span>
      </Button>
  )

  async function showPopup() {
    await props.dispatch(popupsActions.showInvite())
  }

}

export default connect()(InviteButton);
