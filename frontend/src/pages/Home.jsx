import { Link } from 'react-router-dom';

function Home() {
  return (
    <section className="page home-layout">
      <div>
        <p className="eyebrow">Focused t-shirt commerce</p>
        <h1>TeeShirtBazz</h1>
        <p>Browse t-shirts, filter by fit and color, checkout with simulated UPI/card/COD payments, track orders, request returns, and send custom design requests.</p>
        <div className="actions">
          <Link className="button" to="/products">Browse Products</Link>
          <Link className="button secondary" to="/custom-order">Custom Tee</Link>
        </div>
      </div>
      <img src="https://images.unsplash.com/photo-1523381294911-8d3cead13475?auto=format&fit=crop&w=900&q=80" alt="Stack of folded t-shirts" />
    </section>
  );
}

export default Home;
