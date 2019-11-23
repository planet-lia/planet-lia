import React, { Component } from 'react';
import Cookies from 'universal-cookie';

class EditorTracking extends Component {
  constructor(props) {
    super(props);
    const cookies = new Cookies();
    cookies.set('editor-tracking', "false", { path: '/' });
  }

  render(){
    return (
      <div className="container">
        Tracking was turned off.
      </div>
    )
  }
}

export default EditorTracking;
