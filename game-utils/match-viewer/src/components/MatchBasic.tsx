import {Component, createRef, default as React, RefObject} from 'react';
import Fullscreen from 'react-full-screen';
import {Glyphicon} from 'react-bootstrap';
import {ReactBootstrapSlider} from 'react-bootstrap-slider';
import {MatchViewerApplication, startGame} from "../match-viewer";
import {round} from "../match-viewer/math/round";
import {Camera} from "../match-viewer/camera";
import * as Pako from "pako";
import * as JSZip from "jszip";

export interface MatchBaseProps {
    /** json needs to be string; gzip and zip need to be ArrayBuffer */
    replayFile: string | ArrayBuffer | null;
    replayUrl: string | null;
    loopMatch: boolean;
    assetsBaseUrl: string;
}

interface MatchBaseState {
    app: MatchViewerApplication | null;
    duration: number,
    time: number,
    playbackSpeed: number,
    cameraIndex: number,
    isPlaying: boolean,
    insideSpeedSlider: boolean,
    speedSliderTabId: number,
    showCameras: boolean,
    isFull: boolean,
    overlayOpacity: number,
    forceViewerWidth: string,
    numberOfCameras: number,
    errorMessage: string
}

interface MatchBasicProps extends MatchBaseProps {
    setApplication: ((app: MatchViewerApplication) => void) | null;
}

export class MatchBasic extends Component<MatchBasicProps, {}> {

    // Parses the replay file and displays the gameName
    gameCanvas: HTMLDivElement | null = null;

    state: MatchBaseState = {
        app: null,
        duration: 0,
        time: 0,
        playbackSpeed: 1,
        cameraIndex: 0,
        isPlaying: true,
        insideSpeedSlider: false,
        speedSliderTabId: -1,
        showCameras: false,
        isFull: false,
        overlayOpacity: 0,
        forceViewerWidth: "100%",
        numberOfCameras: 0,
        errorMessage: ""
    };

    puiRef: RefObject<HTMLDivElement> = createRef();

    componentDidMount = () => {
        window.addEventListener("resize", this.updateViewerWidth);

        this.updateViewerWidth();

        this.loadReplayAndStart();
    };

    componentWillUnmount = () => {
        window.removeEventListener("resize", this.updateViewerWidth);
        if (this.state.app) {
            this.state.app!.destroy(true);
            this.setState({app: null});
        }
    };

    loadReplayAndStart = async () => {
        let response: Response | null = null;
        let responseClone: Response | null = null;
        let rawData: string | ArrayBuffer | null = null;

        // Fetch from url
        if (this.props.replayUrl !== null) {
            let replayUrl = this.props.replayUrl;
            response = await fetch(replayUrl);
            responseClone = response.clone();
        }
        // Already loaded but not parsed replay data
        else if (this.props.replayFile !== null) {
            rawData = this.props.replayFile;
        } else {
            console.error("replayFile and replayUrl are both null");
            return;
        }

        let replay: JSON;

        // Parse replay file
        try {
            try {
                let data = (response !== null)
                    ? await response.arrayBuffer()
                    : rawData as ArrayBuffer;
                try {
                    // Try gzip
                    replay = JSON.parse(Pako.inflate(new Uint8Array(data), {to: "string"}));
                } catch (e) {
                    // Try zip
                    let replayFileZip = Object.values((await JSZip.loadAsync(data)).files)[0];
                    replay = JSON.parse(await replayFileZip.async("text"));
                }
            } catch (e) {
                // Try plain json
                let data = (responseClone !== null)
                    ? await responseClone.text()
                    : rawData as string;
                replay = JSON.parse(data);
            }


            let app = startGame(replay!, this.props.assetsBaseUrl);
            this.gameCanvas!.appendChild(app.view);
            this.setState({
                app: app,
                duration: app.matchDuration
            });

            this.state.numberOfCameras = app.gameCameras.length;
            this.onCameraChange(0);
            this.setTime();

            // Send back application application
            if (this.props.setApplication != null) {
                this.props.setApplication(app);
            }
        }
        catch (e) {
            console.error(`ERROR: ${e}`);
            this.setState({errorMessage: "Failed to view the replay. Check if the file is in the correct format."});
        }
    };

    updateViewerWidth = () => {
        let containerWidth = "100%";
        const availableW = window.screen.width;
        const availableH = window.screen.height - this.puiRef.current!.clientHeight;
        const availableRatio = round(availableW / availableH, 2);
        const viewerRatio = round(16 / 9, 2);

        if (availableRatio > viewerRatio) {
            containerWidth = (((availableH * viewerRatio) / availableW) * 100).toFixed(2) + "%";
        }

        if (containerWidth !== this.state.forceViewerWidth) {
            this.setState({forceViewerWidth: containerWidth});
        }
    };

    // Calls itself recursively and syncs gameName time and timeline
    setTime = () => {
        let app = this.state.app;

        // this.app is null only when it is destroyed as this
        // method is only called after this.app has been initialized
        if (app == null) return;

        let time = app.time;

        if (this.props.loopMatch && time === this.state.duration) {
            // Infinitely loop the match
            app.time = 0;
            if (!this.state.isPlaying) {
                app.ticker.update();
            }
            this.setState({time: 0});
        } else {
            // Regular functionality
            this.setState({time: time});
        }

        setTimeout(this.setTime, 100)
    };

    // When user picks new time via timeline
    onChangeTime = (event) => {
        let app = this.state.app;

        if (app === null) return;

        app.time = event.target.value;
        if (!this.state.isPlaying) {
            app.ticker.update();
        }

        this.setState({
            time: event.target.value,
        });
    };

    onTogglePlay = () => {
        let app = this.state.app;

        if (app === null) return;

        if (this.state.isPlaying) {
            //this.app!.stop();
            app.playbackSpeed = 0;
            this.setState({
                isPlaying: false,
            });
        } else {
            // this.app!.start();
            app.playbackSpeed = this.state.playbackSpeed;
            this.setState({
                isPlaying: true,
            });
        }

        // Overlay play/pause animation
        this.setState({overlayOpacity: 0.7});
        setTimeout(() => this.setState({overlayOpacity: 0}), 250);
    };

    onResetSpeed = () => {
        let app = this.state.app;

        if (app === null) return;

        if (app) {
            app.playbackSpeed = 1;
        }
        this.setState({
            playbackSpeed: 1,
            speedSliderTabId: 1
        });
    };

    onSpeedChange = (event) => {
        let app = this.state.app;

        if (app === null) return;

        if (app) {
            app.playbackSpeed = event.target.value;
        }
        this.setState({
            playbackSpeed: event.target.value,
            speedSliderTabId: 1
        });
    };

    onCameraChange = (camIndex) => {
        let app = this.state.app;

        if (app === null) return;

        app.currentCamera = app.gameCameras[camIndex];
        if (!this.state.isPlaying) {
            app.ticker.update();
        }
        this.setState({cameraIndex: camIndex});
    };

    goFull = () => {
        let app = this.state.app;

        if (app === null) return;

        let goFullScreen = !this.state.isFull;
        this.setState({isFull: goFullScreen});
    };

    render() {
        let component = this;
        let {app} = this.state;

        return (
            <Fullscreen
                enabled={this.state.isFull}
                onChange={isFull => this.setState({isFull})}
            >
                {this.state.errorMessage !== "" ? <div> {this.state.errorMessage} </div> : null}
                <div className={app === null ? "cont-match-viewer viewer-not-loaded" : "cont-match-viewer"}>

                    {/* Displaying match */}
                    <div className="row-match-viewer">
                        <div ref={(thisDiv) => {
                            component.gameCanvas = thisDiv
                        }}
                             style={{width: (this.state.isFull ? this.state.forceViewerWidth : "100%")}}
                        />
                        {/*<div className="viewer-overlay"/>*/}
                        {this.state.overlayOpacity > 0
                            ? <Glyphicon className="viewer-overlay-icon" glyph={this.state.isPlaying ? "play" : "pause"}
                                         style={{opacity: this.state.overlayOpacity}}/>
                            : null
                        }
                    </div>

                    {/* Replay viewer control buttons */}
                    <div className="row-pui" ref={this.puiRef}>
                        {/* Timeline */}
                        <div className="pui-timeline">
                            <ReactBootstrapSlider value={this.state.time} min={0} max={this.state.duration} step={0.01}
                                                  change={this.onChangeTime} tooltip="hide"/>
                        </div>
                        {/* Control buttons */}
                        <div className="pui-buttons">
                            <div className="pui-btns-left">

                                {/* Play/pause buttons */}
                                <div className="pui-btn" onClick={this.onTogglePlay}>
                                    {this.state.isPlaying ? (
                                        <Glyphicon className="pui-btns-glyph" glyph="pause"/>
                                    ) : (
                                        <Glyphicon className="pui-btns-glyph" glyph="play"/>
                                    )}
                                </div>
                                {/* Playback playbackSpeed controls */}
                                <div className="pui-cont"
                                     onMouseEnter={() => this.setState({insideSpeedSlider: true})}
                                     onMouseLeave={() => this.setState({insideSpeedSlider: false})}
                                >
                                    <div className="pui-btn pui-btn-wide">
                                        <span className="pui-text">{this.state.playbackSpeed + "x"}</span>
                                    </div>
                                    {/* Playback playbackSpeed slider */}
                                    {(app !== null) && (this.state.insideSpeedSlider || this.state.speedSliderTabId > 0) ? (
                                        <div className="pui-speed-slider"
                                             tabIndex={this.state.speedSliderTabId}
                                             onBlur={
                                                 this.state.insideSpeedSlider ? undefined : (
                                                     () => this.setState({
                                                         insideSpeedSlider: false,
                                                         speedSliderTabId: -1
                                                     })
                                                 )
                                             }
                                        >
                                            <span className="pui-divider"/>
                                            <ReactBootstrapSlider
                                                value={this.state.playbackSpeed}
                                                min={-6}
                                                max={6}
                                                step={0.1}
                                                change={this.onSpeedChange}
                                                tooltip="hide"
                                            />
                                            <span className="pui-divider"/>
                                            <span className="pui-btn" onClick={this.onResetSpeed}>
                                                <Glyphicon className="pui-btns-glyph" glyph="refresh"/>
                                            </span>
                                        </div>
                                    ) : null}
                                </div>
                            </div>

                            <div className="pui-btns-right">

                                {/* Cameras */}
                                <div className="pui-cont" onMouseEnter={() => this.setState({showCameras: true})}
                                     onMouseLeave={() => this.setState({showCameras: false})}>
                                    {(app !== null) && this.state.showCameras ? (
                                        <div className="pui-cont pui-cameras">
                                            {app.gameCameras.map((camera: Camera, i: number) => {
                                                return <div className="pui-btn" key={i} onClick={() => this.onCameraChange(i)}
                                                            style={this.state.cameraIndex === i ? {color: "#facd3b"} : {}}>
                                                    <Glyphicon glyph="facetime-video"/>
                                                    <span className="pui-text"> {i + 1}</span>
                                                </div>;
                                            })}
                                            <span className="pui-divider"/>
                                        </div>
                                    ) : null}
                                    <div className="pui-btn">
                                        <Glyphicon className="pui-btns-glyph" glyph="facetime-video"/>
                                        <span className="pui-text">{" " + (this.state.cameraIndex + 1)}</span>
                                    </div>
                                </div>

                                {/* Fullscreen */}
                                <div className="pui-btn" onClick={this.goFull}>
                                    {this.state.isFull ? <Glyphicon className="pui-btns-glyph" glyph="fullscreen"/> :
                                        <Glyphicon className="pui-btns-glyph" glyph="fullscreen"/>}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </Fullscreen>
        )
    }
}
