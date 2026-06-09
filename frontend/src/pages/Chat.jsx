import { useState } from 'react';
import api from '../services/api';

const suggestions = [
  'How do returns work?',
  'How do I track my order?',
  'Which payment methods are supported?',
  'How do custom t-shirt requests work?',
  'How do I choose a size?',
];

function Chat() {
  const [question, setQuestion] = useState('');
  const [answer, setAnswer] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');

  const ask = async (value) => {
    const finalQuestion = value || question;
    if (!finalQuestion.trim()) {
      return;
    }
    setLoading(true);
    setMessage('');
    setQuestion(finalQuestion);
    try {
      const response = await api.post('/chat', { question: finalQuestion });
      setAnswer(response.data);
    } catch (err) {
      setMessage(err.response?.data?.message || 'Unable to get an answer.');
    } finally {
      setLoading(false);
    }
  };

  const handleAsk = async (event) => {
    event.preventDefault();
    ask();
  };

  return (
    <section className="page">
      <h1>FAQ Chatbot</h1>
      <form onSubmit={handleAsk} className="panel">
        <label>Ask a question</label>
        <input value={question} onChange={(e) => setQuestion(e.target.value)} required />
        <button type="submit" disabled={loading}>{loading ? 'Asking...' : 'Ask'}</button>
      </form>
      <div className="suggestion-row">
        {suggestions.map((item) => (
          <button key={item} className="secondary" onClick={() => ask(item)}>{item}</button>
        ))}
      </div>
      {message && <div className="error">{message}</div>}
      {answer && <div className="answer">{answer}</div>}
    </section>
  );
}

export default Chat;
