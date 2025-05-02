import { useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { useAuth } from "../auth/AuthContext";
import "../styles/RegisterEmployeeForm.css";

const RegisterEmployeeForm = ({ onSuccess }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [role, setRole] = useState("ROLE_ADMIN");
  const { token } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password !== repeatPassword) {
      toast.error("Пароли не совпадают");
      return;
    }

    const employeeData = { email, password, role };

    try {
      const response = await fetch("https://62.109.1.5:8966/auth/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(employeeData),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success(data.message || "Сотрудник зарегистрирован!");
        if (onSuccess) {
          setTimeout(() => onSuccess(), 1500); // немного подождать после успеха
        }
      } else {
        toast.error(data.message || "Ошибка регистрации");
      }
    } catch (err) {
      toast.error("Ошибка сервера.");
    }
  };

  return (
    <div className="register-employee__container">
      <h2 className="register-employee__title">Регистрация сотрудника</h2>

      <form onSubmit={handleSubmit}>
        <div className="register-employee__field">
          <label className="register-employee__label">Email</label>
          <input
            type="email"
            className="register-employee__input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        <div className="register-employee__field">
          <label className="register-employee__label">Пароль</label>
          <input
            type="password"
            className="register-employee__input"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <div className="register-employee__field">
          <label className="register-employee__label">Повторите пароль</label>
          <input
            type="password"
            className="register-employee__input"
            value={repeatPassword}
            onChange={(e) => setRepeatPassword(e.target.value)}
            required
          />
        </div>

        <div className="register-employee__field">
          <label className="register-employee__label">Роль</label>
          <select
            className="register-employee__input"
            value={role}
            onChange={(e) => setRole(e.target.value)}
            required
          >
            <option value="ROLE_ADMIN">Администратор ПК клуба</option>
            <option value="ROLE_TECHNICAL">Техническая роль</option>
          </select>
        </div>

        <button type="submit" className="register-employee__button">
          Зарегистрировать сотрудника
        </button>
      </form>

      <ToastContainer position="bottom-right" />
    </div>
  );
};

export default RegisterEmployeeForm;