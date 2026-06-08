import { useEffect, useState } from 'react';
import api from '../services/api';

function Orders() {
  const [orders, setOrders] = useState([]);
  const [returnReasons, setReturnReasons] = useState({});
  const [message, setMessage] = useState('');

  const loadOrders = () => {
    api.get('/orders')
      .then((resp) => setOrders(resp.data))
      .catch(() => setMessage('Unable to load orders.'));
  };

  useEffect(loadOrders, []);

  const requestReturn = async (orderId) => {
    try {
      await api.post('/returns', { orderId, reason: returnReasons[orderId] || 'Customer requested return' });
      setMessage(`Return requested for order #${orderId}.`);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to request return.');
    }
  };

  return (
    <section className="page">
      <h1>Your Orders</h1>
      {message && <div className="notice">{message}</div>}
      {orders.length ? (
        orders.map((order) => (
          <article key={order.id} className="card">
            <div className="page-header">
              <div>
                <h3>Order #{order.id}</h3>
                <p className="muted">{order.shippingAddress}</p>
              </div>
              <span className="status">{order.status}</span>
            </div>
            <div className="line-list">
              {order.items?.map((item) => (
                <div key={item.id} className="summary-row">
                  <span>{item.productName} x {item.quantity}</span>
                  <span>Rs {(item.unitPrice * item.quantity).toFixed(2)}</span>
                </div>
              ))}
            </div>
            <div className="summary-row total">
              <strong>Total</strong>
              <strong>Rs {order.totalAmount?.toFixed(2)}</strong>
            </div>
            <div className="actions wrap">
              <input placeholder="Return reason" value={returnReasons[order.id] || ''} onChange={(e) => setReturnReasons({ ...returnReasons, [order.id]: e.target.value })} />
              <button className="secondary" onClick={() => requestReturn(order.id)}>Request Return</button>
            </div>
          </article>
        ))
      ) : (
        <p>No orders yet.</p>
      )}
    </section>
  );
}

export default Orders;
