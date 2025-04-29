import { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [role, setRole] = useState(localStorage.getItem("role"));
  const [isAuthenticated, setIsAuthenticated] = useState(!!token);

  const login = (newToken, newRole) => {
    localStorage.setItem("token", newToken);
    localStorage.setItem("role", newRole);  // Сохраняем роль в localStorage
    setToken(newToken);
    setRole(newRole); // Устанавливаем роль
    setIsAuthenticated(true);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");  // Удаляем роль
    setToken(null);
    setRole(null); // Обнуляем роль
    setIsAuthenticated(false);
  };

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    const storedRole = localStorage.getItem("role");
  
    if (storedToken) {
      try {
        const { exp } = JSON.parse(atob(storedToken.split('.')[1]));
        if (Date.now() >= exp * 1000) {
          logout(); // токен истёк
          return;
        }
  
        setToken(storedToken);
        setRole(storedRole);
        setIsAuthenticated(true);
      } catch (e) {
        logout(); // ошибка декодирования токена — очищаем всё
      }
    } else {
      logout();
    }
  }, []);

  return (
    <AuthContext.Provider value={{ token, role, isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
