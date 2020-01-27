import React, { Component } from 'react';
import { Button, FormControl, Glyphicon, Dropdown, MenuItem } from 'react-bootstrap';
import MonacoEditor from 'react-monaco-editor';
import Loader from 'react-loader-spinner'
import Cookies from 'universal-cookie';
import ReactResizeDetector from 'react-resize-detector';

import Replay from '../elems/Replay';
import Popup from '../views/Popup';
import PopupConfirm from '../views/PopupConfirm';
import PopupSubmitEditor from '../views/PopupSubmitEditor';
import WaitAlert from '../elems/WaitAlert';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { programmingLanguages } from '../../utils/constants/programmingLanguages';

import { connect } from 'react-redux';


class EditorPage extends Component {
  timeout;

  constructor(props) {
    super(props);
    this.state = {
      code: '',
      currentLang: "python3",
      currentLog: "Press RUN to generate a game.",
      currentReplayFileBase64: "",
      generatingGame: false,
      gameKey: 0,
      isNoLanguageSet: true,
      editorW: "100%",
      editorH: "100%",
      lastPlay: null,
      showWaitAlert: false,
      showResetAlert: false,
      waitRemain: 0,
      isCodeChanged: false,
      showSubmitPopup: false,
      error: null
    };
  }

  componentDidMount = () => {
    window.addEventListener('fullscreenchange', this.scrollToBottom);
    window.addEventListener('webkitfullscreenchange', this.scrollToBottom);
    window.addEventListener('mozfullscreenchange', this.scrollToBottom);
    window.addEventListener('MSFullscreenChange', this.scrollToBottom);

    if(localStorage.editorProgLang) {
      const lang = localStorage.editorProgLang;
      this.setState({
        currentLang: lang,
        isNoLanguageSet: false
      })
      if(localStorage.editorCode) {
        this.setState({
          code: localStorage.editorCode,
          isCodeChanged: true
        });
      } else {
        this.setLanguage(lang)
      }
    }
  }

  componentWillUnmount = () => {
    window.removeEventListener('fullscreenchange', this.scrollToBottom);
    window.removeEventListener('webkitfullscreenchange', this.scrollToBottom);
    window.removeEventListener('mozfullscreenchange', this.scrollToBottom);
    window.removeEventListener('MSFullscreenChange', this.scrollToBottom);

    clearTimeout(this.timeout);
  }

  setLanguage = (lang) => {
    const langData = programmingLanguages[lang];

    // Store current language

    fetch(langData.baseBotUrl)
      .then((resp)=>{ return resp.text() })
      .then( (text) => {this.setState({
        currentLang: lang,
        code: text,
        isNoLanguageSet: false,
        isCodeChanged: false,
      })
      localStorage.removeItem("editorCode");
      localStorage.setItem("editorProgLang", lang);
    });
  };

  generateGame = async () => {
    const { lastPlay, currentLang, isNoLanguageSet } = this.state;
    const currentTime = new Date();
    const delayMiliSec = 15000;

    if(isNoLanguageSet) {
      this.setState({
        error: "No programming language was choosen."
      })
      console.error("No programming language was choosen.")
      return false;
    }

    if(lastPlay && (currentTime - lastPlay) < delayMiliSec){
      this.setState({
        showWaitAlert: true,
        waitRemain: Math.ceil( (delayMiliSec - Number(currentTime - lastPlay)) /1000 )
      })
      return;
    }
    // Let the user know that the game is being generated
    this.setState({
      currentReplayFileBase64: "",
      generatingGame: true,
      gameKey: this.state.gameKey + 1,
    });

    // Set if tracking is enabled
    const cookies = new Cookies();
    let isTrackingOn = cookies.get('editor-tracking') !== "false";

    console.log(isTrackingOn + "  " + cookies.get('editor-tracking'));

    // Generate replay
    const response = await fetch('https://editor.cloud1.liagame.com/generate?tracking=' + isTrackingOn, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
          language: currentLang,
          code:     this.state.code
      })});
    const json = await response.json();

    // TODO handle if there is an error!
    let trackingId = json['trackingId'];

    // Fetch results
    for (let i = 0; i < 180; i++) {
      const response = await fetch('https://editor.cloud1.liagame.com/results/' + trackingId, {
          method: 'GET',
          headers: {
              'Accept': 'application/json',
          }});
      const json2 = await response.json();

      // Update current logs
      this.setState({ currentLog: json2['game']['log'] });
      this.scrollToBottom();


      if (json2['game']['finished']) {
          // Display new replay file
          this.setState({
            currentReplayFileBase64: json2['game']['replay'],
            generatingGame: false,
            gameKey: this.state.gameKey + 1,
            lastPlay: new Date(),
            waitRemain: 0
          });
          return;
      }

      await this.sleep(500);
    }

    this.setState({
      generatingGame: false,
      error: "Failed to fetch game results in time."
    })

    console.error("Failed to fetch game results in time.")

  };

  sleep = async (ms) => {
    return new Promise(resolve => {this.timeout = setTimeout(resolve, ms)});
  };

  resizePlayer = (width, height) => {
    this.setState({editorW: width, editorH: height});
  }

  scrollToBottom = () => {
    if(this.textLog){
      this.textLog.scrollTop = this.textLog.scrollHeight;
    }
  };

  onPopupClose = () => {
    this.setState({
      showWaitAlert: false,
      showResetAlert: false,
      showSubmitPopup: false,
    })
  }

  onChange = (newValue, e) => {
    this.setState({
      code: newValue,
      isCodeChanged: true,
    });
    localStorage.setItem("editorCode", newValue);
  }

  onReset = () => {
    if(this.state.isCodeChanged){
      this.setState({
        showResetAlert: true
      })
    } else {
      this.resetEditor();
    }
  }

  resetEditor = () => {
    this.setState({
      showResetAlert: false,
      code: "",
      currentLang: "python3",
      isNoLanguageSet: true
    })
    localStorage.removeItem("editorCode");
    localStorage.removeItem("editorProgLang");
  }

  render() {
    const {
      code,
      currentLang,
      currentLog,
      currentReplayFileBase64,
      generatingGame,
      gameKey,
      isNoLanguageSet,
      editorW,
      editorH,
      showWaitAlert,
      showResetAlert,
      showSubmitPopup,
      waitRemain
    } = this.state;

    const highlighting = programmingLanguages[currentLang].highlighting;

    const options = {
      selectOnLineNumbers: true,
      fontSize: 12,
      scrollBeyondLastLine: false,
      minimap: {
        enabled: false
      },
    };

    return (
      <div className="editor-main-cont cont-overflow">
        <div className="cont-fullpage editor-cont-page">
          <div id="editor-left">
            <div id="editor-cont-ui">
              <div id="editor-btn-reset" className="editor-cont-uielems">
                <Button bsClass="custom-btn btn-gray btn" onClick={() => this.onReset()} type="button" disabled={isNoLanguageSet}>Reset</Button>
              </div>
              <div id="editor-btn-help" className="editor-cont-uielems">
                <Dropdown id="dropdown-help">
                  <Dropdown.Toggle className="custom-btn btn-gray">
                    Help
                  </Dropdown.Toggle>
                  <Dropdown.Menu className="custom-btn btn-gray">
                    <MenuItem
                      href="https://docs.liagame.com/game-rules/"
                      target="_blank"
                      rel="noopener noreferrer"
                      eventKey="1"
                    >
                      Game rules
                    </MenuItem>
                    <MenuItem
                      href="https://docs.liagame.com/api/"
                      target="_blank"
                      rel="noopener noreferrer"
                      eventKey="2"
                    >
                      API
                    </MenuItem>
                    <MenuItem
                      href="https://docs.liagame.com/examples/aiming-at-the-opponent/"
                      target="_blank"
                      rel="noopener noreferrer"
                      eventKey="3"
                    >
                      Examples
                    </MenuItem>
                    <MenuItem
                      href="https://docs.liagame.com/getting-started/"
                      target="_blank"
                      rel="noopener noreferrer"
                      eventKey="4"
                    >
                      Download
                    </MenuItem>
                  </Dropdown.Menu>
                </Dropdown>
              </div>
              <div id="editor-btn-run" className="editor-cont-uielems">
                <Button bsClass="custom-btn btn" onClick={() => this.generateGame()} type="button" disabled={generatingGame || isNoLanguageSet}>
                  <Glyphicon glyph="play" />
                  {" RUN"}
                </Button>
              </div>
              <div id="editor-btn-submit" className="editor-cont-uielems">
                <Button bsClass="custom-btn btn" onClick={() => this.setState({showSubmitPopup: true})} type="button" disabled={isNoLanguageSet}>
                  <FontAwesomeIcon icon="upload" />
                  {" Submit"}
                </Button>
              </div>
            </div>
            <div id="cont-editor">
              <MonacoEditor
                width={editorW}
                height={editorH}
                language={highlighting}
                theme="vs-dark"
                value={code}
                options={options}
                onChange={this.onChange}
              />
              <ReactResizeDetector handleWidth handleHeight onResize={(width, height) => this.resizePlayer(width, height)} />
              {isNoLanguageSet
                ? (
                  <div id="editor-block">
                    <div id="cont-lang-select" className="text-center">
                      <p>Choose your desired programming language to start.</p>
                      <Button bsClass="custom-btn btn" onClick={() => this.setLanguage("python3")} type="button">
                        Python
                      </Button>
                      <Button bsClass="custom-btn btn" onClick={() => this.setLanguage("java")} type="button">
                        Java
                      </Button>
                      <Button bsClass="custom-btn btn" onClick={() => this.setLanguage("kotlin")} type="button">
                        Kotlin
                      </Button>
                      <p className="txt-small">If you want to switch the language later click on "Reset" button.</p>
                    </div>
                  </div>
                )
                : null
              }
            </div>
          </div>
          <div id="editor-right">
            <div id="editor-notification">
              {"This is just a demo. For full experience "}
              <a href="https://docs.liagame.com/getting-started/" target="_blank" rel="noopener noreferrer">download SDK</a>
              .
            </div>
            {/* Key resets the replay; instead of currentReplayFileBase64 do gameID */}
            <div id="editor-cont-replay" key={gameKey}>
              <Replay containerId="player" number={ 0 } replayFileBase64={ currentReplayFileBase64 } />
              {generatingGame &&
                <div id="editor-loader-overlay">
                  <div className="cont-loader">
                    <Loader
                      type="Triangle"
                      color="#019170"
                      height="100"
                      width="100"
                    />
                  </div>
                </div>
              }
            </div>
            <div id="editor-cont-log">
              <FormControl
                componentClass="textarea"
                value={currentLog}
                inputRef={ref => { this.textLog = ref; }}
                readOnly
                bsClass="form-control editor-input"
              />
            </div>
          </div>
        </div>

        <Popup
          dialogClassName="custom-popup pop-editor-sm pop-text"
          show={showWaitAlert}
          onHide={this.onPopupClose}
          onButtonClick={this.onPopupClose}
          heading="Please wait"
          buttonText="OK"
          center={true}
        >
          <WaitAlert wait={waitRemain}/>
        </Popup>

        <PopupConfirm
          dialogClassName="custom-popup pop-editor-sm pop-text"
          show={showResetAlert}
          onHide={this.onPopupClose}
          onButtonClick={this.resetEditor}
          heading="Warning"
        >
          <p>If you reset the editor, your changes will be lost.</p>
        </PopupConfirm>

        <PopupSubmitEditor
          show={showSubmitPopup}
          onHide={this.onPopupClose}
          code={code}
          language={currentLang}
        />

      </div>
    );
  }
}

function mapStateToProps(state) {
    const { isAuthenticated } = state.authentication;
    return {
        isAuthenticated
    };
}

export default connect(mapStateToProps)(EditorPage);
