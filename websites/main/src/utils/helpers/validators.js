import { validationConst } from '../constants/validationConst';

export const validators = {
  length,
  username,
  email,
  usernameLength,
  usernameRegex,
  emailLength,
  emailRegex,
  passwordLength,
  passwordWithRepeat
};

function length(string, maxLen, minLen=0) {
  if(string.length < minLen || string.length > maxLen){
    return false;
  }
  return true;
}

function username(username) {
  if(!usernameLength(username)){
    return false;
  }
  if(!usernameRegex(username)){
    return false;
  }
  return true
}

function email(email) {
  if(!emailLength(email)){
    return false;
  }
  if(!emailRegex(email)){
    return false;
  }
  return true
}

function usernameLength(username) {
  return length(username, validationConst.USERNAME_MAX_LENGTH, validationConst.USERNAME_MIN_LENGTH);
}

function usernameRegex(username) {
  const regUsername = /^[a-zA-Z0-9_-]+$/;
  return regUsername.test(String(username));
}

function emailLength(email) {
  return length(email, validationConst.EMAIL_MAX_LENGTH);
}

function emailRegex(email) {
  let regEmail = /^[^@\s]+@[^@\s]+\.[^@\s]{2,}$/;
  return regEmail.test(String(email));
}

function passwordLength(password) {
  return length(password, validationConst.PASSWORD_MAX_LENGTH, validationConst.PASSWORD_MIN_LENGTH);
}

function passwordWithRepeat(password, repeat) {
    return (password === repeat);
}
