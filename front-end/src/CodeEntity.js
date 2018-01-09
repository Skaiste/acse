import React, { Component } from 'react';
import MatchingPart from './MatchingPart';
import reactStringReplace from 'react-string-replace';
import PropTypes from 'prop-types';
import './CodeEntity.css';

class CodeEntity extends Component {
  state = {
    tab: " ".repeat(4),
  };

  replaceTabs(line) {
    var newLine = reactStringReplace(line, this.state.tab, (match, i) => (
      // key: key(by)+class+'-tab'+i, e.g. 1query-tab2
      <span key={this.props.by+this.props.class+'-tab'+i} className="tab"></span>
    ));
    return newLine;
  }

  replaceWithMatchingPartInString(element, string, posTo, posFrom) {
    var arr = [];
    arr.push(string.substring(0, posTo));
    arr.push(element);
    arr.push(string.substring(posFrom));
    return arr;
  }

  levelArray(arr) {
    var newArr = [];
    var tmpString = "";
    for (var i in arr){
      if (arr[i] instanceof Array && typeof arr[i] !== "string") {
        var leveled = this.levelArray(arr[i]);
        for (var j in leveled)
          newArr.push(leveled[j]);
      }
      else if (typeof arr[i] === "string" && i+1 < arr.length && typeof arr[i+1] === "string") {
        tmpString += arr[i];
      }
      else if (typeof arr[i] === "string") {
        tmpString += arr[i];
        newArr.push(tmpString);
        tmpString = "";
      }
      else
        newArr.push(arr[i]);
    }
    return newArr;
  }

  replaceMatches(code, codeparts) {
    // replace matched code with spans
    for (var i = codeparts.length-1; i >= 0; i--) {
      if (!codeparts[i].matching) continue;
      // get position of the part
      var pos = codeparts[i].position;
      // key: by+class+'-part'+pos.y
      var elementToInsert = <MatchingPart key={this.props.by+this.props.class+'-part'+pos.y} text={codeparts[i].part} />;
      // check if the string matches that position
      //if (!code[pos.x].startsWith(codeparts[i].part, pos.y)) console.log("Does not align!!!", codeparts[i]);
      if (!(code[pos.x] instanceof Array)) {
        code[pos.x] = this.replaceWithMatchingPartInString(elementToInsert, code[pos.x], pos.y, pos.y + codeparts[i].part.length);
      }
      else {
        var arr = code[pos.x];
        // check if the first element is big enough
        if (!(typeof arr[0] === 'string' && arr[0].length > pos.y))
          console.log("Does not align!!!", codeparts[i], arr[0]);
        else {
          arr[0] = this.replaceWithMatchingPartInString(elementToInsert, arr[0], pos.y, pos.y + codeparts[i].part.length);
          code[pos.x][0] = arr[0];
        }
      }
      code[pos.x] = this.levelArray(code[pos.x]);
    }
    return code;
  }

  getMatchingLineWithCoordinate(coord, other) {
    var isQuery = this.props.class === "query";
    var isResult = this.props.class === "result";
    for (var i in this.props.matchLines){
      if ((isQuery && !other) || (isResult && other)){
        if (this.props.matchLines[i].y === coord)
          return this.props.matchLines[i];
      }
      else if ((isResult && !other) || (isQuery && other)){
        if (this.props.matchLines[i].x === coord)
          return this.props.matchLines[i];
      }
    }
    return null;
  }
  countFuturePosition(lineNo) {
    // find latest match of the other in the provided position
    var matchPair = null;
    for (var i = lineNo; i >= 0 && matchPair === null; i--) {
      matchPair = this.getMatchingLineWithCoordinate(i, false);
    }
    // if it's the first one, return the location where it should be
    var indexOfMatchPair = this.props.matchLines.indexOf(matchPair);
    if (indexOfMatchPair === 0){
      return (matchPair.x > matchPair.y) ? matchPair.x : matchPair.y;
    }
    // get previous pair
    var previousPair = this.props.matchLines[indexOfMatchPair-1];
    if (matchPair.x-previousPair.x >= matchPair.y-previousPair.y){
      return (matchPair.x > matchPair.y) ? matchPair.x : matchPair.y;
    }
    return ((matchPair.x > matchPair.y) ? matchPair.x : matchPair.y) + matchPair.x-previousPair.x;
  }
  adjustLinePosition(code) {
    if (this.props.matchLines === null) return code;
    for (var i = 0; i < code.length; i++) {
      // set current position of other code
      var lineNo = parseInt(code[i].key.substring((this.props.by+this.props.class).length),10);
      var match = this.getMatchingLineWithCoordinate(lineNo, false);
      if (match === null) continue;
      var other = this.countFuturePosition(lineNo);
      var difference = other - i;
      if (difference <= 0) continue;
      for (var j = 0; j < difference; j++)
        code.splice(i, 0, <p className="empty">&nbsp;</p>);
      i += difference;
    }
    // reset bys
    code = React.Children.map(code, (child,  i) =>
      React.cloneElement(child, { by:  i })
    );
    return code;
  }

  render() {
    // replace all matching parts
    var code = this.replaceMatches(this.props.code, this.props.codeparts);
    console.log("code matched");
    // replace all tabs
    code = code.map(function(element){
      if (typeof element === "string")
        return this.replaceTabs(element);
      else if (element instanceof Array){
        var el = element.map(function(element2){
          if (typeof element2 === "string")
            return this.replaceTabs(element2);
          else
            return element2;
        }.bind(this));
        return el;
      }
    }.bind(this));
    console.log("code tabs replaced");

    // join into one big array
    var oneCode = code.map(function(value, index){
      return(
        // key: by+class+index
        <p key={this.props.by+this.props.class+index}><span className="lineNo" data-text={(index+1)+"."}></span>{value}</p>
      );
    }.bind(this));
    console.log("code joined");
    oneCode = this.levelArray(oneCode);
    console.log("code leveled");
    oneCode = this.adjustLinePosition(oneCode);
    console.log("code lines adjusted");

    return (
      <div className={["CodeEntity", this.props.class].join(" ")}>
        {oneCode}
      </div>
    );
  }
}

CodeEntity.propTypes = {
  class: PropTypes.string.isRequired,
  by: PropTypes.number.isRequired,
  code: PropTypes.array.isRequired,
  codeparts: PropTypes.array.isRequired,
  matchLines: PropTypes.array.isRequired,
}

export default CodeEntity;
