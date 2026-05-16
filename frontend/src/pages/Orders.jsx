import { useEffect, useState } from 'react';
import api from '../services/api';

function Orders() {
  const [orders, setOrders] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get('/orders')
      .then((resp) => setOrders(resp.data))
      .catch(() => setMessage('Unable to load orders.'));
  }, []);

  return (
    <section className="page">
      <h2>Your Orders</h2>
      {message && <div className="notice">{message}</div>}
      {orders.length ? (
        orders.map((order) => (
          <article key={order.id} className="card">
            <p>Order #{order.id}</p>
            <p>Status: {order.status}</p>
            <p>Total: ${order.totalAmount?.toFixed(2)}</p>
          </article>
        ))
      ) : (
        <p>No orders yet.</p>
      )}
    </section>
  );
}

export default Orders;
