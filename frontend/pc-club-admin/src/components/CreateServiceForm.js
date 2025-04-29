import React, { useState, useEffect } from "react";
import "../styles/CreateServiceForm.css";

const CreateServiceForm = ({ pcGroups, onSubmit, initialData }) => {
  const [formData, setFormData] = useState({
    name: '',
    description: '',
    pricePerHour: '',
    minAge: '',
    maxAge: '',
    minTime: '',
    maxTime: '',
    pcGroupId: '',
  });

  useEffect(() => {
    if (initialData) {
      setFormData({
        name: initialData.name || '',
        description: initialData.description || '',
        pricePerHour: initialData.pricePerHour?.toString() || '',
        minAge: initialData.minAge?.toString() || '',
        maxAge: initialData.maxAge?.toString() || '',
        minTime: initialData.minTime || '',
        maxTime: initialData.maxTime || '',
        pcGroupId: initialData.pcGroup?.id?.toString() || '',
      });
    }
  }, [initialData]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const requestData = {
      name: formData.name,
      description: formData.description,
      pricePerHour: parseFloat(formData.pricePerHour),
      minAge: formData.minAge ? parseInt(formData.minAge) : 0,
      maxAge: formData.maxAge ? parseInt(formData.maxAge) : 1000,
      minTime: formData.minTime || null,
      maxTime: formData.maxTime || null,
      pcGroupId: parseInt(formData.pcGroupId),
    };

    onSubmit(requestData);
  };

  if (!Array.isArray(pcGroups)) {
    return <div>Загрузка групп ПК...</div>;
  }

  return (
    <form onSubmit={handleSubmit} className="create-service-form">
      <div>
        <label>Название сервиса:</label>
        <input
          type="text"
          name="name"
          value={formData.name}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Описание:</label>
        <textarea
          name="description"
          value={formData.description}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Цена за час:</label>
        <input
          type="number"
          name="pricePerHour"
          value={formData.pricePerHour}
          step="0.01"
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Минимальный возраст (необязательно):</label>
        <input
          type="number"
          name="minAge"
          value={formData.minAge}
          onChange={handleChange}
        />
      </div>
      <div>
        <label>Максимальный возраст (необязательно):</label>
        <input
          type="number"
          name="maxAge"
          value={formData.maxAge}
          onChange={handleChange}
        />
      </div>
      <div>
        <label>Минимальное время начала:</label>
        <input
          type="time"
          name="minTime"
          value={formData.minTime}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Максимальное время конца:</label>
        <input
          type="time"
          name="maxTime"
          value={formData.maxTime}
          onChange={handleChange}
          required
        />
      </div>
      <div>
        <label>Группа ПК:</label>
        <select
          name="pcGroupId"
          value={formData.pcGroupId}
          onChange={handleChange}
          required
        >
          <option value="">Выберите группу</option>
          {pcGroups.map((group) => (
            <option key={group.id} value={group.id}>
              {group.cpu} | {group.gpu} | {group.ram}GB RAM
            </option>
          ))}
        </select>
      </div>
      <button type="submit">{initialData ? "Применить изменения" : "Создать сервис"}</button>
    </form>
  );
};

export default CreateServiceForm;
