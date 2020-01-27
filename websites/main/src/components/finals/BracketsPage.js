import React, { Component } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { OverlayTrigger, Tooltip } from 'react-bootstrap';
import Loader from 'react-loader-spinner';

import Bracket from './Bracket';

import api from '../../utils/api';

import liaLogo from './logotip_border_white256.png';
import './styleBrackets.css'

const NUM_BATTLES = 16;

class BracketsPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      battles: [],
      isLoading: true,
      error: null
    }
  }

  componentDidMount = () => {
    this.getBattles();
  }

  getBattles = async () => {
    try {
      const respBattles = await api.game.getBattles();
      this.setState({
        battles: respBattles.battles,
        isLoading: false
      });
    } catch(err) {
      this.setState({
        error: "Network Error",
        isLoading: false
      });
      console.log(err.message);
    }
  }

  getBracket = () => {
    const { battles } = this.state;
    let res = [];

    if(battles && battles.length===NUM_BATTLES){

      for(let i=NUM_BATTLES-1; i>=0; i--){
        res.push(
          <Bracket
            key={i+1}
            battleId={battles[i].battleId}
            player1={battles[i].player1}
            player2={battles[i].player2}
            matches={battles[i].matches}
          />
        );
      }
    } else {
      for(let i = 1; i<17; i++){
        res.push(
          <Bracket
            key={i}
            battleId={i}
            player1={null}
            player2={null}
            matches={null}
          />
        );
      }
    }
    return res;
  }

  getWinners = () => {
    let winners = {
      first: {
        username: "",
        organization: "",
        level: ""
      },
      second: {
        username: "",
        organization: "",
        level: ""
      },
      third: {
        username: "",
        organization: "",
        level: ""
      },
      isWinners: true
    };

    const { battles } = this.state;
    const battleFinal = battles.filter((battle) => { return battle.battleId === 16; }).pop();
    const battleForThird = battles.filter((battle) => { return battle.battleId === 15; }).pop();

    let matchesFinal = [];
    let matchesForThird = [];

    if(battleFinal && battleForThird && Array.isArray(battleFinal.matches) && Array.isArray(battleForThird.matches)){
      matchesFinal = battleFinal.matches.filter(
        (match) => { return (match.status === "completed" && match.isPublic) }
      );
      matchesForThird = battleForThird.matches.filter(
        (match) => { return (match.status === "completed" && match.isPublic) }
      );

      let player1WinFinal = 0;
      let player2WinFinal = 0;
      for(let i=0; i<matchesFinal.length; i++){
        if(matchesFinal[i].winnerUserId===battleFinal.player1.userId){
          player1WinFinal++;
        } else if(matchesFinal[i].winnerUserId===battleFinal.player2.userId) {
          player2WinFinal++;
        } else {
          winners.isWinners = false;
          //console.log("Wrong userId. F");  //DEBUG
        }
      }

      if(player1WinFinal > player2WinFinal) {
        winners.first.username = battleFinal.player1.username;
        winners.first.organization = battleFinal.player1.organization;
        winners.first.level = battleFinal.player1.level;

        winners.second.username = battleFinal.player2.username;
        winners.second.organization = battleFinal.player2.organization;
        winners.second.level = battleFinal.player2.level;

      } else if(player1WinFinal < player2WinFinal){
        winners.first.username = battleFinal.player2.username;
        winners.first.organization = battleFinal.player2.organization;
        winners.first.level = battleFinal.player2.level;

        winners.second.username = battleFinal.player1.username;
        winners.second.organization = battleFinal.player1.organization;
        winners.second.level = battleFinal.player1.level;

      } else {
        winners.isWinners = false;
      }

      let player1WinForThird = 0;
      let player2WinForThird = 0;
      for(let i=0; i<matchesForThird.length; i++){
        if(matchesForThird[i].winnerUserId===battleForThird.player1.userId){
          player1WinForThird++;
        } else if(matchesForThird[i].winnerUserId===battleForThird.player2.userId) {
          player2WinForThird++;
        } else {
          winners.isWinners = false;
          //console.log("Wrong userId. 3"); //DEBUG
        }
      }

      if(player1WinForThird > player2WinForThird) {
        winners.third.username = battleForThird.player1.username;
        winners.third.organization = battleForThird.player1.organization;
        winners.third.level = battleForThird.player1.level;

      } else if(player1WinForThird < player2WinForThird){
        winners.third.username = battleForThird.player2.username;
        winners.third.organization = battleForThird.player2.organization;
        winners.third.level = battleForThird.player2.level;

      } else {
        winners.isWinners = false;
      }

    } else {
      winners.isWinners = false;
      //console.log("Wrong battleId"); //DEBUG
    }

    if(winners.isWinners) {
      return winners;
    } else {
      return {
        first: null,
        second: null,
        third: null,
        isWinners: false
      }
    }
  }

  getPlayerToolTip = ( player, id ) => {
    let organization = "No organization"
    let level = "No level"

    if(player){
      if(player.organization){
        organization = player.organization
      }

      if(player.level){
        level = player.level
      }
    }

    return (
      <Tooltip id={"tooltip-bracket-w-" + id} className="custom-tooltip">
        <div>{organization + " (" + level + ")"}</div>
      </Tooltip>
    )
  }

  render() {
    if (this.state.isLoading) {
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
      const winners = this.getWinners();

      return (
        <div>
          <div className="container">
            <div id="bracket-title">
              <img id="logo-lia" src={ liaLogo } alt="Lia" />
              <h2>Slovenian Lia Tournament 2019</h2>
              <h2>Finals</h2>
            </div>
          </div>
          <div id="cont-brackets-page">
            <div className="cont-brackets">
              <div id="brackets-winners">
                <div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#d9c72e"/></div>
                  <div className="cont-win-name">
                    {winners.isWinners
                      ? (
                        <OverlayTrigger placement="bottom" overlay={this.getPlayerToolTip(winners.first, 1)}>
                          <a href={"/user/" + winners.first.username} target="_blank" rel="noopener noreferrer">
                            {winners.first.username}
                          </a>
                        </OverlayTrigger>
                      )
                      : "- - -"
                    }
                  </div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#d9c72e"/></div>
                </div>
                <div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#9b9b92"/></div>
                  <div className="cont-win-name">
                  {winners.isWinners
                    ? (
                      <OverlayTrigger placement="bottom" overlay={this.getPlayerToolTip(winners.second, 2)}>
                        <a href={"/user/" + winners.second.username} target="_blank" rel="noopener noreferrer">
                          {winners.second.username}
                        </a>
                      </OverlayTrigger>
                    )
                    : "- - -"
                  }
                  </div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#9b9b92"/></div>
                </div>
                <div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#9a3f1b"/></div>
                  <div className="cont-win-name">
                  {winners.isWinners
                    ? (
                      <OverlayTrigger placement="bottom" overlay={this.getPlayerToolTip(winners.third, 3)}>
                        <a href={"/user/" + winners.third.username} target="_blank" rel="noopener noreferrer">
                          {winners.third.username}
                        </a>
                      </OverlayTrigger>
                    )
                    : "- - -"
                  }
                  </div>
                  <div className="cont-win-icon"><FontAwesomeIcon icon="trophy" color="#9a3f1b"/></div>
                </div>
              </div>
              {this.getBracket()}
            </div>
          </div>
        </div>
      )
    }

  }
}

export default BracketsPage
