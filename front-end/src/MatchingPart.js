import React, { Component } from 'react';
import PropTypes from 'prop-types';

class MatchingPart extends Component {
  render() {
    return (
      <span className="match">{this.props.text}</span>
    );
  }
}

MatchingPart.propTypes = {
  text: PropTypes.string.isRequired,
}

export default MatchingPart;
