import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const Contacts = (props) => {
  return (
    <div className={"cont-contacts " + props.className}>
      <a className="contacts-logo clr-fb" href="https://www.facebook.com/liagame/" target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={["fab", "facebook-square"]} /></a>
      <a className="contacts-logo clr-rd" href="https://www.reddit.com/r/liagame/" target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={["fab", "reddit"]} /></a>
      <a className="contacts-logo clr-dc" href="https://discord.gg/weXRxyU" target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={["fab", "discord"]} /></a>
      <a className="contacts-logo clr-gh" href="https://github.com/planet-lia/" target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={["fab", "github"]} /></a>
      <a className="contacts-logo clr-yt" href="https://www.youtube.com/channel/UC4BFxoC4iBr3m5LQVBxBDzA" target="_blank" rel="noopener noreferrer"><FontAwesomeIcon icon={["fab", "youtube"]} /></a>
      <a className="contacts-logo clr-em" href="mailto:info@liagame.com" target="_self" rel="noopener noreferrer"><FontAwesomeIcon icon="envelope" /></a>
    </div>
  );
}

export default Contacts;
