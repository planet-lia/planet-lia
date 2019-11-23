import React from 'react';
import { Col, Row } from 'react-bootstrap';

import Contacts from '../elems/Contacts';
import PersonalContact from '../elems/PersonalContact';

const AboutPage = () => {
  return (
    <div className="about">
      <div className="custom-section sec-short">
        <div className="container">
          <Row>
            <Col md={6} sm={12}>
              <img id="lia-team" src="/lia_team_sf.jpg" alt="Lia Team" />
            </Col>
            <Col md={6} sm={12}>
              <h2 className="title no-top team">The Team</h2>
              <p>We are Aljaž (left), Miha (middle) and Gregor (right) the three
              members of Lia team. We are all studying Computer Science at the
              University of Ljubljana, Slovenia and we all love building things,
              hanging out in programming communities and in general have fun.
              We started working on Lia in early 2018 as a hobby project but it has
              since evolved into a fully fledged coding competition. We are working
              on Lia in our spare time with full support from our university, so kudos
              to them!</p>
              <h2 className="title">Our Mission</h2>
              <p>Programming is an art. It gives you the ability to express yourself
              from mere concept to a full implementation. With Lia we are building a
              community. A community where programming is a way to connect with
              people, to bring fun and creativity to programming, have some healthy
              competition and learn from one another.</p>
            </Col>
          </Row>
        </div>
      </div>
      <div className="custom-section sec-gray sec-short">
        <div className="container">
          <Row>
            <Col lg={12}>
              <h2 className="title no-top">Contact Us</h2>
              <PersonalContact
                name="Aljaž"
                email="aljaz@liagame.com"
                desc="The project captain, steers the ship and handles the core game together with its tooling and supported languages. He is more than happy to discuss any general or game related stuff."
              />
              <PersonalContact
                name="Gregor"
                email="gregor@liagame.com"
                desc="Our technology guru who keeps the servers up. If anything goes down, he is the guy you need to be pissed at. If you wish to talk containers, K8, servers, security or networking don’t be afraid to contact him."
              />
              <PersonalContact
                name="Miha"
                email="miha@liagame.com"
                desc="The frontend and visual appearance boss with an eye for detail. He will gladly take on any suggestions for the website and overall design for the platform and won’t be satisfied until he implements it perfectly."
              />
            </Col>
          </Row>
          <Row>
            <Col lg={12}>
              <Contacts className="about-cont-contact"/>
            </Col>
          </Row>
        </div>
      </div>
    </div>
  );
}

export default AboutPage;
