import React, { Component } from 'react';
import { Row, Col } from 'react-bootstrap';
import Moment from 'react-moment';
import Replay from '../elems/Replay';
import Link from "react-router-dom/es/Link";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome/index.es";
import queryString from 'query-string';

import StatisticsGraph from '../elems/StatisticsGraph'
import { seconds2time } from '../../utils/helpers/time';
import { parseGameStatistics } from '../../utils/helpers/replayStatistics';

import api from '../../utils/api';

class GameReplay extends Component {
  constructor(props){
		super(props);
    this.state = {
      matchId: "",
      replayUrl: "",
      date: null,
      player1: "",
      player2: "",
      result: "",
      duration: "",
      mapSeed: "",
      unitsRemain: "",
      bubblesAllowP1: true,
      bubblesAllowP2: true,
      gameStatistics: {},
      isInfLoop: false,
      loadingData: false,
      error: null
    };
  }

  componentDidMount = () => {
    const parms = queryString.parse(this.props.location.search)
    if(parms.loop){
      this.setState({isInfLoop: true});
    }
    if(this.props.location.state){
      this.setState(this.props.location.state);
    }
    this.loadGame(this.props.match.params.number)
  }

  loadGame = async (matchId) => {
    this.setState({loadingData: true});
    try {
      const respGame = await api.game.getGame(matchId);
      const matchData = respGame.match;
      this.setState({
        matchId: matchData.matchId,
        replayUrl: matchData.replayUrl,
        date: matchData.completed,
        player1: matchData.bots[0].user.username,
        player2: matchData.bots[1].user.username,
        result: (matchData.bots[0].isWinner ? 1 : 2),
        duration: matchData.duration,
        mapSeed: matchData.mapSeed,
        unitsRemain: Math.floor( (matchData.bots[0].unitsLeft + matchData.bots[1].unitsLeft) / 32 * 100 ) + "%",
        bubblesAllowP1: !matchData.bots[0].speechBubblesDisabled,
        bubblesAllowP2: !matchData.bots[1].speechBubblesDisabled,
        loadingData: false
      });
    } catch(err) {
      this.setState({
        loadingData: false,
        error: "Network Error"
      });
      console.log(err.message);
    }
  }

  render(){
    const { player1, player2, date, duration, mapSeed, replayUrl, result, bubblesAllowP1, bubblesAllowP2, gameStatistics, isInfLoop } = this.state;

    return (
      <div>
        <div className="cont-game-title text-center">
          <div className="game-title">
            {usernameFormatter(player1, result === 1)} vs {usernameFormatter(player2, result === 2)}
          </div>
          <div className="game-stats"><Moment format="DD/MM/YYYY HH:mm">{date}</Moment></div>
          <div className="game-stats">
            {"Duration: " + seconds2time(duration)}
          </div>
          <div className="game-stats">
            {"Map seed: " + (mapSeed===-1 ? "unknown" : mapSeed)}
          </div>
        </div>

        <div>
          <Row>
            <Col sm={8}>
              <div key={this.state.matchId}>
                <Replay
                  containerId="gameView"
                  replayFileBase64=""
                  number={0}
                  replayUrl={replayUrl}
                  setGameStatistics={(gameStatistics) => this.setState({gameStatistics: parseGameStatistics(gameStatistics, player1, player2)})}
                  bubblesAllow={[bubblesAllowP1, bubblesAllowP2]}
                  loop={isInfLoop}
                />
              </div>
            </Col>
            <Col sm={4}>
              <StatisticsGraph title="Power" data={gameStatistics.powerData} />
              <StatisticsGraph title="Resources" data={gameStatistics.resourceData} />
            </Col>
          </Row>
          <Row>
            <Col sm={4}>
              <StatisticsGraph title="Workers" data={gameStatistics.workersData} />
            </Col>
            <Col sm={4}>
              <StatisticsGraph title="Warriors" data={gameStatistics.warriorsData} />
            </Col>
            <Col sm={4}>
              <StatisticsGraph title="Units" data={gameStatistics.unitsData} />
            </Col>
          </Row>
        </div>

      </div>
    )
  }
}

function usernameFormatter(username, isWinner) {
  let trophy = "";
  if (isWinner) {
    trophy = (<span>&nbsp;<FontAwesomeIcon icon="trophy" color={"#CCCCCC"}/>&nbsp;</span>);
  }
  return (<span><Link to={"/user/" + username} style={{ textDecoration: 'none' }}>{username}</Link>{trophy}</span>);
}

export default GameReplay;
