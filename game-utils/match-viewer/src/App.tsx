import React from 'react';
import './components/matchViewer.css';
import {Col, Grid, Row} from "react-bootstrap";
import {MatchAdvanced} from './components/MatchAdvanced';
import {MatchBasic} from "./components/MatchBasic";

const App: React.FC = () => {
    return (
        <Grid>
            <Row>
                <Col md={12}>
                    <MatchBasic
                        replayUrl={"http://localhost:3337/lia-1/assets/1.0/replay-example.json"}
                        replayFile={null}
                        loopMatch={false}
                        assetsBaseUrl={"http://localhost:3337"}
                        setApplication={null}/>
                </Col>
            </Row>
        </Grid>
    );
};

export default App;
