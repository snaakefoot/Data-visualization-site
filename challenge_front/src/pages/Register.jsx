import React, { useState } from "react";
import axios from "axios";
import backgroundImage from './Cognira-logo-blue.png';
import { useNavigate } from 'react-router-dom';
import { Link } from "react-router-dom";
export const Register = (props) => {
    const navigate = useNavigate();
    const [email, setEmail] = useState('');
    const [pass, setPass] = useState('');
    const [name, setName] = useState('');
    const [showError, setShowError] = useState(false);
    const [loading, setLoading] = useState(false);
    const backEndpoint = process.env.REACT_APP_BACK_ENDPOINT;
    const handleSubmit = async (e) => {
        let baseURL;
        if (backEndpoint) {
            // If BACK_ENDPOINT is set, use it in the URL
             baseURL  = `http://${backEndpoint}/signin`;
        } else {
            // If BACK_ENDPOINT is not set, use localhost
             baseURL  = 'http://localhost:8080/signin';
        }
        e.preventDefault();
        setLoading(true)
        try {
            const response = await axios.post(baseURL, {
                "email": email,
                "password": pass
            }); setLoading(false)
            // Do something with the response if needed
            navigate('/login');
        } catch (error) {
            setLoading(false)
            setShowError(true)
            console.error('Login failed:', error);
            // Handle login failure
        }
    };

    return (
        <div>
            {loading ? (<div className="loading-spinner">
                <svg
                    className="spinner"
                    width="50px"
                    height="50px"
                    viewBox="0 0 66 66"
                    xmlns="http://www.w3.org/2000/svg"
                >
                    <circle
                        className="path"
                        fill="none"
                        strokeWidth="6"
                        strokeLinecap="round"
                        cx="33"
                        cy="33"
                        r="30"
                    ></circle>
                </svg>
            </div>) : (
                <div className="auth-form-container">
                    <div className="image-container">
                        <img src={backgroundImage} alt="Background" style={{ width: '200px', height: 'auto' }} />
                    </div>

                    <h2>Register</h2>
                    <form className="register-form" onSubmit={handleSubmit}>
                        <label htmlFor="name">Full name</label>
                        <input value={name} name="name" onChange={(e) => setName(e.target.value)} id="name" placeholder="full Name" />
                        <label htmlFor="email">email</label>
                        <input value={email} onChange={(e) => setEmail(e.target.value)} type="email" placeholder="youremail@gmail.com" id="email" name="email" />
                        <label htmlFor="password">password</label>
                        <input value={pass} onChange={(e) => setPass(e.target.value)} type="password" placeholder="********" id="password" name="password" />
                        <button type="submit" onClick={handleSubmit}>register</button>
                        {showError && <p className="error-message" >Unvalid Informations</p>}
                    </form>
                    <button className="link-btn" ><Link to="/login">Already have an account? Login here.</Link></button>
                </div>
            )}
        </div>
    )
}
