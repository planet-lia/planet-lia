import React, { Component } from 'react';
import { Switch, Route } from 'react-router-dom';
import { Link } from 'react-router-dom';

import GamesList from '../views/GamesList';
import GameReplay from '../views/GameReplay';

class GamesPage extends Component {

  render(){
    return (
      <div>
        <div className="custom-notification margin-bottom20">
          <div className="container text-center">
            <div>Game page is currently in use for <Link to="/tournament">Slovenian Lia tournament 2019</Link>.</div>
          </div>
        </div>
        <div className="container">
          <Switch>
            <Route exact path='/games' component={GamesList} />
            <Route path='/games/:number' component={GameReplay} />
          </Switch>
        </div>
      </div>
    )
  }

}

export default GamesPage;
