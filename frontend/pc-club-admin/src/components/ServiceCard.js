import React from 'react';
import '../styles/ServiceCard.css';

const ServiceCard = ({ service, onClick, isSelected }) => {
  return (
    <div
      onClick={onClick}
      className={`service-card p-4 border rounded-md cursor-pointer transition-all duration-300 ease-in-out transform ${isSelected ? "selected" : "default"}`}
    >
      <h4 className="text-xl font-semibold">{service.name}</h4>
      <p className="text-sm text-gray-600">{service.description}</p>

      <div className="mt-4 text-sm text-gray-700 service-info">
        <p><strong>Процессор:</strong> {service.pcGroup?.cpu}</p>
        <p><strong>Видеокарта:</strong> {service.pcGroup?.gpu}</p>
        <p><strong>Оперативная память:</strong> {service.pcGroup?.ram} GB</p>
      </div>

      {/* Дополнительная информация, например, минимальное и максимальное время */}
      <div className="mt-4 text-sm text-gray-500 time-info">
        <p><strong>Время работы:</strong> {service.minTime} - {service.maxTime}</p>
      </div>
    </div>
  );
};

export default ServiceCard;
