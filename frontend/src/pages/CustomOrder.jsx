import { useEffect, useState } from 'react';
import api from '../services/api';

const initialForm = {
  desiredSize: '',
  desiredColor: '',
  logoUrl: '',
  requestedText: '',
  notes: '',
  estimatedPrice: '',
};

function CustomOrder() {
  const [form, setForm] = useState(initialForm);
  const [requests, setRequests] = useState([]);
  const [message, setMessage] = useState('');

  const loadRequests = () => {
    api.get('/custom-orders')
      .then((resp) => setRequests(resp.data))
      .catch(() => setMessage('Unable to load custom requests.'));
  };

  useEffect(loadRequests, []);

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await api.post('/custom-orders', { ...form, estimatedPrice: Number(form.estimatedPrice || 0) });
      setMessage('Custom request submitted.');
      setForm(initialForm);
      loadRequests();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to submit request.');
    }
  };

  return (
    <section className="page profile-layout">
      <form onSubmit={handleSubmit} className="panel">
        <h1>Custom T-Shirt Request</h1>
        <label>Size</label>
        <select value={form.desiredSize} onChange={(e) => setForm({ ...form, desiredSize: e.target.value })} required>
          <option value="">Choose size</option>
          <option>S</option>
          <option>M</option>
          <option>L</option>
          <option>XL</option>
          <option>XXL</option>
        </select>
        <label>Color</label>
        <input value={form.desiredColor} onChange={(e) => setForm({ ...form, desiredColor: e.target.value })} required />
        <label>Logo URL</label>
        <input value={form.logoUrl} onChange={(e) => setForm({ ...form, logoUrl: e.target.value })} placeholder="Paste image/logo URL for v1" />
        <label>Text to print</label>
        <input value={form.requestedText} onChange={(e) => setForm({ ...form, requestedText: e.target.value })} />
        <label>Design notes</label>
        <textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
        <label>Estimated Budget</label>
        <input type="number" value={form.estimatedPrice} onChange={(e) => setForm({ ...form, estimatedPrice: e.target.value })} required />
        <button type="submit">Submit Request</button>
        {message && <div className="notice">{message}</div>}
      </form>

      <div>
        <h2>Your Custom Requests</h2>
        {requests.length ? requests.map((item) => (
          <article key={item.id} className="card">
            <div className="page-header">
              <div>
                <h3>{item.desiredColor} / {item.desiredSize}</h3>
                <p>{item.requestedText || item.notes}</p>
                <p className="muted">Budget Rs {item.estimatedPrice?.toFixed(2)}</p>
              </div>
              <span className="status">{item.status}</span>
            </div>
            {item.logoUrl && <a href={item.logoUrl} target="_blank" rel="noreferrer">View logo</a>}
          </article>
        )) : <p>No custom requests yet.</p>}
      </div>
    </section>
  );
}

export default CustomOrder;
