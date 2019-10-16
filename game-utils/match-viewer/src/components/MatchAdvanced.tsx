import * as React from "react";
import {Component} from "react";
import {MatchBaseProps, MatchBasic} from "./MatchBasic";
import {Col, Row} from "react-bootstrap";
import {MatchViewerApplication} from "../match-viewer";
import {round} from "../match-viewer/math/round";
import {Chart} from "../match-viewer/chart";
import {StatisticChart} from "./StatisticChart";
import {MatchDetail} from "../match-viewer/matchDetails";
import {BotDetails} from "../match-viewer/botDetails";
import {BotDetailsView} from "./BotDetailsView";

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
                        {app != null
                            ? <div>
                                <Row>{this.getBotsVersus(app.botDetails!, app.teamsFinalOrder!)}</Row>
                                <Row>Duration: {matchDuration} s</Row>

                                {/* Match details */}
                                {app!.matchDetails!.map((detail: MatchDetail, i: number) => {
                                    return <Row key={i}>{detail.description}: {detail.value}</Row>
                                })}

                                {/* Bot details */}
                                <Row>
                                    <BotDetailsView botDetails={app.botDetails!}/>
                                </Row>
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

    getBotsVersus = (botDetails: BotDetails[], teamsFinalOrder: number[]) : any => {

        // Sort bots by teams and by the order of the teams
        botDetails.sort((d1: BotDetails, d2: BotDetails): number => {
            let b1TeamPlace = teamsFinalOrder.indexOf(d1.teamIndex);
            let b2TeamPlace = teamsFinalOrder.indexOf(d2.teamIndex);
            return b1TeamPlace - b2TeamPlace;
        });

        return <h3>{botDetails.map((details: BotDetails, i: number) => {
            return <span key={i}>
                <span style={{color: details.color, fontWeight: "bold"}}>{details.botName}</span>
                <small>{
                    (i < botDetails.length - 1)
                        ? (details.teamIndex !== botDetails[i + 1].teamIndex)
                            ? <span style={{padding: "0.2em"}}>
                                {((details.teamIndex === teamsFinalOrder[0]) ? " defeats " : " and ")}
                            </span>
                            : ", "
                        : null }
                </small>
            </span>;
        })}</h3>
    }
}

