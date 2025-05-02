import React, { useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import { useAuth } from "../auth/AuthContext";
import "../styles/ClientOrders.css";
import 'react-toastify/dist/ReactToastify.css';

const ClientOrders = ({ orders }) => {
  const [visibleOrders, setVisibleOrders] = useState(10);
  const [currentOrders, setCurrentOrders] = useState(orders);
  const { token } = useAuth(); // Твой контекст авторизации

  const ordersToShow = currentOrders.slice(0, visibleOrders);

  const handleShowMore = () => {
    setVisibleOrders((prev) => prev + 10);
  };

  const handleCancelOrder = async (orderId) => {
    try {
      const response = await fetch(`https://62.109.1.5:8966/order/cancel/${orderId}`, {
        method: "DELETE",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      const data = await response.text();

      if (response.ok) {
        // Успешная отмена - показываем зелёное уведомление
        toast.success(`Заказ с ID ${orderId} успешно отменён: ${data}`);
        
        // Удаляем отменённый заказ из списка
        setCurrentOrders((prevOrders) => prevOrders.filter(order => order.id !== orderId));
      } else {
        toast.error(data); // Ошибка при отмене заказа
      }
    } catch (error) {
      toast.error("Ошибка отмены заказа.");
    }
  };

  if (!currentOrders.length) {
    return <p>Нет заказов.</p>;
  }

  return (
    <div className="orders-container">
      <h3 className="text-lg font-semibold">История заказов</h3>
      <ul>
        {ordersToShow.map((order) => (
          <li key={order.id} className="order-item">
            <div className="order-info">
              <p><strong>Услуга:</strong> {order.serviceId}</p>
              <p><strong>ПК:</strong> {order.pcId}</p>
              <p><strong>Дата начала:</strong> {new Date(order.startDttm).toLocaleString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })}</p>
              <p><strong>Дата окончания:</strong> {new Date(order.endDttm).toLocaleString('ru-RU', {
                day: '2-digit',
                month: '2-digit',
                year: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                hour12: false
              })}</p>
              <p><strong>Всего часов:</strong> {order.totalServiceCount}</p>
            </div>
            <button
              className="cancel-order-btn"
              onClick={() => handleCancelOrder(order.id)}
            >
              Отменить
            </button>
          </li>
        ))}
      </ul>

      {currentOrders.length > visibleOrders && (
        <button onClick={handleShowMore} className="btn-show-more">
          Показать ещё
        </button>
      )}
      <ToastContainer position="bottom-right" />
    </div>
  );
};

export default ClientOrders;