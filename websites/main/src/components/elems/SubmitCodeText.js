import React, { Component } from 'react';
import Loader from 'react-loader-spinner';

import api from '../../utils/api';

class SubmitCodeText extends Component {
  constructor(props){
    super(props);
    this.state = {
      msg: null,
      loading: true,
      error: null
    }
  }

  componentDidMount = () => {
    this.loadBotData();
  }

  loadBotData = async () => {
    let activeBotId = null;
    let latestBotStatus =  null;


    try{
      const respBotActive = await api.game.getActiveBot();
      activeBotId = respBotActive.bot.botId;
    }
    catch(err) {
      if(!err.response){
        this.setState({
          error: "Network Error"
        });
        console.log(err.message);
      }
    }

    try {
      const respBotLatest = await api.game.getLatestBot();
      latestBotStatus = respBotLatest.bot.status
      this.setState({
        loading: false
      })
    } catch (err) {
      if(err.response){
        this.setState({ loading: false })
      } else {
        this.setState({
          loading: false,
          error: "Network Error"
        });
        console.log(err.message);
      }
    }
    this.popupMsgText(activeBotId, latestBotStatus)
  }

  popupMsgText = (activeBotId, latestBotStatus) => {
    const { setIsBotProcessing } = this.props;
    let msg;

    if(this.state.error===null) {
      if (latestBotStatus==="processing" || latestBotStatus==="testing") {
        msg = "Your latest bot is still processing. You can submit your code after it is done."
        setIsBotProcessing(true);
      } else {
        if (activeBotId) {
          msg = "Submitting code will override your current bot."
        } else if (latestBotStatus) {
          msg = "Click submit to upload your bot."
        } else {
          msg = "Click submit to upload your first bot."
        }
        setIsBotProcessing(false);
      }
    }

    this.setState({msg: msg});
  }

  render() {
    if (this.state.loading){
      return (
        <div className="loader-sm">
          <div className="cont-loader">
            <Loader
              type="Triangle"
              color="#019170"
              height="100"
              width="100"
            />
          </div>
        </div>
      );
    }
    if (this.state.error) {
      return (<p className="text-danger resp-msg">{this.state.error}</p>)
    }
    return (<div><p>{this.state.msg}</p></div>)
  }

}

export default SubmitCodeText;
