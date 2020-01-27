import React, { Component } from 'react';

import { Glyphicon } from 'react-bootstrap';


class ReplayThumb extends Component {
  constructor(props) {
    super(props);
    this.state = {
      overlayOpacity: 0.7,
    }
  }

  render() {
    return (
      <div>
        <div
          className="cont-thumb"
          onClick={ this.props.onThumbClick }
          onMouseEnter={ () => this.setState({ overlayOpacity: 0.95 }) }
          onMouseLeave={ () => this.setState({ overlayOpacity: 0.7 }) }
        >
          <img className="thumb-thumbnail" src={ this.props.imageSrc } alt="" />
          <Glyphicon className="thumb-overlay" glyph="play" style={ {opacity: this.state.overlayOpacity} }/>
        </div>
        <div
          className="thumb-title"
          onClick={ this.props.onThumbClick }
          onMouseEnter={ () => this.setState({ overlayOpacity: 0.95 }) }
          onMouseLeave={ () => this.setState({ overlayOpacity: 0.7 }) }
        >
          { this.props.replayTitle }
        </div>
      </div>
    )
  }

}

export default ReplayThumb;
