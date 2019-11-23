import { combineReducers } from 'redux';

import { authentication } from './authReducer';
import { popups } from './popupsReducer';

export default combineReducers({
  authentication,
  popups
})
