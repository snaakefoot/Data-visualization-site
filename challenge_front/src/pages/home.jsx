import React from "react";
import { Link } from "react-router-dom";
import backgroundImage from './Cognira-logo-blue.png';

export const Home = (props) => {
  return (
    <div className="home-container">
      <div className="image-container">
        <img src={backgroundImage} alt="Background" style={{ width: '400px', height: 'auto' }} />
      </div>
      <h1>Welcome to Home Page</h1>
      <div className="button-container">
        <Link to="/login" className="light-button">
          <button>Login</button>
        </Link>
        <Link to="/register" className="light-button">
          <button>Register</button>
        </Link>
      </div>
    </div>
  );
}
