import React, { Component } from 'react';
import BootstrapTable from 'react-bootstrap-table-next';
import Loader from 'react-loader-spinner';

class Table extends Component {

  noDataIndication = () => {
    if(this.props.loading){
      return (
        <div className="cont-table-loader">
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
    } else {
      return "No data to display";
    }
  }

  render(){
    return (
      <BootstrapTable
        data = {this.props.data}
        columns = {this.props.columns}
        keyField={this.props.keyField}
				noDataIndication={this.noDataIndication}
        rowClasses={this.props.rowClasses}
				hover
				condensed
      />
    );
  }

}

export default Table;
