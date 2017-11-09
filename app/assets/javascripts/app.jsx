import React from 'react';
import { render } from 'react-dom';
import Greeter from './Greeter.jsx';

import '../stylesheets/style.css'
import '../stylesheets/bootstrap.css'

render((
    <div>
        <h1>DSE health check</h1>
        <Greeter name="Ninja" />
    </div>), document.getElementById("app"));