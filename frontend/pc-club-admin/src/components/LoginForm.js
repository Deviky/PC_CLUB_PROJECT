import { useState } from "react";
import { useNavigate, Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import "../styles/LoginForm.css"; // Подключаем стили

const LoginForm = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const { token, login } = useAuth();

  // Если пользователь уже авторизован — редиректим на главную
  if (token) {
    return <Navigate to="/dashboard" replace />;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
  
    try {
      const response = await fetch("https://dennis.michurin.fvds.ru:8966/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password }),
      });
  
      // Если статус ответа успешный
      if (response.ok) {
        // Проверяем, если в ответе есть тело и оно является JSON
        const data = await response.json();
        login(data.token, data.role); // Логиним пользователя и редиректим
        navigate("/dashboard");
      } else {
        // Обработка ошибок
        if (response.status >= 500 && response.status < 600) {
          setMessage("Ошибка сервера.");
        } else if (response.status === 401) {
          // Ошибка авторизации (например, неверный логин или пароль)
          const errorData = await response.json();
          setMessage(errorData.message || "Неверный логин или пароль!");
        } else {
          // Обработка других ошибок
          const errorData = await response.json();
          setMessage(errorData.message || "Неизвестная ошибка.");
        }
      }
    } catch (err) {
      console.error(err);
      setMessage("Ошибка сервера.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="login-form-container">
      <h2>Вход</h2>
      {message && <p className="error-message">{message}</p>}
      <div className="input-group">
        <label>Email</label>
        <input
          type="email"
          required
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
      </div>
      <div className="input-group">
        <label>Пароль</label>
        <input
          type="password"
          required
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <button type="submit">Войти</button>
    </form>
  );
};

export default LoginForm;
