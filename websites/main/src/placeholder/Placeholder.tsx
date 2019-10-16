import * as React from "react";
import {Component} from "react";
import "./style.css";
import {Image} from "react-bootstrap";

interface Props {}
interface State {}

export class Placeholder extends Component<Props, State> {

    render() {
        return (
            <div>
                <div id="logo">
                    <Image src="placeholder/logo-black.png"/>
                </div>

                <div id="container-banner">
                    <div className="container">
                        <div id="banner"/>
                    </div>
                    <div id="overlay-banner"/>
                </div>

                <div className="section shadow-sm">
                    <h2 className="red-title text-center">Stay tuned for Spring 2020!</h2>
                    <div className="cont-contacts text-center">
                        <a className="contacts-logo clr-gh" href="https://github.com/planet-lia/planet-lia/"
                           target="_blank" rel="noopener noreferrer"><i className="fab fa-github-square"/></a>
                        <a className="contacts-logo clr-dc" href="https://discord.gg/weXRxyU" target="_blank"
                           rel="noopener noreferrer"><i className="fab fa-discord"></i></a>
                        <a className="contacts-logo clr-fb" href="https://www.facebook.com/planetlia.official/" target="_blank"
                           rel="noopener noreferrer"><i className="fab fa-facebook-square"></i></a>
                        <a className="contacts-logo clr-em" href="mailto:info@planetlia.com" target="_self"
                           rel="noopener noreferrer"><i className="fas fa-envelope-square"></i></a>
                    </div>
                </div>

                <div className="section" id="section-content">
                    <div className="container">
                        <div id="content">

                            <div className="sub-section">
                                <h3> What is Planet Lia?</h3>
                                <p>
                                    Planet Lia is a bot programming platform for beginners
                                    and advanced coders alike, where you can use your coding
                                    skills to develop a bot for our video games and battle with
                                    it against other enthusiastic coders. Improve your coding
                                    skills, join the community and have fun!
                                </p>
                            </div>

                            <div className="sub-section">
                                <h3>We are currently in process of upgrading our platform.</h3>
                                <p>
                                    You can still access our <b>previous website at </b>
                                    <a href="https://www.liagame.com/" target="_blank"
                                       rel="noopener noreferrer">www.liagame.com</a>
                                    &nbsp;or you can check out the following:
                                </p>
                                <div className="row text-center" id="container-links">
                                    <div className="col-lg-6 col-md-12">
                                        <a href="https://www.liagame.com/editor" target="_blank"
                                           rel="noopener noreferrer" className="btn btn-lg custom-btn">Try out our
                                            previous game</a>
                                    </div>
                                    <div className="col-lg-6 col-md-12">
                                        <a href="https://www.liagame.com/tournament" target="_blank"
                                           rel="noopener noreferrer" className="btn btn-lg custom-btn">Tournament SLT
                                            2019</a>
                                    </div>
                                </div>
                            </div>

                            <div className="sub-section">
                                <h3 id="title-update">Want to get updates about the project?</h3>
                                <div className="row">
                                    <div className="col-lg-6 col-md-12">
                                        <a href="http://eepurl.com/gEhGDL">
                                            <div id="container-github" className="text-center shadow-sm">
                                            <h5 id="subscribe" className="title-updates">Subscribe to our news</h5>
                                            </div>
                                        </a>
                                    </div>
                                    <div className="col-lg-6 col-md-12">
                                        <a href="https://github.com/planet-lia/planet-lia/projects/1" target="_blank"
                                           rel="noopener noreferrer">
                                            <div id="container-github" className="text-center shadow-sm">
                                                <h5 id="github" className="title-updates">Follow our development</h5>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

