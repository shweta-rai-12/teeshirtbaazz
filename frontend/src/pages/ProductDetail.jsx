import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import api, { isLoggedIn } from '../services/api';

function ProductDetail() {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get(`/products/${id}`)
      .then((resp) => setProduct(resp.data))
      .catch(() => setMessage('Unable to load product.'));
  }, [id]);

  const addToCart = async () => {
    if (!isLoggedIn()) {
      setMessage('Please login before adding items to cart.');
      return;
    }
    try {
      await api.post('/cart/add', { productId: Number(id), quantity });
      setMessage('Added to cart.');
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to add to cart.');
    }
  };

  if (!product) {
    return <section className="page">{message || 'Loading product...'}</section>;
  }

  return (
    <section className="page detail-layout">
      <img className="detail-image" src={product.imageUrl || 'https://via.placeholder.com/720x520?text=TeeShirtBazz'} alt={product.name} />
      <div>
        <Link to="/products">Back to products</Link>
        <h1>{product.name}</h1>
        <p className="muted">{product.category} | {product.ageGroup || 'Adult'} | {product.color} | {product.size}</p>
        <p className="price">Rs {product.price?.toFixed(2)}</p>
        <p>{product.description}</p>
        <p className={product.stock > 0 ? 'stock' : 'error-text'}>{product.stock > 0 ? `${product.stock} pieces available` : 'Out of stock'}</p>
        <label>Quantity</label>
        <input className="small-input" type="number" min="1" max={product.stock || 1} value={quantity} onChange={(e) => setQuantity(Number(e.target.value))} />
        <div className="actions">
          <button disabled={!product.stock} onClick={addToCart}>Add to Cart</button>
          <Link className="button secondary" to="/custom-order">Request Custom Tee</Link>
        </div>
        {message && <div className="notice">{message}</div>}
      </div>
    </section>
  );
}

export default ProductDetail;
