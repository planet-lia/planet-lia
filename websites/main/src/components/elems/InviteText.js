import React, { Component } from 'react';
import { FormControl } from 'react-bootstrap';

import api from '../../utils/api';

class InviteText extends Component {
  constructor(props){
    super(props);
    this.state = {
      cLeft: 0,
      cExtra: 0,
      cMax: 0,
      error: null,
    }
  }

  componentDidMount = () => {
    this.loadChallengeStats();
  }

  loadChallengeStats = async () => {
    try {
      const respStats = await api.game.getChallengesStats();
      this.setState({
        cLeft: respStats.challenges.today,
        cExtra: respStats.challenges.extraChallengesPerReferral,
        cMax: respStats.challenges.maxDailyChallenges,
      });
    } catch(err) {
      this.setState({
        error: "Network Error"
      });
      console.log(err.message);
    }
  }

  render() {
    const { username } = this.props;
    const { cExtra, cMax } = this.state;
    return (
      <div>
        <p>Send this link to a friend and challenge him at Lia. Let's see who creates a better bot!</p>
        <FormControl
          type="text"
          name="ref-link"
          value={"https://www.liagame.com/?ref=" + encodeURI(username)}
          readOnly
          onFocus={(event) => event.target.select()}
          bsClass="form-control ref-link center-block"
        />
        <p>{ "You unlock " + cExtra + " additional  daily challenges for each friend that signs up to Lia. You can have at most " + cMax + " daily challenges."}</p>
      </div>
    )
  }

}

export default InviteText;
