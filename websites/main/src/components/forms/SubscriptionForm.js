import React from 'react';

class SubscriptionForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
        emailValue: "",
    };
  }
  render() {
    return (
      <div>
        <p>
          Subscribe to our mailing list to get notified when Lia is ready to play online.
        </p>
        <div className="sub-form center-text">
          <form action="https://liagame.us19.list-manage.com/subscribe/post" method="POST" target="_blank" noValidate>
            <input type="hidden" name="u" value="93687b28d61c7cec37fd5237e" readOnly/>
            <input type="hidden" name="id" value="63276b2aa1" readOnly/>
            <input
              type="email"
              name="EMAIL"
              id="MERGE0"
              className="form-control"
              value={this.state.emailValue}
              onChange={ (e) => this.setState({emailValue: e.target.value}) }
              autoCapitalize="off"
              autoCorrect="off"
              placeholder="you@example.com"
            />
            <label className="checkbox" htmlFor="gdpr_11997">
              <input
                id="gdpr_11997"
                name="gdpr[11997]"
                value="Y"
                onChange={ this.props.onCheckboxChange }
                type="checkbox"
                checked={ this.props.allowEmailsChecked }
              />
              <div className="sub-chk-text">I would like to subscribe to the Lia newsletter to receive the latest news.</div>
            </label>
            <input type="submit" value="Subscribe" name="subscribe" id="mc-embedded-subscribe" className="hidden"/>

            <div style={{position: 'absolute', left: '-5000px'}} aria-hidden='true' aria-label="Please leave the following three fields empty">
                <label htmlFor="b_name">Name: </label>
                <input type="text" name="b_name" tabIndex="-1" value="" placeholder="Freddie" id="b_name" onChange={()=>{}}/>

                <label htmlFor="b_email">Email: </label>
                <input type="email" name="b_email" tabIndex="-1" value="" placeholder="youremail@gmail.com" id="b_email" onChange={()=>{}}/>

                <label htmlFor="b_comment">Comment: </label>
                <textarea name="b_comment" tabIndex="-1" placeholder="Please comment" id="b_comment" onChange={()=>{}}></textarea>
            </div>
        </form>
      </div>
      <p className="sub-footnote">
        We use Mailchimp as our marketing platform. By clicking below to
        subscribe, you acknowledge that your information will be transferred
        to Mailchimp for processing. Learn more about Mailchimp's privacy
        practices <a href="https://mailchimp.com/legal/" target="_blank" rel="noopener noreferrer">here</a>.
      </p>
    </div>
    )
  }
}

export default SubscriptionForm;
