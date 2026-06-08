import { Link, useNavigate } from 'react-router-dom';
import { getSessionUser } from '../services/api';

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('tsb_token');
  const user = getSessionUser();
  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="brand">TeeShirtBazz</div>
      <div className="links">
        <Link to="/">Home</Link>
        <Link to="/products">Products</Link>
        <Link to="/cart">Cart</Link>
        <Link to="/orders">Orders</Link>
        <Link to="/returns">Returns</Link>
        <Link to="/custom-order">Custom Order</Link>
        <Link to="/chat">Chat</Link>
        {token && <Link to="/profile">Profile</Link>}
        {user?.role === 'ROLE_ADMIN' && <Link to="/admin">Admin</Link>}
        {token ? (
          <button onClick={handleLogout}>Logout</button>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
