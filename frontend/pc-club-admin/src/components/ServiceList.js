import React from 'react';
import ServiceCard from './ServiceCard';
import '../styles/ServiceList.css';

const ServiceList = ({ services, setSelectedService, selectedService }) => {
  return (
    <div className="service-list">
      <h3 className="text-lg">Выберите сервис</h3>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {services.map((service) => (
          <ServiceCard
            key={service.id}
            service={service}
            onClick={() => setSelectedService(service)}
            isSelected={selectedService?.id === service.id} // Передаем информацию о выбранном сервисе
          />
        ))}
      </div>
    </div>
  );
};

export default ServiceList;
