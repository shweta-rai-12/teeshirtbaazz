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

function Profile() {
  const [profile, setProfile] = useState(null);
  const [name, setName] = useState('');
  const [addresses, setAddresses] = useState([]);
  const [form, setForm] = useState(blankAddress);
  const [editingId, setEditingId] = useState(null);
  const [message, setMessage] = useState('');

  const load = async () => {
    try {
      const [profileResp, addressResp] = await Promise.all([
        api.get('/users/me'),
        api.get('/addresses'),
      ]);
      setProfile(profileResp.data);
      setName(profileResp.data.name || '');
      setAddresses(addressResp.data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to load profile.');
    }
  };

  useEffect(() => {
    load();
  }, []);

  const updateProfile = async (event) => {
    event.preventDefault();
    try {
      const resp = await api.put('/users/me', { name });
      setProfile(resp.data);
      const stored = JSON.parse(localStorage.getItem('tsb_user') || '{}');
      localStorage.setItem('tsb_user', JSON.stringify({ ...stored, name: resp.data.name }));
      setMessage('Profile updated.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to update profile.');
    }
  };

  const saveAddress = async (event) => {
    event.preventDefault();
    try {
      if (editingId) {
        await api.put(`/addresses/${editingId}`, form);
        setMessage('Address updated.');
      } else {
        await api.post('/addresses', form);
        setMessage('Address saved.');
      }
      resetAddressForm();
      load();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to save address.');
    }
  };

  const startEdit = (address) => {
    setEditingId(address.id);
    setForm({
      fullName: address.fullName || '',
      phone: address.phone || '',
      line1: address.line1 || '',
      line2: address.line2 || '',
      city: address.city || '',
      state: address.state || '',
      postalCode: address.postalCode || '',
      country: address.country || 'India',
      defaultAddress: Boolean(address.defaultAddress),
    });
  };

  const resetAddressForm = () => {
    setEditingId(null);
    setForm(blankAddress);
  };

  const setDefault = async (id) => {
    try {
      await api.put(`/addresses/${id}/default`);
      setMessage('Default address updated.');
      load();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to update default address.');
    }
  };

  const deleteAddress = async (id) => {
    try {
      await api.delete(`/addresses/${id}`);
      setMessage('Address deleted.');
      load();
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to delete address.');
    }
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
              <button className="secondary" onClick={() => startEdit(address)}>Edit</button>
              <button className="secondary" onClick={() => setDefault(address.id)}>Make Default</button>
              <button className="danger" onClick={() => deleteAddress(address.id)}>Delete</button>
            </div>
          </article>
        )) : <p>No addresses saved yet.</p>}
      </div>

      <form onSubmit={saveAddress} className="panel">
        <h2>{editingId ? 'Edit Address' : 'Add Address'}</h2>
        {Object.keys(blankAddress).filter((key) => key !== 'defaultAddress').map((key) => (
          <label key={key}>
            {labels[key]}
            <input value={form[key]} onChange={(e) => setForm({ ...form, [key]: e.target.value })} required={!['line2'].includes(key)} />
          </label>
        ))}
        <label className="checkbox-row">
          <input type="checkbox" checked={form.defaultAddress} onChange={(e) => setForm({ ...form, defaultAddress: e.target.checked })} />
          Set as default
        </label>
        <div className="actions">
          <button>{editingId ? 'Update Address' : 'Save Address'}</button>
          {editingId && <button type="button" className="secondary" onClick={resetAddressForm}>Cancel</button>}
        </div>
        {message && <div className="notice">{message}</div>}
      </form>
    </section>
  );
}

export default Profile;
