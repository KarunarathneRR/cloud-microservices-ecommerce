import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { deactivateUser, getUserById, updateUser } from '../api';
import { useAuth } from '../AuthContext';

export default function ProfilePage() {
  const { userId, token, role, updateAuth, logout } = useAuth();
  const [user, setUser] = useState(null);
  const [form, setForm] = useState({ name: '', email: '', password: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);
  const [success, setSuccess] = useState('');
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    if (!userId) {
      setError('No user ID');
      setLoading(false);
      return;
    }

    setLoading(true);
    setError('');
    getUserById(userId, token)
      .then((data) => {
        setUser(data);
        setForm({ name: data.name, email: data.email, password: '' });
        setIsEditing(false);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [userId, token]);

  function handleChange(e) {
    setForm((current) => ({ ...current, [e.target.name]: e.target.value }));
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setSuccess('');
    setSaving(true);
    try {
      const updated = await updateUser(
        userId,
        {
          name: form.name,
          email: form.email,
          password: form.password || null,
        },
        token
      );
      setUser(updated);
      setForm((current) => ({ ...current, name: updated.name, email: updated.email, password: '' }));
      updateAuth({ email: updated.email });
      setSuccess('Profile updated successfully.');
      setIsEditing(false);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  }

  function startEditing() {
    if (!user) return;
    setError('');
    setSuccess('');
    setForm({ name: user.name, email: user.email, password: '' });
    setIsEditing(true);
  }

  function cancelEditing() {
    if (!user) return;
    setError('');
    setForm({ name: user.name, email: user.email, password: '' });
    setIsEditing(false);
  }

  async function handleDeactivate() {
    setError('');
    setSuccess('');
    try {
      const updated = await deactivateUser(userId, token);
      setUser(updated);
      logout();
    } catch (err) {
      setError(err.message);
    }
  }

  if (loading) return <Spinner />;

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">User Profile</h1>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 rounded-lg px-4 py-3 mb-4 text-sm">
          {error}
        </div>
      )}

      {user && (
        <div className="bg-white rounded-2xl shadow-lg p-8 space-y-6">
          <div className="flex items-center gap-4">
            <div className="w-16 h-16 rounded-full bg-indigo-100 flex items-center justify-center text-2xl font-bold text-indigo-600">
              {user.name.charAt(0).toUpperCase()}
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex items-center gap-2">
                <p className="text-xl font-semibold text-gray-800">{user.name}</p>
                {!isEditing && (
                  <button
                    type="button"
                    onClick={startEditing}
                    className="inline-flex items-center justify-center w-8 h-8 rounded-full text-indigo-700 hover:bg-indigo-50 transition-colors"
                    aria-label="Edit Profile"
                    title="Edit Profile"
                  >
                    <svg
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      strokeWidth="2"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      className="w-4 h-4"
                      aria-hidden="true"
                    >
                      <path d="M12 20h9" />
                      <path d="M16.5 3.5a2.12 2.12 0 1 1 3 3L7 19l-4 1 1-4 12.5-12.5Z" />
                    </svg>
                  </button>
                )}
              </div>
              <p className="text-sm text-gray-500">{user.email}</p>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4 pt-4 border-t border-gray-100">
            <InfoRow label="User ID" value={`#${user.id}`} />
            <InfoRow label="Role" value={user.role} chip />
          </div>

          {success && (
            <div className="bg-green-50 border border-green-200 text-green-700 rounded-lg px-4 py-3 text-sm">
              {success}
            </div>
          )}

          {!isEditing ? (
            <div className="pt-4 border-t border-gray-100 space-y-5">
              <div className="pt-2 flex flex-wrap gap-3">
                {role !== 'ADMIN' && (
                  <Link
                    to="/payment"
                    className="inline-flex items-center gap-2 border border-indigo-300 hover:bg-indigo-50 text-indigo-700 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                  >
                    Make a Payment
                  </Link>
                )}
                <Link
                  to="/payments"
                  className="inline-flex items-center gap-2 border border-indigo-300 hover:bg-indigo-50 text-indigo-700 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                >
                  {role === 'ADMIN' ? 'View Payment History' : 'Payment History'}
                </Link>
                <button
                  type="button"
                  onClick={handleDeactivate}
                  className="inline-flex items-center gap-2 border border-red-300 hover:bg-red-50 text-red-700 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                >
                  Deactivate Account
                </button>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit} className="pt-4 border-t border-gray-100 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                <input
                  name="name"
                  value={form.name}
                  onChange={handleChange}
                  required
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                <input
                  name="email"
                  type="email"
                  value={form.email}
                  onChange={handleChange}
                  required
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">New Password</label>
                <input
                  name="password"
                  type="password"
                  value={form.password}
                  onChange={handleChange}
                  minLength={6}
                  placeholder="Leave blank to keep the current password"
                  className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
                />
              </div>

              <div className="pt-2 flex flex-wrap gap-3">
                <button
                  type="submit"
                  disabled={saving}
                  className="inline-flex items-center gap-2 bg-indigo-600 hover:bg-indigo-700 disabled:opacity-60 text-white text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                >
                  {saving ? 'Saving...' : 'Save Profile'}
                </button>
                <button
                  type="button"
                  onClick={cancelEditing}
                  className="inline-flex items-center gap-2 border border-gray-300 hover:bg-gray-50 text-gray-700 text-sm font-medium px-4 py-2 rounded-lg transition-colors"
                >
                  Cancel
                </button>
              </div>
            </form>
          )}
        </div>
      )}
    </div>
  );
}

function InfoRow({ label, value, chip }) {
  return (
    <div>
      <p className="text-xs text-gray-400 uppercase tracking-wider mb-1">{label}</p>
      {chip ? (
        <span className="inline-block bg-indigo-100 text-indigo-700 text-xs font-bold px-2.5 py-0.5 rounded-full">
          {value}
        </span>
      ) : (
        <p className="text-sm font-medium text-gray-700">{value}</p>
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
