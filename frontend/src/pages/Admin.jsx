import { useEffect, useState } from 'react';
import api from '../services/api';

const productDefaults = {
  id: null,
  name: '',
  category: 'Men',
  ageGroup: 'Adult',
  color: '',
  size: 'M',
  description: '',
  imageUrl: '',
  price: '',
  stock: '',
  active: true,
};

const faqDefaults = {
  id: null,
  category: '',
  question: '',
  answer: '',
  active: true,
};

function Admin() {
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [returns, setReturns] = useState([]);
  const [customOrders, setCustomOrders] = useState([]);
  const [faqs, setFaqs] = useState([]);
  const [productForm, setProductForm] = useState(productDefaults);
  const [faqForm, setFaqForm] = useState(faqDefaults);
  const [message, setMessage] = useState('');

  const load = () => {
    Promise.all([
      api.get('/admin/products'),
      api.get('/admin/orders'),
      api.get('/admin/returns'),
      api.get('/admin/custom-orders'),
      api.get('/admin/faqs'),
    ]).then(([productResp, orderResp, returnResp, customResp, faqResp]) => {
      setProducts(productResp.data);
      setOrders(orderResp.data);
      setReturns(returnResp.data);
      setCustomOrders(customResp.data);
      setFaqs(faqResp.data);
    }).catch((err) => setMessage(err.response?.data?.message || 'Unable to load admin data.'));
  };

  useEffect(load, []);

  const saveProduct = async (event) => {
    event.preventDefault();
    const payload = {
      ...productForm,
      price: Number(productForm.price),
      stock: Number(productForm.stock),
    };
    if (productForm.id) {
      await api.put(`/products/${productForm.id}`, payload);
      setMessage('Product updated.');
    } else {
      await api.post('/products', payload);
      setMessage('Product created.');
    }
    setProductForm(productDefaults);
    load();
  };

  const deleteProduct = async (id) => {
    await api.delete(`/products/${id}`);
    setMessage('Product deleted.');
    load();
  };

  const updateOrderStatus = async (id, status) => {
    await api.put(`/orders/${id}/status?status=${encodeURIComponent(status)}`);
    load();
  };

  const updateReturnStatus = async (id, status) => {
    await api.put(`/returns/${id}?status=${encodeURIComponent(status)}`);
    load();
  };

  const updateCustomStatus = async (id, status) => {
    await api.put(`/custom-orders/${id}?status=${encodeURIComponent(status)}`);
    load();
  };

  const saveFaq = async (event) => {
    event.preventDefault();
    if (faqForm.id) {
      await api.put(`/admin/faqs/${faqForm.id}`, faqForm);
      setMessage('FAQ updated.');
    } else {
      await api.post('/admin/faqs', faqForm);
      setMessage('FAQ created.');
    }
    setFaqForm(faqDefaults);
    load();
  };

  const deleteFaq = async (id) => {
    await api.delete(`/admin/faqs/${id}`);
    load();
  };

  return (
    <section className="page wide">
      <div className="page-header">
        <div>
          <h1>Admin Workspace</h1>
          <p>Manage catalog, stock, orders, returns, custom requests, and FAQ content.</p>
        </div>
      </div>
      {message && <div className="notice">{message}</div>}

      <div className="admin-grid">
        <form className="panel" onSubmit={saveProduct}>
          <h2>{productForm.id ? 'Edit Product' : 'Add Product'}</h2>
          <input placeholder="Name" value={productForm.name} onChange={(e) => setProductForm({ ...productForm, name: e.target.value })} required />
          <select value={productForm.category} onChange={(e) => setProductForm({ ...productForm, category: e.target.value })}>
            <option>Men</option>
            <option>Women</option>
            <option>Kids</option>
          </select>
          <select value={productForm.ageGroup} onChange={(e) => setProductForm({ ...productForm, ageGroup: e.target.value })}>
            <option>Adult</option>
            <option>Kids</option>
          </select>
          <input placeholder="Color" value={productForm.color} onChange={(e) => setProductForm({ ...productForm, color: e.target.value })} required />
          <select value={productForm.size} onChange={(e) => setProductForm({ ...productForm, size: e.target.value })}>
            <option>S</option>
            <option>M</option>
            <option>L</option>
            <option>XL</option>
            <option>XXL</option>
          </select>
          <input type="number" placeholder="Price" value={productForm.price} onChange={(e) => setProductForm({ ...productForm, price: e.target.value })} required />
          <input type="number" placeholder="Stock" value={productForm.stock} onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })} required />
          <input placeholder="Image URL" value={productForm.imageUrl} onChange={(e) => setProductForm({ ...productForm, imageUrl: e.target.value })} />
          <textarea placeholder="Description" value={productForm.description} onChange={(e) => setProductForm({ ...productForm, description: e.target.value })} />
          <label className="checkbox-row">
            <input type="checkbox" checked={productForm.active} onChange={(e) => setProductForm({ ...productForm, active: e.target.checked })} />
            Active
          </label>
          <div className="actions">
            <button>{productForm.id ? 'Update' : 'Create'}</button>
            {productForm.id && <button type="button" className="secondary" onClick={() => setProductForm(productDefaults)}>Cancel</button>}
          </div>
        </form>

        <div>
          <h2>Products</h2>
          {products.map((product) => (
            <article key={product.id} className="card compact">
              <div>
                <strong>{product.name}</strong>
                <p className="muted">{product.category} | {product.color} | {product.size} | Stock {product.stock}</p>
              </div>
              <div className="actions">
                <button className="secondary" onClick={() => setProductForm({ ...product, price: String(product.price), stock: String(product.stock) })}>Edit</button>
                <button className="danger" onClick={() => deleteProduct(product.id)}>Delete</button>
              </div>
            </article>
          ))}
        </div>
      </div>

      <h2>Orders</h2>
      <div className="grid two">
        {orders.map((order) => (
          <article key={order.id} className="card">
            <h3>Order #{order.id}</h3>
            <p>Status: {order.status}</p>
            <p>Total: Rs {order.totalAmount?.toFixed(2)}</p>
            <select value={order.status} onChange={(e) => updateOrderStatus(order.id, e.target.value)}>
              <option>PENDING</option>
              <option>CONFIRMED</option>
              <option>SHIPPED</option>
              <option>DELIVERED</option>
              <option>CANCELLED</option>
            </select>
          </article>
        ))}
      </div>

      <h2>Returns</h2>
      <div className="grid two">
        {returns.map((item) => (
          <article key={item.id} className="card">
            <h3>Return #{item.id}</h3>
            <p>{item.reason}</p>
            <select value={item.status} onChange={(e) => updateReturnStatus(item.id, e.target.value)}>
              <option>REQUESTED</option>
              <option>APPROVED</option>
              <option>REJECTED</option>
              <option>COMPLETED</option>
            </select>
          </article>
        ))}
      </div>

      <h2>Custom Requests</h2>
      <div className="grid two">
        {customOrders.map((item) => (
          <article key={item.id} className="card">
            <h3>{item.desiredColor} / {item.desiredSize}</h3>
            <p>{item.requestedText || item.notes}</p>
            <select value={item.status} onChange={(e) => updateCustomStatus(item.id, e.target.value)}>
              <option>SUBMITTED</option>
              <option>REVIEWED</option>
              <option>APPROVED</option>
              <option>REJECTED</option>
            </select>
          </article>
        ))}
      </div>

      <div className="admin-grid">
        <form className="panel" onSubmit={saveFaq}>
          <h2>{faqForm.id ? 'Edit FAQ' : 'Add FAQ'}</h2>
          <input placeholder="Category" value={faqForm.category} onChange={(e) => setFaqForm({ ...faqForm, category: e.target.value })} />
          <input placeholder="Question" value={faqForm.question} onChange={(e) => setFaqForm({ ...faqForm, question: e.target.value })} required />
          <textarea placeholder="Answer" value={faqForm.answer} onChange={(e) => setFaqForm({ ...faqForm, answer: e.target.value })} required />
          <label className="checkbox-row">
            <input type="checkbox" checked={faqForm.active} onChange={(e) => setFaqForm({ ...faqForm, active: e.target.checked })} />
            Active
          </label>
          <button>{faqForm.id ? 'Update FAQ' : 'Create FAQ'}</button>
        </form>
        <div>
          <h2>FAQ Content</h2>
          {faqs.map((faq) => (
            <article key={faq.id} className="card compact">
              <div>
                <strong>{faq.question}</strong>
                <p>{faq.answer}</p>
              </div>
              <div className="actions">
                <button className="secondary" onClick={() => setFaqForm(faq)}>Edit</button>
                <button className="danger" onClick={() => deleteFaq(faq.id)}>Delete</button>
              </div>
            </article>
          ))}
        </div>
      </div>
    </section>
  );
}

export default Admin;
