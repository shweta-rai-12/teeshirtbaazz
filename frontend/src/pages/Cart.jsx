import { useEffect, useState } from 'react';
import api from '../services/api';

function Cart() {
  const [cart, setCart] = useState(null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get('/cart')
      .then((resp) => setCart(resp.data))
      .catch(() => setMessage('Unable to load cart.'));
  }, []);

  const handleRemove = async (id) => {
    try {
      const resp = await api.delete(`/cart/remove/${id}`);
      setCart(resp.data);
    } catch (err) {
      setMessage('Unable to remove item');
    }
  };

  return (
    <section className="page">
      <h2>Your Cart</h2>
      {message && <div className="notice">{message}</div>}
      {cart?.items?.length ? (
        <div className="cart-list">
          {cart.items.map((item) => (
            <div key={item.id} className="cart-item">
              <div>{item.product.name}</div>
              <div>Qty: {item.quantity}</div>
              <div>${(item.product.price * item.quantity).toFixed(2)}</div>
              <button onClick={() => handleRemove(item.id)}>Remove</button>
            </div>
          ))}
        </div>
      ) : (
        <p>Your cart is empty.</p>
      )}
    </section>
  );
}

export default Cart;
