import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom"; // Импортируем useParams для получения clientId из URL
import ServiceList from "../components/ServiceList";
import TimeSelector from "../components/TimeSelector";
import PCList from "../components/PCList";
import { useAuth } from "../auth/AuthContext"; // Используем контекст авторизации
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import '../styles/BookPCPage.css';
import { useCallback } from "react";

const BookPCPage = () => {
  const { id: clientId } = useParams(); // Извлекаем clientId из параметров URL

  const { token } = useAuth(); // Получаем токен из контекста

  const [services, setServices] = useState([]);
  const [selectedService, setSelectedService] = useState(null);
  const [dateFrom, setDateFrom] = useState("");
  const [duration, setDuration] = useState("");
  const [availablePCs, setAvailablePCs] = useState([]);
  const [useBonus, setUseBonus] = useState(false);

  // Загружаем сервисы при монтировании страницы (только один раз)
  useEffect(() => {
    const fetchData = async () => {
      if (!token) {
        toast.error("Пожалуйста, авторизуйтесь.");
        return;
      }

      try {
        const headers = {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        };

        const res = await fetch("http://localhost:8966/pc-service/service/get-all", {
          headers,
        });

        if (res.ok) {
          const data = await res.json();
          setServices(data);
        } else {
          toast.error("Не удалось загрузить сервисы.");
        }
      } catch (err) {
        toast.error("Ошибка при загрузке сервисов.");
      }
    };

    fetchData(); // Запрос выполняется только один раз при монтировании компонента
  }, [token]);

  // Фильтруем доступные ПК на основе выбранного сервиса, времени и длительности
  const filterAvailablePCs = useCallback(() => {
    if (!selectedService || !dateFrom || !duration) return;
  
    const start = new Date(dateFrom);
    const end = new Date(start.getTime() + parseInt(duration) * 60 * 60 * 1000);
  
    const filtered = selectedService.pcGroup.pcs.filter((pc) => {
      if (pc.status !== "WORKED") return false;
  
      return !pc.reservationList.some((res) => {
        const resStart = new Date(res.reservedFrom);
        const resEnd = new Date(res.reservedTo);
        return start < resEnd && end > resStart;
      });
    });
  
    setAvailablePCs(filtered);
  }, [selectedService, dateFrom, duration]);  // зависимости
  
  // Фильтруем доступные ПК при изменении выбранного сервиса, времени или длительности
  useEffect(() => {
    filterAvailablePCs();
  }, [filterAvailablePCs]);

  if (!services.length) return <p>Загрузка сервисов...</p>;

  return (
    <div className="book-pc__container">
  <h2 className="book-pc__title">Бронирование ПК</h2>

  <div className="book-pc__section">
    <ServiceList
      services={services}
      setSelectedService={setSelectedService}
      selectedService={selectedService}
    />
  </div>

  {selectedService && (
    <>
      <div className="book-pc__section">
        <TimeSelector
          minTime={selectedService.minTime}
          maxTime={selectedService.maxTime}
          dateFrom={dateFrom}
          setDateFrom={setDateFrom}
          duration={duration}
          setDuration={setDuration}
        />
      </div>

      <div className="book-pc__section book-pc__bonus-checkbox">
        <input
          type="checkbox"
          checked={useBonus}
          onChange={(e) => setUseBonus(e.target.checked)}
          className="form-checkbox h-4 w-4 text-blue-600"
        />
        <span>Использовать бонусы</span>
      </div>

      <div className="book-pc__section">
        <h3 className="book-pc__subtitle">Доступные ПК</h3>
        <PCList
          pcs={availablePCs}
          selectedService={selectedService}
          dateFrom={dateFrom}
          duration={duration}
          clientId={clientId}
          useBonus={useBonus}
        />
      </div>
    </>
  )}

  <ToastContainer position="bottom-right" autoClose={3000} />
</div>
  );
};

export default BookPCPage;
