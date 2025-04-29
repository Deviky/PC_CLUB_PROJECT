import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

const PrivateRoute = ({ children, allowedRoles = [] }) => {
  const { token, isAuthenticated, role, logout } = useAuth();

  if (!token || !isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  try {
    const { exp } = JSON.parse(atob(token.split('.')[1]));
    if (Date.now() >= exp * 1000) {
      logout();
      return <Navigate to="/login" replace />;
    }
  } catch (e) {
    logout();
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length && !allowedRoles.includes(role)) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default PrivateRoute;