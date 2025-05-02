import { useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import 'react-toastify/dist/ReactToastify.css';
import { useAuth } from "../auth/AuthContext";
import "../styles/RegisterClientForm.css";

const RegisterClientForm = () => {
  const [name, setName] = useState("");
  const [surname, setSurname] = useState("");
  const [dateOfBirth, setDateOfBirth] = useState("");
  const [email, setEmail] = useState("");
  const { token } = useAuth();

  const handleSubmit = async (e) => {
    e.preventDefault();

    const clientData = { name, surname, dateOfBirth, email };

    try {
      const response = await fetch("https://62.109.1.5:8966/client/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(clientData),
      });

      const data = await response.text();

      if (response.ok) {
        toast.success(data);
      } else {
        toast.error(data);
      }
    } catch (err) {
      toast.error("Ошибка сервера.");
    }
  };

  return (
    <div className="register-client__container">
      <h2 className="register-client__title">Зарегистрировать нового клиента</h2>

      <form onSubmit={handleSubmit}>
        <div className="register-client__field">
          <label className="register-client__label">Имя</label>
          <input
            type="text"
            className="register-client__input"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div className="register-client__field">
          <label className="register-client__label">Фамилия</label>
          <input
            type="text"
            className="register-client__input"
            value={surname}
            onChange={(e) => setSurname(e.target.value)}
            required
          />
        </div>
        <div className="register-client__field">
          <label className="register-client__label">Дата рождения</label>
          <input
            type="date"
            className="register-client__input"
            value={dateOfBirth}
            onChange={(e) => setDateOfBirth(e.target.value)}
            required
          />
        </div>
        <div className="register-client__field">
          <label className="register-client__label">Email</label>
          <input
            type="email"
            className="register-client__input"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <button type="submit" className="register-client__button">Зарегистрировать клиента</button>
      </form>

      <ToastContainer position="bottom-right" />
    </div>
  );
};

export default RegisterClientForm;