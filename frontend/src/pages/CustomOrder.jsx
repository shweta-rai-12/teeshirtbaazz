import { useState } from 'react';
import api from '../services/api';

function CustomOrder() {
  const [form, setForm] = useState({ desiredSize: '', desiredColor: '', logoUrl: '', notes: '', estimatedPrice: '' });
  const [message, setMessage] = useState('');

  const handleSubmit = async (event) => {
    event.preventDefault();
    try {
      await api.post('/custom-orders', { ...form, estimatedPrice: Number(form.estimatedPrice) });
      setMessage('Custom request submitted');
      setForm({ desiredSize: '', desiredColor: '', logoUrl: '', notes: '', estimatedPrice: '' });
    } catch (err) {
      setMessage('Unable to submit request');
    }
  };

  return (
    <section className="page">
      <h2>Custom T-Shirt Request</h2>
      <form onSubmit={handleSubmit}>
        <label>Size</label>
        <input value={form.desiredSize} onChange={(e) => setForm({ ...form, desiredSize: e.target.value })} required />
        <label>Color</label>
        <input value={form.desiredColor} onChange={(e) => setForm({ ...form, desiredColor: e.target.value })} required />
        <label>Logo URL</label>
        <input value={form.logoUrl} onChange={(e) => setForm({ ...form, logoUrl: e.target.value })} />
        <label>Notes</label>
        <textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
        <label>Estimated Price</label>
        <input type="number" value={form.estimatedPrice} onChange={(e) => setForm({ ...form, estimatedPrice: e.target.value })} required />
        <button type="submit">Submit Request</button>
      </form>
      {message && <div className="notice">{message}</div>}
    </section>
  );
}

export default CustomOrder;
