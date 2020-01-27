import React from 'react';
import { Link } from 'react-router-dom';
import Moment from 'react-moment';
import { seconds2time } from '../../utils/helpers/time';
import Table from '../elems/Table';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const GamesTable = (props) => {
  let gamesColumns = [  {
    dataField: 'no1',
    text: 'Game',
    formatter: watchNowFormatter
  },{
    dataField: 'no2',
    text: 'Player [rank]',
    formatter: player1Formatter
  },{
    dataField: 'no7',
    text: 'Player [rank]',
    formatter: player2Formatter
  }, {
    dataField: 'no8',
    text: 'Result',
    formatter: resultFormatter
  }, {
    dataField: 'no4',
    text: 'Remaining Units',
    formatter: unitsRemainFormatter
  }, {
    dataField: 'no5',
    text: 'Duration',
    formatter: durationFormatter
  }, {
    dataField: 'no6',
    text: 'Date',
    formatter: dateFormatter
  }];

  // Remove Result column if there is no username specified
  if (!props.displayWinsAndLosses) {
    gamesColumns = gamesColumns.filter(function(el) { return el.text !== "Result"; });
  }

  return (
    <div className="cont-overflow cont-table">
      <Table {...props} columns={gamesColumns} keyField="matchId"/>
    </div>
  )
}

export default GamesTable;

function watchNowFormatter(cell, row, rowIndex) {
  if(row.isCompleted){
    return (
      <Link
        to={{
          pathname: "/games/" + row.matchId,
          state: row
        }}
        className="btn-watch"
      >
        <span className="icon"><FontAwesomeIcon icon="tv" /></span>
        <span>Watch</span>
      </Link>
    );
  } else {
    return <span className="status-pend">In Progress</span>
  }

}

function dateFormatter(cell, row, rowIndex) {
  if(row.isCompleted){
    return (
      <Moment format="DD/MM/YYYY HH:mm">{row.date}</Moment>
    );
  } else {
    return "-";
  }
}

function player1Formatter(cell, row, rowIndex) {
  return playerFormatter(cell, row, rowIndex, 1)
}

function player2Formatter(cell, row, rowIndex) {
  return playerFormatter(cell, row, rowIndex, 2)
}

function resultFormatter(cell, row, rowIndex) {
  if (row.result === 0) return "-";
  return row.isWinner ? <span className="result-win"><b>Won</b></span> : <span className="result-loss"><b>Lost</b></span>;
}

function playerFormatter(cell, row, rowIndex, playerIndex) {
  let player = usernameToProfileLink(row.player1);
  let rank = row.player1Rank;
  let isWinner = row.result === 1;
  if (playerIndex === 2) {
    player = usernameToProfileLink(row.player2);
    rank = row.player2Rank;
    isWinner = row.result === 2;
  }

  const trophyIcon = <FontAwesomeIcon icon="trophy" color={"#CCCCCC"}/>;
  const rankField = <span style={{color: "#CCCCCC"}}><small> [{rank}] </small></span>;
  if(isWinner){
    return (<span><strong>{player}</strong>{rankField}&nbsp;{trophyIcon}</span>)
  } else {
    return (<span>{player}{rankField}</span>)
  }
}

function usernameToProfileLink(username) {
  return (<Link to={"/user/" + username} style={{ textDecoration: 'none' }}>{username}</Link>);
}

function durationFormatter(cell, row, rowIndex) {
  if(row.isCompleted){
    return seconds2time(row.duration);
  } else {
    return "-";
  }
}

function unitsRemainFormatter(cell, row, rowIndex) {
  if(row.isCompleted){
    return (row.unitsRemain1 + " - " + row.unitsRemain2);
  } else {
    return "-";
  }
}
