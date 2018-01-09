import React, { Component } from 'react';
import { Button, Form, FormGroup, InputGroup, Input, InputGroupAddon, Container, Label, Alert } from 'reactstrap';
import SearchResult from './SearchResult';
import PropTypes from 'prop-types';
import request from "request";
import './Searcher.css';

class Searcher extends Component {
  state = {
    input: '',
    response: { message: '', success: false, danger: false},
    match: null,
  };

  updateResponse(success, message) {
    var d = !success;
    this.setState({response: {message: message, success: false, danger: d}});
    console.log(this.state.response);
  }

  handleChange = (event) => {
    this.setState({response: {message: '', success: false, danger: false},
                   input: event.target.value,
                   match: null});
  }

  handleSubmit = (event) => {
    // reset result
    this.setState({match: null});
    // create & send request
    request.post({
      url: this.props.url + '/searchcode',
      form: {'querycode' : this.state.input}},
      function(err,httpResponse,body){
        // set message
        var response = JSON.parse(body);
        //console.log(response.matches);
        if (response.status === 500)
          this.updateResponse(false, "The server failed to process data.");
        else if (response.responseCode.indexOf("400") !== -1)
          this.updateResponse(false, response.text);
        else {
          this.updateResponse(true, response.text);
          this.setState({match: response.matches});
        }
      }.bind(this)
    )
    // get request message
    console.log("QUERY:", this.state.input);

    this.setState({input: ""});
    event.preventDefault();
  }

  render() {
    let result = null;
    if (this.state.match !== null)
      result = <SearchResult matches={this.state.match} />;
    return (
      <div className="Searcher">
        <Container>
          <Form className="input" onSubmit={this.handleSubmit}>
            <Alert color="success" isOpen={this.state.response.success}>{this.state.response.message}</Alert>
            <Alert color="danger" isOpen={this.state.response.danger}>{this.state.response.message}</Alert>
            <FormGroup>
              <Label for="input">Enter C code to search</Label>
              <InputGroup>
                <Input type="textarea" value={this.state.input} onChange={this.handleChange}/>
                <InputGroupAddon>
                  <Button type="submit">
                    <span className="glyphicon glyphicon-search"></span>
                  </Button>
                </InputGroupAddon>
              </InputGroup>
            </FormGroup>
          </Form>
          {result}
        </Container>
      </div>
    );
  }
}

Searcher.propTypes = {
  url: PropTypes.string.isRequired,
}

export default Searcher;
