import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Products from './components/Products';
import Cart from './components/Cart';
import Payment from './components/Payment';
import './App.css';

function App() {
  const [cart, setCart] = useState([]);

  const addToCart = (product) => {
    const productIndex = cart.findIndex(item => item.product.id === product.id);

    if (productIndex >= 0) {
      const updatedCart = [...cart];
      updatedCart[productIndex].quantity += 1;
      setCart(updatedCart);
    } else {
      setCart([...cart, { product, quantity: 1 }]);
    }
  };

  const clearCart = () => {
    setCart([]);
  };

  return (
    <Router>
      <div className="app">
        <nav className="navbar">
          <Link to="/">Produkty</Link>
          <Link to="/cart">Koszyk ({cart.length})</Link>
        </nav>
        <Routes>
          <Route path="/" element={<Products addToCart={addToCart} />} />
          <Route path="/cart" element={<Cart cart={cart} clearCart={clearCart} />} />
          <Route path="/payment" element={<Payment cart={cart} clearCart={clearCart} />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
