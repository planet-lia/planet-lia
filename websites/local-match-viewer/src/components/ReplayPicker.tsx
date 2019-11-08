import * as React from "react";
import {Component} from "react";
import {Col, Grid, Image, Row} from "react-bootstrap";
import {Redirect, RouteComponentProps} from 'react-router-dom';

interface ReplayPickerProps extends RouteComponentProps<{}> {
    assetsServerPort: number | undefined
}

interface ReplayPickerState {
    directoryItems: string[];
    currentPath: string;
    chosenReplayPath: string;
}

export class ReplayPicker extends Component<ReplayPickerProps, ReplayPickerState> {

    state: ReplayPickerState = {
        directoryItems: [],
        currentPath: "",
        chosenReplayPath: ""
    };

    componentDidMount = () => {
        this.navigate(this.state.currentPath, "/");
    };

    navigate = async (currentPath: string, item: string) => {
        let path = "";
        if (currentPath !== "") path += `${currentPath}`;
        if (item !== "") path += `${item}`;

        if (!item.endsWith("/")) {
            // It is a file and not a directory -> open LocalMatchViewer
            this.setState({chosenReplayPath: path});
            return;
        }

        let url = (this.props.assetsServerPort === undefined)
            ? `replays${path}`
            : `http://localhost:${this.props.assetsServerPort}${path}`;
        let response = await fetch(url);
        let text = await response.text();

        let el = document.createElement( 'html' );
        el.innerHTML = text;

        let list = el.getElementsByTagName( 'a' );
        let list2: string[] = [];

        for (let i = 0; i < list.length; i++) {
            let el = list.item(i);
            list2.push(el!.innerText);
        }
        this.setState({
            directoryItems: list2,
            currentPath: currentPath + item
        });
    };

    back = () => {
        let currentPath = this.state.currentPath;
        let newPath = "";
        currentPath = currentPath.substr(0, currentPath.length - 1);
        if (currentPath.lastIndexOf("/") !== -1) {
            newPath = currentPath.substr(0, currentPath.lastIndexOf("/"));
        }
        this.navigate(newPath, "/");
    };

    render() {
        const {directoryItems, chosenReplayPath, currentPath} = this.state;
        const {assetsServerPort} = this.props;

        if (chosenReplayPath !== "") {
            this.props.history.push(this.props.location);
            let replayUrl = (this.props.assetsServerPort === undefined)
                ? `replays${chosenReplayPath}`
                : `http://localhost:${assetsServerPort}${chosenReplayPath}`;
            return <Redirect to={`/viewer?replayUrl=${replayUrl}`} />;
        }

        return (
            <Grid>
                <Row>
                    <Col md={12}>
                        <Row>
                            <div id="logo">
                                <Image src="assets/logo-black-512.png"/>
                            </div>
                        </Row>
                        <Row>
                            <h2>Local Match Viewer</h2>
                        </Row>
                        <Row>
                            <h5>Choose a replay file that you want to view.</h5>
                        </Row>
                        <Row>
                            <h5>If you want to run a file outside of a replays/ directory run <b>lia -r [path-to-replay]</b></h5>
                        </Row>
                        <Row>
                            {(currentPath !== "/")
                                ? <Row key={-1} className="replay-picker-item">
                                    {/* eslint-disable-next-line jsx-a11y/anchor-is-valid */}
                                    <a onClick={this.back} className="replay-picker-item">..</a>
                            </Row>
                                : null
                            }
                            {directoryItems.map((item: string, i: number) => {
                                return <Row key={i}>
                                    {/* eslint-disable-next-line jsx-a11y/anchor-is-valid */}
                                    <a onClick={() => {this.navigate(this.state.currentPath, item)}}
                                       className="replay-picker-item">
                                        <li>{item}</li>
                                    </a>
                                </Row>
                            })}
                        </Row>
                    </Col>
                </Row>
            </Grid>
        )
    }
}