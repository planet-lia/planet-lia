import React, { Component } from 'react';
import { Row, Col, Glyphicon, Button } from 'react-bootstrap';
import Scrollchor from 'react-scrollchor';
import { Link } from 'react-router-dom';

import Replay from '../elems/Replay';
import ReplayThumb from '../elems/ReplayThumb';
import SubscriptionPopup from '../views/SubscriptionPopup';
import Supporters from '../elems/Supporters';
import Contacts from '../elems/Contacts';
import queryString from 'query-string';

import api from '../../utils/api';

import thumb1 from '../../assets/thumb1.jpg';
import thumb2 from '../../assets/thumb2.jpg';
import thumb3 from '../../assets/thumb3.jpg';

class LandingPage extends Component {
  constructor(props) {
    super(props);
    this.state = {
      gameId: null,
      showTryNowPopup: false,
      showSubscribePopup: false
    }
  }

  componentDidMount = () => {
    //save invite reference for registration
    const parms = queryString.parse(this.props.location.search);
    if(parms.ref){
      this.saveReference(parms.ref);
    }
  }

  saveReference = async (refUserName) => {
    try {
      const respUserId = await api.user.getUsernameToUserId(refUserName);
      localStorage.setItem("inviteRefUserId", respUserId.userId);
    } catch(err) {
      this.setState({
        error: "Network Error"
      });
      console.log(err.message);
    }
  }

  redirectToLiveDemo = () => {
    window.open("/editor", "_blank");
  }

  redirectToGettingStarted = () => {
    window.open("https://docs.liagame.com/", "_blank");
  }

  onPopupClose = () => {
    this.setState({
      showTryNowPopup: false,
      showSubscribePopup: false
    });
  }

  render() {
    return (
      <div>
        <div id="land-pic">
          <div id="land-cont-title">
            <div className="land-slogan">Competitive</div>
            <div className="land-slogan">Coding Game</div>
            <div id="land-desc">Bring your code to life</div>
            <div id="land-subscribe"><a to="" onClick={() => this.setState({showSubscribePopup: true})}>Subscribe</a></div>

            <Button bsClass="btn land-btn btn-try" onClick={() => this.redirectToLiveDemo()}>Live Demo<div className="btn-subtext">Without registration</div></Button>
            <Button bsClass="btn land-btn btn-get-started" onClick={() => this.redirectToGettingStarted()}>Start playing</Button>
          </div>
        </div>
        <div className="custom-section sec-short">
          <div className="container text-center">
            <h2 className="tour-title">Slovenian Lia Tournament 2019</h2>
            <h4 className="tour-date">18 Feb - 14 Mar</h4>
            <p>
              Are you a university or high school student from Slovenia and know
              a little bit of programming?<br />
              Join Lia and battle for the coding glory!
            </p>
            <h3 id="live-now" className="tour-title">See the results!</h3>
            <Link to="/tournament" className="btn custom-btn custom-btn-xl margin-top20">Tournament Page</Link>
            <Supporters />
          </div>
        </div>
        <div id="what-is-lia" className="custom-section sec-gray">
          <div className="container">
            <Row>
              <Col md={7}>
                <div className="cont-video">
                  <iframe width="560" height="349" title="Teaser Video" src="https://www.youtube.com/embed/aB4XEbj-R9Y?rel=0&showinfo=0" frameBorder="0" allowFullScreen />
                </div>
              </Col>
              <Col md={5}>
                  <h2 className="land-what-title">What is Lia?</h2>
                  <p className="land-what-text">
                    Lia is a competitive coding game where your goal is
                    to lead your units to victory by using your awesome
                    coding skills. Create your bot and put it to the
                    test. Our aim is to create an environment where you can
                    compete with your friends and programmers from all around
                    the world.
                  </p>
                  <Contacts className="land-cont-contact"/>
              </Col>
            </Row>
          </div>
        </div>
        <div className="custom-section">
          <div className="land-cont-title">
            <h2 className="land-title">WATCH GAMES</h2>
            <div className="land-subtext">Watch programmers battle each other.</div>
          </div>
          <div className="container" id="land-cont-watch">
            { this.state.gameId
              ? (<Row>
                  <Col id="land-cont-player">
                    { this.state.gameId===1 ? <Replay containerId="player1" replayFileBase64="" number={ this.state.gameId } /> : null }
                    { this.state.gameId===2 ? <Replay containerId="player1" replayFileBase64="" number={ this.state.gameId } /> : null }
                    { this.state.gameId===3 ? <Replay containerId="player1" replayFileBase64="" number={ this.state.gameId } /> : null }
                  </Col>
                </Row>)
              : null
            }
            <Row>
              <Col md={4}>
                <Scrollchor to="#land-cont-watch"><ReplayThumb imageSrc={thumb1} onThumbClick={ () => this.setState({ gameId: 1 }) } replayTitle="3Head vs neverlucky" /></Scrollchor>
              </Col>
              <Col md={4}>
                <Scrollchor to="#land-cont-watch"><ReplayThumb imageSrc={thumb2} onThumbClick={ () => this.setState({ gameId: 2 }) } replayTitle="PrekaljeniLisjak vs grekiki1234" /></Scrollchor>
              </Col>
              <Col md={4}>
                <Scrollchor to="#land-cont-watch"><ReplayThumb imageSrc={thumb3} onThumbClick={ () => this.setState({ gameId: 3 }) } replayTitle="root vs ailia" /></Scrollchor>
              </Col>
            </Row>
          </div>
        </div>
        <div className="custom-section sec-gray">
          <div className="land-cont-title">
            <h2 className="land-title">COMPETE</h2>
            <div className="land-subtext">Code, fight, win. And have fun!</div>
          </div>
          <div className="container">
            <Col className="land-funs" md={4}>
              <div className="land-cont-glyph">
                <Glyphicon className="land-glyph" glyph="wrench" />
              </div>
              <div className="land-subtitle center-text">Develop bots</div>
              <div className="center-text">
                Use Java, Python3 or Kotlin to create your very own bot.
              </div>
            </Col>
            <Col className="land-funs" md={4}>
              <div className="land-cont-glyph">
                <Glyphicon className="land-glyph" glyph="road" />
              </div>
              <div className="land-subtitle center-text">Join the leaderboard</div>
              <div className="center-text">
                Join the global leaderboard and compete for coding fame and glory.
              </div>
            </Col>
            <Col className="land-funs" md={4}>
              <div className="land-cont-glyph">
                <Glyphicon className="land-glyph" glyph="screenshot" />
              </div>
              <div className="land-subtitle center-text">Battle others</div>
              <div className="center-text">
                Battle your bots against the ones created by your friends or developers from all around the world.
              </div>
            </Col>
          </div>
        </div>
        <SubscriptionPopup
          dialogClassName="custom-popup pop-sub pop-text"
          show={this.state.showSubscribePopup}
          onHide={this.onPopupClose}
          onButtonClick={this.onPopupClose}
          heading="Subscribe"
          buttonText="Subscribe"
        />
      </div>

    );
  }

}

export default LandingPage;
