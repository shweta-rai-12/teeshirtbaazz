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

const labels = {
  fullName: 'Full name',
  phone: 'Phone',
  line1: 'Address line 1',
  line2: 'Address line 2',
  city: 'City',
  state: 'State',
  postalCode: 'Postal code',
  country: 'Country',
};

function Checkout() {
  const [cart, setCart] = useState(null);
  const [addresses, setAddresses] = useState([]);
  const [addressId, setAddressId] = useState('');
  const [addressForm, setAddressForm] = useState(blankAddress);
  const [paymentMethod, setPaymentMethod] = useState('COD');
  const [simulateFailure, setSimulateFailure] = useState(false);
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);
  const [placing, setPlacing] = useState(false);
  const [savingAddress, setSavingAddress] = useState(false);
  const navigate = useNavigate();

  const loadCheckout = async () => {
    setLoading(true);
    setMessage('');
    try {
      const [cartResp, addressResp] = await Promise.all([
        api.get('/cart'),
        api.get('/addresses'),
      ]);
      setCart(cartResp.data);
      setAddresses(addressResp.data);
      const defaultAddress = addressResp.data.find((item) => item.defaultAddress) || addressResp.data[0];
      setAddressId(defaultAddress ? String(defaultAddress.id) : '');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to load checkout.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCheckout();
  }, []);

  const saveAddress = async () => {
    setSavingAddress(true);
    setMessage('');
    try {
      const resp = await api.post('/addresses', addressForm);
      setAddresses((current) => [...current, resp.data]);
      setAddressId(String(resp.data.id));
      setAddressForm(blankAddress);
      setMessage('Address saved.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to save address.');
    } finally {
      setSavingAddress(false);
    }
  };

  const placeOrder = async () => {
    setPlacing(true);
    setMessage('');
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
      setTimeout(() => navigate(`/order-confirmation/${orderResp.data.id}`, {
        state: {
          order: orderResp.data,
          payment: paymentResp.data,
        },
      }), 700);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Checkout failed.');
      loadCheckout();
    } finally {
      setPlacing(false);
    }
  };

  const items = cart?.items || [];
  const selectedAddress = addresses.find((address) => String(address.id) === String(addressId));
  const hasStockIssue = items.some((item) => item.product.stock <= 0 || item.quantity > item.product.stock);
  const newAddressComplete = ['fullName', 'phone', 'line1', 'city', 'state', 'postalCode', 'country']
    .every((key) => addressForm[key]?.trim());
  const hasUsableAddress = Boolean(selectedAddress) || (!addressId && newAddressComplete);
  const canPlaceOrder = items.length > 0 && !hasStockIssue && !placing && hasUsableAddress;

  return (
    <section className="page checkout-layout">
      <div>
        <h1>Checkout</h1>
        {message && <div className="notice">{message}</div>}
        {loading ? (
          <div className="empty-state">Loading checkout...</div>
        ) : items.length === 0 ? (
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
            {selectedAddress && (
              <div className="card compact-address">
                <strong>{selectedAddress.fullName}</strong>
                <p>{selectedAddress.line1}, {selectedAddress.city}, {selectedAddress.state} {selectedAddress.postalCode}</p>
                <p className="muted">{selectedAddress.phone}</p>
              </div>
            )}
            {!addressId && (
              <div className="form-grid">
                {Object.keys(blankAddress).filter((key) => key !== 'defaultAddress').map((key) => (
                  <label key={key}>
                    {labels[key]}
                    <input value={addressForm[key]} onChange={(e) => setAddressForm({ ...addressForm, [key]: e.target.value })} required={key !== 'line2'} />
                  </label>
                ))}
                <button type="button" className="secondary" disabled={savingAddress} onClick={saveAddress}>{savingAddress ? 'Saving...' : 'Save Address'}</button>
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
            {hasStockIssue && <div className="error">Some cart items are unavailable or exceed stock. Return to cart and update quantities.</div>}
            <div className="actions">
              <Link className="button secondary" to="/cart">Back to Cart</Link>
              <button disabled={!canPlaceOrder} onClick={placeOrder}>{placing ? 'Placing Order...' : 'Place Order'}</button>
            </div>
          </>
        )}
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
