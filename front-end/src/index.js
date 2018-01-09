import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom'
import './index.css';
import Storer from './Storer';
import Searcher from './Searcher';
import registerServiceWorker from './registerServiceWorker';
import 'bootstrap/dist/css/bootstrap.css';

var serverUrl = 'http://localhost:8080';
ReactDOM.render((
  <Router>
    <Switch>
      <Route exact path='/' component={() => <Searcher url={serverUrl}/>}/>
      <Route path='/store' component={() => <Storer url={serverUrl}/>}/>
    </Switch>
  </Router>
), document.getElementById('root'));
registerServiceWorker();
