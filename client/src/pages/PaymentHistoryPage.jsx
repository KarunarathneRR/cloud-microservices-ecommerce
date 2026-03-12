import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { getAllPayments, getPaymentsByUser, getUsers } from '../api';
import { useAuth } from '../AuthContext';

const STATUS_STYLES = {
  COMPLETED: 'bg-green-100 text-green-700',
  PENDING: 'bg-yellow-100 text-yellow-700',
  FAILED: 'bg-red-100 text-red-700',
  REFUNDED: 'bg-blue-100 text-blue-700',
};

export default function PaymentHistoryPage() {
  const { userId: authUserId, role, token } = useAuth();
  const [payments, setPayments] = useState([]);
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    if (!authUserId) {
      setError('No user ID provided.');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError('');
    const paymentsRequest = role === 'ADMIN' ? getAllPayments(token) : getPaymentsByUser(authUserId, token);
    const usersRequest = role === 'ADMIN' ? getUsers(token) : Promise.resolve([]);

    Promise.all([paymentsRequest, usersRequest])
      .then(([paymentsData, usersData]) => {
        setPayments(paymentsData);
        setUsers(usersData);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [authUserId, role, token]);

  const usersById = useMemo(
    () => Object.fromEntries(users.map((user) => [user.id, user])),
    [users]
  );

  const filteredPayments = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return payments.filter((payment) => {
      const user = usersById[payment.userId];
      const matchesSearch =
        !normalizedSearch ||
        `${payment.id}`.includes(normalizedSearch) ||
        `${payment.orderId}`.includes(normalizedSearch) ||
        `${payment.userId}`.includes(normalizedSearch) ||
        payment.transactionId.toLowerCase().includes(normalizedSearch) ||
        payment.paymentMethod.toLowerCase().includes(normalizedSearch) ||
        user?.name?.toLowerCase().includes(normalizedSearch) ||
        user?.email?.toLowerCase().includes(normalizedSearch);

      const matchesStatus = statusFilter === 'ALL' || payment.status === statusFilter;
      return matchesSearch && matchesStatus;
    });
  }, [payments, search, statusFilter, usersById]);

  const completedAmount = filteredPayments
    .filter((payment) => payment.status === 'COMPLETED')
    .reduce((sum, payment) => sum + payment.amount, 0);

  if (loading) return <Spinner />;

  return (
    <div className="max-w-4xl mx-auto px-4 py-10">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Payment History</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {role === 'ADMIN' ? 'All user payments' : `User #${authUserId}`}
          </p>
        </div>
        {role !== 'ADMIN' && (
          <Link
            to="/payment"
            className="bg-indigo-600 hover:bg-indigo-700 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
          >
            + New Payment
          </Link>
        )}
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
          {error}
        </div>
      )}

      {!error && payments.length === 0 && (
        <div className="bg-white rounded-2xl shadow-sm p-12 text-center">
          <p className="text-4xl mb-3">💳</p>
          <p className="text-gray-500 text-sm">
            {role === 'ADMIN' ? 'No payments have been recorded yet.' : 'No payments found for this user.'}
          </p>
          {role !== 'ADMIN' && (
            <Link to="/payment" className="mt-4 inline-block text-indigo-600 hover:underline text-sm font-medium">
              Make your first payment →
            </Link>
          )}
        </div>
      )}

      {payments.length > 0 && (
        <>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 mb-6">
            <SummaryCard
              label="Total Paid"
              value={`$${completedAmount.toFixed(2)}`}
              color="text-green-600"
            />
            <SummaryCard label="Transactions" value={String(filteredPayments.length)} color="text-indigo-600" />
            <SummaryCard
              label="Completed"
              value={String(filteredPayments.filter((p) => p.status === 'COMPLETED').length)}
              color="text-green-600"
            />
            <SummaryCard
              label="Failed"
              value={String(filteredPayments.filter((p) => p.status === 'FAILED').length)}
              color="text-red-600"
            />
          </div>

          {role === 'ADMIN' && (
            <div className="bg-white rounded-2xl shadow-sm p-4 mb-6 grid gap-4 md:grid-cols-[1fr_220px]">
              <input
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search by payment, order, user, email, method, transaction ID"
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
              />
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
              >
                <option value="ALL">All statuses</option>
                {Object.keys(STATUS_STYLES).map((status) => (
                  <option key={status} value={status}>
                    {status}
                  </option>
                ))}
              </select>
            </div>
          )}

          <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead className="bg-gray-50 border-b border-gray-200">
                  <tr>
                    {[
                      'ID',
                      ...(role === 'ADMIN' ? ['Customer', 'User ID'] : []),
                      'Order',
                      'Amount',
                      'Method',
                      'Status',
                      'Transaction ID',
                      'Date',
                    ].map((h) => (
                      <th key={h} className="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100">
                  {filteredPayments.map((p) => (
                    <tr key={p.id} className="hover:bg-gray-50 transition-colors">
                      <td className="px-4 py-3 font-medium text-gray-700">#{p.id}</td>
                      {role === 'ADMIN' && (
                        <td className="px-4 py-3 text-gray-600">
                          <div className="font-medium text-gray-700">{usersById[p.userId]?.name ?? 'Unknown user'}</div>
                          <div className="text-xs text-gray-400">{usersById[p.userId]?.email ?? 'No email'}</div>
                        </td>
                      )}
                      {role === 'ADMIN' && <td className="px-4 py-3 text-gray-600">#{p.userId}</td>}
                      <td className="px-4 py-3 text-gray-600">#{p.orderId}</td>
                      <td className="px-4 py-3 font-semibold text-gray-800">${p.amount.toFixed(2)}</td>
                      <td className="px-4 py-3 text-gray-600">{p.paymentMethod.replace('_', ' ')}</td>
                      <td className="px-4 py-3">
                        <span
                          className={`inline-block text-xs font-bold px-2.5 py-0.5 rounded-full ${
                            STATUS_STYLES[p.status] ?? 'bg-gray-100 text-gray-600'
                          }`}
                        >
                          {p.status}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-gray-400 text-xs font-mono max-w-[160px] truncate">
                        {p.transactionId}
                      </td>
                      <td className="px-4 py-3 text-gray-500">
                        {new Date(p.createdAt).toLocaleDateString()}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>

          {filteredPayments.length === 0 && (
            <div className="bg-white rounded-2xl shadow-sm p-8 text-center mt-6 text-sm text-gray-500">
              No payments match the current filters.
            </div>
          )}
        </>
      )}
    </div>
  );
}

function SummaryCard({ label, value, color }) {
  return (
    <div className="bg-white rounded-xl shadow-sm p-4">
      <p className="text-xs text-gray-400 uppercase tracking-wider mb-1">{label}</p>
      <p className={`text-2xl font-bold ${color}`}>{value}</p>
    </div>
  );
}

function Spinner() {
  return (
    <div className="flex justify-center items-center py-20">
      <div className="w-8 h-8 border-4 border-indigo-300 border-t-indigo-600 rounded-full animate-spin" />
    </div>
  );
}
