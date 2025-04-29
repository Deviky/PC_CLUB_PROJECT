import React from "react";
import ReactDOM from "react-dom/client"; // Обратите внимание на 'client'
import App from "./App";
import "./index.css"
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);