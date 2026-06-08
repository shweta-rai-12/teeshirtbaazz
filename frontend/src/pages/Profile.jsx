import { useEffect, useState } from 'react';
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
  defaultAddress: false,
};

function Profile() {
  const [profile, setProfile] = useState(null);
  const [name, setName] = useState('');
  const [addresses, setAddresses] = useState([]);
  const [form, setForm] = useState(blankAddress);
  const [message, setMessage] = useState('');

  const load = () => {
    api.get('/users/me').then((resp) => {
      setProfile(resp.data);
      setName(resp.data.name || '');
    });
    api.get('/addresses').then((resp) => setAddresses(resp.data));
  };

  useEffect(load, []);

  const updateProfile = async (event) => {
    event.preventDefault();
    const resp = await api.put('/users/me', { name });
    setProfile(resp.data);
    const stored = JSON.parse(localStorage.getItem('tsb_user') || '{}');
    localStorage.setItem('tsb_user', JSON.stringify({ ...stored, name: resp.data.name }));
    setMessage('Profile updated.');
  };

  const saveAddress = async (event) => {
    event.preventDefault();
    try {
      await api.post('/addresses', form);
      setForm(blankAddress);
      setMessage('Address saved.');
      load();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to save address.');
    }
  };

  const setDefault = async (id) => {
    await api.put(`/addresses/${id}/default`);
    load();
  };

  const deleteAddress = async (id) => {
    await api.delete(`/addresses/${id}`);
    load();
  };

  return (
    <section className="page profile-layout">
      <div>
        <h1>Profile</h1>
        <form onSubmit={updateProfile} className="card">
          <label>Name</label>
          <input value={name} onChange={(e) => setName(e.target.value)} required />
          <p className="muted">{profile?.email} | {profile?.role}</p>
          <button>Update Profile</button>
        </form>

        <h2>Saved Addresses</h2>
        {addresses.length ? addresses.map((address) => (
          <article key={address.id} className="card">
            <div className="page-header">
              <div>
                <strong>{address.fullName}</strong>
                <p>{address.line1}, {address.city}, {address.state} {address.postalCode}</p>
                <p className="muted">{address.phone}</p>
              </div>
              {address.defaultAddress && <span className="status">Default</span>}
            </div>
            <div className="actions">
              <button className="secondary" onClick={() => setDefault(address.id)}>Make Default</button>
              <button className="danger" onClick={() => deleteAddress(address.id)}>Delete</button>
            </div>
          </article>
        )) : <p>No addresses saved yet.</p>}
      </div>

      <form onSubmit={saveAddress} className="panel">
        <h2>Add Address</h2>
        {Object.keys(blankAddress).filter((key) => key !== 'defaultAddress').map((key) => (
          <input key={key} placeholder={key} value={form[key]} onChange={(e) => setForm({ ...form, [key]: e.target.value })} />
        ))}
        <label className="checkbox-row">
          <input type="checkbox" checked={form.defaultAddress} onChange={(e) => setForm({ ...form, defaultAddress: e.target.checked })} />
          Set as default
        </label>
        <button>Save Address</button>
        {message && <div className="notice">{message}</div>}
      </form>
    </section>
  );
}

export default Profile;
