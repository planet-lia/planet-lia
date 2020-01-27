import { actionTypesPopups } from '../constants/actionTypesPopups';

const initialState = {};

export function popups(state = initialState, action) {

  switch (action.type) {
    case actionTypesPopups.REGISTRATION_SHOW:
      return {
        showRegPopup: true
      };
    case actionTypesPopups.REGISTRATION_EARLY_SHOW:
      return {
        showRegPopup: true,
        earlyRegistration: true
      };
    case actionTypesPopups.SIGNIN_SHOW:
      return {
        showSignInPopup: true
      };
    case actionTypesPopups.CHALLENGE_SHOW:
      return {
        showChallengePopup: true,
        opponent: action.opponent,
        opponentId: action.opponentId
      };
    case actionTypesPopups.INVITE_SHOW:
      return {
        showInvitePopup: true
      }
    case actionTypesPopups.POPUPS_HIDE:
      return {};
    default:
      return state;
  }
}
