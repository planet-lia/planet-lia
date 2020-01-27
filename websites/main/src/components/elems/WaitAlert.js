import React, { Component } from 'react';

class WaitAlert extends Component {
  constructor(props) {
    super(props);
    this.state = {
      wait: props.wait
    };
  }

  componentDidMount = () => {
    setTimeout(this.countdown, 1000);
  }

  countdown = () => {
    const newWait = this.state.wait -1;
    this.setState({wait: newWait});
    if(newWait > 0){
      setTimeout(this.countdown, 1000);
    }
  }

  render() {
    return (
      <div>
        <p>
          You have to wait 15 seconds before you can run a new game.
        </p>
        <p>
          {this.state.wait + " seconds remaining."}
        </p>
      </div>
    )
  }

}

export default WaitAlert;
