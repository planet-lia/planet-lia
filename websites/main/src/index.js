import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import ScrollToTop from './components/layout/ScrollToTop';

import './index.css';
import './styles.scss';
import App from './App';
import { store } from './utils/helpers/store';
import { authActions } from './utils/actions/authActions'

if(localStorage.token){
  store.dispatch(authActions.authenticate(localStorage.token));
}

window.MonacoEnvironment = {
  getWorkerUrl: () => "/monaco-editor-worker-loader-proxy.js",
}

ReactDOM.render((
  <Provider store={store}>
    <BrowserRouter>
      <ScrollToTop>
        <App />
      </ScrollToTop>
    </BrowserRouter>
  </Provider>
), document.getElementById('root'));
