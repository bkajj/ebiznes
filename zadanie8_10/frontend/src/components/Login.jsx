import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import '../styles/Login.css';

const BACKEND_API_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8000';
const FRONTEND_URL = process.env.REACT_APP_FRONTEND_URL || 'http://localhost:3000';

function Login({ onLoginSuccess }) {
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');

  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    if (params.get('registered') === '1') {
      setSuccessMessage('Rejestracja zakończona sukcesem.\nMożesz się teraz zalogować.');
    }

    const token = params.get('token');
    if (token) {
        sessionStorage.setItem('token', token);
        onLoginSuccess?.(token);
        navigate('/', { replace: true });
    }
  }, [location]);

  const handleInputChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    const BACKEND_API_URL = process.env.REACT_APP_BACKEND_URL || 'http://localhost:8000';
    const FRONTEND_URL = process.env.REACT_APP_FRONTEND_URL || 'http://localhost:3000';
  
    console.log('BACKEND:', BACKEND_API_URL);
    console.log('FRONTEND:', FRONTEND_URL);
    

    try {
      const response = await fetch(`${BACKEND_API_URL}/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      });

      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || 'Błąd logowania');
      }

      const data = await response.json();
      console.log(data);
      sessionStorage.setItem('token', data.token);
      onLoginSuccess(data.token);

      navigate('/', { replace: true }); 
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="auth-container">
      <h2>Zaloguj się</h2>
      <form className="auth-form" onSubmit={handleSubmit}>
        <input
          type="text"
          name="username"
          placeholder="Nazwa użytkownika"
          value={form.username}
          onChange={handleInputChange}
          required
        />
        <input
          type="password"
          name="password"
          placeholder="Hasło"
          value={form.password}
          onChange={handleInputChange}
          required
        />
        <button type="submit">Zaloguj się</button>
        {error && <p className="error">{error}</p>}
      </form>
      {successMessage && (
        <p className="success">
            {successMessage.split('\n').map((line, index) => (
            <React.Fragment key={index}>
                {line}
                <br />
            </React.Fragment>
            ))}
        </p>
      )}
      <div className="google-login">
        <p>lub</p>
        <button
          type="button"
          className="google-button"
          onClick={() => {
            window.location.href = `${BACKEND_API_URL}/login-google?redirectUrl=${FRONTEND_URL}`;
          }}
        >
          <img src="/google.svg" alt="Google logo" className="google-icon" />
          Zaloguj się przez Google
        </button>
      </div>
      <div className="google-login">
        <button
          type="button"
          className="google-button"
          onClick={() => {
            window.location.href = `${BACKEND_API_URL}/login-github?redirectUrl=${FRONTEND_URL}`;
          }}
        >
          <img src="/github.svg" alt="Github logo" className="google-icon" />
          Zaloguj się przez GitHub
        </button>
      </div>
    </div>
  );
}

export default Login;
