import React, { useState, useEffect } from "react";
import { useAuth } from "../auth/AuthContext";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "../styles/ManageServicesPage.css";
import CreateServiceForm from "../components/CreateServiceForm";

const ManageServicesPage = () => {
  const [services, setServices] = useState([]);
  const [loading, setLoading] = useState(true);
  const [pcGroups, setPcGroups] = useState([]);
  const [showCreateServiceForm, setShowCreateServiceForm] = useState(false);
  const [editingService, setEditingService] = useState(null);
  const { token } = useAuth();

  // Загрузка сервисов
  useEffect(() => {
    const fetchServices = async () => {
      try {
        const response = await fetch("http://62.109.1.5:8966/pc-service/service/get-all", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        const data = await response.json();
        setServices(data);
      } catch (error) {
        console.error("Ошибка при получении сервисов:", error);
        toast.error("Не удалось загрузить сервисы.");
      } finally {
        setLoading(false);
      }
    };

    fetchServices();
  }, [token]);

  // Загрузка групп ПК
  useEffect(() => {
    const fetchPcGroups = async () => {
      try {
        const response = await fetch("http://62.109.1.5:8966/pc-service/pc-group/get-all", {
          method: "GET",
          headers: {
            "Authorization": `Bearer ${token}`,
          },
        });
        const data = await response.json();
        setPcGroups(data);
      } catch (error) {
        console.error("Ошибка при получении групп ПК:", error);
        toast.error("Не удалось загрузить группы ПК.");
      }
    };

    fetchPcGroups();
  }, [token]);

  // Открытие формы создания
  const openCreateServiceForm = () => {
    setEditingService(null);
    setShowCreateServiceForm(true);
  };

  // Открытие формы редактирования
  const handleEditService = (service) => {
    setEditingService(service);
    setShowCreateServiceForm(true);
  };

  // Отправка формы на создание или обновление
  const handleSubmitService = async (requestData) => {
    try {
      const url = editingService
        ? "https://62.109.1.5:8966/pc-service/service/update"
        : "https://62.109.1.5:8966/pc-service/service/create";
      const method = editingService ? "PUT" : "POST";

      const response = await fetch(url, {
        method,
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(
          editingService
            ? { id: editingService.id, ...requestData }
            : requestData
        ),
      });

      const data = await response.json();

      if (!response.ok) {
        toast.error(data.message || "Ошибка при сохранении сервиса.");
        return;
      }

      if (editingService) {
        // Обновляем сервис в списке
        setServices((prevServices) =>
          prevServices.map((s) => (s.id === data.id ? data : s))
        );
        toast.success("Сервис успешно обновлён!");
      } else {
        // Добавляем новый сервис
        setServices((prevServices) => [...prevServices, data]);
        toast.success("Сервис успешно создан!");
      }

      setShowCreateServiceForm(false);
      setEditingService(null);
    } catch (error) {
      console.error("Ошибка при сохранении сервиса:", error);
      toast.error("Не удалось сохранить сервис.");
    }
  };

  // Удаление сервиса
  const handleDeleteService = async (serviceId) => {
    if (!window.confirm("Вы уверены, что хотите удалить этот сервис?")) {
      return;
    }

    try {
      const response = await fetch(`https://62.109.1.5:8966/pc-service/service/delete/${serviceId}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      if (!response.ok) {
        const data = await response.json();
        toast.error(data.message || "Ошибка при удалении сервиса.");
        return;
      }

      setServices((prevServices) => prevServices.filter((service) => service.id !== serviceId));
      toast.success("Сервис успешно удалён!");
    } catch (error) {
      console.error("Ошибка при удалении сервиса:", error);
      toast.error("Не удалось удалить сервис.");
    }
};

  // Форматирование текста возрастных ограничений
  const getAgeRestrictionText = (minAge, maxAge) => {
    if (minAge === 0 && maxAge < 1000) {
      return `До ${maxAge} лет`;
    }
    if (minAge > 0 && maxAge >= 1000) {
      return `От ${minAge} лет`;
    }
    if (minAge > 0 && maxAge < 1000) {
      return `${minAge} - ${maxAge} лет`;
    }
    return "Без ограничений";
  };

  if (loading) {
    return <div>Загрузка...</div>;
  }

  return (
    <div className="manage-services-page">
      <h1>Управление сервисами</h1>

      <div className="manage-services-actions">
        <button
          className="manage-services-add-button"
          onClick={openCreateServiceForm}
        >
          Добавить сервис
        </button>
      </div>

      {services.length === 0 ? (
        <p>Нет доступных сервисов.</p>
      ) : (
        services.map((service) => (
          <div key={service.id} className="manage-services-card">
            <div className="manage-services-header">
              <h2>{service.name}</h2>
            </div>

            <div className="manage-services-details">
              <p><strong>Описание:</strong> {service.description}</p>
              <p><strong>Цена за час:</strong> {service.pricePerHour}₽</p>
              <p><strong>Возраст:</strong> {getAgeRestrictionText(service.minAge, service.maxAge)}</p>
              <p><strong>Время работы:</strong> {service.minTime} - {service.maxTime}</p>

              {service.pcGroup ? (
                <div className="manage-services-pc-group">
                  <h3>Группа ПК:</h3>
                  <p><strong>CPU:</strong> {service.pcGroup.cpu}</p>
                  <p><strong>GPU:</strong> {service.pcGroup.gpu}</p>
                  <p><strong>RAM:</strong> {service.pcGroup.ram} GB</p>
                </div>
              ) : (
                <p>Нет привязанной группы ПК.</p>
              )}
            </div>

            <div className="manage-services-buttons">
              <button
                className="manage-services-edit-button"
                onClick={() => handleEditService(service)}
              >
                Изменить
              </button>
              <button
                className="manage-services-delete-button"
                onClick={() => handleDeleteService(service.id)}
              >
                Удалить
              </button>
            </div>
          </div>
        ))
      )}

      {showCreateServiceForm && (
        <div className="manage-services-modal-overlay">
          <div className="manage-services-modal-content">
            <CreateServiceForm
              pcGroups={pcGroups}
              onSubmit={handleSubmitService}
              initialData={editingService}
            />
            <button
              className="manage-services-close-button"
              onClick={() => {
                setShowCreateServiceForm(false);
                setEditingService(null);
              }}
            >
              X
            </button>
          </div>
        </div>
      )}

      <ToastContainer />
    </div>
  );
};

export default ManageServicesPage;
