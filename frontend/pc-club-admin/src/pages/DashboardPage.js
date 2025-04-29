import React from "react";
import "../styles/DashboardPage.css"; // Подключаем стили

const DashboardPage = () => {
  return (
    <div className="dashboard">
      <main className="dashboard-content">
        <h1 className="dashboard-title">Добро пожаловать в админку ПК клуба</h1>
        <p className="dashboard-description">
          Здесь вы можете управлять компьютерами клуба, просматривать текущие бронирования, 
          добавлять новые записи и следить за системой.
        </p>
        <div className="dashboard-card">
          <h2 className="card-title">Управление ПК</h2>
          <p className="card-description">
            Добавляйте новые ПК в систему, назначайте доступные часы и следите за состоянием каждого компьютера.
          </p>
        </div>
        <div className="dashboard-card">
          <h2 className="card-title">Бронирования</h2>
          <p className="card-description">
            Просматривайте все бронирования, отредактируйте или удалите их при необходимости.
          </p>
        </div>
        <div className="dashboard-card">
          <h2 className="card-title">История операций</h2>
          <p className="card-description">
            Следите за операциями пользователей: пополнениями баланса, покупками и другими действиями.
          </p>
        </div>
      </main>
    </div>
  );
};

export default DashboardPage;
