import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { processPayment } from '../api';
import { useAuth } from '../AuthContext';

const METHODS = ['CREDIT_CARD', 'DEBIT_CARD', 'PAYPAL', 'BANK_TRANSFER'];

export default function PaymentPage() {
  const { userId, role, token } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    orderId: '',
    amount: '',
    paymentMethod: 'CREDIT_CARD',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [result, setResult] = useState(null);

  function handle(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function submit(e) {
    e.preventDefault();
    if (!userId) {
      setError('You must be logged in to make a payment.');
      return;
    }

    setError('');
    setResult(null);
    setLoading(true);
    try {
      const res = await processPayment({
        orderId: parseInt(form.orderId, 10),
        userId,
        amount: parseFloat(form.amount),
        paymentMethod: form.paymentMethod,
      }, token);
      setResult(res);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  if (role === 'ADMIN') {
    return (
      <div className="max-w-lg mx-auto px-4 py-10">
        <div className="bg-white rounded-2xl shadow-lg p-8">
          <h2 className="text-2xl font-bold text-gray-800 mb-3">Payments Disabled for Admin</h2>
          <p className="text-sm text-gray-500 mb-6">
            Admin accounts do not make payments. Use payment history to review transactions from all users.
          </p>
          <button
            onClick={() => navigate('/payments')}
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
          >
            View Payment History
          </button>
        </div>
      </div>
    );
  }

  if (result) {
    return (
      <div className="max-w-lg mx-auto px-4 py-10">
        <div className="bg-white rounded-2xl shadow-lg p-8 text-center">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4 text-lg font-bold text-green-700">
            OK
          </div>
          <h2 className="text-xl font-bold text-gray-800 mb-1">Payment Successful</h2>
          <p className="text-sm text-gray-500 mb-6">{result.message}</p>

          <div className="bg-gray-50 rounded-xl p-4 text-left space-y-3 text-sm mb-6">
            <Row label="Payment ID" value={`#${result.id}`} />
            <Row label="Order ID" value={`#${result.orderId}`} />
            <Row label="Amount" value={`$${result.amount.toFixed(2)}`} />
            <Row label="Method" value={result.paymentMethod} />
            <Row label="Status" value={result.status} />
            <Row label="Transaction ID" value={result.transactionId} small />
          </div>

          <div className="flex gap-3 justify-center">
            <button
              onClick={() => setResult(null)}
              className="border border-indigo-300 text-indigo-700 hover:bg-indigo-50 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
            >
              New Payment
            </button>
            <button
              onClick={() => navigate('/payments')}
              className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
            >
              View All Payments
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-lg mx-auto px-4 py-10">
      <div className="bg-white rounded-2xl shadow-lg p-8">
        <h2 className="text-2xl font-bold text-gray-800 mb-1">Process Payment</h2>
        <p className="text-sm text-gray-500 mb-6">
          Paying as user ID{' '}
          <span className="font-semibold text-indigo-600">#{userId}</span>
        </p>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
            {error}
          </div>
        )}

        <form onSubmit={submit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Order ID</label>
            <input
              name="orderId"
              type="number"
              min="1"
              value={form.orderId}
              onChange={handle}
              required
              placeholder="e.g. 101"
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Amount (USD)</label>
            <div className="relative">
              <span className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 font-medium">$</span>
              <input
                name="amount"
                type="number"
                min="0.01"
                step="0.01"
                value={form.amount}
                onChange={handle}
                required
                placeholder="0.00"
                className="w-full border border-gray-300 rounded-lg pl-7 pr-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Payment Method</label>
            <div className="grid grid-cols-2 gap-2">
              {METHODS.map((method) => (
                <label
                  key={method}
                  className={`flex items-center gap-2 border rounded-lg px-3 py-2 cursor-pointer text-sm transition-colors ${
                    form.paymentMethod === method
                      ? 'border-indigo-500 bg-indigo-50 text-indigo-700 font-medium'
                      : 'border-gray-200 hover:border-indigo-300 text-gray-600'
                  }`}
                >
                  <input
                    type="radio"
                    name="paymentMethod"
                    value={method}
                    checked={form.paymentMethod === method}
                    onChange={handle}
                    className="accent-indigo-600"
                  />
                  {method.replace('_', ' ')}
                </label>
              ))}
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white font-semibold py-2.5 rounded-lg transition-colors mt-2"
          >
            {loading ? 'Processing...' : 'Pay Now'}
          </button>
        </form>
      </div>
    </div>
  );
}

function Row({ label, value, small }) {
  return (
    <div className="flex justify-between items-start gap-2">
      <span className="text-gray-500 flex-shrink-0">{label}</span>
      <span className={`font-medium text-gray-800 text-right break-all ${small ? 'text-xs text-gray-500' : ''}`}>
        {value}
      </span>
    </div>
  );
}
