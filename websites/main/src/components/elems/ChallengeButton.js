import React from 'react';
import { Button } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { connect } from 'react-redux';
import { popupsActions } from '../../utils/actions/popupsActions';

const ChallengeButton = (props) => {
  return (
    props.username===props.opponent
      ? (
        <span>-</span>
      ) : (
        <Button
          className={"btn custom-btn " + props.className}
          onClick={() => showPopup(props.opponent, props.opponentId)}
        >
          <span><FontAwesomeIcon icon="chess-rook" /></span>
          <span> Challenge</span>
        </Button>
      )
  )

  async function showPopup(opponent, opponentId) {
    await props.dispatch(popupsActions.showChallenge(opponent, opponentId))
  }

}

function mapStateToProps(state) {
  const { username } = state.authentication;
  return {
    username
  };
}

export default connect(mapStateToProps)(ChallengeButton);
