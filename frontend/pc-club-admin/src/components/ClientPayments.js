import React, { useState } from "react";
import "../styles/ClientPayments.css"; // Подключаем стили

const ClientPayments = ({ operations }) => {
  const [visibleOperations, setVisibleOperations] = useState(10); // Показываем 10 операций изначально

  // Отображаем только первые 10 операций
  const operationsToShow = operations.slice(0, visibleOperations);

  const handleShowMore = () => {
    setVisibleOperations((prev) => prev + 10); // Показываем ещё 10 операций
  };

  if (!operations.length) {
    return <p>Нет операций.</p>;
  }

  return (
    <div className="payments-container">
      <h3 className="text-lg font-semibold">История операций</h3>
      <ul>
        {operationsToShow.map((operation) => (
          <li key={operation.id}>
            <p><strong>Сумма:</strong> {operation.amountOfCashPayment}{" "}
              {operation.useBonus ? "Б" : "Руб"}</p>
            <p><strong>Статус:</strong> {operation.paymentStatus}</p>
            <p><strong>Дата операции:</strong> {new Date(operation.operationDttm).toLocaleString('ru-RU', {
              day: '2-digit',
              month: '2-digit',
              year: 'numeric',
              hour: '2-digit',
              minute: '2-digit',
              hour12: false
            })}</p>

            {operation.serviceOrderId === null ? (
              <p className="text-green-600"><strong>Тип операции:</strong> Пополнение</p>
            ) : operation.paymentStatus === "RETURNED" ? (
              <p className="text-green-600"><strong>Тип операции:</strong> Возврат</p>
            ) : (
              <p className="text-blue-600"><strong>Тип операции:</strong> Покупка</p>
            )}
          </li>
        ))}
      </ul>

      {/* Кнопка для загрузки следующих операций */}
      {operations.length > visibleOperations && (
        <button
          onClick={handleShowMore}
          className="btn-show-more"
        >
          Показать ещё
        </button>
      )}
    </div>
  );
};

export default ClientPayments;
