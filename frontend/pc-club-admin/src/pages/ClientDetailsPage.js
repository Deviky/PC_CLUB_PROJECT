import { useState, useEffect, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ClientInfo from "../components/ClientInfo";
import ClientOrders from "../components/ClientOrders";
import ClientPayments from "../components/ClientPayments";
import { useAuth } from "../auth/AuthContext";
import TopUpForm from "../components/TopUpForm";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import "../styles/ClientDetailsPage.css"; // Подключаем стили

const ClientDetailsPage = () => {
  const { id } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [client, setClient] = useState(null);
  const [orders, setOrders] = useState([]);
  const [payments, setPayments] = useState([]);
  const [activeTab, setActiveTab] = useState("orders");

  const fetchData = useCallback(async () => {
    const headers = {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    };

    try {
      const [clientRes, orderRes, paymentRes] = await Promise.all([
        fetch(`https://62.109.1.5:8966/client/get/${id}`, { headers }),
        fetch(`https://62.109.1.5:8966/order/get-by-client/${id}`, { headers }),
        fetch(`https://62.109.1.5:8966/payment/operations/${id}`, { headers }),
      ]);

      if (!clientRes.ok) {
        toast.error("Ошибка при загрузке информации о клиенте.");
        return;
      }

      const clientData = await clientRes.json();
      const ordersData = await orderRes.json();
      const paymentsData = await paymentRes.json();

      setClient(clientData);
      setOrders(Array.isArray(ordersData) ? ordersData : []);
      setPayments(Array.isArray(paymentsData) ? paymentsData : []);

      if (!ordersData || !Array.isArray(ordersData)) {
        toast.error("Не удалось загрузить историю заказов.");
      }
      if (!paymentsData || !Array.isArray(paymentsData)) {
        toast.error("Не удалось загрузить историю операций.");
      }
    } catch (error) {
      console.error("Ошибка при загрузке данных клиента:", error);
      toast.error("Произошла ошибка при загрузке данных.");
      navigate("/dashboard");
    }
  }, [id, token, navigate]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  if (!client) return <p>Загрузка...</p>;

  return (
    <div className="client-details-container">
      <ClientInfo client={client} />

      {/* Кнопки для переключения между компонентами */}
      <div className="tab-buttons">
        <button
          className={`${
            activeTab === "orders" ? "active-tab" : ""
          }`}
          onClick={() => setActiveTab("orders")}
        >
          История заказов
        </button>
        <button
          className={`${
            activeTab === "payments" ? "active-tab" : ""
          }`}
          onClick={() => setActiveTab("payments")}
        >
          История операций
        </button>
        <button
          className={`${
            activeTab === "topUp" ? "active-tab" : ""
          }`}
          onClick={() => setActiveTab("topUp")}
        >
          Пополнить баланс
        </button>
      </div>

      {/* Кнопка для перехода на страницу бронирования ПК */}
      <button
        onClick={() => navigate(`/client/${id}/book-pc`)}
        className="book-pc-button"
      >
        Забронировать ПК
      </button>

      {/* Рендеринг контента в зависимости от выбранной вкладки */}
      {activeTab === "topUp" ? (
        <TopUpForm
          clientId={client.id}
          email={client.email}
          token={token}
          onSuccess={() => {
            fetchData();
            setActiveTab("orders");
          }}
        />
      ) : (
        <>
          {activeTab === "orders" ? (
            <ClientOrders orders={orders} />
          ) : activeTab === "payments" ? (
            <ClientPayments operations={payments} />
          ) : null}
        </>
      )}

      <ToastContainer position="bottom-right" autoClose={3000} />
    </div>
  );
};

export default ClientDetailsPage;
