import React, { Component } from 'react';
import ReactPaginate from 'react-paginate';
import GamesTable from '../elems/GamesTable';

import api from '../../utils/api';

class GamesList extends Component {
  constructor(props){
		super(props);
    this.state = {
      gamesData: [],
      loadingData: false,
      pageCount: 0,
      nGamesPerPage: 0,
      error: null
    };
  }

  componentDidMount = () => {
    this.loadGames(0);
  }

  loadGames = async (page) => {
    let offset = page * this.state.nGamesPerPage
    this.setState({loadingData: true, gamesData: []});
    try {
      const respGames = await api.game.getGamesList(offset);
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

  handlePageClick = (data) => {
    this.loadGames(data.selected);
  };

  render(){
    const { gamesData, loadingData, pageCount } = this.state;

    return (
      <div>
        <h2 className="margin-bottom20">Games</h2>
        <GamesTable data={gamesData} loading={loadingData}/>
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
            />
          : null
        }
      </div>
    )
  }

}

export default GamesList;
