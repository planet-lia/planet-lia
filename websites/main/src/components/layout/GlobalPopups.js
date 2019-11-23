import React from 'react';

import PopupSubmit from '../views/PopupSubmit';
import PopupChallenge from '../views/PopupChallenge';
import PopupInvite from '../views/PopupInvite';

import { connect } from 'react-redux';
import { popupsActions } from '../../utils/actions/popupsActions';

const GlobalPopups = (props) => {
  return (
    <div>
      <PopupSubmit
        dialogClassName="custom-popup sign-in"
        show={props.showSignInPopup}
        onHide={closePopups}
        heading="Sign In"
        buttonText="Sign In"
        formType="sign-in"
      />
      <PopupSubmit
        dialogClassName="custom-popup sign-up"
        show={props.showRegPopup}
        onHide={closePopups}
        heading="Sign Up"
        buttonText="Sign Up"
        formType="sign-up"
      />
      <PopupChallenge
        show={props.showChallengePopup}
        onHide={closePopups}
      />
      <PopupInvite
        show={props.showInvitePopup}
        onHide={closePopups}
      />
    </div>
  );

  async function closePopups() {
    await props.dispatch(popupsActions.hidePopups())
  }
}

function mapStateToProps(state) {
    const { showRegPopup, showSignInPopup, showChallengePopup, showInvitePopup } = state.popups;
    return {
        showRegPopup,
        showSignInPopup,
        showChallengePopup,
        showInvitePopup
    };
}

export default connect(mapStateToProps)(GlobalPopups);
