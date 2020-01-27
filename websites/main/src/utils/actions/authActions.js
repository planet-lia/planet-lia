import jwtDecode from 'jwt-decode'
import { actionTypesAuth } from '../constants/actionTypesAuth';
import api from '../api';
import setAuthHeader from '../helpers/setAuthHeader';

export const authActions = {
  login,
  logout,
  authenticate,
  confirmEmail
};

function login(username, password) {
  return async dispatch => {
    dispatch(request( username ));
    try {
      const respLogin = await api.auth.login(username, password);
      const decoded = jwtDecode(respLogin.token);
      localStorage.setItem("token", respLogin.token);
      setAuthHeader(respLogin.token);
      return dispatch(success( decoded.data.username, decoded.data.userId ));
    } catch(err) {
      localStorage.removeItem("token");
      setAuthHeader();

      let error;
      if(err.response){
        if(err.response.status===401) {
          error = "Incorrect username or password";
        } else if (err.response.status===400){
          error = "Invalid username or password";
        }
      } else {
        error = "Network Error";
      }
      return dispatch(failure(error));
    }
  }
  function request(username) { return {type: actionTypesAuth.LOGIN_REQUEST, username} }
  function success(username, userId) { return {type: actionTypesAuth.LOGIN_SUCCESS, username, userId} }
  function failure(error) { return {type: actionTypesAuth.LOGIN_FAILURE, error} }
}

function logout() {
  localStorage.removeItem("token");
  setAuthHeader();
  return { type: actionTypesAuth.LOGOUT };
}

function authenticate(token) {
  return async dispatch => {
    dispatch(request());
    try {
      await api.auth.verify(token);
      const decoded = jwtDecode(token);
      setAuthHeader(token);
      return dispatch(success( decoded.data.username, decoded.data.userId ));
    } catch(err) {
      localStorage.removeItem("token");
      setAuthHeader();
      return dispatch(failure(err.toString()));
    }
  }
  function request() { return {type: actionTypesAuth.AUTH_REQUEST} }
  function success(username, userId) { return {type: actionTypesAuth.AUTH_SUCCESS, username, userId} }
  function failure(error) { return {type: actionTypesAuth.AUTH_FAILURE, error} }
}

function confirmEmail(code) {
  return async dispatch => {
    dispatch(request());
    try {
      const respConfirm = await api.user.confirmEmail(code);
      const decoded = jwtDecode(respConfirm.token);
      localStorage.setItem("token", respConfirm.token);
      setAuthHeader(respConfirm.token);
      return dispatch(success( decoded.data.username, decoded.data.userId ));
    } catch(err) {
      localStorage.removeItem("user");
      setAuthHeader();
      return dispatch(failure(err.toString()));
    }
  }
  function request() { return {type: actionTypesAuth.CONFIRM_EMAIL_REQUEST} }
  function success(username, userId) { return {type: actionTypesAuth.CONFIRM_EMAIL_SUCCESS, username, userId} }
  function failure(error) { return {type: actionTypesAuth.CONFIRM_EMAIL_FAILURE, error} }
}
