import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

function Cart() {
  const [cart, setCart] = useState(null);
  const [message, setMessage] = useState('');

  const loadCart = () => {
    api.get('/cart')
      .then((resp) => setCart(resp.data))
      .catch(() => setMessage('Unable to load cart.'));
  };

  useEffect(loadCart, []);

  const updateQuantity = async (itemId, quantity) => {
    try {
      const resp = await api.put('/cart/update', { productId: itemId, quantity: Number(quantity) });
      setCart(resp.data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to update item.');
    }
  };

  const removeItem = async (id) => {
    try {
      const resp = await api.delete(`/cart/remove/${id}`);
      setCart(resp.data);
    } catch {
      setMessage('Unable to remove item.');
    }
  };

  const clearCart = async () => {
    const resp = await api.delete('/cart/clear');
    setCart(resp.data);
  };

  const items = cart?.items || [];

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Your Cart</h1>
          <p>Review quantities before checkout.</p>
        </div>
        {items.length > 0 && <button className="secondary" onClick={clearCart}>Clear Cart</button>}
      </div>
      {message && <div className="notice">{message}</div>}
      {items.length ? (
        <>
          <div className="cart-list">
            {items.map((item) => (
              <div key={item.id} className="cart-item">
                <div>
                  <strong>{item.product.name}</strong>
                  <p className="muted">{item.product.color} | {item.product.size}</p>
                </div>
                <input className="qty-input" type="number" min="1" max={item.product.stock} value={item.quantity} onChange={(e) => updateQuantity(item.id, e.target.value)} />
                <div>Rs {item.lineTotal?.toFixed(2)}</div>
                <button className="secondary" onClick={() => removeItem(item.id)}>Remove</button>
              </div>
            ))}
          </div>
          <div className="summary-row">
            <strong>Total</strong>
            <strong>Rs {cart.totalAmount?.toFixed(2)}</strong>
          </div>
          <Link className="button" to="/checkout">Checkout</Link>
        </>
      ) : (
        <div className="empty-state">
          <p>Your cart is empty.</p>
          <Link className="button" to="/products">Browse Products</Link>
        </div>
      )}
    </section>
  );
}

export default Cart;
