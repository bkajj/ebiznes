import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "../styles/Payment.css";
import PropTypes from "prop-types";

export default function Payment({ clearCart }) {
  const location = useLocation();
  const navigate = useNavigate();
  const cart = location.state?.cart || [];

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: ""
  });

  useEffect(() => {
    if (!location.state?.cart || location.state.cart.length === 0) {
      navigate("/");
    }
  }, [location.state, navigate]);

  const totalPrice = cart.reduce((sum, item) => sum + item.product.price * item.quantity, 0).toFixed(2);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const isFormValid = () => {
    return formData.firstName && formData.lastName && formData.email && formData.phone;
  };

  const handlePayment = () => {
    if (!isFormValid()) {
      alert("Uzupełnij wszystkie pola formularza.");
      return;
    }

    const orderData = {
      customer: formData,
      cart: cart,
      total: totalPrice
    };

    fetch("http://localhost:8080/payment", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(orderData)
    })
      .then((res) => res.json())
      .then((data) => {
        alert("Zamówienie zostało złożone!");
        clearCart();
        navigate("/");
      })
      .catch((err) => console.error(err));
  };

  return (
    <div className="payment-container">
      <h2>Formularz płatności</h2>
      <form className="payment-form" onSubmit={(e) => e.preventDefault()}>
        <input
          type="text"
          name="firstName"
          placeholder="Imię"
          value={formData.firstName}
          onChange={handleChange}
          required
        />
        <input
          type="text"
          name="lastName"
          placeholder="Nazwisko"
          value={formData.lastName}
          onChange={handleChange}
          required
        />
        <input
          type="email"
          name="email"
          placeholder="Email"
          value={formData.email}
          onChange={handleChange}
          required
        />
        <input
          type="tel"
          name="phone"
          placeholder="Telefon"
          value={formData.phone}
          onChange={handleChange}
          required
        />
      </form>

      <div className="payment-summary">
        <p>Do zapłaty: <strong>{totalPrice} zł</strong></p>
        <button onClick={handlePayment} disabled={!isFormValid()}>
          Zapłać
        </button>
      </div>
    </div>
  );
}

Payment.propTypes = {
  clearCart: PropTypes.func.isRequired,
};