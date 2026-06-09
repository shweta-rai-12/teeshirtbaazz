import { useEffect, useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import api from '../services/api';

function OrderConfirmation() {
  const { id } = useParams();
  const location = useLocation();
  const [order, setOrder] = useState(location.state?.order || null);
  const [payment, setPayment] = useState(location.state?.payment || null);
  const [message, setMessage] = useState('');

  useEffect(() => {
    const load = async () => {
      try {
        const orderResp = await api.get(`/orders/${id}`);
        setOrder(orderResp.data);
        try {
          const paymentResp = await api.get(`/payments/order/${id}`);
          setPayment(paymentResp.data);
        } catch {
          setPayment(null);
        }
      } catch (err) {
        setMessage(err.response?.data?.message || 'Unable to load order confirmation.');
      }
    };
    load();
  }, [id]);

  if (message) {
    return <section className="page"><div className="error">{message}</div></section>;
  }

  if (!order) {
    return <section className="page"><div className="empty-state">Loading confirmation...</div></section>;
  }

  return (
    <section className="page">
      <div className="confirmation-banner">
        <p className="eyebrow">Order confirmation</p>
        <h1>Order #{order.id}</h1>
        <p>Your order is now {order.status}. Keep the payment reference handy for support.</p>
      </div>

      <div className="grid two">
        <article className="card">
          <h2>Payment</h2>
          <p>Status: <strong>{payment?.status || 'Pending'}</strong></p>
          <p>Method: {payment?.method || 'Not recorded'}</p>
          <p>Reference: {payment?.transactionId || 'Not available'}</p>
          {payment?.failureReason && <p className="error-text">{payment.failureReason}</p>}
        </article>
        <article className="card">
          <h2>Shipping</h2>
          <p>{order.shippingAddress}</p>
          <p className="muted">Track this order from the Orders page.</p>
        </article>
      </div>

      <article className="card">
        <h2>Items</h2>
        {order.items?.map((item) => (
          <div className="summary-row" key={item.id}>
            <span>{item.productName} x {item.quantity}</span>
            <span>Rs {(item.unitPrice * item.quantity).toFixed(2)}</span>
          </div>
        ))}
        <div className="summary-row total">
          <strong>Total</strong>
          <strong>Rs {order.totalAmount?.toFixed(2)}</strong>
        </div>
      </article>

      <div className="actions">
        <Link className="button" to="/orders">Track Order</Link>
        <Link className="button secondary" to="/products">Continue Shopping</Link>
      </div>
    </section>
  );
}

export default OrderConfirmation;
