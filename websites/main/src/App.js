import React, { Component } from 'react';
import { Route, withRouter } from 'react-router-dom';
import Loader from 'react-loader-spinner';
import axios from 'axios';

import Header from './components/layout/Header';
import Footer from './components/layout/Footer';
import Routes from './components/layout/Routes';
import withTracker from './components/tracking/withTracker';
import GlobalPopups from './components/layout/GlobalPopups';

import {connect} from "react-redux";
import { authActions } from './utils/actions/authActions';

import { library } from '@fortawesome/fontawesome-svg-core';
import {
  faFacebookSquare, faGithub, faYoutube, faReddit, faDiscord
} from '@fortawesome/free-brands-svg-icons';
import {
  faEnvelope, faTrophy, faDesktop, faPlay, faUser, faTv, faMedal, faRobot,
  faChessRook, faBullhorn, faUpload, faQuestionCircle, faTicketAlt
} from '@fortawesome/free-solid-svg-icons';

library.add(
  faFacebookSquare, faGithub, faYoutube, faEnvelope, faTrophy,
  faTv, faRobot, faMedal, faDesktop, faPlay, faUser, faChessRook, faReddit,
  faBullhorn, faUpload, faDiscord, faQuestionCircle, faTicketAlt
);


class App extends Component {
  interval = null;

  constructor(props){
		super(props);
    this.state = {};
  }

  componentDidMount = () => {
    this.checkForToken();
  }

  componentWillUnmount = () => {
    clearTimeout(this.interval);
  }

  checkForToken = async () => {
    let axiosToken = axios.defaults.headers.common['Authorization'];

    if(axiosToken) {
      axiosToken = axiosToken.substring(7);
      if(localStorage.token !== axiosToken){
        await this.props.dispatch(authActions.authenticate(localStorage.token));
      }
    }

    if ( !localStorage.token && !this.props.isLoggedOut) {
      await this.props.dispatch(authActions.logout());
    }

    this.interval = setTimeout(this.checkForToken, 5000);
  }

  render() {
    const isEditor = (window.location.pathname.split("/")[1]==="editor");
    const darkPages = ["/editor", "/editor/", "/events/slt2019", "/events/slt2019/"];
    for (let i = 1; i < 17; i++) {
      darkPages.push("/events/slt2019/battle/" + i)
      darkPages.push("/events/slt2019/battle/" + i + "/")
    }
    const isDark = darkPages.includes(window.location.pathname);


    if (this.props.isCheckingAuth) {
      return (
        <div className="cont-loader">
          <Loader
            type="Triangle"
            color="#019170"
            height="100"
            width="100"
          />
        </div>
      )
    } else {
      return (
        <div id="main-container" className={isDark ? "dark" : null}>
          <Header foo={() => false}/>
          <div className={isEditor ? "main-content no-footer" : "main-content"}>
            <Route component={withTracker(Routes, { /* additional attributes */ })}/>
          </div>
          <GlobalPopups />
          { !isEditor ? (<Footer />) : null}
        </div>
      )
    }

  }
}

function mapStateToProps(state) {
  const { isAuthenticated, isLoggedOut, isCheckingAuth } = state.authentication;
  return {
    isAuthenticated,
    isLoggedOut,
    isCheckingAuth
  };
}

export default withRouter(connect(mapStateToProps)(App));
