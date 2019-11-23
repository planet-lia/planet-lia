import React from 'react';
import { Route, Redirect } from 'react-router-dom';

import { connect } from 'react-redux';

const PrivateRoute = ({ isAuthenticated, component: Component, ...rest }) => (
  <Route {...rest} render={props => (
    isAuthenticated
      ? <Component {...props} />
      : <Redirect to="/" />
  )} />
)

function mapStateToProps(state) {
    const { isAuthenticated } = state.authentication;
    return { isAuthenticated };
}

export default connect(mapStateToProps)(PrivateRoute);
