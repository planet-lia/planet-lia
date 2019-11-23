import React from 'react';
import { Row, Col } from 'react-bootstrap';

import logoStyliff from '../../assets/logo_styliff.png';
import logoLoop from '../../assets/logo_loop.svg';
import logoAgilcon from '../../assets/logo_agilcon.png';
import logoSensum from '../../assets/logo_sensum.png';
import logoMedius from '../../assets/logo_medius.png';
import logoCeltra from '../../assets/logo_celtra.png';
import logoSolveraLynx from '../../assets/logo_solvera_lynx.svg';
import logoComtrade from '../../assets/logo_comtrade.png';
import logoRacDrustvo from '../../assets/logo_rac_drustvo.png';
import logoEpilog from '../../assets/logo_epilog.png';
import logoOutfit7 from '../../assets/logo_outfit7.png';
import logoSFRI from '../../assets/logo_sfri.png';
import logoSpica from '../../assets/logo_spica.png';

const Sponsors = () => {
  return (
    <div>
      <Row>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://styliff.com/" target="_blank" rel="noopener noreferrer">
              <img id="logo-styliff" className="tour-company" src={ logoStyliff } alt="Styliff tech" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.intheloop.io/summer-challenge/" target="_blank" rel="noopener noreferrer">
              <img id="logo-loop" className="tour-company" src={ logoLoop } alt="Loop" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.agilcon.com/sl/zaposlitev/" target="_blank" rel="noopener noreferrer">
              <img id="logo-agilcon" className="tour-company" src={ logoAgilcon } alt="Agilcon" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.sensum.eu/" target="_blank" rel="noopener noreferrer">
              <img id="logo-sensum" className="tour-company" src={ logoSensum } alt="Sensum" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.medius.si/" target="_blank" rel="noopener noreferrer">
              <img id="logo-medius" className="tour-company" src={ logoMedius } alt="Medius" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.celtra.com/" target="_blank" rel="noopener noreferrer">
              <img id="logo-celtra" className="tour-company" src={ logoCeltra } alt="Celtra" />
            </a>
          </div>
        </Col>
      </Row>
      <Row>
        <Col md={2} sm={4} smOffset={2} xs={6}>
          <div>
            <a href="https://www.solvera-lynx.com/en/" target="_blank" rel="noopener noreferrer">
              <img id="logo-solvera-lynx" className="tour-company" src={ logoSolveraLynx } alt="Solvera Lynx" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.comtrade.com/" target="_blank" rel="noopener noreferrer">
              <img id="logo-comtrade" className="tour-company" src={ logoComtrade } alt="Comtrade" />
            </a>
          </div>
        </Col>
        <Col md={2} mdOffset={0} sm={4} smOffset={2} xs={6}>
          <div>
            <a href="https://racunalnisko-drustvo.si/" target="_blank" rel="noopener noreferrer">
              <img id="logo-rac-drustvo" className="tour-company" src={ logoRacDrustvo } alt="Računalniško društvo" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://www.epilog.net/" target="_blank" rel="noopener noreferrer">
              <img id="logo-epilog" className="tour-company" src={ logoEpilog } alt="Epilog" />
            </a>
          </div>
        </Col>
      </Row>
      <Row>
        <Col md={2} mdOffset={3} sm={4} xs={6}>
          <div>
            <a href="https://outfit7.com/" target="_blank" rel="noopener noreferrer">
              <img id="logo-outfit7" className="tour-company" src={ logoOutfit7 } alt="Outfit7" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} xs={6}>
          <div>
            <a href="https://svet.fri.uni-lj.si/" target="_blank" rel="noopener noreferrer">
              <img id="logo-sfri" className="tour-company" src={ logoSFRI } alt="ŠS FRI" />
            </a>
          </div>
        </Col>
        <Col md={2} sm={4} smOffset={0} xs={6} xsOffset={3}>
          <div>
            <a href="https://www.spica.si/" target="_blank" rel="noopener noreferrer">
              <img id="logo-spica" className="tour-company" src={ logoSpica } alt="Špica" />
            </a>
          </div>
        </Col>
      </Row>
    </div>
  )
}

export default Sponsors;
