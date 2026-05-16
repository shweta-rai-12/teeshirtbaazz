import { useState } from 'react';
import api from '../services/api';

function Chat() {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');

  const handleAsk = async (event) => {
    event.preventDefault();
    const response = await api.post('/chat', { question });
    setAnswer(response.data);
  };

  return (
    <section className="page">
      <h2>FAQ Chatbot</h2>
      <form onSubmit={handleAsk}>
        <label>Ask a question</label>
        <input value={question} onChange={(e) => setQuestion(e.target.value)} required />
        <button type="submit">Ask</button>
      </form>
      {answer && <div className="answer">{answer}</div>}
    </section>
  );
}

export default Chat;
