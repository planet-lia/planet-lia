import React from 'react';
import { Row, Col, Button, Table } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import Prize from '../elems/Prize';
import Sponsors from '../elems/Sponsors';
import Supporters from '../elems/Supporters';

import logoPython from '../../assets/logo1.png';
import logoJava from '../../assets/logo2.png';
import logoKotlin from '../../assets/logo3.png';

const TournamentMain = (props) => {
  const content = props.content;
  return (
    <div>
      <div className="custom-section sec-gray sec-short">
        <div className="container">
          <Row className="tour-row-padding tour-row-xs-padding">
            <Col sm={6}>
              <h3 className="tour-title">{content.titleWant}</h3>
              <p>{content.txtWant}</p>
            </Col>
            <Col sm={6}>
              <div id="tour-cont-plang">
                <div>
                  <img id="logo-python" className="tour-plang-logo" src={ logoPython } alt="Python" />
                </div>
                <div>
                  <img id="logo-java" className="tour-plang-logo" src={ logoJava } alt="Java" />
                </div>
                <div>
                  <img id="logo-kotlin" className="tour-plang-logo" src={ logoKotlin } alt="Kotlin" />
                </div>
              </div>
            </Col>
          </Row>
          <Row className="tour-row-padding">
            <Col sm={6} smPush={6}>
              <h3 className="tour-title">{content.titleCheck}</h3>
              <p>{content.txtCheck}</p>
            </Col>
            <Col sm={6} smPull={6}>
              <div className="tour-cont-link text-center">
                <div className="tour-cont-icon-lg">
                  <FontAwesomeIcon icon="trophy" />
                </div>
                <a href="/leaderboard" target="_blank" rel="noopener noreferrer" className="btn custom-btn custom-btn-lg">
                  {content.btnLeaderboard}
                </a>
              </div>
              <div className="tour-cont-link text-center">
                <div className="tour-cont-icon-lg position-relative">
                  <FontAwesomeIcon icon="desktop" />
                  <FontAwesomeIcon id="tour-desktop-play" icon="play" />
                </div>
                <a href="/games" target="_blank" rel="noopener noreferrer" className="btn custom-btn custom-btn-lg">
                  {content.btnWatch}
                </a>
              </div>
            </Col>
          </Row>
          <Row>
            <Col sm={6}>
              <h3 className="tour-title">{content.titleGetStarted}</h3>
              <p>{content.txtGetStarted}</p>
            </Col>
            <Col sm={6}>
              <div className="tour-cont-link text-center">
                <div className="tour-cont-unit">
                  <img id="yellow-unit" src="/assets/warrior1.png" alt="yellow unit"/>
                </div>
                <a href="/editor" target="_blank" rel="noopener noreferrer" className="btn custom-btn custom-btn-lg">
                  {content.btnEditor}
                </a>
              </div>
              <div className="tour-cont-link text-center">
              <div className="tour-cont-unit">
                <img id="green-unit" src="/assets/warrior2.png" alt="green unit"/>
              </div>
                <Button bsClass="btn custom-btn custom-btn-lg" href="https://docs.liagame.com/" target="_blank" rel="noopener noreferrer">{content.btnGetStarted}</Button>
              </div>
            </Col>
          </Row>
        </div>
      </div>

      <div className="custom-section sec-short">
        <div className="container">
          <Row className="tour-row-padding">
            <Col lg={10} lgOffset={1} md={12}>
              <h3 className="tour-title text-center">{content.titleAgenda}</h3>
              <Table bsClass="table tour-agenda">
                <tbody>
                  <tr>
                    <td>18.2. - 9.3.</td>
                    <td>
                      {content.txtAgenda1}
                    </td>
                  </tr>
                  <tr>
                    <td>10.3. - 13.3.</td>
                    <td>
                      {content.txtAgenda2}
                    </td>
                  </tr>
                  <tr>
                    <td>14.3.</td>
                    <td>
                      {content.txtAgenda3}
                    </td>
                  </tr>
                </tbody>
              </Table>
            </Col>
          </Row>
          <Row className="tour-row-padding">
            <Col lg={10} lgOffset={1} md={12}>
              <h3 className="tour-title text-center">{content.titlePrizes}</h3>
              <Row>
                <Col sm={6}>
                  <Prize
                    color="#d9c72e"
                    mainText={content.txtPrize1}
                    subText={content.txtSubPrize1}
                    sponsor={{
                      before: content.txtSponBy,
                      text: content.txtSponsor1,
                      link: "https://styliff.com/"
                    }}
                  />
                  <Prize
                    color="#9b9b92"
                    mainText={content.txtPrize2}
                    subText={content.txtSubPrize2}
                  />
                  <Prize
                    color="#9a3f1b"
                    mainText={content.txtPrize3}
                    subText={content.txtSubPrize3}
                  />
                </Col>
                <Col sm={6}>
                  <Prize
                    color="#019170"
                    mainText={content.txtPrize7}
                    subText={content.txtSubPrize7}
                    sponsor={{
                      before: content.txtSponBy,
                      text: content.txtSponsor7,
                      link: "https://svet.fri.uni-lj.si/"
                    }}
                  />
                  <Prize
                    color="#019170"
                    mainText={content.txtPrize4}
                    subText={content.txtSubPrize4}
                  />
                  <Prize
                    color="#019170"
                    mainText={content.txtPrize5}
                    subText={content.txtSubPrize5}
                  />
                  <Prize
                    color="#019170"
                    mainText={content.txtPrize6}
                    subText={content.txtSubPrize6}
                  />
                </Col>
              </Row>
            </Col>
          </Row>
          <Row>
            <Col>
              <h3 className="tour-title text-center">{content.titleRules}</h3>
              <p className="text-center">
                {content.txtRules}
                <a href="/tournament/rules" target="_blank" rel="noopener noreferrer">
                  {content.linkRules}
                </a>
                .
              </p>
            </Col>
          </Row>
        </div>
      </div>

      <div className="custom-section sec-gray sec-short">
        <div className="container text-center">
          <h3 className="tour-title">{content.titleSponsors}</h3>
          <p>{content.txtSponsors}</p>
          <Sponsors />
          <Supporters />
        </div>
      </div>
    </div>
  )
}

export default TournamentMain;
