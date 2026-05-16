import { Link } from 'react-router-dom';

function Home() {
  return (
    <section className="page">
      <h1>Welcome to TeeShirtBazz</h1>
      <p>Shop custom t-shirts, manage your cart and orders, and ask our FAQ chatbot.</p>
      <Link className="button" to="/products">Browse Products</Link>
    </section>
  );
}

export default Home;
