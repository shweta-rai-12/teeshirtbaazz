import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';

function Cart() {
  const [cart, setCart] = useState(null);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [busyItemId, setBusyItemId] = useState(null);
  const [clearing, setClearing] = useState(false);

  const loadCart = () => {
    setLoading(true);
    setMessage('');
    api.get('/cart')
      .then((resp) => setCart(resp.data))
      .catch((err) => setMessage(err.response?.data?.message || 'Unable to load cart.'))
      .finally(() => setLoading(false));
  };

  useEffect(loadCart, []);

  const updateQuantity = async (item, nextQuantity) => {
    const quantity = Math.max(1, Math.min(Number(nextQuantity) || 1, item.product.stock || 1));
    setBusyItemId(item.id);
    setMessage('');
    try {
      const resp = await api.put('/cart/update', { itemId: item.id, quantity });
      setCart(resp.data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to update item.');
      loadCart();
    } finally {
      setBusyItemId(null);
    }
  };

  const removeItem = async (id) => {
    setBusyItemId(id);
    setMessage('');
    try {
      const resp = await api.delete(`/cart/remove/${id}`);
      setCart(resp.data);
      setMessage('Item removed.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to remove item.');
    } finally {
      setBusyItemId(null);
    }
  };

  const clearCart = async () => {
    setClearing(true);
    setMessage('');
    try {
      const resp = await api.delete('/cart/clear');
      setCart(resp.data);
      setMessage('Cart cleared.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to clear cart.');
    } finally {
      setClearing(false);
    }
  };

  const items = cart?.items || [];
  const hasStockIssue = items.some((item) => item.product.stock <= 0 || item.quantity > item.product.stock);

  return (
    <section className="page">
      <div className="page-header">
        <div>
          <h1>Your Cart</h1>
          <p>Update quantities, remove items, or continue to checkout.</p>
        </div>
        {items.length > 0 && <button className="secondary" disabled={clearing} onClick={clearCart}>{clearing ? 'Clearing...' : 'Clear Cart'}</button>}
      </div>
      {message && <div className="notice">{message}</div>}
      {loading ? (
        <div className="empty-state">Loading cart...</div>
      ) : items.length ? (
        <>
          <div className="cart-list">
            {items.map((item) => {
              const itemBusy = busyItemId === item.id;
              const stockIssue = item.product.stock <= 0 || item.quantity > item.product.stock;
              return (
                <div key={item.id} className="cart-item">
                  <div>
                    <strong>{item.product.name}</strong>
                    <p className="muted">{item.product.color} | {item.product.size} | Rs {item.product.price?.toFixed(2)} each</p>
                    <p className={stockIssue ? 'error-text' : 'stock'}>
                      {item.product.stock > 0 ? `${item.product.stock} in stock` : 'Out of stock'}
                    </p>
                  </div>
                  <div className="quantity-control">
                    <button className="secondary icon-button" disabled={itemBusy || item.quantity <= 1} onClick={() => updateQuantity(item, item.quantity - 1)}>-</button>
                    <input className="qty-input" type="number" min="1" max={item.product.stock || 1} value={item.quantity} onChange={(e) => updateQuantity(item, e.target.value)} />
                    <button className="secondary icon-button" disabled={itemBusy || item.quantity >= item.product.stock} onClick={() => updateQuantity(item, item.quantity + 1)}>+</button>
                  </div>
                  <div>Rs {item.lineTotal?.toFixed(2)}</div>
                  <button className="secondary" disabled={itemBusy} onClick={() => removeItem(item.id)}>{itemBusy ? 'Working...' : 'Remove'}</button>
                </div>
              );
            })}
          </div>
          {hasStockIssue && <div className="error">Some cart items are unavailable or exceed stock. Update quantities before checkout.</div>}
          <div className="summary-row total">
            <strong>Total</strong>
            <strong>Rs {cart.totalAmount?.toFixed(2)}</strong>
          </div>
          <div className="actions">
            <Link className="button secondary" to="/products">Continue Shopping</Link>
            <Link className={`button ${hasStockIssue ? 'disabled-link' : ''}`} to={hasStockIssue ? '#' : '/checkout'}>Checkout</Link>
          </div>
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
