import * as React from "react";
import {Component} from "react";
import {Button, Col, Grid, Row} from "react-bootstrap";
import {MatchAdvanced} from "../_dependencies/components/MatchAdvanced";
import {Redirect, RouteComponentProps} from "react-router-dom";

interface LocalMatchViewerProps extends RouteComponentProps<{}> {
    assetsServerPort: number
}

interface LocalMatchViewerState {
    goBack: boolean
}

export class LocalMatchViewer extends Component<LocalMatchViewerProps, LocalMatchViewerState> {

    state: LocalMatchViewerState = {
      goBack: false
    };

    home = () => {
        this.setState({goBack: true});
    };

    render() {
        if (this.state.goBack) {
            this.props.history.push(this.props.location);
            return <Redirect to={`/`} />;
        }

        const search = new URLSearchParams(this.props.location.search);
        let replayUrl = search.get("replayUrl");

        let assetsUrl = (this.props.assetsServerPort === undefined)
            ? `games`
            : `http://localhost:${this.props.assetsServerPort}`;

        return (
            <Grid>
                <Row>
                    <Col md={12}>
                        <h2>Local Match Viewer</h2>
                        <Button onClick={this.home} className="btn btn-lg custom-btn">Replays List</Button>
                        <MatchAdvanced
                            replayUrl={replayUrl}
                            replayFile={null}
                            loopMatch={false}
                            assetsBaseUrl={assetsUrl}
                        />
                    </Col>
                </Row>
            </Grid>
        )
    }
}