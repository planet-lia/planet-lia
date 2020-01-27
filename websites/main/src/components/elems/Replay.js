import React, { Component } from 'react';
import Fullscreen from 'react-full-screen';
import { Glyphicon } from 'react-bootstrap';
import ReactBootstrapSlider from 'react-bootstrap-slider';

class Replay extends Component {
  constructor(props) {
    super(props);
    this.state = {
      replay: null,
      duration: 0,
      time: 0,
      speed: 1,
      camera: 1,
      isPlaying: true,
      insideSpeedSlider: false,
      speedSliderTabId: -1,
      showCameras: false,
      isFull: false,
      overlayOpacity: 0,
      forceReplayWidth: "100%",
      swapTeamColors: false
    }
    this.puiRef = React.createRef();
  }

  componentDidMount = () => {
    window.addEventListener("resize", this.updateReplayWidth);
    window.addEventListener('fullscreenchange', this.resizeReplay);
    window.addEventListener('webkitfullscreenchange', this.resizeReplay);
    window.addEventListener('mozfullscreenchange', this.resizeReplay);
    window.addEventListener('MSFullscreenChange', this.resizeReplay);

    this.updateReplayWidth();
    if(this.props.number || this.props.replayFileBase64 || this.props.replayUrl){
      this.checkAndRun();
    }
    if (this.props.swapTeamColors) {
      this.setState({swapTeamColors: this.props.swapTeamColors})
    }
  }

  componentWillUnmount = () => {
    window.removeEventListener("resize", this.updateReplayWidth);
    window.removeEventListener('fullscreenchange', this.resizeReplay);
    window.removeEventListener('webkitfullscreenchange', this.resizeReplay);
    window.removeEventListener('mozfullscreenchange', this.resizeReplay);
    window.removeEventListener('MSFullscreenChange', this.resizeReplay);
    if(this.state.replay){
      this.state.replay.destroyReplay();
    }
  }

  updateReplayWidth = () => {
    let containerWidth = "100%";
    const avalibleW = window.screen.width;
    const avalibleH = window.screen.height - this.puiRef.current.clientHeight;
    const avalibleRatio = (avalibleW/avalibleH).toFixed(2);
    const replayRatio = (16/9).toFixed(2);

    if(avalibleRatio > replayRatio){
      containerWidth = (((avalibleH * replayRatio)/avalibleW)*100).toFixed(2) + "%";
    }

    if(containerWidth !== this.state.forceReplayWidth){
      this.setState({ forceReplayWidth: containerWidth });
      this.resizeReplay();
    }
  }

  resizeReplay = () => {
    if(this.state.replay){
      this.state.replay.resize();
    }
  }

  checkAndRun = () => {
    const { replayUrl, containerId, replayFileBase64, setGameStatistics, bubblesAllow, swapTeamColors } = this.props;

    if(window.liaGame){
      let url = "/assets/replays/replay_" + this.props.number + ".lia";
      if(replayUrl){
        url = replayUrl;
      }

      this.setState({
        replay: window.liaGame.playReplay(
          containerId,
          "/assets/",
          url,
          replayFileBase64,
          "/assets/banned-words.txt",
          this.setGameDuration,
          this.setTime,
          setGameStatistics
            ? setGameStatistics
            : function(){ return false },
          bubblesAllow ? bubblesAllow[0] : true,
          bubblesAllow ? bubblesAllow[1] : true,
          swapTeamColors
        )
      });
    } else {
      setTimeout(this.checkAndRun, 100);
    }
  }

  setGameDuration = (duration) => {
    this.setState({duration: duration});
  }

  setTime = (time) => {
    if(this.props.loop) {
      //replay infinite loop
      if(time===this.state.duration){
        if(this.state.replay){
            this.state.replay.changeTime(0);
          if(this.state.isPlaying===false){
            this.state.replay.forceUpdate();
          }
        }
        this.setState({time: 0});
      } else {
        this.setState({time: time});
      }
    } else {
      //regular functionality
      this.setState({time: time});
    }
  }

  onChangeTime = (event) => {
    if(this.state.replay===null) return;

    if(this.state.replay){
        this.state.replay.changeTime(event.target.value);
      if(this.state.isPlaying===false){
        this.state.replay.forceUpdate();
      }
    }
    this.setState({
      time: event.target.value,
    });
  }

  onTogglePlay = () => {
    if(this.state.replay===null) return;

    if(this.state.isPlaying===true){
      if(this.state.replay){
        this.state.replay.pause();
      }
      this.setState({
        isPlaying: false,
      });
    } else {
      if(this.state.replay){
        this.state.replay.resume();
      }
      this.setState({
        isPlaying: true,
      });
    }

    //overlayAnimation
    this.setState({ overlayOpacity: 0.7 })
    setTimeout(() => this.setState({ overlayOpacity: 0 }), 250);
  }

  onResetSpeed = () => {
    if(this.state.replay===null) return;

    if(this.state.replay){
      this.state.replay.changeSpeed(1);
    }
    this.setState({
      speed: 1,
      speedSliderTabId: 1
    });
  }

  onSpeedChange = (event) => {
    if(this.state.replay===null) return;

    if(this.state.replay){
      this.state.replay.changeSpeed(event.target.value);
    }
    this.setState({
      speed: event.target.value,
      speedSliderTabId: 1
    });
  }

  onCamChange = (camId) => {
    if(this.state.replay===null) return;

    if(this.state.replay){
      this.state.replay.changeCamera(camId);
      if(this.state.isPlaying===false){
        this.state.replay.forceUpdate();
      }
    }
    this.setState({ camera: camId+1 });
  }

  goFull = () => {
    if(this.state.replay===null) return;

    let goFullScreen;

    if(this.state.isFull===true){
      goFullScreen = false;
    } else {
      goFullScreen = true;
    }

    this.setState({ isFull: goFullScreen });
    this.resizeReplay();
  }

  render() {
    return (
      <Fullscreen
        enabled={this.state.isFull}
        onChange={isFull => this.setState({isFull})}
      >
        <div className={this.state.replay===null ? "cont-player no-replay" : "cont-player"}>
          <div className="row-replay" onClick={this.onTogglePlay} onDoubleClick={this.goFull}>
              <div id={ this.props.containerId } style={{width: (this.state.isFull ? this.state.forceReplayWidth : "100%") }}></div>
              <div className="player-overlay"></div>
              {this.state.isPlaying ? (
                <Glyphicon className="player-overlay-icon" glyph="play" style={{opacity: this.state.overlayOpacity}}/>
              ) : (
                <Glyphicon className="player-overlay-icon" glyph="pause" style={{opacity: this.state.overlayOpacity}}/>
              )}
          </div>
          <div className="row-pui" ref={ this.puiRef }>
            <div className="pui-timeline">
              <ReactBootstrapSlider value={this.state.time} min={0} max={this.state.duration} step={0.01} change={this.onChangeTime} tooltip="hide" />
            </div>
            <div className="pui-buttons">
              <div className="pui-btns-left">
                <div className="pui-btn" onClick={this.onTogglePlay}>
                  {this.state.isPlaying ? (
                    <Glyphicon className="pui-btns-glyph" glyph="pause" />
                  ) : (
                    <Glyphicon className="pui-btns-glyph" glyph="play" />
                  )}
                </div>
                <div className="pui-cont"
                  onMouseEnter={ () => this.setState({insideSpeedSlider: true}) }
                  onMouseLeave={ () => this.setState({insideSpeedSlider: false}) }
                >
                  <div className="pui-btn pui-btn-wide">
                    <span className="pui-text">{this.state.speed + "x"}</span>
                  </div>
                  {(this.state.replay!==null) && (this.state.insideSpeedSlider ||  this.state.speedSliderTabId>0) ? (
                    <div className="pui-speed-slider"
                      tabIndex={ this.state.speedSliderTabId }
                      onBlur={
                        this.state.insideSpeedSlider ? (
                          null
                        ) : (
                          () => this.setState({
                            insideSpeedSlider: false,
                            speedSliderTabId: -1
                          })
                        )
                      }
                    >
                      <span className="pui-divider"></span>
                      <ReactBootstrapSlider
                        value={this.state.speed}
                        min={-6}
                        max={6}
                        step={0.1}
                        change={this.onSpeedChange}
                        tooltip="hide"
                        />
                        <span className="pui-divider"></span>
                        <span className="pui-btn" onClick={this.onResetSpeed}>
                          <Glyphicon className="pui-btns-glyph" glyph="refresh" />
                        </span>
                    </div>
                  ) : null}
                </div>
              </div>
              <div className="pui-btns-right">
                <div className="pui-cont" onMouseEnter={() => this.setState({showCameras: true})} onMouseLeave={() => this.setState({showCameras: false})}>
                  {(this.state.replay!==null) && this.state.showCameras ? (
                    <div className="pui-cont pui-cameras">
                      <div className="pui-btn" onClick={() => this.onCamChange(0)} style={this.state.camera===1 ? {color: "#facd3b"} : {}}>
                        <Glyphicon glyph="facetime-video" />
                        <span className="pui-text"> 1</span>
                      </div>
                      <div className="pui-btn" onClick={() => this.onCamChange(1)} style={this.state.camera===2 ? {color: "#facd3b"} : {}}>
                        <Glyphicon glyph="facetime-video" />
                        <span className="pui-text"> 2</span>
                      </div>
                      <div className="pui-btn" onClick={() => this.onCamChange(2)} style={this.state.camera===3 ? {color: "#facd3b"} : {}}>
                        <Glyphicon glyph="facetime-video" />
                        <span className="pui-text"> 3</span>
                      </div>
                      <span className="pui-divider"></span>
                    </div>
                  ) : null }
                  <div className="pui-btn">
                    <Glyphicon className="pui-btns-glyph" glyph="facetime-video" />
                    <span className="pui-text">{" " + this.state.camera}</span>
                  </div>
                </div>
                <div className="pui-btn" onClick={this.goFull}>
                  {this.state.isFull ? <Glyphicon className="pui-btns-glyph" glyph="fullscreen" /> : <Glyphicon className="pui-btns-glyph" glyph="fullscreen" />}
                </div>
              </div>
            </div>
          </div>
        </div>
      </Fullscreen>
    )
  }

}

export default Replay;
