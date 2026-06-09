import { useEffect, useState } from 'react';
import api from '../services/api';

const trackingSteps = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];
const returnableStatuses = ['CONFIRMED', 'SHIPPED', 'DELIVERED'];

function Orders() {
  const [orders, setOrders] = useState([]);
  const [payments, setPayments] = useState({});
  const [returnReasons, setReturnReasons] = useState({});
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [busyOrderId, setBusyOrderId] = useState(null);

  const loadOrders = async () => {
    setLoading(true);
    setMessage('');
    try {
      const resp = await api.get('/orders');
      setOrders(resp.data);
      const paymentPairs = await Promise.all(resp.data.map(async (order) => {
        try {
          const paymentResp = await api.get(`/payments/order/${order.id}`);
          return [order.id, paymentResp.data];
        } catch {
          return [order.id, null];
        }
      }));
      setPayments(Object.fromEntries(paymentPairs));
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to load orders.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  const requestReturn = async (orderId) => {
    setBusyOrderId(orderId);
    setMessage('');
    try {
      await api.post('/returns', { orderId, reason: returnReasons[orderId] || 'Customer requested return' });
      setMessage(`Return requested for order #${orderId}.`);
      setReturnReasons((current) => ({ ...current, [orderId]: '' }));
      loadOrders();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to request return.');
    } finally {
      setBusyOrderId(null);
    }
  };

  const stepClass = (orderStatus, step) => {
    const activeIndex = trackingSteps.indexOf(orderStatus);
    const stepIndex = trackingSteps.indexOf(step);
    if (activeIndex < 0) {
      return '';
    }
    return stepIndex <= activeIndex ? 'active' : '';
  };

  return (
    <section className="page">
      <h1>Your Orders</h1>
      {message && <div className="notice">{message}</div>}
      {loading ? (
        <div className="empty-state">Loading orders...</div>
      ) : orders.length ? (
        orders.map((order) => {
          const payment = payments[order.id];
          const canReturn = returnableStatuses.includes(order.status);
          return (
            <article key={order.id} className="card">
              <div className="page-header">
                <div>
                  <h3>Order #{order.id}</h3>
                  <p className="muted">{order.shippingAddress}</p>
                  <p className="muted">Payment: {payment?.status || 'Pending'} {payment?.transactionId ? `| ${payment.transactionId}` : ''}</p>
                </div>
                <span className="status">{order.status}</span>
              </div>

              <div className="tracker">
                {trackingSteps.map((step) => (
                  <div key={step} className={`tracker-step ${stepClass(order.status, step)}`}>
                    <span />
                    <small>{step}</small>
                  </div>
                ))}
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
                <input disabled={!canReturn} placeholder={canReturn ? 'Return reason' : 'Return unavailable for this status'} value={returnReasons[order.id] || ''} onChange={(e) => setReturnReasons({ ...returnReasons, [order.id]: e.target.value })} />
                <button className="secondary" disabled={!canReturn || busyOrderId === order.id} onClick={() => requestReturn(order.id)}>
                  {busyOrderId === order.id ? 'Requesting...' : 'Request Return'}
                </button>
              </div>
            </article>
          );
        })
      ) : (
        <p>No orders yet.</p>
      )}
    </section>
  );
}

export default Orders;
