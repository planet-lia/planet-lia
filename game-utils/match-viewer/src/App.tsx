import React from 'react';
import './components/matchViewer.css';
import {Col, Grid, Row} from "react-bootstrap";
import {MatchAdvanced} from './components/MatchAdvanced';

const App: React.FC = () => {
    return (
        <Grid>
            <Row>
                <Col md={12}>
                    <MatchAdvanced
                        replayUrl={"http://localhost:3337/lia-1/assets/1.0/replay-example.json"}
                        replayFile={null}
                        loopMatch={false}
                        assetsBaseUrl={"http://localhost:3337"}
                    />
                </Col>
            </Row>
        </Grid>
    );
};

export default App;
