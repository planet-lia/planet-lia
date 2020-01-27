import React, { Component } from 'react';
import { Row, Col, OverlayTrigger, Tooltip } from 'react-bootstrap';
import ReactPaginate from 'react-paginate';
import GamesTable from '../elems/GamesTable';
import Moment from 'react-moment';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import ProfileDisplay from '../elems/ProfileDisplay';
import ChallengeButton from '../elems/ChallengeButton';
import InviteButton from '../elems/InviteButton';
import Achievements from '../elems/Achievements';

import api from '../../utils/api';

import {connect} from "react-redux";

class ProfilePage extends Component {
  constructor(props){
		super(props);
    this.state = {
      userId: "",
      username: "",
      isPrivate: false,
      rank: 0,
      rating: 0,
      mu: 0,
      sigma: 0,
      wins: 0,
      losses: 0,
      total: 0,
      playing: 0,
      achievements: [],
      activeBotId: "",
      activeBotWins: 0,
      activeBotLosses: 0,
      activeBotTotal: 0,
      activeBotPlaying: 0,
      gamesData: [],
      pageCount: 0,
      nGamesPerPage: 0,
      version: "",
      language: "",
      uploadTime: "",
      latestBotId: "",
      newBotStatus: "",
      newBotUploadTime: "",
      newBotProcessingLogs: "",
      newBotTestMatchLogs: "",
      newBotTestMatchGameEngineLog: "",
      cLeftToday: 0,
      cTotal: 0,
      hasActiveBot: false,
      isChallenges: false,
      currentPage: 0,
      loadingData: false,
      error: null
    }
  }

  componentDidMount = () => {
    this.loadData(this.props.match.params.username);
  }

  loadData = async (username) => {
    const isPrivate = username === this.props.username;
    this.setState({loadingData: true, username: username, isPrivate: isPrivate});

    try {
      const respUserId = await api.user.getUsernameToUserId(username);
      const userId = respUserId.userId;
      this.setState({userId: userId});
      const respUser = await api.game.getUserPublic(userId);

      let hasActiveBot = respUser.user.activeBot !== null;
      this.setState({hasActiveBot: hasActiveBot});
      this.setUserData(respUser.user);
      if (hasActiveBot) {
        const activeBotStats = respUser.user.stats.match.activeBot;
        this.setActiveBotData(respUser.user.activeBot, activeBotStats);
      }

      if (isPrivate) {
        try{
          const respBotLatest = await api.game.getLatestBot();
          this.setLatestBotData(respBotLatest.bot);
        }
        catch(err) {
          // Do nothing, if 404 latest bot does not exist
        }
        const respChallengeStats = await api.game.getChallengesStats();
        this.setState({
          cLeftToday: respChallengeStats.challenges.today,
          cTotal: respChallengeStats.challenges.total
        })
      }

      this.loadGames(userId, 0);
    }
    catch(err) {
      this.setState({
        loadingData: false,
        error: "Network Error"
      });
      console.log(err.message);
    }
  }

  setLatestBotData = (respBotLatest) => {
    this.setState({
      latestBotId: respBotLatest.botId,
      newBotUploadTime: respBotLatest.uploaded,
      newBotStatus: respBotLatest.status,
      newBotProcessingLogs: respBotLatest.logs.processingLog,
      newBotTestMatchLogs: respBotLatest.logs.testMatchLog,
      newBotTestMatchGameEngineLog: respBotLatest.logs.testMatchGameEngineLog,
    });
  }

  loadGames = async (userId, page, isChallenges = false) => {
    let offset = page * this.state.nGamesPerPage
    this.setState({
      loadingData: true,
      gamesData: [],
      isChallenges: isChallenges,
      currentPage: page
    });
    let respGames;
    try {
      if(isChallenges){
        respGames = await api.game.getUserChallenges(userId, offset);
      } else {
        respGames = await api.game.getUserGames(userId, offset);
      }
      this.setGamesData(respGames)
    } catch(err) {
      this.setState({
        loadingData: false,
        error: "Network Error"
      });
      console.log(err.message);
    }
  }

  setGamesData = (respGames) => {
    const gamesList = respGames.matches.map(
      (gamesList) => ({
        matchId: gamesList.matchId,
        replayUrl: gamesList.replayUrl,
        date: gamesList.completed,
        player1: gamesList.bots[0].user.username,
        player2: gamesList.bots[1].user.username,
        player1Rank: gamesList.bots[0].user.rank,
        player2Rank: gamesList.bots[1].user.rank,
        result: gamesList.status==="completed" ? (gamesList.bots[0].isWinner ? 1 : 2) : 0,
        duration: gamesList.duration,
        isWinner: gamesList.isWinner,
        unitsRemain1: Math.max(gamesList.bots[0].unitsLeft, 0),
        unitsRemain2: Math.max(gamesList.bots[1].unitsLeft, 0),
        isCompleted: gamesList.status==="completed"
      })
    );

    let count = respGames.pagination.count;
    let total = respGames.pagination.total;
    let nextOffset = respGames.pagination.nextOffset;
    let nGamesPerPage = this.state.nGamesPerPage;
    let pageCount = this.state.pageCount;

    if (total === 0) {
      //no games
      nGamesPerPage = 0;
      pageCount = 0;
    } else if (count === total) {
      //less than full page
      nGamesPerPage = count;
      pageCount = 1;
    } else if (nextOffset) {
      //full page
      nGamesPerPage = count;
      pageCount = Math.ceil(total / count);
    } /*else {
      //lastpage
    }*/

    this.setState({
      gamesData: gamesList,
      nGamesPerPage: nGamesPerPage,
      pageCount: pageCount,
      loadingData: false
    });
  }

  setUserData = (respUser) => {
    this.setState({
      rank: respUser.stats.leaderboard.rank,
      rating: respUser.stats.leaderboard.rating,
      mu: respUser.stats.leaderboard.mu,
      sigma: respUser.stats.leaderboard.sigma,
      wins: respUser.stats.match.allTime.wins,
      losses: respUser.stats.match.allTime.losses,
      total: respUser.stats.match.allTime.total,
      playing: respUser.stats.match.allTime.playing,
      achievements: respUser.achievements ? respUser.achievements : []
    });
  }

  setActiveBotData = (activeBot, activeBotStats) => {
    this.setState({
      activeBotId: activeBot.botId,
      version: activeBot.version,
      language: activeBot.language,
      uploadTime: activeBot.uploaded,
      activeBotWins: activeBotStats.wins,
      activeBotLosses: activeBotStats.losses,
      activeBotTotal: activeBotStats.total,
      activeBotPlaying: activeBotStats.playing,
    });
  }

  logToDownloadLink = (logLink) => {
    if (logLink === "") return "";
    return (<a href={logLink} target="_blank">download</a>);
  }

  handlePageClick = (data) => {
    this.loadGames(this.state.userId, data.selected, this.state.isChallenges);
  };

  render(){
    const { gamesData, loadingData, userId, username, rank, rating, mu, sigma,
      wins, losses, playing, achievements, pageCount, version, language,
      uploadTime, activeBotId, latestBotId, activeBotWins, activeBotLosses,
      activeBotPlaying, newBotUploadTime, newBotStatus,
      newBotProcessingLogs, newBotTestMatchLogs, newBotTestMatchGameEngineLog,
      cLeftToday, cTotal, isChallenges, currentPage, hasActiveBot, isPrivate
    } = this.state;

    const tooltip = (
      <Tooltip id="tooltip-rank" className="custom-tooltip">
        <div>Rating = Mu - 3 * Sigma</div>
        <div className="margin-top10">Mu - Estimate of your rank</div>
        <div>Sigma - Certainty of your rank</div>
        <div className="margin-top10">The more games you play the closer your Rating is to Mu</div>
      </Tooltip>
    );

    return (
      <div className="container">
        <div id="prof-cont-data">
          <Row>
            <Col xs={12}>
              <h2 className="margin-bottom20">{username}</h2>
              {(this.state.isPrivate) ? (<div className="prof-your">Your profile</div>) : null}
            </Col>
          </Row>
          <Row>
            <Col md={2} sm={3}>
              <div className="margin-bottom30">
                <div id="prof-icon" className="icon-lg">
                  <FontAwesomeIcon icon="robot" color={"#019170"}/>
                </div>
                {this.state.isPrivate
                  ? (
                    <div>
                      <div>{"Challenges left today: " + cLeftToday + "/" + cTotal}</div>
                      <InviteButton className="btn-invite-prof"/>
                    </div>
                  )
                  : <ChallengeButton opponent={username} opponentId={userId} className="custom-btn-lg margin-top10"/>
                }
              </div>
            </Col>
            <Col md={10} sm={9}>
              <Row>
                <Col sm={4}>
                  <ProfileDisplay
                    heading={
                      <span>
                        Rank details
                        <OverlayTrigger placement="bottom" overlay={tooltip}>
                          <FontAwesomeIcon icon="question-circle" size="sm" color="#019170" id="tooltip-rank-icon"/>
                        </OverlayTrigger>
                      </span>
                    }
                    data={[
                      {
                        label: "Rank:",
                        value: rank
                      },
                      {
                        label: "Rating:",
                        value: rating
                      },
                      {
                        label: "Mu:",
                        value: mu
                      },
                      {
                        label: "Sigma:",
                        value: sigma
                      },
                    ]}
                  />
                </Col>
                <Col sm={4}>
                  <ProfileDisplay
                    heading="Current Bot Results"
                    data={[
                      {
                        label: "Wins:",
                        value: activeBotWins
                      },
                      {
                        label: "Losses:",
                        value: activeBotLosses
                      },
                      {
                        label: "Win ratio:",
                        value: winPercentage(activeBotWins, activeBotLosses)
                      },
                      {
                        label: "Playing:",
                        value: activeBotPlaying
                      },
                    ]}
                  />
                </Col>
                <Col sm={4}>
                  <ProfileDisplay
                    heading="All Time Results"
                    data={[
                      {
                        label: "Wins:",
                        value: wins
                      },
                      {
                        label: "Losses:",
                        value: losses
                      },
                      {
                        label: "Win ratio:",
                        value: winPercentage(wins, losses)
                      },
                      {
                        label: "Playing:",
                        value: playing
                      },
                    ]}
                  />
                </Col>
              </Row>
              <Row>
                {(achievements.length > 0)
                  ? (
                    <Col sm={4}>
                      <Achievements data={achievements}/>
                    </Col>
                  )
                  : null
                }
                <Col sm={4}>
                  <ProfileDisplay
                    heading="Current Bot"
                    data={hasActiveBot
                      ?
                      [
                        {
                          label: "Version:",
                          value: version
                        },
                        {
                          label: "Language:",
                          value: language
                        },
                        {
                          label: "Upload time:",
                          value: ((uploadTime === "") ? "" : <Moment format="DD/MM/YY HH:mm">{uploadTime}</Moment>)
                        }
                      ]
                      : []
                    }
                    onEmptyData={
                      <div>
                        <span>To learn how to upload your first bot visit </span>
                        <a href="https://docs.liagame.com/getting-started/" target="_blank" rel="noopener noreferrer">here</a>
                        .
                      </div>
                    }
                  />
                </Col>
                { isPrivate
                  ? (
                    <Col sm={4}>
                      <ProfileDisplay
                        heading="New bot"
                        data={(activeBotId !== latestBotId)
                          ?
                          [
                            {
                              label: "Upload time:",
                              value: (newBotUploadTime === "") ? "" : <Moment format="DD/MM/YY HH:mm">{newBotUploadTime}</Moment>
                            },
                            {
                              label: "Status:",
                              value: newBotStatus,
                              color: "#FF0000"
                            },
                            {
                              label: "Processing logs:",
                              value: (newBotProcessingLogs ? this.logToDownloadLink(newBotProcessingLogs) : null)
                            },
                            {
                              label: "Test match logs:",
                              value: (newBotTestMatchLogs ? this.logToDownloadLink(newBotTestMatchLogs) : null)
                            },
                            {
                              label: "Test match engine logs:",
                              value: (newBotTestMatchGameEngineLog ? this.logToDownloadLink(newBotTestMatchGameEngineLog) : null)
                            }
                          ]
                          : []
                        }
                        onEmptyData="New bot is now current."
                      />
                    </Col>
                  )
                  : null
                }

              </Row>
            </Col>
          </Row>
        </div>
        <h3 className="margin-bottom20">Games</h3>
        <ul className="custom-subnav">
          <li><a className={!isChallenges ? "active" : ""} role="button" onClick={() => this.loadGames(this.state.userId, 0, false)}>Ranked</a></li>
          <li><a className={isChallenges ? "active" : ""} role="button" onClick={() => this.loadGames(this.state.userId, 0, true)}>Challenges</a></li>
        </ul>
        <GamesTable data={gamesData} loading={loadingData} displayWinsAndLosses={true}/>
        {pageCount
          ? <ReactPaginate
              previousLabel={"<"}
              nextLabel={">"}
              breakLabel={"..."}
              breakClassName={"break-me"}
              pageCount={pageCount}
              marginPagesDisplayed={1}
              pageRangeDisplayed={5}
              onPageChange={this.handlePageClick}
              containerClassName={"pagination"}
              subContainerClassName={"pages pagination"}
              activeClassName={"active"}
              forcePage={currentPage}
            />
          : null
        }
      </div>
    )
  }
}

function winPercentage(wins, losses) {
  const p = wins / (wins + losses);
  if (isNaN(p)) return 0;
  return Math.round(p * 1000) / 1000

}

function mapStateToProps(state) {
  const { username } = state.authentication;
  return {
    username
  };
}

export default connect(mapStateToProps)(ProfilePage);
