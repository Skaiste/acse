import React from 'react';
import ReactDOM from 'react-dom';
import Storer from './Storer';

it('renders without crashing', () => {
  const div = document.createElement('div');
  ReactDOM.render(<App />, div);
});
