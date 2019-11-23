import React from 'react';

const PersonalContact = (props) => {
  return (
    <div className="pers-contact">
      <h4 className="name">{props.name}</h4>
      <div className="email"><a href={"mailto:" + props.email} target="_self" rel="noopener noreferrer">{props.email}</a></div>
      <p>{props.desc}</p>
    </div>
  );
}

export default PersonalContact;
