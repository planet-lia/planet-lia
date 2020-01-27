import React, { Component } from 'react';
import { OverlayTrigger, Tooltip } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';

class Bracket extends Component {
  constructor(props) {
    super(props);
    this.state = {
      col: null,
      row: null,
      side: null,
      vert: null,
      type: null
    }
  }

  componentDidMount = () => {
    switch(this.props.battleId) {
      case 1:
        this.setState({ col:"1", row:"1", side:"left", type:"leaf", vert:"top" });
        break;
      case 2:
        this.setState({ col:"1", row:"2", side:"left", type:"leaf", vert:"bottom" });
        break;
      case 3:
        this.setState({ col:"1", row:"3", side:"left", type:"leaf", vert:"top" });
        break;
      case 4:
        this.setState({ col:"1", row:"4", side:"left", type:"leaf", vert:"bottom" });
        break;
      case 9:
        this.setState({ col:"2", row:"12", side:"left", type:"normal", vert:"top" });
        break;
      case 10:
        this.setState({ col:"2", row:"34", side:"left", type:"normal", vert:"bottom" });
        break;
      case 13:
        this.setState({ col:"3", row:"23", side:"left", type:"normal", vert:"bottom-mid" });
        break;
      case 16:
        this.setState({ col:"4", row:"23", side:"center", type:"root" });
        break;
      case 15:
        this.setState({ col:"4", row:"4", side:"center", type:"outside" });
        break;
      case 14:
        this.setState({ col:"5", row:"23", side:"right", type:"normal", vert:"top-mid" });
        break;
      case 11:
        this.setState({ col:"6", row:"12", side:"right", type:"normal", vert:"top" });
        break;
      case 12:
        this.setState({ col:"6", row:"34", side:"right", type:"normal", vert:"bottom" });
        break;
      case 5:
        this.setState({ col:"7", row:"1", side:"right", type:"leaf", vert:"top" });
        break;
      case 6:
        this.setState({ col:"7", row:"2", side:"right", type:"leaf", vert:"bottom" });
        break;
      case 7:
        this.setState({ col:"7", row:"3", side:"right", type:"leaf", vert:"top" });
        break;
      case 8:
        this.setState({ col:"7", row:"4", side:"right", type:"leaf", vert:"bottom" });
        break;
      default:
        //do nothing
    }

  }

  setEdges = () => {
    const { side, vert, type } = this.state;
    const { battleId } = this.props;

    let edgeIn = null;
    let edgeOut = null;
    let edgeLeft = null;
    let edgeRight = null;

    if(type==="normal"){
      edgeIn = (
        <div className="edge-in">
          <div className={"top " + side}></div>
          <div className={"battle-id " + side}>{battleId}</div>
          <div className={"bottom " + side}></div>
        </div>
      );
      edgeOut = (
        <div className="edge-out">
          <div className={"connect " + side}></div>
          <div className={"edge " + vert + " " + side}></div>
        </div>
      );
    } else if(type==="leaf"){
      edgeIn = (
        <div className="edge-in">
          <div className={"battle-id " + side}>{battleId}</div>
        </div>
      )
      edgeOut = (
        <div className="edge-out">
          <div className={"connect " + side}></div>
          <div className={"edge " + vert + " " + side}></div>
        </div>
      );
    } else if(type==="root"){
      edgeLeft = (
        <div className="root-edge">
          <div className="edge left"></div>
          <div className={"battle-id " + side}>{battleId}</div>
        </div>
      )

      edgeRight = (
        <div className="root-edge">
          <div className="edge right"></div>
        </div>
      )
    } else if(type==="outside") {
      edgeLeft = <div className="root-edge"><div className={"battle-id " + side}>{battleId}</div></div>

      edgeRight = <div className="root-edge"></div>
    }

    if(side==="left") {
      edgeLeft = edgeIn;
      edgeRight = edgeOut;
    } else if (side==="right") {
      edgeLeft = edgeOut;
      edgeRight = edgeIn;
    }

    return {edgeLeft, edgeRight};
  }

  getMatches = () => {
    const{ battleId, player1, player2 } = this.props;
    let indicators = [];
    let player1Wins = 0;
    let player2Wins = 0;
    let battleMatches = null;

    if(Array.isArray(this.props.matches)) {
      //console.log(battle.matches); //DEBUG
      battleMatches = this.props.matches.filter(
        (match) => { return (match.status === "completed") }
      );
    }

    if(battleMatches) {

      for(let i=0; i<battleMatches.length; i++) {
        if(battleMatches[i].isPublic) {
          if(battleMatches[i].winnerUserId === player1.userId){
            indicators.push("yellow");
            player1Wins++;
          } else if(battleMatches[i].winnerUserId === player2.userId) {
            indicators.push("green");
            player2Wins++;
          } else {
            console.log("Error: WinnerUserId does not match with any player. BattleId: " + battleId);
            return ({
              indicators: null,
              player1Wins: 0,
              player2Wins: 0,
              isError: true
            })
          }

        } else {
          indicators.push("");
        }
      }

      for(let i=0; i<5-battleMatches.length; i++){
        indicators.push("");
      }

      return ({
        indicators,
        player1Wins,
        player2Wins,
        isGenerated: true,
        isError: false
      });

    } else {
      //console.log("Not ready: There are not 5 matches in this battle. BattleId: " + battleId);
      return ({
        indicators: null,
        player1Wins: 0,
        player2Wins: 0,
        isGenerated: false
      });
    }
  }

  getIndicators = (indicators) => {
    let res = [];

    if(indicators && indicators.length===5){
      for(let i = 0; i<5; i++){
        res.push(
          <div key={i} className={"btn-game " + indicators[i]}></div>
        );
      }
    } else {
      for(let i = 0; i<5; i++){
        res.push(
          <div key={i} className="btn-game"></div>
        );
      }
    }


    return res;
  }

  getPlayerToolTip = ( player, id ) => {
    const { battleId } = this.props;
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
      <Tooltip id={"tooltip-bracket-" + id + "-" + battleId} className="custom-tooltip">
        <div>{organization + " (" + level + ")"}</div>
      </Tooltip>
    )
  }

  render() {
    const { col, row, side } = this.state;
    const { battleId, player1, player2 } = this.props;
    const { edgeLeft, edgeRight } = this.setEdges();

    const matchesResults = this.getMatches();

    return (
      <div className={"g-col-" + col + " g-row-" + row}>
        <div className={"bracket " + side}>
          {edgeLeft}
          <div className="vertex">
            <div className="player-field player1">
              <div>
                {player1
                  ? (
                    <OverlayTrigger placement="bottom" overlay={this.getPlayerToolTip(player1, 1)}>
                      <a href={"/user/" + player1.username} target="_blank" rel="noopener noreferrer">
                        {player1.username}
                      </a>
                    </OverlayTrigger>
                  )
                  : (
                    "- - -"
                  )
                }
              </div>
              <div>
                {matchesResults.player1Wins}
              </div>
            </div>
            <div className="battle-indicators">
              {this.getIndicators(matchesResults.indicators)}
            </div>
            <div className="player-field player2">
              <div>
                {player2
                  ? (
                    <OverlayTrigger placement="bottom" overlay={this.getPlayerToolTip(player2, 2)}>
                      <a href={"/user/" + player2.username} target="_blank" rel="noopener noreferrer">
                        {player2.username}
                      </a>
                    </OverlayTrigger>
                  )
                  : (
                    "- - -"
                  )
                }
              </div>
              <div>
                {matchesResults.player2Wins}
              </div>
            </div>
            <div className="bracket-btn-watch">
              <Link
                className="btn custom-btn btn-red"
                to={"/events/slt2019/battle/" + battleId}
              >
                <span><FontAwesomeIcon icon="tv" /></span>
                <span> Watch</span>
              </Link>
            </div>
          </div>
          {edgeRight}
        </div>
      </div>
    )
  }
}

export default Bracket
