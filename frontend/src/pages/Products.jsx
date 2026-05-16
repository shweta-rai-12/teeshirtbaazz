import { useState, useEffect } from 'react';
import api from '../services/api';

function Products() {
  const [products, setProducts] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get('/products')
      .then((resp) => setProducts(resp.data))
      .catch(() => setMessage('Unable to load products.'));
  }, []);

  const addToCart = async (productId) => {
    try {
      await api.post('/cart/add', { productId, quantity: 1 });
      setMessage('Added to cart');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to add to cart');
    }
  };

  return (
    <section className="page">
      <h2>Products</h2>
      {message && <div className="notice">{message}</div>}
      <div className="grid">
        {products.map((item) => (
          <article key={item.id} className="card">
            <img src={item.imageUrl || 'https://via.placeholder.com/240'} alt={item.name} />
            <div>
              <h3>{item.name}</h3>
              <p>{item.category} • {item.color} • {item.size}</p>
              <p>${item.price?.toFixed(2)}</p>
              <p>{item.description}</p>
              <button onClick={() => addToCart(item.id)}>Add to Cart</button>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

export default Products;
