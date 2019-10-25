import React from 'react';
import './components/matchViewer.css';
import {Col, Row} from "react-bootstrap";
import {MatchAdvanced} from './components/MatchAdvanced';

const App: React.FC = () => {
    return (
        <Row>
            <Col md={2}/>
            <Col md={8}>
                <MatchAdvanced
                    replayUrl={"http://localhost:3333/file.json"}
                    replayFile={null}
                    loopMatch={false}
                    assetsBaseUrl={"http://localhost:3333"}
                />
            </Col>
            <Col md={2}/>
        </Row>
    );
};

export default App;
