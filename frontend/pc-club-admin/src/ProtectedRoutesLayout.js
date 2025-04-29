import { Routes, Route, Navigate } from "react-router-dom";
import Header from "./components/Header";
import DashboardPage from "./pages/DashboardPage";
import ClientDetailsPage from "./pages/ClientDetailsPage";
import BookPCPage from "./pages/BookPCPage";
import RegisterEmployeePage from "./pages/RegisterEmployeePage";
import ManageServicesPage from "./pages/ManageServicesPage";
import ManagePCPage from "./pages/ManagePCPage";
import RegisterClientPage from "./pages/RegisterClientPage";
import PrivateRoute from "./auth/PrivateRoute";

const ProtectedRoutesLayout = () => {
  return (
    <>
      <Header />
      <Routes>
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/client/:id" element={<ClientDetailsPage />} />
        <Route path="/client/:id/book-pc" element={<BookPCPage />} />
        <Route path="/pc-management" element={<ManagePCPage />} />
        <Route
          path="/register-employee"
          element={
            <PrivateRoute allowedRoles={["ROLE_TECHNICAL"]}>
              <RegisterEmployeePage />
            </PrivateRoute>
          }
        />
        <Route
          path="/manage-services"
          element={
            <PrivateRoute allowedRoles={["ROLE_TECHNICAL"]}>
              <ManageServicesPage />
            </PrivateRoute>
          }
        />
        <Route path="/register-client" element={<RegisterClientPage />} />
        {/* если путь не найден — редирект на /dashboard */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </>
  );
};

export default ProtectedRoutesLayout;
