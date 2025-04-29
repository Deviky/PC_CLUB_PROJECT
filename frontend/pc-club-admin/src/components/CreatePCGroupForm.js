import React, { useState } from "react";
import { toast } from "react-toastify";
import '../styles/CreatePCGroupForm.css';

const CreatePCGroupForm = ({ onSubmit }) => {
  const [cpu, setCpu] = useState("");
  const [gpu, setGpu] = useState("");
  const [ram, setRam] = useState(1); // Инициализируем с 1, чтобы по умолчанию было 1 ГБ

  const handleSubmit = async (e) => {
    e.preventDefault();

    const requestData = {
      cpu,
      gpu,
      ram,
    };

    try {
      await onSubmit(requestData);
    } catch (error) {
      toast.error("Ошибка при создании группы ПК.");
    }
  };

  return (
    <form onSubmit={handleSubmit} className="create-pc-group-form">
      <div>
        <label>CPU:</label>
        <input
          type="text"
          value={cpu}
          onChange={(e) => setCpu(e.target.value)}
          required
        />
      </div>
      <div>
        <label>GPU:</label>
        <input
          type="text"
          value={gpu}
          onChange={(e) => setGpu(e.target.value)}
          required
        />
      </div>
      <div>
        <label>RAM (GB):</label>
        <input
          type="number"
          value={ram}
          onChange={(e) => setRam(Math.max(1, e.target.value))} // Ограничиваем минимальным значением
          required
          min="1" // Минимальное значение 1
        />
      </div>
      <button type="submit">Создать группу</button>
    </form>
  );  
};

export default CreatePCGroupForm;
