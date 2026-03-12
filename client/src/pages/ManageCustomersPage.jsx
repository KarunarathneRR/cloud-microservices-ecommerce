import { useEffect, useMemo, useState } from 'react';
import { getUsers } from '../api';
import { useAuth } from '../AuthContext';

export default function ManageCustomersPage() {
  const { token } = useAuth();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('CUSTOMERS');

  useEffect(() => {
    setLoading(true);
    setError('');
    getUsers(token)
      .then(setUsers)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [token]);

  const filteredUsers = useMemo(() => {
    const normalizedSearch = search.trim().toLowerCase();

    return users.filter((user) => {
      const matchesRole =
        roleFilter === 'ALL' ? true : roleFilter === 'CUSTOMERS' ? user.role !== 'ADMIN' : user.role === roleFilter;
      const matchesSearch =
        !normalizedSearch ||
        `${user.id}`.includes(normalizedSearch) ||
        user.name.toLowerCase().includes(normalizedSearch) ||
        user.email.toLowerCase().includes(normalizedSearch) ||
        user.role.toLowerCase().includes(normalizedSearch);
      return matchesRole && matchesSearch;
    });
  }, [roleFilter, search, users]);

  if (loading) return <Spinner />;

  return (
    <div className="max-w-5xl mx-auto px-4 py-10">
      <div className="flex items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-800">Manage Customers</h1>
          <p className="text-sm text-gray-500 mt-0.5">Review registered users and filter customer records.</p>
        </div>
        <div className="bg-white rounded-xl shadow-sm px-4 py-3 text-right">
          <p className="text-xs text-gray-400 uppercase tracking-wider">Visible Users</p>
          <p className="text-2xl font-bold text-indigo-600">{filteredUsers.length}</p>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
          {error}
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-sm p-4 mb-6 grid gap-4 md:grid-cols-[1fr_200px]">
        <input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search by ID, name, email, or role"
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
        />
        <select
          value={roleFilter}
          onChange={(e) => setRoleFilter(e.target.value)}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 bg-white"
        >
          <option value="CUSTOMERS">Customers only</option>
          <option value="ALL">All users</option>
          <option value="ADMIN">Admins only</option>
        </select>
      </div>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-200">
              <tr>
                {['ID', 'Name', 'Email', 'Role'].map((heading) => (
                  <th key={heading} className="text-left text-xs font-semibold text-gray-500 uppercase tracking-wider px-4 py-3">
                    {heading}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filteredUsers.map((user) => (
                <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-4 py-3 font-medium text-gray-700">#{user.id}</td>
                  <td className="px-4 py-3 text-gray-700">{user.name}</td>
                  <td className="px-4 py-3 text-gray-600">{user.email}</td>
                  <td className="px-4 py-3">
                    <span
                      className={`inline-block text-xs font-bold px-2.5 py-0.5 rounded-full ${
                        user.role === 'ADMIN' ? 'bg-slate-100 text-slate-700' : 'bg-indigo-100 text-indigo-700'
                      }`}
                    >
                      {user.role}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {filteredUsers.length === 0 && !error && (
        <div className="bg-white rounded-2xl shadow-sm p-8 text-center mt-6 text-sm text-gray-500">
          No users match the current filters.
        </div>
      )}
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
