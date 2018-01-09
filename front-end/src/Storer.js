import React, { Component } from 'react';
import { Button, Form, FormGroup, Container, Label, Alert } from 'reactstrap';
import CodeMirror from 'react-codemirror';
import PropTypes from 'prop-types';
import request from "request";
import './Storer.css';

require('codemirror/mode/clike/clike');
require('codemirror/lib/codemirror.css');

class Storer extends Component {
  state = {
    input: '',
    response: { message: '', success: false, danger: false},
    tab: 4,
  };

  updateResponse(success, message) {
    var s = success, d = !success;
    this.setState({ response: {message: message, success: s, danger: d}});
  }

  reindent(value) {
    var newValue = "";
    var lines = value.split("\n");
    var oldTab = "";
    for (var l in lines) {
      var line = lines[l];
      var newLine = line.trim();
      if (line !== newLine){
        // set oldtab if not already set
        if (oldTab === ""){
          oldTab = line.substring(0, line.indexOf(newLine));
        }
        // count tabs
        var tabno = line.substring(0, line.indexOf(newLine)).length / oldTab.length;
        line = (" ".repeat(this.state.tab)).repeat(tabno) + newLine;
      }
      newValue += line + "\n";
    }
    newValue = newValue.substring(0, newValue.length-1);
    this.setState({input: newValue});
    return newValue;
  }

  handleChange = (value, data) => {
    //this.reindent(value);
    this.setState({input: value});
    this.setState({response: {message: '', success: false, danger: false}});
  }

  handleSubmit = (event) => {
    // adjust indentation
    var indentedInput = this.reindent(this.state.input);
    // create & send request
    request.post({
      url: this.props.url + '/storecode',
      form: {'code' : indentedInput}},
      function(err,httpResponse,body){
        // set message
        var response = JSON.parse(body);
        if (response.status === 500)
          this.updateResponse(false, "The server failed to process data.");
        else if (response.responseCode.indexOf("400") !== -1)
          this.updateResponse(false, response.text);
        else
          this.updateResponse(true, response.text);
      }.bind(this)
    )
    // get request message
    console.log(this.state.input);

    // reset input textarea
    this.setState({input: ""});

    event.preventDefault();
  }

  render() {
    var codemirrorOptions = {
			lineNumbers: true,
			mode: 'clike',
      indentUnit: this.state.tab,
      electricChars: true,
    }
    return (
      <div className="Storer">
        <Container>
          <Form onSubmit={this.handleSubmit}>
            <Alert color="success" isOpen={this.state.response.success}>{this.state.response.message}</Alert>
            <Alert color="danger" isOpen={this.state.response.danger}>{this.state.response.message}</Alert>
            <FormGroup>
              <Label for="input">Enter C code</Label>
              <CodeMirror ref="editor" options={codemirrorOptions} value={this.state.input} onChange={this.handleChange}/>
            </FormGroup>
            <Button type="submit">Send</Button>
          </Form>
        </Container>
      </div>
    );
  }
}

Storer.propTypes = {
  url: PropTypes.string.isRequired,
}

export default Storer;
