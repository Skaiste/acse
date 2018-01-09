import React, { Component } from 'react';
import PropTypes from 'prop-types';
import CodeEntity from './CodeEntity';

class SearchResult extends Component {
  render() {
    var codeEntities = this.props.matches.map(function(value, index){
      return(
        <div className="entry" key={index}>
          <CodeEntity
            class="query"
            by={index}
            code={value.queryLines}
            codeparts={value.queryMatchingLineParts}
            matchLines={value.matchingLines}
          />
          <CodeEntity
            class="result"
            by={index}
            code={value.dataLines}
            codeparts={value.dataMatchingLineParts}
            matchLines={value.matchingLines}
          />
        </div>
      );
    });
    return (
      <div className="SearchResult">
        {codeEntities}
      </div>
    );
  }
}

SearchResult.propTypes = {
  matches: PropTypes.array.isRequired,
}

export default SearchResult;
