import { useState } from "react";
import { toast } from "react-toastify";
import "../styles/TopUpForm.css"; // Подключаем стили

const TopUpForm = ({ clientId, email, token, onSuccess }) => {
  const [amount, setAmount] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    const numericAmount = parseFloat(amount);
    if (isNaN(numericAmount) || numericAmount <= 0) {
      toast.error("Введите корректную сумму.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8966/payment/top-up", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          clientId,
          amount: numericAmount,
          useBonus: false,
        }),
      });

      const data = await response.json();

      if (response.ok) {
        toast.success("Баланс успешно пополнен");
        setAmount("");
        onSuccess(); // чтобы обновить данные клиента
      } else {
        toast.error(data.message + "\nПожалуйста, повторите попытку.");
      }
    } catch (error) {
      toast.error("Сервер недоступен. Попробуйте позже.");
    }
  };

  const setQuickAmount = (value) => setAmount(String(value));

  return (
    <div className="topup-form-container">
      <h3 className="font-semibold text-lg mb-2">Пополнение баланса</h3>

      <form onSubmit={handleSubmit} className="form mb-4">
        <input
          type="number"
          placeholder="Введите сумму"
          className="border p-2 rounded w-40"
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
        />
        <button
          type="submit"
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
        >
          Пополнить
        </button>
      </form>

      <div className="quick-buttons">
        {[250, 500, 1000, 2000, 5000].map((sum) => (
          <button
            key={sum}
            type="button"
            onClick={() => setQuickAmount(sum)}
            className="bg-gray-200 px-3 py-1 rounded hover:bg-gray-300"
          >
            {sum}
          </button>
        ))}
      </div>
    </div>
  );
};

export default TopUpForm;
