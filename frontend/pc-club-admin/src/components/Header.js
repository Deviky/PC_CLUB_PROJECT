import { useState } from "react";
import { useAuth } from "../auth/AuthContext";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import "../styles/header.css"; 

const Header = () => {
  const { logout, role, token } = useAuth();
  const navigate = useNavigate();
  const [searchEmail, setSearchEmail] = useState("");

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  const handleSearch = async () => {
    if (!searchEmail.trim()) return;

    try {
      const response = await fetch(`https://62.109.1.5:8966/client/get-by-email/${encodeURIComponent(searchEmail)}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.ok) {
        const client = await response.json();
        navigate(`/client/${client.id}`);
      } else {
        toast.info("Клиент с таким email не найден.");
      }
    } catch (error) {
      console.error(error);
      toast.error("Ошибка при поиске клиента.");
    }
  };

  return (
    <header className="header">
      <div className="logo" onClick={() => navigate("/dashboard")}>
        ПК Клуб
      </div>

      <div className="search">
        <input
          type="text"
          placeholder="Поиск клиента по email"
          value={searchEmail}
          onChange={(e) => setSearchEmail(e.target.value)}
        />
        <button onClick={handleSearch}>Найти</button>
      </div>

      <div className="navigation">
        <button onClick={() => navigate("/pc-management")}>Управление ПК</button>
        {role === "ROLE_TECHNICAL" && (
          <>
            <button onClick={() => navigate("/register-employee")}>Зарегистрировать сотрудника</button>
            <button onClick={() => navigate("/manage-services")}>Управление сервисами</button>
          </>
        )}
        <button onClick={() => navigate("/register-client")}>Зарегистрировать клиента</button>
        <button onClick={handleLogout}>Выйти</button>
      </div>
    </header>
  );
};

export default Header;