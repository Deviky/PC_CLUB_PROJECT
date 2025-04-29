import RegisterEmployeeForm from "../components/RegisterEmployeeForm";
import { useNavigate } from "react-router-dom";

const RegisterEmployeePage = () => {
  const navigate = useNavigate();

  const handleSuccess = () => {
    navigate("/dashboard"); // После успешной регистрации переходим на главную
  };

  return (
    <div className="p-4">
      <RegisterEmployeeForm onSuccess={handleSuccess} />
    </div>
  );
};

export default RegisterEmployeePage;
