import React from 'react';
import { Button } from 'react-bootstrap';
import Loader from 'react-loader-spinner'

const LoadingButton = (props) => {
  return (
    <Button bsClass={props.bsClass} disabled>
      <span className="btn-loading">
        {props.children}
        <span className="spinner">
          <Loader
            height="1em"
            type="ThreeDots"
            color="rgba(238,238,238,0.5)"
          />
        </span>
      </span>
    </Button>
  )
}

export default LoadingButton;
