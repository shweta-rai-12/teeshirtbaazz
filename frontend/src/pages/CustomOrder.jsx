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
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [uploading, setUploading] = useState(false);

  const loadRequests = () => {
    setLoading(true);
    api.get('/custom-orders')
      .then((resp) => setRequests(resp.data))
      .catch(() => setMessage('Unable to load custom requests.'))
      .finally(() => setLoading(false));
  };

  useEffect(loadRequests, []);

  const uploadLogo = async (event) => {
    const file = event.target.files?.[0];
    if (!file) {
      return;
    }
    const data = new FormData();
    data.append('file', file);
    setUploading(true);
    setMessage('');
    try {
      const resp = await api.post('/uploads/custom-logo', data, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      setForm((current) => ({ ...current, logoUrl: resp.data.url }));
      setMessage('Logo uploaded.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to upload logo.');
    } finally {
      setUploading(false);
    }
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage('');
    try {
      await api.post('/custom-orders', { ...form, estimatedPrice: Number(form.estimatedPrice || 0) });
      setMessage('Custom request submitted.');
      setForm(initialForm);
      loadRequests();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to submit request.');
    } finally {
      setSubmitting(false);
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
        <label>Upload logo</label>
        <input type="file" accept="image/png,image/jpeg,image/webp,image/gif" onChange={uploadLogo} />
        {uploading && <p className="muted">Uploading logo...</p>}
        <label>Logo URL</label>
        <input value={form.logoUrl} onChange={(e) => setForm({ ...form, logoUrl: e.target.value })} placeholder="Upload or paste image/logo URL" />
        {form.logoUrl && <img className="logo-preview" src={form.logoUrl} alt="Custom logo preview" />}
        <label>Text to print</label>
        <input value={form.requestedText} onChange={(e) => setForm({ ...form, requestedText: e.target.value })} />
        <label>Design notes</label>
        <textarea value={form.notes} onChange={(e) => setForm({ ...form, notes: e.target.value })} />
        <label>Estimated Budget</label>
        <input type="number" value={form.estimatedPrice} onChange={(e) => setForm({ ...form, estimatedPrice: e.target.value })} required />
        <button type="submit" disabled={submitting}>{submitting ? 'Submitting...' : 'Submit Request'}</button>
        {message && <div className="notice">{message}</div>}
      </form>

      <div>
        <h2>Your Custom Requests</h2>
        {loading ? <div className="empty-state">Loading custom requests...</div> : requests.length ? requests.map((item) => (
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
