import * as React from "react";
import {Component} from "react";
import {BotDetails} from "../match-viewer/botDetails";
import {Col, Row} from "react-bootstrap";

interface BotDetailsViewProps {
    botDetails: BotDetails[];
}

interface BotDetailsViewState {
    botIndex: number;
}

export class BotDetailsView extends Component<BotDetailsViewProps, BotDetailsViewState> {

    state: BotDetailsViewState = {
        botIndex: 0
    };

    render() {
        const {botDetails} = this.props;
        const {botIndex} = this.state;
        const details = botDetails[botIndex];

        if (botDetails.length === 0) {
            return null;
        }

        return (
            <Col md={12}>
                {/* Draw tabs */}
                <Row className="bot-details-tabs">
                    {botDetails.map((details: BotDetails, i: number) => {
                        return <div key={i}
                                    className={(i === botIndex) ? "bot-details-tabs-selected" : ""}
                                    style={{color: details.color}}
                                    onClick={() => {this.setState({botIndex: i})}}>
                            {details.botName}
                        </div>
                    })}
                </Row>
                <Row><hr style={{marginTop: "0.5em"}}/></Row>

                {/* Draw content */}
                <Row>Team index: {details.teamIndex}</Row>
                <Row>Rank: {details.rank}</Row>
                <Row>Used CPU time: {details.totalCpuTime} s</Row>
                <Row>Number of timeouts: {details.numberOfTimeouts}</Row>
                {
                    (details.disqualified)
                    ? <div>
                        <Row>Disqualification time: {details.disqualificationTime} s</Row>
                        <Row>Disqualification reason: {details.disqualificationReason}</Row>
                      </div>
                    : null
                }
            </Col>
        )
    }
}