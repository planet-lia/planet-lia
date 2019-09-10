import * as React from "react";
import {Component} from "react";
import {MatchBasic, MatchBaseProps} from "./MatchBasic";
import {Col, Row} from "react-bootstrap";
import {MatchViewerApplication} from "../match-viewer";
import {round} from "../match-viewer/math/round";
import {Chart} from "../match-viewer/chart";
import {StatisticChart} from "./StatisticChart";
import {MatchDetail} from "../match-viewer/matchDetails";

interface MatchAdvancedProps extends MatchBaseProps {
}

interface MatchAdvancedState {
    app: MatchViewerApplication | null;
    matchDuration;
}

export class MatchAdvanced extends Component<MatchAdvancedProps, MatchAdvancedState> {

    state: MatchAdvancedState = {
        app: null,
        matchDuration: 0,
    };

    setApplication = (app: MatchViewerApplication) => {
        this.setState({
            app: app,
            matchDuration: round(app.matchDuration, 0),
        });
    };

    render() {
        const {app, matchDuration} = this.state;

        return (
            <div className="match">
                <Row>

                    {/* Match view */}
                    <Col md={8}>
                        <MatchBasic
                            replayFormat={this.props.replayFormat}
                            replayFile={this.props.replayFile}
                            replayUrl={this.props.replayUrl}
                            loopMatch={this.props.loopMatch}
                            assetsBaseUrl={this.props.assetsBaseUrl}
                            setApplication={this.setApplication}
                        />
                    </Col>

                    <Col md={4}>

                        {/* Match details */}
                        {app != null
                            ? <div>
                                <Row><h3>Bot 1 vs Bot 2</h3></Row>
                                <Row>Duration: {matchDuration} s</Row>
                                {app!.matchDetails!.map((detail: MatchDetail, i: number) => {
                                    return <Row key={i}>{detail.description}: {detail.value}</Row>
                                })}
                            </div>
                            : null
                        }
                    </Col>
                </Row>

                {/* Statistics */}
                {app != null
                    ? <div className="statistics">
                        <Row>
                            {app!.charts.map((stat: Chart, i: number) => {
                                return <Col md={4} key={i} className="statistic-col"><StatisticChart
                                    stat={stat}
                                    matchDuration={matchDuration}
                                /></Col>;
                            })}
                        </Row>
                    </div>
                    : null
                }
            </div>
        )
    }
}

