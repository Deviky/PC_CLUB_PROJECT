import React, { useState } from "react";
import axios from "axios";
import { useAuth } from "../auth/AuthContext"; // Импортируем контекст авторизации
import { toast } from "react-toastify"; // Импортируем toast для уведомлений
import '../styles/ReserveButton.css';

const ReserveButton = ({ pcId, selectedService, dateFrom, duration, clientId, useBonus }) => {
  const [loading, setLoading] = useState(false);

  const { token } = useAuth(); // Получаем токен из контекста

  // Логируем clientId, чтобы убедиться, что он передается правильно
  console.log("clientId в ReserveButton:", clientId);

  const handleReservePC = async () => {
    if (!token) {
      toast.error("Пожалуйста, авторизуйтесь."); // Используем toast для ошибки
      return;
    }

    setLoading(true);

    // Формирование данных для отправки
    const hours = Number(duration);
    const startDttm = new Date(dateFrom); // Начало бронирования
    const endDttm = new Date(dateFrom);
    endDttm.setHours(endDttm.getHours() + hours); // Продолжительность бронирования
    console.log("start и end time:", startDttm, endDttm);

    const orderRequest = {
      clientId: clientId,  // Используем clientId из пропсов
      serviceId: selectedService.id,
      pcId: pcId,
      totalServiceCount: duration, // Пример: количество часов
      startDttm: startDttm,
      endDttm: endDttm,
      useBonus: useBonus,
    };

    try {
      // Отправка запроса на создание заказа с токеном из контекста
      const response = await axios.post(
        "http://localhost:8966/order/create",
        orderRequest,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Используем токен из контекста
            "Content-Type": "application/json",
          },
        }
      );

      if (response.data.message) {
        toast.success(response.data.message); // Успешное уведомление
      }
    } catch (error) {
      if (error.response && error.response.data && error.response.data.message) {
        toast.error(error.response.data.message); // Сообщение об ошибке
      } else {
        toast.error("Ошибка при создании заказа: " + error.message); // Общая ошибка
      }
    }

    setLoading(false);
  };

  return (
    <button
  onClick={handleReservePC}
  className="reserve-btn__button"
  disabled={loading}
>
  {loading ? "⏳ Загружаю..." : "💻 Забронировать ПК"}
</button>
  );
};

export default ReserveButton;
