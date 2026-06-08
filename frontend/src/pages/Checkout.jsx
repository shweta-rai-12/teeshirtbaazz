import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import api from '../services/api';

const blankAddress = {
  fullName: '',
  phone: '',
  line1: '',
  line2: '',
  city: '',
  state: '',
  postalCode: '',
  country: 'India',
  defaultAddress: true,
};

function Checkout() {
  const [cart, setCart] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [addressId, setAddressId] = useState('');
  const [addressForm, setAddressForm] = useState(blankAddress);
  const [paymentMethod, setPaymentMethod] = useState('COD');
  const [simulateFailure, setSimulateFailure] = useState(false);
  const [message, setMessage] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    api.get('/cart').then((resp) => setCart(resp.data));
    api.get('/addresses').then((resp) => {
      setAddresses(resp.data);
      const defaultAddress = resp.data.find((item) => item.defaultAddress) || resp.data[0];
      if (defaultAddress) {
        setAddressId(String(defaultAddress.id));
      }
    });
  }, []);

  const saveAddress = async () => {
    const resp = await api.post('/addresses', addressForm);
    setAddresses((current) => [...current, resp.data]);
    setAddressId(String(resp.data.id));
    setAddressForm(blankAddress);
    setMessage('Address saved.');
  };

  const placeOrder = async () => {
    try {
      let finalAddressId = addressId;
      if (!finalAddressId) {
        const resp = await api.post('/addresses', addressForm);
        finalAddressId = String(resp.data.id);
      }
      const orderResp = await api.post('/orders', { addressId: Number(finalAddressId) });
      const paymentResp = await api.post('/payments', {
        orderId: orderResp.data.id,
        method: paymentMethod,
        simulateFailure,
      });
      setMessage(`Order #${orderResp.data.id} placed. Payment ${paymentResp.data.status}. Ref: ${paymentResp.data.transactionId}`);
      setTimeout(() => navigate('/orders'), 900);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Checkout failed.');
    }
  };

  const items = cart?.items || [];

  return (
    <section className="page checkout-layout">
      <div>
        <h1>Checkout</h1>
        {items.length === 0 ? (
          <div className="empty-state">
            <p>Your cart is empty.</p>
            <Link className="button" to="/products">Browse Products</Link>
          </div>
        ) : (
          <>
            <h2>Shipping Address</h2>
            {addresses.length > 0 && (
              <select value={addressId} onChange={(e) => setAddressId(e.target.value)}>
                <option value="">Use a new address</option>
                {addresses.map((address) => (
                  <option key={address.id} value={address.id}>{address.fullName}, {address.city}, {address.postalCode}</option>
                ))}
              </select>
            )}
            {!addressId && (
              <div className="form-grid">
                {Object.keys(blankAddress).filter((key) => key !== 'defaultAddress').map((key) => (
                  <input key={key} placeholder={key} value={addressForm[key]} onChange={(e) => setAddressForm({ ...addressForm, [key]: e.target.value })} />
                ))}
                <button type="button" className="secondary" onClick={saveAddress}>Save Address</button>
              </div>
            )}

            <h2>Payment</h2>
            <select value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
              <option value="COD">Cash on Delivery</option>
              <option value="UPI">UPI</option>
              <option value="CARD">Card</option>
            </select>
            <label className="checkbox-row">
              <input type="checkbox" checked={simulateFailure} onChange={(e) => setSimulateFailure(e.target.checked)} />
              Simulate payment failure
            </label>
            <button onClick={placeOrder}>Place Order</button>
          </>
        )}
        {message && <div className="notice">{message}</div>}
      </div>

      <aside className="panel">
        <h2>Order Summary</h2>
        {items.map((item) => (
          <div className="summary-row" key={item.id}>
            <span>{item.product.name} x {item.quantity}</span>
            <span>Rs {item.lineTotal?.toFixed(2)}</span>
          </div>
        ))}
        <div className="summary-row total">
          <span>Total</span>
          <span>Rs {cart?.totalAmount?.toFixed(2) || '0.00'}</span>
        </div>
      </aside>
    </section>
  );
}

export default Checkout;
