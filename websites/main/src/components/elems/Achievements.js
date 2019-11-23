import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

const Achievements = (props) => {
  if(!props.data || (props.data && props.data.length===0)){
    return null;
  }

  let achiDisplay = [];
  props.data.forEach((item, index) => {
    achiDisplay.push(
      <tr key={index}>
        <td>
          <span className="achi-icon"><FontAwesomeIcon icon="medal" color={item.color}/></span>
          <span>{item.achievement}</span>
        </td>
      </tr>
    )
  })

  return (
    <div className={"prof-display " + (props.className ? props.className : "") }>
      <table>
        <thead>
          <tr>
            <td>
              <h4>Achievements</h4>
            </td>
          </tr>
        </thead>
        <tbody>
          {achiDisplay}
        </tbody>
      </table>
    </div>
  )
}

export default Achievements;
