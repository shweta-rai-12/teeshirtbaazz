import { useEffect, useState } from 'react';
import api from '../services/api';

function Returns() {
  const [returns, setReturns] = useState([]);
  const [message, setMessage] = useState('');

  useEffect(() => {
    api.get('/returns')
      .then((resp) => setReturns(resp.data))
      .catch(() => setMessage('Unable to load returns.'));
  }, []);

  return (
    <section className="page">
      <h1>Return Requests</h1>
      {message && <div className="notice">{message}</div>}
      {returns.length ? returns.map((item) => (
        <article key={item.id} className="card">
          <div className="page-header">
            <div>
              <h3>Return #{item.id}</h3>
              <p>Order #{item.order?.id}</p>
              <p>{item.reason}</p>
            </div>
            <span className="status">{item.status}</span>
          </div>
        </article>
      )) : <p>No return requests yet.</p>}
    </section>
  );
}

export default Returns;
