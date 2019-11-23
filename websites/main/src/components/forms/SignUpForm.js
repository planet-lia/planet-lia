import React, {Component} from 'react';
import {Row, Col, FormGroup, FormControl, ControlLabel, Button, Checkbox} from 'react-bootstrap';
import {Typeahead} from 'react-bootstrap-typeahead';
import 'react-bootstrap-typeahead/css/Typeahead.css';

import { connect } from 'react-redux';

import Select from '../elems/Select';
import { validators } from '../../utils/helpers/validators';
import api from '../../utils/api';
import Link from "react-router-dom/es/Link";

class SignUpForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      firstName: "",
      lastName: "",
      email: "",
      username: "",
      password: "",
      repeat: "",
      level: "",
      organization: "",
      country: "",
      allowGlobal: false,
      allowTournament: false,
      allowMarketing: false,
      agreeToTerms: false,

      countriesList: [],
      levelsList: [],
      organizationsList: [],

      error: null,
      errorUn: null,
      message: null,

      isLoading: false,
      isSuccess: false
    }
  }

  componentDidMount = () => {
    this.loadCodes();
    if(this.props.earlyRegistration){
      this.setState({allowTournament: true});
    }
  }

  loadCodes = async () => {
    try {
      const respCountries = await api.codes.getCountries();
      this.setCountriesList(respCountries.countries);
      const respLevels = await api.codes.getLevels();
      this.setLevelsList(respLevels.levels);
      const respOrganizations = await api.other.getOrganizations();
      this.setOrganizationsList(respOrganizations.organizations);
    } catch(err) {
      this.setState({error: "Network Error"});
      console.log(err.message);
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

    this.props.setIsSending(true);
    this.setState({
      isLoading: true,
      error: null,
      errorUn: null,
      message: null
    });

    if(this.validateForm()) {
      try {
        this.setState({message: "Signing you up..."});
        const referral = localStorage.inviteRefUserId;

        await api.user.register(
            this.state.username,
            this.state.email,
            this.state.firstName,
            this.state.lastName,
            this.state.password,
            this.state.level,
            this.state.organization,
            this.state.allowGlobal,
            this.state.allowMarketing,
            this.state.allowTournament,
            this.state.country,
            referral
          );

          this.props.setSuccess();
          this.setState({
            message: "Signup successful! A confirmation link was sent to your email. Please follow the link to finish your registration.",
            isLoading: false,
            isSuccess: true
          })
          localStorage.removeItem("inviteRefUserId");

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
            message: null,
            isLoading: false
          });
          //set up errors for each field
        } else {
          this.setState({
            error: "Network Error: ",
            message: null,
            isLoading: false
          });
        }
      }
    }

    this.props.setIsSending(false);
    this.setState({isLoading: false});
  }

  validateForm = () => {
    const {firstName, lastName, username, email, password, repeat, level, country, agreeToTerms} = this.state;

    if( !(firstName && validators.length(firstName, 30)) ) {this.setState({error: "Invalid Name length"}); return false;}
    if( !(lastName && validators.length(lastName, 50)) ) {this.setState({error: "Invalid Last Name length"}); return false;}
    if( !(validators.usernameLength(username)) ) {this.setState({error: "Invalid username length"}); return false;}
    if( !(email && validators.emailLength(email)) ) {this.setState({error: "Invalid email length"}); return false;}
    if( !validators.passwordLength(password) ) {this.setState({error: "Invalid password length"}); return false;}
    if( !validators.usernameRegex(username) ) {this.setState({error: "Invalid username format"}); return false;}
    if( !validators.emailRegex(email) ) {this.setState({error: "Invalid email format"}); return false;}
    if( !validators.passwordWithRepeat(password, repeat) ){this.setState({error: "Passwords don't match"}); return false;}
    if( !(level) ) {this.setState({error: "Level is not chosen"}); return false;}
    if( !(country) ) {this.setState({error: "Country is not chosen"}); return false;}
    if( !(agreeToTerms) ) {this.setState({error: "You need to agree to Lia Terms and Conditions and Privacy Policy."}); return false;}

    if( !this.isUsernameAvalible() ) {this.setState({error: "Username is not available"}); return false;}

    return true;
  }

  isUsernameAvalible = async () => {
    if( !validators.username(this.state.username) ) return false;

    try {
      const resp = await api.user.usernameAvalible(this.state.username);
      if(resp.available===true){
        this.setState({errorUn: null});
        return true;
      } else if(resp.available===false){
        this.setState({errorUn: "Username already in use"});
        return false;
      }
    } catch(err) {
      this.setState({error: "Network Error"});
      console.log(err.message);
      return false;
    }
  }

  onChange = (event) => {
    this.setState({ [event.target.name]: event.target.value });
  }

  onCheckboxChange = (event) => {
    this.setState({ [event.target.name]: event.target.checked });
  }

  render(){
    return (
      <form onSubmit={this.formSubmit} noValidate>
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
          <Col componentClass={FormGroup} md={12}>
            <ControlLabel>Email</ControlLabel>*
            <FormControl
            type="email"
            name="email"
            placeholder="you@example.com"
            value={this.state.email}
            onChange={this.onChange}
          />
          </Col>
        </Row>
        <Row>
          <Col componentClass={FormGroup} md={6}>
            <div className="form-group">
              <ControlLabel>Username</ControlLabel>*
              <FormControl
                type="text"
                name="username"
                placeholder="Pick a username"
                value={this.state.username}
                onChange={this.onChange}
                onBlur={this.usernameAvalible}
              />
            </div>
            <div className="form-group">
              <ControlLabel>Password</ControlLabel>*
              <FormControl
                type="password"
                name="password"
                placeholder="Create a password"
                value={this.state.password}
                onChange={this.onChange}
              />
            </div>
            <div className="form-group">
              <ControlLabel>Repeat Password</ControlLabel>*
              <FormControl
                type="password"
                name="repeat"
                placeholder="Repeat password"
                value={this.state.repeat}
                onChange={this.onChange}
              />
            </div>
          </Col>
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
                onInputChange={(selected) => {
                  this.setState({organization: selected})
                }}
                onChange={(selected) => {
                  this.setState({organization: selected[0]})
                }}
                options={this.state.organizationsList}
              />
            </div>
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
            <Checkbox
              name="agreeToTerms"
              checked={this.state.agreeToTerms}
              onChange={this.onCheckboxChange}
            >
              I agree to Lia &nbsp;
              <Link to={"/terms-and-conditions"} target={"_blank"}>Terms and Conditions</Link>
              &nbsp; and &nbsp;
              <Link to={"/privacy-policy"} target={"_blank"}>Privacy Policy</Link>.
            </Checkbox>
            <p className="sign-up-footnote">
              [1] - We use Mailchimp as our marketing platform. By choosing checkboxes marked with <sup>[1]</sup>,
              you acknowledge that your information will be transferred
              to Mailchimp for processing. Learn more about Mailchimp's privacy
              practices <a href="https://mailchimp.com/legal/" target="_blank" rel="noopener noreferrer">here</a>.
            </p>
          </Col>
        </Row>
        <Button id={this.props.submitButtonId} type="submit" bsClass="hidden" disabled={this.state.isLoading}></Button>
        <p className="clr-em">{this.state.message}</p>
        <p className="text-danger">{this.state.error}</p>
        <p className="text-danger">{this.state.errorUn}</p>
      </form>
    );
  }

}

function mapStateToProps(state) {
    const { earlyRegistration } = state.popups;
    return {
        earlyRegistration
    };
}

export default connect(mapStateToProps)(SignUpForm);
