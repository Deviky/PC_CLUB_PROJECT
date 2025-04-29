import React from "react";
import "../styles/ClientInfo.css";

const ClientInfo = ({ client, onTopUpClick }) => {
  return (
    <div className="client-info-container">
      <h2>Информация о клиенте</h2>
      <p><strong>Имя:</strong> {client.name}</p>
      <p><strong>Фамилия:</strong> {client.surname}</p>
      <p><strong>Дата рождения:</strong> {new Date(client.dateOfBirth).toLocaleDateString()}</p>
      <p className="email"><strong>Email:</strong> {client.email}</p>
      <p className="balance"><strong>Баланс:</strong> {client.wallet ? client.wallet.balance + " руб." : "Не доступен"}</p>
      <p className="bonus-balance"><strong>Бонусы:</strong> {client.wallet ? client.wallet.bonusBalance + " б" : "Не доступен"}</p>
    </div>
  );
};

export default ClientInfo;