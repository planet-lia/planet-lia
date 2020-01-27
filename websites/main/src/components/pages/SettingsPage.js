import React, {Component} from 'react';
import {Row, Col, FormGroup, FormControl, ControlLabel, Button, Checkbox} from 'react-bootstrap';
import { Redirect } from 'react-router-dom';
import {Typeahead} from 'react-bootstrap-typeahead';
import 'react-bootstrap-typeahead/css/Typeahead.css';
import Loader from 'react-loader-spinner';

import Select from '../elems/Select';
import { validators } from '../../utils/helpers/validators';
import api from '../../utils/api';
import {connect} from "react-redux";

class SettingsPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      firstName: "",
      lastName: "",
      level: "",
      organization: "",
      country: "",
      allowGlobal: false,
      allowTournament: false,
      allowMarketing: false,

      countriesList: [],
      levelsList: [],
      organizationsList: [],

      error: null,
      errorUn: null,
      message: null,

      isDataLoaded: false,
      isLoading: false,
      isSuccess: false
    }
  }

  componentDidMount = () => {
    this.loadData();
  }

  loadData = async () => {
    try {
      const respCountries = await api.codes.getCountries();
      this.setCountriesList(respCountries.countries);
      const respLevels = await api.codes.getLevels();
      this.setLevelsList(respLevels.levels);
      const respOrganizations = await api.other.getOrganizations();
      this.setOrganizationsList(respOrganizations.organizations);
      const userInfo = await api.user.getUserInfo();
      this.setUser(userInfo);

      this.setState({isDataLoaded: true});
    } catch(err) {
      this.setState({error: "Network Error"});
      console.log(err.message);
    }
  }

  setUser = (userInfo) => {
    try {
      // Needed so that it can be displayed in form
      this.addOrganizationToList(userInfo.organization);

      this.setState({
        firstName: userInfo.firstName,
        lastName: userInfo.lastName,
        level: userInfo.levelCode,
        organization: userInfo.organization,
        country: userInfo.countryAlpha2Code,
        allowGlobal: userInfo.allowPublicationToGlobalLeaderboard,
        allowTournament: userInfo.allowTournament2019Emails,
        allowMarketing: userInfo.allowMarketingEmails,
      });

    } catch(err) {
      this.setState({error: "Network Error"});
      console.log(err.message);
    }
  }

  addOrganizationToList = (organization) => {
    if (!this.state.organizationsList.includes(organization)) {
      this.state.organizationsList.push(organization);
    }
  }

  setCountriesList = (respCountries) => {
    const countries = respCountries.map(
      (country) => (
        {value: country.alpha2Code, label: country.name}
      )
    );
    this.setState({countriesList: countries});
  }

  setLevelsList = (respLevels) => {
    const levels = respLevels.map(
      (level) => (
        {value: level[0], label: level[1]}
      )
    );
    this.setState({levelsList: levels});
  }

  setOrganizationsList = (respOrganizations) => {
    this.setState({organizationsList: respOrganizations});
  }

  formSubmit = async (event) => {
    event.preventDefault();
    this.setState({isLoading: true, error: null});

    if(this.validateForm()) {
      try {
        this.setState({message: "Updating settings..."});
        await api.user.edit(
          this.state.firstName,
          this.state.lastName,
          this.state.organization,
          this.state.country,
          this.state.level,
          this.state.allowGlobal,
          this.state.allowMarketing,
          this.state.allowTournament
        );

        const userInfo = await api.user.getUserInfo();
        this.setUser(userInfo);

      } catch(err) {
        if(err.response){
          let field = "";
          let msg = JSON.stringify(err.response.data);
          try {
            field = err.response.data.errors[0].field;
            msg = err.response.data.errors[0].msg;
          } catch (exception) {}

          this.setState({
            error: field + ": " + msg,
            isLoading: false
          });
          //set up errors for each field
        } else {
          this.setState({
            error: "Network Error: ",
            isLoading: false
          });
        }
      }
    }
    this.setState({isLoading: false, message: null});
  }

  validateForm = () => {
    const {firstName, lastName, level, country} = this.state;

    if( !(firstName && validators.length(firstName, 30)) ) {this.setState({error: "Invalid Name length"}); return false;}
    if( !(lastName && validators.length(lastName, 50)) ) {this.setState({error: "Invalid Last Name length"}); return false;}
    if( !(level) ) {this.setState({error: "Level not set"}); return false;}
    if( !(country) ) {this.setState({error: "Country not set"}); return false;}

    return true;
  }

  onChange = (event) => {
    this.setState({ [event.target.name]: event.target.value });
  }

  onCheckboxChange = (event) => {
    this.setState({ [event.target.name]: event.target.checked });
  }

  render(){
    if (!this.props.isAuthenticated) {return <Redirect to="/" />}

    if (!this.state.isDataLoaded) {
      return (
        <Row>
          <Col sm={3}/>
          <Col sm={6}>
            <Row><h2>Settings</h2></Row>
            <div className="cont-table-loader">
              <div className="cont-loader">
                <Loader
                  type="Triangle"
                  color="#019170"
                  height="100"
                  width="100"
                />
              </div>
            </div>
          </Col>
        </Row>
    )}

    return (
      <form onSubmit={this.formSubmit} noValidate>
            <Row>
              <Col sm={3}/>
              <Col sm={6}>
                <Row><h2>Settings</h2></Row>
                {(true) ? null : null}
                <Row>
                  <Col componentClass={FormGroup} md={6}>
                    <ControlLabel>Name</ControlLabel>*
                    <FormControl
                      type="text"
                      name="firstName"
                      placeholder="Name"
                      value={this.state.firstName}
                      onChange={this.onChange}
                    />
                  </Col>
                  <Col componentClass={FormGroup} md={6}>
                    <ControlLabel>Last Name</ControlLabel>*
                    <FormControl
                      type="text"
                      name="lastName"
                      placeholder="Last name"
                      value={this.state.lastName}
                      onChange={this.onChange}
                    />
                  </Col>
                </Row>
                <Row>
                  <Col componentClass={FormGroup} md={6}>
                    <div className="form-group">
                      <ControlLabel>Level</ControlLabel>*
                      <Select
                        options={this.state.levelsList}
                        placeholder="Your level"
                        name="level"
                        value={this.state.level}
                        onChange={this.onChange}
                      />
                    </div>
                    <div className="form-group">
                      <ControlLabel>Organization</ControlLabel>
                      <Typeahead
                        type="text"
                        name="organization"
                        placeholder="Organization"
                        selected={[this.state.organization]}
                        onInputChange={(selected) => {
                          this.setState({organization: selected});
                        }}
                        onChange={(selected) => {
                          if (selected.length > 0) {
                            this.setState({organization: selected[0]})
                          }
                        }}
                        options={this.state.organizationsList}
                      />
                    </div>
                  </Col>
                  <Col componentClass={FormGroup} md={6}>
                    <div className="form-group">
                      <ControlLabel>Country</ControlLabel>*
                      <Select
                        options={this.state.countriesList}
                        placeholder="Your country"
                        name="country"
                        value={this.state.country}
                        onChange={this.onChange}
                      />
                    </div>
                  </Col>
                </Row>
                <Row>
                  <Col componentClass={FormGroup} md={12}>
                    <Checkbox
                      name="allowGlobal"
                      checked={this.state.allowGlobal}
                      onChange={this.onCheckboxChange}
                    >
                      I want you to add my account to the global leaderboard after the Slovenian Lia tournament 2019
                    </Checkbox>
                    <Checkbox
                      name="allowTournament"
                      checked={this.state.allowTournament}
                      onChange={this.onCheckboxChange}
                    >
                      I want to receive emails about Slovenian Lia Tournament 2019 <sup>[1]</sup>
                    </Checkbox>
                    <Checkbox
                      name="allowMarketing"
                      checked={this.state.allowMarketing}
                      onChange={this.onCheckboxChange}
                    >
                      I want to receive general Lia emails (Newsletter, etc.) <sup>[1]</sup>
                    </Checkbox>
                  </Col>
                </Row>
                <Row>
                  <Col componentClass={FormGroup} md={12}>
                    <p className="sign-up-footnote">
                      [1] - We use Mailchimp as our marketing platform. By choosing checkboxes marked with <sup>[1]</sup>,
                      you acknowledge that your information will be transferred
                      to Mailchimp for processing. Learn more about Mailchimp's privacy
                      practices <a href="https://mailchimp.com/legal/" target="_blank" rel="noopener noreferrer">here</a>.
                    </p>
                  </Col>
                </Row>
                <Button type="submit" className="btn custom-btn custom-btn-lg pull-right" disabled={this.state.isLoading}>Save</Button>
                <span className="text-info">{this.state.message}</span>
                <span className="text-danger">{this.state.error}</span>
                <span className="text-danger">{this.state.errorUn}</span>
              </Col>
            </Row>
          </form>
    );
  }
}


function mapStateToProps(state) {
  const { isAuthenticated } = state.authentication;
  return {
    isAuthenticated
  };
}

export default connect(mapStateToProps)(SettingsPage);
