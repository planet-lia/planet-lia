import React from 'react';
import './App.css';
import {Placeholder} from "./placeholder/Placeholder";

const App: React.FC = () => {
  return (
    <div>
        {process.env.REACT_APP_ONLINE_EDITOR === "true"
            ? "TODO header with Online Editor"
            : <Placeholder/>
        }
    </div>
  );
};

export default App;
