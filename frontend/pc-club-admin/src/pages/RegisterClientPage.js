import RegisterClientForm from "../components/RegisterClientForm";
import { useNavigate } from "react-router-dom";

const RegisterClientPage = () => {
  const navigate = useNavigate();

  const handleSuccess = () => {
    navigate("/dashboard");  // Переход на главную страницу после успешной регистрации
  };

  return (
    <div className="p-4">
      <RegisterClientForm onSuccess={handleSuccess} />
    </div>
  );
};

export default RegisterClientPage;
