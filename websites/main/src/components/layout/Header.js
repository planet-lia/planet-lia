import React, { Component } from 'react';
import { Navbar, Nav, NavItem, NavDropdown } from 'react-bootstrap';
import { Link, NavLink } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { authActions } from '../../utils/actions/authActions';
import { popupsActions } from '../../utils/actions/popupsActions';

import { connect } from 'react-redux';

class Header extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isExpanded: false
    }
  }

  onNavbarToggle = () => {
    if(this.state.isExpanded===true){
      this.setState({ isExpanded: false });
    } else {
      this.setState({ isExpanded: true });
    }
  }

  onSelectNavItem = () => {
    this.setState({ isExpanded: false });
  }

  showSignPopup = async (mode) => {
    this.onSelectNavItem();
    if(mode===0){
      await this.props.dispatch(popupsActions.showSignIn());
    } else if(mode===1) {
      await this.props.dispatch(popupsActions.showRegistration());
    }
  }

  logout = async () => {
    this.onSelectNavItem();
    await this.props.dispatch(authActions.logout());
  }

  render(){
    return (
      <div id="main-header">
        <div className="container">
          <Navbar id="custom-navbar" fluid expanded={this.state.isExpanded} onToggle={this.onNavbarToggle}>
            <Navbar.Header>
              <Navbar.Brand>
                <Link to="/" onClick={this.onSelectNavItem}><img id="logo" src="/logo_close256.png" alt="logo"/></Link>
              </Navbar.Brand>
              <Navbar.Toggle />
            </Navbar.Header>
            <Navbar.Collapse>
              <ul className="nav navbar-nav">
                <li role="presentation" onClick={this.onSelectNavItem}>
                  <NavLink to="/tournament/overview" exact activeClassName="nav-link-active">Tournament</NavLink>
                </li>
                <li role="presentation" onClick={this.onSelectNavItem}>
                  <NavLink to="/jobs" exact activeClassName="nav-link-active">Jobs</NavLink>
                </li>
              </ul>
              {this.props.isAuthenticated ? (
                <ul className="nav navbar-nav navbar-right">
                  <li role="presentation" onClick={this.onSelectNavItem}>
                    <NavLink to={"/user/" + this.props.username} exact activeClassName="nav-link-active">
                        <div><FontAwesomeIcon icon="user" /> {this.props.username}</div>
                    </NavLink>
                  </li>
                  <li>
                    <NavLink to={"/settings"} exact activeClassName="nav-link-active">
                      <div>Settings</div>
                    </NavLink>
                  </li>
                  <li role="presentation" onClick={this.logout}>
                    <a role="button" onClick={() => {return false}}>Sign Out</a>
                  </li>
                </ul>
              ) : (
                <Nav pullRight>
                  <NavItem onClick={() => this.showSignPopup(1)}>Sign Up</NavItem>
                  <NavItem onClick={() => this.showSignPopup(0)}>Sign In</NavItem>
                </Nav>
              )}
            </Navbar.Collapse>
          </Navbar>
        </div>
      </div>
    );
  }

}

function mapStateToProps(state) {
  const { isAuthenticated, username } = state.authentication;
  return {
      isAuthenticated,
      username
  };
}

export default connect(mapStateToProps)(Header);
