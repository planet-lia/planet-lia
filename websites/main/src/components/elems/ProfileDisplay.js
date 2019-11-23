import React from 'react';

const ProfileDisplay = (props) => {
  let header;
  let body = [];

  if(props.data.length===0){
    if(props.onEmptyData) {
      header = (
        <tr>
          <td>
            <h4>{props.heading}</h4>
          </td>
        </tr>
      );

      body = (
        <tr>
          <td>{props.onEmptyData}</td>
        </tr>
      )
    } else {
      return null;
    }
  } else {
    header = (
      <tr>
        <td colSpan="2">
          <h4>{props.heading}</h4>
        </td>
      </tr>
    );

    props.data.forEach((item, index) => {
      if(item.value!==null){
        body.push(
          <tr key={index}>
            <td>{item.label}</td>
            <td style={item.color ? {color: item.color} : null}>{item.value}</td>
          </tr>
        )
      }
    });
  }

  return (
    <div className={"prof-display " + (props.className ? props.className : "") }>
      <table>
        <thead>
          {header}
        </thead>
        <tbody>
          {body}
        </tbody>
      </table>
    </div>
  )

}

export default ProfileDisplay;
