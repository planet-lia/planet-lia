import React from 'react';
import {
  Switch,
  Route,
  Link
} from 'react-router-dom';

import TournamentOverview from './TournamentOverview';
import LeaderboardPage from './LeaderboardPage';
import GamesPage from './GamesPage';

export default function TournamentPage(props) {
  console.log('props: ', props);
  console.log('props.match.params.subPage: ', props.match.params.subPage);

  return (
    <div>
      <div className="container">
        <div>
          <div className="navPillsCustom" style={{ marginTop: 20 }}>
            <ul className="nav nav-pills nav-justified">
              <li className={`nav-item ${props.match.params.subPage === 'overview' ? 'active' : ''}`}>
                <Link to="/tournament/overview">Overview</Link>
              </li>
              <li className={`nav-item ${props.match.params.subPage === 'leaderboard' ? 'active' : ''}`}>
                <Link to="/tournament/leaderboard">Leaderboard</Link>
              </li>
              <li className={`nav-item ${props.match.params.subPage === 'games' ? 'active' : ''}`}>
                <Link to="/tournament/games">Games</Link>
              </li>
              <li className={`nav-item ${props.match.params.subPage === 'myBot' ? 'active' : ''}`}>
                <Link to="/tournament/myBot">My Bot</Link>
              </li>
              <li className={`nav-item`}>
                <a href="https://docs.liagame.com/" target="_blank" rel="noopener noreferrer">Docs</a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <Switch>
        <Route path="/tournament/overview" component={TournamentOverview}/>
        <Route path="/tournament/leaderboard" component={LeaderboardPage}/>
        <Route path="/tournament/games" component={GamesPage}/>
      </Switch>
    </div>
  );
}
