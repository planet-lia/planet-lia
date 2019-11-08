import React from 'react';
import './_dependencies/components/matchViewer.css';
import './App.css';
import {BrowserRouter as Router, Route, Switch} from 'react-router-dom'
import {LocalMatchViewer} from "./components/LocalMatchViewer";
import {ReplayPicker} from "./components/ReplayPicker";


const App: React.FC = () => {
    let assetsServerPort = process.env.REACT_APP_ASSETS_SERVER_PORT;

    return (
        <Router>
            <Switch>
                <Route exact path="/" component={(props) =>
                    <ReplayPicker assetsServerPort={assetsServerPort} {...props} />}
                />
                <Route exact path="/viewer" component={(props) =>
                    <LocalMatchViewer assetsServerPort={assetsServerPort} {...props} />}
                />
            </Switch>
        </Router>
    );
};

export default App;
