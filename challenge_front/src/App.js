import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import './App.css';
import { Login } from "./pages/Login";
import { Register } from "./pages/Register";
import { Home } from "./pages/home";
import { GridComponent } from "./pages/GridComponent";
import { SalesOverTime } from "./pages/salesOverTime";
function App() {
  const [currentForm, setCurrentForm] = useState('login');

  const toggleForm = (formName) => {
    setCurrentForm(formName);
  }

  return (
    <Router>
    <div className="App">
      <Routes>
      <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/GridComponent" element={<GridComponent />} />
        <Route path="/salesOverTime" element={<SalesOverTime />} /> 
      </Routes>
    </div>
  </Router>
  );
}

export default App;
