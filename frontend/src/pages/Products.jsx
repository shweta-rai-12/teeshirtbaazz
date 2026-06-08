import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import api, { isLoggedIn } from '../services/api';

const emptyFilters = {
  search: '',
  category: '',
  size: '',
  color: '',
  ageGroup: '',
  minPrice: '',
  maxPrice: '',
  sort: 'newest',
};

function Products() {
  const [products, setProducts] = useState([]);
  const [filters, setFilters] = useState(emptyFilters);
  const [message, setMessage] = useState('');

  const query = useMemo(() => {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, value]) => {
      if (value) {
        params.set(key, value);
      }
    });
    return params.toString();
  }, [filters]);

  useEffect(() => {
    api.get(`/products${query ? `?${query}` : ''}`)
      .then((resp) => setProducts(resp.data))
      .catch(() => setMessage('Unable to load products.'));
  }, [query]);

  const addToCart = async (productId) => {
    if (!isLoggedIn()) {
      setMessage('Please login before adding items to cart.');
      return;
    }
    try {
      await api.post('/cart/add', { productId, quantity: 1 });
      setMessage('Added to cart.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to add to cart.');
    }
  };

  const setFilter = (key, value) => {
    setFilters((current) => ({ ...current, [key]: value }));
  };

  return (
    <section className="page wide">
      <div className="page-header">
        <div>
          <h1>T-Shirt Catalog</h1>
          <p>Filter by category, size, color, age group, and price.</p>
        </div>
        <button type="button" className="secondary" onClick={() => setFilters(emptyFilters)}>Reset</button>
      </div>

      <div className="toolbar">
        <input placeholder="Search t-shirts" value={filters.search} onChange={(e) => setFilter('search', e.target.value)} />
        <select value={filters.category} onChange={(e) => setFilter('category', e.target.value)}>
          <option value="">All categories</option>
          <option>Men</option>
          <option>Women</option>
          <option>Kids</option>
        </select>
        <select value={filters.size} onChange={(e) => setFilter('size', e.target.value)}>
          <option value="">All sizes</option>
          <option>S</option>
          <option>M</option>
          <option>L</option>
          <option>XL</option>
          <option>XXL</option>
        </select>
        <input placeholder="Color" value={filters.color} onChange={(e) => setFilter('color', e.target.value)} />
        <select value={filters.ageGroup} onChange={(e) => setFilter('ageGroup', e.target.value)}>
          <option value="">All age groups</option>
          <option>Adult</option>
          <option>Kids</option>
        </select>
        <input type="number" placeholder="Min price" value={filters.minPrice} onChange={(e) => setFilter('minPrice', e.target.value)} />
        <input type="number" placeholder="Max price" value={filters.maxPrice} onChange={(e) => setFilter('maxPrice', e.target.value)} />
        <select value={filters.sort} onChange={(e) => setFilter('sort', e.target.value)}>
          <option value="newest">Newest</option>
          <option value="price-asc">Price low to high</option>
          <option value="price-desc">Price high to low</option>
          <option value="name">Name</option>
        </select>
      </div>

      {message && <div className="notice">{message}</div>}
      <div className="grid">
        {products.map((item) => (
          <article key={item.id} className="card product-card">
            <Link to={`/products/${item.id}`} className="image-link">
              <img src={item.imageUrl || 'https://via.placeholder.com/480x360?text=TeeShirtBazz'} alt={item.name} />
            </Link>
            <div className="stack">
              <div>
                <h3>{item.name}</h3>
                <p className="muted">{item.category} | {item.ageGroup || 'Adult'} | {item.color} | {item.size}</p>
              </div>
              <p className="price">Rs {item.price?.toFixed(2)}</p>
              <p>{item.description}</p>
              <p className={item.stock > 0 ? 'stock' : 'error-text'}>{item.stock > 0 ? `${item.stock} in stock` : 'Out of stock'}</p>
              <div className="actions">
                <Link className="button secondary" to={`/products/${item.id}`}>Details</Link>
                <button disabled={!item.stock} onClick={() => addToCart(item.id)}>Add to Cart</button>
              </div>
            </div>
          </article>
        ))}
      </div>
    </section>
  );
}

export default Products;
