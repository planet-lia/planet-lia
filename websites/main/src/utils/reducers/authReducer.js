import { actionTypesAuth } from '../constants/actionTypesAuth';

const initialState = { isLoggedOut: true };

export function authentication(state = initialState, action) {
  switch (action.type) {
  case actionTypesAuth.LOGIN_REQUEST:
    return {
      isLoggingIn: true,
      username: action.username
    };
  case actionTypesAuth.LOGIN_SUCCESS:
    return {
      isAuthenticated: true,
      username: action.username,
      userId: action.userId
    };
  case actionTypesAuth.LOGIN_FAILURE:
    return {
      error: action.error
    };
  case actionTypesAuth.LOGOUT:
    return {
      isLoggedOut: true
    };
  case actionTypesAuth.AUTH_REQUEST:
    return {
      isCheckingAuth: true
    };
  case actionTypesAuth.AUTH_SUCCESS:
    return {
      isAuthenticated: true,
      username: action.username,
      userId: action.userId
    };
  case actionTypesAuth.AUTH_FAILURE:
    return {
      initialAuthError: action.error
    };
  case actionTypesAuth.CONFIRM_EMAIL_REQUEST:
    return {
      isVerifing: true,
    };
  case actionTypesAuth.CONFIRM_EMAIL_SUCCESS:
    return {
      isAuthenticated: true,
      isVerified: true,
      username: action.username,
      userId: action.userId
    };
  case actionTypesAuth.CONFIRM_EMAIL_FAILURE:
    return {
      error: action.error
    };
  default:
    return state;
  }
}
